package com.robomwm.mehcheat.limiter;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
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
    protected final String configSection;

    private Map<UUID, WarningData> warningLevels = new HashMap<>();

    public Limiter(Plugin plugin, String configSection)
    {
        this.plugin = plugin;
        logger = plugin.getLogger();
        this.configSection = configSection;
        if (getConfig() == null)
            plugin.getConfig().createSection(configSection);
        getConfig().addDefault("maxLevel", 4D);
        getConfig().addDefault("delay", 20000L);
    }

    public void resetWarningLevels(Player player)
    {
        if (player != null)
            warningLevels.remove(player.getUniqueId());
        else
            warningLevels.clear();
    }

    protected ConfigurationSection getConfig()
    {
        return plugin.getConfig().getConfigurationSection(configSection);
    }

    protected void saveConfig()
    {
        plugin.saveConfig();
    }

    protected boolean addWarning(Player player, double distanceSquared, double check)
    {
        WarningData data = warningLevels.getOrDefault(player.getUniqueId(), new WarningData(getConfig().getLong("delay")));
        double value = distanceSquared - check;
        return data.addWarning(value) > getConfig().getDouble("maxLevel");
    }

    protected boolean addWarning(Player player, double distanceSquared, double check, Location location, String violation)
    {
        getLogger().warning(configSection + "." + violation + "=> " + player.getName() + " at " + location + " with " + distanceSquared);
        return addWarning(player, distanceSquared, check);
    }

    protected Logger getLogger()
    {
        return logger;
    }

    protected boolean performCheck(Player player, double distanceSquared, double check, Location location, String violation)
    {
        if (distanceSquared > check)
            return addWarning(player, distanceSquared, check, location, violation);
        return false;
    }
}

class WarningData
{
    private double level;
    private long lastTime;
    private long delay;

    WarningData(long delay)
    {
        this.delay = delay;
        lastTime = System.currentTimeMillis();
    }

    public double addWarning(double level)
    {
        if (lastTime + delay > System.currentTimeMillis())
            this.level += level;
        else
            this.level = level;
        lastTime = System.currentTimeMillis();
        return this.level;
    }
}