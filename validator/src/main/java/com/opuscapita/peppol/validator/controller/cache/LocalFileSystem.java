package com.opuscapita.peppol.validator.controller.cache;

import com.opuscapita.peppol.commons.template.bean.FileMustExist;
import com.opuscapita.peppol.commons.template.bean.ValuesChecker;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Sergejs.Roze
 */
@Component
public class LocalFileSystem extends ValuesChecker implements FileSource {
    @Value("${peppol.validator.rules.directory}")
    @FileMustExist
    private String directory;

    @Override
    public File getFile(@NotNull String fileName) {
        return new File(directory, fileName);
    }

}
