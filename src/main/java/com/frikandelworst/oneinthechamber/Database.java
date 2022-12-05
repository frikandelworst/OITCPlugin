package com.frikandelworst.oneinthechamber;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private void testDataSource(DataSource dataSource) throws SQLException{
        try{
            dataSource.getConnection();
        }catch (NullPointerException | SQLException e){
            Bukkit.getServer().getPluginManager().disablePlugin(OneIntheChamber.getPlugin());
        }
    }

    public DataSource initMySQLDataSource(){
        try{
            String DBname = OneIntheChamber.getPlugin().getConfig().getString("name");
            String port = OneIntheChamber.getPlugin().getConfig().getString("port");
            String host = OneIntheChamber.getPlugin().getConfig().getString("host");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://"+ host + ":" + port + "/" + DBname + "?useSSL=false");
            config.setUsername(OneIntheChamber.getPlugin().getConfig().getString("username"));
            config.setPassword(OneIntheChamber.getPlugin().getConfig().getString("password"));
            HikariDataSource dataSource = new HikariDataSource(config);
            dataSource.setMaximumPoolSize(8);
            testDataSource(dataSource);
            return dataSource;
        }catch (SQLException e){
            Bukkit.getLogger().severe("Something went wrong initializing a connection to the database.");
            return null;
        }

    }
    public boolean initializePlayerInDatabase(Player player){
        String uuid = player.getUniqueId().toString();
        try(Connection conn = OneIntheChamber.getPlugin().getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO KillsAndDeathsTable(uuid, kills, deaths) VALUES(?, ?, ?)"
        )){
                stmt.setString(1, uuid);
                stmt.setInt(2, 0);
                stmt.setInt(3, 0);
                return stmt.executeUpdate() > 0;
        }catch (SQLException e){
                Bukkit.getLogger().warning("Failed to add new player to database.");
                player.kickPlayer("Something went wrong adding you to the database, please notify a developer.");
        }
        return false;
    }

    public boolean updateKills(Player player, int amount){
        String uuid = player.getUniqueId().toString();
        try(Connection conn = OneIntheChamber.getPlugin().getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE KillsAndDeathsTable SET kills = kills + ? WHERE uuid = ?"
        )){
            stmt.setInt(1, amount);
            stmt.setString(2, uuid);
            return stmt.executeUpdate() > 0;
        }catch (SQLException e){
            Bukkit.getLogger().warning("Failed to update player kills.");
        }
        return false;
    }
    public boolean updateDeaths(Player player, int amount){
        String uuid = player.getUniqueId().toString();
        try(Connection conn = OneIntheChamber.getPlugin().getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE KillsAndDeathsTable SET deaths = deaths + ? WHERE uuid = ?"
        )){
            stmt.setInt(1, amount);
            stmt.setString(2, uuid);
            return stmt.executeUpdate() > 0;
        }catch (SQLException e){
            Bukkit.getLogger().warning("Failed to update player deaths.");
        }
        return false;
    }

    public int getKills(Player player){
        String uuid = player.getUniqueId().toString();
        try(Connection conn = OneIntheChamber.getPlugin().getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT kills FROM KillsAndDeathsTable WHERE uuid = ?"
        )){
            stmt.setString(1, uuid);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return (int) resultSet.getLong("kills");
            }
            return 0;
        }catch (SQLException e){
            Bukkit.getLogger().warning("Failed to get kills from database.");
        }
        return 0;
    }

    public int getDeaths(Player player){
        String uuid = player.getUniqueId().toString();
        try(Connection conn = OneIntheChamber.getPlugin().getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT deaths FROM KillsAndDeathsTable WHERE uuid = ?"
        )){
            stmt.setString(1, uuid);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return (int) resultSet.getLong("deaths");
            }
            return 0;
        }catch (SQLException e){
            Bukkit.getLogger().warning("Failed to get kills from database.");
        }
        return 0;
    }
}