package org.openmaptiles.addons;

/**
 * Contains constants for OpenStreetMap (OSM) tags used in the project, 
 * including additional tags for the MapTiler Outdoor Schema.
 */
final public class OsmTags {

    // Existing tags
    /** access: https://wiki.openstreetmap.org/wiki/Key:access */
    public static final String ACCESS = "access";

    /** bridge: https://wiki.openstreetmap.org/wiki/Key:bridge */
    public static final String BRIDGE = "bridge";

    /** ford: https://wiki.openstreetmap.org/wiki/Key:ford */
    public static final String FORD = "ford";

    /** highway: https://wiki.openstreetmap.org/wiki/Key:highway */
    public static final String HIGHWAY = "highway";

    /** oneway: https://wiki.openstreetmap.org/wiki/Key:oneway */
    public static final String ONEWAY = "oneway";

    /** tracktype: https://wiki.openstreetmap.org/wiki/Key:tracktype */
    public static final String TRACKTYPE = "tracktype";

    /** tunnel: https://wiki.openstreetmap.org/wiki/Key:tunnel */
    public static final String TUNNEL = "tunnel";

    // New tags for MapTiler Outdoor Schema

    /** information (boards, maps, etc.): https://wiki.openstreetmap.org/wiki/Key:information */
    public static final String INFORMATION = "information";

    /** tourism (e.g., campsites, attractions): https://wiki.openstreetmap.org/wiki/Key:tourism */
    public static final String TOURISM = "tourism";

    /** tower:type (e.g., observation, communication): https://wiki.openstreetmap.org/wiki/Key:tower:type */
    public static final String TOWER_TYPE = "tower:type";

    /** shelter_type (e.g., basic_hut, weather_shelter): https://wiki.openstreetmap.org/wiki/Key:shelter_type */
    public static final String SHELTER_TYPE = "shelter_type";

    /** amenity (e.g., toilets, parking, drinking_water): https://wiki.openstreetmap.org/wiki/Key:amenity */
    public static final String AMENITY = "amenity";

    /** leisure (e.g., park, garden, sports centre): https://wiki.openstreetmap.org/wiki/Key:leisure */
    public static final String LEISURE = "leisure";

    /** sport (e.g., climbing, swimming, cycling): https://wiki.openstreetmap.org/wiki/Key:sport */
    public static final String SPORT = "sport";

    /** natural (e.g., wood, rock, spring): https://wiki.openstreetmap.org/wiki/Key:natural */
    public static final String NATURAL = "natural";

    /** waterway (e.g., river, stream, waterfall): https://wiki.openstreetmap.org/wiki/Key:waterway */
    public static final String WATERWAY = "waterway";

    /** historic sites (e.g., castle, ruins): https://wiki.openstreetmap.org/wiki/Key:historic */
    public static final String HISTORIC = "historic";

    /** castle_type (e.g., fortress, manor): https://wiki.openstreetmap.org/wiki/Key:castle_type */
    public static final String CASTLE_TYPE = "castle_type";

    /** man-made structures (e.g., lighthouse, water tower): https://wiki.openstreetmap.org/wiki/Key:man_made */
    public static final String MAN_MADE = "man_made";

    /** rental services (e.g., bicycles, skis): https://wiki.openstreetmap.org/wiki/Key:rental */
    public static final String RENTAL = "rental";

    // Power generation tags

    /** power infrastructure: https://wiki.openstreetmap.org/wiki/Key:power */
    public static final String POWER = "power";

    /** generator source (nuclear, wind, hydro, etc.): https://wiki.openstreetmap.org/wiki/Key:generator:source */
    public static final String GENERATOR_SOURCE = "generator:source";

    /** generator method (fission, wind_turbine, water-storage, etc.): https://wiki.openstreetmap.org/wiki/Key:generator:method */
    public static final String GENERATOR_METHOD = "generator:method";

    /** generator type (PWR, horizontal_axis, francis_turbine, etc.): https://wiki.openstreetmap.org/wiki/Key:generator:type */
    public static final String GENERATOR_TYPE = "generator:type";

    /** fireplace areas (e.g., campfire pits): https://wiki.openstreetmap.org/wiki/Tag:tourism=camp_site#Fireplace */
    public static final String FIREPLACE = "fireplace";
}

