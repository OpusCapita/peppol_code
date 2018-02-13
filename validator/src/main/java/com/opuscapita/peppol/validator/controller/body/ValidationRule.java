package com.opuscapita.peppol.validator.controller.body;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Single validation rule, like "use given xsl file and ignore suppress given set of xsl errors".
 *
 * Field "suppress" accepts comma-separated string of rules to be suppressed.
 *
 * @author Sergejs.Roze
 */
public class ValidationRule {
    private String profile;
    private List<String> rules;
    private List<String> suppress;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    @Nullable
    public List<String> getSuppress() {
        return suppress;
    }

    @SuppressWarnings("unused")
    public void setSuppress(List<String> suppress) {
        this.suppress = suppress;
    }

}
