package com.opuscapita.peppol.validator;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergejs.Roze
 */
public interface ValidationController {

    ContainerMessage validate(@NotNull ContainerMessage cm) throws Exception;

}
