package com.opuscapita.peppol.commons.container;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class ContainerMessageSerializer {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().setVersion(1.5).create();

    public ContainerMessage fromJson(@NotNull String json) {
        return gson.fromJson(json, ContainerMessage.class);
    }

    public String toJson(@NotNull ContainerMessage containerMessage) {
        return gson.toJson(containerMessage);
    }
}
