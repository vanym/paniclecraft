package com.vanym.paniclecraft.recipe;

import java.util.Optional;

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
        try {
            return Direction.byIndex(JSONUtils.getInt(json, key));
        } catch (JsonSyntaxException e) {
            return Optional.ofNullable(SideName.byName(JSONUtils.getString(json, key)))
                           .orElseThrow(IllegalArgumentException::new)
                           .getSide();
        }
    }
}
