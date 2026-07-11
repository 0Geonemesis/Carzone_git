package carzone.patron;

import carzone.modelo.Venta;
import java.util.ArrayList;
import java.util.List;

public class VentaSubject {

    private final List<VentaObserver> observadores = new ArrayList<>();

    public void agregarObservador(VentaObserver obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void quitarObservador(VentaObserver obs) {
        observadores.remove(obs);
    }

    public void notificarRegistro(Venta v) {
        for (VentaObserver obs : observadores) {
            obs.onVentaRegistrada(v);
        }
    }

    public void notificarModificacion(Venta v) {
        for (VentaObserver obs : observadores) {
            obs.onVentaModificada(v);
        }
    }

    public void notificarEliminacion(String idVenta) {
        for (VentaObserver obs : observadores) {
            obs.onVentaEliminada(idVenta);
        }
    }
}
