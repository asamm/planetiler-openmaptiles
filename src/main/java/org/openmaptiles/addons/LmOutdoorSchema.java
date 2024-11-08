package org.openmaptiles.addons;

import com.onthegomap.planetiler.expression.Expression;
import com.onthegomap.planetiler.expression.MultiExpression;
import java.util.List;
import java.util.Set;

public class LmOutdoorSchema {

    public interface LmTrasportationSchema {

        final class Fields {

            public static final String CLASS = "class";
            public static final String ONEWAY = "oneway";
            public static final String ACCESS = "access";
            public static final String TRACKTYPE = "tracktype";
            public static final String BRUNNEL = "brunnel";
        }

        final class FieldValues {

            public static final String CLASS_TRACK = "track";
            public static final String BRUNNEL_BRIDGE = "bridge";
            public static final String BRUNNEL_TUNNEL = "tunnel";
            public static final String BRUNNEL_FORD = "ford";
            public static final Set<String> BRUNNEL_VALUES = Set.of("bridge", "tunnel", "ford");
        }

        Expression IS_TRACK_EXPRESSION = Expression.matchAny("highway","track");
    }

    public interface OutdoorPoiSchema {

        final class Fields {

            public static final String TOURISM = "tourism";
            public static final String LEISURE = "leisure";
            public static final String AMENITY = "amenity";
            public static final String SHOP = "shop";
            public static final String NATURAL = "natural";
            public static final String LANDUSE = "landuse";
            public static final String HISTORIC = "historic";
        }

//        Expression IS_POI_EXPRESSION = Expression.or(
//            Expression.matchAny("amenity","bench", "shelter")
//        );

        public static final MultiExpression<String> OUTDOOR_FILTER = MultiExpression.of(List.of(
            MultiExpression.entry("information", Expression.matchAny(
                "information", "board", "guidepost", "map", "office")),
            MultiExpression.entry("tourism", Expression.matchAny(
                "tourism", "information", "camp_site", "attraction", "viewpoint", "picnic_site")),
            MultiExpression.entry("tower:type", Expression.matchAny(
                "tower:type", "observation", "communication", "watchtower")),
            MultiExpression.entry("shelter_type", Expression.matchAny(
                "shelter_type", "basic_hut", "lean_to", "public_transport", "weather_shelter")),
            MultiExpression.entry("amenity", Expression.matchAny(
                "amenity", "bench", "shelter", "toilets", "drinking_water", "fountain", "waste_basket", "recycling", "picnic_table", "parking")),
            MultiExpression.entry("leisure", Expression.matchAny(
                "leisure", "park", "playground", "sports_centre", "pitch", "swimming_pool", "garden", "dog_park", "nature_reserve")),
            MultiExpression.entry("sport", Expression.matchAny(
                "sport", "climbing", "swimming", "cycling", "skiing", "hiking", "fishing", "golf", "tennis")),
            MultiExpression.entry("natural", Expression.matchAny(
                "natural", "wood", "tree", "peak", "spring", "cave_entrance", "coastline", "water", "rock", "scrub")),
            MultiExpression.entry("waterway", Expression.matchAny(
                "waterway", "river", "stream", "waterfall", "canal", "ditch")),
            MultiExpression.entry("historic", Expression.matchAny(
                "historic", "castle", "ruins", "monument", "archaeological_site", "memorial", "wayside_cross", "wayside_shrine")),
            MultiExpression.entry("castle_type", Expression.matchAny(
                "castle_type", "fortress", "defensive", "manor", "stately")),
            MultiExpression.entry("man_made", Expression.matchAny(
                "man_made", "lighthouse", "windmill", "water_tower", "observatory")),
            MultiExpression.entry("rental", Expression.matchAny(
                "rental", "bicycle", "canoe", "kayak", "ski", "surfboard")),
            MultiExpression.entry("fireplace", Expression.matchAny(
                "fireplace", "yes")) // assuming `fireplace=yes`
        ));


        public static final MultiExpression<String> OUTDOOR_CLASS_MAPPING = MultiExpression.of(List.of(
            MultiExpression.entry("board", Expression.matchAny("information", "board")),
            MultiExpression.entry("map", Expression.matchAny("information", "map")),
            MultiExpression.entry("info_office", Expression.matchAny("information", "office")),
            MultiExpression.entry("guidepost", Expression.matchAny("information", "guidepost")),
            MultiExpression.entry("observation_tower", Expression.matchAny("tower:type", "observation")),
            MultiExpression.entry("shelter", Expression.matchAny("amenity", "shelter")),
            MultiExpression.entry("hut", Expression.matchAny("shelter_type", "basic_hut")),
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
            MultiExpression.entry("rental", Expression.matchAny("rental", "bicycle", "canoe", "kayak", "ski", "surfboard")),
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
            MultiExpression.entry("monument", Expression.matchAny("historic", "monument"))
        ));
    }

}
