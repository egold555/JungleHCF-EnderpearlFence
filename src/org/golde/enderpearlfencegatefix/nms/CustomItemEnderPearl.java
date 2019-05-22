package org.golde.enderpearlfencegatefix.nms;

import net.minecraft.server.v1_7_R4.*;

public class CustomItemEnderPearl extends ItemEnderPearl
{
    public ItemStack a(final ItemStack item, final World world, final EntityHuman entity) {
        if (entity.abilities.canInstantlyBuild) {
            return item;
        }
        --item.count;
        world.makeSound((Entity)entity, "random.bow", 0.5f, 0.4f / (CustomItemEnderPearl.g.nextFloat() * 0.4f + 0.8f));
        if (!world.isStatic) {
            world.addEntity((Entity)new CustomEntityEnderPearl(world, (EntityLiving)entity));
        }
        return item;
    }
}
