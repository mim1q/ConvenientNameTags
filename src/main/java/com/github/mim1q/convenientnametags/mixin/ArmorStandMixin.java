package com.github.mim1q.convenientnametags.mixin;

import com.github.mim1q.convenientnametags.ConvenientNameTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandMixin extends Entity {
  public ArmorStandMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(method = "interactAt(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
  protected void cancelInteractAt(PlayerEntity player, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
    ItemStack stack = player.getStackInHand(hand);
    boolean isNameTagApplicable =
      stack.isOf(Items.NAME_TAG)
      && stack.hasCustomName()
      && !stack.getName().equals(this.getCustomName());
    boolean areShearsApplicable =
      ConvenientNameTags.CONFIG.enableNameTagShearing()
      && stack.isOf(Items.SHEARS)
      && player.isSneaking();

    if (isNameTagApplicable || areShearsApplicable) {
      cir.setReturnValue(ActionResult.PASS);
    }
  }
}
