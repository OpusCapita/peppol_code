package com.opuscapita.peppol.commons.container.route;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The complete route from end-to-end.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("unused")
public class Route implements Serializable {
    public final static String NEXT = ":";
    final static String PARAMETERS_SEPARATOR = ":";
    final static String PAIRS_SEPARATOR = ",";
    final static String VALUE_SEPARATOR = "=";
    private List<String> endpoints = new ArrayList<>();
    private String description;
    private String mask;
    private String source;

    private int current = 0;

    public Route() {}

    /**
     * Copy constructor.
     *
     * @param other the other route to be used as an example
     */
    public Route(@NotNull Route other) {
        this.endpoints = other.getEndpoints();
        this.description = other.getDescription();
        this.mask = other.getMask();
        this.source = other.getSource();
    }

    /**
     * Returns next process in the route and makes next process current.
     *
     * @return the next process if any or null when this was the end process
     */
    @Nullable
    public String pop() {
        if (current >= endpoints.size()) {
            return null;
        }
        String result = endpoints.get(current++);

        if (result.contains(PARAMETERS_SEPARATOR)) {
            return result.substring(0, result.indexOf(PARAMETERS_SEPARATOR));
        }

        return result;
    }

    /**
     * Returns the next endpoint name and all additional parameters.
     * The syntax is:<br/><code>
     *     next_route:parameter1=aaa,parameter2,parameter3=ccc
     *     </code>
     * <br/>
     * The next queue name is returned as the map element with the name Route.NEXT
     *
     * @return the map containing the next endpoint name and all parameters, or empty map if the end of the route is reached
     */
    @NotNull
    public Map<String, String> popFull() {
        Map<String, String> result = new HashMap<>();
        if (current >= endpoints.size()) {
            return result;
        }

        String next = endpoints.get(current++);
        if (next == null) {
            return result;
        }

        if (next.contains(PARAMETERS_SEPARATOR)) {
            if (next.indexOf(PARAMETERS_SEPARATOR) < next.length()) {
                String list = next.substring(next.indexOf(PARAMETERS_SEPARATOR) + 1);
                String[] pairs = StringUtils.split(list, PAIRS_SEPARATOR);

                for (String pair : pairs) {
                    if (pair.contains(VALUE_SEPARATOR)) {
                        String[] keyValue = StringUtils.split(pair, VALUE_SEPARATOR);
                        result.put(keyValue[0], keyValue[1]);
                    } else {
                        result.put(pair, null);
                    }
                }

            }
            result.put(NEXT, next.substring(0, next.indexOf(PARAMETERS_SEPARATOR)));
        } else {
            result.put(NEXT, next);
        }

        return result;
    }

    @Override
    public String toString() {
        String result = description;
        if (mask != null) {
            result += " (" + mask + ") ";
        }
        result += "[ " + source + " ";
        for (String endpoint : endpoints) {
            result += endpoint + " ";
        }
        return result + "]";
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getMask() {
        return mask;
    }

    @SuppressWarnings("SameParameterValue")
    public void setMask(@Nullable String mask) {
        this.mask = mask;
    }

    @NotNull
    public String getSource() {
        return source;
    }

    public void setSource(@NotNull String source) {
        this.source = source;
    }

    @NotNull
    private List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(@NotNull List<String> endpoints) {
        this.endpoints = endpoints;
    }

}
