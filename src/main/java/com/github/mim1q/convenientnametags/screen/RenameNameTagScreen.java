package com.github.mim1q.convenientnametags.screen;

import com.github.mim1q.convenientnametags.ConvenientNameTags;
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
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

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

    this.applyButton = new ButtonWidget(
      halfWidth - 101,
      halfHeight + 14,
      20,
      20,
      Text.translatable(APPLY_KEY),
      this::onButtonClicked,
      new ApplyButtonTooltipSupplier(APPLY_TOOLTIP_KEY, NOT_ENOUGH_EXPERIENCE_KEY, this)
    );
    this.applyButton.active = canApply();
    this.addDrawableChild(applyButton);

    this.clearButton = new ButtonWidget(
      halfWidth - 79,
      halfHeight + 14,
      20,
      20,
      Text.translatable(CLEAR_KEY),
      this::onButtonClicked,
      new ButtonTooltipSupplier(CLEAR_TOOLTIP_KEY, this)
    );
    this.clearButton.active = false;
    this.addDrawableChild(clearButton);

    this.cancelButton = new ButtonWidget(
      halfWidth - 57,
      halfHeight + 14,
      20,
      20,
      Text.translatable(CANCEL_KEY),
      this::onButtonClicked,
      new ButtonTooltipSupplier(CANCEL_TOOLTIP_KEY, this)
    );
    this.addDrawableChild(cancelButton);

    // Set default input text to current name
    if (this.itemStack.hasCustomName()) {
      this.textField.setText(this.itemStack.getName().getString());
      this.clearButton.active = true;
    }
    this.setInitialFocus(this.textField);
  }

  @Override
  public void removed() {
    super.removed();
    if (this.client != null) {
      this.client.keyboard.setRepeatEvents(false);
    }
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
    var costMultiplier = ConvenientNameTags.CONFIG.renameCostPerWholeStack() ? 1 : this.itemStack.getCount();
    var cost = ConvenientNameTags.CONFIG.renameCost() * costMultiplier;
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
    var costMultiplier = ConvenientNameTags.CONFIG.renameCostPerWholeStack() ? 1 : this.itemStack.getCount();
    var cost = ConvenientNameTags.CONFIG.renameCost() * costMultiplier;
    if (cost > 0) {
      var canAfford = player.experienceLevel >= cost;
      var text = Text.translatable(EXPERIENCE_REQUIRED_KEY, cost).formatted(canAfford ? Formatting.GREEN : Formatting.RED);
      var textWidth = textRenderer.getWidth(text);
      this.fillGradient(
        matrices,
        halfWidth + 100 - textWidth - 3,
        halfHeight - 24,
        halfWidth + 101,
        halfHeight - 13,
        0x80000000,
        0x80000000
      );
      this.textRenderer.drawWithShadow(
        matrices,
        text,
        halfWidth + 99 - textWidth,
        halfHeight - 22.0F,
        0xFFFFFF
      );
    }
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

  private static class ApplyButtonTooltipSupplier implements ButtonWidget.TooltipSupplier {
    private final RenameNameTagScreen screen;
    private final Text text;
    private final Text disabledText;

    public ApplyButtonTooltipSupplier(
      String key,
      String disabledKey,
      RenameNameTagScreen screen
    ) {
      this.screen = screen;
      this.text = Text.translatable(key);
      this.disabledText = Text.translatable(disabledKey).formatted(Formatting.RED);
    }

    @Override
    public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
      var costMultiplier = ConvenientNameTags.CONFIG.renameCostPerWholeStack() ? 1 : screen.itemStack.getCount();
      var cost = ConvenientNameTags.CONFIG.renameCost() * costMultiplier;
      if (cost > 0) {
        var canAfford = screen.player.experienceLevel >= cost;
        screen.renderTooltip(matrices, canAfford ? text : disabledText, mouseX, mouseY + 12);
      }
    }

    @Override
    public void supply(Consumer<Text> consumer) {
      var costMultiplier = ConvenientNameTags.CONFIG.renameCostPerWholeStack() ? 1 : screen.itemStack.getCount();
      var cost = ConvenientNameTags.CONFIG.renameCost() * costMultiplier;
      consumer.accept(screen.player.experienceLevel >= cost ? text : disabledText);
    }
  }
}
