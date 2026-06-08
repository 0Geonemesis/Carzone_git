package carzone.gui;

import carzone.dao.UsuarioDAO;
import carzone.modelo.Usuario;
import carzone.patron.ValidadorContrasena;
import javax.swing.*;
import java.awt.*;

public class FrmCambiarContrasena extends JFrame {

    private JPasswordField txtActual, txtNueva, txtConfirmar;
    private JButton btnGuardar, btnCancelar;
    private Usuario usuarioActual;
    private UsuarioDAO usuarioDAO;

    public FrmCambiarContrasena(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("CarZone - Cambiar Contraseña");
        setSize(400, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(245, 245, 245));

        JPanel panelHeader = new JPanel(null);
        panelHeader.setBounds(0, 0, 400, 50);
        panelHeader.setBackground(new Color(140, 80, 20));

        JLabel lblTitulo = new JLabel("Cambiar Contraseña");
        lblTitulo.setBounds(20, 12, 300, 26);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        panelHeader.add(lblTitulo);

        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombreUsuario());
        lblUsuario.setBounds(20, 60, 360, 22);
        lblUsuario.setFont(new Font("Arial", Font.ITALIC, 12));

        JLabel lblActual = new JLabel("Contraseña actual:");
        lblActual.setBounds(20, 95, 160, 22);
        lblActual.setFont(new Font("Arial", Font.BOLD, 12));

        txtActual = new JPasswordField();
        txtActual.setBounds(185, 93, 190, 28);
        txtActual.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel lblNueva = new JLabel("Nueva contraseña:");
        lblNueva.setBounds(20, 135, 160, 22);
        lblNueva.setFont(new Font("Arial", Font.BOLD, 12));

        txtNueva = new JPasswordField();
        txtNueva.setBounds(185, 133, 190, 28);
        txtNueva.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel lblConfirmar = new JLabel("Confirmar contraseña:");
        lblConfirmar.setBounds(20, 175, 160, 22);
        lblConfirmar.setFont(new Font("Arial", Font.BOLD, 12));

        txtConfirmar = new JPasswordField();
        txtConfirmar.setBounds(185, 173, 190, 28);
        txtConfirmar.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel lblRequisitos = new JLabel("<html><font color='gray' size='2'>Mín. 8 caracteres · 1 mayúscula · 1 minúscula · 1 número · 1 caracter especial</font></html>");
        lblRequisitos.setBounds(20, 210, 360, 20);

        btnGuardar = new JButton("GUARDAR");
        btnGuardar.setBounds(60, 260, 120, 38);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(40, 140, 80));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCancelar = new JButton("CANCELAR");
        btnCancelar.setBounds(220, 260, 120, 38);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(200, 50, 50));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(panelHeader);
        panel.add(lblUsuario);
        panel.add(lblActual);
        panel.add(txtActual);
        panel.add(lblNueva);
        panel.add(txtNueva);
        panel.add(lblConfirmar);
        panel.add(txtConfirmar);
        panel.add(lblRequisitos);
        panel.add(btnGuardar);
        panel.add(btnCancelar);

        add(panel);

        btnGuardar.addActionListener(e -> accionGuardar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void accionGuardar() {
        String actual = new String(txtActual.getPassword());
        String nueva = new String(txtNueva.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());

        if (!usuarioActual.getContrasena().equals(actual)) {
            JOptionPane.showMessageDialog(this, "La contraseña actual es incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ValidadorContrasena.esValida(nueva)) {
            JOptionPane.showMessageDialog(this, ValidadorContrasena.getMensajeRequisitos(), "Contraseña inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!nueva.equals(confirmar)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas nuevas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        usuarioActual.setContrasena(nueva);
        if (usuarioDAO.actualizar(usuarioActual)) {
            JOptionPane.showMessageDialog(this, "Contraseña actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
