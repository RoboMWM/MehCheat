package com.robomwm.mehcheat.heuristic;

import com.robomwm.mehcheat.UsefulUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created on 1/21/2019.
 *
 * @author RoboMWM
 */
public class Heuristic
{
    private File file;
    private Set<UUID> trustees;
    private Set<UUID> notorieties;

    public Heuristic(Plugin plugin)
    {
        file = new File(plugin.getDataFolder() + File.separator + "exemplaries.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        trustees = convertUUIDsFromString(yaml.getStringList("trustees"));
        notorieties = convertUUIDsFromString(yaml.getStringList("notorieties"));
    }

    public void addTrustee(UUID uuid)
    {
        if (trustees.add(uuid))
            save();
    }

    public void removeTrustee(UUID uuid)
    {
        if (trustees.remove(uuid))
            save();
    }

    public void addNotoriety(UUID uuid)
    {
        if (notorieties.add(uuid))
            save();
    }

    public void removeNotoriety(UUID uuid)
    {
        if (notorieties.remove(uuid))
            save();
    }

    private Set<UUID> convertUUIDsFromString(List<String> uuidStrings)
    {
        Set<UUID> uuids = new HashSet<>(uuidStrings.size());
        for (String uuidString : uuidStrings)
            uuids.add(UUID.fromString(uuidString));
        return uuids;
    }

    private List<String> convertUUIDsToString(Set<UUID> uuids)
    {
        List<String> uuidStrings = new ArrayList<>(uuids.size());
        for (UUID uuid : uuids)
            uuidStrings.add(uuid.toString());
        return uuidStrings;
    }

    private void save()
    {
        List<String> trusteesList = convertUUIDsToString(trustees);
        List<String> notorietiesList = convertUUIDsToString(notorieties);
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("trustees", trusteesList);
        yaml.set("notorieties", notorietiesList);
        UsefulUtil.saveStringToFile(file, yaml.saveToString());
    }
}
