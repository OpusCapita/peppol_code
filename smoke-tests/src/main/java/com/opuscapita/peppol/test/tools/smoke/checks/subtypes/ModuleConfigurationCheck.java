package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;
import com.opuscapita.peppol.test.tools.smoke.util.StringJoinUtils;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by bambr on 16.20.10.
 */
public class ModuleConfigurationCheck extends Check {
    private String profile, host;
    private List<String> genericConfiguration;

    public ModuleConfigurationCheck(String moduleName, Map<String, Object> params) {
        super(moduleName,params);
    }


    @Override
    public CheckResult run() {
        boolean result;
        Optional<String> oneModuleError;
        String fullError = "";
        String details = "";

        try{
            host = ((String)rawConfig.get("host")).replaceAll("/$","");
            profile = (String)rawConfig.get("profile");
            List<String> modules = ((List<String>)rawConfig.get("module-names"));
            genericConfiguration = (List<String>)rawConfig.get("expected-configurations");

            for (String module : modules){
                URL url = new URL(host + "/" + module + "/" + profile);
                URLConnection urlConn = url.openConnection();
                InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                JsonObject jsonObj = new Gson().fromJson(is, JsonObject.class);

                Set<String> receivedConfiguration = new HashSet<>();
                //collecting all configuration files retrieved from the server for current module
                for(JsonElement source : jsonObj.get("propertySources").getAsJsonArray()){
                    URL remoteConfigFile = new URL(source.getAsJsonObject().get("name").getAsString());
                    String configurationFile = Paths.get(remoteConfigFile.getFile()).getFileName().toString();
                    receivedConfiguration.add(configurationFile);
                }
                oneModuleError = testConfiguration(receivedConfiguration, module);
                if(oneModuleError.isPresent())
                    fullError += module.toUpperCase() + " Check failed: " + oneModuleError.get() + " ";
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            return new CheckResult(moduleName, false, "Configuration check failed " + ex, rawConfig);
        }
        result = fullError.isEmpty() ? true : false;
        details = fullError.isEmpty() ? "Configuration check successful" : fullError;
        return new CheckResult(moduleName, result, details, rawConfig);
    }

    //comparing expected configuration files and the actual files found on server
    private Optional<String> testConfiguration(Set<String> configuration, String module) {
        List<String> expectedConfiguration = getExpectedConfigurationForModule(module);
        Optional<String> error = Optional.empty();
        if (configuration.size() < expectedConfiguration.size() || !configuration.containsAll(expectedConfiguration))
            error = Optional.of("Module configuration doesn't match, expected: [" + StringJoinUtils.join(expectedConfiguration, ", ")  +
                    "] received configuration: [" + StringJoinUtils.join(configuration, ", ") + "]");
        if(!configuration.contains(module + "-" + profile + ".yml") && !configuration.contains(module + ".yml"))
            error = Optional.of("Mandatory configuration missing: " + module + "-" + profile + ".yml or " + module + ".yml should be present");
        return error;
    }


    private List<String> getExpectedConfigurationForModule(String module) {
        List<String> moduleExpectedConfiguration = new ArrayList<>(genericConfiguration);
        moduleExpectedConfiguration.add("application-" + profile + ".yml");
        //moduleExpectedConfiguration.add(module + "-" + profile + ".yml");
        //moduleExpectedConfiguration.add(module + ".yml"); //Accepting second option without the profile
        return  moduleExpectedConfiguration;
    }
}
