package carzone.gui;

import carzone.dao.UsuarioDAO;
import carzone.modelo.Usuario;
import carzone.patron.ValidadorContrasena;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmMantenimientoUsuarios extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdUsuario, txtNombreUsuario, txtBuscar;
    private JPasswordField txtContrasena;
    private JComboBox<String> cmbRol, cmbEstado;
    private JButton btnNuevo, btnGuardar, btnModificar, btnEliminarLogico, btnEliminarFisico, btnBuscar, btnLimpiar;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;
    private boolean modoEdicion = false;

    public FrmMantenimientoUsuarios(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        setTitle("CarZone - Mantenimiento de Usuarios");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(null);
        panelPrincipal.setBackground(new Color(245, 245, 245));

        JPanel panelHeader = new JPanel(null);
        panelHeader.setBounds(0, 0, 900, 50);
        panelHeader.setBackground(new Color(40, 140, 80));

        JLabel lblTitulo = new JLabel("Mantenimiento de Usuarios");
        lblTitulo.setBounds(20, 12, 400, 26);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelHeader.add(lblTitulo);

        JPanel panelForm = new JPanel(null);
        panelForm.setBounds(10, 60, 340, 320);
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));

        String[] etiquetas = {"ID Usuario:", "Nombre Usuario:", "Contraseña:", "Rol:", "Estado:"};
        int[] posY = {25, 70, 115, 165, 210};
        for (int i = 0; i < etiquetas.length; i++) {
            JLabel lbl = new JLabel(etiquetas[i]);
            lbl.setBounds(10, posY[i], 120, 22);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            panelForm.add(lbl);
        }

        txtIdUsuario = new JTextField();
        txtIdUsuario.setBounds(130, 25, 190, 28);
        txtIdUsuario.setEditable(false);
        txtIdUsuario.setBackground(new Color(230, 230, 230));
        txtIdUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        panelForm.add(txtIdUsuario);

        txtNombreUsuario = new JTextField();
        txtNombreUsuario.setBounds(130, 70, 190, 28);
        txtNombreUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        panelForm.add(txtNombreUsuario);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(130, 115, 190, 28);
        txtContrasena.setFont(new Font("Arial", Font.PLAIN, 12));
        panelForm.add(txtContrasena);

        cmbRol = new JComboBox<>(new String[]{"administrador", "vendedor"});
        cmbRol.setBounds(130, 165, 190, 28);
        cmbRol.setFont(new Font("Arial", Font.PLAIN, 12));
        panelForm.add(cmbRol);

        cmbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cmbEstado.setBounds(130, 210, 190, 28);
        cmbEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        panelForm.add(cmbEstado);

        JPanel panelBotones = new JPanel(null);
        panelBotones.setBounds(10, 390, 340, 120);
        panelBotones.setBackground(new Color(245, 245, 245));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Acciones"));

        btnNuevo = crearBoton("Nuevo", new Color(40, 140, 80), 10, 25, panelBotones);
        btnGuardar = crearBoton("Guardar", new Color(40, 100, 160), 100, 25, panelBotones);
        btnModificar = crearBoton("Modificar", new Color(180, 120, 20), 200, 25, panelBotones);
        btnEliminarLogico = crearBoton("Elim. Lógica", new Color(200, 100, 20), 10, 70, panelBotones);
        btnEliminarFisico = crearBoton("Elim. Física", new Color(200, 50, 50), 120, 70, panelBotones);
        btnLimpiar = crearBoton("Limpiar", new Color(100, 100, 100), 235, 70, panelBotones);

        JPanel panelBusqueda = new JPanel(null);
        panelBusqueda.setBounds(360, 60, 520, 60);
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda por nombre"));

        JLabel lbBus = new JLabel("Nombre:");
        lbBus.setBounds(10, 22, 70, 22);
        panelBusqueda.add(lbBus);
        txtBuscar = new JTextField();
        txtBuscar.setBounds(80, 20, 200, 28);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 12));
        panelBusqueda.add(txtBuscar);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(290, 20, 100, 28);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(40, 140, 80));
        btnBuscar.setForeground(Color.BLACK);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelBusqueda.add(btnBuscar);

        String[] columnas = {"ID", "Nombre Usuario", "Contraseña", "Rol", "Estado"};
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
        tabla.setSelectionBackground(new Color(180, 220, 200));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(360, 128, 520, 310);

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

        setFormularioHabilitado(false);
    }

    private void accionNuevo() {
        limpiarFormulario();
        modoEdicion = false;
        txtIdUsuario.setText(usuarioDAO.generarNuevoId());
        setFormularioHabilitado(true);
        txtNombreUsuario.requestFocus();
    }

    private void accionGuardar() {
        if (!validarFormulario()) {
            return;
        }
        String contrasena = new String(txtContrasena.getPassword());
        if (!ValidadorContrasena.esValida(contrasena)) {
            JOptionPane.showMessageDialog(this, ValidadorContrasena.getMensajeRequisitos(), "Contraseña inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Usuario u = obtenerUsuarioDelFormulario();
            if (modoEdicion) {
                if (usuarioDAO.actualizar(u)) {
                    JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (usuarioDAO.insertar(u)) {
                    JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modoEdicion = true;
        setFormularioHabilitado(true);
        txtIdUsuario.setEditable(false);
    }

    private void accionEliminarLogico() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = modeloTabla.getValueAt(fila, 0).toString();
        if (id.equals(usuarioActual.getIdUsuario())) {
            JOptionPane.showMessageDialog(this, "No puede desactivar su propia cuenta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desactivar el usuario " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            usuarioDAO.eliminarLogico(id);
            JOptionPane.showMessageDialog(this, "Usuario desactivado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void accionEliminarFisico() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = modeloTabla.getValueAt(fila, 0).toString();
        if (id.equals(usuarioActual.getIdUsuario())) {
            JOptionPane.showMessageDialog(this, "No puede eliminar su propia cuenta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿ELIMINAR FÍSICAMENTE el usuario " + id + "?\nEsta acción no se puede deshacer.", "CONFIRMAR", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            usuarioDAO.eliminarFisico(id);
            JOptionPane.showMessageDialog(this, "Usuario eliminado permanentemente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void accionBuscar() {
        List<Usuario> lista = usuarioDAO.buscarPorNombre(txtBuscar.getText().trim());
        poblarTabla(lista);
    }

    private void cargarTabla() {
        poblarTabla(usuarioDAO.listarTodos());
    }

    private void poblarTabla(List<Usuario> lista) {
        modeloTabla.setRowCount(0);
        for (Usuario u : lista) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(), u.getNombreUsuario(), u.getContrasena(),
                u.getRol(), u.isEstado() ? "Activo" : "Inactivo"
            });
        }
    }

    private void cargarDesdeTabla() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        txtIdUsuario.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtNombreUsuario.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtContrasena.setText(modeloTabla.getValueAt(fila, 2).toString());
        cmbRol.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 4).toString());
    }

    private Usuario obtenerUsuarioDelFormulario() {
        return new Usuario(
                txtIdUsuario.getText().trim(),
                txtNombreUsuario.getText().trim(),
                new String(txtContrasena.getPassword()),
                cmbRol.getSelectedItem().toString(),
                "Activo".equals(cmbEstado.getSelectedItem().toString())
        );
    }

    private boolean validarFormulario() {
        if (txtNombreUsuario.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre de usuario.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtContrasena.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Ingrese la contraseña.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtIdUsuario.setText("");
        txtNombreUsuario.setText("");
        txtContrasena.setText("");
        cmbRol.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        modoEdicion = false;
        setFormularioHabilitado(false);
    }

    private void setFormularioHabilitado(boolean habilitado) {
        txtNombreUsuario.setEnabled(habilitado);
        txtContrasena.setEnabled(habilitado);
        cmbRol.setEnabled(habilitado);
        cmbEstado.setEnabled(habilitado);
        btnGuardar.setEnabled(habilitado);
    }

    private JButton crearBoton(String texto, Color color, int x, int y, JPanel panel) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 95, 32);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn);
        return btn;
    }
}
