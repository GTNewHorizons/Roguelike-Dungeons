package greymerk.roguelike.worldgen.blocks;

import java.util.Random;

import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IWorldEditor;
import greymerk.roguelike.worldgen.MetaBlock;
import greymerk.roguelike.worldgen.shapes.RectSolid;

public class Vine {

    public static void fill(IWorldEditor editor, Random rand, Coord start, Coord end) {
        for (Coord cursor : new RectSolid(start, end)) {
            set(editor, rand, cursor);
        }
    }

    private static void set(IWorldEditor editor, Random rand, Coord origin) {
        if (!editor.isAirBlock(origin)) return;
        MetaBlock vine = BlockType.get(BlockType.VINE);
        for (Cardinal dir : Cardinal.directions) {
            if (editor.canPlace(vine, origin, dir)) {
                setOrientation(vine, dir);
                vine.set(editor, origin);
                return;
            }
        }
    }

    public static MetaBlock setOrientation(MetaBlock vine, Cardinal dir) {
        switch (dir) {
            case SOUTH:
                vine.setMeta(1);
                break;
            case WEST:
                vine.setMeta(2);
                break;
            case NORTH:
                vine.setMeta(4);
                break;
            case EAST:
                vine.setMeta(8);
                break;
            default:
                vine.setMeta(0);
                break;
        }
        return vine;
    }
}
