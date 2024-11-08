package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import java.util.Set;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.util.Utils;

public class LmTransportation implements Layer,
    LmOutdoorSchema.LmTrasportationSchema,
    OpenMapTilesProfile.OsmAllProcessor {

    private static final double BUFFER_SIZE = 4.0;

    private static final String LAYER_NAME = "lm_transportation";

    private static final Set<String> ACCESS_NO_VALUES = Set.of(
        "private", "no"
    );


    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature feature, FeatureCollector features) {

        if (feature.canBeLine() && IS_TRACK_EXPRESSION.evaluate(feature)) {
            FeatureCollector.Feature feat = features.line(LAYER_NAME);
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setAttrWithMinzoom(Fields.CLASS, feature.getTag(OsmTags.HIGHWAY), 12);
            feat.setAttrWithMinzoom(Fields.ACCESS, getAccess(feature.getTag(OsmTags.ACCESS)), 12);
            feat.setAttrWithMinzoom(Fields.ONEWAY, getOneWay(feature.getTag(OsmTags.ONEWAY)), 12);
            feat.setAttrWithMinzoom(Fields.TRACKTYPE, feature.getTag(OsmTags.TRACKTYPE), 12);
            feat.setAttrWithMinzoom(Fields.BRUNNEL, getBrunnel(feature), 12);
        }
    }

    /**
     * Returns a value for {@code brunnel} tag constrained when recognize if feature is a bridge, tunnel or ford.
     * @param feature feature to check
     * @return value 'bridge', 'tunnel', 'ford' or null
     */
    private Object getBrunnel(SourceFeature feature) {
        return Utils.brunnel(feature.getBoolean(OsmTags.BRIDGE), feature.getBoolean(OsmTags.TUNNEL),  feature.getBoolean(
            OsmTags.FORD));
    }

    /**
     * Returns a value for {@code access} tag constrained to a small set of known values from raw OSM data.
     */
    private String getAccess(Object value) {
        return value == null ? null : ACCESS_NO_VALUES.contains(String.valueOf(value)) ? "no" : null;
    }


    /**
     * Check values and
     *  - for yes, 'true', 1 return 1,
     *  - for empty, null, no, 'false' return 0
     *  - for -1, reverse return -1
     * @param value
     * @return
     */
    private Integer getOneWay(Object value) {
        if (Utils.nullOrEmpty(value)) {
            return null;
        }

        switch (String.valueOf(value)) {
            case "yes":
            case "true":
            case "1":
                return 1;
            case "-1":
            case "reverse":
                return -1;
            default:
                return null;
        }
    }
}
