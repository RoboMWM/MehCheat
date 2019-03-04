package com.robomwm.mehcheat.limiter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 1/14/2019.
 *
 * horizontal moving is far more important than vertical movement due to causing the server to load chunks
 *
 * @author RoboMWM
 */
public class HorizontalMovingLimit extends Limiter implements Listener
{
    private Map<Player, Long> lastSprint = new HashMap<>();

    public HorizontalMovingLimit(Plugin plugin)
    {
        super(plugin, "horizontal");
        getConfig().addDefault("cancel", false);
        getConfig().addDefault("walk", .085D);
        getConfig().addDefault("sprint", 0.38D);
        getConfig().addDefault("glide", 4.1D);
        getConfig().addDefault("swim", .08D); //0.08
        saveConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        //getConfig().set("horizontal.fly", 1.3D);
    }

    @EventHandler(ignoreCancelled = true)
    private void onQuit(PlayerQuitEvent event)
    {
        resetWarningLevels(event.getPlayer());
        lastSprint.remove(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    private void onMove(PlayerMoveEvent event)
    {
        Location from = event.getFrom().clone();
        from.setY(event.getTo().getY());

        double distanceSquared;

        try
        {
            distanceSquared = from.distanceSquared(event.getTo());
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

        if (event.getPlayer().isSprinting() || event.getPlayer().isSwimming()) //surfacing has a weird velocity bump
            lastSprint.put(event.getPlayer(), System.currentTimeMillis());
    }

    private boolean bad(PlayerMoveEvent event, double distanceSquared, Location from)
    {
        Player player = event.getPlayer();
        if (player.isGliding())
            return handleGlide(event, distanceSquared);
        else if (player.isSwimming())
            return handleSwim(event, distanceSquared);
        else if (player.isSprinting() || recentlySprinted(player))
            return handleSprint(event, distanceSquared, from);
        else
            return handleWalk(event, distanceSquared);
    }

    private boolean recentlySprinted(Player player)
    {
        Long sprint = lastSprint.get(player);
        return sprint != null && sprint >= System.currentTimeMillis() - 2500L;
    }

    private boolean handleWalk(PlayerMoveEvent event, double distanceSquared)
    {
        String checkString = "walk";
        double check = getConfig().getDouble(checkString);
        if (event.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
            check += check * (0.2D * event.getPlayer().getPotionEffect(PotionEffectType.SPEED).getAmplifier());

        return performCheck(event.getPlayer(), distanceSquared, check, event.getFrom(), checkString);
    }

    private boolean handleSprint(PlayerMoveEvent event, double distanceSquared, Location from)
    {
        String checkString = "sprint";
        double check = getConfig().getDouble(checkString);
        if (event.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
            check += check * (0.2D * event.getPlayer().getPotionEffect(PotionEffectType.SPEED).getAmplifier());

        if (distanceSquared > check)
        {
            //check for ice
            from.add(0, -1, 0);
            switch (from.getBlock().getType())
            {
                case ICE:
                case PACKED_ICE:
                    return addWarning(event.getPlayer(), distanceSquared, check + 1D, event.getFrom(), checkString + from.getBlock().getType());
            }
            return addWarning(event.getPlayer(), distanceSquared, check, event.getFrom(), checkString + from.getBlock().getType());
        }
        return false;
    }

    private boolean handleGlide(PlayerMoveEvent event, double distanceSquared)
    {
        String checkString = "glide";
        double check = getConfig().getDouble(checkString);
        return performCheck(event.getPlayer(), distanceSquared, check, event.getFrom(), checkString);
    }

    private boolean handleSwim(PlayerMoveEvent event, double distanceSquared)
    {
        String checkString = "swim";
        double check = getConfig().getDouble(checkString);
        return performCheck(event.getPlayer(), distanceSquared, check, event.getFrom(), checkString);
    }

//    private void handleFly(PlayerMoveEvent event, double distanceSquared)
//    {
//        if (distanceSquared > 1.3)
//            addWarning(event.getPlayer(), distanceSquared, check, event.getFrom(), "flying");
//    }
}
