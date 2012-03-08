package me.daddychurchill.MaxiWorld;

import java.util.Random;

import me.daddychurchill.MaxiWorld.Support.ByteChunk;
import me.daddychurchill.MaxiWorld.Support.RealChunk;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class Generator {
	private World world;
	private WorldConfig config;
	
	private int stoneThickness; // how thick is the stone bit
	private int dirtThickness; // how thick is the dirt bit
	private int waterLevel; // how thick is the water bit
	
	private double horizontalFactor;
	private double verticalFactor;
	
	private double oddsForAnOre = 0.050;
	private double oddsForATree = 0.010;
	
	private int blocksPerBlock;
	private int xBlockHeight;
	private int zBlockHeight;
	
	private SimplexNoiseGenerator noiseMaker;
	
	public Generator(World aWorld, WorldConfig aConfig) {
		super();
		this.world = aWorld;
		this.config = aConfig;
		
		long seed = world.getSeed();
		noiseMaker = new SimplexNoiseGenerator(seed);
		
		blocksPerBlock = config.getBlockSize(world);
		
		horizontalFactor = blocksPerBlock * 20.0;
		verticalFactor = 7.0 * (8.0 / (double) blocksPerBlock);
		
		xBlockHeight = ByteChunk.chunkWidth / blocksPerBlock;
		zBlockHeight = ByteChunk.chunkWidth / blocksPerBlock;
		
		stoneThickness = 1;
		dirtThickness = 1;
		waterLevel = (int) verticalFactor - stoneThickness - dirtThickness + 1;
	}
	
	private int getGroundSurfaceY(int chunkX, int chunkZ, int blockX, int blockZ) {
		double x = chunkX * blocksPerBlock + blockX;
		double z = chunkZ * blocksPerBlock + blockZ;
		double noiseLevel = noiseMaker.noise(x / horizontalFactor, z / horizontalFactor);
		return stoneThickness + dirtThickness + waterLevel + NoiseGenerator.floor(noiseLevel * verticalFactor);
	}
	
//	public boolean ifATree(int chunkX, int chunkZ, int blockX, int blockZ) {
//		double x = chunkX * blocksPerBlock + blockX;
//		double z = chunkZ * blocksPerBlock + blockZ;
//		return (noiseMaker.noise(x, z) + 1) / 2 < oddsForATree;
//	}
	
	private boolean ifATree(Random random) {
		return random.nextDouble() < oddsForATree;
	}
	
	private boolean ifAnOre(Random random) {
		return random.nextDouble() < oddsForAnOre;
	}
	
	public void generateChunk(ByteChunk chunk, Random random, int chunkX, int chunkZ) {
		for (int x = 0; x < xBlockHeight; x++) {
			for (int z = 0; z < zBlockHeight; z++) {
				int ySurface = getGroundSurfaceY(chunkX, chunkZ, x, z);
				//MaxiWorld.log.info("ySurface = " + ySurface);
				
				int xOrigin = x * blocksPerBlock;
				int zOrigin = z * blocksPerBlock;
				
				for (int y = 0; y < ySurface + 1; y++) {
					int yOrigin = y * blocksPerBlock;
					
					// generate the layers
					if (y >= 0 && y < ySurface - dirtThickness) {
						generateStoneBlock(chunk, xOrigin, yOrigin, zOrigin, random);
					} else if (y >= ySurface - dirtThickness && y < ySurface) {
						generateDirtBlock(chunk, xOrigin, yOrigin, zOrigin);
					} else if (y == ySurface) {
						if (y < waterLevel) {
							generateSandBlock(chunk, xOrigin, yOrigin, zOrigin);
							for (int yLake = y + 1; yLake < waterLevel; yLake++)
								generateWaterBlock(chunk, xOrigin, yLake * blocksPerBlock, zOrigin);
						} else {
							generateGrassBlock(chunk, xOrigin, yOrigin, zOrigin);
							if (ifATree(random))
								generateTrunkBlock(chunk, xOrigin, (y + 1) * blocksPerBlock, zOrigin);
						}
					}
				}
			}
		}
	}
	
	private byte byteBase = (byte) Material.BEDROCK.getId();
	private byte byteStone = (byte) Material.STONE.getId();
	private byte byteDirt = (byte) Material.DIRT.getId();
	private byte byteGrass = (byte) Material.GRASS.getId();
	private byte byteSand = (byte) Material.SAND.getId();
	private byte byteWater = (byte) Material.STATIONARY_WATER.getId();
	private byte byteTrunk = (byte) Material.LOG.getId();
	private byte byteLeaves = (byte) Material.LEAVES.getId();
	
	private void generateStoneBlock(ByteChunk chunk, int x, int y, int z, Random random) {
		if (y == 0) {
			chunk.setBlocks(x, x + blocksPerBlock, 0, 1, z, z + blocksPerBlock, byteBase);
			chunk.setBlocks(x, x + blocksPerBlock, y + 1, y + blocksPerBlock, z, z + blocksPerBlock, byteStone);
		} else
			chunk.setBlocks(x, x + blocksPerBlock, y, y + blocksPerBlock, z, z + blocksPerBlock, byteStone);
		
		if (ifAnOre(random))
			chunk.setBlocks(x + 1, x + blocksPerBlock - 1, y + 1, y + blocksPerBlock - 1, z + 1, z + blocksPerBlock - 1, pickRandomMineralAt(random, y));
	}

	private void generateDirtBlock(ByteChunk chunk, int x, int y, int z) {
		chunk.setBlocks(x, x + blocksPerBlock, y, y + blocksPerBlock, z, z + blocksPerBlock, byteDirt);
	}

	private void generateGrassBlock(ByteChunk chunk, int x, int y, int z) {
		chunk.setBlocks(x, x + blocksPerBlock, y, y + blocksPerBlock - 1, z, z + blocksPerBlock, byteDirt);
		chunk.setBlocks(x, x + blocksPerBlock, y + blocksPerBlock - 1, y + blocksPerBlock, z, z + blocksPerBlock, byteGrass);
	}

	private void generateSandBlock(ByteChunk chunk, int x, int y, int z) {
		chunk.setBlocks(x, x + blocksPerBlock, y, y + blocksPerBlock, z, z + blocksPerBlock, byteSand);
	}

	private void generateWaterBlock(ByteChunk chunk, int x, int y, int z) {
		chunk.setBlocks(x, x + blocksPerBlock, y, y + blocksPerBlock, z, z + blocksPerBlock, byteWater);
	}
	
	private void generateTrunkBlock(ByteChunk chunk, int x, int y, int z) {
		chunk.setBlocks(x, x + blocksPerBlock, y, y + blocksPerBlock, z, z + blocksPerBlock, byteTrunk);
	}

	private int trunkHeight = 5;
	private int leavesStart = 2;
	
	public void populateBlocks(RealChunk chunk, Random random, int chunkX, int chunkZ) {
		for (int x = 0; x < xBlockHeight; x++) {
			for (int z = 0; z < zBlockHeight; z++) {
				
				// surface features are actually in the block above the surface
				int y = getGroundSurfaceY(chunkX, chunkZ, x, z) + 1;
				
				// calculate real coordinates
				int xOrigin = x * blocksPerBlock;
				int zOrigin = z * blocksPerBlock;
				int yOrigin = y * blocksPerBlock;
				
				// what is here?
				Material surfaceFeature = chunk.getMaterial(xOrigin, yOrigin, zOrigin);
				
				// got a tree?
				if (surfaceFeature.getId() == byteTrunk) {
					
					// normalize to the world as a whole
					xOrigin = chunkX * chunk.width + xOrigin;
					zOrigin = chunkZ * chunk.width + zOrigin;
					
					// go up the trunk
					for (int yTrunk = 1; yTrunk < trunkHeight + leavesStart - 1; yTrunk++) {
						
						// where are we actually?
						yOrigin = (yTrunk + y) * blocksPerBlock;
						
						// in trunk range?
						if (yTrunk < trunkHeight)
							generateTrunkBlock(world, xOrigin, yOrigin, zOrigin);
						
						// in leaves range?
						if (yTrunk >= leavesStart) {
							switch (yTrunk - leavesStart) {
							case 0:
								generateLeavesLayer(world, random, xOrigin, yOrigin, zOrigin, 2, 0.30);
								break;
							case 1:
								generateLeavesLayer(world, random, xOrigin, yOrigin, zOrigin, 2, 0.50);
								break;
							case 2:
								generateLeavesLayer(world, random, xOrigin, yOrigin, zOrigin, 1, 0.40);
								break;
							default:
								generateLeavesLayer(world, random, xOrigin, yOrigin, zOrigin, 1, 0.10);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	private void generateLeavesLayer(World world, Random random, int xOrigin, int yOrigin, int zOrigin, int range, double oddsOfCorner) {
		for (int xLeaves = -range; xLeaves <= range; xLeaves++) {
			for (int zLeaves = -range; zLeaves <= range; zLeaves++) {
				if (!((xLeaves == -range && zLeaves == -range) ||
					  (xLeaves == -range && zLeaves == range) ||
					  (xLeaves == range && zLeaves == -range) ||
					  (xLeaves == range && zLeaves == range)) ||
					  random.nextDouble() < oddsOfCorner)
					generateLeavesBlock(world, xOrigin - xLeaves * blocksPerBlock, yOrigin, zOrigin - zLeaves * blocksPerBlock);
			}
		}
	}
	
	private void generateLeavesBlock(World world, int worldX, int worldY, int worldZ) {
		generateRealBlock(world, worldX, worldY, worldZ, byteLeaves);
	}
	
	private void generateTrunkBlock(World world, int worldX, int worldY, int worldZ) {
		generateRealBlock(world, worldX, worldY, worldZ, byteTrunk);
	}

	private void generateRealBlock(World world, int worldX, int worldY, int worldZ, int typeId) {
		Block block = world.getBlockAt(worldX, worldY, worldZ);
		if (block.isEmpty()) {
			for (int x = 0; x < blocksPerBlock; x++) {
				for (int y = 0; y < blocksPerBlock; y++) {
					for (int z = 0; z < blocksPerBlock; z++) {
						block = world.getBlockAt(worldX + x, worldY + y, worldZ + z);
						block.setTypeIdAndData(typeId, (byte) 0, false);
					}
				}
			}
		}
	}
	
	private byte pickRandomMineralAt(Random random, int y) {
		// a VERY VERY VERY rough version of http://www.minecraftwiki.net/wiki/Ore
		if (y <= 16)
			return pickRandomMineral(random, 6);
		else if (y <= 34)
			return pickRandomMineral(random, 4);
		else if (y <= 69)
			return pickRandomMineral(random, 2);
		else
			return pickRandomMineral(random, 1);
	}
	
	private byte oreIron = (byte) Material.IRON_ORE.getId();
	private byte oreGold = (byte) Material.GOLD_ORE.getId();
	private byte oreLapis = (byte) Material.LAPIS_ORE.getId();
	private byte oreRedstone = (byte) Material.REDSTONE_ORE.getId();
	private byte oreDiamond = (byte) Material.DIAMOND_ORE.getId();
	private byte oreCoal = (byte) Material.COAL_ORE.getId();
	
	private byte pickRandomMineral(Random random, int max) {
		switch (random.nextInt(max)) {
		case 1:
			return oreIron;
		case 2:
			return oreGold;
		case 3:
			return oreLapis;
		case 4:
			return oreRedstone;
		case 5:
			return oreDiamond;
		default:
			return oreCoal;
		}
	}
}
