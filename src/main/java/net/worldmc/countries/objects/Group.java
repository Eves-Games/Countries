package net.worldmc.countries.objects;

import net.worldmc.countries.objects.controllers.BankAccount;
import net.worldmc.countries.objects.enumerations.Rank;
import net.worldmc.countries.objects.interfaces.Identity;
import net.worldmc.countries.objects.interfaces.MemberList;
import net.worldmc.countries.objects.interfaces.PlotList;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public abstract class Group implements MemberList, PlotList, Identity {
    private final UUID uuid;
    private String name;
    private Location spawn;
    private final BankAccount bankAccount;

    private Citizen leader;
    private final HashMap<Citizen, Rank> ranks = new HashMap<>();

    public Group(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name.trim();
        this.bankAccount = new BankAccount(uuid, name);
    }

    // Identity
    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String newName) {
        this.name = newName;
    }

    // Ranks
    public Rank getRank(Citizen citizen) { return ranks.getOrDefault(citizen, Rank.MEMBER); }

    public Citizen getLeader() { return leader; }

    public void setRank(Citizen citizen, Rank rank) {
        if (rank == Rank.LEADER) {
            if (leader != null) {
                ranks.put(leader, Rank.DEPUTY);
            }
            this.leader = citizen;
        }
        ranks.put(citizen, rank);
    }

    public boolean promote(Citizen citizen) {
        Rank currentRank = getRank(citizen);
        Rank nextRank = currentRank.getNextRank();

        if (currentRank != nextRank) {
            setRank(citizen, nextRank);
            return true;
        }
        return false;
    }

    public boolean demote(Citizen citizen) {
        Rank currentRank = getRank(citizen);
        Rank previousRank = currentRank.getPreviousRank();

        if (currentRank != previousRank) {
            setRank(citizen, previousRank);
            return true;
        }
        return false;
    }

    // Default
    public void setSpawn(Location spawn) { this.spawn = spawn; }

    public Location getSpawn() { return spawn; }

    public BankAccount getBankAccount() { return bankAccount; }

    public boolean isTrusted(Citizen citizen) { return getRank(citizen).getRankOrder() >= 1; }

    public boolean isExtraTrusted(Citizen citizen) { return getRank(citizen).getRankOrder() >= 2; }
}
