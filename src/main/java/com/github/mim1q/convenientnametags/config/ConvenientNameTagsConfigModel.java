package com.github.mim1q.convenientnametags.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Sync;

import java.util.List;

import static io.wispforest.owo.config.Option.SyncMode.OVERRIDE_CLIENT;

@SuppressWarnings("unused")
@Config(name = "convenientnametags", wrapperName = "ConvenientNameTagsConfig")
public class ConvenientNameTagsConfigModel {
  @Sync(OVERRIDE_CLIENT)
  public int renameCost = 0;
  @Sync(OVERRIDE_CLIENT)
  public boolean renameCostPerWholeStack = true;
  public boolean dropNameTagsOnDeath = true;
  public boolean dropNameTagsOnNameChange = true;
  public boolean enableNameTagShearing = true;
  @Sync(OVERRIDE_CLIENT)
  public boolean enableRenameScreen = true;
  public boolean enableCraftingRecipe = true;
  public List<String> denylist = List.of();
}
