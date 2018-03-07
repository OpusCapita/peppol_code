package com.opuscapita.peppol.commons.template.bean;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base class for any bean that aims to check own parameters.<br/>
 *
 * Usage:<br/>
 * <pre>class Test extends ValuesChecker {
 *     \@Value{"${very.important.file}"}
 *     \@FileMustExist
 *     private String fileToVeryImportantFile;
 * }</pre>
 *
 * <br/>Supported annotation: FileMustExist
 *
 * @author Sergejs.Roze
 */
public class ValuesChecker {
    @Value("${running_tests:false}")
    private boolean isTestConfig = false;

    private static Map<Class, Consumer<String>> checks = new HashMap<>();

    {
        checks.put(FileMustExist.class, this::fileMustExist);
    }

    @PostConstruct
    public void checkValues() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {

            for (Annotation annotation : field.getAnnotations()) {
                if (isTestConfig && disabledInTests(annotation)) {
                    continue;
                }

                Consumer<String> check = checks.get(annotation.annotationType());
                if (check != null) {
                    field.setAccessible(true);
                    check.accept(field.get(this).toString());
                }
            }

        }
    }

    private void fileMustExist(@NotNull String value) {
        if (!new File(value).exists()) {
            throw new IllegalArgumentException("File not found: " + value);
        }
    }

    private boolean disabledInTests(@NotNull Annotation annotation) throws IllegalAccessException {
        Field[] fields = annotation.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals("disableInTests")) {
                return field.getBoolean(annotation);
            }
        }
        return false;
    }

}
