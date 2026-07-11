package carzone.patron;

import carzone.modelo.Venta;

public interface VentaObserver {

    void onVentaRegistrada(Venta venta);

    void onVentaModificada(Venta venta);

    void onVentaEliminada(String idVenta);
}
