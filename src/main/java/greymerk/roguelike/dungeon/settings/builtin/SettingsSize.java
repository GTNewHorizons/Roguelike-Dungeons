package greymerk.roguelike.dungeon.settings.builtin;

import greymerk.roguelike.dungeon.settings.DungeonSettings;
import greymerk.roguelike.dungeon.settings.LevelSettings;

public class SettingsSize extends DungeonSettings {

    public SettingsSize() {

        int[] numRooms = { 10, 15, 30, 20, 10 };
        int[] range = { 40, 50, 80, 60, 40 };

        for (int i = 0; i < 5; ++i) {
            LevelSettings level = new LevelSettings();
            level.setNumRooms(numRooms[i]);
            level.setRange(range[i]);
            level.setScatter(12);
            levels.put(i, level);
        }
    }

}
