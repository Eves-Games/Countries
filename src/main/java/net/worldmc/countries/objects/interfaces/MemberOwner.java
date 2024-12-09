package net.worldmc.countries.objects.interfaces;

import net.worldmc.countries.objects.Citizen;

public interface MemberOwner extends MemberList {
    void addMember(Citizen member);
    void removeMember(Citizen member);
}
