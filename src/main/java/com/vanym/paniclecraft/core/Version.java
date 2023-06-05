package com.vanym.paniclecraft.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.relauncher.Side;

public class Version {
    
    protected static final List<URL> URLS =
            Stream.of("https://paniclecraft.vanym.com/versions.json",
                      "https://raw.githubusercontent.com/vanym/paniclecraft/versions/versions.json",
                      "https://gitlab.com/vanym/paniclecraft/-/raw/versions/versions.json")
                  .map(URI::create)
                  .map(Version::buildURI)
                  .map(Version::toURL)
                  .collect(Collectors.toList());
    
    protected static final int MAX_HTTP_REDIRECTS = Integer.getInteger("http.maxRedirects", 20);
    
    protected static Status status = Status.PENDING;
    protected static String target = null;
    protected static String homepage = null;
    
    public static String getVersion() {
        return DEF.VERSION;
    }
    
    public static Status getStatus() {
        return status;
    }
    
    public static String getTarget() {
        return target;
    }
    
    public static String getHomepage() {
        return homepage;
    }
    
    protected static void handleResult(CheckResult result) {
        if (result == null) {
            status = Status.FAILED;
        } else {
            status = result.status;
            target = result.target;
            homepage = result.homepage;
        }
        putResultToForgeVersion(result);
    }
    
    @SuppressWarnings("deprecation")
    protected static void putResultToForgeVersion(CheckResult result) {
        try {
            // @formatter:off
            Map<ModContainer, ForgeVersion.CheckResult> results =
                    net.minecraftforge.fml.relauncher.
                    ReflectionHelper.getPrivateValue(ForgeVersion.class, null, "results");
            Constructor<ForgeVersion.CheckResult> constructor =
                    net.minecraftforge.fml.relauncher.
                    ReflectionHelper.findConstructor(ForgeVersion.CheckResult.class,
                                                     Status.class, ComparableVersion.class,
                                                     Map.class, String.class);
            // @formatter:on
            ComparableVersion version = null;
            if (result.target != null) {
                version = new ComparableVersion(result.target);
            }
            ForgeVersion.CheckResult forgeResult =
                    constructor.newInstance(result.status, version, null, result.homepage);
            if (forgeResult != null) {
                ModContainer mod = FMLCommonHandler.instance().findContainerFor(Core.instance);
                results.put(mod, forgeResult);
            }
        } catch (Exception e) {
        }
    }
    
    public static void startVersionCheck() {
        new Thread(Version::runVersionCheck).start();
    }
    
    protected static void runVersionCheck() {
        Iterator<URL> it = URLS.iterator();
        CheckResult result = null;
        while (it.hasNext() && result == null) {
            URL url = it.next();
            result = check(url);
        }
        handleResult(result);
    }
    
    protected static CheckResult check(URL url) {
        try {
            InputStream input = openUrlStream(url);
            String data = new String(ByteStreams.toByteArray(input));
            input.close();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> json = new Gson().fromJson(data, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, String> promos = (Map<String, String>)json.get("promos");
            String rec = promos.get(MinecraftForge.MC_VERSION + "-recommended");
            String lat = promos.get(MinecraftForge.MC_VERSION + "-latest");
            ComparableVersion current = new ComparableVersion(getVersion());
            String homepage = (String)json.get("homepage");
            Status status = Status.BETA;
            String target = null;
            if (rec != null) {
                int diff = current.compareTo(new ComparableVersion(rec));
                target = rec;
                if (diff == 0) {
                    status = Status.UP_TO_DATE;
                } else if (diff > 0) {
                    status = Status.AHEAD;
                } else {
                    status = Status.OUTDATED;
                }
            }
            if (lat != null && (rec == null || status == Status.AHEAD)) {
                target = lat;
                if (current.compareTo(new ComparableVersion(lat)) < 0) {
                    status = Status.BETA_OUTDATED;
                } else {
                    status = Status.BETA;
                }
            }
            return new CheckResult(status, target, homepage);
        } catch (Exception e) {
            return null;
        }
    }
    
    protected static InputStream openUrlStream(URL url) throws IOException {
        URL currentUrl = url;
        for (int redirects = 0; redirects < MAX_HTTP_REDIRECTS; redirects++) {
            URLConnection c = currentUrl.openConnection();
            if (c instanceof HttpURLConnection) {
                HttpURLConnection huc = (HttpURLConnection)c;
                huc.setInstanceFollowRedirects(false);
                int responseCode = huc.getResponseCode();
                if (responseCode >= 300 && responseCode <= 399) {
                    try {
                        String loc = huc.getHeaderField("Location");
                        currentUrl = new URL(currentUrl, loc);
                        continue;
                    } finally {
                        huc.disconnect();
                    }
                }
            }
            return c.getInputStream();
        }
        throw new IOException("Too many redirects while trying to fetch " + url);
    }
    
    protected static URI buildURI(URI uri) {
        List<Map.Entry<String, String>> list = new ArrayList<>();
        BiFunction<String, String, AbstractMap.SimpleImmutableEntry<String, String>> pair =
                AbstractMap.SimpleImmutableEntry<String, String>::new;
        BiConsumer<String, String> params = (key, val)->list.add(pair.apply(key, val));
        params.accept("paniclecraft", getVersion());
        params.accept("forge", ForgeVersion.getVersion());
        params.accept("minecraft", MinecraftForge.MC_VERSION);
        Side side = FMLCommonHandler.instance().getSide();
        params.accept("side", Objects.toString(side).toLowerCase());
        boolean deobfEnv = (boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
        if (deobfEnv) {
            params.accept("environment", "deobfuscated");
        }
        try {
            StringBuilder sb = new StringBuilder(Optional.ofNullable(uri.getQuery()).orElse(""));
            for (Map.Entry<String, String> e : list) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(e.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
            }
            return new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    sb.toString(),
                    uri.getFragment());
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static URL toURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static class CheckResult {
        public final Status status;
        public final String target;
        public final String homepage;
        
        public CheckResult(Status status, String target, String homepage) {
            this.status = status;
            this.target = target;
            this.homepage = homepage;
        }
    }
}
