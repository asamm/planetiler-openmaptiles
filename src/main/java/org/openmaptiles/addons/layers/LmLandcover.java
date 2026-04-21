package org.openmaptiles.addons.layers;

import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorLmLandcoverSchema.IS_LANDTYPE_EXPRESSION;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import com.onthegomap.planetiler.util.ZoomFunction;
import java.util.Map;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.addons.OsmTags;

public class LmLandcover implements
    Layer,
    OpenMapTilesProfile.OsmAllProcessor {

    final double BUFFER_SIZE = 4.0;

    public static final ZoomFunction<Number> MIN_PIXEL_SIZE_THRESHOLDS = ZoomFunction.fromMaxZoomThresholds(Map.of(
        13, 4,
        7, 2,
        6, 1
    ));

    final String LAYER_NAME = "lm_landcover";

    public LmLandcover(Translations translations, PlanetilerConfig config, Stats stats) {

    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        boolean isLandtype = IS_LANDTYPE_EXPRESSION.evaluate(sourceFeature);

        if (isLandtype && sourceFeature.canBePolygon()) {
            var feat = collector.polygon(LAYER_NAME);
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setMinZoom((int) sourceFeature.getLong(OsmTags.NE_MIN_ZOOM));
            feat.setMaxZoom((int) sourceFeature.getLong(OsmTags.NE_MAX_ZOOM));
            feat.setAttr(LmOutdoorSchema.OutdoorLmLandcoverSchema.Fields.CLASS,
                sourceFeature.getString(OsmTags.NE_LANDTYPE));
            feat.setMinPixelSizeOverrides(MIN_PIXEL_SIZE_THRESHOLDS);
        }
    }
}
