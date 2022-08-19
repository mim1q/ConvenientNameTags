package com.github.mim1q.convenientnametags;

import com.github.mim1q.convenientnametags.network.RenameNameTagPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConvenientNameTags implements ModInitializer {
  public static final String MOD_ID = "convenientnametags";
  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onInitialize() {
    LOGGER.info("ConvenientNameTags is initializing.");

    ServerPlayNetworking.registerGlobalReceiver(RenameNameTagPacket.ID, RenameNameTagPacket::apply);
  }

  public static Identifier createId(String path) {
    return new Identifier(MOD_ID, path);
  }
}
