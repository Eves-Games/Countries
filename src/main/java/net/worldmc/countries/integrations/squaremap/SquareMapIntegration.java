package net.worldmc.countries.integrations.squaremap;

import org.bukkit.World;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.marker.Rectangle;

import java.util.Optional;

public final class SquareMapIntegration {

    private static Squaremap platform;

    public static void initialize() {
        platform = SquaremapProvider.get();
    }

    public static void registerLayer(World world, String key, String label, int priority, boolean defaultHidden, int zIndex) {
        Optional<MapWorld> mapWorldOptional = platform.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world));
        if (mapWorldOptional.isEmpty()) {
            return;
        }

        MapWorld mapWorld = mapWorldOptional.get();
        Key layerKey = Key.of(key);

        SimpleLayerProvider provider = SimpleLayerProvider.builder(label)
                .showControls(true)
                .defaultHidden(defaultHidden)
                .layerPriority(priority)
                .zIndex(zIndex)
                .build();

        mapWorld.layerRegistry().register(layerKey, provider);
    }

    public static void addRectangleMarker(String layerKey, String markerKey, World world, Point topLeft, Point bottomRight, String tooltip) {
        Optional<MapWorld> mapWorldOptional = platform.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world));
        if (mapWorldOptional.isEmpty()) {
            throw new IllegalStateException("Squaremap is not enabled for the world: " + world.getName());
        }

        MapWorld mapWorld = mapWorldOptional.get();
        SimpleLayerProvider provider = (SimpleLayerProvider) mapWorld.layerRegistry().get(Key.of(layerKey));

        Key markerKeyObj = Key.of(markerKey);
        Rectangle marker = Marker.rectangle(topLeft, bottomRight);

        marker.markerOptions(MarkerOptions.builder().hoverTooltip(tooltip).build());

        provider.addMarker(markerKeyObj, marker);
    }


    public static void removeMarker(String layerKey, String markerKey, World world) {
        Optional<MapWorld> mapWorldOptional = platform.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world));
        if (mapWorldOptional.isEmpty()) {
            return;
        }

        MapWorld mapWorld = mapWorldOptional.get();
        SimpleLayerProvider provider = (SimpleLayerProvider) mapWorld.layerRegistry().get(Key.of(layerKey));

        provider.removeMarker(Key.of(markerKey));
    }

    public static void unregisterLayer(String key, World world) {
        Optional<MapWorld> mapWorldOptional = platform.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world));
        if (mapWorldOptional.isEmpty()) {
            return;
        }

        MapWorld mapWorld = mapWorldOptional.get();
        mapWorld.layerRegistry().unregister(Key.of(key));
    }
}
