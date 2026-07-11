package carzone.gui;

import carzone.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class FrmReportes extends JFrame {

    private final Usuario usuarioActual;

    private static final Color C_HEADER = new Color(15, 20, 40);
    private static final Color C_ACCENT = new Color(255, 200, 50);
    private static final Color C_FONDO = new Color(230, 235, 245);

    public FrmReportes(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        initComponents();
    }

    private void initComponents() {
        setTitle("CarZone — Reportes");
        setSize(820, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(null);
        panelPrincipal.setBackground(C_FONDO);

        JPanel panelHeader = new JPanel(null);
        panelHeader.setBounds(0, 0, 820, 70);
        panelHeader.setBackground(C_HEADER);

        JLabel lblTitulo = new JLabel("CARZONE  |  Reportes");
        lblTitulo.setBounds(20, 10, 400, 30);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(C_ACCENT);

        JLabel lblUsuarioInfo = new JLabel("Usuario: " + usuarioActual.getNombreUsuario()
                + "   |   Rol: " + usuarioActual.getRol().toUpperCase());
        lblUsuarioInfo.setBounds(20, 42, 500, 18);
        lblUsuarioInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUsuarioInfo.setForeground(new Color(150, 200, 255));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(690, 20, 110, 30);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 11));
        btnCerrar.setBackground(new Color(200, 50, 50));
        btnCerrar.setForeground(Color.BLACK);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelHeader.add(lblTitulo);
        panelHeader.add(lblUsuarioInfo);
        panelHeader.add(btnCerrar);

        JLabel lblSeccion = new JLabel("Reportes Disponibles");
        lblSeccion.setBounds(0, 90, 820, 25);
        lblSeccion.setFont(new Font("Arial", Font.BOLD, 16));
        lblSeccion.setForeground(new Color(30, 30, 80));
        lblSeccion.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnReporteVentas = crearTarjetaReporte(
                "📄  Reporte de Venta de Autos",
                "Historial de ventas filtrable por fechas, exportable a PDF.",
                new Color(140, 80, 20), 80, 130);

        JButton btnReporteIndicadores = crearTarjetaReporte(
                "📊  Reporte de Indicadores de Gestión",
                "Eficacia, Producto, Insumo y Resultado, con ficha técnica y PDF por indicador.",
                new Color(100, 40, 130), 80, 230);

        JButton btnReporteEstadisticas = crearTarjetaReporte(
                "📈  Reporte de Estadísticas de Ventas",
                "Máximo, mínimo y promedio de las ventas según la fecha, exportable a PDF.",
                new Color(25, 85, 140), 80, 330);

        JButton btnReporteAnuladas = crearTarjetaReporte(
                "🗑️  Reporte de Ventas Anuladas",
                "Ventas eliminadas lógicamente (anuladas), previas y nuevas, según la fecha.",
                new Color(180, 40, 40), 80, 430);

        JButton btnReporteIngresos = crearTarjetaReporte(
                "💰  Reporte de Ventas e Ingresos",
                "Detalle de ventas y de los ingresos generados según la fecha, exportable a PDF.",
                new Color(30, 120, 60), 80, 530);

        panelPrincipal.add(panelHeader);
        panelPrincipal.add(lblSeccion);
        panelPrincipal.add(btnReporteVentas);
        panelPrincipal.add(btnReporteIndicadores);
        panelPrincipal.add(btnReporteEstadisticas);
        panelPrincipal.add(btnReporteAnuladas);
        panelPrincipal.add(btnReporteIngresos);

        add(panelPrincipal);

        btnCerrar.addActionListener(e -> dispose());
        btnReporteVentas.addActionListener(e -> new FrmReporteVentasAutos(usuarioActual).setVisible(true));
        btnReporteIndicadores.addActionListener(e -> new FrmReporteIndicadores(usuarioActual).setVisible(true));
        btnReporteEstadisticas.addActionListener(e -> new FrmReporteEstadisticasVentas(usuarioActual).setVisible(true));
        btnReporteAnuladas.addActionListener(e -> new FrmReporteVentasAnuladas(usuarioActual).setVisible(true));
        btnReporteIngresos.addActionListener(e -> new FrmReporteVentasIngresos(usuarioActual).setVisible(true));
    }

    private JButton crearTarjetaReporte(String titulo, String descripcion, Color colorFondo, int x, int y) {
        String html = "<html><div style='width:600px;'>"
                + "<span style='font-size:14px;font-weight:bold;'>" + titulo + "</span><br>"
                + "<span style='font-size:11px;'>" + descripcion + "</span>"
                + "</div></html>";
        JButton btn = new JButton(html);
        btn.setBounds(x, y, 660, 90);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(10, 16, 10, 10));
        return btn;
    }
}
