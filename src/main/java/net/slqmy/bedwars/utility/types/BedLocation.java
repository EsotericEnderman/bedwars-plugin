package net.slqmy.bedwars.utility.types;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BedLocation extends Location {
	private final BlockFace facing;

	public BedLocation(@Nullable World world, double x, double y, double z, @NotNull final BlockFace facing) {
		super(world, x, y, z);
		this.facing = facing;
	}

	public BlockFace getFacing() {
		return facing;
	}
}
