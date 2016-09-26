package com.opuscapita.peppol.support.ui.config;

import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.5.2
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class WebPackageFilter extends RegexPatternTypeFilter {
    public WebPackageFilter() {
        super(Pattern.compile("com\\.opuscapita\\.peppol\\.support\\.ui\\.config\\..*"));
    }
}
