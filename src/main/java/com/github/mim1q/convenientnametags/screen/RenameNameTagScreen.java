package com.github.mim1q.convenientnametags.screen;

import com.github.mim1q.convenientnametags.ConvenientNameTags;
import com.github.mim1q.convenientnametags.network.RenameNameTagPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class RenameNameTagScreen extends Screen {

  private static final String TITLE_KEY = "gui.convenientnametags.title";
  private static final String EXPERIENCE_REQUIRED_KEY = "gui.convenientnametags.experience_required";
  private static final String NOT_ENOUGH_EXPERIENCE_KEY = "gui.convenientnametags.not_enough_experience";
  private static final String APPLY_KEY = "gui.convenientnametags.apply";
  private static final String CLEAR_KEY = "gui.convenientnametags.clear";
  private static final String CANCEL_KEY = "gui.convenientnametags.cancel";
  private static final String APPLY_TOOLTIP_KEY = "gui.convenientnametags.apply.tooltip";
  private static final String CLEAR_TOOLTIP_KEY = "gui.convenientnametags.clear.tooltip";
  private static final String CANCEL_TOOLTIP_KEY = "gui.convenientnametags.cancel.tooltip";

  private TextFieldWidget textField;
  private ButtonWidget applyButton;
  private ButtonWidget clearButton;
  private ButtonWidget cancelButton;

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

    var costMultiplier = ConvenientNameTags.CONFIG.renameCostPerWholeStack ? 1 : itemStack.getCount();
    var cost = ConvenientNameTags.CONFIG.renameCost * costMultiplier;
    var applyTooltip = cost <= player.experienceLevel
      ? Text.translatable(APPLY_TOOLTIP_KEY)
      : Text.translatable(NOT_ENOUGH_EXPERIENCE_KEY).formatted(Formatting.RED);
    this.applyButton = ButtonWidget.builder(Text.translatable(APPLY_KEY), this::onButtonClicked)
      .dimensions(halfWidth - 101, halfHeight + 14, 20, 20)
      .tooltip(Tooltip.of(applyTooltip))
      .build();
    this.applyButton.active = canApply();
    this.addDrawableChild(applyButton);

    this.clearButton = ButtonWidget.builder(Text.translatable(CLEAR_KEY), this::onButtonClicked)
      .dimensions(halfWidth - 79, halfHeight + 14, 20, 20)
      .tooltip(Tooltip.of(Text.translatable(CLEAR_TOOLTIP_KEY)))
      .build();
    this.clearButton.active = false;
    this.addDrawableChild(clearButton);

    this.cancelButton = ButtonWidget.builder(Text.translatable(CANCEL_KEY), this::onButtonClicked)
      .dimensions(halfWidth - 57, halfHeight + 14, 20, 20)
      .tooltip(Tooltip.of(Text.translatable(CANCEL_TOOLTIP_KEY)))
      .build();
    this.addDrawableChild(cancelButton);

    // Set default input text to current name
    if (this.itemStack.hasCustomName()) {
      this.textField.setText(this.itemStack.getName().getString());
      this.clearButton.active = true;
    }
    this.setInitialFocus(this.textField);
  }

  // Enter: Apply
  // Shift + Enter: Clear
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    // 257 - Enter Key
    if (keyCode == 257) {
      // 1 - Shift Modifier
      if (modifiers == 1 && this.clearButton.active) {
        this.onButtonClicked(this.clearButton);
        return true;
      }
      if (this.applyButton.active) {
        this.onButtonClicked(this.applyButton);
        return true;
      }
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  private void onTextChanged(String string) {
    this.applyButton.active = canApply();
  }

  private boolean canApply() {
    String currentName = this.itemStack.hasCustomName() ? this.itemStack.getName().getString() : "";
    String newName = this.textField.getText();
    var costMultiplier = ConvenientNameTags.CONFIG.renameCostPerWholeStack ? 1 : this.itemStack.getCount();
    var cost = ConvenientNameTags.CONFIG.renameCost * costMultiplier;
    return !(currentName.equals(newName) || newName.isBlank() || player.experienceLevel < cost);
  }

  private void onButtonClicked(ButtonWidget button) {
    if (button == this.applyButton) {
      this.sendRenamePacket(this.textField.getText());
    } else if (button == this.clearButton) {
      this.sendRenamePacket("");
    }
    this.close();
  }

  private void sendRenamePacket(String customName) {
    ClientPlayNetworking.send(RenameNameTagPacket.ID, new RenameNameTagPacket(customName));
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    int halfWidth = this.width / 2;
    int halfHeight = this.height / 2;

    this.renderBackground(drawContext);
    this.textRenderer.draw(
      this.getTitle(),
      halfWidth - 100.0F,
      halfHeight - 22.0F,
      0xFFFFFF,
      true,
      drawContext.getMatrices().peek().getPositionMatrix(),
      drawContext.getVertexConsumers(),
      TextRenderer.TextLayerType.NORMAL,
      0,
      0x0000F0
    );
    var costMultiplier = ConvenientNameTags.CONFIG.renameCostPerWholeStack ? 1 : this.itemStack.getCount();
    var cost = ConvenientNameTags.CONFIG.renameCost * costMultiplier;
    if (cost > 0) {
      var canAfford = player.experienceLevel >= cost;
      var text = Text.translatable(EXPERIENCE_REQUIRED_KEY, cost).formatted(canAfford ? Formatting.GREEN : Formatting.RED);
      var textWidth = textRenderer.getWidth(text);
      drawContext.fill(
        halfWidth + 100 - textWidth - 3,
        halfHeight - 24,
        halfWidth + 101,
        halfHeight - 13,
        0x80000000
      );
      textRenderer.draw(
        text,
        halfWidth + 99 - textWidth,
        halfHeight - 22.0F,
        0xFFFFFF,
        true,
        drawContext.getMatrices().peek().getPositionMatrix(),
        drawContext.getVertexConsumers(),
        TextRenderer.TextLayerType.NORMAL,
        0,
        0x0000F0
      );
    }
    super.render(drawContext, mouseX, mouseY, delta);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  public static void open(PlayerEntity player, ItemStack stack) {
    MinecraftClient.getInstance().setScreen(new RenameNameTagScreen((ClientPlayerEntity) player, stack));
  }
}
