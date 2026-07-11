package carzone.reporte;

import carzone.modelo.Venta;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReporteVentasAutos implements Reporte {

    private final List<Venta> ventas;
    private final String rangoDescripcion;
    private final String usuarioGenerador;

    private static final float MARGEN_IZQ = 40f;
    private static final float MARGEN_SUP = 40f;
    private static final float LIMITE_INFERIOR = 745f;

    public ReporteVentasAutos(List<Venta> ventas, String rangoDescripcion, String usuarioGenerador) {
        this.ventas = ventas;
        this.rangoDescripcion = rangoDescripcion;
        this.usuarioGenerador = usuarioGenerador;
    }

    @Override
    public void exportarPDF(String rutaArchivo) {
        DocumentoPDF pdf = new DocumentoPDF();

        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        NumberFormat moneda = NumberFormat.getNumberInstance(new Locale("es", "PE"));
        moneda.setMinimumFractionDigits(2);
        moneda.setMaximumFractionDigits(2);

        float[] colX = {MARGEN_IZQ, 92, 168, 300, 418, 478, 528};
        String[] colTitulos = {"ID", "Fecha", "Cliente", "Auto", "Vendedor", "Estado", "Monto"};

        float y = dibujarEncabezado(pdf);
        y = dibujarCabeceraTabla(pdf, y, colX, colTitulos);

        if (ventas == null || ventas.isEmpty()) {
            pdf.texto(MARGEN_IZQ, y + 10, "No se encontraron ventas para el criterio seleccionado.", 10, false);
        } else {
            BigDecimal total = BigDecimal.ZERO;
            int contador = 0;
            boolean filaAlterna = false;

            for (Venta v : ventas) {
                if (y > LIMITE_INFERIOR) {
                    pdf.nuevaPagina();
                    y = dibujarEncabezado(pdf);
                    y = dibujarCabeceraTabla(pdf, y, colX, colTitulos);
                    filaAlterna = false;
                }

                if (filaAlterna) {
                    pdf.rectangulo(MARGEN_IZQ - 2, y - 11, 536, 15, 0.96f, 0.97f, 0.99f, true);
                }
                filaAlterna = !filaAlterna;

                pdf.texto(colX[0], y, v.getIdVenta(), 8, false);
                pdf.texto(colX[1], y, v.getFechaVenta() != null ? sdfFecha.format(v.getFechaVenta()) : "-", 8, false);
                pdf.texto(colX[2], y, recorte(v.getClienteCompleto(), 19), 8, false);
                pdf.texto(colX[3], y, recorte(v.getAutoDescripcion(), 22), 8, false);
                pdf.texto(colX[4], y, recorte(v.getNombreUsuario(), 12), 8, false);
                pdf.texto(colX[5], y, capitalizar(v.getEstadoVenta()), 8, false);
                pdf.texto(colX[6], y, "S/ " + moneda.format(v.getMontoTotal() != null ? v.getMontoTotal() : BigDecimal.ZERO), 8, false);

                if ("completada".equalsIgnoreCase(v.getEstadoVenta()) && v.getMontoTotal() != null) {
                    total = total.add(v.getMontoTotal());
                }
                contador++;
                y += 15;
            }

            y += 12;
            if (y > LIMITE_INFERIOR + 15) {
                pdf.nuevaPagina();
                y = MARGEN_SUP + 10;
            }
            pdf.linea(MARGEN_IZQ, y, 572, y);
            y += 18;
            pdf.texto(MARGEN_IZQ, y, "Total de ventas listadas: " + contador, 10, true);
            y += 16;
            pdf.texto(MARGEN_IZQ, y, "Monto total (ventas completadas): S/ " + moneda.format(total), 10, true);
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
        pdf.texto(MARGEN_IZQ, 42, "Reporte de Venta de Autos", 11, false, 1f, 1f, 1f);

        float yy = 75;
        pdf.texto(MARGEN_IZQ, yy, rangoDescripcion, 10, false);
        yy += 14;
        pdf.texto(MARGEN_IZQ, yy, "Generado por: " + (usuarioGenerador != null ? usuarioGenerador : "-")
                + "   |   Fecha de emision: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), 9, false);
        return yy + 24;
    }

    private float dibujarCabeceraTabla(DocumentoPDF pdf, float y, float[] colX, String[] titulos) {
        pdf.rectangulo(MARGEN_IZQ - 2, y - 12, 536, 18, 0.82f, 0.86f, 0.93f, true);
        for (int i = 0; i < titulos.length; i++) {
            pdf.texto(colX[i], y, titulos[i], 9, true);
        }
        pdf.linea(MARGEN_IZQ - 2, y + 6, 572, y + 6);
        return y + 20;
    }

    private String recorte(String texto, int max) {
        if (texto == null) {
            return "";
        }
        texto = texto.trim();
        return texto.length() > max ? texto.substring(0, max - 1) + "." : texto;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
