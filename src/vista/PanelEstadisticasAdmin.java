package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import negocio.ResumenEstadisticas;
import negocio.Turno;
import servicio.EstadisticasService;

public class PanelEstadisticasAdmin extends JPanel {

    private static final long serialVersionUID = 1L;

    private final PanelManager panelManager;
    private final EstadisticasService estadisticasService;

    public PanelEstadisticasAdmin(PanelManager panelManager) {
        this.panelManager = panelManager;
        this.estadisticasService = new EstadisticasService();
    }

    public void armarPanelEstadisticas(String usuario) {
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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(22, 30, 15, 30));

        JLabel titulo = new JLabel("Resumen administrativo");
        titulo.setFont(EstilosUI.FUENTE_TITULO);
        titulo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Indicadores generales y agenda correspondiente a hoy, "
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

    private JPanel crearContenido(ResumenEstadisticas resumen) {
        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(EstilosUI.FONDO_PRINCIPAL);
        contenido.setBorder(new EmptyBorder(0, 30, 10, 30));

        contenido.add(crearTarjetas(resumen), BorderLayout.NORTH);
        contenido.add(crearPanelTurnosHoy(resumen), BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearTarjetas(ResumenEstadisticas resumen) {
        JPanel panel = new JPanel(new GridLayout(2, 4, 12, 12));
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

        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(EstilosUI.FONDO_SECUNDARIO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        4, 0, 0, 0, colorAcento
                ),
                new EmptyBorder(14, 15, 14, 15)
        ));
        tarjeta.setPreferredSize(new Dimension(180, 105));

        JLabel lblValor = new JLabel(Integer.toString(valor));
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

    private JPanel crearPanelTurnosHoy(
            ResumenEstadisticas resumen) {

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);

        JLabel titulo = new JLabel("Agenda de hoy");
        titulo.setFont(EstilosUI.FUENTE_SUBTITULO);
        titulo.setForeground(EstilosUI.TEXTO_PRINCIPAL);

        DefaultTableModel modelo = new DefaultTableModel(
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

    private JPanel crearBotonera(String usuario) {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(10, 30, 18, 30));

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

    private String formatearFecha(LocalDate fecha) {
        return String.format(
                "%02d/%02d/%04d",
                fecha.getDayOfMonth(),
                fecha.getMonthValue(),
                fecha.getYear()
        );
    }

    private String formatearHora(LocalTime hora) {
        return hora == null
                ? ""
                : String.format(
                        "%02d:%02d",
                        hora.getHour(),
                        hora.getMinute()
                );
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Estadísticas no disponibles",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
