package com.github.mim1q.convenientnametags.config;

import draylar.omegaconfig.api.Config;
import draylar.omegaconfig.api.Syncing;

import java.util.List;

public class ConvenientNameTagsConfig implements Config {
  @Syncing public int renameCost = 0;
  @Syncing public boolean renameCostPerWholeStack = true;
  public boolean dropNameTagsOnDeath = true;
  public boolean dropNameTagsOnNameChange = true;
  public boolean enableNameTagShearing = true;
  @Syncing public boolean enableRenameScreen = true;
  public boolean enableCraftingRecipe = true;
  public List<String> denylist = List.of();

  @Override
  public String getName() {
    return "convenientnametags";
  }
}
