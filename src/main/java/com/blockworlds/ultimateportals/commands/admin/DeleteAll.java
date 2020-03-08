package com.blockworlds.ultimateportals.commands.admin;

import com.blockworlds.ultimateportals.Portal;
import com.blockworlds.ultimateportals.PortalHandler;
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
		if(this.args.length<1){
			this.sender.sendMessage("\u00A7cPlease provide an identifier for deletion.");
			return;
		}
		if(!(this.sender instanceof Player)) {
			for(Portal i : PortalHandler.instances){
				if(i.getIdentifier().equals(this.args[0])) {
					i.unregister(true);
				}
			}
			return;
		}
		Player player = (Player)this.sender;
		if(ranCommand.getOrDefault(player.getUniqueId(), Boolean.FALSE).booleanValue()){
			for(Portal i : PortalHandler.instances) {
				if(i.getIdentifier().equals(this.args[0])) {
					i.unregister(true);
				}
			}
			player.sendMessage("\u00A7cDeleted Every Portal Named " + this.args[0] + ".");
			ranCommand.remove(player.getUniqueId());
		} else {
			player.sendMessage("\u00A7cPlease run the command again to confirm that you want to delete EVERY portal on the server");
			ranCommand.put(player.getUniqueId(), Boolean.TRUE);
		}
	}
}
