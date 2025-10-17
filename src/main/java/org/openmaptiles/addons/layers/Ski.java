package org.openmaptiles.addons.layers;

import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorSkiSchema.SKI_CLASS_MAPPING;
import static org.openmaptiles.addons.LmOutdoorSchema.OutdoorSkiSchema.SKI_SUBCLASS_MAPPING;
import static org.openmaptiles.addons.layers.LmTransportation.getBrunnel;
import static org.openmaptiles.addons.layers.LmTransportation.getOneWay;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import java.util.Map;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.addons.LmOutdoorSchema;
import org.openmaptiles.addons.LmUtils;
import org.openmaptiles.addons.OsmTags;
import org.openmaptiles.util.OmtLanguageUtils;
import org.slf4j.LoggerFactory;

public class Ski implements Layer,
    LmOutdoorSchema.OutdoorSkiSchema,
    OpenMapTilesProfile.OsmAllProcessor{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Ski.class);

    private static final double BUFFER_SIZE = 4.0;

    private static final String LAYER_NAME = "ski";

    private final MultiExpression.Index<String> classMapping;

    private final MultiExpression.Index<String> subClassMapping;

    private final Translations translations;

    public Ski(Translations translations, PlanetilerConfig config, Stats stats) {

        this.classMapping = SKI_CLASS_MAPPING.index();

        this.subClassMapping = SKI_SUBCLASS_MAPPING.index();

        this.translations = translations;
    }

    @Override
    public String name() {
        return LAYER_NAME;
    }

    @Override
    public void processAllOsm(SourceFeature sourceFeature, FeatureCollector collector) {

        String classValue = classMapping.getOrElse(sourceFeature, null);
        if (classValue == null) {
            //LOGGER.debug("Skipping element {} with tags {}", sourceFeature.id(), sourceFeature.tagsAsJson());
            // when the class is not found, skip this feature because it's not an outdoor feature
            return;
        }

        String subClassValue = getSubClassValue(classValue, sourceFeature);

        var feat = collector.anyGeometry(LAYER_NAME);

        // create a new feature in the outdoor layer as a point
        feat.setBufferPixels(BUFFER_SIZE);
        feat.setAttr(LmOutdoorSchema.OutdoorPoiSchema.Fields.CLASS, classValue);
        feat.setAttr(LmOutdoorSchema.OutdoorPoiSchema.Fields.SUBCLASS, subClassValue);
        feat.putAttrs(getPisteName(sourceFeature));

        // get original value of ref tag
        feat.setAttr(Fields.REF, sourceFeature.getString("ref"));

        // original value for piste:difficulty tag
        feat.setAttr(Fields.DIFFICULTY, sourceFeature.getString("piste:difficulty"));

        // original value for piste:grooming tag
        feat.setAttr(Fields.GROOMING, sourceFeature.getString("piste:grooming"));

        // for lines check for brunnel and oneway and lit
        if (sourceFeature.canBeLine()){
            feat.setAttrWithMinzoom(LmOutdoorSchema.LmTrasportationSchema.Fields.BRUNNEL, getBrunnel(sourceFeature),14);
            feat.setAttrWithMinzoom(LmOutdoorSchema.LmTrasportationSchema.Fields.ONEWAY, getPisteOneWay(sourceFeature), 14);
            feat.setAttrWithMinzoom(Fields.LIT, getLit(sourceFeature), 14);
        }

        feat.setMinZoom(getZoomLevel(classValue));
    }

    /**
     * Get the names for piste features. If piste:name is present and no default name is set, use piste:name as default name.
     * @param sourceFeature
     * @return map of names
     */
    private Map<String, Object> getPisteName(SourceFeature sourceFeature) {

        Map<String, Object>  names = OmtLanguageUtils.getNames(sourceFeature.tags(), translations);

        if (sourceFeature.hasTag("piste:name") && !names.containsKey("name")) {
            // add piste:name as default name
            names.put("name", sourceFeature.getString("piste:name"));
        }
        return names;
    }

    /**
     * Get the lit value for piste features. First check the lit tag and if not present check the piste:lit tag.
     * @param sourceFeature the source feature
     * @return 1 for lit, null if piste without lighting
     */
    private Integer getLit(SourceFeature sourceFeature) {
        // original boolean value for lit tag or piste:lit tag if lit tag is not present
        if (sourceFeature.hasTag("lit")) {
            return LmUtils.getBoolAsPositiveInt(sourceFeature,"lit") ;
        }
        return LmUtils.getBoolAsPositiveInt(sourceFeature,"piste:lit");
    }

    /**
     * Get the one way value for piste features. First check the piste specific oneway tag and if not present
     * check the general oneway tag.
     * @param sourceFeature the source feature
     * @return 1 for one way, -1 for one way against the direction, 0 for not one way or null if not applicable
     */
    private Integer getPisteOneWay(SourceFeature sourceFeature) {

        if (sourceFeature.hasTag(OsmTags.ONEWAY_PISTE)) {
            return getOneWay(sourceFeature.getTag(OsmTags.ONEWAY_PISTE));
        }

        return getOneWay(sourceFeature.getTag(OsmTags.ONEWAY));
    }

    /**
     * Get the sub class value for the given feature and class value. Only specific classes have sub classes.
     * @param classValue the class value
     * @param feature the feature to get the sub class value for
     * @return the sub class value or null if the feature doesn't fit sub class
     */
    private String getSubClassValue(String classValue, SourceFeature feature) {

        if (CLASSES_WITH_SUBCLASSES.contains(classValue)) {
            if (classValue.equals("lift")){
                // for lift return original value of aerialway tag
                return feature.getString("aerialway");
            }
            if (classValue.equals("avalanche")) {
                // for avalanche return original value of avalanche tag if present
                return feature.getString("avalanche_protection");
            }
            if (classValue.equals("snowmobile")) {
                // if
            }

            return subClassMapping.getOrElse(feature, null);
        }

        return null;
    }

    private int getZoomLevel(String classValue) {

        // switch case by classValue
        switch (classValue) {
            case "ski_resort":
                return 10;
            case "lift":
                return 12;
            case "downhill":
                return 12;
            default:
                return 12;
        }
    }
}
