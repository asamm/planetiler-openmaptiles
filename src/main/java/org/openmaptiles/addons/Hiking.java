package org.openmaptiles.addons;

import static com.onthegomap.planetiler.util.MemoryEstimator.CLASS_HEADER_BYTES;
import static com.onthegomap.planetiler.util.MemoryEstimator.POINTER_BYTES;
import static com.onthegomap.planetiler.util.MemoryEstimator.estimateSize;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.reader.osm.OsmElement;
import com.onthegomap.planetiler.reader.osm.OsmRelationInfo;
import com.onthegomap.planetiler.util.MemoryEstimator;
import java.util.List;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;

public class Hiking implements
    Layer,
    ForwardingProfile.OsmRelationPreprocessor,
    OpenMapTilesProfile.OsmAllProcessor {

    private record HikingRelation(
        long id,
        String route,
        String network,
        String name,
        String ref,
        String osmcSymbol
    ) implements OsmRelationInfo {

        @Override
        public long estimateMemoryUsageBytes() {
            return CLASS_HEADER_BYTES + MemoryEstimator.estimateSizeLong(id) + POINTER_BYTES + estimateSize(route) +
                estimateSize(network) + POINTER_BYTES + estimateSize(name) + POINTER_BYTES + estimateSize(ref) +
                POINTER_BYTES + estimateSize(osmcSymbol);
        }
    }

    @Override
    public String name() {
        return "hiking";
    }

    @Override
    public List<OsmRelationInfo> preprocessOsmRelation(OsmElement.Relation relation) {
        if (relation.hasTag("route") && relation.getTag("route").equals("hiking")) {
            return List.of(new HikingRelation(
                relation.id(),
                relation.getString("route"),
                relation.getString("network"),
                relation.getString("name"),
                relation.getString("ref"),
                relation.getString("osmc:symbol")));
        }
        return null;
    }

    @Override
    public void processAllOsm(SourceFeature feature, FeatureCollector features) {
        var relationInfos = feature.relationInfo(HikingRelation.class);

        if (feature.canBeLine() && feature.hasTag("route") && feature.getTag("route").equals("hiking")) {
            FeatureCollector.Feature feat = features.line("hiking")
                .setBufferPixels(4)
                .setMinZoom(10);
            feature.tags().forEach((k, v) -> {
                feat.setAttr(k, v);
            });
        }
    }
}
