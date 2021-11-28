package pucrs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        var id = Integer.parseInt(args[0]);
        var totalSlaves = Integer.parseInt(args[1]);
        Path path = Path.of("src/main/java/config.txt");
        try {
            var configurations = Files.lines(path)
                    .map(Configuration::new)
                    .collect(Collectors.toList());

            var configuration = configurations.stream()
                    .filter(config -> config.getId() == id)
                    .findFirst()
                    .orElseThrow();

            if (id == 0)
                new Master(configuration, totalSlaves, configurations);
            else
                new Slave(configuration, configurations);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
