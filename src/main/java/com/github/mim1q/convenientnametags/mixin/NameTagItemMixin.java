package com.github.mim1q.convenientnametags.mixin;

import com.github.mim1q.convenientnametags.ConvenientNameTags;
import com.github.mim1q.convenientnametags.interfaces.RemovableNameTag;
import com.github.mim1q.convenientnametags.screen.RenameNameTagScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
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
    if (world.isClient() && ConvenientNameTags.CONFIG.enableRenameScreen) {
      RenameNameTagScreen.open(user, itemStack);
    }
    return ConvenientNameTags.CONFIG.enableRenameScreen ? TypedActionResult.success(itemStack) : TypedActionResult.pass(itemStack);
  }

  @Inject(method = "useOnEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
  public void useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
    if (stack.getName().equals(entity.getCustomName())) {
      cir.setReturnValue(ActionResult.PASS);
      return;
    }
    if (
      ConvenientNameTags.CONFIG.dropNameTagsOnNameChange
      && !user.getWorld().isClient()
      && stack.hasCustomName()
      && entity.isAlive()
    ) {
      ((RemovableNameTag) entity).removeNameAndNameTag();
    }
  }
}
