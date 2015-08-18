package me.daddychurchill.MaxiWorld.Support;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

public class RealBlocks {
	private Chunk chunk;

	public int width = InitialBlocks.chunksBlockWidth;
	public int height;

	public RealBlocks(World world, Chunk chunk) {
		super();
		this.chunk = chunk;
		height = world.getMaxHeight();
	}
	
	public void setBlock(int x, int y, int z, Material material, boolean aDoPhysics) {
		chunk.getBlock(x, y, z).setType(material, aDoPhysics);
	}

	public Material getMaterial(int x, int y, int z) {
		return chunk.getBlock(x, y, z).getType();
	}
}
