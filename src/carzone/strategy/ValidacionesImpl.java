package carzone.strategy;

public class ValidacionesImpl {

    /** Valida que no esté vacío y no supere maxLen caracteres */
    public static class NoVacio implements ValidacionStrategy {
        private final String campo;
        private final int maxLen;
        public NoVacio(String campo, int maxLen) { this.campo = campo; this.maxLen = maxLen; }
        @Override public boolean validar(String v) {
            return v != null && !v.trim().isEmpty() && v.length() <= maxLen;
        }
        @Override public String getMensaje() {
            return campo + " es obligatorio y debe tener máximo " + maxLen + " caracteres.";
        }
    }

    /** Valida que el ID tenga el formato correcto: USR### */
    public static class FormatoId implements ValidacionStrategy {
        @Override public boolean validar(String v) {
            return v != null && v.matches("[A-Z]{3}\\d{3}");
        }
        @Override public String getMensaje() {
            return "El ID debe tener el formato XXX000 (3 letras y 3 números), ej: USR001.";
        }
    }

    /** Valida que la contraseña tenga al menos 6 caracteres */
    public static class Contrasena implements ValidacionStrategy {
        @Override public boolean validar(String v) {
            return v != null && v.length() >= 6;
        }
        @Override public String getMensaje() {
            return "La contraseña debe tener al menos 6 caracteres.";
        }
    }

    /** Valida que el rol sea uno de los valores permitidos */
    public static class RolValido implements ValidacionStrategy {
        @Override public boolean validar(String v) {
            return "administrador".equals(v) || "vendedor".equals(v);
        }
        @Override public String getMensaje() {
            return "El rol debe ser 'administrador' o 'vendedor'.";
        }
    }
}
