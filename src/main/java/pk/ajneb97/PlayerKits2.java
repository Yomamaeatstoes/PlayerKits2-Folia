
package pk.ajneb97;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pk.ajneb97.api.ExpansionPlayerKits;
import pk.ajneb97.api.PlayerKitsAPI;
import pk.ajneb97.configs.ConfigsManager;
import pk.ajneb97.database.MySQLConnection;
import pk.ajneb97.listeners.InventoryEditListener;
import pk.ajneb97.listeners.PlayerListener;
import pk.ajneb97.managers.*;
import pk.ajneb97.managers.edit.InventoryEditManager;
import pk.ajneb97.tasks.InventoryUpdateTaskManager;
import pk.ajneb97.tasks.PlayerDataSaveTask;
import pk.ajneb97.versions.NMSManager;

public class PlayerKits2 extends JavaPlugin {

    private String version = getDescription().getVersion();
    public static String prefix = MessagesManager.getColoredMessage("&8[&bPlayerKits&a²&8] ");

    private KitItemManager kitItemManager;
    private KitsManager kitsManager;
    private DependencyManager dependencyManager;
    private ConfigsManager configsManager;
    private MessagesManager messagesManager;
    private PlayerDataManager playerDataManager;
    private InventoryManager inventoryManager;
    private InventoryEditManager inventoryEditManager;
    private NMSManager nmsManager;


    private InventoryUpdateTaskManager inventoryUpdateTaskManager;
    private PlayerDataSaveTask playerDataSaveTask;
    private MySQLConnection mySQLConnection;

    public void onEnable(){
        registerCommands();
        registerEvents();

        this.kitItemManager = new KitItemManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.inventoryEditManager = new InventoryEditManager(this);
        this.kitsManager = new KitsManager(this);
        this.dependencyManager = new DependencyManager(this);
        this.nmsManager = new NMSManager();
        this.playerDataManager = new PlayerDataManager(this);

        this.configsManager = new ConfigsManager(this);
        this.configsManager.configure();

        this.inventoryUpdateTaskManager = new InventoryUpdateTaskManager(this);
        this.inventoryUpdateTaskManager.start();

        if(configsManager.getMainConfigManager().isMySQL()){
            mySQLConnection = new MySQLConnection(this);
            mySQLConnection.setupMySql();
        }else{
            reloadPlayerDataSaveTask();
        }

        PlayerKitsAPI api = new PlayerKitsAPI(this);
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new ExpansionPlayerKits(this).register();
        }

        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+"&eHas been enabled! &fVersion: "+version));
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+"&eThanks for using my plugin!   &f~Ajneb97"));
    }

    public void onDisable(){
        this.configsManager.getPlayersConfigManager().saveConfigs();
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+"&eHas been disabled! &fVersion: "+version));
    }

    public void registerCommands(){
        this.getCommand("kit").setExecutor(new MainCommand(this));
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new InventoryEditListener(this), this);
    }

    public void reloadPlayerDataSaveTask() {
        if(playerDataSaveTask != null) {
            playerDataSaveTask.end();
        }
        playerDataSaveTask = new PlayerDataSaveTask(this);
        playerDataSaveTask.start(configsManager.getMainConfigManager().getConfig().getInt("player_data_save_time"));
    }

    public KitItemManager getKitItemManager() {
        return kitItemManager;
    }

    public KitsManager getKitsManager() {
        return kitsManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void setMessagesManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    public ConfigsManager getConfigsManager() {
        return configsManager;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public InventoryEditManager getInventoryEditManager() {
        return inventoryEditManager;
    }

    public MySQLConnection getMySQLConnection() {
        return mySQLConnection;
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }
}