package carzone.gui;

import carzone.dao.AutoDAO;
import carzone.modelo.Auto;
import carzone.modelo.Usuario;
import carzone.patron.GeneradorCodigo4Cifras;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class FrmMantenimientoAutos extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdAuto, txtMarca, txtModelo, txtAnio, txtColor, txtPrecio, txtCodigo;
    private JComboBox<String> cmbEstado;
    private JTextField txtBuscarMarca, txtBuscarModelo, txtBuscarAnio, txtBuscarColor;
    private JButton btnNuevo, btnGuardar, btnModificar, btnEliminarLogico, btnEliminarFisico, btnBuscar, btnLimpiar;
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
        txtModelo = crearTextField(140, 105, 200, panelForm);
        txtAnio = crearTextField(140, 145, 200, panelForm);
        txtColor = crearTextField(140, 185, 200, panelForm);
        txtPrecio = crearTextField(140, 225, 200, panelForm);

        cmbEstado = new JComboBox<>(new String[]{"disponible", "reservado", "vendido"});
        cmbEstado.setBounds(140, 265, 200, 28);
        cmbEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        panelForm.add(cmbEstado);

        txtCodigo = crearTextField(140, 305, 200, panelForm);
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new Color(230, 230, 230));

        JPanel panelBotones = new JPanel(null);
        panelBotones.setBounds(10, 450, 360, 120);
        panelBotones.setBackground(new Color(245, 245, 245));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Acciones"));

        btnNuevo = crearBoton("Nuevo", new Color(40, 100, 160), 10, 25, panelBotones);
        btnGuardar = crearBoton("Guardar", new Color(40, 140, 80), 100, 25, panelBotones);
        btnModificar = crearBoton("Modificar", new Color(180, 120, 20), 190, 25, panelBotones);
        btnEliminarLogico = crearBoton("Elim. Lógica", new Color(200, 100, 20), 10, 70, panelBotones);
        btnEliminarFisico = crearBoton("Elim. Física", new Color(200, 50, 50), 120, 70, panelBotones);
        btnLimpiar = crearBoton("Limpiar", new Color(100, 100, 100), 250, 70, panelBotones);

        JPanel panelBusqueda = new JPanel(null);
        panelBusqueda.setBounds(380, 60, 560, 80);
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda"));

        JLabel lbBMarca = new JLabel("Marca:");
        lbBMarca.setBounds(10, 25, 50, 20);
        panelBusqueda.add(lbBMarca);
        txtBuscarMarca = new JTextField();
        txtBuscarMarca.setBounds(60, 22, 90, 26);
        panelBusqueda.add(txtBuscarMarca);

        JLabel lbBModelo = new JLabel("Modelo:");
        lbBModelo.setBounds(160, 25, 60, 20);
        panelBusqueda.add(lbBModelo);
        txtBuscarModelo = new JTextField();
        txtBuscarModelo.setBounds(220, 22, 90, 26);
        panelBusqueda.add(txtBuscarModelo);

        JLabel lbBAnio = new JLabel("Año:");
        lbBAnio.setBounds(320, 25, 35, 20);
        panelBusqueda.add(lbBAnio);
        txtBuscarAnio = new JTextField();
        txtBuscarAnio.setBounds(355, 22, 60, 26);
        panelBusqueda.add(txtBuscarAnio);

        JLabel lbBColor = new JLabel("Color:");
        lbBColor.setBounds(425, 25, 40, 20);
        panelBusqueda.add(lbBColor);
        txtBuscarColor = new JTextField();
        txtBuscarColor.setBounds(465, 22, 75, 26);
        panelBusqueda.add(txtBuscarColor);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(10, 50, 100, 26);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(40, 100, 160));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelBusqueda.add(btnBuscar);

        String[] columnas = {"ID", "Marca", "Modelo", "Año", "Color", "Precio", "Estado", "Cód.4"};
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

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(380, 148, 560, 330);

        panelPrincipal.add(panelHeader);
        panelPrincipal.add(panelForm);
        panelPrincipal.add(panelBotones);
        panelPrincipal.add(panelBusqueda);
        panelPrincipal.add(scroll);

        add(panelPrincipal);

        btnNuevo.addActionListener(e -> accionNuevo());
        btnGuardar.addActionListener(e -> accionGuardar());
        btnModificar.addActionListener(e -> accionModificar());
        btnEliminarLogico.addActionListener(e -> accionEliminarLogico());
        btnEliminarFisico.addActionListener(e -> accionEliminarFisico());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> accionBuscar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDesdeTabla();
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionModificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un auto de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modoEdicion = true;
        setFormularioHabilitado(true);
        txtIdAuto.setEditable(false);
    }

    private void accionEliminarLogico() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un auto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = modeloTabla.getValueAt(fila, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar lógicamente el auto " + id + "?\nSe marcará como 'vendido'.", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            autoDAO.eliminarLogico(id);
            JOptionPane.showMessageDialog(this, "Auto marcado como vendido.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void accionEliminarFisico() {
        if (!"administrador".equals(usuarioActual.getRol())) {
            JOptionPane.showMessageDialog(this, "Solo el administrador puede eliminar físicamente.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un auto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = modeloTabla.getValueAt(fila, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿ELIMINAR FÍSICAMENTE el auto " + id + "?\nEsta acción no se puede deshacer.", "CONFIRMAR ELIMINACIÓN FÍSICA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            autoDAO.eliminarFisico(id);
            JOptionPane.showMessageDialog(this, "Auto eliminado permanentemente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void accionBuscar() {
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

    private void poblarTabla(List<Auto> lista) {
        modeloTabla.setRowCount(0);
        for (Auto a : lista) {
            modeloTabla.addRow(new Object[]{
                a.getIdAuto(), a.getMarca(), a.getModelo(), a.getAnio(),
                a.getColor(), a.getPrecio(), a.getEstado(), a.getCodigo4Cifras()
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
        if (txtMarca.getText().trim().isEmpty() || txtModelo.getText().trim().isEmpty()
                || txtAnio.getText().trim().isEmpty() || txtColor.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            int anio = Integer.parseInt(txtAnio.getText().trim());
            if (anio < 1900 || anio > 2100) {
                JOptionPane.showMessageDialog(this, "Año inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El año debe ser numérico.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser numérico.", "Validación", JOptionPane.WARNING_MESSAGE);
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
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn);
        return btn;
    }
}
