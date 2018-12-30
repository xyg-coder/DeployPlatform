package osu.xinyuangui.springbootvuejs.util;

import java.util.HashMap;
import java.util.Map;

public class StringUtil {
    public static Map<String, String> queryStringParse(String query) {
        Map<String, String> result = new HashMap<>();
        String[] splits = query.split("&");
        for (String split : splits) {
            String[] eqs = split.split("=");
            if (eqs.length > 1) {
                result.put(eqs[0], eqs[1]);
            }
        }
        return result;
    }
}
