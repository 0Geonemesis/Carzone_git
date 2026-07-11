package carzone.dao;

import carzone.modelo.Indicador;
import carzone.patron.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IndicadorDAO {

    public static final String COD_EFICACIA_CUMPLIMIENTO = "IND_EFICACIA";
    public static final String COD_PRODUCTO_COMPROBANTES = "IND_PRODUCTO";
    public static final String COD_INSUMO_INVENTARIO = "IND_INSUMO";
    public static final String COD_RESULTADO_RETENCION = "IND_RESULTADO";

    private Connection obtenerConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    public List<Indicador> listarIndicadoresGestion() {
        List<Indicador> lista = new ArrayList<>();
        lista.add(calcularEficaciaCumplimiento());
        lista.add(calcularProductoComprobantes());
        lista.add(calcularInsumoInventario());
        lista.add(calcularResultadoRetencion());
        return lista;
    }

    public Indicador obtenerPorCodigo(String codigo) {
        switch (codigo) {
            case COD_EFICACIA_CUMPLIMIENTO:
                return calcularEficaciaCumplimiento();
            case COD_PRODUCTO_COMPROBANTES:
                return calcularProductoComprobantes();
            case COD_INSUMO_INVENTARIO:
                return calcularInsumoInventario();
            case COD_RESULTADO_RETENCION:
                return calcularResultadoRetencion();
            default:
                throw new IllegalArgumentException("Código de indicador no soportado: " + codigo);
        }
    }

    private Indicador calcularEficaciaCumplimiento() {
        Indicador ind = new Indicador(
                COD_EFICACIA_CUMPLIMIENTO,
                "Eficacia",
                "Tasa de Cumplimiento de Ventas Registradas Correctamente",
                "Mide el porcentaje de ventas que fueron registradas correctamente en el "
                + "sistema (estado 'completada') sin quedar anuladas ni pendientes, "
                + "respecto al total de ventas registradas.",
                "Eficacia de registro = (Ventas completadas / Total de ventas registradas) x 100",
                "Trimestral",
                "Alcanzar un 98% de ventas registradas correctamente al cabo del primer "
                + "trimestre de uso del sistema.");

        String sql = "SELECT estado_venta, COUNT(*) AS cantidad FROM venta GROUP BY estado_venta";
        Map<String, Integer> conteoPorEstado = new LinkedHashMap<>();
        int total = 0;

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String estado = rs.getString("estado_venta");
                int cantidad = rs.getInt("cantidad");
                conteoPorEstado.put(estado, cantidad);
                total += cantidad;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular indicador de eficacia: " + e.getMessage());
        }

        int completadas = conteoPorEstado.getOrDefault("completada", 0);
        double porcentaje = total > 0 ? (completadas * 100.0 / total) : 0.0;

        ind.setValorActual(String.format("%.1f%%", porcentaje));
        ind.setColumnasDetalle(new String[]{"Estado de Venta", "Cantidad", "Porcentaje del Total"});

        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<String, Integer> e : conteoPorEstado.entrySet()) {
            double pct = total > 0 ? (e.getValue() * 100.0 / total) : 0.0;
            filas.add(new String[]{capitalizar(e.getKey()), String.valueOf(e.getValue()), String.format("%.1f%%", pct)});
        }
        filas.add(new String[]{"TOTAL", String.valueOf(total), "100.0%"});
        ind.setFilasDetalle(filas);
        return ind;
    }

    private Indicador calcularProductoComprobantes() {
        Indicador ind = new Indicador(
                COD_PRODUCTO_COMPROBANTES,
                "Producto",
                "Comprobantes de Venta Generados Correctamente",
                "Mide el porcentaje de ventas concluidas (estado 'completada') que "
                + "generaron automáticamente un comprobante con código identificador "
                + "único válido.",
                "Comprobantes válidos = (Comprobantes con código único / Total de ventas completadas) x 100",
                "Mensual",
                "Lograr que el 100% de las ventas completadas generen un comprobante con "
                + "código único desde el primer mes de uso del sistema.");

        String sql = "SELECT v.id_venta, v.estado_venta, c.codigo_identificador, c.tipo_comprobante "
                + "FROM venta v LEFT JOIN comprobante c ON v.id_venta = c.id_venta "
                + "WHERE v.estado_venta = 'completada' ORDER BY v.id_venta";

        List<String[]> filas = new ArrayList<>();
        int totalCompletadas = 0;
        int conComprobante = 0;

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                totalCompletadas++;
                String codigo = rs.getString("codigo_identificador");
                String tipo = rs.getString("tipo_comprobante");
                boolean tiene = codigo != null && !codigo.isEmpty();
                if (tiene) {
                    conComprobante++;
                }
                filas.add(new String[]{
                    rs.getString("id_venta"),
                    tiene ? "Sí" : "No",
                    tiene ? capitalizar(tipo) : "-",
                    tiene ? codigo : "-"
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular indicador de producto: " + e.getMessage());
        }

        double porcentaje = totalCompletadas > 0 ? (conComprobante * 100.0 / totalCompletadas) : 0.0;
        ind.setValorActual(String.format("%.1f%%", porcentaje));
        ind.setColumnasDetalle(new String[]{"ID Venta", "¿Tiene Comprobante?", "Tipo", "Código Identificador"});
        ind.setFilasDetalle(filas);
        return ind;
    }

    private Indicador calcularInsumoInventario() {
        Indicador ind = new Indicador(
                COD_INSUMO_INVENTARIO,
                "Insumo",
                "Vehículos Ingresados al Inventario por Mes",
                "Cuantifica la cantidad de autos registrados en el sistema de inventario "
                + "cada mes, como insumo base para el proceso de ventas.",
                "Insumo de inventario = Número total de vehículos ingresados al sistema en el mes",
                "Mensual",
                "Registrar el 100% de vehículos adquiridos en el sistema dentro de las 24 "
                + "horas siguientes a su recepción.");

        String sql = "SELECT DATE_FORMAT(fecha_ingreso, '%Y-%m') AS mes, COUNT(*) AS cantidad "
                + "FROM inventario GROUP BY mes ORDER BY mes";

        List<String[]> filas = new ArrayList<>();
        int total = 0;
        String ultimoMes = null;
        int cantidadUltimoMes = 0;

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String mes = rs.getString("mes");
                int cantidad = rs.getInt("cantidad");
                total += cantidad;
                ultimoMes = mes;
                cantidadUltimoMes = cantidad;
                filas.add(new String[]{mes, String.valueOf(cantidad)});
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular indicador de insumo: " + e.getMessage());
        }

        if (ultimoMes != null) {
            ind.setValorActual(cantidadUltimoMes + " (mes " + ultimoMes + ")  |  " + total + " acumulado");
        } else {
            ind.setValorActual("0");
        }
        ind.setColumnasDetalle(new String[]{"Mes de Ingreso", "Vehículos Ingresados"});
        filas.add(new String[]{"TOTAL ACUMULADO", String.valueOf(total)});
        ind.setFilasDetalle(filas);
        return ind;
    }

    private Indicador calcularResultadoRetencion() {
        Indicador ind = new Indicador(
                COD_RESULTADO_RETENCION,
                "Resultado",
                "Tasa de Retención de Clientes",
                "Mide el porcentaje de clientes que registran más de una compra en "
                + "CARZONE respecto al total de clientes atendidos, como indicador de "
                + "fidelización tras la implementación del sistema.",
                "Retención = (Clientes con más de 1 venta / Total de clientes con venta registrada) x 100",
                "Trimestral",
                "Incrementar la tasa de retención en un 20% al finalizar el primer semestre "
                + "después de la implementación.");

        String sql = "SELECT c.dni, c.nombres, c.apellidos, COUNT(v.id_venta) AS nro_ventas "
                + "FROM cliente c INNER JOIN venta v ON v.id_cliente = c.id_cliente "
                + "GROUP BY c.id_cliente, c.dni, c.nombres, c.apellidos "
                + "ORDER BY nro_ventas DESC";

        List<String[]> filas = new ArrayList<>();
        int totalClientes = 0;
        int clientesRecurrentes = 0;

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                totalClientes++;
                int nroVentas = rs.getInt("nro_ventas");
                boolean recurrente = nroVentas > 1;
                if (recurrente) {
                    clientesRecurrentes++;
                }
                filas.add(new String[]{
                    rs.getString("nombres") + " " + rs.getString("apellidos"),
                    rs.getString("dni"),
                    String.valueOf(nroVentas),
                    recurrente ? "Recurrente" : "Nuevo"
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular indicador de resultado: " + e.getMessage());
        }

        double porcentaje = totalClientes > 0 ? (clientesRecurrentes * 100.0 / totalClientes) : 0.0;
        ind.setValorActual(String.format("%.1f%%", porcentaje));
        ind.setColumnasDetalle(new String[]{"Cliente", "DNI", "N° de Ventas", "Condición"});
        ind.setFilasDetalle(filas);
        return ind;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "-";
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
