package carzone.factory;

import carzone.model.Usuario;

/**
 * Patrón Factory Method - Crea entidades del dominio.
 */
public class EntityFactory {

    public static Usuario crearUsuario(String id, String nombre, String clave,
                                       String rol, boolean estado) {
        return new Usuario(id, nombre, clave, rol, estado);
    }

    public static Usuario crearUsuarioVacio() {
        return new Usuario("", "", "", "vendedor", true);
    }
}
