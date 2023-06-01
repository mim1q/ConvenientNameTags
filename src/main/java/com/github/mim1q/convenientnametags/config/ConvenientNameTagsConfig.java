package com.github.mim1q.convenientnametags.config;

import draylar.omegaconfig.api.Config;
import draylar.omegaconfig.api.Syncing;

public class ConvenientNameTagsConfig implements Config {
  @Syncing
  public int renameCost = 0;

  public boolean enableNameTagDrop = true;

  public boolean enableNameTagShearing = true;

  public boolean enableRenameScreen = true;

  public boolean enableCraftingRecipe = true;

  @Override
  public String getName() {
    return "convenientnametags";
  }

  @Override
  public String getExtension() {
    return "json5";
  }
}
