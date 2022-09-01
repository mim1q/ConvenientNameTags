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

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class RenameNameTagScreen extends Screen {

  private static final String TITLE_KEY = "gui.convenientnametags.title";
  private static final String EXPREIENCE_REQUIRED_KEY = "gui.convenientnametags.experience_required";
  private static final String APPLY_KEY = "gui.convenientnametags.apply";
  private static final String CLEAR_KEY = "gui.convenientnametags.clear";
  private static final String CANCEL_KEY = "gui.convenientnametags.cancel";
  private static final String APPLY_TOOLTIP_KEY = "gui.convenientnametags.apply.tooltip";
  private static final String CLEAR_TOOLTIP_KEY = "gui.convenientnametags.clear.tooltip";
  private static final String CANCEL_TOOLTIP_KEY = "gui.convenientnametags.cancel.tooltip";

  private TextFieldWidget textField;
  private ButtonWidget applyButton;

  private final ClientPlayerEntity player;
  private final ItemStack itemStack;

  public RenameNameTagScreen(ClientPlayerEntity player, ItemStack stack) {
    super(Text.translatable(TITLE_KEY));
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
    this.applyButton = new ButtonWidget(
      halfWidth - 101,
      halfHeight + 14,
      20,
      20,
      Text.translatable(APPLY_KEY),
      this::onSubmit,
      new ButtonTooltipSupplier(APPLY_TOOLTIP_KEY, this)
    );
    this.addDrawableChild(applyButton);

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
    this.applyButton.active = string.length() > 0;
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
      this.getTitle(),
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

  private static class ButtonTooltipSupplier implements ButtonWidget.TooltipSupplier {

    private final Text text;
    private final Screen screen;

    public ButtonTooltipSupplier(String key, Screen screen) {
      this.text = Text.translatable(key);
      this.screen = screen;
    }

    @Override
    public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
      screen.renderTooltip(matrices, this.text, mouseX, mouseY + 12);
    }

    @Override
    public void supply(Consumer<Text> consumer) {
      consumer.accept(this.text);
    }
  }
}
