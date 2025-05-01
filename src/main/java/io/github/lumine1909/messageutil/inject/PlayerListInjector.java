package io.github.lumine1909.messageutil.inject;

import io.github.lumine1909.messageutil.core.PacketInterceptor;
import io.github.lumine1909.messageutil.util.ReflectionUtil;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;


public class PlayerListInjector implements Injector {

    private static final ReflectionUtil.FieldAccessor Field$MinecraftServer$playerList = new ReflectionUtil.FieldAccessor(MinecraftServer.class, "playerList");

    private InjectedPlayerList injectedPlayerList;

    @Override
    public void inject() {
        injectedPlayerList = createInjectedPlayerList();
        Field$MinecraftServer$playerList.set(MinecraftServer.getServer(), injectedPlayerList);
    }

    @Override
    public void uninject() {
        DedicatedPlayerList original = injectedPlayerList.getOriginal();
        ReflectionUtil.copyFields(injectedPlayerList, original);
        Field$MinecraftServer$playerList.set(MinecraftServer.getServer(), original);
    }

    private InjectedPlayerList createInjectedPlayerList() {
        DedicatedPlayerList original = (DedicatedPlayerList) MinecraftServer.getServer().getPlayerList();
        InjectedPlayerList playerList = new InjectedPlayerList(original);
        ReflectionUtil.copyFields(original, playerList);
        return playerList;
    }

    protected void inject2Player(Connection connection, ServerPlayer player) {
        connection.channel.pipeline().addBefore("packet_handler", "pli_handler", new PacketInterceptor() {
            @Override
            protected ServerPlayer player() {
                return player;
            }
        });
    }

    private class InjectedPlayerList extends DedicatedPlayerList {

        private final DedicatedPlayerList dedicatedPlayerList;

        public InjectedPlayerList(DedicatedPlayerList original) {
            super(original.getServer(), original.getServer().registries(), original.playerIo);
            this.dedicatedPlayerList = original;
        }

        public DedicatedPlayerList getOriginal() {
            return dedicatedPlayerList;
        }

        @Override
        public void placeNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie) {
            PlayerListInjector.this.inject2Player(connection, player);
            super.placeNewPlayer(connection, player, cookie);
        }
    }
}
