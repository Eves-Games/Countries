package net.worldmc.countries.objects.enumerations;

public enum Rank {
    MEMBER(0),
    ELDER(1),
    DEPUTY(2),
    LEADER(3);

    private final int rankOrder;

    Rank(int rankOrder) {
        this.rankOrder = rankOrder;
    }

    public int getRankOrder() {
        return rankOrder;
    }

    public Rank getNextRank() {
        if (this == LEADER) return this;
        return values()[this.ordinal() + 1];
    }

    public Rank getPreviousRank() {
        if (this == MEMBER) return this;
        return values()[this.ordinal() - 1];
    }
}
