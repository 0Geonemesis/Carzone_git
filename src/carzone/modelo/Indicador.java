package carzone.modelo;

import java.util.List;


public class Indicador {

    private final String codigo;
    private final String categoria;
    private final String nombre;
    private final String descripcion;
    private final String formula;
    private final String periodoMedicion;
    private final String meta;

    private String valorActual;
    private String[] columnasDetalle;
    private List<String[]> filasDetalle;

    public Indicador(String codigo, String categoria, String nombre, String descripcion,
            String formula, String periodoMedicion, String meta) {
        this.codigo = codigo;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.formula = formula;
        this.periodoMedicion = periodoMedicion;
        this.meta = meta;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFormula() {
        return formula;
    }

    public String getPeriodoMedicion() {
        return periodoMedicion;
    }

    public String getMeta() {
        return meta;
    }

    public String getValorActual() {
        return valorActual;
    }

    public void setValorActual(String valorActual) {
        this.valorActual = valorActual;
    }

    public String[] getColumnasDetalle() {
        return columnasDetalle;
    }

    public void setColumnasDetalle(String[] columnasDetalle) {
        this.columnasDetalle = columnasDetalle;
    }

    public List<String[]> getFilasDetalle() {
        return filasDetalle;
    }

    public void setFilasDetalle(List<String[]> filasDetalle) {
        this.filasDetalle = filasDetalle;
    }
}
