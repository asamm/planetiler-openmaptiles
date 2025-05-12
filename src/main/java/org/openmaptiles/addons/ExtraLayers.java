package org.openmaptiles.addons;

import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import java.util.List;
import org.openmaptiles.Layer;
import org.openmaptiles.addons.layers.Contour;
import org.openmaptiles.addons.layers.Cycling;
import org.openmaptiles.addons.layers.Hiking;
import org.openmaptiles.addons.layers.LmTransportation;
import org.openmaptiles.addons.layers.OutdoorPoi;
import org.openmaptiles.addons.layers.Ski;

/**
 * Registry of extra custom layers that you can add to the openmaptiles schema.
 */
public class ExtraLayers {

    public static List<Layer> create(Translations translations, PlanetilerConfig config, Stats stats) {
        return List.of(
            // Create classes that extend Layer interface in the addons package, then instantiate them here
            new Hiking(translations, config, stats),
            new Cycling(translations, config, stats),
            new Contour(translations, config, stats),
            new LmTransportation(translations, config, stats),
            new OutdoorPoi(translations, config, stats),
            new Ski(translations, config, stats)
        );
    }
}
