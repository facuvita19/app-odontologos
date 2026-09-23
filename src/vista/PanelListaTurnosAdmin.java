package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import negocio.EstadoTurno;
import negocio.Turno;
import servicio.TurnoService;

public class PanelListaTurnosAdmin extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable tabla;
    private DefaultTableModel contenidoTabla;
    private JScrollPane scrollPane;
    private TableRowSorter<DefaultTableModel> ordenador;

    private JTextField txtBuscar;
    private JComboBox<String> comboFiltroEstado;
    private JButton btnLimpiarFiltros;
    private JLabel lblContadorResultados;

    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnCancelarTurno;
    private JButton btnCambiarEstado;
    private JButton btnVolver;

    private JComboBox<EstadoTurno> comboEstado;

    private final PanelManager panelManager;
    private final TurnoService turnoService;

    public PanelListaTurnosAdmin(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.turnoService = new TurnoService();
    }

    public void armarPanelListaTurnos(
            String usuario) {

        removeAll();
        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        crearTabla();
        cargarTurnos();

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        configurarEventos(usuario);
        actualizarContadorResultados();

        revalidate();
        repaint();
        setVisible(true);

        SwingUtilities.invokeLater(
                () -> txtBuscar.requestFocusInWindow()
        );
    }

    private JPanel crearEncabezado() {
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
                        18,
                        22,
                        12,
                        22
                )
        );

        JLabel titulo = new JLabel(
                "Administración de turnos"
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
                "Consulte reservas, modifique horarios "
                        + "y gestione el estado de atención"
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

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(6));
        encabezado.add(subtitulo);

        return encabezado;
    }

    private JPanel crearPanelCentral() {
        JPanel panelCentral = new JPanel(
                new BorderLayout()
        );

        panelCentral.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        panelCentral.add(
                crearPanelFiltros(),
                BorderLayout.NORTH
        );

        panelCentral.add(
                crearContenidoTabla(),
                BorderLayout.CENTER
        );

        return panelCentral;
    }

    private JPanel crearPanelFiltros() {
        JPanel contenedor = new JPanel(
                new BorderLayout(15, 8)
        );

        contenedor.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        contenedor.setBorder(
                new EmptyBorder(0, 20, 10, 20)
        );

        JPanel controles = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        10,
                        0
                )
        );

        controles.setOpaque(false);

        JLabel lblBuscar =
                EstilosUI.crearEtiqueta("Buscar:");

        txtBuscar = new JTextField(18);
        EstilosUI.prepararCampo(txtBuscar);
        txtBuscar.setToolTipText(
                "Buscar por paciente u odontólogo"
        );

        JLabel lblEstado =
                EstilosUI.crearEtiqueta("Estado:");

        comboFiltroEstado = new JComboBox<>(
                new String[]{
                        "Todos",
                        "Pendiente",
                        "Confirmado",
                        "Atendido",
                        "Cancelado",
                        "Ausente"
                }
        );

        prepararComboFiltroEstado();

        btnLimpiarFiltros =
                EstilosUI.crearBotonSecundario(
                        "Limpiar filtros"
                );

        controles.add(lblBuscar);
        controles.add(txtBuscar);
        controles.add(lblEstado);
        controles.add(comboFiltroEstado);
        controles.add(btnLimpiarFiltros);

        lblContadorResultados = new JLabel(
                "Mostrando 0 de 0 turnos"
        );

        lblContadorResultados.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        lblContadorResultados.setForeground(
                EstilosUI.TEXTO_SECUNDARIO
        );

        lblContadorResultados.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        contenedor.add(
                controles,
                BorderLayout.WEST
        );

        contenedor.add(
                lblContadorResultados,
                BorderLayout.EAST
        );

        return contenedor;
    }

    private void prepararComboFiltroEstado() {
        comboFiltroEstado.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        comboFiltroEstado.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        comboFiltroEstado.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        comboFiltroEstado.setPreferredSize(
                new Dimension(130, 36)
        );

        comboFiltroEstado.setRenderer(
                crearRendererCombo()
        );
    }

    private DefaultListCellRenderer crearRendererCombo() {
        return new DefaultListCellRenderer() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean tieneFoco) {

                super.getListCellRendererComponent(
                        lista,
                        valor,
                        indice,
                        seleccionado,
                        tieneFoco
                );

                if (seleccionado) {
                    setBackground(
                            EstilosUI.COLOR_PRIMARIO
                    );
                    setForeground(Color.WHITE);
                } else {
                    setBackground(
                            EstilosUI.FONDO_SECUNDARIO
                    );
                    setForeground(
                            EstilosUI.TEXTO_PRINCIPAL
                    );
                }

                setBorder(
                        new EmptyBorder(5, 8, 5, 8)
                );

                return this;
            }
        };
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

            @Override
            public Class<?> getColumnClass(
                    int columna) {

                if (columna == 0
                        || columna == 4
                        || columna == 5
                        || columna == 6) {

                    return Number.class;
                }

                if (columna == 3) {
                    return EstadoTurno.class;
                }

                return String.class;
            }
        };

        tabla = new JTable(contenidoTabla);
        EstilosUI.prepararTabla(tabla);
        configurarAlineacionTabla();

        tabla.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        tabla.setFillsViewportHeight(true);

        ordenador = new TableRowSorter<>(
                contenidoTabla
        );

        tabla.setRowSorter(ordenador);

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
                    .setCellRenderer(celdasCentradas);
        }
    }

    private JPanel crearContenidoTabla() {
        JPanel contenedor = new JPanel(
                new BorderLayout()
        );

        contenedor.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        contenedor.setBorder(
                new EmptyBorder(0, 20, 0, 20)
        );

        contenedor.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return contenedor;
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

    private void cargarTurnos() {
        List<Turno> turnos = turnoService.listar();

        for (Turno turno : turnos) {
            EstadoTurno estado = turno.getEstado();

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

    private JPanel crearPanelInferior() {
        JPanel contenedor = new JPanel(
                new BorderLayout()
        );

        contenedor.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        contenedor.setBorder(
                new EmptyBorder(10, 20, 10, 20)
        );

        contenedor.add(
                crearAccionesPrincipales(),
                BorderLayout.WEST
        );

        contenedor.add(
                crearAccionesEstado(),
                BorderLayout.EAST
        );

        return contenedor;
    }

    private JPanel crearAccionesPrincipales() {
        JPanel panel = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        8,
                        0
                )
        );

        panel.setOpaque(false);

        btnCrear = EstilosUI.crearBotonPrimario(
                "Nuevo turno"
        );

        btnModificar = EstilosUI.crearBotonSecundario(
                "Modificar"
        );

        btnCancelarTurno = EstilosUI.crearBotonPeligro(
                "Cancelar turno"
        );

        btnVolver = EstilosUI.crearBotonSecundario(
                "Volver"
        );

        panel.add(btnCrear);
        panel.add(btnModificar);
        panel.add(btnCancelarTurno);
        panel.add(btnVolver);

        return panel;
    }

    private JPanel crearAccionesEstado() {
        JPanel panel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        0
                )
        );

        panel.setOpaque(false);

        JLabel lblEstado =
                EstilosUI.crearEtiqueta(
                        "Nuevo estado:"
                );

        comboEstado = new JComboBox<>(
                EstadoTurno.values()
        );

        prepararComboEstado();

        btnCambiarEstado =
                EstilosUI.crearBotonPrimario(
                        "Cambiar estado"
                );

        panel.add(lblEstado);
        panel.add(comboEstado);
        panel.add(btnCambiarEstado);

        return panel;
    }

    private void prepararComboEstado() {
        comboEstado.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        comboEstado.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        comboEstado.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        comboEstado.setPreferredSize(
                new Dimension(135, 36)
        );

        comboEstado.setToolTipText(
                "Seleccione el nuevo estado del turno"
        );

        comboEstado.setRenderer(
                crearRendererCombo()
        );
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

        btnCambiarEstado.addActionListener(
                evento -> cambiarEstado(usuario)
        );

        btnVolver.addActionListener(
                evento -> regresar(usuario)
        );

        tabla.getSelectionModel()
                .addListSelectionListener(
                        evento -> {
                            if (!evento.getValueIsAdjusting()) {
                                sincronizarComboConFila();
                            }
                        }
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

        tabla.getInputMap(
                JComponent.WHEN_FOCUSED
        ).put(
                KeyStroke.getKeyStroke("ENTER"),
                "modificarTurno"
        );

        tabla.getActionMap().put(
                "modificarTurno",
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent evento) {

                        modificarTurno(usuario);
                    }
                }
        );

        configurarFiltros();
    }

    private void configurarFiltros() {
        txtBuscar.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    DocumentEvent evento) {

                                aplicarFiltros();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent evento) {

                                aplicarFiltros();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent evento) {

                                aplicarFiltros();
                            }
                        }
                );

        comboFiltroEstado.addActionListener(
                evento -> aplicarFiltros()
        );

        btnLimpiarFiltros.addActionListener(
                evento -> limpiarFiltros()
        );
    }

    private void aplicarFiltros() {
        String textoBusqueda = txtBuscar
                .getText()
                .trim();

        String estadoSeleccionado =
                (String) comboFiltroEstado
                        .getSelectedItem();

        List<RowFilter<DefaultTableModel, Object>> filtros =
                new ArrayList<>();

        if (!textoBusqueda.isEmpty()) {
            String expresion =
                    "(?i)" + Pattern.quote(textoBusqueda);

            filtros.add(
                    RowFilter.regexFilter(
                            expresion,
                            1,
                            2
                    )
            );
        }

        if (estadoSeleccionado != null
                && !"Todos".equals(estadoSeleccionado)) {

            EstadoTurno estadoFiltro =
                    convertirEstadoFiltro(
                            estadoSeleccionado
                    );

            if (estadoFiltro != null) {
                filtros.add(
                        new RowFilter
                                <DefaultTableModel, Object>() {

                            @Override
                            public boolean include(
                                    Entry
                                            <? extends DefaultTableModel,
                                            ? extends Object> entrada) {

                                Object valorEstado =
                                        entrada.getValue(3);

                                return valorEstado
                                        == estadoFiltro;
                            }
                        }
                );
            }
        }

        if (filtros.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(
                    RowFilter.andFilter(filtros)
            );
        }

        tabla.clearSelection();
        actualizarContadorResultados();
    }

    private EstadoTurno convertirEstadoFiltro(
            String valor) {

        if (valor == null) {
            return null;
        }

        switch (valor) {
            case "Pendiente":
                return EstadoTurno.PENDIENTE;
            case "Confirmado":
                return EstadoTurno.CONFIRMADO;
            case "Atendido":
                return EstadoTurno.ATENDIDO;
            case "Cancelado":
                return EstadoTurno.CANCELADO;
            case "Ausente":
                return EstadoTurno.AUSENTE;
            default:
                return null;
        }
    }

    private void limpiarFiltros() {
        txtBuscar.setText("");
        comboFiltroEstado.setSelectedItem("Todos");
        ordenador.setRowFilter(null);
        tabla.clearSelection();
        actualizarContadorResultados();
        txtBuscar.requestFocusInWindow();
    }

    private void actualizarContadorResultados() {
        if (lblContadorResultados == null
                || tabla == null
                || contenidoTabla == null) {

            return;
        }

        int cantidadVisible = tabla.getRowCount();
        int cantidadTotal = contenidoTabla.getRowCount();

        String palabra =
                cantidadVisible == 1
                        ? "turno"
                        : "turnos";

        lblContadorResultados.setText(
                "Mostrando "
                        + cantidadVisible
                        + " de "
                        + cantidadTotal
                        + " "
                        + palabra
        );
    }

    private void crearTurno(
            String usuario) {

        JOptionPane.showMessageDialog(
                this,
                "Seleccione primero el odontólogo "
                        + "y luego el paciente.",
                "Crear turno",
                JOptionPane.INFORMATION_MESSAGE
        );

        panelManager.mostrarPanelSeleccionOdontologo(
                usuario
        );
    }

    private void modificarTurno(
            String usuario) {

        Long turnoId = obtenerIdSeleccionado();

        if (turnoId == null) {
            return;
        }

        try {
            Turno turno = turnoService.buscar(turnoId);

            if (turno == null) {
                mostrarError(
                        "El turno seleccionado ya no existe."
                );

                refrescarLista(usuario);
                return;
            }

            if (esEstadoFinal(turno.getEstado())) {
                mostrarError(
                        "No se puede modificar la fecha "
                                + "o el horario de un turno "
                                + "finalizado."
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

        Long turnoId = obtenerIdSeleccionado();

        if (turnoId == null) {
            return;
        }

        try {
            Turno turno = turnoService.buscar(turnoId);

            if (turno == null) {
                mostrarError(
                        "El turno seleccionado ya no existe."
                );

                refrescarLista(usuario);
                return;
            }

            if (!puedeCancelar(turno.getEstado())) {
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

            if (respuesta != JOptionPane.YES_OPTION) {
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

    private void cambiarEstado(
            String usuario) {

        Long turnoId = obtenerIdSeleccionado();

        if (turnoId == null) {
            return;
        }

        EstadoTurno nuevoEstado =
                (EstadoTurno) comboEstado
                        .getSelectedItem();

        if (nuevoEstado == null) {
            mostrarError(
                    "Debe seleccionar un estado."
            );

            return;
        }

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea cambiar el estado "
                                + "del turno a "
                                + nuevoEstado
                                + "?",
                        "Confirmar cambio de estado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            turnoService.cambiarEstado(
                    turnoId,
                    nuevoEstado
            );

            JOptionPane.showMessageDialog(
                    this,
                    "El estado del turno se actualizó "
                            + "correctamente.",
                    "Estado actualizado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refrescarLista(usuario);

        } catch (IllegalArgumentException exception) {
            mostrarError(exception.getMessage());

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo actualizar "
                            + "el estado del turno."
            );
        }
    }

    private Long obtenerIdSeleccionado() {
        int filaVista = tabla.getSelectedRow();

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

    private void sincronizarComboConFila() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            return;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(
                        filaVista
                );

        Object valorEstado =
                contenidoTabla.getValueAt(
                        filaModelo,
                        3
                );

        if (valorEstado instanceof EstadoTurno) {
            comboEstado.setSelectedItem(
                    valorEstado
            );
            return;
        }

        if (valorEstado != null) {
            try {
                EstadoTurno estado =
                        EstadoTurno.valueOf(
                                valorEstado
                                        .toString()
                                        .trim()
                                        .toUpperCase()
                        );

                comboEstado.setSelectedItem(estado);

            } catch (IllegalArgumentException exception) {
                comboEstado.setSelectedItem(
                        EstadoTurno.PENDIENTE
                );
            }
        }
    }

    private boolean puedeCancelar(
            EstadoTurno estado) {

        return estado == null
                || estado == EstadoTurno.PENDIENTE
                || estado == EstadoTurno.CONFIRMADO;
    }

    private String obtenerMensajeNoCancelable(
            EstadoTurno estado) {

        if (estado == EstadoTurno.CANCELADO) {
            return "El turno ya está cancelado.";
        }

        if (estado == EstadoTurno.ATENDIDO) {
            return "No se puede cancelar un turno atendido.";
        }

        if (estado == EstadoTurno.AUSENTE) {
            return "No se puede cancelar un turno "
                    + "marcado como ausente.";
        }

        return "El turno no puede cancelarse "
                + "en el estado actual.";
    }

    private boolean esEstadoFinal(
            EstadoTurno estado) {

        return estado == EstadoTurno.ATENDIDO
                || estado == EstadoTurno.CANCELADO
                || estado == EstadoTurno.AUSENTE;
    }

    private void refrescarLista(
            String usuario) {

        panelManager.mostrarPanelListaTurnosAdmin(
                usuario
        );
    }

    private void regresar(
            String usuario) {

        panelManager.mostrarPanelPrincipalAdmin(
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
