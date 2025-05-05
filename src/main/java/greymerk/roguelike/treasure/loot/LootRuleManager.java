package greymerk.roguelike.treasure.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import greymerk.roguelike.treasure.Treasure;
import greymerk.roguelike.treasure.TreasureManager;
import greymerk.roguelike.util.IWeighted;
import greymerk.roguelike.util.WeightedRandomizer;

public class LootRuleManager {

    private List<LootRule> rules;

    public LootRuleManager() {
        this.rules = new ArrayList<LootRule>();
    }

    public LootRuleManager(JsonElement e) {
        this.rules = new ArrayList<LootRule>();
        JsonArray arr = e.getAsJsonArray();
        for (JsonElement ruleElement : arr) {

            JsonObject rule = ruleElement.getAsJsonObject();

            if (!rule.has("loot")) continue;
            JsonArray data = rule.get("loot").getAsJsonArray();
            WeightedRandomizer<ItemStack> items = getItems(data);
            List<Integer> levels = getLevels(rule);
            List<JsonObject> lootPools = getLootPools(rule);
            for (JsonObject lootPool : lootPools) {
                addItemsToLootPoolInLevels(lootPool, levels, items);
            }

        }
    }

    private WeightedRandomizer<ItemStack> getItems(JsonArray data) {
        WeightedRandomizer<ItemStack> items = new WeightedRandomizer<ItemStack>(1);
        for (JsonElement item : data) {
            items.add(parseProvider(item.getAsJsonObject()));
        }
        return items;
    }

    private static List<Integer> getLevels(JsonObject rule) {
        List<Integer> levels = new ArrayList<Integer>();
        JsonElement levelElement = rule.get("level");
        if (levelElement.isJsonArray()) {
            JsonArray levelArray = levelElement.getAsJsonArray();
            for (JsonElement lvl : levelArray) {
                levels.add(lvl.getAsInt());
            }
        } else {
            levels.add(rule.get("level").getAsInt());
        }
        return levels;
    }

    private static List<JsonObject> getLootPools(JsonObject rule) {
        List<JsonObject> lootPools = new ArrayList<>();
        if (rule.has("loot_pools") && rule.get("loot_pools").isJsonArray()) {
            JsonArray lootPoolsJson = rule.get("loot_pools").getAsJsonArray();
            for (JsonElement lootPool1 : lootPoolsJson) {
                if (lootPool1.isJsonObject()) {
                    lootPools.add(lootPool1.getAsJsonObject());
                }
            }
        }
        if (lootPools.isEmpty()) {
            lootPools.add(rule);
        }
        return lootPools;
    }

    private void addItemsToLootPoolInLevels(JsonObject lootPool, List<Integer> levels,
            WeightedRandomizer<ItemStack> items) {
        Treasure type = lootPool.has("type") ? Treasure.valueOf(lootPool.get("type").getAsString()) : null;
        boolean each = lootPool.get("each").getAsBoolean();
        int amount = lootPool.get("quantity").getAsInt();

        for (int level : levels) {
            this.add(type, items, level, each, amount);
        }
    }

    public void add(Treasure type, IWeighted<ItemStack> item, int level, boolean toEach, int amount) {
        this.rules.add(new LootRule(type, item, level, toEach, amount));
    }

    public void add(LootRuleManager other) {
        if (other == null) return;
        this.rules.addAll(other.rules);
    }

    public void process(Random rand, ILoot loot, TreasureManager treasure) {
        for (LootRule rule : this.rules) {
            rule.process(rand, loot, treasure);
        }
    }

    private IWeighted<ItemStack> parseProvider(JsonObject lootItem) {

        int weight = lootItem.has("weight") ? lootItem.get("weight").getAsInt() : 1;

        if (lootItem.get("data").isJsonObject()) {
            JsonObject data = lootItem.get("data").getAsJsonObject();
            WeightedRandomLoot item = null;
            try {
                item = new WeightedRandomLoot(data, weight);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                item = new WeightedRandomLoot(Items.stick, 1);
            }

            return item;
        }

        JsonArray data = lootItem.get("data").getAsJsonArray();
        WeightedRandomizer<ItemStack> items = new WeightedRandomizer<ItemStack>(weight);
        for (JsonElement e : data) {
            items.add(parseProvider(e.getAsJsonObject()));
        }

        return items;
    }

    @Override
    public String toString() {
        return Integer.toString(this.rules.size());
    }
}
