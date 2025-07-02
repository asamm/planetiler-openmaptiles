package org.openmaptiles.addons.layers;

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

public class Power implements
    Layer,
    OpenMapTilesProfile.OsmAllProcessor,
    OutdoorCyclingSchema,
    OutdoorHikeSchema {

    final double BUFFER_SIZE = 4.0;

    final int DEF_MIN_ZOOM = 12;

    private final PlanetilerConfig config;

    final String LAYER_NAME = "power";

    private final MultiExpression.Index<String> powerClassMapping;

    public Power(Translations translations, PlanetilerConfig config, Stats stats) {
        this.powerClassMapping = POWER_CLASS_MAPPING.index();
        this.config = config;
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        String classValue = powerClassMapping.getOrElse(sourceFeature, null);
        if (classValue == null) {
            return;
        }

        // Check if the source feature is a power line or a power pole if yes then change the min zoom to 14
        var minZoom = IS_MINOR_LINE_POLE_EXPRESSION.evaluate(sourceFeature) ? 14 : DEF_MIN_ZOOM;

//        if (sourceFeature.isPoint()) {
//            // Power poles, generator
//            var feat = collector.centroidIfConvex(LAYER_NAME);
//            feat.setBufferPixels(BUFFER_SIZE);
//            feat.setMinZoom(minZoom);
//            feat.setAttr(LmOutdoorSchema.OutdoorPowerSchema.Fields.CLASS, classValue);
//            feat.setAttr(LmOutdoorSchema.OutdoorPowerSchema.Fields.SUBCLASS, getSubClassValue(classValue, sourceFeature));
//        }

        var feat = collector.anyGeometry(LAYER_NAME);
        feat.setBufferPixels(BUFFER_SIZE);
        feat.setMinZoom(minZoom);
        feat.setAttr(LmOutdoorSchema.OutdoorPoiSchema.Fields.CLASS, classValue);
        feat.setAttr(LmOutdoorSchema.OutdoorPowerSchema.Fields.SUBCLASS, getSubClassValue(classValue, sourceFeature));
        if (sourceFeature.canBePolygon()){
            // for example names of power plants
            // TODO should be in separate label layer
            feat.setAttrWithMinzoom(LmOutdoorSchema.OutdoorPowerSchema.Fields.NAME, sourceFeature.getString("name"), 14);
        }
    }

    /**
     * Get the sub class value for the given feature and class value. Only specific classes have sub classes.
     *
     * @param classValue    the class value
     * @param sourceFeature the feature to get the sub class value for
     * @return the sub class value or null if the feature doesn't fit sub class
     */
    private String getSubClassValue(String classValue, SourceFeature sourceFeature) {

        if (IS_POWER_GENERATOR_EXPRESSION.evaluate(sourceFeature)) {
            return sourceFeature.getString("generator:source");
        }

        return null;
    }
}
