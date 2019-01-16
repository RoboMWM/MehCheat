package com.robomwm.mehcheat.limiter;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created on 1/14/2019.
 *
 * Holds data
 *
 * @author RoboMWM
 */
public abstract class Limiter
{
    private Logger logger;

    private Plugin plugin;

    private Map<UUID, Integer> warningLevels = new HashMap<>();

    public Limiter(Plugin plugin)
    {
        this.plugin = plugin;
        logger = plugin.getLogger();
    }

    protected FileConfiguration getConfig()
    {
        return plugin.getConfig();
    }

    protected void saveConfig()
    {
        plugin.saveConfig();
    }

    protected int addWarning(Player player)
    {
        int previousLevel = warningLevels.getOrDefault(player.getUniqueId(), 0);
        warningLevels.put(player.getUniqueId(), ++previousLevel);
        return previousLevel;
    }

    protected int addWarning(Player player, Location location, double distance, String violation)
    {
        getLogger().warning(violation + "=> " + player.getName() + " at " + location + " with " + distance);
        return addWarning(player);
    }

    protected Logger getLogger()
    {
        return logger;
    }
}
