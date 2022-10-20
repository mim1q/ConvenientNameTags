package com.github.mim1q.convenientnametags.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
  VillagerEntity.class,
  WanderingTraderEntity.class
})
public abstract class CancelInteractMobMixin extends MobEntity {
  private CancelInteractMobMixin(EntityType<? extends MobEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
  protected void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
    ItemStack stack = player.getStackInHand(hand);
    if (stack.isOf(Items.NAME_TAG)
      || (player.isSneaking() && this.hasCustomName() && stack.isOf(Items.SHEARS))) {
      cir.setReturnValue(ActionResult.PASS);
    }
  }
}
