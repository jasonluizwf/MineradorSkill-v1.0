package com.victor.minerador;

import com.victor.minerador.commands.ComandoMinerador;
import com.victor.minerador.data.PlayerManager;
import com.victor.minerador.listeners.MinerarListener;
import com.victor.minerador.utils.BarraDeProgressoUtil;
import com.victor.minerador.utils.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class Minerador extends JavaPlugin {

    PlayerManager playerManager;
    BarraDeProgressoUtil barraDeProgressoUtil;
    ItemFactory itemFactory;

    @Override
    public void onEnable() {
        itemFactory = new ItemFactory(this);
        playerManager = new PlayerManager(this);

        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new MinerarListener(this, itemFactory), this);
        Objects.requireNonNull(getCommand("minerador")).setExecutor(
                new ComandoMinerador(playerManager, barraDeProgressoUtil)
        );
        getLogger().info("Plugin Minerador ativado!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public PlayerManager getPlayerManager() { return playerManager; }
}
