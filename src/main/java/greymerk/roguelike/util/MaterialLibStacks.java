package greymerk.roguelike.util;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ruling_0.materiallib.api.StackResolver;

/// Turns a `ml:<Material>:<shape>` settings name into the stack it pins.
///
/// Sole holder of MaterialLib imports: every call site passes [MaterialLibNames#isReference] or
/// [MaterialLibNames#isPresent] first, so this class never loads without MaterialLib.
public final class MaterialLibStacks {

    private static final Logger logger = LogManager.getLogger();

    private static int resolved;
    private static int invalid;

    private MaterialLibStacks() {}

    /// The stack `name` pins, or null when it names nothing.
    public static ItemStack resolveItem(String name) {
        return count(lookup(name));
    }

    /// The stack `name` pins, or null when it names nothing or names an item that places no block.
    public static ItemStack resolveBlock(String name) {
        ItemStack stack = lookup(name);
        if (stack != null && !(stack.getItem() instanceof ItemBlock)) {
            logger.error("{} is not a block", name);
            stack = null;
        }
        return count(stack);
    }

    /// Reports what the settings load just resolved, and clears the tally for the next one.
    public static void logCounts() {
        if (resolved + invalid > 0) {
            logger.info("Roguelike Dungeons: resolved {} MaterialLib entries ({} invalid)", resolved, invalid);
        }
        resolved = 0;
        invalid = 0;
    }

    private static ItemStack lookup(String name) {
        String[] parts = name.split(":");
        if (parts.length != 3) {
            logger.error("{} is not a material and shape pair", name);
            return null;
        }
        return StackResolver.getStack(parts[1], parts[2], 1);
    }

    private static ItemStack count(ItemStack stack) {
        if (stack == null) {
            invalid++;
        } else {
            resolved++;
        }
        return stack;
    }
}
