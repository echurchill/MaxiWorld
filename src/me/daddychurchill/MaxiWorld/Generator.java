package me.daddychurchill.MaxiWorld;

import java.util.Random;

import me.daddychurchill.MaxiWorld.Support.ByteChunk;
import me.daddychurchill.MaxiWorld.Support.RealChunk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class Generator {
	private World world;
	private WorldConfig config;
	
	protected int specialBlockOdds; // 1/n chance that there is minerals on this level
	protected int specialsPerLayer; // number of minerals per layer
	protected int bottomLevel; // how thick is the bottom bit
	protected int middleThickness; // how thick is the middle bit
	protected int liquidLevel; // how thick is the water bit
	protected int flowerOdds = 6; // 1/n chance that there is a flower on the grass, if there isn't a tree else make some tall grass
	protected TreeType treeType;
	protected int treesPerChunk;
	
	protected byte bottomMaterial; // what is the stone made of?
	protected byte middleMaterial; // what is dirt made of?
	protected byte topMaterial; // what is grass made of?
	protected byte liquidBaseMaterial; // what is sand made of?
	protected byte liquidMaterial; // what is the liquid made of?
	protected byte bladesMaterial; // what is a blade of grass made of?
	protected byte flowerMaterial; // what is a flower made of?
	
	protected int mineralId; // for later use in the populator
	protected int fertileId;
	protected int airId; 
	
	protected int octives = 3;
	protected double xFactor = 1.0;
	protected double zFactor = 1.0;
	protected double frequency = 0.5;
	protected double amplitude = 0.5;
	protected double hScale = 1.0 / 64.0;
	protected double vScale = 16.0;
	
	private SimplexOctaveGenerator noiseMaker;
	
	public Generator(World world, WorldConfig config) {
		super();
		this.world = world;
		this.config = config;
		
		long seed = world.getSeed();
		Random random = new Random(seed);
		noiseMaker = new SimplexOctaveGenerator(seed, octives);
		
		specialBlockOdds = random.nextInt(3) + 1;
		specialsPerLayer = random.nextInt(20) + 10;
		bottomLevel = random.nextInt(32) + 48;
		liquidLevel = bottomLevel + random.nextInt(8) - 4;
		middleThickness = random.nextInt(5) + 1;
		treeType = random.nextBoolean() ? TreeType.BIRCH : TreeType.TREE;
		treesPerChunk = 2;
		
		xFactor = calcRandomRange(random, 0.75, 1.25);
		zFactor = calcRandomRange(random, 0.75, 1.25);
		frequency = calcRandomRange(random, 0.40, 0.60);
		amplitude = calcRandomRange(random, 0.40, 0.60);
		vScale = calcRandomRange(random, 13.0, 19.0);
		
		bottomMaterial = (byte) Material.STONE.getId();
		middleMaterial = (byte) Material.DIRT.getId();
		topMaterial = (byte) Material.GRASS.getId();
		liquidBaseMaterial = (byte) Material.SAND.getId();
		liquidMaterial = (byte) Material.STATIONARY_WATER.getId();
		
		mineralId = bottomMaterial;
		fertileId = topMaterial;
		airId = Material.AIR.getId();
	}
	
	protected int getHeight(double x, double z) {
		return NoiseGenerator.floor(noiseMaker.noise(x, z, frequency, amplitude) * vScale) + bottomLevel;
	}

	protected double calcRandomRange(Random random, double min, double max) {
		return min + random.nextDouble() * (max - min);
	}
	
	public void generateChunk(ByteChunk chunk, Random random, int chunkX, int chunkZ) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int y = getHeight((chunkX * 16 + x) / xFactor, (chunkZ * 16 + z) / zFactor);
				chunk.setBlocks(x, 1, y - middleThickness, z, bottomMaterial);
				chunk.setBlocks(x, y - middleThickness, y, z, middleMaterial);
				if (y < liquidLevel) {
					chunk.setBlock(x, y, z, liquidBaseMaterial);
					chunk.setBlocks(x, y + 1, liquidLevel, z, liquidMaterial);
				} else
					chunk.setBlock(x, y, z, topMaterial);
			}
		}
	}

	public void populateBlocks(RealChunk chunk, Random random, int chunkX, int chunkZ) {
		populateSpecials(chunk, random, chunkX, chunkZ);
		populateTrees(chunk, random, chunkX, chunkZ);
	}
	
	protected void populateSpecials(RealChunk chunk, Random random, int chunkX, int chunkZ) {

		// sprinkle minerals/foliage for each y layer, one of millions of ways to do this!
		for (int y = 1; y < 127; y++) {
			if (random.nextInt(specialBlockOdds) == 0) {
				for (int i = 0; i < specialsPerLayer; i++) {
					Block block = chunk.getBlock(random.nextInt(16), y, random.nextInt(16));
					int id = block.getTypeId();
					if (id != airId) {
						
						// Transmutation?
						if (id == mineralId)
							populateMineral(block, random, y);
					}
				}
			}
		}
	}
	
	protected void populateMineral(Block block, Random random, int atY) {
		block.setTypeId(pickRandomMineralAt(random, atY).getId(), false);
	}
	
	protected void populateTrees(RealChunk chunk, Random random, int chunkX, int chunkZ) {
		
		// plant lots of trees
		int worldX = chunk.chunkX * chunk.width; // these use the chunk's world relative location instead..
		int worldZ = chunk.chunkZ * chunk.width; // ..of well's as generateTree needs world coordinates
		for (int t = 0; t < treesPerChunk; t++) {
			int centerX = worldX + random.nextInt(16);
			int centerZ = worldZ + random.nextInt(16);
			int centerY = world.getHighestBlockYAt(centerX, centerZ);
			Block rootBlock = world.getBlockAt(centerX, centerY - 1, centerZ);
			if (rootBlock.getTypeId() == fertileId) {
				Location treeAt = rootBlock.getLocation().add(0, 1, 0);
				world.generateTree(treeAt, treeType);
			}
		}
	}
	
	protected Material pickRandomMineralAt(Random random, int y) {
		// a VERY VERY rough version of http://www.minecraftwiki.net/wiki/Ore
		if (y <= 16)
			return pickRandomMineral(random, 6);
		else if (y <= 34)
			return pickRandomMineral(random, 4);
		else if (y <= 69)
			return pickRandomMineral(random, 2);
		else
			return pickRandomMineral(random, 1);
	}
	
	protected Material pickRandomMineral(Random random, int max) {
		switch (random.nextInt(max)) {
		case 1:
			return Material.IRON_ORE;
		case 2:
			return Material.GOLD_ORE;
		case 3:
			return Material.LAPIS_ORE;
		case 4:
			return Material.REDSTONE_ORE;
		case 5:
			return Material.DIAMOND_ORE;
		default:
			return Material.COAL_ORE;
		}
	}
}
