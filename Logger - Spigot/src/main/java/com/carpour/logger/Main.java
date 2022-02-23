package com.carpour.logger;

import com.carpour.logger.API.AuthMeUtil;
import com.carpour.logger.API.EssentialsUtil;
import com.carpour.logger.API.VaultUtil;
import com.carpour.logger.Commands.OnLogger;
import com.carpour.logger.Database.External.External;
import com.carpour.logger.Database.External.ExternalData;
import com.carpour.logger.Discord.Discord;
import com.carpour.logger.Discord.DiscordFile;
import com.carpour.logger.Events.*;
import com.carpour.logger.Events.OnCommands.OnCommand;
import com.carpour.logger.Events.OnInventories.OnCraft;
import com.carpour.logger.Events.OnInventories.OnFurnace;
import com.carpour.logger.Events.PluginDependent.OnAFK;
import com.carpour.logger.Events.PluginDependent.OnAuthMePassword;
import com.carpour.logger.Events.PluginDependent.OnVault;
import com.carpour.logger.Utils.*;
import com.carpour.logger.Database.SQLite.SQLite;
import com.carpour.logger.Database.SQLite.SQLiteData;
import com.carpour.logger.ServerSide.*;
import de.jeff_media.updatechecker.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static com.carpour.logger.Utils.Data.*;

public class Main extends JavaPlugin {

    private static Main instance;

    private External external;

    private SQLite sqLite;

    private Discord discord;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults();

        this.initializer(new Data());

        Messages.Setup();
        Messages.get().options().copyDefaults(true);

        DiscordFile.Setup();
        DiscordFile.get().options().copyDefaults(true);

        this.discord = new Discord();
        this.discord.run();

        databaseSetup();

        if (isLogToFiles && isSqlite){

            getLogger().warning("Logging to Files and SQLite are both enabled, this might impact your Server's Performance!");

        }

        FileHandler fileHandler = new FileHandler(getDataFolder());
        fileHandler.deleteFiles();

        getServer().getPluginManager().registerEvents(new OnPlayerChat(), this);
        getServer().getPluginManager().registerEvents(new OnCommand(), this);
        getServer().getPluginManager().registerEvents(new Console(), this);
        getServer().getPluginManager().registerEvents(new OnSign(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerLeave(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerKick(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerTeleport(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerLevel(), this);
        getServer().getPluginManager().registerEvents(new OnBlockPlace(), this);
        getServer().getPluginManager().registerEvents(new OnBlockBreak(), this);
        getServer().getPluginManager().registerEvents(new PortalCreation(), this);
        getServer().getPluginManager().registerEvents(new OnBucketFill(), this);
        getServer().getPluginManager().registerEvents(new OnBucketEmpty(), this);
        getServer().getPluginManager().registerEvents(new OnAnvil(), this);
        getServer().getPluginManager().registerEvents(new OnItemPickup(), this);
        getServer().getPluginManager().registerEvents(new OnItemDrop(), this);
        getServer().getPluginManager().registerEvents(new OnEnchant(), this);
        getServer().getPluginManager().registerEvents(new OnBook(), this);
        getServer().getPluginManager().registerEvents(new RCON(), this);
        getServer().getPluginManager().registerEvents(new OnGameMode(), this);

        getServer().getPluginManager().registerEvents(new OnFurnace(), this);
        getServer().getPluginManager().registerEvents(new OnCraft(), this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPS(), 300L, ramTpsChecker);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new RAM(), 300L, ramTpsChecker);

        Objects.requireNonNull(getCommand("logger")).setExecutor(new OnLogger());

        new ASCIIArt().Art();

        // bStats
        new Metrics(this, 12036);

        // Update Checker
        if (isUpdateChecker) {

            UpdateChecker.init(this, resource_ID)
                    .checkEveryXHours(2)
                    .setChangelogLink(resource_ID)
                    .setNotifyByPermissionOnJoin(loggerUpdate)
                    .checkNow();

        }

        loadPluginDepends();

        getLogger().info(ChatColor.GOLD + "Thank you " + ChatColor.GREEN + ChatColor.BOLD + "thelooter" + ChatColor.GOLD + " for the Contribution!");

        getLogger().info("Plugin Enabled!");

        new Start().run();
    }

    @Override
    public void onDisable() {

        new Stop().run();

        if (isExternal && this.external.isConnected()) this.external.disconnect();

        if (isSqlite && this.sqLite.isConnected()) this.sqLite.disconnect();

        this.discord.disconnect();

        getLogger().info("Plugin Disabled!");

    }

    private void initializer(Data data){

        data.initializeDateFormatter();
        data.initializeStrings();
        data.initializeListOfStrings();
        data.initializeIntegers();
        data.initializeLongs();
        data.initializeBooleans();
        data.initializePermissionStrings();

    }

    private void databaseSetup(){

        if (isExternal) {

            this.external = new External();
            this.external.connect();
            ExternalData externalData = new ExternalData(this);
            if (this.external.isConnected()) {
                externalData.createTable();
                externalData.emptyTable();
            }
        }

        if (isSqlite) {

            this.sqLite = new SQLite();
            this.sqLite.connect();
            SQLiteData sqLiteData = new SQLiteData(this);
            if (this.sqLite.isConnected()) {
                sqLiteData.createTable();
                sqLiteData.emptyTable();
            }
        }
    }

    private void loadPluginDepends(){

        if (EssentialsUtil.getEssentialsAPI() != null){

            getServer().getPluginManager().registerEvents(new OnAFK(), this);

            getLogger().info("Essentials Plugin Detected!");

        }

        if (AuthMeUtil.getAuthMeAPI() != null){

            getServer().getPluginManager().registerEvents(new OnAuthMePassword(), this);

            getLogger().info("AuthMe Plugin Detected!");

        }

        if (VaultUtil.getVaultAPI()){

            if (VaultUtil.getVault().isEnabled()) {

                OnVault vault = new OnVault();
                getServer().getPluginManager().registerEvents(vault, this);
                getServer().getScheduler().scheduleSyncRepeatingTask(this, vault, 60L, vaultChecker);
            }

            getLogger().info("Vault Plugin Detected!");

        }
    }

    public static Main getInstance() { return instance; }

    public External getExternal() { return this.external; }

    public SQLite getSqLite() { return this.sqLite; }
}