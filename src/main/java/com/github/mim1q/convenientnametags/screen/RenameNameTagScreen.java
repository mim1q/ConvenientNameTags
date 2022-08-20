package com.github.mim1q.convenientnametags.screen;

import com.github.mim1q.convenientnametags.network.RenameNameTagPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RenameNameTagScreen extends Screen {

  private TextFieldWidget textField;
  private ButtonWidget submitButton;

  private final ClientPlayerEntity player;
  private final ItemStack itemStack;

  public RenameNameTagScreen(ClientPlayerEntity player, ItemStack stack) {
    super(Text.of("Rename Name Tag"));
    this.player = player;
    this.itemStack = stack;
  }

  @Override
  protected void init() {
    if (this.client == null) {
      return;
    }

    this.clearChildren();
    this.client.keyboard.setRepeatEvents(true);

    int halfWidth = this.width / 2;
    int halfHeight = this.height / 2;

    // New name input field
    this.textField = new TextFieldWidget(
      this.client.textRenderer,
      halfWidth - 100,
      halfHeight - 10,
      200,
      20,
      Text.empty()
    );
    this.textField.setMaxLength(50);
    this.textField.setChangedListener(this::onTextChanged);
    this.addDrawableChild(this.textField);

    // Submit button
    this.submitButton = new ButtonWidget(
      halfWidth - 101,
      halfHeight + 14,
      20,
      20,
      Text.of("✔"),
      this::onSubmit
    );
    this.addDrawableChild(submitButton);

    // Set default input text to current name
    this.textField.setText(this.itemStack.getName().getString());
    this.setInitialFocus(this.textField);
  }

  @Override
  public void removed() {
    super.removed();
    if (this.client != null) {
      this.client.keyboard.setRepeatEvents(false);
    }
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
    int halfWidth = this.width / 2;
    int halfHeight = this.height / 2;

    this.renderBackground(matrices);
    this.textRenderer.drawWithShadow(
      matrices,
      "Rename Name Tag",
      halfWidth - 100.0F,
      halfHeight - 22.0F,
      0xFFFFFF
    );
    super.render(matrices, mouseX, mouseY, delta);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  public static void open(PlayerEntity player, ItemStack stack) {
    MinecraftClient.getInstance().setScreen(new RenameNameTagScreen((ClientPlayerEntity) player, stack));
  }
}
