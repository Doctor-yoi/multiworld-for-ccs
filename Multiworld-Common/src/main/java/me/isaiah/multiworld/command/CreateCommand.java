package me.isaiah.multiworld.command;

import java.util.Random;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
// import net.minecraft.util.registry.Registry;
// import net.minecraft.registry.RegistryKey;
// import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
// import net.minecraft.world.biome.Biome;
// import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import me.isaiah.multiworld.MultiworldMod;

import static me.isaiah.multiworld.MultiworldMod.text;
import static me.isaiah.multiworld.MultiworldMod.text_plain;
import net.minecraft.server.world.ServerWorld;

import java.io.File;
import me.isaiah.multiworld.config.*;

public class CreateCommand {
	
	public static int run(MinecraftServer mc, ServerPlayerEntity plr, String[] args) {
		if (args.length == 1 || args.length == 2) {
			plr.sendMessage(text_plain("Usage: /mv create <id> <env> <?seed> <?difficulty>"), false);
			return 0;
		}
		
		Identifier dim = null;
		long seed;
		if(args[3]!=null){
			seed = Long.parseLong(args[3]);
		}else{
			Random r = new Random();
			seed = r.nextInt();
		}
		
		Difficulty dif = Difficulty.NORMAL;
		if(args.length==4){
			switch (args[4]){
				case "normal":
					dif = Difficulty.NORMAL;
					break;
				
				case "easy":
					dif = Difficulty.EASY;
					break;
				
				case "hard":
					dif = Difficulty.HARD;
					break;
				
				case "peaceful":
					dif = Difficulty.PEACEFUL;
					break;
				
				default:
					dif = Difficulty.NORMAL;
			}
		}
		
		
		ChunkGenerator gen = null;
		if (args[2].contains("NORMAL")) {
			gen = mc.getWorld(World.OVERWORLD).getChunkManager().getChunkGenerator();
			dim = Util.OVERWORLD_ID;
		}
		
		if (args[2].contains("NETHER")) {
			gen = mc.getWorld(World.NETHER).getChunkManager().getChunkGenerator();
			dim = Util.THE_NETHER_ID;
		}
		
		if (args[2].contains("END")) {
			gen = mc.getWorld(World.END).getChunkManager().getChunkGenerator();
			dim = Util.THE_END_ID;
		}
		
		String arg1 = args[1];
		if (arg1.indexOf(':') == -1) arg1 = "multiworld:" + arg1;
		
		ServerWorld world = MultiworldMod.create_world(arg1, dim, gen, dif, seed);
		make_config(world, args[2], seed);
		
		plr.sendMessage(text("Created world with id: " + args[1], Formatting.GREEN), false);
		
		return 1;
	}
	
	public static void reinit_world_from_config(MinecraftServer mc, String id) {
		File config_dir = new File("config");
        config_dir.mkdirs();
		
		String[] spl = id.split(":");
        
        File cf = new File(config_dir, "multiworld"); 
        cf.mkdirs();

        File worlds = new File(cf, "worlds");
        worlds.mkdirs();

        File namespace = new File(worlds, spl[0]);
        namespace.mkdirs();

        File wc = new File(namespace, spl[1] + ".yml");
        FileConfiguration config;
        try {
			if (!wc.exists()) {
				wc.createNewFile();
			}
            config = new FileConfiguration(wc);
			String env = config.getString("environment");
			long seed = 0;
			
			try {
				seed = config.getLong("seed");
			} catch (Exception e) {
				seed = config.getInt("seed");
			}
			
			Identifier dim = null;

			ChunkGenerator gen = null;
			if (env.contains("NORMAL")) {
				gen = mc.getWorld(World.OVERWORLD).getChunkManager().getChunkGenerator(); // .withSeed(seed);
				dim = Util.OVERWORLD_ID;
			}

			if (env.contains("NETHER")) {
				gen = mc.getWorld(World.NETHER).getChunkManager().getChunkGenerator();
				dim = Util.THE_NETHER_ID;
			}
			
			if (env.contains("END")) {
				gen = mc.getWorld(World.END).getChunkManager().getChunkGenerator(); // .withSeed(seed);
				dim = Util.THE_END_ID;
			}
			
			MultiworldMod.create_world(id, dim, gen, Difficulty.NORMAL, seed);
			
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void make_config(ServerWorld w, String dim, long seed) {
        File config_dir = new File("config");
        config_dir.mkdirs();
        
        File cf = new File(config_dir, "multiworld"); 
        cf.mkdirs();

        File worlds = new File(cf, "worlds");
        worlds.mkdirs();

        Identifier id = w.getRegistryKey().getValue();
        File namespace = new File(worlds, id.getNamespace());
        namespace.mkdirs();

        File wc = new File(namespace, id.getPath() + ".yml");
        FileConfiguration config;
        try {
			if (!wc.exists()) {
				wc.createNewFile();
			}
            config = new FileConfiguration(wc);
			config.set("namespace", id.getNamespace());
			config.set("path", id.getPath());
			config.set("environment", dim);
			config.set("seed", seed);
			config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
