package com.robomwm.mehcheat.limiter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

/**
 * Created on 1/14/2019.
 *
 * horizontal moving is far more important than vertical movement due to causing the server to load chunks
 *
 * @author RoboMWM
 */
public class HorizontalMovingLimit extends Limiter implements Listener
{
    public HorizontalMovingLimit(Plugin plugin)
    {
        super(plugin);
        getConfig().addDefault("horizontal.cancel", false);
        getConfig().addDefault("horizontal.normal", 0.5D);
        getConfig().addDefault("horizontal.glide", 4.1D);
        getConfig().addDefault("horizontal.swim", .08D);
        saveConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        //getConfig().set("horizontal.fly", 1.3D);
    }

    @EventHandler(ignoreCancelled = true)
    private void onMove(PlayerMoveEvent event)
    {
        Location from = event.getFrom().clone();
        from.setY(event.getTo().getY());

        double distanceSquared;

        try
        {
            distanceSquared = event.getFrom().distanceSquared(event.getTo());
        }
        catch (IllegalArgumentException e)
        {
            getLogger().info("moved across worlds");
            return;
        }

        from.setY(event.getFrom().getY());

        boolean bad = bad(event, distanceSquared, from);

        if (getConfig().getBoolean("horizontal.cancel"))
            event.setCancelled(bad);
    }

    private boolean bad(PlayerMoveEvent event, double distanceSquared, Location from)
    {
        Player player = event.getPlayer();
        if (player.isGliding())
            return handleGlide(event, distanceSquared);
        else if (player.isSwimming())
            return handleSwim(event, distanceSquared);
        else
            return handleNormalMove(event, distanceSquared, from);
    }

    private boolean handleNormalMove(PlayerMoveEvent event, double distanceSquared, Location from)
    {
        double check = getConfig().getDouble("horizontal.normal");
        if (event.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
            check += check * (0.2D * event.getPlayer().getPotionEffect(PotionEffectType.SPEED).getAmplifier());

        if (distanceSquared > check)
        {
            //check for ice
            from.setY(-1);
            if (event.getPlayer().isSprinting() && from.getBlock().getType() == Material.ICE && distanceSquared < check + 1D)
                return false;
            addWarning(event.getPlayer(), event.getFrom(), distanceSquared, "normal," + from.getBlock().getType());
            return true;
        }
        return false;
    }

    private boolean handleGlide(PlayerMoveEvent event, double distanceSquared)
    {
        if (distanceSquared > getConfig().getDouble("horizontal.glide"))
        {
            addWarning(event.getPlayer(), event.getFrom(), distanceSquared, "gliding");
            return true;
        }
        return false;
    }

    private boolean handleSwim(PlayerMoveEvent event, double distanceSquared)
    {
        if (distanceSquared > getConfig().getDouble("horizontal.swim"))
        {
            addWarning(event.getPlayer(), event.getFrom(), distanceSquared, "swimming");
            return true;
        }
        return false;
    }

    private void handleFly(PlayerMoveEvent event, double distanceSquared)
    {
        if (distanceSquared > 1.3)
            addWarning(event.getPlayer(), event.getFrom(), distanceSquared, "flying");
    }
}
