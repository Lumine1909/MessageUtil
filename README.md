## A Demo Usage

```java
package io.github.lumine1909;

import io.github.lumine1909.messageutil.api.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DemoPlugin extends JavaPlugin implements Listener {

    private final MessageUtil messageUtil = new MessageUtil(this, MessageUtil.InjectorType.PLAYER_LIST);

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        messageUtil.enable();
        messageUtil.getMessengerManager().register(new DemoReceiver());
    }

    @Override
    public void onDisable() {
        messageUtil.disable();
    }
}
```

```java
package io.github.lumine1909;

import io.github.lumine1909.messageutil.api.MessageReceiver;
import io.github.lumine1909.messageutil.object.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
public class DemoReceiver extends MessageReceiver {

    @Override
    public boolean isActive() {
        return true;
    }

    @Bytebuf(key = "blocktuner:server_bound_hello")
    public void handleHello(PacketContext context, FriendlyByteBuf buf) {
        System.out.println("Received Hello packet");
        int protocolVersion = buf.readInt();
        if (protocolVersion == 3) {
            context.send("blocktuner:client_bound_hello", buf);
        }
    }

    @Bytebuf(key = "blocktuner:server_bound_tuning")
    public void handleTuning(PacketContext context, FriendlyByteBuf buf) {
        ServerPlayer player = context.player().orElseThrow();
        BlockPos pos = buf.readBlockPos();
        Level world = player.level();
        if (world.getBlockState(pos).getBlock() != Blocks.NOTE_BLOCK) {
            return;
        }
        player.sendSystemMessage(Component.literal("Sorry, this is a fake tuning plugin").withStyle(ChatFormatting.AQUA));
    }
}
```