package org.openmaptiles.addons.layers;

import static com.onthegomap.planetiler.collection.FeatureGroup.SORT_KEY_BITS;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorHikeSchema.IS_PARKING_EXPRESSION;
import static org.openmaptiles.util.Utils.coalesce;
import static org.openmaptiles.util.Utils.nullIfLong;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.SortKey;
import com.onthegomap.planetiler.util.Translations;
import com.onthegomap.planetiler.util.ZoomFunction;
import java.util.List;
import java.util.Set;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.addons.LmUtils;
import org.openmaptiles.addons.OsmTags;
import org.openmaptiles.generated.OpenMapTilesSchema;
import org.openmaptiles.util.OmtLanguageUtils;
import org.openmaptiles.util.Utils;

public class LmTransportation implements Layer,
    LmOutdoorSchema.LmTrasportationSchema,
    OpenMapTilesProfile.OsmAllProcessor,
    ForwardingProfile.LayerPostProcessor{

    private static final double BUFFER_SIZE = 4.0;

    private static final String LAYER_NAME = "lm_transportation";


    private static final Set<String> ACCESS_NO_VALUES = Set.of(
        "private", "no"
    );

    public static final Set<Integer> ONEWAY_VALUES = Set.of(-1, 1);

    public static final ZoomFunction.MeterToPixelThresholds MIN_LENGTH = ZoomFunction.meterThresholds()
        .put(10, 500)
        .put(11, 200)
        .put(11, 100);

    private final MultiExpression.Index<String> assistedTrailMapping;

    private final PlanetilerConfig config;

    public LmTransportation(Translations translations, PlanetilerConfig config, Stats stats) {

        this.config = config;

        this.assistedTrailMapping = LmOutdoorSchema.LmTrasportationSchema.ASSISTED_TRAIL_MAPPING.index();
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector features) {

        boolean is_via_ferrata = IS_VIA_FERRATA_EXPRESSION.evaluate(sourceFeature);
        boolean is_path = IS_PATH_EXPRESSION.evaluate(sourceFeature);
        boolean is_track = IS_TRACK_EXPRESSION.evaluate(sourceFeature);
        boolean is_parking = IS_PARKING_EXPRESSION.evaluate(sourceFeature);

        // Check if the source feature can be a line and if it is either a via ferrata or a track
        // (when processLoMapsTracks is enabled in configuration)
        if (sourceFeature.canBeLine() &&
            (is_via_ferrata || is_path || IS_TRACK_EXPRESSION.evaluate(sourceFeature))) {

            FeatureCollector.Feature feat = features.line(LAYER_NAME);
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setMinZoom(12);
            feat.setAttr(Fields.CLASS, sourceFeature.getTag(OsmTags.HIGHWAY));
            feat.setAttr(Fields.ACCESS, getAccess(sourceFeature.getTag(OsmTags.ACCESS)));
            feat.setAttr(Fields.ONEWAY, getOneWay(sourceFeature.getTag(OsmTags.ONEWAY)));
            feat.setAttr(Fields.BRUNNEL, getBrunnel(sourceFeature));
            feat.setAttrWithMinzoom(Fields.LAYER, nullIfLong(sourceFeature.getLong("layer"), 0), 9);

            if (is_track){
                feat.setAttr(Fields.TRACKTYPE, sourceFeature.getTag(OsmTags.TRACKTYPE));
                // TODO consider set tracktype as subclass
                // feat.setAttr(Fields.SUBCLASS, "track");

            }
            feat.setAttr(Fields.TRACKTYPE, sourceFeature.getTag(OsmTags.TRACKTYPE));
            if (is_via_ferrata){
                feat.setMinZoom(13);
                feat.setAttr(Fields.VIA_FERRATA_SCALE, sourceFeature.getString(Fields.VIA_FERRATA_SCALE));
                feat.setAttr(Fields.SUBCLASS, "via_ferrata");
            }

            if (is_path){
                feat.setAttr(Fields.SUBCLASS, "path");
                feat.setAttrWithMinzoom(Fields.ASSISTED_TRAIL, this.assistedTrailMapping.getOrElse(sourceFeature, null), 14 );
                feat.setAttrWithMinzoom(Fields.TRAIL_VISIBILITY, sourceFeature.getString(Fields.TRAIL_VISIBILITY), 14);
            }
        }

        if (is_parking && sourceFeature.canBePolygon()){
            FeatureCollector.Feature feat = features.polygon(LAYER_NAME);
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setMinZoom(14);
            feat.setAttr(Fields.CLASS, "parking");
            feat.setAttr(Fields.ACCESS, getAccess(sourceFeature.getTag(OsmTags.ACCESS)));
            // name of parking lot is part of OpenMapTilesSchema
        }
    }

    @Override
    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
        return LmUtils.mergeTransportationLines(zoom, BUFFER_SIZE, items, config);
    }

    /**
     * Returns a value for {@code brunnel} tag constrained when recognize if feature is a bridge, tunnel or ford.
     * @param feature feature to check
     * @return value 'bridge', 'tunnel', 'ford' or null
     */
    public static Object getBrunnel(SourceFeature feature) {
        return Utils.brunnel(feature.getBoolean(OsmTags.BRIDGE), feature.getBoolean(OsmTags.TUNNEL),  feature.getBoolean(
            OsmTags.FORD));
    }

    /**
     * Returns a value for {@code access} tag constrained to a small set of known values from raw OSM data.
     */
    public static String getAccess(Object value) {
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
    public static Integer getOneWay(Object value) {
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
