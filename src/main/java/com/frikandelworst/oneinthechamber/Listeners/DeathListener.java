package com.frikandelworst.oneinthechamber.Listeners;

import com.frikandelworst.oneinthechamber.OneIntheChamber;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.IOException;

public class DeathListener implements Listener {
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        Player killer = p.getKiller();
        if(killer == null){
            return;
        }
        p.getInventory().clear();
        String dataSaveMethod = OneIntheChamber.getPlugin().getConfig().getString("DataSaveMethod");
        var dataFile = OneIntheChamber.getPlugin().getCustomConfig();
        if(dataSaveMethod.equals("YML")){
            int kills = dataFile.getInt("players." + killer.getUniqueId() + ".kills");
            int deaths = dataFile.getInt("players." + p.getUniqueId() + ".deaths");
            dataFile.set("players." + killer.getUniqueId() + ".kills", kills + 1);
            dataFile.set("players." + p.getUniqueId() + ".deaths", deaths + 1);
            try{
                dataFile.save(OneIntheChamber.getPlugin().getCustomConfigFile());
            }catch (IOException ex){
                Bukkit.getLogger().warning("Failed to update kills and deaths.");
            }
        }else if(dataSaveMethod.equals("SQL")){
            OneIntheChamber.getPlugin().getDatabase().updateKills(killer, 1);
            OneIntheChamber.getPlugin().getDatabase().updateDeaths(p, 1);
        }else{
            Bukkit.getLogger().warning("Kon de death & kill counters niet updaten.");
        }
        OneIntheChamber.getPlugin().getKdBoard().createBoard(p);
        OneIntheChamber.getPlugin().getKdBoard().createBoard(killer);
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        OneIntheChamber.getPlugin().givePlayerKit(p);
    }
}
