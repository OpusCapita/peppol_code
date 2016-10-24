package com.opuscapita.peppol.test.tools.smoke.checks;

import com.opuscapita.peppol.test.tools.smoke.checks.subtypes.*;

import javax.activation.UnsupportedDataTypeException;
import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class ChecksFactory {

    public static Check createCheck(String moduleName, String type, Map<String, String> params) throws UnsupportedDataTypeException {
        switch(type){
            case "link":
                return new LinkCheck().init(moduleName, params);
            case "reference":
                return new HealthCheck().init(moduleName,params); // TODO: clarify this
            case "DB-connection":
                return new DbConnectionCheck().init(moduleName,params);
            case "MQ-connection":
                return new MqConnectionCheck().init(moduleName,params);
            case "File-System-connection":
                return new FileSystemCheck().init(moduleName,params);
            case "module-configuration":
                return new ModuleConfigurationCheck().init(moduleName,params);
            default :
                throw new UnsupportedDataTypeException("No Check found for specified type: "+moduleName);
        }
    }
}
