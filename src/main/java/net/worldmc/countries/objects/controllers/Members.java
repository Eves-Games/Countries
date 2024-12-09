package net.worldmc.countries.objects.controllers;

import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.enumerations.Rank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Members {
    private final Map<Citizen, Rank> memberRanks = new HashMap<>();

    public void addMember(Citizen member, Rank initialRank) { memberRanks.putIfAbsent(member, initialRank); }

    public void removeMember(Citizen member) { memberRanks.remove(member); }

    public List<Citizen> getMembers() { return List.copyOf(memberRanks.keySet()); }

    public boolean hasMember(Citizen member) { return memberRanks.containsKey(member); }

    public Rank getRank(Citizen member) { return memberRanks.get(member); }

    public void setRank(Citizen member, Rank newRank) {
        if (!hasMember(member)) {
            throw new IllegalArgumentException("Member does not exist.");
        }

        Rank currentRank = getRank(member);
        if (currentRank.equals(newRank)) {
            return;
        }

        memberRanks.put(member, newRank);
    }

    public boolean promote(Citizen member) {
        if (!hasMember(member)) {
            throw new IllegalArgumentException("Member does not exist.");
        }

        Rank currentRank = getRank(member);
        Rank nextRank = currentRank.getNextRank();
        if (!currentRank.equals(nextRank)) {
            setRank(member, nextRank);
            return true;
        }
        return false;
    }

    public boolean demote(Citizen member) {
        if (!hasMember(member)) {
            throw new IllegalArgumentException("Member does not exist.");
        }

        Rank currentRank = getRank(member);
        Rank previousRank = currentRank.getPreviousRank();
        if (!currentRank.equals(previousRank)) {
            setRank(member, previousRank);
            return true;
        }
        return false;
    }

    public List<Citizen> getMembersByRank(Rank rank) {
        return memberRanks.entrySet().stream()
                .filter(entry -> entry.getValue() == rank)
                .map(Map.Entry::getKey)
                .toList();
    }
}
