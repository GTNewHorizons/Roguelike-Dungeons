package greymerk.roguelike.dungeon.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import greymerk.roguelike.config.RogueConfig;
import greymerk.roguelike.dungeon.settings.builtin.SettingsCustomBase;
import greymerk.roguelike.dungeon.settings.builtin.SettingsDesertTheme;
import greymerk.roguelike.dungeon.settings.builtin.SettingsForestTheme;
import greymerk.roguelike.dungeon.settings.builtin.SettingsGenerator;
import greymerk.roguelike.dungeon.settings.builtin.SettingsGrasslandTheme;
import greymerk.roguelike.dungeon.settings.builtin.SettingsJungleTheme;
import greymerk.roguelike.dungeon.settings.builtin.SettingsLootRules;
import greymerk.roguelike.dungeon.settings.builtin.SettingsMesaTheme;
import greymerk.roguelike.dungeon.settings.builtin.SettingsMountainTheme;
import greymerk.roguelike.dungeon.settings.builtin.SettingsRooms;
import greymerk.roguelike.dungeon.settings.builtin.SettingsSecrets;
import greymerk.roguelike.dungeon.settings.builtin.SettingsSegments;
import greymerk.roguelike.dungeon.settings.builtin.SettingsSize;
import greymerk.roguelike.dungeon.settings.builtin.SettingsSwampTheme;
import greymerk.roguelike.dungeon.settings.builtin.SettingsTheme;
import greymerk.roguelike.util.WeightedChoice;
import greymerk.roguelike.util.WeightedRandomizer;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IWorldEditor;

public class SettingsResolver {

    private static final String SETTINGS_DIRECTORY = RogueConfig.configDirName + "/settings";
    private Map<String, DungeonSettings> settings;
    private List<DungeonSettings> builtin;
    private DungeonSettings base;

    public SettingsResolver() {
        settings = new HashMap<String, DungeonSettings>();
        DungeonSettings base = new SettingsBlank();
        base = new DungeonSettings(base, new SettingsRooms());
        base = new DungeonSettings(base, new SettingsSecrets());
        base = new DungeonSettings(base, new SettingsSegments());
        base = new DungeonSettings(base, new SettingsSize());
        base = new DungeonSettings(base, new SettingsTheme());
        base = new DungeonSettings(base, new SettingsGenerator());
        base = new DungeonSettings(base, new SettingsLootRules());
        base.setCriteria(new SpawnCriteria());
        this.base = base;

        this.builtin = new ArrayList<DungeonSettings>();
        this.builtin.add(new SettingsDesertTheme());
        this.builtin.add(new SettingsGrasslandTheme());
        this.builtin.add(new SettingsJungleTheme());
        this.builtin.add(new SettingsSwampTheme());
        this.builtin.add(new SettingsMountainTheme());
        this.builtin.add(new SettingsForestTheme());
        this.builtin.add(new SettingsMesaTheme());

        File settingsDir = new File(SETTINGS_DIRECTORY);
        if (!settingsDir.exists() || !settingsDir.isDirectory()) return;
        File[] settingsFiles = settingsDir.listFiles();
        if (settingsFiles == null) return;
        Arrays.sort(settingsFiles);

        Map<String, JsonObject> dungeons = readDungeonJsonSettings(settingsFiles);
        loadDungeonsInDependencyOrder(dungeons, getDungeonDependencies(dungeons));
    }

    private static Map<String, JsonObject> readDungeonJsonSettings(File[] settingsFiles) {
        Map<String, JsonObject> dungeons = new HashMap<>();
        for (File toParse : settingsFiles) {
            try {
                dungeons.put(toParse.getName(), readJson(toParse));
            } catch (Exception e) {
                System.err.println("Error found in file " + toParse.getName());
                System.err.println(e.getMessage());
            }
        }
        return dungeons;
    }

    private static JsonObject readJson(File toParse) throws Exception {
        try {
            return (JsonObject) new JsonParser().parse(readFile(toParse));
        } catch (JsonSyntaxException e) {
            Throwable cause = e.getCause();
            throw new Exception(cause.getMessage());
        } catch (Exception e) {
            throw new Exception("An unknown error occurred while parsing json");
        }
    }

    private static String readFile(File toParse) throws Exception {
        try {
            return Files.toString(toParse, Charsets.UTF_8);
        } catch (IOException e) {
            throw new Exception("Error reading file");
        }
    }

    private static Map<String, List<String>> getDungeonDependencies(Map<String, JsonObject> dungeons) {
        Map<String, List<String>> dependencies = new HashMap<>();
        for (Map.Entry<String, JsonObject> dungeon : dungeons.entrySet()) {
            dependencies.put(dungeon.getKey(), DungeonSettings.readDungeonInheritance(dungeon.getValue()));
        }
        return dependencies;
    }

    private void loadDungeonsInDependencyOrder(Map<String, JsonObject> dungeons,
            Map<String, List<String>> dungeonDependencies) {
        Set<String> dungeonsToAdd = new HashSet<>(dungeons.keySet());

        boolean loadedSomeDungeons;
        do {
            loadedSomeDungeons = loadDungeons(dungeons, dungeonDependencies, dungeonsToAdd);
        } while (loadedSomeDungeons);

        logUnloadedDungeons(dungeonsToAdd, dungeonDependencies);
    }

    private boolean loadDungeons(Map<String, JsonObject> dungeons, Map<String, List<String>> dungeonDependencies,
            Set<String> dungeonsToAdd) {
        boolean loadedSomeDungeons = false;
        for (Iterator<String> it = dungeonsToAdd.iterator(); it.hasNext();) {
            String dungeonPath = it.next();
            if (dungeonDependencies.get(dungeonPath).isEmpty()) {
                JsonObject root = dungeons.get(dungeonPath);
                String dungeonName = DungeonSettings.readDungeonName(root);
                loadDungeon(dungeonName, root);
                for (String otherDungeon : dungeonsToAdd) {
                    dungeonDependencies.get(otherDungeon).remove(dungeonName);
                }
                it.remove();
                loadedSomeDungeons = true;
            }
        }
        return loadedSomeDungeons;
    }

    private void loadDungeon(String dungeonName, JsonObject root) {
        try {
            settings.put(dungeonName, new DungeonSettings(settings, root));
        } catch (Exception e) {
            System.err.println("An error occured while adding " + dungeonName);
            System.err.println(e.getMessage());
        }
    }

    private static void logUnloadedDungeons(Set<String> dungeonsToAdd, Map<String, List<String>> dungeonDependencies) {
        for (String unloadedDungeon : dungeonsToAdd) {
            System.err.println("Dungeon " + unloadedDungeon + " not loaded because of unmet dependencies");
            System.err.println("Unmet dependencies: " + dungeonDependencies.get(unloadedDungeon));
        }
    }

    public DungeonSettings getByName(String name) {
        DungeonSettings override = this.settings.get(name);
        if (override == null) return null;
        return new DungeonSettings(this.base, override);
    }

    public ISettings getSettings(IWorldEditor editor, Random rand, Coord pos) {

        DungeonSettings builtin = this.getBuiltin(editor, rand, pos);
        DungeonSettings custom = this.getCustom(editor, rand, pos);

        if (custom != null) {
            List<SettingsType> overrides = custom.getOverrides();
            DungeonSettings customBase = new SettingsCustomBase();
            for (SettingsType type : SettingsType.values()) {
                if (overrides.contains(type)) continue;
                switch (type) {
                    case LOOT:
                        break;
                    case LOOTRULES:
                        customBase = new DungeonSettings(customBase, new SettingsLootRules());
                        break;
                    case SECRETS:
                        customBase = new DungeonSettings(customBase, new SettingsSecrets());
                        break;
                    case ROOMS:
                        customBase = new DungeonSettings(customBase, new SettingsRooms());
                        break;
                    case THEMES:
                        customBase = new DungeonSettings(customBase, new SettingsTheme());
                        break;
                    case SEGMENTS:
                        customBase = new DungeonSettings(customBase, new SettingsSegments());
                        break;
                    case SIZE:
                        customBase = new DungeonSettings(customBase, new SettingsSize());
                        break;
                    case GENERATORS:
                        customBase = new DungeonSettings(customBase, new SettingsGenerator());
                        break;
                }
            }
            return new DungeonSettings(customBase, custom);
        }

        if (builtin != null && RogueConfig.getBoolean(RogueConfig.DONOVELTYSPAWN)) {
            return new DungeonSettings(this.base, builtin);
        }

        if (this.base.isValid(editor, pos)) return new DungeonSettings(this.base);

        return null;

    }

    private DungeonSettings getBuiltin(IWorldEditor editor, Random rand, Coord pos) {
        WeightedRandomizer<DungeonSettings> settingsRandomizer = new WeightedRandomizer<DungeonSettings>();

        for (DungeonSettings setting : this.builtin) {
            if (setting.isValid(editor, pos)) {
                settingsRandomizer.add(new WeightedChoice<DungeonSettings>(setting, setting.criteria.weight));
            }
        }

        return settingsRandomizer.get(rand);
    }

    private DungeonSettings getCustom(IWorldEditor editor, Random rand, Coord pos) {
        WeightedRandomizer<DungeonSettings> settingsRandomizer = new WeightedRandomizer<DungeonSettings>();

        for (DungeonSettings setting : this.settings.values()) {
            if (setting.isValid(editor, pos)) {
                int weight = setting.criteria.weight;
                settingsRandomizer.add(new WeightedChoice<DungeonSettings>(setting, weight));
            }
        }

        return settingsRandomizer.get(rand);
    }

    public ISettings getDefaultSettings() {
        return new DungeonSettings(base);
    }

    public ISettings getWithDefault(String name) {

        DungeonSettings custom = this.settings.get(name);
        if (custom == null) return null;
        List<SettingsType> overrides = custom.getOverrides();
        DungeonSettings customBase = new SettingsCustomBase();
        for (SettingsType type : SettingsType.values()) {
            if (overrides.contains(type)) continue;
            switch (type) {
                case LOOT:
                    break;
                case LOOTRULES:
                    customBase = new DungeonSettings(customBase, new SettingsLootRules());
                    break;
                case SECRETS:
                    customBase = new DungeonSettings(customBase, new SettingsSecrets());
                    break;
                case ROOMS:
                    customBase = new DungeonSettings(customBase, new SettingsRooms());
                    break;
                case THEMES:
                    customBase = new DungeonSettings(customBase, new SettingsTheme());
                    break;
                case SEGMENTS:
                    customBase = new DungeonSettings(customBase, new SettingsSegments());
                    break;
                case SIZE:
                    customBase = new DungeonSettings(customBase, new SettingsSize());
                    break;
                case GENERATORS:
                    customBase = new DungeonSettings(customBase, new SettingsGenerator());
                    break;
            }
        }
        return new DungeonSettings(customBase, custom);
    }

    @Override
    public String toString() {
        String s = "";
        for (String key : this.settings.keySet()) {
            s += key += " ";
        }
        return s;
    }
}
