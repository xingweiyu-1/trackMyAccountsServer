package com.trackmycounts.server.util;

import java.math.BigDecimal;
import java.util.Map;

/** H2 等库返回的 Map 列名可能是全小写，统一做忽略大小写取值。 */
public final class MapValueHelper {

    private MapValueHelper() {}

    public static Object get(Map<String, ?> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        if (map.containsKey(key)) {
            return map.get(key);
        }
        for (Map.Entry<String, ?> e : map.entrySet()) {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) {
                return e.getValue();
            }
        }
        return null;
    }

    public static String getString(Map<String, ?> map, String key) {
        Object val = get(map, key);
        return val == null ? null : val.toString();
    }

    public static BigDecimal toBigDecimal(Object val) {
        return val != null ? new BigDecimal(val.toString()) : BigDecimal.ZERO;
    }

    public static BigDecimal getBigDecimal(Map<String, ?> map, String key) {
        return toBigDecimal(get(map, key));
    }
}
