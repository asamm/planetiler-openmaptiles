package org.openmaptiles.addons;

import static org.openmaptiles.util.Utils.coalesce;

import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import java.util.List;
import org.openmaptiles.addons.layers.LmTransportation;
import org.openmaptiles.generated.OpenMapTilesSchema;

public class LmUtils {

    private static final String LIMIT_MERGE_TAG = "__limit_merge";

    public static List<VectorTile.Feature> mergeTransportationLines(
        int zoom, double bufferSize , List<VectorTile.Feature> items, PlanetilerConfig config) {

        double tolerance = config.tolerance(zoom);
        double minLength = coalesce(LmTransportation.MIN_LENGTH.apply(zoom), 0).doubleValue();

        // don't merge road segments with oneway tag
        int onewayId = 1;
        for (var item : items) {
            var oneway = item.tags().get(OpenMapTilesSchema.Transportation.Fields.ONEWAY);
            if (oneway instanceof Number n && LmTransportation.ONEWAY_VALUES.contains(n.intValue())) {
                item.tags().put(LIMIT_MERGE_TAG, onewayId++);
            }
        }

        var merged = FeatureMerge.mergeLineStrings(items, minLength, tolerance, bufferSize);

        for (var item : merged) {
            // remove the custom no merge tag from merged items
            item.tags().remove(LIMIT_MERGE_TAG);
        }
        return merged;
    }
}
