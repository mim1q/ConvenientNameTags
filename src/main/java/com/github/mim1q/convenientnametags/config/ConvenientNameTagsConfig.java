package com.github.mim1q.convenientnametags.config;

import draylar.omegaconfig.api.Config;
import draylar.omegaconfig.api.Syncing;

public class ConvenientNameTagsConfig implements Config {
  @Syncing public int renameCost = 0;
  public boolean dropNameTagsOnDeath = true;
  public boolean dropNameTagsOnNameChange = true;
  public boolean enableNameTagShearing = true;
  @Syncing public boolean enableRenameScreen = true;
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
