package no.difi.vefa.validator;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.checker.SvrlXsltChecker;
import no.difi.vefa.validator.checker.XsdChecker;
import no.difi.vefa.validator.declaration.SbdhDeclaration;
import no.difi.vefa.validator.declaration.UblDeclaration;
import no.difi.vefa.validator.renderer.XsltRenderer;

/**
 * Created by bambr on 17.24.1.
 */
public class DifiValidatorBuilder {
    public static Validator getValidatorInstance(Source source) throws ValidatorException {
        Validator result = new Validator();
        if(source != null) {
            result.setSource(source);
        }
        Class<? extends Checker>[] checkers = new Class[]{XsdChecker.class, SvrlXsltChecker.class};
        Class<? extends Renderer>[] renderers = new Class[]{XsltRenderer.class};
        Declaration[] declarations = new Declaration[] { new UblDeclaration(), new SbdhDeclaration()};
        result.load(checkers, renderers, declarations);

        return result;
    }
}
