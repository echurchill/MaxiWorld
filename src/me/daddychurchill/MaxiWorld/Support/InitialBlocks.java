package me.daddychurchill.MaxiWorld.Support;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.ChunkData;

//new 1.2.3 block code is loosely based on Mike Primm's updated version of 
//DinnerBone's Moon generator from: https://github.com/mikeprimm/BukkitFullOfMoon

public class InitialBlocks {
	public ChunkData data;
	
	public static final int chunksBlockWidth = 16;
		
	public InitialBlocks (World aWorld, ChunkData chunkData, int aChunkX, int aChunkZ) {
		super();	
		data = chunkData;
	}
	
	public void setBlock(int x, int y, int z, Material material) {
		data.setBlock(x, y, z, material);
	}
	
	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, Material material) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				for (int y = y1; y < y2; y++)
					setBlock(x, y, z, material);
			}
		}
	}
}
