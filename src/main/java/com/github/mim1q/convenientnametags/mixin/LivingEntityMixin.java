package com.github.mim1q.convenientnametags.mixin;

import com.github.mim1q.convenientnametags.ConvenientNameTags;
import com.github.mim1q.convenientnametags.interfaces.RemovableNameTag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements RemovableNameTag {
  private LivingEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(method = "dropInventory()V", at = @At("HEAD"))
  protected void dropInventory(CallbackInfo info) {
    this.removeNameAndNameTag();
  }

  public void removeNameAndNameTag() {
    if (this.hasCustomName() && ConvenientNameTags.CONFIG.dropNameTagsOnDeath()) {
      ItemStack nameTagItemStack = new ItemStack(Items.NAME_TAG);
      nameTagItemStack.setCustomName(this.getCustomName());
      ItemEntity item = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), nameTagItemStack);
      this.world.spawnEntity(item);
      this.setCustomName(null);
    }
  }
}
