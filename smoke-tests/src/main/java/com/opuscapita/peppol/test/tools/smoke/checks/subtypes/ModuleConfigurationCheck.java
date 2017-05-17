package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

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
    private String[] configurableConfiguration;

    public ModuleConfigurationCheck(String moduleName, Map<String, String> params) {
        super(moduleName,params);
    }


    @Override
    public CheckResult run() {
        boolean result;
        String oneModuleError = "";
        String fullError = "";
        String details = "";

        try{
            host = rawConfig.get("host").replaceAll("/$","");
            profile = rawConfig.get("profile");
            String[] modules = rawConfig.get("module-names").trim().split(" ");
            configurableConfiguration = rawConfig.get("expected-configurations").trim().split(" ");

            for (String module : modules){
                URL url = new URL(host + "/" + module + "/" + profile);
                URLConnection urlConn = url.openConnection();
                InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                JsonObject jsonObj = new Gson().fromJson(is, JsonObject.class);

                Set<String> configuration = new HashSet<>();
                //collecting all configuration files retrieved from the server for current module
                for(JsonElement source : jsonObj.get("propertySources").getAsJsonArray()){
                    URL remoteConfigFile = new URL(source.getAsJsonObject().get("name").getAsString());
                    String configurationFile = Paths.get(remoteConfigFile.getFile()).getFileName().toString();
                    configuration.add(configurationFile);
                }
                oneModuleError = testConfiguration(configuration, getExpectedConfigurationForModule(module));
                if(!oneModuleError.isEmpty())
                    fullError += module + " failed: " + oneModuleError + " ";
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            return new CheckResult(name, false, "Configuration check failed " + ex, rawConfig);
        }
        result = fullError.isEmpty() ? true : false;
        details = oneModuleError.isEmpty() ? "Configuration check successful" : oneModuleError;
        return new CheckResult(name, result, details, rawConfig);
    }

    //comparing expected configuration files and the actual files found on server
    private String testConfiguration(Set<String> configuration, List<String> expectedConfiguration) {
        if (configuration.size() != expectedConfiguration.size())
            return "Module configuration size doesn't match, expected: " + expectedConfiguration.size() + " real: "+ configuration.size();
        return configuration.containsAll(expectedConfiguration) ? "" : "Module configuration doesn't match!";
    }

    private List<String> getExpectedConfigurationForModule(String module) {
        List<String> moduleExpectedConfiguration = Arrays.asList(configurableConfiguration);
        moduleExpectedConfiguration.add("application-" + profile + ".yml");
        moduleExpectedConfiguration.add(module + "-" + profile + ".yml");
        return  moduleExpectedConfiguration;
    }
}
