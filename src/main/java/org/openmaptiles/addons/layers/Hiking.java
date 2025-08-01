package org.openmaptiles.addons.layers;

import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorCyclingSchema.IS_NODE_NETWORK;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.addons.OsmTags;

public class Hiking implements
    Layer,
    OpenMapTilesProfile.OsmAllProcessor,
    LmOutdoorSchema.OutdoorHikeSchema {

    final double BUFFER_SIZE = 4.0;

    final int DEF_MIN_ZOOM = 11;

    final String LAYER_NAME = "hiking";

    private final MultiExpression.Index<String> highwayMapping;

    private final PlanetilerConfig config;

    public Hiking(Translations translations, PlanetilerConfig config, Stats stats) {

        this.highwayMapping = LM_HIGHWAY_MAPPING.index();
        this.config = config;
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
            feat.setMinZoom(DEF_MIN_ZOOM);

            // get original value of ref tag
            feat.setAttr(Fields.REF, sourceFeature.getString("ref"));
            feat.setAttrWithMinzoom(Fields.NAME, sourceFeature.getString("name"), 14);
            feat.setAttr(Fields.NETWORK, sourceFeature.getString("network"));
            feat.setAttr(Fields.OSMC_COLOR, sourceFeature.getString("osmc_color"));
            feat.setAttr(Fields.OSMC_FOREGROUND, sourceFeature.getString("osmc_foreground"));
            feat.setAttr(Fields.OSMC_ORDER, sourceFeature.getLong("osmc_order"));
            feat.setAttr(Fields.HIGHWAY, this.highwayMapping.getOrElse(sourceFeature, null));
            feat.setAttrWithMinzoom(LmOutdoorSchema.LmTrasportationSchema.Fields.BRUNNEL,
                LmTransportation.getBrunnel(sourceFeature), 12);
            feat.setAttrWithMinzoom(LmOutdoorSchema.LmTrasportationSchema.Fields.ONEWAY,
                LmTransportation.getOneWay(sourceFeature.getTag(
                    OsmTags.ONEWAY)), 14);
            feat.setAttr(Fields.ROUTE_SPEC, getRouteSpecification(sourceFeature)); // "educational" or null


        } else if (sourceFeature.isPoint()) {
            // NL BE Network nodes
            if (IS_NODE_NETWORK.evaluate(sourceFeature) && sourceFeature.hasTag("rwn_ref")) {

                var feat = collector.point(LAYER_NAME);
                feat.setBufferPixels(BUFFER_SIZE);
                feat.setMinZoom(12);
                feat.setAttr("network_type", sourceFeature.getString("network:type"));
                feat.setAttr(Fields.RWN_REF, sourceFeature.getString("rwn_ref"));
            }
        }
    }

    /**
     * Check if the source feature is educational route and return the route specification.
     *
     * @param sourceFeature source feature to check
     * @return route specification "educational" or null if not applicable
     */
    private Object getRouteSpecification(SourceFeature sourceFeature) {
        return IS_EDUCATIONAL_EXPRESSION.evaluate(sourceFeature) ? "educational" : null;
    }

//  Do not merge hiking lines because it cause incorrect offset of hiking lines in the map.
//    @Override
//    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
//        return LmUtils.mergeTransportationLines(zoom, BUFFER_SIZE, items, config);
//    }
}
