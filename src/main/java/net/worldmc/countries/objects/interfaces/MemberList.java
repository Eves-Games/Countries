package net.worldmc.countries.objects.interfaces;

import net.worldmc.countries.objects.Citizen;

import java.util.List;

public interface MemberList {
    List<Citizen> getMembers();
    boolean hasMember(Citizen citizen);
    int getMemberCount();
}