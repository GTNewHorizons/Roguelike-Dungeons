package greymerk.roguelike.treasure.loot;

import java.util.Random;

public enum Slot {

    WEAPON,
    HEAD,
    CHEST,
    LEGS,
    FEET;

    public static Slot getSlotByNumber(int num) {

        switch (num) {
            case 0:
                return WEAPON;
            case 1:
                return HEAD;
            case 2:
                return CHEST;
            case 3:
                return LEGS;
            case 4:
                return FEET;
            default:
                return null;
        }

    }

    public static Slot getRandomArmourSlot(Random rand) {
        return getSlotByNumber(1 + rand.nextInt(4));
    }
}
