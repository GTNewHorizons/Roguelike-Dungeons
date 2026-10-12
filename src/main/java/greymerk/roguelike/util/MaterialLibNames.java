package greymerk.roguelike.util;

import cpw.mods.fml.common.Loader;

/// Recognises the `ml:<Material>:<shape>` names settings files use to pin a MaterialLib item or block.
/// Material and shape survive the metadata renumbering between sessions.
///
/// Carries no MaterialLib types and is safe to call when MaterialLib is absent.
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
