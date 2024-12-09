package net.worldmc.countries.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

public class SidebarManager {
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Map<String, Integer> fieldLines;
    private final Map<Integer, String> displayedLines;

    public SidebarManager() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        objective = scoreboard.registerNewObjective("sidebar", Criteria.DUMMY, Component.text("[Countries]", NamedTextColor.GOLD));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        fieldLines = new HashMap<>();
        displayedLines = new HashMap<>();
    }

    public void updateField(String field, Component value, int line) {
        if (fieldLines.containsKey(field)) {
            int oldLine = fieldLines.get(field);
            String oldDisplay = displayedLines.get(oldLine);
            scoreboard.resetScores(oldDisplay);
        }

        String plainTextValue = LegacyComponentSerializer.legacySection().serialize(value);

        Score score = objective.getScore(plainTextValue);
        score.setScore(line);

        fieldLines.put(field, line);
        displayedLines.put(line, plainTextValue);
    }

    public void clearFields() {
        for (String displayedLine : displayedLines.values()) {
            scoreboard.resetScores(displayedLine);
        }
        fieldLines.clear();
        displayedLines.clear();
    }

    public void displayTo(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void removeFrom(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
