package me.realized.tokenmanager.util.compat;

import me.realized.tokenmanager.util.NumberUtil;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class CompatUtil {

    private static final Pattern BUKKIT_VERSION_PATTERN = Pattern.compile("\\b1\\.(\\d{1,2})\\b");

    private static final long SUB_VERSION;
    private static final Method ITEMSTACK_GET_DURABILITY;
    private static final Method ITEMSTACK_SET_DURABILITY;
    private static final Method SKULLMETA_SET_OWNING_PLAYER;
    private static final Method SKULLMETA_GET_OWNER;

    private static final Method ENCHANTMENT_GET_KEY;
    private static final Method NAMESPACEDKEY_GET_KEY;
    private static final Method ITEMMETA_SET_UNBREAKABLE;
    private static final Method ITEMMETA_SET_CUSTOM_MODEL_DATA;
    private static final Method POTIONMETA_SET_BASE_POTION_DATA;

    static {
        SUB_VERSION = detectSubVersion();

        ITEMSTACK_GET_DURABILITY = ReflectionUtil.getMethodUnsafe(ItemStack.class, "getDurability");
        ITEMSTACK_SET_DURABILITY = ReflectionUtil.getMethodUnsafe(ItemStack.class, "setDurability", short.class);
        SKULLMETA_SET_OWNING_PLAYER = ReflectionUtil.getMethodUnsafe(SkullMeta.class, "setOwningPlayer", OfflinePlayer.class);
        SKULLMETA_GET_OWNER = ReflectionUtil.getMethodUnsafe(SkullMeta.class, "getOwner");

        ENCHANTMENT_GET_KEY = ReflectionUtil.getMethodUnsafe(Enchantment.class, "getKey");
        NAMESPACEDKEY_GET_KEY = ReflectionUtil.getMethodUnsafe(
            ReflectionUtil.getClassUnsafe("org.bukkit.NamespacedKey"),
            "getKey"
        );
        ITEMMETA_SET_UNBREAKABLE = ReflectionUtil.getMethodUnsafe(ItemMeta.class, "setUnbreakable", boolean.class);
        ITEMMETA_SET_CUSTOM_MODEL_DATA = ReflectionUtil.getMethodUnsafe(ItemMeta.class, "setCustomModelData", Integer.class);
        POTIONMETA_SET_BASE_POTION_DATA = ReflectionUtil.getMethodUnsafe(
            PotionMeta.class,
            "setBasePotionData",
            ReflectionUtil.getClassUnsafe("org.bukkit.potion.PotionData")
        );
    }

    private static long detectSubVersion() {
        try {
            final String packageName = Bukkit.getServer().getClass().getPackage().getName();
            final String token = packageName.substring(packageName.lastIndexOf('.') + 1);

            if (token.startsWith("v") && token.contains("_")) {
                final String[] parts = token.split("_");
                if (parts.length > 1) {
                    final java.util.OptionalLong parsed = NumberUtil.parseLong(parts[1]);
                    if (parsed.isPresent()) {
                        return parsed.getAsLong();
                    }
                }
            }
        } catch (Exception ignored) {
            // fallback below
        }

        try {
            final String bukkitVersion = Bukkit.getBukkitVersion();
            final Matcher matcher = BUKKIT_VERSION_PATTERN.matcher(bukkitVersion);
            if (matcher.find()) {
                return NumberUtil.parseLong(matcher.group(1)).orElse(0);
            }
        } catch (Exception ignored) {
            return 0;
        }

        return 0;
    }

    private CompatUtil() {}

    public static boolean isPre1_17() {
        return SUB_VERSION < 17;
    }

    public static boolean isPre1_16() {
        return SUB_VERSION < 16;
    }

    public static boolean isPre1_14() {
        return SUB_VERSION < 14;
    }

    public static boolean isPre1_13() {
        return SUB_VERSION < 13;
    }

    public static boolean isPre1_12() {
        return SUB_VERSION < 12;
    }

    public static int getDurability(final ItemStack item) {
        if (item == null) {
            return 0;
        }

        if (ITEMSTACK_GET_DURABILITY != null) {
            try {
                return ((Short) ITEMSTACK_GET_DURABILITY.invoke(item)).intValue();
            } catch (Exception ignored) {
                return 0;
            }
        }

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }

        try {
            final Class<?> damageableClass = Class.forName("org.bukkit.inventory.meta.Damageable");
            if (!damageableClass.isInstance(meta)) {
                return 0;
            }

            final Method getDamage = damageableClass.getMethod("getDamage");
            return (int) getDamage.invoke(meta);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static void setDurability(final ItemStack item, final int durability) {
        if (item == null) {
            return;
        }

        if (ITEMSTACK_SET_DURABILITY != null) {
            try {
                ITEMSTACK_SET_DURABILITY.invoke(item, (short) durability);
            } catch (Exception ignored) {
                return;
            }
        }

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        try {
            final Class<?> damageableClass = Class.forName("org.bukkit.inventory.meta.Damageable");
            if (!damageableClass.isInstance(meta)) {
                return;
            }

            final Method setDamage = damageableClass.getMethod("setDamage", int.class);
            setDamage.invoke(meta, durability);
            item.setItemMeta(meta);
        } catch (Exception ignored) {
            return;
        }
    }

    public static void setSkullOwner(final SkullMeta meta, final OfflinePlayer owner, final String ownerName) {
        if (meta == null) {
            return;
        }

        if (SKULLMETA_SET_OWNING_PLAYER != null && owner != null) {
            try {
                SKULLMETA_SET_OWNING_PLAYER.invoke(meta, owner);
                return;
            } catch (Exception ignored) {
                // fallback
            }
        }

        if (ownerName != null && !ownerName.isEmpty()) {
            meta.setOwner(ownerName);
        }
    }

    public static String getSkullOwnerName(final SkullMeta meta) {
        if (meta == null) {
            return null;
        }

        if (SKULLMETA_GET_OWNER != null) {
            try {
                return (String) SKULLMETA_GET_OWNER.invoke(meta);
            } catch (Exception ignored) {
                return null;
            }
        }

        return null;
    }

    public static String getEnchantmentKey(final Enchantment enchantment) {
        if (enchantment == null || ENCHANTMENT_GET_KEY == null || NAMESPACEDKEY_GET_KEY == null) {
            return null;
        }

        try {
            final Object namespacedKey = ENCHANTMENT_GET_KEY.invoke(enchantment);
            if (namespacedKey == null) {
                return null;
            }

            return (String) NAMESPACEDKEY_GET_KEY.invoke(namespacedKey);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static boolean setUnbreakable(final ItemMeta meta, final boolean unbreakable) {
        if (meta == null || ITEMMETA_SET_UNBREAKABLE == null) {
            return false;
        }

        try {
            ITEMMETA_SET_UNBREAKABLE.invoke(meta, unbreakable);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean setCustomModelData(final ItemMeta meta, final int customModelData) {
        if (meta == null || ITEMMETA_SET_CUSTOM_MODEL_DATA == null) {
            return false;
        }

        try {
            ITEMMETA_SET_CUSTOM_MODEL_DATA.invoke(meta, customModelData);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean setBasePotionData(
        final PotionMeta meta,
        final String potionTypeName,
        final boolean extended,
        final boolean upgraded
    ) {
        if (meta == null || POTIONMETA_SET_BASE_POTION_DATA == null) {
            return false;
        }

        try {
            final Class<?> potionTypeClass = Class.forName("org.bukkit.potion.PotionType");
            final Object potionType = Enum.valueOf((Class<? extends Enum>) potionTypeClass, potionTypeName);
            final Class<?> potionDataClass = Class.forName("org.bukkit.potion.PotionData");
            final Object potionData = potionDataClass
                .getConstructor(potionTypeClass, boolean.class, boolean.class)
                .newInstance(potionType, extended, upgraded);
            POTIONMETA_SET_BASE_POTION_DATA.invoke(meta, potionData);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isPre1_9() {
        return SUB_VERSION < 9;
    }
}
