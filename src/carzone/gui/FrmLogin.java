package carzone.gui;

import carzone.dao.UsuarioDAO;
import carzone.modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FrmLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnCancelar;
    private JLabel lblTitulo;
    private JLabel lblSubtitulo;

    public FrmLogin() {
        initComponents();
        configurarVentana();
    }

    private void initComponents() {
        setTitle("CarZone - Inicio de Sesión");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(null);
        panelPrincipal.setBackground(new Color(245, 245, 245));

        JPanel panelHeader = new JPanel(null);
        panelHeader.setBounds(0, 0, 420, 80);
        panelHeader.setBackground(new Color(30, 30, 50));

        lblTitulo = new JLabel("CARZONE");
        lblTitulo.setBounds(0, 12, 420, 32);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        lblSubtitulo = new JLabel("Sistema de Gestión de Ventas");
        lblSubtitulo.setBounds(0, 46, 420, 22);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(180, 180, 200));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        panelHeader.add(lblTitulo);
        panelHeader.add(lblSubtitulo);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(60, 110, 100, 22);
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 13));

        txtUsuario = new JTextField();
        txtUsuario.setBounds(60, 135, 300, 34);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(60, 182, 120, 22);
        lblContrasena.setFont(new Font("Arial", Font.BOLD, 13));

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(60, 207, 300, 34);
        txtContrasena.setFont(new Font("Arial", Font.PLAIN, 13));
        txtContrasena.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        btnIngresar = new JButton("INGRESAR");
        btnIngresar.setBounds(60, 265, 135, 40);
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnIngresar.setBackground(new Color(30, 30, 50));
        btnIngresar.setForeground(Color.BLACK);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCancelar = new JButton("CANCELAR");
        btnCancelar.setBounds(225, 265, 135, 40);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(200, 50, 50));
        btnCancelar.setForeground(Color.RED);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelPrincipal.add(panelHeader);
        panelPrincipal.add(lblUsuario);
        panelPrincipal.add(txtUsuario);
        panelPrincipal.add(lblContrasena);
        panelPrincipal.add(txtContrasena);
        panelPrincipal.add(btnIngresar);
        panelPrincipal.add(btnCancelar);

        add(panelPrincipal);

        btnIngresar.addActionListener(e -> accionIngresar());
        btnCancelar.addActionListener(e -> System.exit(0));

        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    accionIngresar();
                }
            }
        });
    }

    private void accionIngresar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese usuario y contraseña.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario u = dao.autenticar(usuario, contrasena);
            if (u != null) {
                dispose();
                new FrmMenuPrincipal(u).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                txtContrasena.setText("");
                txtUsuario.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarVentana() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
