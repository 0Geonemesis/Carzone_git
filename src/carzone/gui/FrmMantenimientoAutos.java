package carzone.gui;

import carzone.dao.AutoDAO;
import carzone.modelo.Auto;
import carzone.modelo.Usuario;
import carzone.patron.GeneradorCodigo4Cifras;
import carzone.patron.ValidadorAuto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class FrmMantenimientoAutos extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTable tablaSimple;
    private DefaultTableModel modeloTablaSimple;
    private JTabbedPane tabsConsulta;
    private JTextField txtIdAuto, txtMarca, txtModelo, txtAnio, txtColor, txtPrecio, txtCodigo;
    private JComboBox<String> cmbEstado;
    private JTextField txtBuscarMarca, txtBuscarModelo, txtBuscarAnio, txtBuscarColor;
    private JTextField txtBuscarMarcaSimple, txtBuscarModeloSimple, txtBuscarAnioSimple, txtBuscarColorSimple;
    private JButton btnNuevo, btnGuardar, btnModificar, btnEliminarLogico, btnEliminarFisico, btnBuscar, btnLimpiar, btnVerEliminados, btnRestaurar;
    private boolean viendoEliminados = false;
    private AutoDAO autoDAO;
    private Usuario usuarioActual;
    private boolean modoEdicion = false;

    public FrmMantenimientoAutos(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.autoDAO = new AutoDAO();
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        setTitle("CarZone - Mantenimiento de Autos");
        setSize(960, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(null);
        panelPrincipal.setBackground(new Color(245, 245, 245));

        JPanel panelHeader = new JPanel(null);
        panelHeader.setBounds(0, 0, 960, 50);
        panelHeader.setBackground(new Color(40, 100, 160));

        JLabel lblTitulo = new JLabel("Mantenimiento de Autos");
        lblTitulo.setBounds(20, 12, 400, 26);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelHeader.add(lblTitulo);

        JPanel panelForm = new JPanel(null);
        panelForm.setBounds(10, 60, 360, 380);
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Auto"));

        JLabel[] labels = {
            new JLabel("ID Auto:"), new JLabel("Marca:"), new JLabel("Modelo:"),
            new JLabel("Año:"), new JLabel("Color:"), new JLabel("Precio (S/.):"),
            new JLabel("Estado:"), new JLabel("Código 4 cifras:")
        };
        int[] labY = {25, 65, 105, 145, 185, 225, 265, 305};
        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(10, labY[i], 130, 22);
            labels[i].setFont(new Font("Arial", Font.BOLD, 12));
            panelForm.add(labels[i]);
        }

        txtIdAuto = crearTextField(140, 25, 200, panelForm);
        txtIdAuto.setEditable(false);
        txtIdAuto.setBackground(new Color(230, 230, 230));
        txtMarca = crearTextField(140, 65, 200, panelForm);
        limitarLongitud(txtMarca, ValidadorAuto.LONGITUD_MAX_MARCA);
        txtModelo = crearTextField(140, 105, 200, panelForm);
        limitarLongitud(txtModelo, ValidadorAuto.LONGITUD_MAX_MODELO);
        txtAnio = crearTextField(140, 145, 200, panelForm);
        limitarLongitud(txtAnio, 4);
        txtColor = crearTextField(140, 185, 200, panelForm);
        limitarLongitud(txtColor, ValidadorAuto.LONGITUD_MAX_COLOR);
        txtPrecio = crearTextField(140, 225, 200, panelForm);
        limitarLongitud(txtPrecio, 10);

        cmbEstado = new JComboBox<>(new String[]{"disponible", "reservado", "vendido"});
        cmbEstado.setBounds(140, 265, 200, 28);
        cmbEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        panelForm.add(cmbEstado);

        txtCodigo = crearTextField(140, 305, 200, panelForm);
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new Color(230, 230, 230));

        JPanel panelBotones = new JPanel(null);
        panelBotones.setBounds(10, 450, 360, 160);
        panelBotones.setBackground(new Color(245, 245, 245));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Acciones"));

        btnNuevo = crearBoton("Nuevo", new Color(40, 100, 160), 10, 25, panelBotones);
        btnGuardar = crearBoton("Guardar", new Color(40, 140, 80), 100, 25, panelBotones);
        btnModificar = crearBoton("Modificar", new Color(180, 120, 20), 190, 25, panelBotones);
        btnEliminarLogico = crearBoton("Elim. Lógica", new Color(200, 100, 20), 10, 70, panelBotones);
        btnEliminarFisico = crearBoton("Elim. Física", new Color(200, 50, 50), 120, 70, panelBotones);
        btnLimpiar = crearBoton("Limpiar", new Color(100, 100, 100), 250, 70, panelBotones);
        btnVerEliminados = crearBoton("Ver Elim.", new Color(90, 90, 160), 10, 115, panelBotones);
        btnRestaurar = crearBoton("Restaurar", new Color(40, 140, 140), 120, 115, panelBotones);

        JPanel panelBusquedaDoble = new JPanel(null);
        panelBusquedaDoble.setBackground(Color.WHITE);
        panelBusquedaDoble.setBorder(BorderFactory.createTitledBorder("Búsqueda (auto + inventario)"));

        JLabel lbBMarca = new JLabel("Marca:");
        lbBMarca.setBounds(10, 25, 50, 20);
        panelBusquedaDoble.add(lbBMarca);
        txtBuscarMarca = new JTextField();
        txtBuscarMarca.setBounds(60, 22, 90, 26);
        panelBusquedaDoble.add(txtBuscarMarca);

        JLabel lbBModelo = new JLabel("Modelo:");
        lbBModelo.setBounds(160, 25, 60, 20);
        panelBusquedaDoble.add(lbBModelo);
        txtBuscarModelo = new JTextField();
        txtBuscarModelo.setBounds(220, 22, 90, 26);
        panelBusquedaDoble.add(txtBuscarModelo);

        JLabel lbBAnio = new JLabel("Año:");
        lbBAnio.setBounds(320, 25, 35, 20);
        panelBusquedaDoble.add(lbBAnio);
        txtBuscarAnio = new JTextField();
        txtBuscarAnio.setBounds(355, 22, 60, 26);
        panelBusquedaDoble.add(txtBuscarAnio);

        JLabel lbBColor = new JLabel("Color:");
        lbBColor.setBounds(425, 25, 40, 20);
        panelBusquedaDoble.add(lbBColor);
        txtBuscarColor = new JTextField();
        txtBuscarColor.setBounds(465, 22, 75, 26);
        panelBusquedaDoble.add(txtBuscarColor);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(10, 50, 100, 26);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(40, 100, 160));
        btnBuscar.setForeground(Color.BLACK);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelBusquedaDoble.add(btnBuscar);

        String[] columnas = {"ID", "Marca", "Modelo", "Año", "Color", "Precio", "Estado", "Cód.4", "Stock"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.setSelectionBackground(new Color(180, 210, 240));

        JPanel panelTabDoble = new JPanel(new BorderLayout(0, 4));
        panelTabDoble.setBackground(new Color(245, 245, 245));
        panelBusquedaDoble.setPreferredSize(new Dimension(560, 80));
        panelTabDoble.add(panelBusquedaDoble, BorderLayout.NORTH);
        panelTabDoble.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBusquedaSimple = new JPanel(null);
        panelBusquedaSimple.setBackground(Color.WHITE);
        panelBusquedaSimple.setBorder(BorderFactory.createTitledBorder("Búsqueda (solo tabla auto)"));

        JLabel lbSMarca = new JLabel("Marca:");
        lbSMarca.setBounds(10, 25, 50, 20);
        panelBusquedaSimple.add(lbSMarca);
        txtBuscarMarcaSimple = new JTextField();
        txtBuscarMarcaSimple.setBounds(60, 22, 90, 26);
        panelBusquedaSimple.add(txtBuscarMarcaSimple);

        JLabel lbSModelo = new JLabel("Modelo:");
        lbSModelo.setBounds(160, 25, 60, 20);
        panelBusquedaSimple.add(lbSModelo);
        txtBuscarModeloSimple = new JTextField();
        txtBuscarModeloSimple.setBounds(220, 22, 90, 26);
        panelBusquedaSimple.add(txtBuscarModeloSimple);

        JLabel lbSAnio = new JLabel("Año:");
        lbSAnio.setBounds(320, 25, 35, 20);
        panelBusquedaSimple.add(lbSAnio);
        txtBuscarAnioSimple = new JTextField();
        txtBuscarAnioSimple.setBounds(355, 22, 60, 26);
        panelBusquedaSimple.add(txtBuscarAnioSimple);

        JLabel lbSColor = new JLabel("Color:");
        lbSColor.setBounds(425, 25, 40, 20);
        panelBusquedaSimple.add(lbSColor);
        txtBuscarColorSimple = new JTextField();
        txtBuscarColorSimple.setBounds(465, 22, 75, 26);
        panelBusquedaSimple.add(txtBuscarColorSimple);

        JButton btnBuscarSimple = new JButton("Buscar");
        btnBuscarSimple.setBounds(10, 50, 100, 26);
        btnBuscarSimple.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscarSimple.setBackground(new Color(40, 100, 160));
        btnBuscarSimple.setForeground(Color.BLACK);
        btnBuscarSimple.setFocusPainted(false);
        btnBuscarSimple.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelBusquedaSimple.add(btnBuscarSimple);

        String[] columnasSimple = {"ID", "Marca", "Modelo", "Año", "Color", "Precio", "Estado", "Cód.4"};
        modeloTablaSimple = new DefaultTableModel(columnasSimple, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaSimple = new JTable(modeloTablaSimple);
        tablaSimple.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaSimple.setRowHeight(22);
        tablaSimple.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaSimple.setSelectionBackground(new Color(180, 210, 240));

        JPanel panelTabSimple = new JPanel(new BorderLayout(0, 4));
        panelTabSimple.setBackground(new Color(245, 245, 245));
        panelBusquedaSimple.setPreferredSize(new Dimension(560, 80));
        panelTabSimple.add(panelBusquedaSimple, BorderLayout.NORTH);
        panelTabSimple.add(new JScrollPane(tablaSimple), BorderLayout.CENTER);

        tabsConsulta = new JTabbedPane();
        tabsConsulta.setFont(new Font("Arial", Font.BOLD, 12));
        tabsConsulta.addTab("Consulta (1 tabla)", panelTabSimple);
        tabsConsulta.addTab("Consulta (2 tablas)", panelTabDoble);
        tabsConsulta.setBounds(380, 60, 560, 480);

        tabsConsulta.addChangeListener(e -> {
            if (tabsConsulta.getSelectedIndex() == 0) {
                cargarTablaSimple();
            } else {
                cargarTabla();
            }
        });

        panelPrincipal.add(panelHeader);
        panelPrincipal.add(panelForm);
        panelPrincipal.add(panelBotones);
        panelPrincipal.add(tabsConsulta);

        add(panelPrincipal);

        btnNuevo.addActionListener(e -> accionNuevo());
        btnGuardar.addActionListener(e -> accionGuardar());
        btnModificar.addActionListener(e -> accionModificar());
        btnEliminarLogico.addActionListener(e -> accionEliminarLogico());
        btnEliminarFisico.addActionListener(e -> accionEliminarFisico());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> accionBuscar());
        btnBuscarSimple.addActionListener(e -> accionBuscarSimple());
        btnVerEliminados.addActionListener(e -> accionVerEliminados());
        btnRestaurar.addActionListener(e -> accionRestaurar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (tabla.getSelectedRow() >= 0) {
                    tablaSimple.clearSelection();
                }
                cargarDesdeTabla();
            }
        });

        tablaSimple.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (tablaSimple.getSelectedRow() >= 0) {
                    tabla.clearSelection();
                }
                cargarDesdeTablaSimple();
            }
        });

        txtMarca.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                generarCodigo();
            }
        });
        txtModelo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                generarCodigo();
            }
        });
        txtAnio.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                generarCodigo();
            }
        });
        txtColor.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                generarCodigo();
            }
        });

        setFormularioHabilitado(false);
    }

    private void generarCodigo() {
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String anioStr = txtAnio.getText().trim();
        String color = txtColor.getText().trim();
        if (!marca.isEmpty() && !modelo.isEmpty() && !anioStr.isEmpty() && !color.isEmpty()) {
            try {
                int anio = Integer.parseInt(anioStr);
                txtCodigo.setText(GeneradorCodigo4Cifras.generar(marca, modelo, anio, color));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void accionNuevo() {
        viendoEliminados = false;
        btnVerEliminados.setText("Ver Elim.");
        limpiarFormulario();
        modoEdicion = false;
        txtIdAuto.setText(autoDAO.generarNuevoId());
        setFormularioHabilitado(true);
        txtMarca.requestFocus();
    }

    private void accionGuardar() {
        if (!validarFormulario()) {
            return;
        }
        try {
            Auto auto = obtenerAutoDelFormulario();
            if (modoEdicion) {
                if (autoDAO.actualizar(auto)) {
                    JOptionPane.showMessageDialog(this, "Auto actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (autoDAO.insertar(auto)) {
                    JOptionPane.showMessageDialog(this, "Auto registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            limpiarFormulario();
            setFormularioHabilitado(false);
            cargarTabla();
            cargarTablaSimple();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionModificar() {
        if (viendoEliminados) {
            JOptionPane.showMessageDialog(this, "No se puede modificar un auto eliminado. Restáurelo primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (obtenerIdAutoSeleccionado() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un auto de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modoEdicion = true;
        setFormularioHabilitado(true);
        txtIdAuto.setEditable(false);
        txtMarca.setEnabled(false);
        txtModelo.setEnabled(false);
        txtAnio.setEnabled(false);
    }

    private void accionEliminarLogico() {
        if (viendoEliminados) {
            JOptionPane.showMessageDialog(this, "Este auto ya está eliminado lógicamente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = obtenerIdAutoSeleccionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un auto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar lógicamente el auto " + id + "?\n"
                + "El registro no se borrará de la base de datos, solo se ocultará\n"
                + "del mantenimiento (quedará marcado como no disponible en inventario).",
                "Confirmar eliminación lógica", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            autoDAO.eliminarLogico(id);
            JOptionPane.showMessageDialog(this, "Auto eliminado lógicamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
            cargarTablaSimple();
        }
    }

    private void accionEliminarFisico() {
        if (!"administrador".equals(usuarioActual.getRol())) {
            JOptionPane.showMessageDialog(this, "Solo el administrador puede eliminar físicamente.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = obtenerIdAutoSeleccionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un auto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿ELIMINAR FÍSICAMENTE el auto " + id + "?\nEsta acción no se puede deshacer.", "CONFIRMAR ELIMINACIÓN FÍSICA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            autoDAO.eliminarFisico(id);
            JOptionPane.showMessageDialog(this, "Auto eliminado permanentemente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
            cargarTablaSimple();
        }
    }

    private void accionVerEliminados() {
        viendoEliminados = !viendoEliminados;
        if (viendoEliminados) {
            poblarTabla(autoDAO.listarEliminadosLogicamente());
            btnVerEliminados.setText("Ver Activos");
            JOptionPane.showMessageDialog(this, "Mostrando autos eliminados lógicamente.", "Papelera", JOptionPane.INFORMATION_MESSAGE);
        } else {
            cargarTabla();
            btnVerEliminados.setText("Ver Elim.");
        }
    }

    private void accionRestaurar() {
        if (!viendoEliminados) {
            JOptionPane.showMessageDialog(this, "Use primero 'Ver Elim.' para ubicar el auto a restaurar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un auto de la lista de eliminados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = modeloTabla.getValueAt(fila, 0).toString();
        autoDAO.reactivar(id);
        JOptionPane.showMessageDialog(this, "Auto " + id + " restaurado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        poblarTabla(autoDAO.listarEliminadosLogicamente());
    }

    private void accionBuscar() {
        viendoEliminados = false;
        btnVerEliminados.setText("Ver Elim.");
        String marca = txtBuscarMarca.getText().trim();
        String modelo = txtBuscarModelo.getText().trim();
        String anioStr = txtBuscarAnio.getText().trim();
        String color = txtBuscarColor.getText().trim();
        int anio = 0;
        if (!anioStr.isEmpty()) {
            try {
                anio = Integer.parseInt(anioStr);
            } catch (NumberFormatException ignored) {
            }
        }
        List<Auto> lista = autoDAO.buscarPorCriteria(marca, modelo, anio, color);
        poblarTabla(lista);
    }

    private void cargarTabla() {
        poblarTabla(autoDAO.listarTodos());
    }

    private void cargarTablaSimple() {
        poblarTablaSimple(autoDAO.listarSoloTablaAuto());
    }

    private void poblarTablaSimple(List<Auto> lista) {
        modeloTablaSimple.setRowCount(0);
        for (Auto a : lista) {
            modeloTablaSimple.addRow(new Object[]{
                a.getIdAuto(), a.getMarca(), a.getModelo(), a.getAnio(),
                a.getColor(), a.getPrecio(), a.getEstado(), a.getCodigo4Cifras()
            });
        }
    }

    private void accionBuscarSimple() {
        String marca = txtBuscarMarcaSimple.getText().trim();
        String modelo = txtBuscarModeloSimple.getText().trim();
        String anioStr = txtBuscarAnioSimple.getText().trim();
        String color = txtBuscarColorSimple.getText().trim();
        int anio = 0;
        if (!anioStr.isEmpty()) {
            try {
                anio = Integer.parseInt(anioStr);
            } catch (NumberFormatException ignored) {
            }
        }
        poblarTablaSimple(autoDAO.buscarSoloTablaAuto(marca, modelo, anio, color));
    }

    private void poblarTabla(List<Auto> lista) {
        modeloTabla.setRowCount(0);
        for (Auto a : lista) {
            modeloTabla.addRow(new Object[]{
                a.getIdAuto(), a.getMarca(), a.getModelo(), a.getAnio(),
                a.getColor(), a.getPrecio(), a.getEstado(), a.getCodigo4Cifras(), a.getStock()
            });
        }
    }

    private void cargarDesdeTabla() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        txtIdAuto.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtMarca.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtModelo.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtAnio.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtColor.setText(modeloTabla.getValueAt(fila, 4).toString());
        txtPrecio.setText(modeloTabla.getValueAt(fila, 5).toString());
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 6).toString());
        txtCodigo.setText(modeloTabla.getValueAt(fila, 7).toString());
    }

    private void cargarDesdeTablaSimple() {
        int fila = tablaSimple.getSelectedRow();
        if (fila < 0) {
            return;
        }
        txtIdAuto.setText(modeloTablaSimple.getValueAt(fila, 0).toString());
        txtMarca.setText(modeloTablaSimple.getValueAt(fila, 1).toString());
        txtModelo.setText(modeloTablaSimple.getValueAt(fila, 2).toString());
        txtAnio.setText(modeloTablaSimple.getValueAt(fila, 3).toString());
        txtColor.setText(modeloTablaSimple.getValueAt(fila, 4).toString());
        txtPrecio.setText(modeloTablaSimple.getValueAt(fila, 5).toString());
        cmbEstado.setSelectedItem(modeloTablaSimple.getValueAt(fila, 6).toString());
        txtCodigo.setText(modeloTablaSimple.getValueAt(fila, 7).toString());
    }


    private String obtenerIdAutoSeleccionado() {
        if (tabsConsulta.getSelectedIndex() == 0) {
            int fila = tablaSimple.getSelectedRow();
            if (fila >= 0) {
                return modeloTablaSimple.getValueAt(fila, 0).toString();
            }
        } else {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                return modeloTabla.getValueAt(fila, 0).toString();
            }
        }
        return null;
    }

    private Auto obtenerAutoDelFormulario() {
        return new Auto(
                txtIdAuto.getText().trim(),
                txtMarca.getText().trim(),
                txtModelo.getText().trim(),
                Integer.parseInt(txtAnio.getText().trim()),
                txtColor.getText().trim(),
                new BigDecimal(txtPrecio.getText().trim()),
                cmbEstado.getSelectedItem().toString(),
                txtCodigo.getText().trim()
        );
    }

    private boolean validarFormulario() {
        if (!ValidadorAuto.esMarcaValida(txtMarca.getText())) {
            JOptionPane.showMessageDialog(this, ValidadorAuto.mensajeMarca(), "Validación", JOptionPane.WARNING_MESSAGE);
            txtMarca.requestFocus();
            return false;
        }
        if (!ValidadorAuto.esModeloValido(txtModelo.getText())) {
            JOptionPane.showMessageDialog(this, ValidadorAuto.mensajeModelo(), "Validación", JOptionPane.WARNING_MESSAGE);
            txtModelo.requestFocus();
            return false;
        }
        if (!ValidadorAuto.esAnioValido(txtAnio.getText())) {
            JOptionPane.showMessageDialog(this, ValidadorAuto.mensajeAnio(), "Validación", JOptionPane.WARNING_MESSAGE);
            txtAnio.requestFocus();
            return false;
        }
        if (!ValidadorAuto.esColorValido(txtColor.getText())) {
            JOptionPane.showMessageDialog(this, ValidadorAuto.mensajeColor(), "Validación", JOptionPane.WARNING_MESSAGE);
            txtColor.requestFocus();
            return false;
        }
        if (!ValidadorAuto.esPrecioValido(txtPrecio.getText())) {
            JOptionPane.showMessageDialog(this, ValidadorAuto.mensajePrecio(), "Validación", JOptionPane.WARNING_MESSAGE);
            txtPrecio.requestFocus();
            return false;
        }
        if (!ValidadorAuto.esCodigoValido(txtCodigo.getText())) {
            JOptionPane.showMessageDialog(this, "El código de 4 cifras no se generó correctamente. Vuelva a completar marca, modelo, año y color.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtIdAuto.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtAnio.setText("");
        txtColor.setText("");
        txtPrecio.setText("");
        txtCodigo.setText("");
        cmbEstado.setSelectedIndex(0);
        modoEdicion = false;
        setFormularioHabilitado(false);
    }

    private void setFormularioHabilitado(boolean habilitado) {
        txtMarca.setEnabled(habilitado);
        txtModelo.setEnabled(habilitado);
        txtAnio.setEnabled(habilitado);
        txtColor.setEnabled(habilitado);
        txtPrecio.setEnabled(habilitado);
        cmbEstado.setEnabled(habilitado);
        btnGuardar.setEnabled(habilitado);
    }

    private void limitarLongitud(JTextField campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (fb.getDocument().getLength() + string.length() <= maximo) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int largoFinal = fb.getDocument().getLength() - length + (text == null ? 0 : text.length());
                if (largoFinal <= maximo) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private JTextField crearTextField(int x, int y, int w, JPanel panel) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, w, 28);
        tf.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(tf);
        return tf;
    }

    private JButton crearBoton(String texto, Color color, int x, int y, JPanel panel) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 90, 32);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn);
        return btn;
    }
}
