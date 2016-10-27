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
    /*http://localhost:441/events-persistence/development*/
    public CheckResult run() {
        boolean result = true;

        try{
            host = rawConfig.get("host").replaceAll("/$","");
            profile = rawConfig.get("profile");
            String[] modules = rawConfig.get("module-names").trim().split(" ");

            for (String module : modules){
                URL url = new URL(host + "/" + module + "/" + profile);
                URLConnection urlConn = url.openConnection();
                InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                JsonObject jsonObj = new Gson().fromJson(is, JsonObject.class);

                Set<String> configurationFiles = new HashSet<>();
                for(JsonElement source : jsonObj.get("propertySources").getAsJsonArray()){
                    URL remoteConfigFile = new URL(source.getAsJsonObject().get("name").getAsString());
                    String configurationFile = Paths.get(remoteConfigFile.getFile()).getFileName().toString();
                    configurationFiles.add(configurationFile);
                }

                if(!testConfiguration(configurationFiles,getValidConfigFilesForModule(module)))
                    result = false;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            return new CheckResult(name, false, "configuration check failed " + ex, rawConfig);
        }

        return new CheckResult(name, result, "configuration check performed", rawConfig);
    }

    private boolean testConfiguration(Set<String> configurationFiles, Set<String> expectedConfigurationFiles) {
        if (configurationFiles.size() != expectedConfigurationFiles.size())
            return false;
        return configurationFiles.containsAll(expectedConfigurationFiles);
    }

    private Set<String> getValidConfigFilesForModule(String module) {
        return new HashSet<>(
                Arrays.asList("application.yml",
                        "application-" + profile + ".yml",
                        module + "-" + profile + ".yml"));
    }
}
