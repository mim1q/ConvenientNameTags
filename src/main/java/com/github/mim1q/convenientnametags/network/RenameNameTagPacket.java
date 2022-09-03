package com.github.mim1q.convenientnametags.network;

import com.github.mim1q.convenientnametags.ConvenientNameTags;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RenameNameTagPacket extends PacketByteBuf {

  public static final Identifier ID = ConvenientNameTags.createId("rename_name_tag");

  public RenameNameTagPacket(String customName) {
    super(Unpooled.buffer());
    writeString(customName);
  }

  public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
    final String customName = buf.readString();

    server.execute(() -> {
      final ItemStack itemStack = player.getMainHandStack();
      if (itemStack != null) {
        if (customName.isEmpty()) {
          itemStack.removeCustomName();
        } else {
          itemStack.setCustomName(Text.of(customName));
        }
      }
    });
  }
}
