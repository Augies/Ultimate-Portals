package com.blockworlds.ultimateportals.commands.admin;

import com.blockworlds.ultimateportals.Portal;
import com.blockworlds.ultimateportals.commands.UPCommand;
import org.bukkit.command.CommandSender;

import static com.blockworlds.ultimateportals.Portal.getPortal;

public class Delete extends UPCommand {
    public Delete(CommandSender sender, String label, String[] args) {
        super(sender, label, args, "ultimateportals.delete");
    }

    @Override
    public void executeCommand() {
        if(args.length < 2){
            sender.sendMessage("\u00A7cPlease provide an identifier and instance number.");
            return;
        }
        String identifier = args[0];
        int instanceNum;
        try{
            instanceNum = Integer.parseInt(args[1]);
        }catch(NumberFormatException e){
            sender.sendMessage("\u00A7cPlease provide a valid instance number.");
            return;
        }
        Portal portal = getPortal(identifier, instanceNum);
        if(portal==null){
            sender.sendMessage("\u00A7cThat is not a valid portal.");
        }else {
            portal.unregister(true);
            sender.sendMessage("\u00A7cDeleted the portal.");
        }
    }
}
