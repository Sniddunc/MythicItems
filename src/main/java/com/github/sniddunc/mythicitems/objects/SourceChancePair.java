package com.github.sniddunc.mythicitems.objects;

public class SourceChancePair {

    private int chance;
    private int amount;

    public SourceChancePair(int chance, int amount) {
        this.chance = chance;
        this.amount = amount;
    }

    public int getChance() {
        return chance;
    }

    public int getAmount() {
        return amount;
    }
}
