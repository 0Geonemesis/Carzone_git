package carzone.patron;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidadorVenta {

    public static final int MAX_NOMBRES = 60;
    public static final int MAX_APELLIDOS = 60;
    public static final int LEN_DNI = 8;
    public static final int MAX_TELEFONO = 9;
    public static final int MAX_CORREO = 60;
    public static final int MAX_DIRECCION = 100;

    public static final BigDecimal MONTO_MINIMO = new BigDecimal("1000.00");
    public static final BigDecimal MONTO_MAXIMO = new BigDecimal("99999999.99");

    private static final Pattern PATTERN_SOLO_TEXTO = Pattern.compile("[A-Za-zÁÉÍÓÚáéíóúÑñ\\s\\-']+");
    private static final Pattern PATTERN_DNI = Pattern.compile("\\d{8}");
    private static final Pattern PATTERN_TELEFONO = Pattern.compile("\\d{9}");
    private static final Pattern PATTERN_CORREO = Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PATTERN_MONTO = Pattern.compile("\\d{1,8}(\\.\\d{1,2})?");

    private ValidadorVenta() {
    }

    public static boolean esNombresValido(String valor) {
        return valor != null && !valor.trim().isEmpty()
                && PATTERN_SOLO_TEXTO.matcher(valor.trim()).matches()
                && valor.trim().length() <= MAX_NOMBRES;
    }

    public static boolean esApellidosValido(String valor) {
        return valor != null && !valor.trim().isEmpty()
                && PATTERN_SOLO_TEXTO.matcher(valor.trim()).matches()
                && valor.trim().length() <= MAX_APELLIDOS;
    }

    public static boolean esDniValido(String valor) {
        return valor != null && PATTERN_DNI.matcher(valor.trim()).matches();
    }

    public static boolean esTelefonoValido(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return true;
        }
        return PATTERN_TELEFONO.matcher(valor.trim()).matches();
    }

    public static boolean esCorreoValido(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return true;
        }
        return PATTERN_CORREO.matcher(valor.trim()).matches()
                && valor.trim().length() <= MAX_CORREO;
    }

    public static boolean esDireccionValida(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return true;
        }
        return valor.trim().length() <= MAX_DIRECCION;
    }

    public static boolean esMontoValido(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return false;
        }
        if (!PATTERN_MONTO.matcher(valor.trim()).matches()) {
            return false;
        }
        try {
            BigDecimal monto = new BigDecimal(valor.trim());
            return monto.compareTo(MONTO_MINIMO) >= 0 && monto.compareTo(MONTO_MAXIMO) <= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esEstadoVentaValido(String valor) {
        return "completada".equals(valor) || "anulada".equals(valor) || "en proceso".equals(valor);
    }

    public static String mensajeNombres() {
        return "Nombres inválido: solo letras, máximo " + MAX_NOMBRES + " caracteres.";
    }

    public static String mensajeApellidos() {
        return "Apellidos inválido: solo letras, máximo " + MAX_APELLIDOS + " caracteres.";
    }

    public static String mensajeDni() {
        return "DNI inválido: debe tener exactamente " + LEN_DNI + " dígitos numéricos.";
    }

    public static String mensajeTelefono() {
        return "Teléfono inválido: debe tener exactamente " + MAX_TELEFONO + " dígitos (o dejarlo vacío).";
    }

    public static String mensajeCorreo() {
        return "Correo inválido: formato incorrecto o supera " + MAX_CORREO + " caracteres.";
    }

    public static String mensajeDireccion() {
        return "Dirección inválida: supera " + MAX_DIRECCION + " caracteres.";
    }

    public static String mensajeMonto() {
        return "Monto inválido: debe ser un número entre S/. " + MONTO_MINIMO + " y S/. " + MONTO_MAXIMO + ".";
    }

    public static String mensajeEstadoVenta() {
        return "Estado inválido: debe ser 'completada', 'anulada' o 'en proceso'.";
    }
}
