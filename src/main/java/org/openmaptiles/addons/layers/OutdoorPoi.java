package org.openmaptiles.addons.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.util.OmtLanguageUtils;
import org.slf4j.LoggerFactory;

public class OutdoorPoi implements Layer,
    LmOutdoorSchema.OutdoorPoiSchema,
    OpenMapTilesProfile.OsmAllProcessor{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OutdoorPoi.class);

    private static final double BUFFER_SIZE = 64.0;

    private static final String LAYER_NAME = "outdoor_poi";

    private final MultiExpression.Index<String> classMapping;

    private final MultiExpression.Index<String> subClassMapping;

    private final Translations translations;

    public OutdoorPoi(Translations translations, PlanetilerConfig config, Stats stats) {

        this.classMapping = OUTDOOR_POI_CLASS_MAPPING.index();

        this.subClassMapping = OUTDOOR_POI_SUBCLASS_MAPPING.index();

        this.translations = translations;
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        String classValue = classMapping.getOrElse(sourceFeature, null);
        if (classValue == null) {
            //LOGGER.debug("Skipping element {} with tags {}", sourceFeature.id(), sourceFeature.tagsAsJson());
            // when the class is not found, skip this feature because it's not an outdoor feature
            return;
        }

        String subClassValue = getSubClassValue(classValue, sourceFeature);

        // create a new feature in the outdoor layer as a point
        var feat = collector.centroidIfConvex(LAYER_NAME);
        feat.setBufferPixels(BUFFER_SIZE);
        feat.setAttr(Fields.CLASS, classValue);
        feat.setAttr(Fields.SUBCLASS, subClassValue);
        feat.putAttrs(OmtLanguageUtils.getNames(sourceFeature.tags(), translations));
        feat.setAttr(Fields.ELE, sourceFeature.getString("ele"));
        feat.setPointLabelGridPixelSize(14, 64);
        feat.setMinZoom(14);
        // TODO prepare rank
    }

    /**
     * Get the sub class value for the given feature and class value. Only specific classes have sub classes.
     * @param classValue the class value
     * @param feature the feature to get the sub class value for
     * @return the sub class value or null if the feature doesn't fit sub class
     */
    private String getSubClassValue(String classValue, SourceFeature feature) {

        if (CLASSES_WITH_SUBCLASSES.contains(classValue)) {
            return subClassMapping.getOrElse(feature, null);
        }

        return null;
    }
}
