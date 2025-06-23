package com.victor.minerador.utils;

import com.victor.minerador.data.PlayerManager;
import com.victor.minerador.model.PremiacaoEnum;
import org.bukkit.entity.Player;
import java.util.UUID;

public class PremiacaoPlayer {

    /**
     * Função para premiar um player que alcança a quantidade de blocos, premia com qualquer item criado no itemFactory.
     * @param player Deve ser passado como parametro um objeto do tipo player.
     * @param playerManager Deve ser passado como parametro um objeto que gerencia player.
     * @param itemFactory Deve ser passado como parametro um objeto que cria itens.
     */
    public static void premiarPlayer(Player player, PlayerManager playerManager, ItemFactory itemFactory) {
        if(playerManager == null) return;

        UUID id = player.getUniqueId();
        int qtdBlocos = playerManager.carregarProgresso(id);

        if(qtdBlocos == PremiacaoEnum.PRIMEIRA.getQtdBlocos()) {
            player.getInventory().addItem(itemFactory.criarPicaretaPersonalizada());
            player.sendMessage("Parabéns! Você ganhou sua picareta!");
        }
    }
}
