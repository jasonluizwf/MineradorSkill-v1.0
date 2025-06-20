package com.victor.minerador.utils;

import com.victor.minerador.Minerador;
import com.victor.minerador.model.PremiacaoEnum;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class PremiacaoPlayer {

    private final Minerador plugin;

    public PremiacaoPlayer(Minerador plugin) {
        this.plugin = plugin;
    }

    public void premiarPlayer(Player p) {
        getLogger().info("entrou para premiar antes do if");

        if(p == null) return;
        getLogger().info("passou o if");

        UUID id = p.getUniqueId();
        int qtdBlocos = plugin.getPlayerManager().carregarProgresso(id);

        if(qtdBlocos == PremiacaoEnum.PRIMEIRA.getQtdBlocos()) {
            p.getInventory().addItem(plugin.getItemFactory().criarPicaretaPersonalizada());
            p.sendMessage("Parabéns! Você ganhou sua picareta!");
        }
    }
}
