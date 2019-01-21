package com.robomwm.mehcheat;

import com.robomwm.mehcheat.limiter.HorizontalMovingLimit;
import com.robomwm.mehcheat.limiter.Limiter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created on 1/14/2019.
 *
 * @author RoboMWM
 */
public class MehCheat extends JavaPlugin
{
    private List<Limiter> limiters = new ArrayList<>(2);

    @Override
    public void onEnable()
    {
        getConfig().options().copyDefaults(true);
        limiters.add(new HorizontalMovingLimit(this));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!sender.isOp())
            return true;
        reloadConfig();
        for (Limiter limiter : limiters)
            limiter.resetWarningLevels(null);
        sender.sendMessage("Reloaded config");
        return true;
    }
}
