package me.onethecrazy;

import me.onethecrazy.blocks.ModBlocks;
import me.onethecrazy.sceen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableOfUncrafting implements ModInitializer {
	public static final String MOD_ID = "tableofuncrafting";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModScreenHandlers.initialize();
	}
}