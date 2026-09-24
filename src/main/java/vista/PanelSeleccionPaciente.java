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

import negocio.Paciente;
import negocio.Turno;
import servicio.PacienteService;

public class PanelSeleccionPaciente extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable tabla;
    private DefaultTableModel contenidoTabla;
    private JScrollPane scrollPane;

    private JButton btnContinuar;
    private JButton btnCancelar;

    private final PanelManager panelManager;
    private final PacienteService pacienteService;

    public PanelSeleccionPaciente(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.pacienteService =
                new PacienteService();
    }

    public void armarPanelSeleccionPaciente(
            Turno turno,
            String usuario) {

        removeAll();

        setLayout(new BorderLayout());

        setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        if (turno == null) {
            mostrarError(
                    "No se recibió la información "
                            + "del turno."
            );

            regresarAlListado(usuario);
            return;
        }

        JPanel encabezado =
                crearEncabezado(turno);

        JPanel contenido =
                crearContenidoTabla();

        JPanel botonera =
                crearBotonera();

        add(
                encabezado,
                BorderLayout.NORTH
        );

        add(
                contenido,
                BorderLayout.CENTER
        );

        add(
                botonera,
                BorderLayout.SOUTH
        );

        cargarPacientes();

        configurarEventos(
                turno,
                usuario
        );

        revalidate();
        repaint();
        setVisible(true);

        SwingUtilities.invokeLater(
                () -> tabla.requestFocusInWindow()
        );
    }

    private JPanel crearEncabezado(
            Turno turno) {

        JPanel encabezado =
                new JPanel();

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

        JLabel titulo =
                new JLabel(
                        "Seleccione un paciente"
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

        JLabel subtitulo =
                new JLabel(
                        "Paso 2 de 3: seleccione "
                                + "la ficha correspondiente"
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

        JLabel odontologo =
                new JLabel(
                        "Odontólogo seleccionado: "
                                + obtenerNombreOdontologo(
                                        turno
                                )
                );

        odontologo.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        odontologo.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        odontologo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel ayuda =
                new JLabel(
                        "Seleccione una fila y presione "
                                + "Continuar. También puede "
                                + "hacer doble clic o pulsar Enter."
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
                Box.createVerticalStrut(8)
        );

        encabezado.add(odontologo);

        encabezado.add(
                Box.createVerticalStrut(5)
        );

        encabezado.add(ayuda);

        return encabezado;
    }

    private String obtenerNombreOdontologo(
            Turno turno) {

        if (turno.getNomOdontologo() == null
                || turno.getNomOdontologo()
                        .trim()
                        .isEmpty()) {

            return "sin identificar";
        }

        return turno.getNomOdontologo()
                .trim();
    }

    private JPanel crearContenidoTabla() {
        JPanel contenedor =
                new JPanel(
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
        contenidoTabla =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Nombre",
                                "Apellido",
                                "DNI",
                                "Domicilio",
                                "Edad"
                        },
                        0
                ) {

                    private static final long serialVersionUID =
                            1L;

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
                                || columna == 5) {

                            return Number.class;
                        }

                        return String.class;
                    }
                };

        tabla =
                new JTable(
                        contenidoTabla
                );

        EstilosUI.prepararTabla(
                tabla
        );

        tabla.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        tabla.setFillsViewportHeight(
                true
        );

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        configurarAlineacionTabla();
        configurarOrdenamiento();

        scrollPane =
                new JScrollPane(
                        tabla
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        EstilosUI.BORDE
                )
        );

        scrollPane.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        scrollPane.getViewport()
                .setBackground(
                        EstilosUI.FONDO_SECUNDARIO
                );

        scrollPane.getVerticalScrollBar()
                .setBackground(
                        EstilosUI.FONDO_SECUNDARIO
                );

        scrollPane.getHorizontalScrollBar()
                .setBackground(
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

        tabla.setRowSorter(
                ordenador
        );
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
        if (tabla.getColumnCount() < 6) {
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
                .setPreferredWidth(150);

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(150);

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(105);

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(300);

        tabla.getColumnModel()
                .getColumn(5)
                .setMinWidth(55);

        tabla.getColumnModel()
                .getColumn(5)
                .setMaxWidth(85);

        tabla.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(65);
    }

    private void cargarPacientes() {
        try {
            List<Paciente> pacientes =
                    pacienteService.listar();

            for (Paciente paciente : pacientes) {
                Object[] fila = {
                        paciente.getId(),
                        paciente.getNombre(),
                        paciente.getApellido(),
                        Integer.toString(
                                paciente.getDni()
                        ),
                        paciente.getDomicilio(),
                        paciente.getEdad()
                };

                contenidoTabla.addRow(
                        fila
                );
            }

            boolean sinPacientes =
                    pacientes.isEmpty();

            btnContinuar.setEnabled(
                    !sinPacientes
            );

            if (sinPacientes) {
                SwingUtilities.invokeLater(
                        () ->
                                JOptionPane.showMessageDialog(
                                        this,
                                        "No hay pacientes activos "
                                                + "disponibles para seleccionar.",
                                        "Sin pacientes",
                                        JOptionPane.INFORMATION_MESSAGE
                                )
                );
            }

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            btnContinuar.setEnabled(false);

            SwingUtilities.invokeLater(
                    () ->
                            mostrarError(
                                    "No se pudieron cargar "
                                            + "los pacientes."
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

        botonera.add(
                btnCancelar
        );

        botonera.add(
                btnContinuar
        );

        return botonera;
    }

    private void configurarEventos(
            Turno turno,
            String usuario) {

        btnContinuar.addActionListener(
                evento ->
                        seleccionarPaciente(
                                turno,
                                usuario
                        )
        );

        btnCancelar.addActionListener(
                evento ->
                        cancelar(usuario)
        );

        tabla.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent evento) {

                        if (evento.getClickCount() == 2) {
                            seleccionarPaciente(
                                    turno,
                                    usuario
                            );
                        }
                    }
                }
        );

        tabla.getInputMap(
                JComponent.WHEN_FOCUSED
        ).put(
                KeyStroke.getKeyStroke(
                        "ENTER"
                ),
                "seleccionarPaciente"
        );

        tabla.getActionMap().put(
                "seleccionarPaciente",
                new AbstractAction() {

                    private static final long serialVersionUID =
                            1L;

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent evento) {

                        seleccionarPaciente(
                                turno,
                                usuario
                        );
                    }
                }
        );
    }

    private void seleccionarPaciente(
            Turno turno,
            String usuario) {

        Long pacienteId =
                obtenerIdSeleccionado();

        if (pacienteId == null) {
            return;
        }

        try {
            Paciente paciente =
                    pacienteService.buscar(
                            pacienteId
                    );

            if (paciente == null) {
                mostrarError(
                        "El paciente seleccionado "
                                + "ya no existe "
                                + "o está inactivo."
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

            panelManager
                    .mostrarPanelFormularioTurno(
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
                            + "la información del paciente."
            );
        }
    }

    private Long obtenerIdSeleccionado() {
        int filaVista =
                tabla.getSelectedRow();

        if (filaVista < 0) {
            mostrarError(
                    "Debe seleccionar un paciente."
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
                            + "al paciente seleccionado."
            );

            return null;
        }

        return ((Number) valorId)
                .longValue();
    }

    private void cancelar(
            String usuario) {

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea cancelar la creación "
                                + "del turno?\n\n"
                                + "Se perderá la selección "
                                + "del odontólogo.",
                        "Cancelar reserva",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta
                != JOptionPane.YES_OPTION) {

            return;
        }

        regresarAlListado(
                usuario
        );
    }

    private void regresarAlListado(
            String usuario) {

        if ("admin".equalsIgnoreCase(
                usuario)) {

            panelManager
                    .mostrarPanelListaTurnosAdmin(
                            usuario
                    );

        } else {
            panelManager
                    .mostrarPanelListaTurnosUsuario(
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