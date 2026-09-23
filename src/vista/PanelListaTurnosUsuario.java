package vista;

import java.awt.BorderLayout;
import java.time.LocalTime;
import java.util.List;

import javax.swing.BorderFactory;
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

import negocio.EstadoTurno;
import negocio.Turno;
import servicio.TurnoService;

public class PanelListaTurnosUsuario extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable tabla;
    private DefaultTableModel contenidoTabla;
    private JScrollPane scrollPane;
    private JPanel botonera;

    private JButton btnCrear;
    private JButton btnCancelarTurno;
    private JButton btnModificar;
    private JButton btnVolver;

    private final PanelManager panelManager;
    private final TurnoService turnoService;

    public PanelListaTurnosUsuario(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.turnoService = new TurnoService();
    }

    public void armarPanelListaTurnos(
            String usuario) {

        removeAll();
        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        JLabel titulo =
                EstilosUI.crearTitulo("Mis turnos");

        crearTabla();
        cargarTurnos(usuario);

        botonera = crearBotonera();

        add(titulo, BorderLayout.NORTH);
        add(
                crearContenidoTabla(),
                BorderLayout.CENTER
        );
        add(botonera, BorderLayout.SOUTH);

        configurarEventos(usuario);

        revalidate();
        repaint();
        setVisible(true);
    }

    private void crearTabla() {
        contenidoTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Paciente",
                        "Odontólogo",
                        "Estado",
                        "Día",
                        "Mes",
                        "Año",
                        "Inicio",
                        "Fin"
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

        tabla = new JTable(contenidoTabla);

        EstilosUI.prepararTabla(tabla);
        configurarAlineacionTabla();

        tabla.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        tabla.setFillsViewportHeight(true);

        definirAnchoColumnas();

        scrollPane = new JScrollPane(tabla);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        EstilosUI.BORDE
                )
        );

        scrollPane.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        scrollPane.getViewport().setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        scrollPane.getVerticalScrollBar().setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        scrollPane.getHorizontalScrollBar().setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );
    }
    private JPanel crearContenidoTabla() {
        JPanel contenedor = new JPanel(
                new BorderLayout()
        );

        contenedor.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        contenedor.setBorder(
                new EmptyBorder(
                        0,
                        20,
                        10,
                        20
                )
        );

        contenedor.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return contenedor;
    }
    private void configurarAlineacionTabla() {
        DefaultTableCellRenderer encabezadoCentrado =
                (DefaultTableCellRenderer)
                        tabla.getTableHeader()
                                .getDefaultRenderer();

        encabezadoCentrado.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        DefaultTableCellRenderer celdasCentradas =
                new DefaultTableCellRenderer();

        celdasCentradas.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        for (int columna = 0;
                columna < tabla.getColumnCount();
                columna++) {

            tabla.getColumnModel()
                    .getColumn(columna)
                    .setCellRenderer(
                            celdasCentradas
                    );
        }
    }

    private void definirAnchoColumnas() {
        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(45);

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(170);

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(170);

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(110);

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(50);

        tabla.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(50);

        tabla.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(70);

        tabla.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(80);

        tabla.getColumnModel()
                .getColumn(8)
                .setPreferredWidth(80);
    }

    private void cargarTurnos(
            String usuario) {

        List<Turno> turnos =
                turnoService.listar();

        for (Turno turno : turnos) {
            if (!perteneceAlUsuario(
                    turno,
                    usuario)) {

                continue;
            }

            EstadoTurno estado =
                    turno.getEstado();

            if (estado == null) {
                estado = EstadoTurno.PENDIENTE;
            }

            Object[] fila = {
                    turno.getId(),
                    turno.getNomPaciente(),
                    turno.getNomOdontologo(),
                    estado,
                    turno.getDia(),
                    turno.getMes(),
                    turno.getAño(),
                    formatearHora(
                            turno.getHoraInicio()
                    ),
                    formatearHora(
                            turno.getHoraFin()
                    )
            };

            contenidoTabla.addRow(fila);
        }
    }

    private boolean perteneceAlUsuario(
            Turno turno,
            String usuario) {

        if (turno == null
                || turno.getNomUsuario() == null
                || usuario == null) {

            return false;
        }

        return turno.getNomUsuario()
                .equalsIgnoreCase(
                        usuario.trim()
                );
    }

    private String formatearHora(
            LocalTime hora) {

        if (hora == null) {
            return "";
        }

        return String.format(
                "%02d:%02d",
                hora.getHour(),
                hora.getMinute()
        );
    }

    private JPanel crearBotonera() {
        JPanel panelBotones =
                EstilosUI.crearPanelBotonera();

        btnCrear =
                EstilosUI.crearBotonPrimario(
                        "Nuevo turno"
                );

        btnModificar =
                EstilosUI.crearBotonSecundario(
                        "Modificar"
                );

        btnCancelarTurno =
                EstilosUI.crearBotonPeligro(
                        "Cancelar turno"
                );

        btnVolver =
                EstilosUI.crearBotonSecundario(
                        "Volver"
                );

        panelBotones.add(btnCrear);
        panelBotones.add(btnModificar);
        panelBotones.add(btnCancelarTurno);
        panelBotones.add(btnVolver);

        return panelBotones;
    }

    private void configurarEventos(
            String usuario) {

        btnCrear.addActionListener(
                evento -> crearTurno(usuario)
        );

        btnModificar.addActionListener(
                evento -> modificarTurno(usuario)
        );

        btnCancelarTurno.addActionListener(
                evento -> cancelarTurno(usuario)
        );

        btnVolver.addActionListener(
                evento ->
                        panelManager
                                .mostrarPanelPrincipalUsuario(
                                        usuario
                                )
        );

        tabla.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent evento) {

                        if (evento.getClickCount() == 2) {
                            modificarTurno(usuario);
                        }
                    }
                }
        );
    }

    private void crearTurno(
            String usuario) {

        JOptionPane.showMessageDialog(
                this,
                "A continuación seleccione "
                        + "el odontólogo con el que "
                        + "desea atenderse.",
                "Seleccionar odontólogo",
                JOptionPane.INFORMATION_MESSAGE
        );

        panelManager.mostrarPanelSeleccionOdontologo(
                usuario
        );
    }

    private void modificarTurno(
            String usuario) {

        Long turnoId =
                obtenerIdSeleccionado();

        if (turnoId == null) {
            return;
        }

        try {
            Turno turno =
                    turnoService.buscar(turnoId);

            if (turno == null) {
                mostrarError(
                        "El turno seleccionado "
                                + "ya no existe."
                );

                refrescarLista(usuario);
                return;
            }

            if (!perteneceAlUsuario(
                    turno,
                    usuario)) {

                mostrarError(
                        "No puede modificar un turno "
                                + "que pertenece a otro usuario."
                );

                refrescarLista(usuario);
                return;
            }

            if (!puedeModificar(
                    turno.getEstado())) {

                mostrarError(
                        obtenerMensajeNoModificable(
                                turno.getEstado()
                        )
                );

                return;
            }

            panelManager.mostrarPanelFormularioTurno(
                    turno,
                    usuario
            );

        } catch (IllegalArgumentException exception) {
            mostrarError(exception.getMessage());

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo recuperar el turno."
            );
        }
    }

    private void cancelarTurno(
            String usuario) {

        Long turnoId =
                obtenerIdSeleccionado();

        if (turnoId == null) {
            return;
        }

        try {
            Turno turno =
                    turnoService.buscar(turnoId);

            if (turno == null) {
                mostrarError(
                        "El turno seleccionado "
                                + "ya no existe."
                );

                refrescarLista(usuario);
                return;
            }

            if (!perteneceAlUsuario(
                    turno,
                    usuario)) {

                mostrarError(
                        "No puede cancelar un turno "
                                + "que pertenece a otro usuario."
                );

                refrescarLista(usuario);
                return;
            }

            if (!puedeCancelar(
                    turno.getEstado())) {

                mostrarError(
                        obtenerMensajeNoCancelable(
                                turno.getEstado()
                        )
                );

                return;
            }

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            this,
                            "¿Desea cancelar el turno "
                                    + "seleccionado?",
                            "Confirmar cancelación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (respuesta
                    != JOptionPane.YES_OPTION) {

                return;
            }

            turnoService.cambiarEstado(
                    turnoId,
                    EstadoTurno.CANCELADO
            );

            JOptionPane.showMessageDialog(
                    this,
                    "El turno se canceló correctamente.",
                    "Turno cancelado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refrescarLista(usuario);

        } catch (IllegalArgumentException exception) {
            mostrarError(exception.getMessage());

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo cancelar el turno."
            );
        }
    }

    private boolean puedeModificar(
            EstadoTurno estado) {

        return estado == null
                || estado == EstadoTurno.PENDIENTE;
    }

    private boolean puedeCancelar(
            EstadoTurno estado) {

        return estado == null
                || estado == EstadoTurno.PENDIENTE
                || estado == EstadoTurno.CONFIRMADO;
    }

    private String obtenerMensajeNoModificable(
            EstadoTurno estado) {

        if (estado == EstadoTurno.CONFIRMADO) {
            return "El turno está confirmado. "
                    + "Puede cancelarlo, pero no modificar "
                    + "la fecha o el horario.";
        }

        if (estado == EstadoTurno.CANCELADO) {
            return "No se puede modificar "
                    + "un turno cancelado.";
        }

        if (estado == EstadoTurno.ATENDIDO) {
            return "No se puede modificar "
                    + "un turno atendido.";
        }

        if (estado == EstadoTurno.AUSENTE) {
            return "No se puede modificar un turno "
                    + "marcado como ausente.";
        }

        return "El turno no puede modificarse "
                + "en el estado actual.";
    }

    private String obtenerMensajeNoCancelable(
            EstadoTurno estado) {

        if (estado == EstadoTurno.CANCELADO) {
            return "El turno ya está cancelado.";
        }

        if (estado == EstadoTurno.ATENDIDO) {
            return "No se puede cancelar "
                    + "un turno atendido.";
        }

        if (estado == EstadoTurno.AUSENTE) {
            return "No se puede cancelar un turno "
                    + "marcado como ausente.";
        }

        return "El turno no puede cancelarse "
                + "en el estado actual.";
    }

    private Long obtenerIdSeleccionado() {
        int filaVista =
                tabla.getSelectedRow();

        if (filaVista < 0) {
            mostrarError(
                    "Debe seleccionar un turno."
            );

            return null;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(
                        filaVista
                );

        Object valorId =
                contenidoTabla.getValueAt(
                        filaModelo,
                        0
                );

        if (!(valorId instanceof Number)) {
            mostrarError(
                    "No se pudo identificar el turno."
            );

            return null;
        }

        return ((Number) valorId).longValue();
    }

    private void refrescarLista(
            String usuario) {

        panelManager.mostrarPanelListaTurnosUsuario(
                usuario
        );
    }

    private void mostrarError(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Operación incorrecta",
                JOptionPane.ERROR_MESSAGE
        );
    }
}