package org.openmaptiles.addons.layers;

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
import org.openmaptiles.util.OmtLanguageUtils;

/**
 * Layer for lines with water names of oceans, sea in ZL 0 - 9 from Natural Earth data
 */
public class GeographyName implements
    Layer,
    OpenMapTilesProfile.OsmAllProcessor {

    final double BUFFER_SIZE = 128.0;

    final String LAYER_NAME = "geography_name";

    private final MultiExpression.Index<String> geographyClassMapping;
    private final MultiExpression.Index<String> geographySubClassMapping;

    private final Translations translations;

    public GeographyName(Translations translations, PlanetilerConfig config, Stats stats) {
        this.translations = translations;
        this.geographyClassMapping = LmOutdoorSchema.OutdoorGeographyNamesSchema.GEOGRAPHY_NAMES_CLASS_MAPPING.index();
        this.geographySubClassMapping = LmOutdoorSchema.OutdoorGeographyNamesSchema.GEOGRAPHY_NAMES_SUBCLASS_MAPPING.index();
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        String classValue = geographyClassMapping.getOrElse(sourceFeature, null);
        if (classValue == null) {
            return;
        }

        if (sourceFeature.canBeLine()) {
            var feat = collector.line(LAYER_NAME);
            feat.setBufferPixels(BUFFER_SIZE);
            feat.setMinZoom((int) sourceFeature.getLong(OsmTags.NE_MIN_ZOOM));
            feat.setMaxZoom((int) sourceFeature.getLong(OsmTags.NE_MAX_ZOOM));
            feat.setAttr(LmOutdoorSchema.OutdoorGeographyNamesSchema.Fields.CLASS, classValue);
            feat.setAttr(
                LmOutdoorSchema.OutdoorGeographyNamesSchema.Fields.SUBCLASS,
                geographySubClassMapping.getOrElse(sourceFeature, null));

            feat.setAttr(LmOutdoorSchema.OutdoorGeographyNamesSchema.Fields.RANK, sourceFeature.getLong(OsmTags.RANK));
            feat.putAttrs(OmtLanguageUtils.getNames(sourceFeature.tags(), translations));
        }
    }
}
