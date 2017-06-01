package com.opuscapita.peppol.test.tools.smoke.checks;

import com.opuscapita.peppol.test.tools.smoke.checks.subtypes.*;

import javax.activation.UnsupportedDataTypeException;
import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class ChecksFactory {

    public static Check createCheck(String moduleName, String type, Map<String, Object> params) throws UnsupportedDataTypeException {
        switch(type){
            case "link":
                return new LinkCheck(moduleName, params);
            case "reference":
                return new HealthCheck(moduleName,params);
            case "DB-connection":
                return new DbConnectionCheck(moduleName,params);
            case "MQ-connection":
                return new MqConnectionCheck(moduleName,params);
            case "File-System-connection":
                return new FileSystemCheck(moduleName,params);
            case "module-configuration":
                return new ModuleConfigurationCheck(moduleName,params);
            case "queue":
                return new QueuesCheck(moduleName,params);
            default :
                throw new UnsupportedDataTypeException("No Check found for specified type: "+moduleName);
        }
    }
}
