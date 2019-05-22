package org.golde.enderpearlfencegatefix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.plugin.java.JavaPlugin;
import org.golde.enderpearlfencegatefix.nms.CustomItemEnderPearl;

import net.minecraft.server.v1_7_R4.Item;

public class Main extends JavaPlugin
{
    public void onEnable() {
        Item i = null;
        try {
            final Method f = Item.class.getDeclaredMethod("f", String.class);
            f.setAccessible(true);
            try {
                i = (Item)f.invoke(new CustomItemEnderPearl().c("enderPearl"), "ender_pearl");
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
            f.setAccessible(false);
        }
        catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        Item.REGISTRY.a(368, "ender_pearl", (Object)i);
    }
}
