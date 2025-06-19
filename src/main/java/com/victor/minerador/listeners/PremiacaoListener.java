package com.victor.minerador.listeners;

import com.victor.minerador.data.PlayerManager;
import com.victor.minerador.model.PremiacaoEnum;
import com.victor.minerador.utils.ItemFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

import java.util.UUID;

public class PremiacaoListener implements Listener {

    private final PlayerManager playerManager;
    private final ItemFactory itemFactory;

    public PremiacaoListener(PlayerManager playerManager, ItemFactory itemFactory) {
        this.playerManager = playerManager;
        this.itemFactory = itemFactory;
    }

    @EventHandler
    public void premiarPlayer(PlayerEvent e) {
        Player player = e.getPlayer();
        UUID id = player.getUniqueId();
        int qtdBlocos = playerManager.carregarProgresso(id);

        if(qtdBlocos >= PremiacaoEnum.PRIMEIRA.getQtdBlocos()) {
            player.getInventory().addItem(itemFactory.criarPicaretaPersonalizada());
        }
    }
}
