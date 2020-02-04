package com.blockworlds.ultimateportals.commands.admin;

import com.blockworlds.ultimateportals.Portal;
import com.blockworlds.ultimateportals.commands.UPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class DeleteAllPortals extends UPCommand {
    private static final Map<UUID,Boolean> ranCommand = new HashMap<>();

    public DeleteAllPortals(CommandSender sender, String label, String[] args) {
        super(sender, label, args, "ultimateportals.deleteallportals");
    }

    @Override
    public void executeCommand() {
        if(!(sender instanceof Player)){
            for(Portal i : Portal.instances){
                i.unregister(true);
            }
            sender.sendMessage("\u00A7cDeleted Every Portal.");
            return;
        }
        Player player = (Player)sender;
        if(ranCommand.getOrDefault(player.getUniqueId(), false)){
            for(Portal i : Portal.instances){
                i.unregister(true);
            }
            player.sendMessage("\u00A7cDeleted Every Portal.");
            ranCommand.remove(player.getUniqueId());
        }else{
            player.sendMessage("\u00A7cPlease run the command again to confirm that you want to delete EVERY portal on the server");
            ranCommand.put(player.getUniqueId(), true);
        }
    }
}
