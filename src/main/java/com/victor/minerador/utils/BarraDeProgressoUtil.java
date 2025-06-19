package com.victor.minerador.utils;

import com.victor.minerador.model.NivelMineracaoeEnum;
import org.bukkit.entity.Player;

public class BarraDeProgressoUtil {

    public void mostrarBarra(Player p, int blocos) {

        int iniciante = NivelMineracaoeEnum.INICIANTE.getBlocosNecessarios();
        int avancado = NivelMineracaoeEnum.AVANCADO.getBlocosNecessarios();
        int mestre = NivelMineracaoeEnum.MESTRE.getBlocosNecessarios();

        int proximo;

        if (blocos < iniciante) {
            proximo = iniciante;
        } else if (blocos < avancado) {
            proximo = avancado;
        } else if (blocos < mestre) {
            proximo = mestre;
        } else {
            proximo = -1; // ou 0, ou mestre novamente, depende do que você quer dizer com "máximo"
        }

        int nivel = 0;
        String nivelString = "";

        int barra = (int) (((double) blocos / proximo) * 20);
        StringBuilder barraVisual = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            barraVisual.append(i < barra ? "§a|" : "§7|");
        }

        if (nivel >= mestre) {
            nivelString = NivelMineracaoeEnum.MESTRE.name();
        } else if (nivel >= avancado) {
            nivelString = NivelMineracaoeEnum.AVANCADO.name();
        } else if (nivel >= iniciante) {
            nivelString = NivelMineracaoeEnum.INICIANTE.name();
        }

        p.sendMessage("§eMinerador nivel " + nivelString + " [" + barraVisual + "§e] §f" + blocos + "/" + proximo);
    }
}