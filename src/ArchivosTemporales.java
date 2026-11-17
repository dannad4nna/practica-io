import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArchivosTemporales { public static void main(String[] args)
        throws IOException { Path temporal = Files.createTempFile("dsi-", ".tmp");
    System.out.println("Creado" + temporal);
    Path directorioTemporal = Files.createTempDirectory("dsi-");
    System.out.println("Creado " + directorioTemporal); }
}