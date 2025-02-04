package org.openmaptiles.addons.layers;

import static org.openmaptiles.util.Utils.brunnel;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.generated.OpenMapTilesSchema;

public class Cycling implements
    Layer,
    OpenMapTilesProfile.OsmAllProcessor,
    LmOutdoorSchema.OutdoorCyclingSchema,
    LmOutdoorSchema.OutdoorHikeSchema {

    final double BUFFER_SIZE = 4.0;

    final String LAYER_NAME = "cycling";

    private final MultiExpression.Index<String> highwayMapping;

    public Cycling() {
        this.highwayMapping = LM_HIGHWAY_MAPPING.index();
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        if (sourceFeature.canBeLine()) {
            boolean isCysling = IS_CYCLING_EXPRESSION.evaluate(sourceFeature);
            boolean isMTB = IS_MTB_EXPRESSION.evaluate(sourceFeature);

            if (isCysling || isMTB) {

                var feat = collector.line(LAYER_NAME);
                feat.setBufferPixels(BUFFER_SIZE);
                feat.setMinZoom(12);

                feat.setAttr(LmOutdoorSchema.OutdoorHikeSchema.Fields.REF, sourceFeature.getString("ref"));
                feat.setAttr(LmOutdoorSchema.OutdoorHikeSchema.Fields.NAME, sourceFeature.getString("name"));
                feat.setAttr(LmOutdoorSchema.OutdoorHikeSchema.Fields.NETWORK, sourceFeature.getString("network"));
                feat.setAttr("route", sourceFeature.getString("route"));

                feat.setAttr(
                    LmOutdoorSchema.OutdoorHikeSchema.Fields.HIGHWAY, this.highwayMapping.getOrElse(sourceFeature, null));
                feat.setAttr(OpenMapTilesSchema.Transportation.Fields.BRUNNEL,
                    brunnel(sourceFeature.getBoolean("bridge"), sourceFeature.getBoolean("tunnel"),
                        sourceFeature.getBoolean("ford")));

                if (isMTB) {
                    feat.setAttr("mtb", 1);
                    feat.setAttr(LmOutdoorSchema.OutdoorCyclingSchema.Fields.MTB_SCALE,
                        sourceFeature.getString("mtb:scale"));
                }

            }
        } else if (sourceFeature.isPoint()) {
            // NL BE Network nodes
            if (IS_NODE_NETWORK.evaluate(sourceFeature) && sourceFeature.hasTag("rcn_ref")) {

                var feat = collector.point(LAYER_NAME);
                feat.setBufferPixels(BUFFER_SIZE);
                feat.setMinZoom(14);
                feat.setAttr("network_type", sourceFeature.getString("network:type"));
                feat.setAttr(LmOutdoorSchema.OutdoorCyclingSchema.Fields.RCN_REF, sourceFeature.getString("rcn_ref"));
            }
        }
    }
}
