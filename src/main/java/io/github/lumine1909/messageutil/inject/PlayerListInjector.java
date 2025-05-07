package io.github.lumine1909.messageutil.inject;

import io.github.lumine1909.messageutil.core.PacketInterceptor;
import io.github.lumine1909.messageutil.object.PacketContext;
import io.github.lumine1909.messageutil.util.ReflectionUtil;
import io.github.lumine1909.messageutil.util.UnsafeUtil;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.level.storage.PlayerDataStorage;

import java.util.UUID;


public class PlayerListInjector implements Injector {

    private static final ReflectionUtil.FieldAccessor Field$MinecraftServer$playerList = new ReflectionUtil.FieldAccessor(MinecraftServer.class, "playerList");

    private boolean injected;
    private InjectedPlayerList injectedPlayerList;

    protected static void inject2Player(Connection connection, ServerPlayer player) {
        String name = "pli_handler" + UUID.randomUUID();
        PacketContext context = new PacketContext(player, connection, name);
        connection.channel.pipeline().addBefore("packet_handler", name, new PacketInterceptor() {
            @Override
            protected PacketContext context() {
                return context;
            }
        });
    }

    @Override
    public void inject() {
        injectedPlayerList = createInjectedPlayerList();
        Field$MinecraftServer$playerList.set(MinecraftServer.getServer(), injectedPlayerList);
        injected = true;
    }

    @Override
    public void uninject() {
        DedicatedPlayerList original = injectedPlayerList.getOriginal();
        ReflectionUtil.copyFields(injectedPlayerList, original);
        Field$MinecraftServer$playerList.set(MinecraftServer.getServer(), original);
        injected = false;
    }

    @Override
    public boolean isInjected() {
        return injected;
    }

    private InjectedPlayerList createInjectedPlayerList() {
        DedicatedPlayerList original = (DedicatedPlayerList) MinecraftServer.getServer().getPlayerList();
        InjectedPlayerList playerList = UnsafeUtil.createInstance(InjectedPlayerList.class);
        playerList.setOriginal(original);
        ReflectionUtil.copyFields(original, playerList);
        return playerList;
    }

    private static class InjectedPlayerList extends DedicatedPlayerList {

        private DedicatedPlayerList dedicatedPlayerList;

        public InjectedPlayerList(DedicatedServer server, LayeredRegistryAccess<RegistryLayer> registries, PlayerDataStorage playerIo) {
            super(server, registries, playerIo);
        }

        public DedicatedPlayerList getOriginal() {
            return dedicatedPlayerList;
        }

        public void setOriginal(DedicatedPlayerList original) {
            this.dedicatedPlayerList = original;
        }

        @Override
        public void placeNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie) {
            inject2Player(connection, player);
            super.placeNewPlayer(connection, player, cookie);
        }
    }
}