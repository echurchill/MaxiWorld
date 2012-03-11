package me.daddychurchill.MaxiWorld.Support;

import java.util.Arrays;

import org.bukkit.World;

public class ByteChunk {
	public final static int chunkWidth = 16;
	
	public int chunkX;
	public int chunkZ;
	public byte[] blocks;
	public int width;
	public int height;
		
	public ByteChunk (World world, int chunkX, int chunkZ) {
		super();
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.width = chunkWidth;
		this.height = world.getMaxHeight();
		this.blocks = new byte[width * width * height];
	}
	
	public void setBlock(int x, int y, int z, byte materialId) {
		blocks[(x * width + z) * height + y] = materialId;
	}
	
	public byte getBlock(int x, int y, int z) {
		return blocks[(x * width + z) * height + y];
	}
	
	public void setBlocks(int x, int y1, int y2, int z, byte materialId) {
		int xz = (x * width + z) * height;
		Arrays.fill(blocks, xz + y1, xz + y2, materialId);
	}
	
	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, byte materialId) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				int xz = (x * width + z) * height;
				Arrays.fill(blocks, xz + y1, xz + y2, materialId);
			}
		}
	}
	
	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, byte primaryId, byte secondaryId, MaterialFactory maker) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				int xz = (x * width + z) * height;
				Arrays.fill(blocks, xz + y1, xz + y2, maker.pickMaterial(primaryId, secondaryId, x, z));
			}
		}
	}
	
	public int setLayer(int blocky, byte materialId) {
		setBlocks(0, width, blocky, blocky + 1, 0, width, materialId);
		return blocky + 1;
	}
	
	public int setLayer(int blocky, int height, byte materialId) {
		setBlocks(0, width, blocky, blocky + height, 0, width, materialId);
		return blocky + height;
	}
	
	public int setLayer(int blocky, int height, int inset, byte materialId) {
		setBlocks(inset, width - inset, blocky, blocky + height, inset, width - inset, materialId);
		return blocky + height;
	}
}
