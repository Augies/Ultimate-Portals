package com.blockworlds.ultimateportals.commands.admin;

import com.blockworlds.ultimateportals.Portal;
import com.blockworlds.ultimateportals.commands.UPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeleteAll extends UPCommand {
    private static final Map<UUID,Boolean> ranCommand = new HashMap<>();

    public DeleteAll(CommandSender sender, String label, String[] args) {
        super(sender, label, args, "ultimateportals.deleteall");
    }

    @Override
    public void executeCommand() {
        if(args.length<1){
            sender.sendMessage("\u00A7cPlease provide an identifier for deletion.");
            return;
        }
        if(!(sender instanceof Player)){
            for(Portal i : Portal.instances){
                if(i.getIdentifier().equals(args[0])) {
                    i.unregister(true);
                }
            }
            return;
        }
        Player player = (Player)sender;
        if(ranCommand.getOrDefault(player.getUniqueId(), false)){
            for(Portal i : Portal.instances){
                if(i.getIdentifier().equals(args[0])) {
                    i.unregister(true);
                }
            }
            player.sendMessage("\u00A7cDeleted Every Portal Named " + args[0] + ".");
            ranCommand.remove(player.getUniqueId());
        }else{
            player.sendMessage("\u00A7cPlease run the command again to confirm that you want to delete EVERY portal on the server");
            ranCommand.put(player.getUniqueId(), true);
        }
    }
}
