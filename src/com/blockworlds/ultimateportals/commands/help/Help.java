package com.blockworlds.ultimateportals.commands.help;

import com.blockworlds.ultimateportals.commands.UPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.Set;

public class Help extends UPCommand {
    ArrayList<String> playerpermissions;

    public Help(CommandSender sender, String label, String[] args, String requiredPermission) {
        super(sender, label, args, requiredPermission);
        if(sender instanceof Player){
            Player player = (Player)sender;
            getUPPermissions(player);
        }
    }

    @Override
    public void executeCommand() {
        if(args.length==0){
            //TODO
        }
    }

    public void getUPPermissions(Player player){
        Set<PermissionAttachmentInfo> pais = player.getEffectivePermissions();
        ArrayList<String> perms = new ArrayList<>();
        for(PermissionAttachmentInfo pai : pais){
            if(pai.getPermission().toLowerCase().startsWith("ultimateportals.")){
                perms.add(pai.getPermission());
            }
        }
        playerpermissions = perms;
    }
}
