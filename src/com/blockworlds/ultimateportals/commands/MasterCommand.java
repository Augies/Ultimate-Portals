package com.blockworlds.ultimateportals.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MasterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==0){
            sender.sendMessage("Please provide an argument. \nUse \"/ultimateportals help\" for the available commands.");
            return true;
        }
        switch(args[0]){
            case "help":
                //TODO
                break;
            default:
                sender.sendMessage("\u00A7cThat Command was not found. \nUse \"/ultimateportals help\" for the available commands.");
                break;
        }
        return true;
    }
}
