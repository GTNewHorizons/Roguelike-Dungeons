package greymerk.roguelike.worldgen.redstone;

import net.minecraft.init.Blocks;

import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IWorldEditor;
import greymerk.roguelike.worldgen.MetaBlock;

public class Lever {

    public static void generate(IWorldEditor editor, Cardinal dir, Coord pos, boolean active) {

        MetaBlock lever = new MetaBlock(Blocks.lever);

        int meta;

        switch (dir) {
            case EAST:
                meta = 1;
                break;
            case WEST:
                meta = 2;
                break;
            case SOUTH:
                meta = 3;
                break;
            case NORTH:
                meta = 4;
                break;
            case UP:
                meta = 5;
                break;
            case DOWN:
                meta = 7;
                break;

            default:
                meta = 5;
                break;
        }

        if (active) {
            meta += 8;
        }

        lever.setMeta(meta);

        lever.set(editor, pos);
    }

}
