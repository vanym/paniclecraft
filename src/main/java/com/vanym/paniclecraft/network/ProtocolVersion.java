package com.vanym.paniclecraft.network;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.vanym.paniclecraft.DEF;

import cpw.mods.fml.common.versioning.ComparableVersion;

public class ProtocolVersion {
    
    protected static final String JSON_PATH = "/assets/" + DEF.MOD_ID + "/protocol_versions.json";
    protected static final NavigableMap<ComparableVersion, String> VERSIONS_MAP;
    
    static {
        NavigableMap<ComparableVersion, String> map = new TreeMap<>();
        try {
            InputStream input = ProtocolVersion.class.getResourceAsStream(JSON_PATH);
            InputStreamReader reader = new InputStreamReader(input);
            @SuppressWarnings("unchecked")
            Map<String, String> json = new Gson().fromJson(reader, Map.class);
            json.forEach((mv, pv)->map.put(new ComparableVersion(mv), pv));
        } catch (Exception e) {
        }
        VERSIONS_MAP = Collections.unmodifiableNavigableMap(map);
    }
    
    @Nullable
    public static String getSupposedVersion(String modVersion) {
        ComparableVersion version = new ComparableVersion(modVersion);
        Entry<ComparableVersion, String> entry = VERSIONS_MAP.floorEntry(version);
        return entry == null ? null : entry.getValue();
    }
}
