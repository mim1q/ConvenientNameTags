package com.github.mim1q.convenientnametags.mixin;

import com.github.mim1q.convenientnametags.screen.RenameNameTagScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.item.NameTagItem.class)
public abstract class NameTagItemMixin extends Item {
  public NameTagItemMixin(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack itemStack = user.getStackInHand(hand);
    if (hand == Hand.OFF_HAND) {
      return TypedActionResult.pass(itemStack);
    }

    if (world.isClient()) {
      RenameNameTagScreen.open(user, itemStack);
    }
    return TypedActionResult.success(itemStack);
  }
}
