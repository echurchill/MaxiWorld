package me.daddychurchill.MaxiWorld;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.daddychurchill.MaxiWorld.Support.InitialBlocks;
import me.daddychurchill.MaxiWorld.Support.RealBlocks;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class ChunkCallback extends ChunkGenerator {

	private WorldConfig config;
	private BlockCallback blockCallback;
	private Generator generators;

	public ChunkCallback(WorldConfig config) {
		super();
		this.config = config;
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		blockCallback = new BlockCallback(this);
		return Arrays.asList((BlockPopulator) blockCallback);
	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		// see if this works any better (loosely based on ExpansiveTerrain)
		int x = random.nextInt(100) - 50;
		int z = random.nextInt(100) - 50;
		int y = Math.max(world.getHighestBlockYAt(x, z), config.getStreetLevel() + 1);
		// int y = world.getHighestBlockYAt(x, z);
		return new Location(world, x, y, z);
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {

		// who makes what?
		if (generators == null)
			generators = new Generator(world, config);

		// place to work
		InitialBlocks initialBlocks = new InitialBlocks(world, createChunkData(world), chunkX, chunkZ);

		// figure out what everything looks like
		generators.generateChunk(initialBlocks, random, chunkX, chunkZ);
		generators.generateBiome(biomes);

		// let minecraft/bukkit do it's thing
		return initialBlocks.data;
	}

	public void populate(World world, Random random, Chunk source) {

		// who makes what?
		if (generators == null)
			generators = new Generator(world, config);

		// where are we?
		int chunkX = source.getX();
		int chunkZ = source.getZ();

		// place to work
		RealBlocks realChunk = new RealBlocks(world, source);

		// figure out what everything looks like
		generators.populateBlocks(realChunk, random, chunkX, chunkZ);
	}
}
