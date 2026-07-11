package carzone.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Venta {

    private String idVenta;
    private String idCliente;
    private String idUsuario;
    private String idAuto;
    private Timestamp fechaVenta;
    private BigDecimal montoTotal;
    private String estadoVenta;

    private String nombreCliente;
    private String apellidosCliente;
    private String dniCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String nombreUsuario;
    private String marcaAuto;
    private String modeloAuto;
    private int anioAuto;
    private String colorAuto;

    public Venta() {
    }

    public Venta(String idVenta, String idCliente, String idUsuario, String idAuto,
            Timestamp fechaVenta, BigDecimal montoTotal, String estadoVenta) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.idAuto = idAuto;
        this.fechaVenta = fechaVenta;
        this.montoTotal = montoTotal;
        this.estadoVenta = estadoVenta;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdAuto() {
        return idAuto;
    }

    public void setIdAuto(String idAuto) {
        this.idAuto = idAuto;
    }

    public Timestamp getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Timestamp fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstadoVenta() {
        return estadoVenta;
    }

    public void setEstadoVenta(String estadoVenta) {
        this.estadoVenta = estadoVenta;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidosCliente() {
        return apellidosCliente;
    }

    public void setApellidosCliente(String apellidosCliente) {
        this.apellidosCliente = apellidosCliente;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getMarcaAuto() {
        return marcaAuto;
    }

    public void setMarcaAuto(String marcaAuto) {
        this.marcaAuto = marcaAuto;
    }

    public String getModeloAuto() {
        return modeloAuto;
    }

    public void setModeloAuto(String modeloAuto) {
        this.modeloAuto = modeloAuto;
    }

    public int getAnioAuto() {
        return anioAuto;
    }

    public void setAnioAuto(int anioAuto) {
        this.anioAuto = anioAuto;
    }

    public String getColorAuto() {
        return colorAuto;
    }

    public void setColorAuto(String colorAuto) {
        this.colorAuto = colorAuto;
    }

    public String getClienteCompleto() {
        return (nombreCliente != null ? nombreCliente : "") + " "
                + (apellidosCliente != null ? apellidosCliente : "");
    }

    public String getAutoDescripcion() {
        return marcaAuto + " " + modeloAuto + " " + anioAuto + " - " + colorAuto;
    }

    @Override
    public String toString() {
        return idVenta + " | " + getClienteCompleto() + " | " + getAutoDescripcion();
    }
}
