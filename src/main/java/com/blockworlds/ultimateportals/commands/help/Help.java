package com.blockworlds.ultimateportals.commands.help;

import com.blockworlds.ultimateportals.commands.MasterCommand;
import com.blockworlds.ultimateportals.commands.UPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Help extends UPCommand {
    ArrayList<String> playerPermissions;
    ArrayList<String> displayedCommands;

    public Help(CommandSender sender, String label, String[] args) {
        super(sender, label, args, null);
        if(sender instanceof Player){
            Player player = (Player)sender;
            getUPPermissions(player);
        }else{
            playerPermissions = new ArrayList<>();
            for(Entry<String,String> i : MasterCommand.getUpCommands().entrySet()){
                playerPermissions.add(i.getKey());
            }
        }
    }

    @Override
    public void executeCommand() {
        this.displayedCommands = getDisplayedCommands();
        if(args.length==0){
            displayPage(1);
        }else if(args.length==1){
            try {
                int pageNum = Integer.parseInt(args[0]);
                displayPage(pageNum);
            }catch(NumberFormatException e){
                sender.sendMessage("\u00A7cPlease provide an actual page number.");
            }
        }else{
            sender.sendMessage("\u00A7cProvided too many arguments!");
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
        playerPermissions = perms;
    }

    public void displayPage(int pageNum){
        int maxPageNum = (int) Math.ceil(((double)displayedCommands.size())/5);
        if(pageNum>maxPageNum){
            sender.sendMessage("\u00A7cThis Exceeds the maximum help page of " + maxPageNum + ".");
            return;
        }
        sender.sendMessage("\u00A76----Ultimate Portals Help (" + pageNum + "/" + maxPageNum + ")----");
        String[] displayed = displayedCommands.toArray(new String[0]);
        displayed = Arrays.copyOfRange(displayed,5*(pageNum-1),5*(pageNum-1)+4);
        for(String i : displayed){
            sender.sendMessage(getDescription(i));
        }
    }

    public static String getDescription(String commandName){
        switch(commandName){
            case "help":
                return "/up help <Page Number>: Used to learn Ultimate Portal's Commands.";
            case "deleteallportals":
                return "/up deleteallportals: An admin command to delete every portal.\n\u00A7cUSE WITH EXTREME CAUTION AS THIS CANNOT BE REVERSED!";
            case "deleteall":
                return "/up deleteall <Identifier>: An admin command to delete every portal with that identifier.";
            case "delete":
                return "/up delete <Identifier> <Number>: An admin command to delete a specific portal.";
            default:
                return "Please tell the admin that " + commandName + " has no Description.";
        }
    }

    public ArrayList<String> getDisplayedCommands(){
        Map<String,String> upCommands = MasterCommand.getUpCommands();
        ArrayList<String> displayedCommands = new ArrayList<>();
        for(Entry<String,String> command : upCommands.entrySet()){
            if(command.getValue()==null || playerPermissions.contains(command.getValue())){
                displayedCommands.add(command.getKey());
            }
        }
        return displayedCommands;
    }
}
