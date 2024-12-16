package org.openmaptiles.addons;

import static com.onthegomap.planetiler.expression.Expression.and;
import static com.onthegomap.planetiler.expression.Expression.matchAny;
import static com.onthegomap.planetiler.expression.Expression.or;

import com.onthegomap.planetiler.expression.Expression;
import com.onthegomap.planetiler.expression.MultiExpression;
import com.onthegomap.planetiler.geo.GeometryType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LmOutdoorSchema {

    static class SchemaFields {
        public static final String CLASS = "class";
        public static final String SUBCLASS = "subclass";;
    }

    public interface LmTrasportationSchema {

        final class Fields extends SchemaFields {
            public static final String ONEWAY = "oneway";
            public static final String ACCESS = "access";
            public static final String TRACKTYPE = "tracktype";
            public static final String BRUNNEL = "brunnel";
            public static final String VIA_FERRATA_SCALE = "via_ferrata_scale";
        }

        final class FieldValues {

            public static final String CLASS_TRACK = "track";
            public static final String BRUNNEL_BRIDGE = "bridge";
            public static final String BRUNNEL_TUNNEL = "tunnel";
            public static final String BRUNNEL_FORD = "ford";
            public static final Set<String> BRUNNEL_VALUES = Set.of("bridge", "tunnel", "ford");
        }

        Expression IS_TRACK_EXPRESSION = Expression.matchAny("highway", "track");

        Expression IS_VIA_FERRATA_EXPRESSION = Expression.matchAny("highway", "via_ferrata");
    }

    public interface OutdoorPoiSchema {

        final class Fields extends SchemaFields {
            public static final String ELE = "ele";
        }

        MultiExpression<String> OUTDOOR_POI_CLASS_MAPPING = MultiExpression.of(List.of(
            MultiExpression.entry("board", Expression.matchAny("information", "board")),
            MultiExpression.entry("map", Expression.matchAny("information", "map")),
            MultiExpression.entry("info_office", Expression.matchAny("information", "office")),
            MultiExpression.entry("guidepost", Expression.matchAny("information", "guidepost")),
            MultiExpression.entry("observation_tower", Expression.matchAny("tower:type", "observation")),
            MultiExpression.entry("shelter", Expression.matchAny("amenity", "shelter")),
            MultiExpression.entry("hut", Expression.matchAny("tourism", "alpine_hut", "wilderness_hut")),
            MultiExpression.entry("camp_site", Expression.matchAny("tourism", "camp_site")),
            MultiExpression.entry("picnic_site", Expression.matchAny("tourism", "picnic_site")),
            MultiExpression.entry("viewpoint", Expression.matchAny("tourism", "viewpoint")),
            MultiExpression.entry("waterfall", Expression.matchAny("waterway", "waterfall")),
            MultiExpression.entry("cave_entrance", Expression.matchAny("natural", "cave_entrance")),
            MultiExpression.entry("spring", Expression.matchAny("natural", "spring")),
            MultiExpression.entry("drinking_water", Expression.matchAny("amenity", "drinking_water")),
            MultiExpression.entry("bench", Expression.matchAny("amenity", "bench")),
            MultiExpression.entry("fireplace", Expression.matchAny("fireplace", "yes")),
            MultiExpression.entry("mountain_rescue", Expression.matchAny("emergency", "mountain_rescue")),
            MultiExpression.entry("first_aid", Expression.matchAny("emergency", "first_aid")),
            MultiExpression.entry("ski_school", Expression.matchAny("amenity", "ski_school")),
            MultiExpression.entry("rental",
                Expression.matchAny("rental", "bicycle", "canoe", "kayak", "ski", "surfboard")),
            MultiExpression.entry("ice_rink", Expression.matchAny("leisure", "ice_rink")),
            MultiExpression.entry("ski_playground", Expression.matchAny("leisure", "ski_playground")),
            MultiExpression.entry("snow_cannon", Expression.matchAny("man_made", "snow_cannon")),
            MultiExpression.entry("biathlon", Expression.matchAny("sport", "biathlon")),
            MultiExpression.entry("bobsleigh", Expression.matchAny("sport", "bobsleigh")),
            MultiExpression.entry("curling", Expression.matchAny("sport", "curling")),
            MultiExpression.entry("ice_hockey", Expression.matchAny("sport", "ice_hockey")),
            MultiExpression.entry("ice_skating", Expression.matchAny("sport", "ice_skating")),
            MultiExpression.entry("skiing", Expression.matchAny("sport", "skiing")),
            MultiExpression.entry("ski_jumping", Expression.matchAny("sport", "ski_jumping")),
            MultiExpression.entry("castle", Expression.matchAny("historic", "castle")),
            MultiExpression.entry("fortress", Expression.matchAny("castle_type", "fortress")),
            MultiExpression.entry("ruins", Expression.matchAny("historic", "ruins")),
            MultiExpression.entry("memorial", Expression.matchAny("historic", "memorial")),
            MultiExpression.entry("monument", Expression.matchAny("historic", "monument")),

            // ASAMM ADDITIONAL TO MAPTILER OUTDOOR
            MultiExpression.entry("caravan_site", Expression.matchAny("tourism", "caravan_site")),
            MultiExpression.entry("bunker", Expression.matchAny("military", "bunker")),
            MultiExpression.entry("stone", Expression.matchAny("natural", "stone")),
            MultiExpression.entry("rock", Expression.matchAny("natural", "rock"))
        ));

        MultiExpression<String> OUTDOOR_POI_SUBCLASS_MAPPING = MultiExpression.of(List.of(
            MultiExpression.entry("shelter_lean_to", Expression.matchAny("shelter_type", "lean_to")),
            MultiExpression.entry("picnic_shelter", Expression.matchAny("shelter_type", "picnic_shelter")),
            MultiExpression.entry("rock_shelter", Expression.matchAny("shelter_type", "rock_shelter")),
            MultiExpression.entry("weather_shelter", Expression.matchAny("shelter_type", "weather_shelter")),
            MultiExpression.entry("basic_hut", Expression.matchAny("shelter_type", "basic_hut")),
            MultiExpression.entry("alpine_hut", Expression.matchAny("tourism", "alpine_hut")),
            MultiExpression.entry("wilderness_hut", Expression.matchAny("tourism", "wilderness_hut")),
            MultiExpression.entry("spring", Expression.matchAny("natural", "spring")),
            MultiExpression.entry("ski", Expression.matchAny("amenity", "ski_rental")),
            MultiExpression.entry("sled", Expression.matchAny("amenity", "sled_rental")),
            // TODO MapTiler scheme defines probably rental skates need to find the key/value for such sub-class

            // ASAMM ADDITIONAL TO MAPTILER OUTDOOR
            MultiExpression.entry("shelter_public_transport", Expression.matchAny("shelter_type", "public_transport")),
            MultiExpression.entry("caravan_site", Expression.matchAny("tourism", "caravan_site")),
            MultiExpression.entry("pillbox", Expression.matchAny("bunker_type", "pillbox"))
            ));

        // these classes have sub-classes that should be included in the output
        Set<String> CLASSES_WITH_SUBCLASSES = new HashSet<>(Set.of("shelter", "hut", "spring", "tourism", "rental", "bunker"));
    }

    public interface OutdoorSkiSchema {

        final class Fields extends SchemaFields {
            public static final String REF = "ref";
            public static final String DIFFICULTY = "difficulty";
            public static final String GROOMING = "grooming";
            public static final String LIT = "lit";
        }

        MultiExpression<String> SKI_CLASS_MAPPING = MultiExpression.of(List.of(
            MultiExpression.entry("ski_resort", or(
                Expression.matchAny("landuse", "winter_sports"), and(Expression.matchAny("leisure", "sports_centre"), Expression.matchAny("sport", "ski")))),
            MultiExpression.entry("station", Expression.matchAny("aerialway", "station", "halt")),
            MultiExpression.entry("pylon", Expression.matchAny("aerialway", "pylon")),
            MultiExpression.entry("lift", Expression.matchAny("aerialway", "chair_lift", "drag_lift", "gondola", "cable_car", "mixed-lift", "t-bar", "j-bar", "platter", "rope_tow", "magic_carpet")),
            MultiExpression.entry("avalanche", or(Expression.matchAny("man_made", "snow_fence", "snow_net"),Expression.matchField("avalanche_protection"))),
            MultiExpression.entry("downhill", Expression.matchAny("piste:type", "downhill")),
            MultiExpression.entry("nordic", Expression.matchAny("piste:type", "nordic")),
            MultiExpression.entry("skitour", Expression.matchAny("piste:type", "skitour")),
            MultiExpression.entry("playground", Expression.matchAny("piste:type", "playground")),
            MultiExpression.entry("snow_park", Expression.matchAny("piste:type", "snow_park")),
            MultiExpression.entry("ski_jump", Expression.matchAny("piste:type", "ski_jump")),
            MultiExpression.entry("ski_jump_landing", Expression.matchAny("piste:type", "ski_jump_landing")),
            MultiExpression.entry("hike", Expression.matchAny("piste:type", "hike")),
            MultiExpression.entry("sled", Expression.matchAny("piste:type", "sled")),
            MultiExpression.entry("sleigh", Expression.matchAny("piste:type", "sleigh")),
            MultiExpression.entry("ice_skate", Expression.matchAny("piste:type", "ice_skate")),
            MultiExpression.entry("fatbike", Expression.matchAny("piste:type", "fatbike")),
            MultiExpression.entry("snowkite", Expression.matchAny("piste:type", "snowkite")),
            MultiExpression.entry("connection", Expression.matchAny("piste:type", "path", "track", "service")),
            MultiExpression.entry("snowshoe", Expression.matchAny("snowshoe", "snowshoe"))
        ));

        MultiExpression<String> SKI_SUBCLASS_MAPPING = MultiExpression.of(List.of(
            MultiExpression.entry("avalanche_dam", Expression.matchAny("avalanche_protection", "dam")),
            MultiExpression.entry("fence", Expression.matchAny("man_made", "snow_fence")),
            MultiExpression.entry("net", (Expression.matchAny("man_made", "snow_net")))
        ));

        // these classes have sub-classes that should be included in the output
        Set<String> CLASSES_WITH_SUBCLASSES = new HashSet<>(Set.of("lift", "avalanche"));

    }

    public interface OutdoorHikeSchema {

        final class Fields extends SchemaFields {
            public static final String REF = "ref";
            public static final String NETWORK = "network";
            public static final String HIGHWAY = "highway";
            public static final String NAME = "name";
            public static final String OSMC_COLOR = "osmc_color";
            public static final String OSMC_ORDER = "osmc_order";
            public static final String OSMC_FOREGROUND = "osmc_foreground";
        }

        Expression IS_HIKE_EXPRESSION = Expression.matchAny("route", "hiking");

        MultiExpression<String> LM_HIGHWAY_MAPPING = MultiExpression.of(List.of(
            MultiExpression.entry("motorway", or(
                matchAny("lm_highway", "motorway", "motorway_link"),
                matchAny("highway", "motorway", "motorway_link"))),
            MultiExpression.entry("trunk", or(
                matchAny("lm_highway", "trunk", "trunk_link"),
                matchAny("highway", "trunk", "trunk_link"))),
            MultiExpression.entry("primary", or(
                matchAny("lm_highway", "primary", "primary_link"),
                matchAny("highway", "primary", "primary_link"))),
            MultiExpression.entry("secondary", or(
                matchAny("lm_highway", "secondary", "secondary_link"),
                matchAny("highway", "secondary", "secondary_link"))),
            MultiExpression.entry("tertiary", or(
                matchAny("lm_highway", "tertiary", "tertiary_link"),
                matchAny("highway", "tertiary", "tertiary_link"))),
            MultiExpression.entry("minor", or(
                matchAny("lm_highway", "unclassified", "residential", "living_street", "road"),
                matchAny("highway", "unclassified", "residential", "living_street", "road"))),
            MultiExpression.entry("track", or(
                matchAny("lm_highway", "track"),
                matchAny("highway", "track"))),
            MultiExpression.entry("path", or(
                matchAny("lm_highway", "pedestrian", "path", "footway", "cycleway", "steps", "bridleway", "corridor", "via_ferrata"),
                matchAny("highway", "pedestrian", "path", "footway", "cycleway", "steps", "bridleway", "corridor", "via_ferrata")))
        ));
    }

    public interface OutdoorCyclingSchema {

        final class Fields extends SchemaFields {
            public static final String MTB_SCALE = "mtb_scale";

        }

        Expression IS_CYCLING_EXPRESSION = Expression.matchAny("route", "bicycle");
        Expression IS_MTB_EXPRESSION = or(Expression.matchAny("route", "mtb"),
            Expression.matchAny("bicycle", "mtb", "yes","designated", "official"));
    }
}
