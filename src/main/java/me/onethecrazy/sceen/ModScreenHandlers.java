package me.onethecrazy.sceen;

import me.onethecrazy.TableOfUncrafting;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<UncraftingTableScreenHandler> UNCRAFTING_SCREEN =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(TableOfUncrafting.MOD_ID, "uncrafting_screen"),
                    new ScreenHandlerType<>(UncraftingTableScreenHandler::new,
                            FeatureFlags.VANILLA_FEATURES)
            );

    public static void initialize() {
        TableOfUncrafting.LOGGER.info("Initializing Screen Handlers");
    }
}
