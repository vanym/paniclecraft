package com.vanym.paniclecraft.recipe;

import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.item.ItemPaintingFrame.SideName;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;

public class RecipeUtils {
    
    public static void addPainting(ItemStack frame, ItemStack painting, Direction pside) {
        CompoundNBT pictureTag = ItemPainting.getPictureTag(painting)
                                             .map(CompoundNBT::copy)
                                             .orElseGet(CompoundNBT::new);
        if (painting.hasDisplayName()) {
            ItemPaintingFrame.putPictureTagName(pictureTag,
                                                painting.getDisplayName().getFormattedText());
        }
        ItemPaintingFrame.putPictureTag(frame, pside, pictureTag);
    }
    
    static Direction getSide(JsonObject json, String key) throws JsonSyntaxException {
        return getSide(json.get(key), key);
    }
    
    static Direction getSide(JsonArray arr, String key, int i) throws JsonSyntaxException {
        return getSide(arr.get(i), String.format("%s[%d]", key, i));
    }
    
    static Direction getSide(JsonElement ele, String key) throws JsonSyntaxException {
        if (ele == null) {
            throw new JsonSyntaxException(
                    "Missing " + key + ", expected to find a Int or a String");
        }
        try {
            return Direction.byIndex(JSONUtils.getInt(ele, key));
        } catch (JsonSyntaxException e) {
            return Optional.ofNullable(SideName.byName(JSONUtils.getString(ele, key)))
                           .orElseThrow(IllegalArgumentException::new)
                           .getSide();
        }
    }
}
