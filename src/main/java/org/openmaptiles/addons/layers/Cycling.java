package org.openmaptiles.addons.layers;

import static org.openmaptiles.addons.LmOutdoorSchema.LmTrasportationSchema;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorCyclingSchema;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorHikeSchema;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import java.util.List;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmUtils;
import org.openmaptiles.addons.OsmTags;

public class Cycling implements
    Layer,
    OpenMapTilesProfile.OsmAllProcessor,
    OutdoorCyclingSchema,
    OutdoorHikeSchema {

    final double BUFFER_SIZE = 4.0;

    final int DEF_MIN_ZOOM = 9;

    private final PlanetilerConfig config;

    final String LAYER_NAME = "cycling";

    private final MultiExpression.Index<String> highwayMapping;

    public Cycling(Translations translations,  PlanetilerConfig config, Stats stats) {
        this.highwayMapping = LM_HIGHWAY_MAPPING.index();
        this.config = config;
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        if (sourceFeature.canBeLine()) {
            boolean isCyslingRoute = IS_CYCLING_ROUTE_EXPRESSION.evaluate(sourceFeature);
            boolean isCyslingSimple = IS_CYCLING_SIMPLE_EXPRESSION.evaluate(sourceFeature);
            boolean isMtbRoute = IS_MTB_ROUTE_EXPRESSION.evaluate(sourceFeature);
            boolean isMtbSimple = IS_MTB_SIMPLE_EXPRESSION.evaluate(sourceFeature);

            if (isCyslingRoute || isCyslingSimple || isMtbRoute || isMtbSimple) {

                var feat = collector.line(LAYER_NAME);
                feat.setBufferPixels(BUFFER_SIZE);
                feat.setMinZoom(DEF_MIN_ZOOM);

                feat.setAttr(OutdoorHikeSchema.Fields.REF, sourceFeature.getString("ref"));
                if (isCyslingRoute || isMtbRoute) {
                    // name set only for cycling routes not for simple cycling paths
                    feat.setAttrWithMinzoom(OutdoorHikeSchema.Fields.NAME, sourceFeature.getString("name"), 14);
                }
                feat.setAttr(OutdoorHikeSchema.Fields.NETWORK, sourceFeature.getString("network"));
                feat.setAttr("route", sourceFeature.getString("route"));

                feat.setAttr(
                    OutdoorHikeSchema.Fields.HIGHWAY, this.highwayMapping.getOrElse(sourceFeature, null));
                feat.setAttr(LmTrasportationSchema.Fields.BRUNNEL, LmTransportation.getBrunnel(sourceFeature));
                feat.setAttrWithMinzoom(LmTrasportationSchema.Fields.ONEWAY, LmTransportation.getOneWay(sourceFeature.getTag(OsmTags.ONEWAY)),14);

                if (isMtbRoute || isMtbSimple) {
                    feat.setAttr("mtb", 1);
                    feat.setAttr(OutdoorCyclingSchema.Fields.MTB_SCALE,
                        sourceFeature.getString("mtb:scale"));
                }

            }
        } else if (sourceFeature.isPoint()) {
            // NL BE Network nodes
            if (IS_NODE_NETWORK.evaluate(sourceFeature) && sourceFeature.hasTag("rcn_ref")) {

                var feat = collector.point(LAYER_NAME);
                feat.setBufferPixels(BUFFER_SIZE);
                feat.setMinZoom(12);
                feat.setAttr("network_type", sourceFeature.getString("network:type"));
                feat.setAttr(OutdoorCyclingSchema.Fields.RCN_REF, sourceFeature.getString("rcn_ref"));
            }
        }
    }
//    Do not merge cycling lines because incorrect offset (cycling can collide with hiking lines).
//    @Override
//    public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) {
//        return LmUtils.mergeTransportationLines(zoom, BUFFER_SIZE, items, config);
//    }
}
