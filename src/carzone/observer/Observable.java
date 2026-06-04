package carzone.observer;

/**
 * Patrón Observer - Interfaz observable (sujeto).
 */
public interface Observable {
    void agregarObserver(Observer o);
    void removerObserver(Observer o);
    void notificarObservers(String evento, Object dato);
}
