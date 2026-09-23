package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import negocio.Odontologo;
import negocio.Paciente;
import negocio.Turno;
import servicio.OdontologoService;
import servicio.PacienteService;
import servicio.UsuarioService;

public class PanelSeleccionOdontologo extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable tabla;
    private DefaultTableModel contenidoTabla;
    private JScrollPane scrollPane;

    private JButton btnContinuar;
    private JButton btnCancelar;

    private final PanelManager panelManager;
    private final OdontologoService odontologoService;
    private final UsuarioService usuarioService;
    private final PacienteService pacienteService;

    public PanelSeleccionOdontologo(
            PanelManager panelManager) {

        this.panelManager = panelManager;

        this.odontologoService =
                new OdontologoService();

        this.usuarioService =
                new UsuarioService();

        this.pacienteService =
                new PacienteService();
    }

    public void armarPanelSeleccionOdontologo(
            String usuario) {

        removeAll();

        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        add(
                crearEncabezado(usuario),
                BorderLayout.NORTH
        );

        add(
                crearContenidoTabla(),
                BorderLayout.CENTER
        );

        add(
                crearBotonera(),
                BorderLayout.SOUTH
        );

        cargarOdontologos();
        configurarEventos(usuario);

        revalidate();
        repaint();
        setVisible(true);

        SwingUtilities.invokeLater(
                () -> tabla.requestFocusInWindow()
        );
    }

    private JPanel crearEncabezado(
            String usuario) {

        JPanel encabezado = new JPanel();

        encabezado.setLayout(
                new BoxLayout(
                        encabezado,
                        BoxLayout.Y_AXIS
                )
        );

        encabezado.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        encabezado.setBorder(
                new EmptyBorder(
                        22,
                        30,
                        15,
                        30
                )
        );

        JLabel titulo = new JLabel(
                "Seleccione un odontólogo"
        );

        titulo.setFont(
                EstilosUI.FUENTE_TITULO
        );

        titulo.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        titulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel subtitulo = new JLabel(
                obtenerSubtitulo(usuario)
        );

        subtitulo.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        subtitulo.setForeground(
                EstilosUI.TEXTO_SECUNDARIO
        );

        subtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel ayuda = new JLabel(
                "Seleccione una fila y presione Continuar. "
                        + "También puede hacer doble clic."
        );

        ayuda.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.ITALIC,
                        12
                )
        );

        ayuda.setForeground(
                EstilosUI.TEXTO_SECUNDARIO
        );

        ayuda.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        encabezado.add(titulo);

        encabezado.add(
                Box.createVerticalStrut(6)
        );

        encabezado.add(subtitulo);

        encabezado.add(
                Box.createVerticalStrut(5)
        );

        encabezado.add(ayuda);

        return encabezado;
    }

    private String obtenerSubtitulo(
            String usuario) {

        if ("admin".equalsIgnoreCase(usuario)) {
            return "Paso 1 de 3: seleccione al profesional "
                    + "que atenderá el turno";
        }

        return "Paso 1 de 2: seleccione al profesional "
                + "con el que desea atenderse";
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
                        30,
                        10,
                        30
                )
        );

        crearTabla();

        contenedor.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return contenedor;
    }

    private void crearTabla() {
        contenidoTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Nombre",
                        "Apellido",
                        "Matrícula",
                        "Edad"
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

            @Override
            public Class<?> getColumnClass(
                    int columna) {

                if (columna == 0
                        || columna == 3
                        || columna == 4) {

                    return Number.class;
                }

                return String.class;
            }
        };

        tabla = new JTable(contenidoTabla);

        EstilosUI.prepararTabla(tabla);

        tabla.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        tabla.setFillsViewportHeight(true);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        configurarAlineacionTabla();
        configurarOrdenamiento();

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

        SwingUtilities.invokeLater(
                this::definirAnchoColumnas
        );
    }

    private void configurarOrdenamiento() {
        TableRowSorter<DefaultTableModel> ordenador =
                new TableRowSorter<>(
                        contenidoTabla
                );

        tabla.setRowSorter(ordenador);
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
        if (tabla.getColumnCount() < 5) {
            return;
        }

        tabla.getColumnModel()
                .getColumn(0)
                .setMinWidth(45);

        tabla.getColumnModel()
                .getColumn(0)
                .setMaxWidth(70);

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(55);

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(200);

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(200);

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(130);

        tabla.getColumnModel()
                .getColumn(4)
                .setMinWidth(60);

        tabla.getColumnModel()
                .getColumn(4)
                .setMaxWidth(90);

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(70);
    }

    private void cargarOdontologos() {
        List<Odontologo> odontologos =
                odontologoService.listar();

        for (Odontologo odontologo : odontologos) {
            Object[] fila = {
                    odontologo.getId(),
                    odontologo.getNombre(),
                    odontologo.getApellido(),
                    odontologo.getMatricula(),
                    odontologo.getEdad()
            };

            contenidoTabla.addRow(fila);
        }

        if (odontologos.isEmpty()) {
            SwingUtilities.invokeLater(
                    () -> JOptionPane.showMessageDialog(
                            this,
                            "No hay odontólogos activos "
                                    + "disponibles para seleccionar.",
                            "Sin odontólogos",
                            JOptionPane.INFORMATION_MESSAGE
                    )
            );
        }
    }

    private JPanel crearBotonera() {
        JPanel botonera =
                EstilosUI.crearPanelBotonera();

        botonera.setBorder(
                new EmptyBorder(
                        10,
                        30,
                        18,
                        30
                )
        );

        btnCancelar =
                EstilosUI.crearBotonSecundario(
                        "Cancelar"
                );

        btnContinuar =
                EstilosUI.crearBotonPrimario(
                        "Continuar"
                );

        botonera.add(btnCancelar);
        botonera.add(btnContinuar);

        return botonera;
    }

    private void configurarEventos(
            String usuario) {

        btnContinuar.addActionListener(
                evento -> seleccionarOdontologo(
                        usuario
                )
        );

        btnCancelar.addActionListener(
                evento -> cancelar(usuario)
        );

        tabla.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent evento) {

                        if (evento.getClickCount() == 2) {
                            seleccionarOdontologo(
                                    usuario
                            );
                        }
                    }
                }
        );

        tabla.getInputMap(
                JComponent.WHEN_FOCUSED
        ).put(
                KeyStroke.getKeyStroke("ENTER"),
                "seleccionarOdontologo"
        );

        tabla.getActionMap().put(
                "seleccionarOdontologo",
                new AbstractAction() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent evento) {

                        seleccionarOdontologo(
                                usuario
                        );
                    }
                }
        );
    }

    private void seleccionarOdontologo(
            String usuario) {

        Long odontologoId =
                obtenerIdSeleccionado();

        if (odontologoId == null) {
            return;
        }

        try {
            Odontologo odontologo =
                    odontologoService.buscar(
                            odontologoId
                    );

            if (odontologo == null) {
                mostrarError(
                        "El odontólogo seleccionado "
                                + "ya no existe o está inactivo."
                );

                return;
            }

            Turno turno = new Turno();

            turno.setOdontologoId(
                    odontologo.getId()
            );

            turno.setNomOdontologo(
                    odontologo.getNombre()
                            + " "
                            + odontologo.getApellido()
            );

            continuarReserva(
                    turno,
                    usuario
            );

        } catch (IllegalArgumentException exception) {
            mostrarError(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo recuperar "
                            + "la información del odontólogo."
            );
        }
    }

    private Long obtenerIdSeleccionado() {
        int filaVista =
                tabla.getSelectedRow();

        if (filaVista < 0) {
            mostrarError(
                    "Debe seleccionar un odontólogo."
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
                    "No se pudo identificar "
                            + "al odontólogo seleccionado."
            );

            return null;
        }

        return ((Number) valorId).longValue();
    }

    private void continuarReserva(
            Turno turno,
            String usuario) {

        if ("admin".equalsIgnoreCase(usuario)) {
            panelManager.mostrarPanelSeleccionPaciente(
                    turno,
                    usuario
            );

            return;
        }

        asignarPacienteDelUsuario(
                turno,
                usuario
        );
    }

    private void asignarPacienteDelUsuario(
            Turno turno,
            String usuario) {

        try {
            Long pacienteId =
                    usuarioService.buscarPacienteId(
                            usuario
                    );

            if (pacienteId == null) {
                int respuesta =
                        JOptionPane.showConfirmDialog(
                                this,
                                "Antes de reservar un turno "
                                        + "debe completar sus datos.\n"
                                        + "¿Desea hacerlo ahora?",
                                "Datos requeridos",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                if (respuesta
                        == JOptionPane.YES_OPTION) {

                    panelManager
                            .mostrarPanelFormularioPaciente(
                                    usuario
                            );
                }

                return;
            }

            Paciente paciente =
                    pacienteService.buscar(
                            pacienteId
                    );

            if (paciente == null) {
                mostrarError(
                        "La ficha vinculada no existe "
                                + "o se encuentra inactiva."
                );

                return;
            }

            turno.setPacienteId(
                    paciente.getId()
            );

            turno.setNomPaciente(
                    paciente.getNombre()
                            + " "
                            + paciente.getApellido()
            );

            panelManager.mostrarPanelFormularioTurno(
                    turno,
                    usuario
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudieron recuperar "
                            + "sus datos personales."
            );
        }
    }

    private void cancelar(
            String usuario) {

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea cancelar la creación "
                                + "del turno?",
                        "Cancelar reserva",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta
                != JOptionPane.YES_OPTION) {

            return;
        }

        if ("admin".equalsIgnoreCase(usuario)) {
            panelManager.mostrarPanelListaTurnosAdmin(
                    usuario
            );

        } else {
            panelManager.mostrarPanelListaTurnosUsuario(
                    usuario
            );
        }
    }

    private void mostrarError(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Selección incorrecta",
                JOptionPane.ERROR_MESSAGE
        );
    }
}