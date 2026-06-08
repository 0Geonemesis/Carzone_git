package carzone.patron;

import java.util.HashMap;
import java.util.Map;

public class GeneradorCodigo4Cifras {

    private static final Map<String, Integer> MARCAS = new HashMap<>();
    private static final Map<String, Integer> COLORES = new HashMap<>();

    static {
        String[] marcas = {"Toyota", "Hyundai", "Kia", "Nissan", "Chevrolet", "Ford", "Honda", "Volkswagen", "Subaru", "Mazda"};
        for (int i = 0; i < marcas.length; i++) {
            MARCAS.put(marcas[i].toLowerCase(), i);
        }

        String[] colores = {"Blanco", "Negro", "Gris", "Rojo", "Azul", "Verde", "Amarillo", "Naranja", "Marron", "Plateado"};
        for (int i = 0; i < colores.length; i++) {
            COLORES.put(colores[i].toLowerCase(), i);
        }
    }

    private GeneradorCodigo4Cifras() {
    }

    public static String generar(String marca, String modelo, int anio, String color) {
        int digMarca = MARCAS.getOrDefault(marca.toLowerCase(), Math.abs(marca.hashCode()) % 10);
        int digModelo = Math.abs(modelo.hashCode()) % 10;
        int digAnio = anio % 10;
        int digColor = COLORES.getOrDefault(color.toLowerCase(), Math.abs(color.hashCode()) % 10);
        return String.valueOf(digMarca) + digModelo + digAnio + digColor;
    }
}
