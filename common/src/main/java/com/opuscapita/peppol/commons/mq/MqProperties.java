package com.opuscapita.peppol.commons.mq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class MqProperties {
    private final String host;
    private final int port;
    private final String userName;
    private final String password;

    public MqProperties(@NotNull String host, @Nullable String port, @Nullable String userName, @Nullable String password) {
        this.host = host;
        this.port = Integer.parseInt(port == null ? "5672" : port);
        this.userName = userName;
        this.password = password;
    }

    @NotNull
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Nullable
    public String getUserName() {
        return userName;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        String result = "";
        if (userName != null) {
            result += userName + "@";
        }
        result += host;
        if (port != 0) {
            result += ":" + port;
        }
        return result;
    }
}
