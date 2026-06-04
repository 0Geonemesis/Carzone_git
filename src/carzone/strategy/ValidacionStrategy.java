package carzone.strategy;

/**
 * Patrón Strategy - Estrategias de validación intercambiables.
 */
public interface ValidacionStrategy {
    boolean validar(String valor);
    String getMensaje();
}
