package carzone.gui;

import carzone.dao.IndicadorDAO;
import carzone.modelo.Indicador;
import carzone.modelo.Usuario;
import carzone.reporte.Reporte;
import carzone.reporte.ReporteFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class FrmReporteIndicadores extends JFrame {

    private final IndicadorDAO indicadorDAO = new IndicadorDAO();
    private final Usuario usuarioActual;

    private static final Color C_HEADER = new Color(15, 20, 40);
    private static final Color C_ACCENT = new Color(255, 200, 50);
    private static final Color C_AZUL = new Color(25, 85, 140);
    private static final Color C_VERDE = new Color(30, 120, 60);
    private static final Color C_MORADO = new Color(100, 40, 130);
    private static final Color C_ROJO = new Color(180, 40, 40);
    private static final Color C_FONDO = new Color(230, 235, 245);
    private static final Color C_PANEL = new Color(245, 248, 255);

    private List<Indicador> indicadores;

    public FrmReporteIndicadores(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        initComponents();
    }

    private void initComponents() {
        setTitle("CarZone — Reporte de Indicadores de Gestión");
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 8));
        panelPrincipal.setBackground(C_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        panelPrincipal.add(crearHeader(), BorderLayout.NORTH);

        indicadores = indicadorDAO.listarIndicadoresGestion();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        tabs.setBackground(C_PANEL);

        for (Indicador ind : indicadores) {
            tabs.addTab(ind.getCategoria(), crearPestanaIndicador(ind));
        }

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(C_FONDO);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panelCentro.add(tabs, BorderLayout.CENTER);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private JPanel crearHeader() {
        JPanel p = new JPanel(null);
        p.setBackground(C_HEADER);
        p.setPreferredSize(new Dimension(1080, 60));

        JLabel lblTitulo = new JLabel("CARZONE  |  Reporte de Indicadores de Gestión");
        lblTitulo.setBounds(16, 8, 600, 26);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(C_ACCENT);

        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombreUsuario()
                + "   |   Rol: " + usuarioActual.getRol().toUpperCase());
        lblUsuario.setBounds(16, 36, 500, 16);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 11));
        lblUsuario.setForeground(new Color(150, 200, 255));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(960, 15, 100, 30);
        estilizarBoton(btnCerrar, C_ROJO, Color.WHITE);
        btnCerrar.addActionListener(e -> dispose());

        p.add(lblTitulo);
        p.add(lblUsuario);
        p.add(btnCerrar);
        return p;
    }

    private JPanel crearPestanaIndicador(Indicador ind) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(C_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(crearPanelResumen(ind), BorderLayout.NORTH);

        JPanel panelMedio = new JPanel(new BorderLayout(0, 6));
        panelMedio.setBackground(C_FONDO);
        panelMedio.add(crearPanelFicha(ind), BorderLayout.NORTH);
        panelMedio.add(crearPanelTabla(ind), BorderLayout.CENTER);

        panel.add(panelMedio, BorderLayout.CENTER);
        panel.add(crearPanelBotones(ind), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelResumen(Indicador ind) {
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
        p.setBackground(C_FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(0, 10, 4, 10));

        JLabel lblValor = crearTarjetaMetrica(ind.getValorActual());
        JLabel lblCategoria = crearTarjetaMetrica(ind.getCategoria());

        p.add(contenedorTarjeta("Valor actual calculado", lblValor, C_VERDE));
        p.add(contenedorTarjeta("Categoría del indicador", lblCategoria, C_MORADO));
        return p;
    }

    private JLabel crearTarjetaMetrica(String valor) {
        JLabel lbl = new JLabel(valor, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JPanel contenedorTarjeta(String titulo, JLabel lblValor, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
        lblTit.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTit.setForeground(new Color(230, 230, 255));
        p.add(lblTit, BorderLayout.NORTH);
        p.add(lblValor, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelFicha(Indicador ind) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 190, 220)),
                        ind.getNombre(), TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12), C_AZUL),
                BorderFactory.createEmptyBorder(4, 12, 10, 12)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(crearLineaFicha("Descripción:", ind.getDescripcion()));
        p.add(crearLineaFicha("Fórmula:", ind.getFormula()));
        p.add(crearLineaFicha("Período de medición:", ind.getPeriodoMedicion()));
        p.add(crearLineaFicha("Meta:", ind.getMeta()));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(C_FONDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        wrapper.add(p, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel crearLineaFicha(String etiqueta, String contenido) {
        String html = "<html><div style='width:940px;padding:2px 0;'>"
                + "<span style='font-weight:bold;color:#19558c;'>" + etiqueta + "</span> "
                + "<span>" + contenido + "</span></div></html>";
        JLabel lbl = new JLabel(html);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JScrollPane crearPanelTabla(Indicador ind) {
        String[] columnas = ind.getColumnasDetalle();
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        if (ind.getFilasDetalle() != null) {
            for (String[] fila : ind.getFilasDetalle()) {
                modelo.addRow(fila);
            }
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(C_HEADER);
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.setSelectionBackground(new Color(180, 210, 255));
        tabla.setGridColor(new Color(200, 210, 230));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 190, 220)),
                        "Detalle de Datos", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 11), C_AZUL)));
        scroll.setPreferredSize(new Dimension(1040, 260));
        return scroll;
    }

    private JPanel crearPanelBotones(Indicador ind) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        p.setBackground(C_FONDO);

        JButton btnDescargar = new JButton("Descargar PDF de este Indicador");
        estilizarBoton(btnDescargar, C_VERDE, Color.WHITE);
        btnDescargar.addActionListener(e -> descargarPDF(ind));

        p.add(btnDescargar);
        return p;
    }

    private void descargarPDF(Indicador ind) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Reporte del Indicador");
        String nombreSugerido = "Indicador_" + ind.getCodigo() + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".pdf";
        chooser.setSelectedFile(new File(nombreSugerido));

        int seleccion = chooser.showSaveDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();
        String ruta = archivo.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta = ruta + ".pdf";
        }

        try {
            Reporte reporte = ReporteFactory.crear(ind, usuarioActual.getNombreUsuario());
            reporte.exportarPDF(ruta);

            int abrir = JOptionPane.showConfirmDialog(this,
                    "El reporte se generó correctamente en:\n" + ruta + "\n\n¿Desea abrirlo ahora?",
                    "Reporte generado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (abrir == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(new File(ruta));
                } catch (Exception exAbrir) {
                    JOptionPane.showMessageDialog(this, "No se pudo abrir el archivo automáticamente.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte PDF:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void estilizarBoton(JButton btn, Color fondo, Color texto) {
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
