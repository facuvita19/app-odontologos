package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import negocio.EstadoTurno;
import negocio.ResumenEstadisticas;
import negocio.Turno;
import servicio.EstadisticasService;

public class PanelEstadisticasAdmin extends JPanel {

    private static final long serialVersionUID = 1L;

    private final PanelManager panelManager;
    private final EstadisticasService estadisticasService;

    public PanelEstadisticasAdmin(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.estadisticasService =
                new EstadisticasService();
    }

    public void armarPanelEstadisticas(
            String usuario) {

        removeAll();
        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        try {
            ResumenEstadisticas resumen =
                    estadisticasService.obtenerResumen();

            add(crearEncabezado(), BorderLayout.NORTH);
            add(crearContenido(resumen), BorderLayout.CENTER);
            add(crearBotonera(usuario), BorderLayout.SOUTH);

        } catch (RuntimeException exception) {
            exception.printStackTrace();
            mostrarError(
                    "No se pudieron cargar las estadísticas."
            );
            panelManager.mostrarPanelPrincipalAdmin(usuario);
            return;
        }

        revalidate();
        repaint();
        setVisible(true);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(20, 30, 12, 30));

        JLabel titulo = new JLabel("Resumen administrativo");
        titulo.setFont(EstilosUI.FUENTE_TITULO);
        titulo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Indicadores, gráficos y agenda de hoy, "
                        + formatearFecha(LocalDate.now())
        );
        subtitulo.setFont(EstilosUI.FUENTE_NORMAL);
        subtitulo.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitulo);
        return panel;
    }

    private JPanel crearContenido(
            ResumenEstadisticas resumen) {

        JPanel contenido = new JPanel(
                new BorderLayout(0, 14)
        );
        contenido.setBackground(EstilosUI.FONDO_PRINCIPAL);
        contenido.setBorder(new EmptyBorder(0, 30, 8, 30));

        contenido.add(
                crearTarjetas(resumen),
                BorderLayout.NORTH
        );
        contenido.add(
                crearPestanas(resumen),
                BorderLayout.CENTER
        );

        return contenido;
    }

    private JPanel crearTarjetas(
            ResumenEstadisticas resumen) {

        JPanel panel = new JPanel(
                new GridLayout(2, 4, 10, 10)
        );
        panel.setOpaque(false);

        panel.add(crearTarjeta(
                "Pacientes activos",
                resumen.getPacientesActivos(),
                EstilosUI.COLOR_PRIMARIO
        ));
        panel.add(crearTarjeta(
                "Odontólogos activos",
                resumen.getOdontologosActivos(),
                EstilosUI.COLOR_EXITO
        ));
        panel.add(crearTarjeta(
                "Turnos pendientes",
                resumen.getTurnosPendientes(),
                new Color(230, 167, 45)
        ));
        panel.add(crearTarjeta(
                "Turnos confirmados",
                resumen.getTurnosConfirmados(),
                new Color(77, 145, 230)
        ));
        panel.add(crearTarjeta(
                "Atendidos este mes",
                resumen.getAtendidosMes(),
                EstilosUI.COLOR_EXITO
        ));
        panel.add(crearTarjeta(
                "Cancelados este mes",
                resumen.getCanceladosMes(),
                EstilosUI.COLOR_PELIGRO
        ));
        panel.add(crearTarjeta(
                "Ausentes este mes",
                resumen.getAusentesMes(),
                new Color(145, 103, 190)
        ));
        panel.add(crearTarjeta(
                "Turnos programados hoy",
                resumen.getTurnosHoy(),
                EstilosUI.COLOR_PRIMARIO
        ));

        return panel;
    }

    private JPanel crearTarjeta(
            String titulo,
            int valor,
            Color colorAcento) {

        JPanel tarjeta = new JPanel(
                new BorderLayout(0, 5)
        );
        tarjeta.setBackground(EstilosUI.FONDO_SECUNDARIO);
        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                4,
                                0,
                                0,
                                0,
                                colorAcento
                        ),
                        new EmptyBorder(10, 12, 10, 12)
                )
        );
        tarjeta.setPreferredSize(new Dimension(165, 80));

        JLabel lblValor = new JLabel(
                Integer.toString(valor)
        );
        lblValor.setFont(EstilosUI.FUENTE_TITULO);
        lblValor.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTitulo = new JLabel(
                "<html><div style='text-align:center;'>"
                        + titulo
                        + "</div></html>"
        );
        lblTitulo.setFont(EstilosUI.FUENTE_NORMAL);
        lblTitulo.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        tarjeta.add(lblValor, BorderLayout.CENTER);
        tarjeta.add(lblTitulo, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JTabbedPane crearPestanas(
            ResumenEstadisticas resumen) {

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(EstilosUI.FUENTE_NORMAL);
        pestanas.setBackground(EstilosUI.FONDO_SECUNDARIO);
        pestanas.setForeground(EstilosUI.TEXTO_PRINCIPAL);

        pestanas.addTab(
                "Gráficos",
                crearPanelGraficos(resumen)
        );
        pestanas.addTab(
                "Agenda de hoy",
                crearPanelTurnosHoy(resumen)
        );

        return pestanas;
    }

    private JPanel crearPanelGraficos(
            ResumenEstadisticas resumen) {

        JPanel panel = new JPanel(
                new GridLayout(1, 2, 14, 0)
        );
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        ChartPanel graficoEstados =
                crearGraficoEstados(resumen);
        ChartPanel graficoMeses =
                crearGraficoMeses(resumen);

        panel.add(graficoEstados);
        panel.add(graficoMeses);
        return panel;
    }

    private ChartPanel crearGraficoEstados(
            ResumenEstadisticas resumen) {

        DefaultPieDataset<String> dataset =
                new DefaultPieDataset<>();

        for (EstadoTurno estado : EstadoTurno.values()) {
            int cantidad = resumen.getTurnosPorEstado()
                    .getOrDefault(estado, 0);
            dataset.setValue(
                    nombreEstado(estado),
                    cantidad
            );
        }

        JFreeChart grafico = ChartFactory.createPieChart(
                "Distribución por estado",
                dataset,
                true,
                true,
                false
        );

        aplicarEstiloGeneral(grafico);

        @SuppressWarnings("unchecked")
        PiePlot<String> plot =
                (PiePlot<String>) grafico.getPlot();

        plot.setBackgroundPaint(EstilosUI.FONDO_SECUNDARIO);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelBackgroundPaint(
                EstilosUI.FONDO_PRINCIPAL
        );
        plot.setLabelPaint(EstilosUI.TEXTO_PRINCIPAL);
        plot.setLabelOutlinePaint(EstilosUI.BORDE);
        plot.setLabelShadowPaint(null);
        plot.setLabelFont(new Font(
                Font.SANS_SERIF,
                Font.PLAIN,
                11
        ));
        plot.setLabelGenerator(
                new StandardPieSectionLabelGenerator(
                        "{0}: {1} ({2})"
                )
        );
        plot.setNoDataMessage("No hay turnos registrados");
        plot.setNoDataMessagePaint(EstilosUI.TEXTO_SECUNDARIO);

        plot.setSectionPaint(
                "Pendientes",
                new Color(230, 167, 45)
        );
        plot.setSectionPaint(
                "Confirmados",
                new Color(77, 145, 230)
        );
        plot.setSectionPaint(
                "Atendidos",
                EstilosUI.COLOR_EXITO
        );
        plot.setSectionPaint(
                "Cancelados",
                EstilosUI.COLOR_PELIGRO
        );
        plot.setSectionPaint(
                "Ausentes",
                new Color(145, 103, 190)
        );

        return crearChartPanel(grafico);
    }

    private ChartPanel crearGraficoMeses(
            ResumenEstadisticas resumen) {

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        for (Map.Entry<String, Integer> entrada :
                resumen.getTurnosPorMes().entrySet()) {

            dataset.addValue(
                    entrada.getValue(),
                    "Turnos",
                    entrada.getKey()
            );
        }

        JFreeChart grafico = ChartFactory.createBarChart(
                "Turnos de los últimos seis meses",
                "Mes",
                "Cantidad",
                dataset
        );

        aplicarEstiloGeneral(grafico);

        CategoryPlot plot =
                grafico.getCategoryPlot();

        plot.setBackgroundPaint(EstilosUI.FONDO_SECUNDARIO);
        plot.setDomainGridlinePaint(EstilosUI.BORDE);
        plot.setRangeGridlinePaint(EstilosUI.BORDE);
        plot.setOutlineVisible(false);

        CategoryAxis ejeMes = plot.getDomainAxis();
        ejeMes.setLabelPaint(EstilosUI.TEXTO_SECUNDARIO);
        ejeMes.setTickLabelPaint(EstilosUI.TEXTO_PRINCIPAL);
        ejeMes.setTickLabelFont(new Font(
                Font.SANS_SERIF,
                Font.PLAIN,
                11
        ));

        NumberAxis ejeCantidad =
                (NumberAxis) plot.getRangeAxis();
        ejeCantidad.setStandardTickUnits(
                NumberAxis.createIntegerTickUnits()
        );
        ejeCantidad.setLowerBound(0.0);
        ejeCantidad.setLabelPaint(EstilosUI.TEXTO_SECUNDARIO);
        ejeCantidad.setTickLabelPaint(EstilosUI.TEXTO_PRINCIPAL);

        BarRenderer renderer =
                (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(
                0,
                EstilosUI.COLOR_PRIMARIO
        );
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.12);

        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlineStroke(
                new BasicStroke(1.0f)
        );
        plot.setNoDataMessage("No hay turnos registrados");
        plot.setNoDataMessagePaint(EstilosUI.TEXTO_SECUNDARIO);

        return crearChartPanel(grafico);
    }

    private void aplicarEstiloGeneral(
            JFreeChart grafico) {

        grafico.setBackgroundPaint(
                EstilosUI.FONDO_SECUNDARIO
        );
        grafico.getTitle().setPaint(
                EstilosUI.TEXTO_PRINCIPAL
        );
        grafico.getTitle().setFont(new Font(
                Font.SANS_SERIF,
                Font.BOLD,
                15
        ));

        if (grafico.getLegend() != null) {
            grafico.getLegend().setBackgroundPaint(
                    EstilosUI.FONDO_SECUNDARIO
            );
            grafico.getLegend().setItemPaint(
                    EstilosUI.TEXTO_PRINCIPAL
            );
            grafico.getLegend().setFrame(
                    org.jfree.chart.block.BlockBorder.NONE
            );
        }
    }

    private ChartPanel crearChartPanel(
            JFreeChart grafico) {

        ChartPanel panel = new ChartPanel(grafico);
        panel.setBackground(EstilosUI.FONDO_SECUNDARIO);
        panel.setBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE)
        );
        panel.setMouseWheelEnabled(true);
        panel.setPopupMenu(null);
        panel.setPreferredSize(new Dimension(420, 310));
        panel.setMinimumDrawWidth(250);
        panel.setMinimumDrawHeight(200);
        panel.setMaximumDrawWidth(1400);
        panel.setMaximumDrawHeight(900);
        return panel;
    }

    private JPanel crearPanelTurnosHoy(
            ResumenEstadisticas resumen) {

        JPanel panel = new JPanel(
                new BorderLayout(0, 10)
        );
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel titulo = new JLabel("Agenda de hoy");
        titulo.setFont(EstilosUI.FUENTE_SUBTITULO);
        titulo.setForeground(EstilosUI.TEXTO_PRINCIPAL);

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[]{
                                "Inicio",
                                "Fin",
                                "Paciente",
                                "Odontólogo",
                                "Estado"
                        },
                        0
                ) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(
                            int fila,
                            int columna) {
                        return false;
                    }
                };

        for (Turno turno : resumen.getTurnosDelDia()) {
            modelo.addRow(new Object[]{
                    formatearHora(turno.getHoraInicio()),
                    formatearHora(turno.getHoraFin()),
                    turno.getNomPaciente(),
                    turno.getNomOdontologo(),
                    turno.getEstado()
            });
        }

        JTable tabla = new JTable(modelo);
        EstilosUI.prepararTabla(tabla);
        tabla.setFillsViewportHeight(true);

        DefaultTableCellRenderer centrado =
                new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer encabezado =
                (DefaultTableCellRenderer)
                        tabla.getTableHeader()
                                .getDefaultRenderer();
        encabezado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int columna = 0;
                columna < tabla.getColumnCount();
                columna++) {
            tabla.getColumnModel()
                    .getColumn(columna)
                    .setCellRenderer(centrado);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE)
        );
        scroll.getViewport().setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBotonera(
            String usuario) {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(8, 30, 16, 30));

        JButton btnActualizar =
                EstilosUI.crearBotonPrimario("Actualizar");
        JButton btnVolver =
                EstilosUI.crearBotonSecundario("Volver");

        btnActualizar.addActionListener(
                evento -> armarPanelEstadisticas(usuario)
        );
        btnVolver.addActionListener(
                evento -> panelManager
                        .mostrarPanelPrincipalAdmin(usuario)
        );

        panel.add(btnVolver);
        panel.add(btnActualizar);
        return panel;
    }

    private String nombreEstado(
            EstadoTurno estado) {

        switch (estado) {
            case PENDIENTE:
                return "Pendientes";
            case CONFIRMADO:
                return "Confirmados";
            case ATENDIDO:
                return "Atendidos";
            case CANCELADO:
                return "Cancelados";
            case AUSENTE:
                return "Ausentes";
            default:
                return estado.toString();
        }
    }

    private String formatearFecha(
            LocalDate fecha) {

        return String.format(
                "%02d/%02d/%04d",
                fecha.getDayOfMonth(),
                fecha.getMonthValue(),
                fecha.getYear()
        );
    }

    private String formatearHora(
            LocalTime hora) {

        return hora == null
                ? ""
                : String.format(
                        "%02d:%02d",
                        hora.getHour(),
                        hora.getMinute()
                );
    }

    private void mostrarError(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Estadísticas no disponibles",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
