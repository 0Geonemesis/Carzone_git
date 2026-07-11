package carzone.gui;

import carzone.dao.AutoDAO;
import carzone.dao.ClienteDAO;
import carzone.dao.VentaDAO;
import carzone.modelo.Auto;
import carzone.modelo.Cliente;
import carzone.modelo.Usuario;
import carzone.modelo.Venta;
import carzone.patron.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

public class FrmVentas extends JFrame implements VentaObserver {

    private final VentaDAO ventaDAO = new VentaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final AutoDAO autoDAO = new AutoDAO();

    private final VentaSubject subject = new VentaSubject();

    private final Usuario usuarioActual;

    private JLabel lblTotalVentas, lblMontoTotal, lblVentasCompletadas, lblVentasAnuladas;

    private JTextField txtBuscarId, txtBuscarDni, txtFechaDesde, txtFechaHasta;
    private JComboBox<String> cmbFiltroEstado;

    private JTextField txtBuscarIdSimple, txtFechaDesdeSimple, txtFechaHastaSimple;
    private JComboBox<String> cmbFiltroEstadoSimple;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JTable tablaSimple;
    private DefaultTableModel modeloTablaSimple;

    private JTabbedPane tabsConsulta;

    private static final Color C_HEADER = new Color(15, 20, 40);
    private static final Color C_ACCENT = new Color(255, 200, 50);
    private static final Color C_AZUL = new Color(25, 85, 140);
    private static final Color C_VERDE = new Color(30, 120, 60);
    private static final Color C_ROJO = new Color(180, 40, 40);
    private static final Color C_FONDO = new Color(230, 235, 245);
    private static final Color C_PANEL = new Color(245, 248, 255);

    public FrmVentas(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        subject.agregarObservador(this);
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("CarZone — Módulo de Ventas");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 8));
        panelPrincipal.setBackground(C_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        panelPrincipal.add(crearHeader(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 6));
        panelCentro.setBackground(C_FONDO);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        tabsConsulta = new JTabbedPane();
        tabsConsulta.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel panelMultiTabla = new JPanel(new BorderLayout(0, 6));
        panelMultiTabla.setBackground(C_FONDO);
        panelMultiTabla.add(crearPanelBusqueda(), BorderLayout.NORTH);
        panelMultiTabla.add(crearPanelTabla(), BorderLayout.CENTER);

        JPanel panelUnaTabla = new JPanel(new BorderLayout(0, 6));
        panelUnaTabla.setBackground(C_FONDO);
        panelUnaTabla.add(crearPanelBusquedaSimple(), BorderLayout.NORTH);
        panelUnaTabla.add(crearPanelTablaSimple(), BorderLayout.CENTER);

        tabsConsulta.addTab("Consulta (2 tablas)", panelMultiTabla);
        tabsConsulta.addTab("Consulta (1 tabla)", panelUnaTabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                tablaSimple.clearSelection();
            }
        });
        tablaSimple.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaSimple.getSelectedRow() >= 0) {
                tabla.clearSelection();
            }
        });

        tabsConsulta.addChangeListener(e -> {
            if (tabsConsulta.getSelectedIndex() == 0) {
                cargarDatos();
            } else {
                cargarDatosSimple();
            }
        });

        panelCentro.add(tabsConsulta, BorderLayout.CENTER);
        panelCentro.add(crearPanelBotones(), BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(C_FONDO);
        wrapper.add(crearDashboard(), BorderLayout.NORTH);
        wrapper.add(panelCentro, BorderLayout.CENTER);

        panelPrincipal.add(wrapper, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private JPanel crearHeader() {
        JPanel p = new JPanel(null);
        p.setBackground(C_HEADER);
        p.setPreferredSize(new Dimension(1100, 60));

        JLabel lblTitulo = new JLabel("CARZONE  |  Módulo de Ventas");
        lblTitulo.setBounds(16, 8, 500, 26);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(C_ACCENT);

        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombreUsuario()
                + "   |   Rol: " + usuarioActual.getRol().toUpperCase());
        lblUsuario.setBounds(16, 36, 500, 16);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 11));
        lblUsuario.setForeground(new Color(150, 200, 255));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(980, 15, 100, 30);
        estilizarBoton(btnCerrar, C_ROJO, Color.WHITE);
        btnCerrar.addActionListener(e -> dispose());

        p.add(lblTitulo);
        p.add(lblUsuario);
        p.add(btnCerrar);
        return p;
    }

    private JPanel crearDashboard() {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setBackground(C_FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));

        lblTotalVentas = crearTarjetaMetrica("Total Ventas", "0", new Color(25, 85, 140));
        lblMontoTotal = crearTarjetaMetrica("Monto Acumulado", "S/. 0.00", new Color(30, 100, 60));
        lblVentasCompletadas = crearTarjetaMetrica("Completadas", "0", new Color(20, 100, 90));
        lblVentasAnuladas = crearTarjetaMetrica("Anuladas", "0", new Color(150, 40, 40));

        p.add(contenedorTarjeta("Total Ventas", lblTotalVentas, new Color(25, 85, 140)));
        p.add(contenedorTarjeta("Monto Acumulado", lblMontoTotal, new Color(30, 100, 60)));
        p.add(contenedorTarjeta("Completadas", lblVentasCompletadas, new Color(20, 100, 90)));
        p.add(contenedorTarjeta("Anuladas", lblVentasAnuladas, new Color(150, 40, 40)));
        return p;
    }

    private JLabel crearTarjetaMetrica(String titulo, String valor, Color color) {
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

    private JPanel crearPanelBusqueda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBackground(C_PANEL);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 220)),
                "Búsqueda y Filtrado", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11), C_AZUL));

        txtBuscarId = campoTexto(8);
        txtBuscarDni = campoTexto(10);
        txtFechaDesde = campoTexto(10);
        txtFechaDesde.setText("aaaa-mm-dd");
        txtFechaHasta = campoTexto(10);
        txtFechaHasta.setText("aaaa-mm-dd");

        cmbFiltroEstado = new JComboBox<>(new String[]{"(Todos)", "completada", "anulada", "en proceso"});
        cmbFiltroEstado.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar");
        estilizarBoton(btnBuscar, C_AZUL, Color.WHITE);
        estilizarBoton(btnLimpiar, new Color(90, 90, 90), Color.WHITE);

        p.add(new JLabel("ID Venta:"));
        p.add(txtBuscarId);
        p.add(new JLabel("DNI Cliente:"));
        p.add(txtBuscarDni);
        p.add(new JLabel("Estado:"));
        p.add(cmbFiltroEstado);
        p.add(new JLabel("Desde:"));
        p.add(txtFechaDesde);
        p.add(new JLabel("Hasta:"));
        p.add(txtFechaHasta);
        p.add(btnBuscar);
        p.add(btnLimpiar);

        btnBuscar.addActionListener(e -> buscarVentas());
        btnLimpiar.addActionListener(e -> {
            txtBuscarId.setText("");
            txtBuscarDni.setText("");
            txtFechaDesde.setText("aaaa-mm-dd");
            txtFechaHasta.setText("aaaa-mm-dd");
            cmbFiltroEstado.setSelectedIndex(0);
            cargarDatos();
        });
        return p;
    }

    private JScrollPane crearPanelTabla() {
        String[] columnas = {"ID Venta", "Cliente", "DNI", "Fecha", "Monto (S/.)", "Estado"};
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

        int[] anchos = {80, 200, 100, 140, 110, 110};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(1060, 330));
        return scroll;
    }

    private JPanel crearPanelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        p.setBackground(C_FONDO);

        JButton btnNueva = new JButton("Nueva Venta");
        JButton btnVer = new JButton("Ver Detalle");
        JButton btnModificar = new JButton("Modificar Estado");
        JButton btnAnular = new JButton("Anular (Lógica)");
        JButton btnEliminar = new JButton("Eliminar (Física)");

        estilizarBoton(btnNueva, C_VERDE, Color.WHITE);
        estilizarBoton(btnVer, C_AZUL, Color.WHITE);
        estilizarBoton(btnModificar, new Color(120, 80, 20), Color.WHITE);
        estilizarBoton(btnAnular, new Color(180, 100, 20), Color.WHITE);
        estilizarBoton(btnEliminar, C_ROJO, Color.WHITE);

        p.add(btnNueva);
        p.add(btnVer);
        p.add(btnModificar);
        p.add(btnAnular);
        p.add(btnEliminar);

        btnNueva.addActionListener(e -> abrirWizardNuevaVenta());
        btnVer.addActionListener(e -> verDetalleVenta());
        btnModificar.addActionListener(e -> modificarEstadoVenta());
        btnAnular.addActionListener(e -> eliminarVenta(false));
        btnEliminar.addActionListener(e -> {
            if ("administrador".equals(usuarioActual.getRol())) {
                eliminarVenta(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Solo el administrador puede realizar la eliminación física.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            }
        });
        return p;
    }

    private void cargarDatos() {
        try {
            List<Venta> ventas = ventaDAO.listarDosTablas();
            refrescarTabla(ventas);
            actualizarDashboard(ventas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ventas:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla(List<Venta> ventas) {
        modeloTabla.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Venta v : ventas) {
            modeloTabla.addRow(new Object[]{
                v.getIdVenta(),
                v.getClienteCompleto(),
                v.getDniCliente(),
                v.getFechaVenta() != null ? sdf.format(v.getFechaVenta()) : "",
                v.getMontoTotal() != null ? String.format("%.2f", v.getMontoTotal()) : "",
                v.getEstadoVenta()
            });
        }
    }

    private void actualizarDashboard(List<Venta> ventas) {
        int total = ventas.size();
        BigDecimal monto = BigDecimal.ZERO;
        long completadas = 0, anuladas = 0;
        for (Venta v : ventas) {
            if (v.getMontoTotal() != null && !"anulada".equals(v.getEstadoVenta())) {
                monto = monto.add(v.getMontoTotal());
            }
            if ("completada".equals(v.getEstadoVenta())) {
                completadas++;
            }
            if ("anulada".equals(v.getEstadoVenta())) {
                anuladas++;
            }
        }
        lblTotalVentas.setText(String.valueOf(total));
        lblMontoTotal.setText("S/. " + String.format("%,.2f", monto));
        lblVentasCompletadas.setText(String.valueOf(completadas));
        lblVentasAnuladas.setText(String.valueOf(anuladas));
    }

    private void buscarVentas() {
        String id = txtBuscarId.getText().trim();
        String dni = txtBuscarDni.getText().trim();
        String estado = (String) cmbFiltroEstado.getSelectedItem();
        String desde = txtFechaDesde.getText().trim();
        String hasta = txtFechaHasta.getText().trim();

        if ("(Todos)".equals(estado)) {
            estado = "";
        }
        if ("aaaa-mm-dd".equals(desde)) {
            desde = "";
        }
        if ("aaaa-mm-dd".equals(hasta)) {
            hasta = "";
        }

        if (!desde.isEmpty() && !desde.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Fecha 'Desde' inválida. Use el formato aaaa-mm-dd.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!hasta.isEmpty() && !hasta.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Fecha 'Hasta' inválida. Use el formato aaaa-mm-dd.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Venta> resultado = ventaDAO.buscarDosTablas(id, dni, estado, desde, hasta);
            refrescarTabla(resultado);
            actualizarDashboard(resultado);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en la búsqueda:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Venta obtenerVentaSeleccionada() {
        String idVenta = null;
        if (tabsConsulta.getSelectedIndex() == 0) {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                idVenta = (String) modeloTabla.getValueAt(fila, 0);
            }
        } else {
            int fila = tablaSimple.getSelectedRow();
            if (fila >= 0) {
                idVenta = (String) modeloTablaSimple.getValueAt(fila, 0);
            }
        }
        if (idVenta == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta de la tabla.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return ventaDAO.buscarPorIdCompleto(idVenta);
    }

    private void abrirWizardNuevaVenta() {
        JDialog dlg = new JDialog(this, "Nueva Venta — Proceso Guiado", true);
        dlg.setSize(680, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));

        final Cliente[] clienteSeleccionado = {null};
        final Auto[] autoSeleccionado = {null};

        JPanel paso1 = new JPanel(null);
        paso1.setBackground(C_PANEL);

        JTextField txtDniBuscar = campo(paso1, "DNI del cliente:", 20, 20, 140, 25);
        JButton btnBuscarCliente = new JButton("Buscar");
        btnBuscarCliente.setBounds(200, 20, 90, 25);
        estilizarBoton(btnBuscarCliente, C_AZUL, Color.WHITE);
        paso1.add(btnBuscarCliente);

        JTextField txtNombres = campo(paso1, "Nombres *:", 20, 65, 200, 25);
        JTextField txtApellidos = campo(paso1, "Apellidos *:", 20, 110, 200, 25);
        JTextField txtDni = campo(paso1, "DNI *:", 20, 155, 140, 25);
        JTextField txtTelefono = campo(paso1, "Teléfono:", 20, 200, 140, 25);
        JTextField txtCorreo = campo(paso1, "Correo:", 20, 245, 300, 25);
        JTextField txtDireccion = campo(paso1, "Dirección:", 20, 290, 400, 25);

        JLabel lblNuevoCliente = new JLabel("(Llena los campos para registrar un nuevo cliente)");
        lblNuevoCliente.setBounds(20, 330, 440, 18);
        lblNuevoCliente.setFont(new Font("Arial", Font.ITALIC, 11));
        lblNuevoCliente.setForeground(new Color(100, 100, 150));
        paso1.add(lblNuevoCliente);

        btnBuscarCliente.addActionListener(e -> {
            String dniBuscar = txtDniBuscar.getText().trim();
            if (!ValidadorVenta.esDniValido(dniBuscar)) {
                JOptionPane.showMessageDialog(dlg, ValidadorVenta.mensajeDni(), "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cliente c = clienteDAO.buscarPorDni(dniBuscar);
            if (c != null) {
                clienteSeleccionado[0] = c;
                txtNombres.setText(c.getNombres());
                txtApellidos.setText(c.getApellidos());
                txtDni.setText(c.getDni());
                txtTelefono.setText(c.getTelefono() != null ? c.getTelefono() : "");
                txtCorreo.setText(c.getCorreo() != null ? c.getCorreo() : "");
                txtDireccion.setText(c.getDireccion() != null ? c.getDireccion() : "");
                lblNuevoCliente.setText("✔ Cliente encontrado: " + c.getNombres() + " " + c.getApellidos());
                lblNuevoCliente.setForeground(new Color(20, 120, 50));
            } else {
                clienteSeleccionado[0] = null;
                lblNuevoCliente.setText("Cliente no encontrado. Completa los datos para registrarlo.");
                lblNuevoCliente.setForeground(new Color(180, 90, 20));
            }
        });

        tabs.addTab("Paso 1: Cliente", paso1);

        JPanel paso2 = new JPanel(new BorderLayout(0, 8));
        paso2.setBackground(C_PANEL);
        paso2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pFiltroAuto = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pFiltroAuto.setBackground(C_PANEL);
        JTextField txtFiltroMarca = new JTextField(10);
        JButton btnFiltrarAutos = new JButton("Filtrar");
        estilizarBoton(btnFiltrarAutos, C_AZUL, Color.WHITE);
        pFiltroAuto.add(new JLabel("Filtrar por marca:"));
        pFiltroAuto.add(txtFiltroMarca);
        pFiltroAuto.add(btnFiltrarAutos);

        String[] colsAuto = {"ID", "Marca", "Modelo", "Año", "Color", "Precio (S/.)", "Estado"};
        DefaultTableModel modeloAutos = new DefaultTableModel(colsAuto, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tablaAutos = new JTable(modeloAutos);
        tablaAutos.setRowHeight(22);
        tablaAutos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaAutos.getTableHeader().setBackground(C_HEADER);
        tablaAutos.getTableHeader().setForeground(Color.BLACK);
        tablaAutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JLabel lblAutoSelec = new JLabel("Ningún auto seleccionado");
        lblAutoSelec.setFont(new Font("Arial", Font.ITALIC, 11));
        lblAutoSelec.setForeground(new Color(100, 100, 150));
        lblAutoSelec.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        Runnable cargarAutosDisponibles = () -> {
            modeloAutos.setRowCount(0);
            String marca = txtFiltroMarca.getText().trim();
            List<Auto> autos = marca.isEmpty()
                    ? autoDAO.listarTodos()
                    : autoDAO.buscarPorCriteria(marca, null, 0, null);
            for (Auto a : autos) {
                if ("disponible".equals(a.getEstado())) {
                    modeloAutos.addRow(new Object[]{
                        a.getIdAuto(), a.getMarca(), a.getModelo(),
                        a.getAnio(), a.getColor(),
                        String.format("%.2f", a.getPrecio()), a.getEstado()
                    });
                }
            }
        };
        cargarAutosDisponibles.run();

        tablaAutos.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && tablaAutos.getSelectedRow() >= 0) {
                int fila = tablaAutos.getSelectedRow();
                String idAuto = (String) modeloAutos.getValueAt(fila, 0);
                autoSeleccionado[0] = autoDAO.buscarPorId(idAuto);
                if (autoSeleccionado[0] != null) {
                    lblAutoSelec.setText("✔ Seleccionado: " + autoSeleccionado[0]
                            + "  |  Precio: S/. " + String.format("%.2f", autoSeleccionado[0].getPrecio()));
                    lblAutoSelec.setForeground(new Color(20, 120, 50));
                }
            }
        });

        btnFiltrarAutos.addActionListener(e -> cargarAutosDisponibles.run());

        paso2.add(pFiltroAuto, BorderLayout.NORTH);
        paso2.add(new JScrollPane(tablaAutos), BorderLayout.CENTER);
        paso2.add(lblAutoSelec, BorderLayout.SOUTH);
        tabs.addTab("Paso 2: Auto", paso2);

        JPanel paso3 = new JPanel(null);
        paso3.setBackground(C_PANEL);

        JTextArea txtResumen = new JTextArea();
        txtResumen.setBounds(20, 20, 600, 200);
        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResumen.setBorder(BorderFactory.createLineBorder(new Color(180, 190, 220)));
        paso3.add(txtResumen);

        JLabel lblPrecioFinal = new JLabel("Monto total: S/. --");
        lblPrecioFinal.setBounds(20, 230, 400, 24);
        lblPrecioFinal.setFont(new Font("Arial", Font.BOLD, 14));
        lblPrecioFinal.setForeground(C_VERDE);
        paso3.add(lblPrecioFinal);

        JButton btnConfirmar = new JButton("Confirmar y Registrar Venta");
        btnConfirmar.setBounds(20, 270, 260, 36);
        estilizarBoton(btnConfirmar, C_VERDE, Color.WHITE);
        paso3.add(btnConfirmar);

        JButton btnCancelarWizard = new JButton("Cancelar");
        btnCancelarWizard.setBounds(295, 270, 120, 36);
        estilizarBoton(btnCancelarWizard, C_ROJO, Color.WHITE);
        paso3.add(btnCancelarWizard);

        tabs.addChangeListener(ev -> {
            if (tabs.getSelectedIndex() == 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("=== RESUMEN DE VENTA ===\n\n");
                if (clienteSeleccionado[0] != null) {
                    sb.append("CLIENTE EXISTENTE:\n");
                    sb.append("  Nombre : ").append(clienteSeleccionado[0].getNombres())
                            .append(" ").append(clienteSeleccionado[0].getApellidos()).append("\n");
                    sb.append("  DNI    : ").append(clienteSeleccionado[0].getDni()).append("\n");
                } else {
                    sb.append("NUEVO CLIENTE A REGISTRAR:\n");
                    sb.append("  Nombre : ").append(txtNombres.getText().trim())
                            .append(" ").append(txtApellidos.getText().trim()).append("\n");
                    sb.append("  DNI    : ").append(txtDni.getText().trim()).append("\n");
                }
                sb.append("\nAUTO:\n");
                if (autoSeleccionado[0] != null) {
                    sb.append("  ").append(autoSeleccionado[0].toString()).append("\n");
                    sb.append("  Precio : S/. ").append(String.format("%.2f", autoSeleccionado[0].getPrecio())).append("\n");
                    lblPrecioFinal.setText("Monto total: S/. " + String.format("%.2f", autoSeleccionado[0].getPrecio()));
                } else {
                    sb.append("  (ningún auto seleccionado)\n");
                    lblPrecioFinal.setText("Monto total: S/. --");
                }
                sb.append("\nVendedor: ").append(usuarioActual.getNombreUsuario());
                txtResumen.setText(sb.toString());
            }
        });

        btnConfirmar.addActionListener(e -> {
            if (!validarYRegistrarVenta(
                    clienteSeleccionado, autoSeleccionado,
                    txtNombres, txtApellidos, txtDni, txtTelefono, txtCorreo, txtDireccion)) {
                return;
            }
            dlg.dispose();
            cargarDatos();
            cargarDatosSimple();
        });
        btnCancelarWizard.addActionListener(e -> dlg.dispose());

        tabs.addTab("Paso 3: Confirmar", paso3);

        dlg.add(tabs);
        dlg.setVisible(true);
    }

    private boolean validarYRegistrarVenta(
            Cliente[] clienteRef, Auto[] autoRef,
            JTextField txtN, JTextField txtAp, JTextField txtDni,
            JTextField txtTel, JTextField txtCor, JTextField txtDir) {

        if (autoRef[0] == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un auto en el Paso 2.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Cliente cliente = clienteRef[0];

        if (cliente == null) {
            String nombres = txtN.getText().trim();
            String apellidos = txtAp.getText().trim();
            String dni = txtDni.getText().trim();
            String telefono = txtTel.getText().trim();
            String correo = txtCor.getText().trim();
            String direccion = txtDir.getText().trim();

            if (!ValidadorVenta.esNombresValido(nombres)) {
                JOptionPane.showMessageDialog(this, ValidadorVenta.mensajeNombres(), "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!ValidadorVenta.esApellidosValido(apellidos)) {
                JOptionPane.showMessageDialog(this, ValidadorVenta.mensajeApellidos(), "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!ValidadorVenta.esDniValido(dni)) {
                JOptionPane.showMessageDialog(this, ValidadorVenta.mensajeDni(), "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!ValidadorVenta.esTelefonoValido(telefono)) {
                JOptionPane.showMessageDialog(this, ValidadorVenta.mensajeTelefono(), "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!ValidadorVenta.esCorreoValido(correo)) {
                JOptionPane.showMessageDialog(this, ValidadorVenta.mensajeCorreo(), "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!ValidadorVenta.esDireccionValida(direccion)) {
                JOptionPane.showMessageDialog(this, ValidadorVenta.mensajeDireccion(), "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            Cliente clienteExistente = clienteDAO.buscarPorDni(dni);
            if (clienteExistente != null) {

                boolean mismaPersona
                        = clienteExistente.getNombres().trim().equalsIgnoreCase(nombres)
                        && clienteExistente.getApellidos().trim().equalsIgnoreCase(apellidos);

                if (!mismaPersona) {
                    JOptionPane.showMessageDialog(this,
                            "Ya existe un cliente registrado con el DNI " + dni + ":\n"
                            + clienteExistente.getNombres() + " " + clienteExistente.getApellidos()
                            + "\n\nLos datos ingresados no coinciden con ese registro.\n"
                            + "Verifique el DNI ingresado.",
                            "Cliente ya registrado", JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                cliente = clienteExistente;
                clienteRef[0] = cliente;
            } else {
                if (clienteDAO.tieneDniDuplicado(dni, null)) {
                    JOptionPane.showMessageDialog(this, "Ya existe un cliente con DNI: " + dni, "Validación", JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                if (!telefono.isEmpty()) {
                    Cliente porTelefono = clienteDAO.buscarPorTelefono(telefono);
                    if (porTelefono != null) {
                        JOptionPane.showMessageDialog(this,
                                "Ya existe un cliente registrado con este teléfono:\n"
                                + porTelefono.getNombres() + " " + porTelefono.getApellidos()
                                + " (DNI: " + porTelefono.getDni() + ")",
                                "Dato duplicado", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
                if (!correo.isEmpty()) {
                    Cliente porCorreo = clienteDAO.buscarPorCorreo(correo);
                    if (porCorreo != null) {
                        JOptionPane.showMessageDialog(this,
                                "Ya existe un cliente registrado con este correo:\n"
                                + porCorreo.getNombres() + " " + porCorreo.getApellidos()
                                + " (DNI: " + porCorreo.getDni() + ")",
                                "Dato duplicado", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }

                cliente = new Cliente(clienteDAO.generarNuevoId(), nombres, apellidos, dni,
                        telefono.isEmpty() ? null : telefono,
                        correo.isEmpty() ? null : correo,
                        direccion.isEmpty() ? null : direccion);
                if (!clienteDAO.insertar(cliente)) {
                    JOptionPane.showMessageDialog(this, "Error al registrar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                clienteRef[0] = cliente;
            }
        }

        BigDecimal monto = autoRef[0].getPrecio();
        if (!ValidadorVenta.esMontoValido(monto.toPlainString())) {
            JOptionPane.showMessageDialog(this, ValidadorVenta.mensajeMonto(), "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Venta venta = new Venta(
                ventaDAO.generarNuevoId(),
                cliente.getIdCliente(),
                usuarioActual.getIdUsuario(),
                autoRef[0].getIdAuto(),
                null,
                monto,
                "completada"
        );

        if (ventaDAO.insertar(venta)) {
            subject.notificarRegistro(venta);
            JOptionPane.showMessageDialog(this, "Venta registrada exitosamente.\nID: " + venta.getIdVenta(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void verDetalleVenta() {
        Venta v = obtenerVentaSeleccionada();
        if (v == null) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String detalle
                = "══════════════════════════════════════\n"
                + "  DETALLE DE VENTA\n"
                + "══════════════════════════════════════\n"
                + "ID Venta     : " + v.getIdVenta() + "\n"
                + "Fecha        : " + (v.getFechaVenta() != null ? sdf.format(v.getFechaVenta()) : "N/A") + "\n"
                + "Estado       : " + v.getEstadoVenta().toUpperCase() + "\n"
                + "Monto Total  : S/. " + String.format("%.2f", v.getMontoTotal()) + "\n\n"
                + "──────────────────────────────────────\n"
                + "  DATOS DEL CLIENTE\n"
                + "──────────────────────────────────────\n"
                + "ID Cliente   : " + v.getIdCliente() + "\n"
                + "Nombre       : " + v.getClienteCompleto() + "\n"
                + "DNI          : " + v.getDniCliente() + "\n"
                + "Teléfono     : " + (v.getTelefonoCliente() != null ? v.getTelefonoCliente() : "—") + "\n"
                + "Correo       : " + (v.getCorreoCliente() != null ? v.getCorreoCliente() : "—") + "\n\n"
                + "──────────────────────────────────────\n"
                + "  DATOS DEL AUTO\n"
                + "──────────────────────────────────────\n"
                + "ID Auto      : " + v.getIdAuto() + "\n"
                + "Descripción  : " + v.getAutoDescripcion() + "\n\n"
                + "──────────────────────────────────────\n"
                + "  VENDEDOR\n"
                + "──────────────────────────────────────\n"
                + "Usuario      : " + v.getNombreUsuario() + "\n";

        JTextArea area = new JTextArea(detalle);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(C_PANEL);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 380));

        JOptionPane.showMessageDialog(this, scroll, "Detalle de Venta — " + v.getIdVenta(),
                JOptionPane.PLAIN_MESSAGE);
    }

    private void modificarEstadoVenta() {
        Venta v = obtenerVentaSeleccionada();
        if (v == null) {
            return;
        }

        if ("anulada".equals(v.getEstadoVenta())) {
            JOptionPane.showMessageDialog(this, "La venta ya está anulada y no puede modificarse.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opciones = {"completada", "en proceso", "anulada"};
        String nuevoEstado = (String) JOptionPane.showInputDialog(
                this,
                "Venta: " + v.getIdVenta() + "\nEstado actual: " + v.getEstadoVenta() + "\n\nSeleccione el nuevo estado:",
                "Modificar Estado de Venta",
                JOptionPane.QUESTION_MESSAGE, null, opciones, v.getEstadoVenta()
        );

        if (nuevoEstado == null || nuevoEstado.equals(v.getEstadoVenta())) {
            return;
        }

        if (!ValidadorVenta.esEstadoVentaValido(nuevoEstado)) {
            JOptionPane.showMessageDialog(this, ValidadorVenta.mensajeEstadoVenta(), "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Cambiar estado de '" + v.getEstadoVenta() + "' a '" + nuevoEstado + "'?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) {
            return;
        }

        v.setEstadoVenta(nuevoEstado);
        if (ventaDAO.actualizar(v)) {
            subject.notificarModificacion(v);
            JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
            cargarDatosSimple();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarVenta(boolean fisica) {
        Venta v = obtenerVentaSeleccionada();
        if (v == null) {
            return;
        }

        EstrategiaEliminarVenta estrategia = fisica
                ? new EstrategiaEliminarVentaFisica()
                : new EstrategiaEliminarVentaLogica();

        String msg = fisica
                ? "¿Eliminar FÍSICAMENTE la venta " + v.getIdVenta() + "?\n⚠ Esta acción es irreversible."
                : "¿Anular (eliminación lógica) la venta " + v.getIdVenta() + "?\nCambiará su estado a 'anulada'.";

        int conf = JOptionPane.showConfirmDialog(this, msg,
                estrategia.descripcion(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) {
            return;
        }

        if (estrategia.eliminar(v.getIdVenta(), ventaDAO)) {
            subject.notificarEliminacion(v.getIdVenta());
            JOptionPane.showMessageDialog(this, estrategia.descripcion() + " completada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
            cargarDatosSimple();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo completar la operación.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onVentaRegistrada(Venta venta) {
    }

    @Override
    public void onVentaModificada(Venta venta) {
    }

    @Override
    public void onVentaEliminada(String idVenta) {
    }

    private JPanel crearPanelBusquedaSimple() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBackground(C_PANEL);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 220)),
                "Búsqueda — Solo tabla VENTA", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11), C_AZUL));

        txtBuscarIdSimple = campoTexto(8);
        txtFechaDesdeSimple = campoTexto(10);
        txtFechaDesdeSimple.setText("aaaa-mm-dd");
        txtFechaHastaSimple = campoTexto(10);
        txtFechaHastaSimple.setText("aaaa-mm-dd");

        cmbFiltroEstadoSimple = new JComboBox<>(new String[]{"(Todos)", "completada", "anulada", "en proceso"});
        cmbFiltroEstadoSimple.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar");
        estilizarBoton(btnBuscar, C_AZUL, Color.WHITE);
        estilizarBoton(btnLimpiar, new Color(90, 90, 90), Color.WHITE);

        p.add(new JLabel("ID Venta:"));
        p.add(txtBuscarIdSimple);
        p.add(new JLabel("Estado:"));
        p.add(cmbFiltroEstadoSimple);
        p.add(new JLabel("Desde:"));
        p.add(txtFechaDesdeSimple);
        p.add(new JLabel("Hasta:"));
        p.add(txtFechaHastaSimple);
        p.add(btnBuscar);
        p.add(btnLimpiar);

        btnBuscar.addActionListener(e -> buscarVentasSimple());
        btnLimpiar.addActionListener(e -> {
            txtBuscarIdSimple.setText("");
            txtFechaDesdeSimple.setText("aaaa-mm-dd");
            txtFechaHastaSimple.setText("aaaa-mm-dd");
            cmbFiltroEstadoSimple.setSelectedIndex(0);
            cargarDatosSimple();
        });
        return p;
    }

    private JScrollPane crearPanelTablaSimple() {
        String[] columnas = {"ID Venta", "ID Cliente", "ID Usuario", "ID Auto", "Fecha", "Monto (S/.)", "Estado"};
        modeloTablaSimple = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaSimple = new JTable(modeloTablaSimple);
        tablaSimple.setRowHeight(24);
        tablaSimple.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaSimple.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaSimple.getTableHeader().setBackground(C_HEADER);
        tablaSimple.getTableHeader().setForeground(Color.BLACK);
        tablaSimple.setSelectionBackground(new Color(180, 210, 255));
        tablaSimple.setGridColor(new Color(200, 210, 230));
        tablaSimple.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] anchos = {80, 90, 90, 90, 130, 100, 100};
        for (int i = 0; i < anchos.length; i++) {
            tablaSimple.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll = new JScrollPane(tablaSimple);
        scroll.setPreferredSize(new Dimension(1060, 300));
        return scroll;
    }

    private void cargarDatosSimple() {
        try {
            List<Venta> ventas = ventaDAO.listarSoloTablaVenta();
            refrescarTablaSimple(ventas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ventas (tabla simple):\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarVentasSimple() {
        String id = txtBuscarIdSimple.getText().trim();
        String estado = (String) cmbFiltroEstadoSimple.getSelectedItem();
        String desde = txtFechaDesdeSimple.getText().trim();
        String hasta = txtFechaHastaSimple.getText().trim();

        if ("(Todos)".equals(estado)) {
            estado = "";
        }
        if ("aaaa-mm-dd".equals(desde)) {
            desde = "";
        }
        if ("aaaa-mm-dd".equals(hasta)) {
            hasta = "";
        }

        if (!desde.isEmpty() && !desde.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Fecha 'Desde' inválida. Use aaaa-mm-dd.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!hasta.isEmpty() && !hasta.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Fecha 'Hasta' inválida. Use aaaa-mm-dd.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Venta> resultado = ventaDAO.buscarSoloTablaVenta(id, estado, desde, hasta);
            refrescarTablaSimple(resultado);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en la búsqueda:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTablaSimple(List<Venta> ventas) {
        modeloTablaSimple.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Venta v : ventas) {
            modeloTablaSimple.addRow(new Object[]{
                v.getIdVenta(),
                v.getIdCliente(),
                v.getIdUsuario(),
                v.getIdAuto(),
                v.getFechaVenta() != null ? sdf.format(v.getFechaVenta()) : "",
                v.getMontoTotal() != null ? String.format("%.2f", v.getMontoTotal()) : "",
                v.getEstadoVenta()
            });
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

    private JTextField campo(JPanel panel, String etiqueta, int x, int y, int ancho, int alto) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setBounds(x, y - 18, 200, 16);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lbl);

        JTextField tf = new JTextField();
        tf.setBounds(x, y, ancho, alto);
        tf.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(tf);
        return tf;
    }
}
