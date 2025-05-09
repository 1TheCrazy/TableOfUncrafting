package me.onethecrazy.blocks;

import me.onethecrazy.TableOfUncrafting;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block TABLE_OF_UNCRAFTING = register(
            new TableOfUncraftingBlock(
                    AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
            ),
            "uncrafting_table",
            true
    );


    // Default Template from fabric docs: https://docs.fabricmc.net/1.21/develop/blocks/first-block
    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = Identifier.of(TableOfUncrafting.MOD_ID, name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize() {
        TableOfUncrafting.LOGGER.info("Registering items for: " + TableOfUncrafting.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(TABLE_OF_UNCRAFTING));
    }
}
