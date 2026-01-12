package me.realized.tokenmanager.util.compat;

import me.realized.tokenmanager.util.NumberUtil;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionUtil {
    private static final String PACKAGE_VERSION;
    private static final int MAJOR_VERSION;

    static {
        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        final String token = packageName.substring(packageName.lastIndexOf('.') + 1);

        if (token.startsWith("v") && token.contains("_")) {
            PACKAGE_VERSION = token;

            final String[] parts = token.split("_");
            if (parts.length > 1) {
                MAJOR_VERSION = (int) NumberUtil.parseLong(parts[1]).orElse(0);
            } else {
                MAJOR_VERSION = 0;
            }
        } else {
            PACKAGE_VERSION = "";
            MAJOR_VERSION = detectMajorFromBukkitVersion();
        }
    }

    private ReflectionUtil() {
    }

    private static int detectMajorFromBukkitVersion() {
        try {
            final String bukkitVersion = Bukkit.getBukkitVersion();
            final int idx = bukkitVersion.indexOf("1.");
            if (idx == -1) {
                return 0;
            }

            final int start = idx + 2;
            int end = start;
            while (end < bukkitVersion.length()
                    && Character.isDigit(bukkitVersion.charAt(end))) {
                end++;
            }

            if (end == start) {
                return 0;
            }

            return (int) NumberUtil.parseLong(bukkitVersion.substring(start, end))
                    .orElse(0);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static int getMajorVersion() {
        return MAJOR_VERSION;
    }

    public static Class<?> getClassUnsafe(final String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    public static Method getMethodUnsafe(final Class<?> clazz, final String name, final Class<?>... parameters) {
        if (clazz == null) {
            return null;
        }

        try {
            return clazz.getMethod(name, parameters);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static Class<?> getNMSClass(final String name, final boolean logError) {
        try {
            if (getMajorVersion() < 17 && !PACKAGE_VERSION.isEmpty()) {
                return Class.forName(
                        "net.minecraft.server." + PACKAGE_VERSION + "." + name
                );
            }

            return Class.forName("net.minecraft." + name);
        } catch (ClassNotFoundException ex) {
            if (logError) {
                ex.printStackTrace();
            }

            return null;
        }
    }

    public static Class<?> getNMSClass(final String name) {
        return getNMSClass(name, true);
    }

    public static Class<?> getCBClass(final String path, final boolean logError) {
        try {
            final String base = "org.bukkit.craftbukkit";
            final String version = PACKAGE_VERSION.isEmpty() ? "" : ("." + PACKAGE_VERSION);
            return Class.forName(base + version + "." + path);
        } catch (ClassNotFoundException ex) {
            if (logError) {
                ex.printStackTrace();
            }

            return null;
        }
    }

    public static Class<?> getCBClass(final String path) {
        return getCBClass(path, true);
    }

    public static Class<?> getALClass(final String name) {
        try {
            return Class.forName("com.mojang.authlib." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(final Class<?> clazz, final String name, final Class<?>... parameters) {
        try {
            return clazz.getMethod(name, parameters);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Method findDeclaredMethod(final Class<?> clazz, final String name, final Class<?>... parameters) throws NoSuchMethodException {
        final Method method = clazz.getDeclaredMethod(name, parameters);
        method.setAccessible(true);
        return method;
    }

    public static Method getDeclaredMethod(final Class<?> clazz, final String name, final Class<?>... parameters) {
        try {
            return findDeclaredMethod(clazz, name, parameters);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Method getDeclaredMethodUnsafe(final Class<?> clazz, final String name, final Class<?>... parameters) {
        try {
            return findDeclaredMethod(clazz, name, parameters);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static Field getField(final Class<?> clazz, final String name) {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Field getDeclaredField(final Class<?> clazz, final String name) {
        try {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... parameters) {
        try {
            return clazz.getConstructor(parameters);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
