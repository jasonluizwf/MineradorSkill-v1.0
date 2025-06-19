package com.victor.minerador.model;

public enum NivelMineracaoeEnum {
    INICIANTE(500),
    AVANCADO(2000),
    MESTRE(5000),
    GOD(10000);

    private final int blocosNecessarios;

    NivelMineracaoeEnum(int blocos) {
        this.blocosNecessarios = blocos;
    }

    public int getBlocosNecessarios(){
        return blocosNecessarios;
    }

    public static NivelMineracaoeEnum porBlocos(int blocos) {
        NivelMineracaoeEnum atual = INICIANTE;
        for (NivelMineracaoeEnum nivel : values()) {
            if (blocos >= nivel.getBlocosNecessarios()) {
                atual = nivel;
            }
        }
        return atual;
    }
}