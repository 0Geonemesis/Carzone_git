package carzone.gui;

import carzone.dao.VentaDAO;
import carzone.modelo.Usuario;
import carzone.modelo.Venta;
import carzone.reporte.Reporte;
import carzone.reporte.ReporteFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class FrmReporteVentasAutos extends JFrame {

    private final VentaDAO ventaDAO = new VentaDAO();
    private final Usuario usuarioActual;

    private static final Color C_HEADER = new Color(15, 20, 40);
    private static final Color C_ACCENT = new Color(255, 200, 50);
    private static final Color C_AZUL = new Color(25, 85, 140);
    private static final Color C_VERDE = new Color(30, 120, 60);
    private static final Color C_ROJO = new Color(180, 40, 40);
    private static final Color C_FONDO = new Color(230, 235, 245);
    private static final Color C_PANEL = new Color(245, 248, 255);

    private JRadioButton rbTodas, rbRango;
    private JTextField txtFechaDesde, txtFechaHasta;
    private JComboBox<String> cmbFechaRapida;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotalVentas, lblMontoTotal;

    private List<Venta> ventasActuales;
    private String rangoDescripcionActual = "Todas las fechas";

    public FrmReporteVentasAutos(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        initComponents();
        cargarTodas();
    }

    private void initComponents() {
        setTitle("CarZone — Reporte de Venta de Autos");
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 8));
        panelPrincipal.setBackground(C_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        panelPrincipal.add(crearHeader(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 8));
        panelCentro.setBackground(C_FONDO);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        panelCentro.add(crearPanelResumen(), BorderLayout.NORTH);

        JPanel panelMedio = new JPanel(new BorderLayout(0, 6));
        panelMedio.setBackground(C_FONDO);
        panelMedio.add(crearPanelFiltro(), BorderLayout.NORTH);
        panelMedio.add(crearPanelTabla(), BorderLayout.CENTER);

        panelCentro.add(panelMedio, BorderLayout.CENTER);
        panelCentro.add(crearPanelBotones(), BorderLayout.SOUTH);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private JPanel crearHeader() {
        JPanel p = new JPanel(null);
        p.setBackground(C_HEADER);
        p.setPreferredSize(new Dimension(1080, 60));

        JLabel lblTitulo = new JLabel("CARZONE  |  Reporte de Venta de Autos");
        lblTitulo.setBounds(16, 8, 550, 26);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(C_ACCENT);

        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombreUsuario()
                + "   |   Rol: " + usuarioActual.getRol().toUpperCase());
        lblUsuario.setBounds(16, 36, 500, 16);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 11));
        lblUsuario.setForeground(new Color(150, 200, 255));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(960, 15, 100, 30);
        estilizarBoton(btnCerrar, C_ROJO, Color.WHITE);
        btnCerrar.addActionListener(e -> dispose());

        p.add(lblTitulo);
        p.add(lblUsuario);
        p.add(btnCerrar);
        return p;
    }

    private JPanel crearPanelResumen() {
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
        p.setBackground(C_FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));

        lblTotalVentas = crearTarjetaMetrica("0");
        lblMontoTotal = crearTarjetaMetrica("S/. 0.00");

        p.add(contenedorTarjeta("Ventas en el reporte", lblTotalVentas, C_AZUL));
        p.add(contenedorTarjeta("Monto total (completadas)", lblMontoTotal, C_VERDE));
        return p;
    }

    private JLabel crearTarjetaMetrica(String valor) {
        JLabel lbl = new JLabel(valor, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JPanel contenedorTarjeta(String titulo, JLabel lblValor, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
        lblTit.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTit.setForeground(new Color(200, 230, 255));
        p.add(lblTit, BorderLayout.NORTH);
        p.add(lblValor, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelFiltro() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBackground(C_PANEL);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 220)),
                "Filtrar por Fecha", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11), C_AZUL));

        rbTodas = new JRadioButton("Todas las fechas", true);
        rbRango = new JRadioButton("Rango de fechas");
        rbTodas.setBackground(C_PANEL);
        rbRango.setBackground(C_PANEL);
        rbTodas.setFont(new Font("Arial", Font.PLAIN, 12));
        rbRango.setFont(new Font("Arial", Font.PLAIN, 12));

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbTodas);
        grupo.add(rbRango);

        txtFechaDesde = campoTexto(10);
        txtFechaDesde.setText("aaaa-mm-dd");
        txtFechaDesde.setEnabled(false);

        txtFechaHasta = campoTexto(10);
        txtFechaHasta.setText("aaaa-mm-dd");
        txtFechaHasta.setEnabled(false);

        JButton btnAplicar = new JButton("Aplicar Filtro");
        estilizarBoton(btnAplicar, C_AZUL, Color.WHITE);

        cmbFechaRapida = new JComboBox<>(new String[]{
            "Fecha exacta...", "Ventas de hoy", "Última semana", "Último mes", "Último año"
        });
        cmbFechaRapida.setFont(new Font("Arial", Font.PLAIN, 12));

        p.add(rbTodas);
        p.add(rbRango);
        p.add(new JLabel("Desde:"));
        p.add(txtFechaDesde);
        p.add(new JLabel("Hasta:"));
        p.add(txtFechaHasta);
        p.add(btnAplicar);
        p.add(new JLabel("|"));
        p.add(cmbFechaRapida);

        rbTodas.addActionListener(e -> {
            txtFechaDesde.setEnabled(false);
            txtFechaHasta.setEnabled(false);
            txtFechaDesde.setText("aaaa-mm-dd");
            txtFechaHasta.setText("aaaa-mm-dd");
            cargarTodas();
        });
        rbRango.addActionListener(e -> {
            txtFechaDesde.setEnabled(true);
            txtFechaHasta.setEnabled(true);
        });

        btnAplicar.addActionListener(e -> aplicarFiltro());

        cmbFechaRapida.addActionListener(e -> {
            int idx = cmbFechaRapida.getSelectedIndex();
            if (idx <= 0) {
                return;
            }
            LocalDate hoy = LocalDate.now();
            LocalDate desde;
            String etiqueta;
            switch (idx) {
                case 1:
                    desde = hoy;
                    etiqueta = "Ventas de hoy";
                    break;
                case 2:
                    desde = hoy.minusWeeks(1);
                    etiqueta = "Ventas de la última semana";
                    break;
                case 3:
                    desde = hoy.minusMonths(1);
                    etiqueta = "Ventas del último mes";
                    break;
                default:
                    desde = hoy.minusYears(1);
                    etiqueta = "Ventas del último año";
                    break;
            }
            aplicarFiltroRapido(etiqueta, desde, hoy);
            cmbFechaRapida.setSelectedIndex(0);
        });

        return p;
    }

    private void aplicarFiltroRapido(String etiqueta, LocalDate desdeLd, LocalDate hastaLd) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        String desde = desdeLd.format(fmt);
        String hasta = hastaLd.format(fmt);

        rbRango.setSelected(true);
        txtFechaDesde.setEnabled(true);
        txtFechaHasta.setEnabled(true);
        txtFechaDesde.setText(desde);
        txtFechaHasta.setText(hasta);

        ventasActuales = ventaDAO.buscarPorCriteria(null, null, null, desde, hasta);
        rangoDescripcionActual = etiqueta + " (" + calcularDescripcionFecha(ventasActuales) + ")";
        refrescarTabla();
    }

    private String calcularDescripcionFecha(List<Venta> ventas) {
        if (ventas == null || ventas.isEmpty()) {
            return "Sin ventas registradas";
        }
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd/MM/yyyy");
        Date minFecha = null, maxFecha = null;
        for (Venta v : ventas) {
            Date f = v.getFechaVenta();
            if (f == null) {
                continue;
            }
            if (minFecha == null || f.before(minFecha)) {
                minFecha = f;
            }
            if (maxFecha == null || f.after(maxFecha)) {
                maxFecha = f;
            }
        }
        if (minFecha == null) {
            return "Sin fecha registrada";
        }
        String minStr = sdfDia.format(minFecha);
        String maxStr = sdfDia.format(maxFecha);
        if (minStr.equals(maxStr)) {
            return "Fecha: " + minStr;
        }
        return "Rango de fechas: desde " + minStr + " hasta " + maxStr;
    }

    private JScrollPane crearPanelTabla() {
        String[] columnas = {"ID Venta", "Fecha", "Cliente", "Auto", "Vendedor", "Estado", "Monto (S/.)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(C_HEADER);
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.setSelectionBackground(new Color(180, 210, 255));
        tabla.setGridColor(new Color(200, 210, 230));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] anchos = {70, 120, 170, 220, 100, 90, 100};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 220)),
                "Vista Previa", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11), C_AZUL));
        return scroll;
    }

    private JPanel crearPanelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        p.setBackground(C_FONDO);

        JButton btnDescargar = new JButton("Descargar PDF");
        estilizarBoton(btnDescargar, C_VERDE, Color.WHITE);
        btnDescargar.addActionListener(e -> descargarPDF());

        p.add(btnDescargar);
        return p;
    }

    private void cargarTodas() {
        ventasActuales = ventaDAO.listarTodas();
        rangoDescripcionActual = calcularDescripcionFecha(ventasActuales);
        refrescarTabla();
    }

    private void aplicarFiltro() {
        if (rbTodas.isSelected()) {
            cargarTodas();
            return;
        }

        String desde = txtFechaDesde.getText().trim();
        String hasta = txtFechaHasta.getText().trim();

        if (desde.isEmpty() || desde.equals("aaaa-mm-dd") || hasta.isEmpty() || hasta.equals("aaaa-mm-dd")) {
            JOptionPane.showMessageDialog(this, "Ingrese ambas fechas en formato aaaa-mm-dd.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validarFecha(desde, "Desde") == null || validarFecha(hasta, "Hasta") == null) {
            return;
        }

        if (desde.compareTo(hasta) > 0) {
            JOptionPane.showMessageDialog(this, "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.",
                    "Rango inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ventasActuales = ventaDAO.buscarPorCriteria(null, null, null, desde, hasta);
        rangoDescripcionActual = calcularDescripcionFecha(ventasActuales);
        refrescarTabla();
    }

    private java.util.Date validarFecha(String texto, String etiquetaCampo) {
        if (texto.length() != 10) {
            JOptionPane.showMessageDialog(this,
                    "La fecha '" + etiquetaCampo + "' debe tener exactamente 10 caracteres (aaaa-mm-dd).",
                    "Longitud inválida", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (!texto.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                    "La fecha '" + etiquetaCampo + "' solo debe contener números en formato aaaa-mm-dd.",
                    "Tipo de dato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int anio = Integer.parseInt(texto.substring(0, 4));
        if (anio < 2025 || anio > 2026) {
            JOptionPane.showMessageDialog(this,
                    "El año de la fecha '" + etiquetaCampo + "' debe estar entre 2025 y 2026.",
                    "Rango inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            return sdf.parse(texto);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                    "La fecha '" + etiquetaCampo + "' no corresponde a un día del calendario válido.",
                    "Rango inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        BigDecimal total = BigDecimal.ZERO;
        for (Venta v : ventasActuales) {
            modeloTabla.addRow(new Object[]{
                v.getIdVenta(),
                v.getFechaVenta() != null ? sdf.format(v.getFechaVenta()) : "-",
                v.getClienteCompleto(),
                v.getAutoDescripcion(),
                v.getNombreUsuario(),
                v.getEstadoVenta(),
                v.getMontoTotal() != null ? String.format("%,.2f", v.getMontoTotal()) : "0.00"
            });
            if ("completada".equalsIgnoreCase(v.getEstadoVenta()) && v.getMontoTotal() != null) {
                total = total.add(v.getMontoTotal());
            }
        }

        lblTotalVentas.setText(String.valueOf(ventasActuales.size()));
        lblMontoTotal.setText(String.format("S/. %,.2f", total));
    }

    private void descargarPDF() {
        if (ventasActuales == null) {
            ventasActuales = java.util.Collections.emptyList();
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Reporte de Venta de Autos");
        String nombreSugerido = "ReporteVentasAutos_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".pdf";
        chooser.setSelectedFile(new File(nombreSugerido));

        int seleccion = chooser.showSaveDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();
        String ruta = archivo.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta = ruta + ".pdf";
        }

        try {
            Reporte reporte = ReporteFactory.crear(
                    ReporteFactory.TIPO_VENTAS_AUTOS,
                    ventasActuales,
                    rangoDescripcionActual,
                    usuarioActual.getNombreUsuario());
            reporte.exportarPDF(ruta);

            int abrir = JOptionPane.showConfirmDialog(this,
                    "El reporte se generó correctamente en:\n" + ruta + "\n\n¿Desea abrirlo ahora?",
                    "Reporte generado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (abrir == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(new File(ruta));
                } catch (Exception exAbrir) {
                    JOptionPane.showMessageDialog(this, "No se pudo abrir el archivo automáticamente.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte PDF:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void estilizarBoton(JButton btn, Color fondo, Color texto) {
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JTextField campoTexto(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(new Font("Arial", Font.PLAIN, 12));
        return tf;
    }
}
