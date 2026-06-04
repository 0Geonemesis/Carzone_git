package carzone.view;

import carzone.dao.UsuarioDAO;
import carzone.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Patrón GUI: Form Entry Pattern
 * Login con soporte para dos perfiles: administrador y vendedor.
 */
public class LoginForm extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JButton btnIngresar;
    private JButton btnCancelar;
    private JLabel lblMensaje;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginForm() {
        initComponents();
    }

    private void initComponents() {
        setTitle("CarZone - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con color corporativo
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(30, 30, 45));

        // Header
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(255, 200, 0));
        panelHeader.setPreferredSize(new Dimension(0, 70));
        JLabel lblTitulo = new JLabel("CARZONE");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(30, 30, 45));
        panelHeader.add(lblTitulo);

        // Panel de formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(30, 30, 45));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        // Usuario
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panelForm.add(lblUser, gbc);

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        panelForm.add(txtUsuario, gbc);

        // Contraseña
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panelForm.add(lblPass, gbc);

        txtClave = new JPasswordField();
        txtClave.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        panelForm.add(txtClave, gbc);

        // Mensaje de error/éxito
        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(new Color(255, 80, 80));
        lblMensaje.setFont(new Font("Arial", Font.ITALIC, 11));
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelForm.add(lblMensaje, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(new Color(30, 30, 45));

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(new Color(255, 200, 0));
        btnIngresar.setForeground(new Color(30, 30, 45));
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnIngresar.setPreferredSize(new Dimension(110, 35));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(80, 80, 95));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancelar.setPreferredSize(new Dimension(110, 35));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panelBotones.add(btnIngresar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(panelBotones, gbc);

        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        panelPrincipal.add(panelForm, BorderLayout.CENTER);

        setContentPane(panelPrincipal);

        // Eventos
        btnIngresar.addActionListener(e -> intentarLogin());
        btnCancelar.addActionListener(e -> System.exit(0));

        // Enter para login
        txtClave.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });
    }

    private void intentarLogin() {
        // Validación de tipo de dato y longitud
        String usuario = txtUsuario.getText().trim();
        String clave   = new String(txtClave.getPassword());

        if (usuario.isEmpty()) {
            mostrarError("El campo usuario es obligatorio.");
            txtUsuario.requestFocus();
            return;
        }
        if (usuario.length() > 50) {
            mostrarError("El usuario no puede superar 50 caracteres.");
            return;
        }
        if (clave.isEmpty()) {
            mostrarError("Ingrese su contraseña.");
            txtClave.requestFocus();
            return;
        }
        if (clave.length() > 50) {
            mostrarError("Contraseña no puede superar 50 caracteres.");
            return;
        }

        Usuario u = usuarioDAO.autenticar(usuario, clave);
        if (u == null) {
            mostrarError("Usuario o contraseña incorrectos.");
            txtClave.setText("");
            txtUsuario.requestFocus();
            return;
        }

        // Abrir dashboard según perfil
        dispose();
        DashboardForm dash = new DashboardForm(u);
        dash.setVisible(true);
    }

    private void mostrarError(String msg) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(new Color(255, 80, 80));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
