import java.io.*;
import java.nio.file.*;

public class GeneradorReporteIncidencias {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Uso: java GeneradorReporteIncidencias <ruta-incidencias.txt>");
            return;
        }

        Path archivoEntrada = Path.of(args[0]);


        if (!Files.exists(archivoEntrada)) {
            System.err.println("El archivo no existe: " + archivoEntrada);
            return;
        }

        try {
            System.out.println("Archivo: " + archivoEntrada.getFileName());
            System.out.println("Ruta absoluta: " + archivoEntrada.toAbsolutePath());
            System.out.println("Tamaño: " + Files.size(archivoEntrada) + " bytes");
            System.out.println("Última modificación: " + Files.getLastModifiedTime(archivoEntrada));
        } catch (IOException e) {
            System.err.println("No fue posible leer los metadatos: " + e.getMessage());
        }

        Path directorioSalida = Path.of("mesa-ayuda", "salida", "reportes");
        try {
            Files.createDirectories(directorioSalida);
        } catch (IOException e) {
            System.err.println("No fue posible crear el directorio de salida: " + e.getMessage());
            return;
        }


        Path archivoReporte = directorioSalida.resolve("reporte-incidencias.txt");
        Path archivoAltas = directorioSalida.resolve("incidencias-alta.txt");

        int total = 0;
        int altas = 0;
        int medias = 0;
        int bajas = 0;

        StringBuilder detalleAltas = new StringBuilder();

        try (
                BufferedReader lector = Files.newBufferedReader(archivoEntrada);
                PrintWriter reporte = new PrintWriter(Files.newBufferedWriter(archivoReporte));
                PrintWriter reporteAltas = new PrintWriter(Files.newBufferedWriter(archivoAltas))
        ) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) continue;


                String[] partes = linea.split("\\|");
                if (partes.length < 3) continue;

                String id = partes[0];
                String descripcion = partes[1];
                String prioridad = partes[2];

                total++;


                switch (prioridad) {
                    case "ALTA" -> {
                        altas++;
                        detalleAltas.append(id).append(" - ").append(descripcion).append(System.lineSeparator());

                        reporteAltas.println(linea);
                    }
                    case "MEDIA" -> medias++;
                    case "BAJA" -> bajas++;
                }
            }

            // 10. Generar resumen
            reporte.println("REPORTE DE INCIDENCIAS");
            reporte.println("======================");
            reporte.println();
            reporte.println("Archivo procesado: " + archivoEntrada.getFileName());
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
            reporte.print(detalleAltas);

            System.out.println("Reporte generado en: " + archivoReporte.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        }
    }
}