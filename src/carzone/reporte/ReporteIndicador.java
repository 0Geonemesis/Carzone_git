package carzone.reporte;

import carzone.modelo.Indicador;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReporteIndicador implements Reporte {

    private final Indicador indicador;
    private final String usuarioGenerador;

    private static final float MARGEN_IZQ = 40f;
    private static final float MARGEN_SUP = 40f;
    private static final float LIMITE_INFERIOR = 745f;
    private static final float ANCHO_TABLA = 532f;

    public ReporteIndicador(Indicador indicador, String usuarioGenerador) {
        this.indicador = indicador;
        this.usuarioGenerador = usuarioGenerador;
    }

    @Override
    public void exportarPDF(String rutaArchivo) {
        DocumentoPDF pdf = new DocumentoPDF();

        float y = dibujarEncabezado(pdf);
        y = dibujarFichaTecnica(pdf, y);

        String[] columnas = indicador.getColumnasDetalle();
        List<String[]> filas = indicador.getFilasDetalle();
        float[] colX = calcularColumnas(columnas != null ? columnas.length : 0);

        if (columnas != null) {
            y = dibujarCabeceraTabla(pdf, y, colX, columnas);
        }

        if (filas == null || filas.isEmpty()) {
            pdf.texto(MARGEN_IZQ, y + 10, "No se encontraron datos para este indicador.", 10, false);
        } else {
            boolean filaAlterna = false;
            for (String[] fila : filas) {
                if (y > LIMITE_INFERIOR) {
                    pdf.nuevaPagina();
                    y = dibujarEncabezado(pdf);
                    y = dibujarCabeceraTabla(pdf, y, colX, columnas);
                    filaAlterna = false;
                }

                if (filaAlterna) {
                    pdf.rectangulo(MARGEN_IZQ - 2, y - 11, ANCHO_TABLA, 15, 0.96f, 0.97f, 0.99f, true);
                }
                filaAlterna = !filaAlterna;

                for (int i = 0; i < fila.length && i < colX.length; i++) {
                    pdf.texto(colX[i], y, recorte(fila[i], anchoMaximoColumna(columnas.length)), 8, false);
                }
                y += 15;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            fos.write(pdf.generar());
            fos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo PDF: " + e.getMessage());
        }
    }

    private float dibujarEncabezado(DocumentoPDF pdf) {
        pdf.rectangulo(0, 0, pdf.ancho(), 55, 0.06f, 0.08f, 0.16f, true);
        pdf.texto(MARGEN_IZQ, 25, "CARZONE", 18, true, 1f, 1f, 1f);
        pdf.texto(MARGEN_IZQ, 42, "Reporte de Indicador de Gestión — " + indicador.getCategoria(), 11, false, 1f, 1f, 1f);
        return 75;
    }

    private float dibujarFichaTecnica(DocumentoPDF pdf, float yInicial) {
        float y = yInicial;

        pdf.texto(MARGEN_IZQ, y, indicador.getNombre(), 13, true);
        y += 18;

        pdf.rectangulo(MARGEN_IZQ - 2, y - 12, ANCHO_TABLA, 96, 0.94f, 0.96f, 0.99f, true);

        y += 2;
        y = dibujarCampoMultilinea(pdf, y, "Descripción:", indicador.getDescripcion(), 95);
        y = dibujarCampoMultilinea(pdf, y, "Fórmula:", indicador.getFormula(), 95);
        pdf.texto(MARGEN_IZQ + 5, y, "Período de medición: " + indicador.getPeriodoMedicion(), 9, false);
        y += 13;
        y = dibujarCampoMultilinea(pdf, y, "Meta:", indicador.getMeta(), 95);

        y += 6;
        pdf.texto(MARGEN_IZQ + 5, y, "Valor actual calculado:", 10, true);
        pdf.texto(MARGEN_IZQ + 160, y, indicador.getValorActual(), 12, true, 0.05f, 0.4f, 0.15f);
        y += 22;

        pdf.texto(MARGEN_IZQ, y, "Generado por: " + (usuarioGenerador != null ? usuarioGenerador : "-")
                + "   |   Fecha de emisión: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), 9, false);
        y += 18;

        pdf.texto(MARGEN_IZQ, y, "Detalle de datos", 11, true);
        y += 16;
        return y;
    }

    private float dibujarCampoMultilinea(DocumentoPDF pdf, float y, String etiqueta, String contenido, int anchoMaxLinea) {
        pdf.texto(MARGEN_IZQ + 5, y, etiqueta, 9, true);
        y += 12;
        for (String linea : envolverTexto(contenido, anchoMaxLinea)) {
            pdf.texto(MARGEN_IZQ + 5, y, linea, 9, false);
            y += 11;
        }
        return y + 2;
    }

    private List<String> envolverTexto(String texto, int maxCaracteres) {
        List<String> lineas = new java.util.ArrayList<>();
        if (texto == null || texto.isEmpty()) {
            lineas.add("-");
            return lineas;
        }
        String[] palabras = texto.split(" ");
        StringBuilder actual = new StringBuilder();
        for (String palabra : palabras) {
            if (actual.length() + palabra.length() + 1 > maxCaracteres) {
                lineas.add(actual.toString());
                actual = new StringBuilder();
            }
            if (actual.length() > 0) {
                actual.append(' ');
            }
            actual.append(palabra);
        }
        if (actual.length() > 0) {
            lineas.add(actual.toString());
        }
        return lineas;
    }

    private float dibujarCabeceraTabla(DocumentoPDF pdf, float y, float[] colX, String[] titulos) {
        pdf.rectangulo(MARGEN_IZQ - 2, y - 12, ANCHO_TABLA, 18, 0.82f, 0.86f, 0.93f, true);
        for (int i = 0; i < titulos.length && i < colX.length; i++) {
            pdf.texto(colX[i], y, titulos[i], 9, true);
        }
        pdf.linea(MARGEN_IZQ - 2, y + 6, MARGEN_IZQ - 2 + ANCHO_TABLA, y + 6);
        return y + 20;
    }

    private float[] calcularColumnas(int numColumnas) {
        if (numColumnas <= 0) {
            return new float[0];
        }
        float[] colX = new float[numColumnas];
        float ancho = ANCHO_TABLA / numColumnas;
        for (int i = 0; i < numColumnas; i++) {
            colX[i] = MARGEN_IZQ + (i * ancho);
        }
        return colX;
    }

    private int anchoMaximoColumna(int numColumnas) {
        if (numColumnas <= 0) {
            return 20;
        }
        return Math.max(10, (int) (ANCHO_TABLA / numColumnas / 4.6f));
    }

    private String recorte(String texto, int max) {
        if (texto == null) {
            return "";
        }
        texto = texto.trim();
        return texto.length() > max ? texto.substring(0, max - 1) + "." : texto;
    }
}
