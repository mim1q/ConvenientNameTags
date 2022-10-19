package com.github.mim1q.convenientnametags.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
  private VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
  protected void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
    if (player.getMainHandStack().isOf(Items.NAME_TAG)) {
      cir.setReturnValue(ActionResult.PASS);
    }
  }
}
