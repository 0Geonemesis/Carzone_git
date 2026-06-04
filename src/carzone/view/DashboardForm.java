package carzone.view;

import carzone.model.Usuario;
import javax.swing.*;
import java.awt.*;

/**
 * Patrón GUI: Dashboard Pattern
 * Panel principal con acceso a módulos según perfil.
 */
public class DashboardForm extends JFrame {

    private final Usuario usuarioActual;

    public DashboardForm(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
    }

    private void initComponents() {
        setTitle("CarZone - Sistema de Gestión  |  Usuario: "
                + usuarioActual.getNombreUsuario()
                + "  [" + usuarioActual.getRol() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 540);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));

        // ── Header ───────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 45));
        header.setPreferredSize(new Dimension(0, 60));

        JLabel lblMarca = new JLabel("  🚗 CARZONE");
        lblMarca.setFont(new Font("Arial", Font.BOLD, 22));
        lblMarca.setForeground(new Color(255, 200, 0));

        JLabel lblInfo = new JLabel("Bienvenido, " + usuarioActual.getNombreUsuario()
                + "  |  Perfil: " + usuarioActual.getRol() + "  ");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setForeground(Color.LIGHT_GRAY);

        header.add(lblMarca, BorderLayout.WEST);
        header.add(lblInfo, BorderLayout.EAST);

        // ── Menú lateral ─────────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 42, 60));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setForeground(new Color(255, 200, 0));
        lblMenu.setFont(new Font("Arial", Font.BOLD, 11));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblMenu);
        sidebar.add(Box.createVerticalStrut(15));

        // Botones de menú
        JButton btnUsuarios = crearBotonMenu("👤 Usuarios");
        sidebar.add(btnUsuarios);
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(Box.createVerticalGlue());

        JButton btnCerrar = crearBotonMenu("🚪 Cerrar Sesión");
        btnCerrar.setBackground(new Color(200, 60, 60));
        sidebar.add(btnCerrar);

        // ── Panel central (contenido) ─────────────────────────────────────────
        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(new Color(245, 247, 250));
        contenido.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Tarjetas de acceso rápido
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(12, 12, 12, 12);

        g.gridx = 0; g.gridy = 0;
        contenido.add(crearTarjeta("👤", "Gestión de Usuarios", "Administrar cuentas"), g);

        g.gridx = 1; g.gridy = 0;
        contenido.add(crearTarjeta("🚗", "Autos", "Catálogo de vehículos"), g);

        g.gridx = 0; g.gridy = 1;
        contenido.add(crearTarjeta("👥", "Clientes", "Registros de clientes"), g);

        g.gridx = 1; g.gridy = 1;
        contenido.add(crearTarjeta("📊", "Ventas", "Registro de transacciones"), g);

        // ── Ensamblado ───────────────────────────────────────────────────────
        panelPrincipal.add(header, BorderLayout.NORTH);
        panelPrincipal.add(sidebar, BorderLayout.WEST);
        panelPrincipal.add(contenido, BorderLayout.CENTER);
        setContentPane(panelPrincipal);

        // ── Eventos ──────────────────────────────────────────────────────────
        btnUsuarios.addActionListener(e -> abrirMantenimientoUsuarios());

        // Tarjeta usuarios (click)
        for (Component c : contenido.getComponents()) {
            if (c instanceof JPanel) {
                JPanel tarjeta = (JPanel) c;
                // Buscar label con texto "Gestión de Usuarios"
                for (Component sub : tarjeta.getComponents()) {
                    if (sub instanceof JLabel && ((JLabel)sub).getText().contains("Usuarios")) {
                        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                                abrirMantenimientoUsuarios();
                            }
                        });
                    }
                }
            }
        }

        btnCerrar.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });
    }

    private void abrirMantenimientoUsuarios() {
        MantenimientoUsuarioForm form = new MantenimientoUsuarioForm(usuarioActual);
        form.setVisible(true);
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(180, 38));
        btn.setPreferredSize(new Dimension(180, 38));
        btn.setBackground(new Color(60, 63, 90));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private JPanel crearTarjeta(String icono, String titulo, String subtitulo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));
        card.setPreferredSize(new Dimension(200, 120));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblIco = new JLabel(icono);
        lblIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIco.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Arial", Font.BOLD, 13));
        lblTit.setForeground(new Color(30, 30, 45));
        lblTit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblIco);
        card.add(Box.createVerticalStrut(6));
        card.add(lblTit);
        card.add(Box.createVerticalStrut(3));
        card.add(lblSub);

        return card;
    }
}
