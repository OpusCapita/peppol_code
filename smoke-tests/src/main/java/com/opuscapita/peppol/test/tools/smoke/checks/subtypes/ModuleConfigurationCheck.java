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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by bambr on 16.20.10.
 */
public class ModuleConfigurationCheck extends Check {

    String profile, host;

    public ModuleConfigurationCheck(String moduleName, Map<String, String> params) {
        super(moduleName,params);
    }


    @Override
    public CheckResult run() {
        boolean result;
        String errorMsg = "";
        String details = "";

        try{
            host = rawConfig.get("host").replaceAll("/$","");
            profile = rawConfig.get("profile");
            String[] modules = rawConfig.get("module-names").trim().split(" ");

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
                errorMsg = testConfiguration(configuration, getExpectedConfigurationForModule(module));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            return new CheckResult(name, false, "Configuration check failed " + ex, rawConfig);
        }
        result = errorMsg.isEmpty() ? true : false;
        details = errorMsg.isEmpty() ? "Configuration check successful" : errorMsg;
        return new CheckResult(name, result, details, rawConfig);
    }

    //comparing expected configuration files and the actual files found on server
    private String testConfiguration(Set<String> configuration, Set<String> expectedConfiguration) {
        if (configuration.size() != expectedConfiguration.size())
            return "Module configuration size doesn't match, expected: " + expectedConfiguration.size() + " real: "+ configuration.size();
        return configuration.containsAll(expectedConfiguration) ? "" : "Module configuration doesn't match! check module-names !";
    }

    private Set<String> getExpectedConfigurationForModule(String module) {
        return new HashSet<>(
                Arrays.asList("application.yml",
                        "application-" + profile + ".yml",
                        module + "-" + profile + ".yml"));
    }
}
