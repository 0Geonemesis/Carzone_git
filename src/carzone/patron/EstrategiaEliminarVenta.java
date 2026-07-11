package carzone.patron;

import carzone.dao.VentaDAO;

public interface EstrategiaEliminarVenta {

    boolean eliminar(String idVenta, VentaDAO dao);

    String descripcion();
}
