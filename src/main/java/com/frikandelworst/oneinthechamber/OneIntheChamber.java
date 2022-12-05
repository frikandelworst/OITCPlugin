package com.frikandelworst.oneinthechamber;

import com.frikandelworst.oneinthechamber.Listeners.BowListener;
import com.frikandelworst.oneinthechamber.Listeners.DeathListener;
import com.frikandelworst.oneinthechamber.Listeners.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class OneIntheChamber extends JavaPlugin {
    private static OneIntheChamber plugin;
    private DataSource dataSource;
    private Database database;
    private KDBoard kdBoard;
    private String dataSaveMethod;
    private File customConfigFile;
    private FileConfiguration customConfig;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new BowListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        setDataSaveMethod(getPlugin().getConfig().getString("DataSaveMethod"));
        kdBoard = new KDBoard();
        if(getDataSaveMethod().equals("SQL")){
            database = new Database();
            this.dataSource = database.initMySQLDataSource();
            Bukkit.getLogger().severe("Disabling plugin because of yeyeye");
            Bukkit.getServer().getPluginManager().disablePlugin(OneIntheChamber.getPlugin());
            initDb();
        }
        createCustomConfig();
        Bukkit.getLogger().info("Plugin succesfully enabled.");
    }

    @Override
    public void onDisable() {
    }

    public static OneIntheChamber getPlugin() {
        return plugin;
    }

    public void givePlayerKit(Player player){
        //Maak een boog die unbreakable is
        ItemStack bowStack = new ItemStack(Material.BOW);
        ItemMeta bowStackMeta = bowStack.getItemMeta();
        bowStackMeta.setUnbreakable(true);
        bowStackMeta.setDisplayName(ChatColor.AQUA + "Special bow");
        bowStack.setItemMeta(bowStackMeta);

        //Voeg de boog & een arrow toe aan de inventory van de speler.
        player.getInventory().addItem(bowStack);
        player.getInventory().addItem(new ItemStack(Material.ARROW));
    }

    private void initDb() {
        try{
            String setup;
            try (InputStream in = getClassLoader().getResourceAsStream("dbsetup.sql")) {
                setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
                throw e;
            }
            String[] queries = setup.split(";");
            for (String query : queries) {
                if (query.isBlank()) continue;
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.execute();
                }
            }
            getLogger().info("Database setup complete.");
        }catch(SQLException | IOException e){
            getLogger().severe("Something went wrong initializing the database.");
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Database getDatabase() {
        return database;
    }

    public String getDataSaveMethod() {
        return dataSaveMethod;
    }

    public void setDataSaveMethod(String dataSaveMethod) {
        this.dataSaveMethod = dataSaveMethod;
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "data.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public File getCustomConfigFile() {
        return customConfigFile;
    }

    public KDBoard getKdBoard() {
        return kdBoard;
    }
}



