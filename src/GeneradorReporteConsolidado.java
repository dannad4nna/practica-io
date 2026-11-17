import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneradorReporteConsolidado {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Uso: java GeneradorReporteConsolidado <directorio-de-entrada>");
            System.exit(1);
        }

        Path directorioEntrada = Path.of(args[0]);
        if (!Files.isDirectory(directorioEntrada)) {
            System.err.println("No es un directorio válido: " + directorioEntrada);
            System.exit(1);
        }

        Path raiz = directorioEntrada.toAbsolutePath().normalize().getParent();
        Path directorioSalida = raiz.resolve("salida").resolve("reportes");
        Path archivoReporte = directorioSalida.resolve("reporte-consolidado.txt");

        // Selección de archivos mediante glob: solo *.txt
        List<Path> archivos = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directorioEntrada, "*.txt")) {
            for (Path p : stream) {
                if (Files.isRegularFile(p)) {
                    archivos.add(p);
                }
            }
        } catch (IOException e) {
            System.err.println("No fue posible listar el directorio: " + e.getMessage());
            System.exit(1);
        }
        Collections.sort(archivos);

        if (archivos.isEmpty()) {
            System.err.println("No se encontraron archivos .txt en " + directorioEntrada);
            System.exit(1);
        }

        int total = 0, altas = 0, medias = 0, bajas = 0;
        List<String> detallePorArchivo = new ArrayList<>();
        List<String> listaAltas = new ArrayList<>();

        for (Path archivo : archivos) {
            int totalArchivo = 0;
            try (BufferedReader lector = Files.newBufferedReader(archivo)) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    if (linea.isBlank()) {
                        continue;
                    }
                    String[] campos = linea.split("\\|");
                    if (campos.length != 3) {
                        System.err.println(archivo.getFileName() + ": línea ignorada: " + linea);
                        continue;
                    }
                    String id = campos[0].trim();
                    String descripcion = campos[1].trim();
                    String prioridad = campos[2].trim().toUpperCase();

                    switch (prioridad) {
                        case "ALTA" -> {
                            altas++;
                            listaAltas.add(id + " - " + descripcion + "  [" + archivo.getFileName() + "]");
                        }
                        case "MEDIA" -> medias++;
                        case "BAJA" -> bajas++;
                        default -> {
                            System.err.println(archivo.getFileName() + ": prioridad desconocida: " + linea);
                            continue;
                        }
                    }
                    total++;
                    totalArchivo++;
                }
            } catch (IOException e) {
                System.err.println("Error leyendo " + archivo.getFileName() + ": " + e.getMessage());
                continue;
            }
            detallePorArchivo.add(archivo.getFileName() + ": " + totalArchivo + " incidencias");
        }

        try {
            Files.createDirectories(directorioSalida);
            try (PrintWriter reporte = new PrintWriter(Files.newBufferedWriter(archivoReporte))) {
                reporte.println("REPORTE CONSOLIDADO DE INCIDENCIAS");
                reporte.println("==================================");
                reporte.println();
                reporte.println("Archivos procesados: " + archivos.size());
                for (String detalle : detallePorArchivo) {
                    reporte.println("  - " + detalle);
                }
                reporte.println();
                reporte.println("Total de incidencias: " + total);
                reporte.println();
                reporte.println("Prioridad ALTA: " + altas);
                reporte.println("Prioridad MEDIA: " + medias);
                reporte.println("Prioridad BAJA: " + bajas);
                reporte.println();
                reporte.println("INCIDENCIAS DE ALTA PRIORIDAD");
                reporte.println("--");
                reporte.println();
                for (String alta : listaAltas) {
                    reporte.println(alta);
                }
            }
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
            return;
        }

        System.out.println("Archivos .txt procesados: " + archivos.size());
        System.out.println("Reporte consolidado:     " + archivoReporte);
    }
}
