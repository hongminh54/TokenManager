package me.realized.tokenmanager.util.compat;

import me.realized.tokenmanager.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class Items {

    private static final String PANE = "STAINED_GLASS_PANE";
    private static final String RED_PANE_13 = "RED_STAINED_GLASS_PANE";
    private static final String GRAY_PANE_13 = "GRAY_STAINED_GLASS_PANE";
    private static final String GREEN_PANE_13 = "GREEN_STAINED_GLASS_PANE";
    private static final String PLAYER_HEAD_13 = "PLAYER_HEAD";

    public static final ItemStack RED_PANE;
    public static final ItemStack GRAY_PANE;
    public static final ItemStack GREEN_PANE;

    public static final ItemStack HEAD;

    static {
        RED_PANE = CompatUtil.isPre1_13()
            ? ItemBuilder.of(PANE, 1, (short) 14).name(" ").build()
            : ItemBuilder.of(resolveMaterial(RED_PANE_13)).name(" ").build();
        GRAY_PANE = CompatUtil.isPre1_13()
            ? ItemBuilder.of(PANE, 1, (short) 7).name(" ").build()
            : ItemBuilder.of(resolveMaterial(GRAY_PANE_13)).name(" ").build();
        GREEN_PANE = CompatUtil.isPre1_13()
            ? ItemBuilder.of(PANE, 1, (short) 13).name(" ").build()
            : ItemBuilder.of(resolveMaterial(GREEN_PANE_13)).name(" ").build();
        HEAD = CompatUtil.isPre1_13()
            ? ItemBuilder.of("SKULL_ITEM", 1, (short) 3).build()
            : ItemBuilder.of(resolveMaterial(PLAYER_HEAD_13)).build();
    }

    private static Material resolveMaterial(final String name) {
        Material material = Material.matchMaterial(name);
        if (material == null) {
            material = Material.getMaterial(name);
        }

        return material != null ? material : Material.AIR;
    }

    public static boolean equals(final ItemStack item, final ItemStack other) {
        return item.getType() == other.getType()
            && CompatUtil.getDurability(item) == CompatUtil.getDurability(other);
    }

    private Items() {}
}
