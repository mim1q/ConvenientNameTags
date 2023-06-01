package com.github.mim1q.convenientnametags;

import com.github.mim1q.convenientnametags.config.ConvenientNameTagsConfig;
import com.github.mim1q.convenientnametags.network.RenameNameTagPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConvenientNameTags implements ModInitializer {
  public static final String MOD_ID = "convenientnametags";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final ConvenientNameTagsConfig CONFIG = ConvenientNameTagsConfig.createAndLoad();

  @Override
  public void onInitialize() {
    LOGGER.info("Convenient Name Tags is initializing...");

    ResourceConditions.register(createId("name_tag_recipe_enabled"), (json) -> CONFIG.enableCraftingRecipe());
    ServerPlayNetworking.registerGlobalReceiver(RenameNameTagPacket.ID, RenameNameTagPacket::apply);
  }

  public static Identifier createId(String path) {
    return new Identifier(MOD_ID, path);
  }
}
