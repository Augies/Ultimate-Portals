package com.blockworlds.ultimateportals.commands;

import com.blockworlds.ultimateportals.commands.admin.Delete;
import com.blockworlds.ultimateportals.commands.admin.DeleteAll;
import com.blockworlds.ultimateportals.commands.admin.DeleteAllPortals;
import com.blockworlds.ultimateportals.commands.help.Help;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MasterCommand implements CommandExecutor {
    private static final Map<String,String> upCommands = new HashMap<>();

    public MasterCommand(){
        if(upCommands.size()==0) {
            upCommands.put("deleteallportals", "ultimateportals.deleteallportals");
            upCommands.put("help", null);
            upCommands.put("deleteall", "ultimateportals.deleteall");
            upCommands.put("delete", "ultimateportals.delete");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length==0){
            sender.sendMessage("Please provide an argument. \nUse \"/ultimateportals help\" for the available commands.");
            return true;
        }
        switch(args[0]){
            case "help":
                Help help = new Help(sender, label, Arrays.copyOfRange(args, 1, args.length));
                help.tryCommand();
                break;
            case "deleteallportals":
                DeleteAllPortals deleteAllPortals = new DeleteAllPortals(sender, label, args);
                deleteAllPortals.tryCommand();
                break;
            case "deleteall":
                DeleteAll deleteAll = new DeleteAll(sender, label, Arrays.copyOfRange(args, 1, args.length));
                deleteAll.tryCommand();
                break;
            case "delete":
                Delete delete = new Delete(sender, label, Arrays.copyOfRange(args, 1, args.length));
                delete.tryCommand();
                break;
            default:
                sender.sendMessage("\u00A7cThat Command was not found. \nUse \"/ultimateportals help\" for the available commands.");
                break;
        }
        return true;
    }

    public static Map<String,String> getUpCommands(){
        return upCommands;
    }


}
