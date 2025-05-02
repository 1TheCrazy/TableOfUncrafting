package me.onethecrazy.blocks;

import me.onethecrazy.TableOfUncrafting;
import me.onethecrazy.sceen.UncraftingTableScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;



public class TableOfUncraftingBlock extends Block {

    private static final Text TITLE = Text.translatable("container.tableofuncrafting.uncrafting");

    public TableOfUncraftingBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, inventory, playerEntity) -> new UncraftingTableScreenHandler(syncId, inventory), TITLE
            ));
            TableOfUncrafting.LOGGER.info("Should be here"); // <- this is printed
            return ActionResult.CONSUME;
        }

        return ActionResult.SUCCESS;
    }
}
