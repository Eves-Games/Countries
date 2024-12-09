package net.worldmc.countries.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.worldmc.countries.objects.*;

public class ObjectComponents {

    public static Component groupComponent(Group group) {
        if (group == null) {
            return FormattingUtils.fail("None");
        }

        String commandPrefix;
        String citizenKey = "Citizens";
        String leaderKey = "Leader";
        switch (group) {
            case Town town -> commandPrefix = "town";
            case Country country -> commandPrefix = "country";
            case Business business -> {
                commandPrefix = "business";
                citizenKey = "Employees";
                leaderKey = "Owner";
            }
            default -> {
                return FormattingUtils.fail("Unknown Organization Type");
            }
        }

        Component hoverText = FormattingUtils.key(leaderKey + ": ").append(FormattingUtils.object(group.getLeader().getName()))
                .appendNewline()
                .append(FormattingUtils.key("Bank Account: ").append(FormattingUtils.balance(group.getBankAccount().getBalance())))
                .appendNewline()
                .append(FormattingUtils.key(citizenKey + ": ").append(FormattingUtils.general(group.getMembers().size())))
                .appendNewline()
                .append(FormattingUtils.key("Plots: ")).append(FormattingUtils.general(group.getPlotCount()));

        String command = "/" + commandPrefix + " info " + group.getName();

        return FormattingUtils.object(group.getName())
                .hoverEvent(HoverEvent.showText(hoverText))
                .clickEvent(ClickEvent.runCommand(command));
    }

    public static Component citizenComponent(Citizen citizen) {
        if (citizen == null) {
            return FormattingUtils.fail("None");
        }

        Component hoverText =
                FormattingUtils.key("Town: ").append(citizen.getTown() != null
                        ? FormattingUtils.object(citizen.getTown().getName())
                        : FormattingUtils.fail("None"))
                .appendNewline()
                .append(FormattingUtils.key("Business: ")).append(citizen.getBusiness() != null
                        ? FormattingUtils.object(citizen.getBusiness().getName())
                        : FormattingUtils.fail("None"))
                .appendNewline()
                .append(FormattingUtils.key("Plots: ")).append(FormattingUtils.general(citizen.getPlotCount()));

        String command = "/citizen info " + citizen.getName();

        return FormattingUtils.object(citizen.getName())
                .hoverEvent(HoverEvent.showText(hoverText))
                .clickEvent(ClickEvent.runCommand(command));
    }
}
