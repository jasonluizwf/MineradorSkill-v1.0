package com.victor.minerador.model;

public enum NivelMineracao {
    INICIANTE(500),
    AVANCADO(2000),
    MESTRE(5000);

    private final int blocosNecessarios;

    NivelMineracao(int blocos) {
        this.blocosNecessarios = blocos;
    }

    public int getBlocosNecessarios(){
        return blocosNecessarios;
    }

    public static NivelMineracao porBlocos(int blocos) {
        NivelMineracao atual = INICIANTE;
        for (NivelMineracao nivel : values()) {
            if (blocos >= nivel.getBlocosNecessarios()) {
                atual = nivel;
            }
        }
        return atual;
    }

}
