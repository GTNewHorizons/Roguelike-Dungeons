package greymerk.roguelike.dungeon.settings.builtin;

import greymerk.roguelike.dungeon.base.DungeonFactory;
import greymerk.roguelike.dungeon.base.DungeonRoom;
import greymerk.roguelike.dungeon.settings.DungeonSettings;
import greymerk.roguelike.dungeon.settings.LevelSettings;

public class SettingsRooms extends DungeonSettings {

    public SettingsRooms() {
        for (int i = 0; i < 5; ++i) {

            DungeonFactory factory;

            switch (i) {
                case 0:
                    factory = new DungeonFactory();
                    factory.addSingle(DungeonRoom.CAKE);
                    factory.addSingle(DungeonRoom.FIRE);
                    factory.addRandom(DungeonRoom.BRICK, 4);
                    factory.addRandom(DungeonRoom.CORNER, 2);
                    break;
                case 1:
                    factory = new DungeonFactory();
                    factory.addSingle(DungeonRoom.PIT);
                    factory.addRandom(DungeonRoom.CORNER, 10);
                    factory.addRandom(DungeonRoom.BRICK, 3);
                    break;
                case 2:
                    factory = new DungeonFactory();
                    factory.addSingle(DungeonRoom.OSSUARY);
                    factory.addSingle(DungeonRoom.CRYPT);
                    factory.addSingle(DungeonRoom.CREEPER);
                    factory.addSingle(DungeonRoom.FIRE);
                    factory.addSingle(DungeonRoom.SPIDER);
                    factory.addSingle(DungeonRoom.PRISON);
                    factory.addRandom(DungeonRoom.CRYPT, 5);
                    factory.addRandom(DungeonRoom.CORNER, 5);
                    factory.addRandom(DungeonRoom.BRICK, 3);
                    break;
                case 3:
                    factory = new DungeonFactory();
                    factory.addSingle(DungeonRoom.OSSUARY);
                    factory.addSingle(DungeonRoom.ENDER);
                    factory.addSingle(DungeonRoom.CRYPT);
                    factory.addRandom(DungeonRoom.PRISON, 3);
                    factory.addRandom(DungeonRoom.SLIME, 5);
                    factory.addRandom(DungeonRoom.CREEPER, 1);
                    factory.addRandom(DungeonRoom.SPIDER, 1);
                    factory.addRandom(DungeonRoom.PIT, 1);
                    break;
                case 4:
                    factory = new DungeonFactory();
                    factory.addSingle(DungeonRoom.OBSIDIAN);
                    factory.addSingle(DungeonRoom.FIRE);
                    factory.addSingle(DungeonRoom.NETHERFORT);
                    factory.addRandom(DungeonRoom.CORNER, 10);
                    factory.addRandom(DungeonRoom.NETHER, 3);
                    factory.addRandom(DungeonRoom.SPIDER, 1);
                    break;
                default:
                    factory = new DungeonFactory();
                    break;
            }

            LevelSettings level = new LevelSettings();
            level.setRooms(factory);
            levels.put(i, level);
        }
    }
}
