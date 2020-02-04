package com.blockworlds.ultimateportals.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class UPCommand {
    protected final String requiredPermission;
    protected final CommandSender sender;
    protected final String label;
    protected final String[] args;

    public UPCommand(CommandSender sender, String label, String[] args, String requiredPermission){
        this.sender = sender;
        this.label = label;
        this.args = args;
        this.requiredPermission = requiredPermission;
    }

    public boolean tryCommand(){
        if(requiredPermission == null){
            executeCommand();
            return true;
        }
        if(!(sender instanceof Player) || sender.hasPermission(requiredPermission)){
            executeCommand();
            return true;
        }
        return false;
    }

    public abstract void executeCommand();
}
