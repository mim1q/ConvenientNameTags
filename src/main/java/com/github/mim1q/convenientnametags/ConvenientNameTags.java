package com.github.mim1q.convenientnametags;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConvenientNameTags implements ModInitializer {
  public static final String MOD_ID = "convenientnametags";
  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onInitialize() {
    LOGGER.info("ConvenientNameTags is initializing.");
  }
}
