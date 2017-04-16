package com.opuscapita.peppol.commons.validation;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Daniil on 03.05.2016.
 */
public interface BasicValidator {

    @NotNull ContainerMessage validate(@NotNull ContainerMessage cm, @NotNull byte[] data);

}
