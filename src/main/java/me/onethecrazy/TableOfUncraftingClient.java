// TableOfUncraftingClient.java
package me.onethecrazy;

import me.onethecrazy.sceen.ModScreenHandlers;
import me.onethecrazy.sceen.UncraftingTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public final class TableOfUncraftingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TableOfUncrafting.LOGGER.info("Initializing Client");
        HandledScreens.register(ModScreenHandlers.UNCRAFTING_SCREEN,
                UncraftingTableScreen::new);
    }
}