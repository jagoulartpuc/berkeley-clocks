package pucrs;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration implements Serializable {
    private static final Logger log = Logger.getLogger(Configuration.class.getName());
    private int id;
    private String host;
    private int port;
    private LocalTime time;
    private LocalTime delay;

    public Configuration(String configurationLine) {
        try {
            String[] config = configurationLine.split(";");
            if (config.length != 5) throw new Exception("Linha de configuração incorreta: " + configurationLine);
            this.id = Integer.parseInt(config[0]);
            this.host = config[1];
            this.port = Integer.parseInt(config[2]);
            this.time = LocalTime.parse(config[3]);
            this.delay = LocalTime.parse(config[4]);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public Configuration(int id, LocalTime time, LocalTime delay) {
        this.id = id;
        this.time = time;
        this.delay = delay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalTime getDelay() {
        return delay;
    }

    public void setDelay(LocalTime delay) {
        this.delay = delay;
    }
}
