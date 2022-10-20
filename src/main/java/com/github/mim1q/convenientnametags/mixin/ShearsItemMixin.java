package com.github.mim1q.convenientnametags.mixin;

import com.github.mim1q.convenientnametags.interfaces.RemovableNameTag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends Item {
  public ShearsItemMixin(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
    if (entity.hasCustomName() && user.isSneaking()) {
      ((RemovableNameTag) entity).removeNameAndNameTag();
      stack.damage(1, user, (playerEntity) -> playerEntity.sendToolBreakStatus(hand));
      return ActionResult.SUCCESS;
    }
    return super.useOnEntity(stack, user, entity, hand);
  }
}
