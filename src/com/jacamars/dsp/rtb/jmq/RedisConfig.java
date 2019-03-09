package com.jacamars.dsp.rtb.jmq;

public class RedisConfig {

    String address;
    String topic;
    String host;
    String auth;
    int port = 6379;
    int timeout = 5000;

    public RedisConfig(String saddress) throws Exception {

        address = saddress;
        saddress = saddress.replace("redis://", "");
        String[] parts = saddress.split("&");
        int i = 0;

        for (String part : parts) {
            if (i != 0) {
                String[] t = part.split("=");
                if (t.length != 2)
                    throw new Exception("Bad redis option at " + part);
                switch (t[0]) {
                    case "host":
                        host = t[1];
                        break;
                    case "topic":
                        topic = t[1];
                        break;
                    case "auth":
                        auth = t[1];
                        break;
                    case "port":
                        port = Integer.parseInt(t[1]);
                        break;
                    case "timeout":
                        timeout = Integer.parseInt(t[1]);
                        break;
                    default:
                        throw new Exception("Unknown redis option: " + part);
                }
            } else {
                host = part;
                if (host.contains("=")) {
                    String [] tuple = host.split("=");
                    host = tuple[1];
                } else {
                    String [] tuple = host.split(":");
                    host = tuple[0];
                    port = Integer.parseInt(tuple[1]);
                }
            }
            i++;
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getAuth() {
        return auth;
    }

    public String getTopic() {
        return topic;
    }
}
