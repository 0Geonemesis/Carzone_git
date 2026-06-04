package carzone.view;

import carzone.dao.UsuarioDAO;
import carzone.factory.EntityFactory;
import carzone.model.Usuario;
import carzone.observer.Observer;
import carzone.strategy.ValidacionStrategy;
import carzone.strategy.ValidacionesImpl;
import carzone.util.IDGenerator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Patrón GUI: CRUD Interface Pattern + Search and Filter Pattern + Form Entry Pattern
 * Patrón Desarrollo: Observer (actualiza tabla automáticamente)
 * Patrón Desarrollo: Factory Method (crea entidades)
 * Patrón Desarrollo: Strategy (validaciones)
 * 
 * Mantenimiento de Usuarios:
 *  - Adicionar
 *  - Modificar
 *  - Consultar usando una tabla / usando dos tablas
 *  - Eliminar lógica / física
 */
public class MantenimientoUsuarioForm extends JFrame implements Observer {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Usuario usuarioSesion;

    // Tabla principal
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Campos de formulario (Form Entry Pattern)
    private JTextField txtId;
    private JTextField txtNombre;
    private JPasswordField txtClave;
    private JComboBox<String> cmbRol;
    private JCheckBox chkEstado;

    // Búsqueda (Search and Filter Pattern)
    private JTextField txtBuscar;

    // Botones CRUD
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminarLogico;
    private JButton btnEliminarFisico;
    private JButton btnLimpiar;

    // Tabla detalle (segunda tabla para "consultar usando dos tablas")
    private JTable tablaDetalle;
    private DefaultTableModel modeloDetalle;

    // Validaciones (Strategy)
    private final ValidacionStrategy[] validaciones = {
        new ValidacionesImpl.FormatoId(),
        new ValidacionesImpl.NoVacio("Nombre de usuario", 50),
        new ValidacionesImpl.Contrasena(),
        new ValidacionesImpl.RolValido()
    };

    private static final Color COLOR_PRIMARY = new Color(30, 30, 45);
    private static final Color COLOR_ACCENT  = new Color(255, 200, 0);
    private static final Color COLOR_BG      = new Color(245, 247, 250);

    public MantenimientoUsuarioForm(Usuario sesion) {
        this.usuarioSesion = sesion;
        usuarioDAO.agregarObserver(this); // Observer pattern
        initComponents();
        cargarTabla("");
    }

    // ── Observer ─────────────────────────────────────────────────────────────
    @Override
    public void actualizar(String evento, Object dato) {
        cargarTabla(txtBuscar.getText().trim());
        actualizarTablaDetalle();
    }

    // ── UI ───────────────────────────────────────────────────────────────────
    private void initComponents() {
        setTitle("CarZone - Mantenimiento de Usuarios");
        setSize(1050, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(COLOR_BG);

        // Header
        root.add(crearHeader(), BorderLayout.NORTH);

        // Panel izquierdo: formulario
        root.add(crearPanelFormulario(), BorderLayout.WEST);

        // Panel derecho: tablas + búsqueda
        root.add(crearPanelTablas(), BorderLayout.CENTER);

        setContentPane(root);
        configurarEventos();
        setModoNuevo();
    }

    private JPanel crearHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_PRIMARY);
        p.setPreferredSize(new Dimension(0, 50));
        JLabel lbl = new JLabel("  👤  Gestión de Usuarios");
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(COLOR_ACCENT);
        JLabel lblSesion = new JLabel("Sesión: " + usuarioSesion.getNombreUsuario() + "  ");
        lblSesion.setForeground(Color.LIGHT_GRAY);
        lblSesion.setFont(new Font("Arial", Font.PLAIN, 11));
        p.add(lbl, BorderLayout.WEST);
        p.add(lblSesion, BorderLayout.EAST);
        return p;
    }

    private JPanel crearPanelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(290, 0));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 230)),
            BorderFactory.createEmptyBorder(20, 18, 10, 18)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 3, 5, 3);
        g.gridwidth = 2;

        // Título sección
        JLabel tit = new JLabel("Datos del Usuario");
        tit.setFont(new Font("Arial", Font.BOLD, 13));
        tit.setForeground(COLOR_PRIMARY);
        g.gridx = 0; g.gridy = 0;
        p.add(tit, g);

        JSeparator sep = new JSeparator();
        g.gridy = 1;
        p.add(sep, g);

        // ID
        g.gridwidth = 1; g.weightx = 0.35;
        g.gridx = 0; g.gridy = 2;
        p.add(label("ID:"), g);
        txtId = new JTextField();
        txtId.setToolTipText("Formato: USR001 (3 letras + 3 dígitos)");
        g.gridx = 1; g.weightx = 0.65;
        p.add(txtId, g);

        // Nombre
        g.gridx = 0; g.gridy = 3; g.weightx = 0.35;
        p.add(label("Nombre:"), g);
        txtNombre = new JTextField();
        txtNombre.setToolTipText("Máximo 50 caracteres");
        g.gridx = 1; g.weightx = 0.65;
        p.add(txtNombre, g);

        // Contraseña
        g.gridx = 0; g.gridy = 4; g.weightx = 0.35;
        p.add(label("Contraseña:"), g);
        txtClave = new JPasswordField();
        txtClave.setToolTipText("Mínimo 6 caracteres, máximo 50");
        g.gridx = 1; g.weightx = 0.65;
        p.add(txtClave, g);

        // Rol
        g.gridx = 0; g.gridy = 5; g.weightx = 0.35;
        p.add(label("Rol:"), g);
        cmbRol = new JComboBox<>(new String[]{"administrador", "vendedor"});
        g.gridx = 1; g.weightx = 0.65;
        p.add(cmbRol, g);

        // Estado
        g.gridx = 0; g.gridy = 6; g.weightx = 0.35;
        p.add(label("Estado:"), g);
        chkEstado = new JCheckBox("Activo");
        chkEstado.setSelected(true);
        chkEstado.setBackground(Color.WHITE);
        g.gridx = 1; g.weightx = 0.65;
        p.add(chkEstado, g);

        // Separador
        g.gridwidth = 2; g.gridx = 0; g.gridy = 7;
        p.add(new JSeparator(), g);

        // Botones CRUD
        g.gridy = 8;
        btnNuevo = boton("Nuevo", new Color(70, 130, 180));
        p.add(btnNuevo, g);
        g.gridy = 9;
        btnGuardar = boton("Guardar", new Color(46, 139, 87));
        p.add(btnGuardar, g);
        g.gridy = 10;
        btnModificar = boton("Modificar", new Color(210, 140, 0));
        p.add(btnModificar, g);

        g.gridy = 11;
        p.add(new JSeparator(), g);

        // Fila eliminar (2 botones)
        JPanel panelElim = new JPanel(new GridLayout(1, 2, 5, 0));
        panelElim.setBackground(Color.WHITE);
        btnEliminarLogico  = boton("Elim. Lógica",  new Color(180, 80, 80));
        btnEliminarFisico  = boton("Elim. Física",  new Color(120, 30, 30));
        panelElim.add(btnEliminarLogico);
        panelElim.add(btnEliminarFisico);
        g.gridy = 12;
        p.add(panelElim, g);

        g.gridy = 13;
        btnLimpiar = boton("Limpiar", new Color(110, 110, 130));
        p.add(btnLimpiar, g);

        // Relleno
        g.gridy = 14; g.weighty = 1.0;
        p.add(new JLabel(), g);

        return p;
    }

    private JPanel crearPanelTablas() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // Barra de búsqueda (Search and Filter Pattern)
        JPanel barraTop = new JPanel(new BorderLayout(8, 0));
        barraTop.setBackground(COLOR_BG);
        JLabel lblBuscar = new JLabel("🔍 Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.PLAIN, 13));
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar por ID, nombre o rol");
        barraTop.add(lblBuscar, BorderLayout.WEST);
        barraTop.add(txtBuscar, BorderLayout.CENTER);
        p.add(barraTop, BorderLayout.NORTH);

        // Panel dividido en dos tablas (consultar usando dos tablas)
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.6);
        split.setDividerSize(6);
        split.setBackground(COLOR_BG);

        // Tabla principal
        String[] colsPrincipal = {"ID", "Nombre de Usuario", "Rol", "Estado"};
        modeloTabla = new DefaultTableModel(colsPrincipal, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.getTableHeader().setBackground(COLOR_PRIMARY);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setGridColor(new Color(230, 230, 235));
        tabla.setSelectionBackground(new Color(255, 220, 80));
        tabla.setSelectionForeground(COLOR_PRIMARY);

        JPanel panelTabla1 = new JPanel(new BorderLayout());
        panelTabla1.add(new JLabel("  Lista de Usuarios", SwingConstants.LEFT), BorderLayout.NORTH);
        panelTabla1.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Tabla detalle (segunda tabla para detalle/auditoría)
        String[] colsDetalle = {"Campo", "Valor"};
        modeloDetalle = new DefaultTableModel(colsDetalle, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setRowHeight(24);
        tablaDetalle.getTableHeader().setBackground(new Color(60, 63, 90));
        tablaDetalle.getTableHeader().setForeground(Color.WHITE);
        tablaDetalle.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tablaDetalle.setFont(new Font("Arial", Font.PLAIN, 11));

        JPanel panelTabla2 = new JPanel(new BorderLayout());
        JLabel lblDetalle = new JLabel("  Detalle del Registro Seleccionado");
        lblDetalle.setFont(new Font("Arial", Font.BOLD, 11));
        lblDetalle.setForeground(new Color(60, 63, 90));
        panelTabla2.add(lblDetalle, BorderLayout.NORTH);
        panelTabla2.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);

        split.setTopComponent(panelTabla1);
        split.setBottomComponent(panelTabla2);
        p.add(split, BorderLayout.CENTER);

        return p;
    }

    private void configurarEventos() {
        // Búsqueda en tiempo real (Search and Filter Pattern)
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
            private void filtrar() { cargarTabla(txtBuscar.getText().trim()); }
        });

        // Selección en tabla → llena formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarEnFormulario();
                actualizarTablaDetalle();
            }
        });

        // CRUD
        btnNuevo.addActionListener(e -> setModoNuevo());
        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminarLogico.addActionListener(e -> eliminar(false));
        btnEliminarFisico.addActionListener(e -> eliminar(true));
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Validación en tiempo real del campo ID
        txtId.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validarIdVisual(); }
            @Override public void removeUpdate(DocumentEvent e) { validarIdVisual(); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });
    }

    // ── Lógica de negocio ────────────────────────────────────────────────────
    private void guardar() {
        if (!validarFormulario()) return;

        String id = txtId.getText().trim().toUpperCase();

        if (usuarioDAO.existeId(id)) {
            mostrarError("El ID '" + id + "' ya existe. Use otro o pulse Modificar.");
            return;
        }

        // Factory Method crea la entidad
        Usuario u = EntityFactory.crearUsuario(
            id,
            txtNombre.getText().trim(),
            new String(txtClave.getPassword()),
            (String) cmbRol.getSelectedItem(),
            chkEstado.isSelected()
        );

        if (usuarioDAO.insertar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario guardado correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } else {
            mostrarError("No se pudo guardar el usuario.");
        }
    }

    private void modificar() {
        if (tabla.getSelectedRow() < 0) {
            mostrarError("Seleccione un usuario de la tabla.");
            return;
        }
        if (!validarFormulario()) return;

        Usuario u = EntityFactory.crearUsuario(
            txtId.getText().trim().toUpperCase(),
            txtNombre.getText().trim(),
            new String(txtClave.getPassword()),
            (String) cmbRol.getSelectedItem(),
            chkEstado.isSelected()
        );

        if (usuarioDAO.actualizar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario actualizado.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarError("No se pudo actualizar el usuario.");
        }
    }

    private void eliminar(boolean esFisico) {
        if (tabla.getSelectedRow() < 0) {
            mostrarError("Seleccione un usuario de la tabla.");
            return;
        }
        String id = (String) modeloTabla.getValueAt(tabla.getSelectedRow(), 0);
        String tipo = esFisico ? "FÍSICA (irreversible)" : "LÓGICA (desactivar)";

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirma eliminación " + tipo + " del usuario " + id + "?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = esFisico
            ? usuarioDAO.eliminarFisico(id)
            : usuarioDAO.eliminarLogico(id);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } else {
            mostrarError("No se pudo eliminar el usuario.");
        }
    }

    // ── Tabla ────────────────────────────────────────────────────────────────
    private void cargarTabla(String filtro) {
        modeloTabla.setRowCount(0);
        List<Usuario> lista = filtro.isEmpty()
            ? usuarioDAO.listar()
            : usuarioDAO.buscar(filtro);
        for (Usuario u : lista) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombreUsuario(),
                u.getRol(),
                u.isEstado() ? "Activo" : "Inactivo"
            });
        }
    }

    /** Segunda tabla: detalle del usuario seleccionado */
    private void actualizarTablaDetalle() {
        modeloDetalle.setRowCount(0);
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        modeloDetalle.addRow(new Object[]{"ID",       modeloTabla.getValueAt(fila, 0)});
        modeloDetalle.addRow(new Object[]{"Nombre",   modeloTabla.getValueAt(fila, 1)});
        modeloDetalle.addRow(new Object[]{"Rol",      modeloTabla.getValueAt(fila, 2)});
        modeloDetalle.addRow(new Object[]{"Estado",   modeloTabla.getValueAt(fila, 3)});
    }

    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtId.setText((String) modeloTabla.getValueAt(fila, 0));
        txtId.setEditable(false);
        txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
        cmbRol.setSelectedItem(modeloTabla.getValueAt(fila, 2));
        chkEstado.setSelected("Activo".equals(modeloTabla.getValueAt(fila, 3)));
        txtClave.setText("");
        txtClave.requestFocus();
    }

    // ── Validaciones (Strategy Pattern) ─────────────────────────────────────
    private boolean validarFormulario() {
        String id     = txtId.getText().trim().toUpperCase();
        String nombre = txtNombre.getText().trim();
        String clave  = new String(txtClave.getPassword());
        String rol    = (String) cmbRol.getSelectedItem();

        String[] valores = {id, nombre, clave, rol};
        for (int i = 0; i < validaciones.length; i++) {
            if (!validaciones[i].validar(valores[i])) {
                mostrarError(validaciones[i].getMensaje());
                return false;
            }
        }
        return true;
    }

    private void validarIdVisual() {
        String id = txtId.getText().trim().toUpperCase();
        if (id.isEmpty()) {
            txtId.setBackground(Color.WHITE);
        } else if (new ValidacionesImpl.FormatoId().validar(id)) {
            txtId.setBackground(new Color(220, 255, 220));
        } else {
            txtId.setBackground(new Color(255, 220, 220));
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────
    private void setModoNuevo() {
        limpiarFormulario();
        txtId.setEditable(true);
        // Auto-generar ID
        txtId.setText(IDGenerator.generarIdUsuario());
        validarIdVisual();
        txtNombre.requestFocus();
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtId.setEditable(true);
        txtId.setBackground(Color.WHITE);
        txtNombre.setText("");
        txtClave.setText("");
        cmbRol.setSelectedIndex(0);
        chkEstado.setSelected(true);
        tabla.clearSelection();
        modeloDetalle.setRowCount(0);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel label(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        return lbl;
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 32));
        return btn;
    }
}
