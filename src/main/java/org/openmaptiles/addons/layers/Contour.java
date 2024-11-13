package org.openmaptiles.addons.layers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;

public class Contour implements Layer, OpenMapTilesProfile.OsmAllProcessor {

    public static String LAYER_NAME = "contour";

    final double BUFFER_SIZE = 4.0;

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature feature, FeatureCollector features) {
        if (feature.canBeLine() && feature.hasTag("contour") && feature.hasTag("ele")) {

            float ele = Float.parseFloat((String) feature.getTag("ele"));

            FeatureCollector.Feature feat = features.line(LAYER_NAME);
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setAttrWithMinzoom("height", ele, 11);
            feat.setAttrWithMinzoom("nth_line", get_maptiler_nth_value(ele), 11);
        }
    }

    /**
     * Based on the ele value, return the nth line value for the contour line. Decide by division without the remainder
     * of the ele value 100m - 10 50m - 5 20m - 2 10m - 1 5m - 0
     * <p>
     * It's not exactly the same as the maptiler implementation, but it's a good approximation (because MapTiler
     * calculates the nth line value based on the zoom) https://docs.maptiler.com/schema/contours/
     *
     * @param ele
     * @return
     */
    private int get_maptiler_nth_value(float ele) {

        if (ele % 100 == 0) {
            return 10;
        } else if (ele % 50 == 0) {
            return 5;
        } else if (ele % 20 == 0) {
            return 2;
        } else if (ele % 10 == 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
