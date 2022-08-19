package com.github.mim1q.convenientnametags.screen;

import com.github.mim1q.convenientnametags.network.RenameNameTagPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class RenameNameTagScreen extends Screen {

  private TextFieldWidget textField;
  private ButtonWidget submitButton;

  public RenameNameTagScreen() {
    super(Text.of("Rename Name Tag"));
  }

  @Override
  protected void init() {
    this.clearChildren();

    if (this.client == null) {
      return;
    }

    int halfWidth = this.width / 2;
    int halfHeight = this.height / 2;

    this.textField = new TextFieldWidget(this.client.textRenderer, halfWidth - 100, halfHeight - 10, 200, 20, Text.empty());
    this.textField.setMaxLength(50);
    this.textField.setChangedListener(this::onTextChanged);
    this.addDrawableChild(this.textField);

    this.submitButton = new ButtonWidget(halfWidth - 101, halfHeight + 14, 20, 20, Text.of("v"), this::onSubmit);
    this.addDrawableChild(submitButton);
    submitButton.active = false;

    this.setInitialFocus(this.textField);
  }

  private void onTextChanged(String string) {
    this.submitButton.active = string.length() > 0;
  }

  private void onSubmit(ButtonWidget button) {
    ClientPlayNetworking.send(RenameNameTagPacket.ID, new RenameNameTagPacket(this.textField.getText()));
    this.close();
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    this.renderBackground(matrices);
    super.render(matrices, mouseX, mouseY, delta);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  public static void open(World world, PlayerEntity player, ItemStack stack) {
    MinecraftClient.getInstance().setScreen(new RenameNameTagScreen());
  }
}
