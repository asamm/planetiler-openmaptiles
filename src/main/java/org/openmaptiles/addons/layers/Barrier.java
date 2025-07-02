package org.openmaptiles.addons.layers;

import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorBarrierSchema.BARRIER_CLASS_MAPPING;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorBarrierSchema.Fields.CLASS_BARRIER;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorCyclingSchema;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorHikeSchema;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorPowerSchema.IS_MINOR_LINE_POLE_EXPRESSION;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorPowerSchema.IS_POWER_GENERATOR_EXPRESSION;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorPowerSchema.POWER_CLASS_MAPPING;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;

public class Barrier implements
    Layer,
    OpenMapTilesProfile.OsmAllProcessor
    {

    final double BUFFER_SIZE = 4.0;

    final int DEF_MIN_ZOOM = 14;

    final String LAYER_NAME = "barrier";

    private final MultiExpression.Index<String> barrierClassMapping;

    public Barrier(Translations translations, PlanetilerConfig config, Stats stats) {
        this.barrierClassMapping = BARRIER_CLASS_MAPPING.index();
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        String classValue = barrierClassMapping.getOrElse(sourceFeature, null);
        if (classValue == null) {
            return;
        }

        var feat = collector.line(LAYER_NAME);
        feat.setBufferPixels(BUFFER_SIZE);
        feat.setMinZoom(DEF_MIN_ZOOM);
        feat.setAttr(LmOutdoorSchema.OutdoorBarrierSchema.Fields.CLASS, classValue);
        feat.setAttr(LmOutdoorSchema.OutdoorBarrierSchema.Fields.SUBCLASS, getSubClassValue(classValue, sourceFeature));

    }

    /**
     * Get the sub class value for the given feature and class value. Only specific classes have sub classes.
     *
     * @param classValue    the class value
     * @param sourceFeature the feature to get the sub class value for
     * @return the sub class value or null if the feature doesn't fit sub class
     */
    private String getSubClassValue(String classValue, SourceFeature sourceFeature) {

        if (classValue == CLASS_BARRIER) {
            // get value of barrier tag;
            return sourceFeature.getString("barrier");
        }

        return null;
    }
}
