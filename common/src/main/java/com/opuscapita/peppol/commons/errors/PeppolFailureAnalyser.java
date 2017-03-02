package com.opuscapita.peppol.commons.errors;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Created by bambr on 16.20.12.
 */
public class PeppolFailureAnalyser extends AbstractFailureAnalyzer {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, Throwable cause) {
        if (cause.getMessage().contains("Could not resolve placeholder")) {
            return new FailureAnalysis(cause.getMessage(), "Please, ask Edgars to add it to configuration (and give him some chocolate cookies).", cause);
        }
        return null;
    }
}
