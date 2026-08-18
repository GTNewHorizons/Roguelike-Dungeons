package greymerk.roguelike.util;

import cpw.mods.fml.common.Loader;

/// Recognises the `ml:<Material>:<shape>` names settings files use to pin a MaterialLib item or block by
/// material and shape rather than by metadata, which is renumbered between sessions.
///
/// Free of MaterialLib types so it can be called unconditionally; [MaterialLibStacks] holds the imports and
/// is only reachable once a check here passes.
public final class MaterialLibNames {

    private static final String MOD_ID = "materiallib";
    private static final String PREFIX = "ml:";

    private MaterialLibNames() {}

    public static boolean isPresent() {
        return Loader.isModLoaded(MOD_ID);
    }

    /// Whether `name` is a MaterialLib reference that can be resolved in this session.
    public static boolean isReference(String name) {
        return name.startsWith(PREFIX) && isPresent();
    }
}
