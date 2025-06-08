package com.victor.minerador.utils;

import com.victor.minerador.model.NivelMineracao;
import org.bukkit.entity.Player;

public class BarraDeProgressoUtil {

    public void mostrarBarra(Player p, int blocos) {

        int iniciante = NivelMineracao.INICIANTE.getBlocosNecessarios();
        int avancado = NivelMineracao.AVANCADO.getBlocosNecessarios();
        int mestre = NivelMineracao.MESTRE.getBlocosNecessarios();

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
            nivelString = NivelMineracao.MESTRE.name();
        } else if (nivel >= avancado) {
            nivelString = NivelMineracao.AVANCADO.name();
        } else if (nivel >= iniciante) {
            nivelString = NivelMineracao.INICIANTE.name();
        }

        p.sendMessage("§eMinerador nivel " + nivelString + " [" + barraVisual + "§e] §f" + blocos + "/" + proximo);
    }
}
