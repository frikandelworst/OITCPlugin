package com.frikandelworst.oneinthechamber.Listeners;

import com.frikandelworst.oneinthechamber.OneIntheChamber;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.io.IOException;


public class JoinListener implements Listener {
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) throws IOException {
        Player player = e.getPlayer();
        OneIntheChamber.getPlugin().givePlayerKit(player);
        var dataSaveMethod = OneIntheChamber.getPlugin().getDataSaveMethod();
        if(player.hasPlayedBefore()){
            OneIntheChamber.getPlugin().getKdBoard().createBoard(player);
            Bukkit.getLogger().info("Made the scoreboard.");
           return;
       }


        var dataFile = OneIntheChamber.getPlugin().getCustomConfig();
        if(dataSaveMethod.equals("YML")){
            dataFile.set("players." + player.getUniqueId() + ".kills", 0);
            dataFile.set("players." + player.getUniqueId() + ".deaths", 0);
            dataFile.save(OneIntheChamber.getPlugin().getCustomConfigFile());
        }else if(dataSaveMethod.equals("SQL")){
            OneIntheChamber.getPlugin().getDatabase().initializePlayerInDatabase(player);
        }else{
            Bukkit.getLogger().severe("Ongeldige data save methode in de config!" + "    " + dataSaveMethod);
        }

        OneIntheChamber.getPlugin().getKdBoard().createBoard(player);
        Bukkit.getLogger().info("Made the scoreboard.");
    }
}
