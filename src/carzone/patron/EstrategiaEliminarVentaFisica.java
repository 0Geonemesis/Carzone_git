package carzone.patron;

import carzone.dao.VentaDAO;

public class EstrategiaEliminarVentaFisica implements EstrategiaEliminarVenta {

    @Override
    public boolean eliminar(String idVenta, VentaDAO dao) {
        return dao.eliminarFisico(idVenta);
    }

    @Override
    public String descripcion() {
        return "Eliminación física (borrado permanente)";
    }
}
