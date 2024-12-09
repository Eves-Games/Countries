package net.worldmc.countries.listeners;

import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TownProtectionListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player attacker && damaged instanceof Player victim) {

            Chunk chunk = victim.getLocation().getChunk();
            Plot plot = CacheManager.getInstance().getPlot(chunk);

            if (plot != null && plot.getTown() != null) {
                event.setCancelled(true);
                MessageUtils.sendMessage(attacker, Component.text("PvP is disabled within town boundaries."));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isPlayerTrustedInCurrentTown(player)) {
            event.setCancelled(true);
            MessageUtils.sendMessage(player, Component.text("You cannot break blocks in this town as you are not trusted."));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isPlayerTrustedInCurrentTown(player)) {
            event.setCancelled(true);
            MessageUtils.sendMessage(player, Component.text("You cannot place blocks in this town as you are not trusted."));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isPlayerTrustedInCurrentTown(player)) {
            event.setCancelled(true);
            MessageUtils.sendMessage(player, Component.text("You cannot interact with items or blocks in this town as you are not trusted."));
        }
    }

    private boolean isPlayerTrustedInCurrentTown(Player player) {
        Chunk playerChunk = player.getLocation().getChunk();
        Plot block = CacheManager.getInstance().getPlot(playerChunk);

        if (block == null || block.getTown() == null) {
            return false;
        }

        Town town = block.getTown();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);

        return !town.isTrusted(citizen);
    }
}
