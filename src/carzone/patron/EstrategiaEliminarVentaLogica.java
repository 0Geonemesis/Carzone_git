package carzone.patron;

import carzone.dao.VentaDAO;

public class EstrategiaEliminarVentaLogica implements EstrategiaEliminarVenta {

    @Override
    public boolean eliminar(String idVenta, VentaDAO dao) {
        return dao.eliminarLogico(idVenta);
    }

    @Override
    public String descripcion() {
        return "Eliminación lógica (anulación de venta)";
    }
}
