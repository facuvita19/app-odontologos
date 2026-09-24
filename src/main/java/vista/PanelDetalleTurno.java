package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import negocio.Odontologo;
import negocio.Turno;
import servicio.OdontologoService;

public class PanelDetalleTurno extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PanelManager panelManager;
    private final OdontologoService odontologoService;

    public PanelDetalleTurno(PanelManager panelManager) {
        this.panelManager = panelManager;
        this.odontologoService = new OdontologoService();
    }

    public void armarPanelDetalleTurno(
            Turno turno,
            String usuario) {

        removeAll();
        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(EstilosUI.FONDO_PRINCIPAL);
        contenedor.setBorder(new EmptyBorder(30, 80, 30, 80));

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weightx = 1.0;
        restricciones.weighty = 1.0;
        restricciones.fill = GridBagConstraints.BOTH;

        contenedor.add(crearTarjeta(turno, usuario), restricciones);
        add(contenedor, BorderLayout.CENTER);

        revalidate();
        repaint();
        setVisible(true);
    }

    private JPanel crearTarjeta(Turno turno, String usuario) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 20));
        tarjeta.setBackground(EstilosUI.FONDO_SECUNDARIO);
        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE),
                        new EmptyBorder(28, 38, 28, 38)
                )
        );

        tarjeta.add(crearEncabezado(), BorderLayout.NORTH);
        tarjeta.add(crearContenido(turno, usuario), BorderLayout.CENTER);
        tarjeta.add(crearBotonera(usuario), BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titulo = new JLabel("Detalle del turno");
        titulo.setFont(EstilosUI.FUENTE_TITULO);
        titulo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Información completa de la reserva seleccionada"
        );
        subtitulo.setFont(EstilosUI.FUENTE_NORMAL);
        subtitulo.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(7));
        panel.add(subtitulo);
        return panel;
    }

    private JScrollPane crearContenido(Turno turno, String usuario) {
        JPanel datos = new JPanel(new GridBagLayout());
        datos.setOpaque(false);

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.anchor = GridBagConstraints.NORTHWEST;

        Odontologo odontologo = turno == null
                ? null
                : odontologoService.buscar(turno.getOdontologoId());

        int fila = 0;
        agregarDato(datos, restricciones, fila++, "Paciente:",
                turno == null ? "" : turno.getNomPaciente());
        agregarDato(datos, restricciones, fila++, "Odontólogo:",
                turno == null ? "" : turno.getNomOdontologo());
        agregarDato(datos, restricciones, fila++, "Especialidad:",
                odontologo == null ? "Sin identificar"
                        : odontologo.getEspecialidad().toString());
        agregarDato(datos, restricciones, fila++, "Fecha:",
                formatearFecha(turno));
        agregarDato(datos, restricciones, fila++, "Horario:",
                formatearHorario(turno));
        agregarDato(datos, restricciones, fila++, "Estado:",
                turno == null || turno.getEstado() == null
                        ? "PENDIENTE"
                        : turno.getEstado().toString());
        agregarDato(datos, restricciones, fila++, "Motivo de consulta:",
                textoVisible(turno == null ? null : turno.getMotivoConsulta()));
        agregarDato(datos, restricciones, fila++, "Usuario que reservó:",
                turno == null ? "" : turno.getNomUsuario());
        agregarDato(datos, restricciones, fila++, "Fecha de creación:",
                formatearFechaCreacion(
                        turno == null ? null : turno.getFechaCreacion()
                ));

        if ("admin".equalsIgnoreCase(usuario)) {
            agregarDato(
                    datos,
                    restricciones,
                    fila,
                    "Observaciones administrativas:",
                    textoVisible(turno == null
                            ? null
                            : turno.getObservaciones())
            );
        }

        JScrollPane scroll = new JScrollPane(datos);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(650, 430));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private void agregarDato(
            JPanel panel,
            GridBagConstraints restricciones,
            int fila,
            String nombre,
            String valor) {

        JLabel etiqueta = EstilosUI.crearEtiqueta(nombre);

        restricciones.gridx = 0;
        restricciones.gridy = fila;
        restricciones.weightx = 0.0;
        restricciones.weighty = 0.0;
        restricciones.insets = new Insets(8, 0, 8, 22);
        panel.add(etiqueta, restricciones);

        JTextArea texto = new JTextArea(valor == null ? "" : valor);
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setOpaque(false);
        texto.setFont(EstilosUI.FUENTE_NORMAL);
        texto.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        texto.setRows(valor != null && valor.length() > 75 ? 3 : 1);

        restricciones.gridx = 1;
        restricciones.weightx = 1.0;
        restricciones.insets = new Insets(8, 0, 8, 0);
        panel.add(texto, restricciones);
    }

    private JPanel crearBotonera(String usuario) {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        panel.setOpaque(false);

        JButton btnVolver = EstilosUI.crearBotonSecundario("Volver");
        btnVolver.addActionListener(evento -> {
            if ("admin".equalsIgnoreCase(usuario)) {
                panelManager.mostrarPanelListaTurnosAdmin(usuario);
            } else {
                panelManager.mostrarPanelListaTurnosUsuario(usuario);
            }
        });

        panel.add(btnVolver);
        return panel;
    }

    private String formatearFecha(Turno turno) {
        if (turno == null) {
            return "";
        }
        try {
            return LocalDate.of(
                    turno.getAño(),
                    turno.getMes(),
                    turno.getDia()
            ).format(FORMATO_FECHA);
        } catch (RuntimeException exception) {
            return "Fecha no disponible";
        }
    }

    private String formatearHorario(Turno turno) {
        if (turno == null) {
            return "";
        }
        return formatearHora(turno.getHoraInicio())
                + " a "
                + formatearHora(turno.getHoraFin());
    }

    private String formatearHora(LocalTime hora) {
        if (hora == null) {
            return "--:--";
        }
        return String.format("%02d:%02d", hora.getHour(), hora.getMinute());
    }

    private String formatearFechaCreacion(LocalDateTime fecha) {
        return fecha == null
                ? "No disponible"
                : fecha.format(FORMATO_FECHA_HORA);
    }

    private String textoVisible(String valor) {
        return valor == null || valor.trim().isEmpty()
                ? "Sin información"
                : valor.trim();
    }
}

