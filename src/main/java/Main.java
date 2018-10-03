
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 *
 * @author Matthias Ramsauer
 *
 * Copyright (c) 2018 by matthias-ramsauer.de to present. All rights reserved.
 */
public class Main extends Thread {
    
    public static Process javaProcess;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Files.walk(Paths.get("server", "plugins"))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .peek(System.out::println)
            .forEach(File::delete);
        
        Files.createDirectories(Paths.get("server", "plugins"));
        Files.copy(Paths.get("target", "SkinCache-1.0-SNAPSHOT.jar"), Paths.get("server", "plugins", "SkinCache-1.0-SNAPSHOT.jar"));
        
        Runtime.getRuntime().addShutdownHook(new Main());
        
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", "spigot-1.8.8.jar");
        builder.directory(new File("server"));
        builder.inheritIO();
        javaProcess = builder.start();
        javaProcess.waitFor();
    }

    @Override
    public void run() {
        javaProcess.destroy();
    }
    
    
}
