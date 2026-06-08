package carzone.modelo;

import java.math.BigDecimal;

public class Auto {

    private String idAuto;
    private String marca;
    private String modelo;
    private int anio;
    private String color;
    private BigDecimal precio;
    private String estado;
    private String codigo4Cifras;

    public Auto() {
    }

    public Auto(String idAuto, String marca, String modelo, int anio, String color, BigDecimal precio, String estado, String codigo4Cifras) {
        this.idAuto = idAuto;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.precio = precio;
        this.estado = estado;
        this.codigo4Cifras = codigo4Cifras;
    }

    public String getIdAuto() {
        return idAuto;
    }

    public void setIdAuto(String idAuto) {
        this.idAuto = idAuto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo4Cifras() {
        return codigo4Cifras;
    }

    public void setCodigo4Cifras(String codigo4Cifras) {
        this.codigo4Cifras = codigo4Cifras;
    }

    @Override
    public String toString() {
        return marca + " " + modelo + " " + anio + " - " + color;
    }
}
