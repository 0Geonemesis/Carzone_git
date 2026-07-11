package carzone.patron;

import java.math.BigDecimal;
import java.time.Year;

public class ValidadorAuto {

    public static final int LONGITUD_MAX_MARCA = 30;
    public static final int LONGITUD_MAX_MODELO = 30;
    public static final int LONGITUD_MAX_COLOR = 20;
    public static final int LONGITUD_CODIGO = 4;

    public static final int ANIO_MINIMO = 1990;
    public static final int ANIO_MAXIMO = Year.now().getValue() + 1;

    public static final BigDecimal PRECIO_MINIMO = new BigDecimal("1000.00");
    public static final BigDecimal PRECIO_MAXIMO = new BigDecimal("999999.99");

    private ValidadorAuto() {
    }

    public static boolean esTextoValido(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return false;
        }
        return valor.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s\\-]+");
    }

    public static boolean longitudValida(String valor, int maximo) {
        return valor != null && !valor.trim().isEmpty() && valor.trim().length() <= maximo;
    }

    public static boolean esMarcaValida(String marca) {
        return esTextoValido(marca) && longitudValida(marca, LONGITUD_MAX_MARCA);
    }

    public static boolean esModeloValido(String modelo) {
        return esTextoValido(modelo) && longitudValida(modelo, LONGITUD_MAX_MODELO);
    }

    public static boolean esColorValido(String color) {

        if (color == null || color.trim().isEmpty()) {
            return false;
        }
        boolean soloLetras = color.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ\\s\\-]+");
        return soloLetras && longitudValida(color, LONGITUD_MAX_COLOR);
    }

    public static boolean esAnioValido(String anioTexto) {
        if (anioTexto == null || anioTexto.trim().isEmpty()) {
            return false;
        }
        if (!anioTexto.trim().matches("\\d{4}")) {
            return false;
        }
        int anio = Integer.parseInt(anioTexto.trim());
        return anio >= ANIO_MINIMO && anio <= ANIO_MAXIMO;
    }

    public static boolean esPrecioValido(String precioTexto) {
        if (precioTexto == null || precioTexto.trim().isEmpty()) {
            return false;
        }
        if (!precioTexto.trim().matches("\\d{1,7}(\\.\\d{1,2})?")) {
            return false;
        }
        try {
            BigDecimal precio = new BigDecimal(precioTexto.trim());
            return precio.compareTo(PRECIO_MINIMO) >= 0 && precio.compareTo(PRECIO_MAXIMO) <= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esCodigoValido(String codigo) {
        return codigo != null && codigo.trim().matches("\\d{" + LONGITUD_CODIGO + "}");
    }

    public static String mensajeMarca() {
        return "Marca inválida: solo letras y espacios, máximo " + LONGITUD_MAX_MARCA + " caracteres.";
    }

    public static String mensajeModelo() {
        return "Modelo inválido: solo letras/números y espacios, máximo " + LONGITUD_MAX_MODELO + " caracteres.";
    }

    public static String mensajeColor() {
        return "Color inválido: solo letras, máximo " + LONGITUD_MAX_COLOR + " caracteres.";
    }

    public static String mensajeAnio() {
        return "Año inválido: debe ser un número de 4 dígitos entre " + ANIO_MINIMO + " y " + ANIO_MAXIMO + ".";
    }

    public static String mensajePrecio() {
        return "Precio inválido: debe ser numérico, entre S/. " + PRECIO_MINIMO + " y S/. " + PRECIO_MAXIMO + ".";
    }
}
