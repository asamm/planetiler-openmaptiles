package org.openmaptiles.addons.layers;

import static org.openmaptiles.addons.layers.LmTransportation.getBrunnel;
import static org.openmaptiles.addons.layers.LmTransportation.getOneWay;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.addons.LmUtils;
import org.openmaptiles.addons.OsmTags;
import org.openmaptiles.util.OmtLanguageUtils;
import org.slf4j.LoggerFactory;

public class Snowmobile implements Layer,
    LmOutdoorSchema.SnowmobileSchema,
    OpenMapTilesProfile.OsmAllProcessor{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Snowmobile.class);

    private static final double BUFFER_SIZE = 4.0;

    private static final String LAYER_NAME = "snowmobile";

    private final MultiExpression.Index<String> classMapping;

    private final Translations translations;

    public Snowmobile(Translations translations, PlanetilerConfig config, Stats stats) {

        this.classMapping = SNOWMOBILE_CLASS_MAPPING.index();

        this.translations = translations;
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        if (sourceFeature.canBeLine()){

            String classValue = classMapping.getOrElse(sourceFeature, null);

            if (classValue == null) {
                return;
            }

            var feat = collector.line(LAYER_NAME);

            // create a new feature in the outdoor layer as a point
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setAttr(LmOutdoorSchema.SnowmobileSchema.Fields.CLASS, classValue);
            feat.setAttr(LmOutdoorSchema.SnowmobileSchema.Fields.SUBCLASS, sourceFeature.getString("snowmobile"));
            feat.setMinZoom(12);

            // get original value of ref tag
            feat.setAttr(Fields.ICE_ROAD, LmUtils.getBoolAsPositiveInt(sourceFeature, "ice_road"));

        }
    }
}
