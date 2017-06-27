package com.opuscapita.peppol.support.ui.domain;

import org.apache.log4j.Logger;

import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.4.12
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class TableParameters {
    private static final Logger logger = Logger.getLogger(TableParameters.class);
    private Integer page;
    private Integer count;
    private Map<String, String> filter = new HashMap<String, String>();
    private Map<String, String> sorting = new HashMap<String, String>();
    private String search;
    private boolean exactSearch = false;

    public TableParameters(String uriQuery) {
        try {
            String[] params = uriQuery.split("&");
            for (String param : params) {
                try {
                    String key = param.split("=")[0];
                    String value = param.split("=")[1];
                    if (key.equals("page")) {
                        this.page = Integer.valueOf(value);
                    } else if (key.equals("count")) {
                        this.count = Integer.valueOf(value);
                    } else if (key.startsWith("sorting")) {
                        int leftBracket = key.indexOf("[") + 1;
                        int rightBracket = key.indexOf("]");
                        this.sorting.put(key.substring(leftBracket, rightBracket), value);
                    } else if (key.startsWith("filter")) {
                        int leftBracket = key.indexOf("[") + 1;
                        int rightBracket = key.indexOf("]");
                        this.filter.put(key.substring(leftBracket, rightBracket), value);
                    } else if (key.equals("search")) {
                        this.search = value;
                    } else if (key.equals(("exactSearch"))){
                        this.exactSearch = Boolean.valueOf(value);
                    }
                } catch (Exception e) {
                    logger.warn("Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {

        }
    }

    public TableParameters(UriInfo uriInfo) {
        this(uriInfo.getRequestUri().getQuery());
    }

    public TableParameters(Map<String, String> requestParams) {
        this(getQueryFromRequestParams(requestParams));
    }

    private static String getQueryFromRequestParams(Map<String, String> requestParams) {
        StringBuilder query = new StringBuilder();
        Iterator it = requestParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            query
                    .append(pairs.getKey())
                    .append("=")
                    .append(pairs.getValue())
                    .append("&");
            it.remove();
        }
        return query.toString();
    }


    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public Map<String, String> getSorting() {
        return sorting;
    }

    public void setSorting(Map<String, String> sorting) {
        this.sorting = sorting;
    }

    public String getSearch() {
        return search;
    }

    public boolean isExactSearch() {
        return exactSearch;
    }

    public void setExactSearch(boolean exactSearch) {
        this.exactSearch = exactSearch;
    }
}
