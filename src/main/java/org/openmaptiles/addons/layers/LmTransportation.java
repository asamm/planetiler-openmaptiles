package org.openmaptiles.addons.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import java.util.Set;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.addons.OsmTags;
import org.openmaptiles.util.Utils;

public class LmTransportation implements Layer,
    LmOutdoorSchema.LmTrasportationSchema,
    OpenMapTilesProfile.OsmAllProcessor {

    private static final double BUFFER_SIZE = 4.0;

    private static final String LAYER_NAME = "lm_transportation";

    private static final Set<String> ACCESS_NO_VALUES = Set.of(
        "private", "no"
    );

    /**
     * Config option to process tracks and track type in LoMaps style
     */
    private final boolean processLoMapsTracks;

    public LmTransportation(Translations translations, PlanetilerConfig config, Stats stats) {
        this.processLoMapsTracks = config.arguments().getBoolean(
            "lomaps_add_tracks",
            "LoMaps Transportation: add tracks and tracktypes (because tracktype is missing in OpenMapTiles)",
            false);
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector features) {

        boolean is_via_ferrata = IS_VIA_FERRATA_EXPRESSION.evaluate(sourceFeature);

        // Check if the source feature can be a line and if it is either a via ferrata or a track
        // (when processLoMapsTracks is enabled in configuration)
        if (sourceFeature.canBeLine() &&
            (is_via_ferrata ||  (this.processLoMapsTracks && IS_TRACK_EXPRESSION.evaluate(sourceFeature)))) {

            FeatureCollector.Feature feat = features.line(LAYER_NAME);
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setMinZoom(12);
            feat.setAttr(Fields.CLASS, sourceFeature.getTag(OsmTags.HIGHWAY));
            feat.setAttr(Fields.ACCESS, getAccess(sourceFeature.getTag(OsmTags.ACCESS)));
            feat.setAttr(Fields.ONEWAY, getOneWay(sourceFeature.getTag(OsmTags.ONEWAY)));
            feat.setAttr(Fields.TRACKTYPE, sourceFeature.getTag(OsmTags.TRACKTYPE));
            feat.setAttr(Fields.BRUNNEL, getBrunnel(sourceFeature));

            if (is_via_ferrata){
                feat.setMinZoom(13);
                feat.setAttr(Fields.VIA_FERRATA_SCALE, sourceFeature.getString(Fields.VIA_FERRATA_SCALE));
                feat.setAttr(Fields.SUBCLASS, sourceFeature.getTag(OsmTags.HIGHWAY));
            }
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
