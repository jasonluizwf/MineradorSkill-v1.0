package com.victor.minerador.model;

public enum PremiacaoEnum {
    PRIMEIRA(1000),
    SEGUNDA(2000),
    TERCEIRA(3000);

    private final int qtdBlocos;

    PremiacaoEnum(int qtdBlocos) {this.qtdBlocos = qtdBlocos;}

    public int getQtdBlocos() {return qtdBlocos;}
}
