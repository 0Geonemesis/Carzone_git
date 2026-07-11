package carzone.reporte;

import carzone.modelo.Indicador;
import carzone.modelo.Venta;

import java.util.List;

public class ReporteFactory {

    public static final String TIPO_VENTAS_AUTOS = "ventas_autos";
    public static final String TIPO_INDICADOR = "indicador";
    public static final String TIPO_ESTADISTICAS_VENTAS = "estadisticas_ventas";
    public static final String TIPO_VENTAS_ANULADAS = "ventas_anuladas";
    public static final String TIPO_VENTAS_INGRESOS = "ventas_ingresos";

    private ReporteFactory() {
    }

    public static Reporte crear(String tipoReporte, List<Venta> datos, String rangoDescripcion, String usuarioGenerador) {
        if (TIPO_VENTAS_AUTOS.equals(tipoReporte)) {
            return new ReporteVentasAutos(datos, rangoDescripcion, usuarioGenerador);
        }
        if (TIPO_ESTADISTICAS_VENTAS.equals(tipoReporte)) {
            return new ReporteEstadisticasVentas(datos, rangoDescripcion, usuarioGenerador);
        }
        if (TIPO_VENTAS_ANULADAS.equals(tipoReporte)) {
            return new ReporteVentasAnuladas(datos, rangoDescripcion, usuarioGenerador);
        }
        if (TIPO_VENTAS_INGRESOS.equals(tipoReporte)) {
            return new ReporteVentasIngresos(datos, rangoDescripcion, usuarioGenerador);
        }
        throw new IllegalArgumentException("Tipo de reporte no soportado: " + tipoReporte);
    }

    public static Reporte crear(Indicador indicador, String usuarioGenerador) {
        return new ReporteIndicador(indicador, usuarioGenerador);
    }
}
