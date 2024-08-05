package net.slqmy.bedwars_plugin.utility;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PacketUtility {
	public static void respawnPlayer(@NotNull final Player player) {
		final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

		serverPlayer.connection.send(
						new ClientboundGameEventPacket(
										ClientboundGameEventPacket.IMMEDIATE_RESPAWN,
										1
						)
		);
	}
}
