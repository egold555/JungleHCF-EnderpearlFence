package org.golde.enderpearlfencegatefix.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.event.CraftEventFactory;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EnderpearlLandEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Gate;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityEnderPearl;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import net.minecraft.server.v1_7_R4.World;

public class CustomEntityEnderPearl extends EntityEnderPearl
{
	public CustomEntityEnderPearl(final World world, final EntityLiving entity) {
		super(world, entity);
	}

	protected void a(final MovingObjectPosition movingobjectposition) {
		final Block block = this.world.getType(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d);
		if (block == Blocks.TRIPWIRE || block == Blocks.WEB) {
			return;
		}
		if (block == Blocks.FENCE_GATE) {
			BlockIterator b = null;
			final Vector vectorA = new Vector(this.locX, this.locY, this.locZ);
			final Vector vectorB = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
			final Vector vectorC = new Vector(vectorB.getX() - vectorA.getX(), vectorB.getY() - vectorA.getY(), vectorB.getZ() - vectorA.getZ()).normalize();
			b = new BlockIterator((org.bukkit.World)this.world.getWorld(), vectorA, vectorC, 0.0, 1);
			if (b != null) {
				boolean open = true;
				while (b.hasNext()) {
					final org.bukkit.block.Block blockB = b.next();
					if (blockB.getState().getData() instanceof Gate && !((Gate)blockB.getState().getData()).isOpen()) {
						open = false;
						break;
					}
				}
				if (open) {
					return;
				}
			}
		}
		if (movingobjectposition.entity != null) {
			movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), 0.0f);
		}
		for (int i = 0; i < 32; ++i) {
			this.world.addParticle("portal", this.locX, this.locY + this.random.nextDouble() * 2.0, this.locZ, this.random.nextGaussian(), 0.0, this.random.nextGaussian());
		}
		if (!this.world.isStatic) {
			final EntityLiving shooter = this.getShooter();
			if (shooter != null && shooter instanceof EntityPlayer) {
				final EntityPlayer entityPlayer = (EntityPlayer)this.getShooter();
				if (entityPlayer.playerConnection.b().isConnected() && entityPlayer.world == this.world) {
					final CraftPlayer craftPlayer = entityPlayer.getBukkitEntity();
					final EnderpearlLandEvent.Reason reason = (movingobjectposition.entity != null) ? EnderpearlLandEvent.Reason.ENTITY : EnderpearlLandEvent.Reason.BLOCK;
					final CraftEntity bukkitHitEntity = (movingobjectposition.entity != null) ? movingobjectposition.entity.getBukkitEntity() : null;
					final EnderpearlLandEvent landEvent = new EnderpearlLandEvent((EnderPearl)this.getBukkitEntity(), reason, bukkitHitEntity);
                    Bukkit.getPluginManager().callEvent(landEvent);
                    if (landEvent.isCancelled()) {
                        this.die();
                        return;
                    }
					final Location locationA = this.getBukkitEntity().getLocation().clone();
					final Location locationB = craftPlayer.getLocation();
					locationA.setPitch(locationB.getPitch());
					locationA.setYaw(locationB.getYaw());
					final PlayerTeleportEvent event = new PlayerTeleportEvent((Player)craftPlayer, locationB, locationA, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
					Bukkit.getPluginManager().callEvent((Event)event);
					if (!event.isCancelled() && !entityPlayer.playerConnection.isDisconnected()) {
						if (shooter.am()) {
							shooter.mount((Entity)null);
						}
						entityPlayer.playerConnection.teleport(event.getTo());
						shooter.fallDistance = 0.0f;
						CraftEventFactory.entityDamage = (Entity)this;
						shooter.damageEntity(DamageSource.FALL, 5.0f);
						CraftEventFactory.entityDamage = null;
					}
				}
			}
			this.die();
		}
	}
}
