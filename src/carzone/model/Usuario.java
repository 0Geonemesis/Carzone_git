package carzone.model;

public class Usuario {
    private String idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private String rol;
    private boolean estado;

    public Usuario() {}

    public Usuario(String idUsuario, String nombreUsuario, String contrasena, String rol, boolean estado) {
        this.idUsuario     = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena    = contrasena;
        this.rol           = rol;
        this.estado        = estado;
    }

    public String getIdUsuario()      { return idUsuario; }
    public void setIdUsuario(String v){ idUsuario = v; }

    public String getNombreUsuario()      { return nombreUsuario; }
    public void setNombreUsuario(String v){ nombreUsuario = v; }

    public String getContrasena()      { return contrasena; }
    public void setContrasena(String v){ contrasena = v; }

    public String getRol()      { return rol; }
    public void setRol(String v){ rol = v; }

    public boolean isEstado()       { return estado; }
    public void setEstado(boolean v){ estado = v; }

    @Override
    public String toString() { return nombreUsuario + " [" + rol + "]"; }
}
