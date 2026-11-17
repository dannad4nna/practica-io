import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CrearDirectorios { public static void main(String[] args)
        throws IOException {
    Path directorio = Path.of("salida", "reportes", "procesados");
    Files.createDirectories(directorio);
    Path archivo = directorio.resolve("resultado.txt");
    if (!Files.exists(archivo)) {
        Files.createFile(archivo); }
    System.out.println("Creado: " + archivo.toAbsolutePath()); }
}