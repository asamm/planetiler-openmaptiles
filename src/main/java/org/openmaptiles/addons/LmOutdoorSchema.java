package org.openmaptiles.addons;

import com.onthegomap.planetiler.expression.Expression;
import java.util.Set;

public class LmOutdoorSchema {

    public interface LmTrasportationSchema {

        final class Tags {
            public static final String HIGHWAY = "highway";

            public static final String ONEWAY = "oneway";

            public static final String ACCESS = "access";

            public static final String TRACKTYPE = "tracktype";

            public static final String BRIDGE = "bridge";

            public static final String TUNNEL = "tunnel";

            public static final String FORD = "ford";
        }

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
}
