package com.blockworlds.ultimateportals.commands.admin;

import com.blockworlds.ultimateportals.Portal;
import com.blockworlds.ultimateportals.commands.UPCommand;
import org.bukkit.command.CommandSender;

import static com.blockworlds.ultimateportals.PortalHandler.getPortal;

public class Delete extends UPCommand {
	
	public Delete(CommandSender sender, String label, String[] args) {
		super(sender, label, args, "ultimateportals.delete");
	}
	
	@Override
	public void executeCommand() {
		if(this.args.length < 2){
			this.sender.sendMessage("\u00A7cPlease provide an identifier and instance number.");
			return;
		}
		String identifier = this.args[0];
		int instanceNum;
		try{
			instanceNum = Integer.parseInt(this.args[1]);
		}catch(NumberFormatException e){
			this.sender.sendMessage("\u00A7cPlease provide a valid instance number.");
			return;
		}
		Portal portal = getPortal(identifier, instanceNum);
		if(portal==null){
			this.sender.sendMessage("\u00A7cThat is not a valid portal.");
		}else {
			portal.unregister(true);
			this.sender.sendMessage("\u00A7cDeleted the portal.");
		}
	}
	
}
