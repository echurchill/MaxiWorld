package me.daddychurchill.MaxiWorld;


import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class BlockCallback extends BlockPopulator {

	private ChunkCallback chunkGen;
	
	public BlockCallback(ChunkCallback chunkGen){
		this.chunkGen = chunkGen;
	}
	
	@Override
	public void populate(World world, Random random, Chunk source) {
		chunkGen.populate(world, random, source);
	}
}
