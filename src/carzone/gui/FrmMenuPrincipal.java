package carzone.gui;

import carzone.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class FrmMenuPrincipal extends JFrame {

    private Usuario usuarioActual;

    public FrmMenuPrincipal(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        initComponents();
    }

    private void initComponents() {
        setTitle("CarZone - Menú Principal");
        setSize(820, 590);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(null);
        panelPrincipal.setBackground(new Color(230, 235, 245));

        JPanel panelHeader = new JPanel(null);
        panelHeader.setBounds(0, 0, 820, 70);
        panelHeader.setBackground(new Color(15, 20, 40));

        JLabel lblTitulo = new JLabel("CARZONE");
        lblTitulo.setBounds(20, 10, 400, 30);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(255, 200, 50));

        JLabel lblUsuarioInfo = new JLabel("Usuario: " + usuarioActual.getNombreUsuario() + "   |   Rol: " + usuarioActual.getRol().toUpperCase());
        lblUsuarioInfo.setBounds(20, 42, 500, 18);
        lblUsuarioInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUsuarioInfo.setForeground(new Color(150, 200, 255));

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(690, 20, 110, 30);
        btnCerrarSesion.setFont(new Font("Arial", Font.BOLD, 11));
        btnCerrarSesion.setBackground(new Color(200, 50, 50));
        btnCerrarSesion.setForeground(Color.BLACK);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelHeader.add(lblTitulo);
        panelHeader.add(lblUsuarioInfo);
        panelHeader.add(btnCerrarSesion);

        JLabel lblModulos = new JLabel("Módulos del Sistema");
        lblModulos.setBounds(0, 90, 820, 25);
        lblModulos.setFont(new Font("Arial", Font.BOLD, 16));
        lblModulos.setForeground(new Color(30, 30, 80));
        lblModulos.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnAutos = crearBotonModulo("🚗  Mantenimiento\nde Autos", new Color(25, 85, 140), Color.WHITE, 80, 135);
        JButton btnVentas = crearBotonModulo("💰  Módulo\nde Ventas", new Color(140, 80, 20), Color.WHITE, 320, 135);
        JButton btnUsuarios = crearBotonModulo("👤  Mantenimiento\nde Usuarios", new Color(30, 120, 60), Color.WHITE, 560, 135);
        JButton btnReportes = crearBotonModulo("📊  Módulo\nde Reportes", new Color(100, 40, 130), Color.WHITE, 320, 245);

        panelPrincipal.add(panelHeader);
        panelPrincipal.add(lblModulos);
        panelPrincipal.add(btnAutos);
        panelPrincipal.add(btnVentas);
        panelPrincipal.add(btnUsuarios);
        panelPrincipal.add(btnReportes);

        add(panelPrincipal);

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            new FrmLogin().setVisible(true);
        });

        btnAutos.addActionListener(e -> new FrmMantenimientoAutos(usuarioActual).setVisible(true));

        btnVentas.addActionListener(e -> new FrmVentas(usuarioActual).setVisible(true));

        btnUsuarios.addActionListener(e -> {
            if ("administrador".equals(usuarioActual.getRol())) {
                new FrmMantenimientoUsuarios(usuarioActual).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Solo el administrador puede gestionar usuarios.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnReportes.addActionListener(e -> new FrmReportes(usuarioActual).setVisible(true));
    }

    private JButton crearBotonModulo(String texto, Color colorFondo, Color colorTexto, int x, int y) {
        JButton btn = new JButton("<html><center>" + texto.replace("\n", "<br>") + "</center></html>");
        btn.setBounds(x, y, 200, 90);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(colorFondo);
        btn.setForeground(colorTexto);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
