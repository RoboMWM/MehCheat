package com.robomwm.mehcheat;

import com.robomwm.mehcheat.limiter.HorizontalMovingLimit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 1/14/2019.
 *
 * @author RoboMWM
 */
public class MehCheat extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        getConfig().options().copyDefaults(true);
        new HorizontalMovingLimit(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!sender.isOp())
            return true;
        reloadConfig();
        sender.sendMessage("Reloaded config");
        return true;
    }
}
