package com.integratingfactor.idp.identity.external;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * global app properties provider bean
 * 
 * @author gnulib
 *
 */
public class IdpAppProperties {
    
    static final String EnvHostKey = "app.hostname";
    String appHostName = "https://if-idp.appspot.com";
    static final String EnvAppEmailAddress = "app.email.address";
    String appEmailAddress = "integratingactor.idp@gmail.com";
    static final String EnvAppEmailName = "app.email.name";
    String appEmailName = "Team Integratingfactor.com";
    static final String EnvAppNameKey = "app.name";
    String appName = "Integratingfactor.com IDP Service";
    static final String EnvSystemAppPrefix = "app.system.app";
    static final String EnvSystemTenantPrefix = "app.system.tenant";

    @Autowired
    private Environment env;

    private String getNotNull(String envKey, String old) {
        String prop = env.getProperty(envKey);
        return prop == null ? old : prop;
    }

    @PostConstruct
    public void setup() {
        appHostName = getNotNull(EnvHostKey, appHostName);
        appEmailAddress = getNotNull(EnvAppEmailAddress, appEmailAddress);
        appEmailName = getNotNull(EnvAppEmailName, appEmailName);
        appName = getNotNull(EnvAppNameKey, appName);
    }

    public String getSystemAppProp(String app, String prop) {
        return getNotNull(EnvSystemAppPrefix + "." + app + "." + prop, null);
    }

    public String getSystemTenantProp(String tenant, String prop) {
        return getNotNull(EnvSystemTenantPrefix + "." + tenant + "." + prop, null);
    }

    public String getAppName() {
        return appName;
    }

    public String getAppEmailAddress() {
        return appEmailAddress;
    }

    public String getAppEmailName() {
        return appEmailName;
    }

    public String getAppHostName() {
        return appHostName;
    }

}
