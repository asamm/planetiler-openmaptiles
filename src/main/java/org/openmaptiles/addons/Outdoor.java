package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import java.util.logging.Logger;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesMain;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.generated.OpenMapTilesSchema;
import org.slf4j.LoggerFactory;

public class Outdoor implements Layer,
    LmOutdoorSchema.OutdoorPoiSchema,
    OpenMapTilesProfile.OsmAllProcessor{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Outdoor.class);

    private static final double BUFFER_SIZE = 64.0;

    private static final String LAYER_NAME = "outdoor";

    private final MultiExpression.Index<String> classMapping;

    public Outdoor() {

        this.classMapping = OUTDOOR_FILTER.index();
    }


    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature feature, FeatureCollector features) {
        String className = classMapping.getOrElse(feature, null);
        if (className != null) {
            LOGGER.info("Processing feature with class: " + className);
        }
    }
}
