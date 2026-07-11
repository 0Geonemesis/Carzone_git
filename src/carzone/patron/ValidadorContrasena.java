package carzone.patron;

public class ValidadorContrasena {

    private ValidadorContrasena() {
    }

    public static boolean esValida(String contrasena) {
        if (contrasena == null || contrasena.length() < 8) {
            return false;
        }
        boolean tieneMayus = false;
        boolean tieneMinus = false;
        boolean tieneNumero = false;
        boolean tieneEspecial = false;
        for (char c : contrasena.toCharArray()) {
            if (Character.isUpperCase(c)) {
                tieneMayus = true;
            } else if (Character.isLowerCase(c)) {
                tieneMinus = true;
            } else if (Character.isDigit(c)) {
                tieneNumero = true;
            } else if (!Character.isLetterOrDigit(c)) {
                tieneEspecial = true;
            }
        }
        return tieneMayus && tieneMinus && tieneNumero && tieneEspecial;
    }

    public static String getMensajeRequisitos() {
        return "La contraseña debe tener:\n• Mínimo 8 caracteres (1 letra mayúscula, 1 letra minúscula,1 número,1 carácter especial)";
    }
}
