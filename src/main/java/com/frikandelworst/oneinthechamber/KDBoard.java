package com.frikandelworst.oneinthechamber;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class KDBoard {
    private String killsAmount;
    private String deathsAmount;

    public void createBoard(Player player){
        var dataFile = OneIntheChamber.getPlugin().getCustomConfig();
        var dataSaveMethod = OneIntheChamber.getPlugin().getDataSaveMethod();
        if(dataSaveMethod.equals("YML")){
            setKillsAmount(String.valueOf(dataFile.getInt("players." + player.getUniqueId() + ".kills")));
            setDeathsAmount(String.valueOf(dataFile.getInt("players." + player.getUniqueId() + ".deaths")));
        }else if(dataSaveMethod.equals("SQL")){
            setKillsAmount(String.valueOf(OneIntheChamber.getPlugin().getDatabase().getKills(player)));
            setDeathsAmount(String.valueOf(OneIntheChamber.getPlugin().getDatabase().getDeaths(player)));
        }else{
            Bukkit.getLogger().severe("Ongeldige data save methode in de config!" + "    " + dataSaveMethod);
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("oitcBoard","dummy", ChatColor.translateAlternateColorCodes('&', "&a&lOneInTheChamber"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score killsText = obj.getScore(ChatColor.GREEN + "Kills:");
        killsText.setScore(7);
        Score kills = obj.getScore(ChatColor.WHITE + getKillsAmount());
        kills.setScore(6);
        Score deathsText = obj.getScore(ChatColor.RED + "Deaths:");
        deathsText.setScore(5);
        Score deaths = obj.getScore(ChatColor.RED + getDeathsAmount());
        deaths.setScore(4);
        if(Integer.parseInt(deathsAmount) > 1){
            Score kdRatio = obj.getScore(ChatColor.WHITE + "K/D: " + Double.parseDouble(killsAmount)/Double.parseDouble(getDeathsAmount()));
            kdRatio.setScore(3);
        }else{
            Score kdRatio = obj.getScore(ChatColor.WHITE + "K/D: " + Double.parseDouble(getKillsAmount()));
            kdRatio.setScore(3);
        }
        player.setScoreboard(board);
    }

    public String getKillsAmount() {
        return killsAmount;
    }

    public void setKillsAmount(String killsAmount) {
        this.killsAmount = killsAmount;
    }

    public String getDeathsAmount() {
        return deathsAmount;
    }

    public void setDeathsAmount(String deathsAmount) {
        this.deathsAmount = deathsAmount;
    }
}
