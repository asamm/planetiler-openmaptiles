package org.openmaptiles.addons.layers;

import static com.onthegomap.planetiler.util.MemoryEstimator.CLASS_HEADER_BYTES;
import static com.onthegomap.planetiler.util.MemoryEstimator.POINTER_BYTES;
import static com.onthegomap.planetiler.util.MemoryEstimator.estimateSize;
import static org.openmaptiles.util.Utils.brunnel;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.reader.osm.OsmElement;
import com.onthegomap.planetiler.reader.osm.OsmRelationInfo;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.MemoryEstimator;
import com.onthegomap.planetiler.util.Translations;
import java.util.List;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.generated.OpenMapTilesSchema;
import org.openmaptiles.util.OmtLanguageUtils;

public class Hiking implements
    Layer,
    OpenMapTilesProfile.OsmAllProcessor ,
    LmOutdoorSchema.OutdoorHikeSchema {

    final double BUFFER_SIZE = 4.0;

    final String LAYER_NAME = "hiking";

    private final MultiExpression.Index<String> highwayMapping;

    public Hiking(Translations translations,  PlanetilerConfig config, Stats stats) {

        this.highwayMapping = LM_HIGHWAY_MAPPING.index();
    }
    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        if (sourceFeature.canBeLine() && IS_HIKE_EXPRESSION.evaluate(sourceFeature)) {

            var feat = collector.line(LAYER_NAME);

            // create a new feature in the outdoor layer as a point
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setMinZoom(12);

            // get original value of ref tag
            feat.setAttr(Fields.REF, sourceFeature.getString("ref"));
            feat.setAttr(Fields.NAME, sourceFeature.getString("name"));
            feat.setAttr(Fields.NETWORK, sourceFeature.getString("network"));
            feat.setAttr(Fields.OSMC_COLOR, sourceFeature.getString("osmc_color"));
            feat.setAttr(Fields.OSMC_FOREGROUND, sourceFeature.getString("osmc_foreground"));
            feat.setAttr(Fields.OSMC_ORDER, sourceFeature.getLong("osmc_order"));
            feat.setAttr(Fields.HIGHWAY, this.highwayMapping.getOrElse(sourceFeature, null));
            feat.setAttr(OpenMapTilesSchema.Transportation.Fields.BRUNNEL,
                brunnel(sourceFeature.getBoolean("bridge"), sourceFeature.getBoolean("tunnel"), sourceFeature.getBoolean("ford")));
        }
    }
}
