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
    private JButton btnVerDetalle;
    private JButton btnModificar;
    private JButton btnCancelarTurno;
    private JButton btnCambiarEstado;
    private JButton btnVolver;
    private JComboBox<EstadoTurno> comboEstado;

    private final PanelManager panelManager;
    private final TurnoService turnoService;

    public PanelListaTurnosAdmin(PanelManager panelManager) {
        this.panelManager = panelManager;
        this.turnoService = new TurnoService();
    }

    public void armarPanelListaTurnos(String usuario) {
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

        SwingUtilities.invokeLater(() -> txtBuscar.requestFocusInWindow());
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(18, 22, 12, 22));

        JLabel titulo = new JLabel("Administración de turnos");
        titulo.setFont(EstilosUI.FUENTE_TITULO);
        titulo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Consulte detalles, modifique horarios y gestione estados"
        );
        subtitulo.setFont(EstilosUI.FUENTE_NORMAL);
        subtitulo.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitulo);
        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.add(crearPanelFiltros(), BorderLayout.NORTH);
        panel.add(crearContenidoTabla(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelFiltros() {
        JPanel contenedor = new JPanel(new BorderLayout(15, 8));
        contenedor.setBackground(EstilosUI.FONDO_PRINCIPAL);
        contenedor.setBorder(new EmptyBorder(0, 20, 10, 20));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controles.setOpaque(false);

        JLabel lblBuscar = EstilosUI.crearEtiqueta("Buscar:");
        txtBuscar = new JTextField(18);
        EstilosUI.prepararCampo(txtBuscar);
        txtBuscar.setToolTipText("Buscar por paciente u odontólogo");

        JLabel lblEstado = EstilosUI.crearEtiqueta("Estado:");
        comboFiltroEstado = new JComboBox<>(new String[]{
                "Todos", "Pendiente", "Confirmado", "Atendido",
                "Cancelado", "Ausente"
        });
        prepararCombo(comboFiltroEstado, 130);

        btnLimpiarFiltros = EstilosUI.crearBotonSecundario("Limpiar filtros");
        controles.add(lblBuscar);
        controles.add(txtBuscar);
        controles.add(lblEstado);
        controles.add(comboFiltroEstado);
        controles.add(btnLimpiarFiltros);

        lblContadorResultados = new JLabel("Mostrando 0 de 0 turnos");
        lblContadorResultados.setFont(EstilosUI.FUENTE_NORMAL);
        lblContadorResultados.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        lblContadorResultados.setHorizontalAlignment(SwingConstants.RIGHT);

        contenedor.add(controles, BorderLayout.WEST);
        contenedor.add(lblContadorResultados, BorderLayout.EAST);
        return contenedor;
    }

    private void crearTabla() {
        contenidoTabla = crearModelo();
        tabla = new JTable(contenidoTabla);
        EstilosUI.prepararTabla(tabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setFillsViewportHeight(true);
        centrarTabla();

        ordenador = new TableRowSorter<>(contenidoTabla);
        tabla.setRowSorter(ordenador);
        definirAnchos();

        scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(EstilosUI.BORDE));
        scrollPane.setBackground(EstilosUI.FONDO_SECUNDARIO);
        scrollPane.getViewport().setBackground(EstilosUI.FONDO_SECUNDARIO);
    }

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(new Object[]{
                "ID", "Paciente", "Odontólogo", "Estado",
                "Día", "Mes", "Año", "Inicio", "Fin"
        }, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columna) {
                if (columna == 0 || columna == 4
                        || columna == 5 || columna == 6) {
                    return Number.class;
                }
                return columna == 3 ? EstadoTurno.class : String.class;
            }
        };
    }

    private void centrarTabla() {
        DefaultTableCellRenderer encabezado =
                (DefaultTableCellRenderer) tabla.getTableHeader().getDefaultRenderer();
        encabezado.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer celdas = new DefaultTableCellRenderer();
        celdas.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(celdas);
        }
    }

    private void definirAnchos() {
        int[] anchos = {45, 170, 170, 110, 50, 50, 70, 80, 80};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private JPanel crearContenidoTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(0, 20, 0, 20));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void cargarTurnos() {
        for (Turno turno : turnoService.listar()) {
            contenidoTabla.addRow(new Object[]{
                    turno.getId(), turno.getNomPaciente(), turno.getNomOdontologo(),
                    turno.getEstado() == null ? EstadoTurno.PENDIENTE : turno.getEstado(),
                    turno.getDia(), turno.getMes(), turno.getAño(),
                    formatearHora(turno.getHoraInicio()),
                    formatearHora(turno.getHoraFin())
            });
        }
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.add(crearAccionesPrincipales(), BorderLayout.WEST);
        panel.add(crearAccionesEstado(), BorderLayout.EAST);
        return panel;
    }

    private JPanel crearAccionesPrincipales() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        btnCrear = EstilosUI.crearBotonPrimario("Nuevo turno");
        btnVerDetalle = EstilosUI.crearBotonSecundario("Ver detalle");
        btnModificar = EstilosUI.crearBotonSecundario("Modificar");
        btnCancelarTurno = EstilosUI.crearBotonPeligro("Cancelar turno");
        btnVolver = EstilosUI.crearBotonSecundario("Volver");

        panel.add(btnCrear);
        panel.add(btnVerDetalle);
        panel.add(btnModificar);
        panel.add(btnCancelarTurno);
        panel.add(btnVolver);
        return panel;
    }

    private JPanel crearAccionesEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);
        JLabel etiqueta = EstilosUI.crearEtiqueta("Nuevo estado:");
        comboEstado = new JComboBox<>(EstadoTurno.values());
        prepararCombo(comboEstado, 135);
        btnCambiarEstado = EstilosUI.crearBotonPrimario("Cambiar estado");
        panel.add(etiqueta);
        panel.add(comboEstado);
        panel.add(btnCambiarEstado);
        return panel;
    }

    private void prepararCombo(JComboBox<?> combo, int ancho) {
        combo.setFont(EstilosUI.FUENTE_NORMAL);
        combo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        combo.setBackground(EstilosUI.FONDO_SECUNDARIO);
        combo.setPreferredSize(new Dimension(ancho, 36));
        combo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, foco
                );
                setBackground(seleccionado
                        ? EstilosUI.COLOR_PRIMARIO
                        : EstilosUI.FONDO_SECUNDARIO);
                setForeground(seleccionado
                        ? Color.WHITE
                        : EstilosUI.TEXTO_PRINCIPAL);
                setBorder(new EmptyBorder(5, 8, 5, 8));
                return this;
            }
        });
    }

    private void configurarEventos(String usuario) {
        btnCrear.addActionListener(e -> crearTurno(usuario));
        btnVerDetalle.addActionListener(e -> verDetalle(usuario));
        btnModificar.addActionListener(e -> modificarTurno(usuario));
        btnCancelarTurno.addActionListener(e -> cancelarTurno(usuario));
        btnCambiarEstado.addActionListener(e -> cambiarEstado(usuario));
        btnVolver.addActionListener(e -> regresar(usuario));

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarComboConFila();
            }
        });

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetalle(usuario);
                }
            }
        });

        tabla.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke("ENTER"), "verDetalle"
        );
        tabla.getActionMap().put("verDetalle", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                verDetalle(usuario);
            }
        });

        configurarFiltros();
    }

    private void configurarFiltros() {
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });
        comboFiltroEstado.addActionListener(e -> aplicarFiltros());
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
    }

    private void aplicarFiltros() {
        List<RowFilter<DefaultTableModel, Object>> filtros = new ArrayList<>();
        String texto = txtBuscar.getText().trim();
        if (!texto.isEmpty()) {
            filtros.add(RowFilter.regexFilter(
                    "(?i)" + Pattern.quote(texto), 1, 2
            ));
        }

        EstadoTurno estadoFiltro = convertirEstadoFiltro(
                (String) comboFiltroEstado.getSelectedItem()
        );
        if (estadoFiltro != null) {
            filtros.add(new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel,
                        ? extends Object> entrada) {
                    return entrada.getValue(3) == estadoFiltro;
                }
            });
        }

        ordenador.setRowFilter(filtros.isEmpty()
                ? null
                : RowFilter.andFilter(filtros));
        tabla.clearSelection();
        actualizarContadorResultados();
    }

    private EstadoTurno convertirEstadoFiltro(String valor) {
        if (valor == null || "Todos".equals(valor)) {
            return null;
        }
        try {
            return EstadoTurno.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
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
        int visibles = tabla.getRowCount();
        int total = contenidoTabla.getRowCount();
        lblContadorResultados.setText(
                "Mostrando " + visibles + " de " + total + " "
                        + (visibles == 1 ? "turno" : "turnos")
        );
    }

    private void verDetalle(String usuario) {
        Turno turno = obtenerTurnoSeleccionado();
        if (turno != null) {
            panelManager.mostrarPanelDetalleTurno(turno, usuario);
        }
    }

    private void modificarTurno(String usuario) {
        Turno turno = obtenerTurnoSeleccionado();
        if (turno == null) {
            return;
        }
        if (esEstadoFinal(turno.getEstado())) {
            mostrarError(
                    "No se puede modificar la fecha o el horario "
                            + "de un turno finalizado."
            );
            return;
        }
        panelManager.mostrarPanelFormularioTurno(turno, usuario);
    }

    private Turno obtenerTurnoSeleccionado() {
        Long id = obtenerIdSeleccionado();
        if (id == null) {
            return null;
        }
        try {
            Turno turno = turnoService.buscar(id);
            if (turno == null) {
                mostrarError("El turno seleccionado ya no existe.");
            }
            return turno;
        } catch (RuntimeException e) {
            e.printStackTrace();
            mostrarError("No se pudo recuperar el turno.");
            return null;
        }
    }

    private void crearTurno(String usuario) {
        JOptionPane.showMessageDialog(
                this,
                "Seleccione primero el odontólogo y luego el paciente.",
                "Crear turno",
                JOptionPane.INFORMATION_MESSAGE
        );
        panelManager.mostrarPanelSeleccionOdontologo(usuario);
    }

    private void cancelarTurno(String usuario) {
        Turno turno = obtenerTurnoSeleccionado();
        if (turno == null) {
            return;
        }
        if (!puedeCancelar(turno.getEstado())) {
            mostrarError(obtenerMensajeNoCancelable(turno.getEstado()));
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cancelar el turno seleccionado?",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            turnoService.cambiarEstado(turno.getId(), EstadoTurno.CANCELADO);
            JOptionPane.showMessageDialog(
                    this,
                    "El turno se canceló correctamente.",
                    "Turno cancelado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            refrescarLista(usuario);
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cambiarEstado(String usuario) {
        Long id = obtenerIdSeleccionado();
        if (id == null) {
            return;
        }
        EstadoTurno nuevo = (EstadoTurno) comboEstado.getSelectedItem();
        if (nuevo == null) {
            mostrarError("Debe seleccionar un estado.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cambiar el estado del turno a " + nuevo + "?",
                "Confirmar cambio de estado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            turnoService.cambiarEstado(id, nuevo);
            JOptionPane.showMessageDialog(
                    this,
                    "El estado del turno se actualizó correctamente.",
                    "Estado actualizado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            refrescarLista(usuario);
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private Long obtenerIdSeleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            mostrarError("Debe seleccionar un turno.");
            return null;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        Object valor = contenidoTabla.getValueAt(filaModelo, 0);
        if (!(valor instanceof Number)) {
            mostrarError("No se pudo identificar el turno.");
            return null;
        }
        return ((Number) valor).longValue();
    }

    private void sincronizarComboConFila() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        Object valor = contenidoTabla.getValueAt(filaModelo, 3);
        if (valor instanceof EstadoTurno) {
            comboEstado.setSelectedItem(valor);
        }
    }

    private boolean puedeCancelar(EstadoTurno estado) {
        return estado == null
                || estado == EstadoTurno.PENDIENTE
                || estado == EstadoTurno.CONFIRMADO;
    }

    private String obtenerMensajeNoCancelable(EstadoTurno estado) {
        if (estado == EstadoTurno.CANCELADO) {
            return "El turno ya está cancelado.";
        }
        if (estado == EstadoTurno.ATENDIDO) {
            return "No se puede cancelar un turno atendido.";
        }
        if (estado == EstadoTurno.AUSENTE) {
            return "No se puede cancelar un turno marcado como ausente.";
        }
        return "El turno no puede cancelarse en el estado actual.";
    }

    private boolean esEstadoFinal(EstadoTurno estado) {
        return estado == EstadoTurno.ATENDIDO
                || estado == EstadoTurno.CANCELADO
                || estado == EstadoTurno.AUSENTE;
    }

    private String formatearHora(LocalTime hora) {
        return hora == null
                ? ""
                : String.format("%02d:%02d", hora.getHour(), hora.getMinute());
    }

    private void refrescarLista(String usuario) {
        panelManager.mostrarPanelListaTurnosAdmin(usuario);
    }

    private void regresar(String usuario) {
        panelManager.mostrarPanelPrincipalAdmin(usuario);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje == null ? "Operación incorrecta." : mensaje,
                "Operación incorrecta",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
