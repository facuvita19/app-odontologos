package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

import negocio.Odontologo;
import servicio.OdontologoService;

public class PanelListaOdontologos extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable tabla;
    private DefaultTableModel contenidoTabla;
    private JScrollPane scrollPane;
    private TableRowSorter<DefaultTableModel> ordenador;

    private JTextField txtBuscar;
    private JButton btnLimpiarBusqueda;
    private JLabel lblContadorResultados;

    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnDesactivar;
    private JButton btnVolver;

    private final PanelManager panelManager;
    private final OdontologoService odontologoService;

    public PanelListaOdontologos(PanelManager panelManager) {
        this.panelManager = panelManager;
        this.odontologoService = new OdontologoService();
    }

    public void armarPanelListaOdontologos(String usuario) {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        crearTabla();
        cargarOdontologos();

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearBotonera(), BorderLayout.SOUTH);

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
                new BoxLayout(encabezado, BoxLayout.Y_AXIS)
        );
        encabezado.setBackground(EstilosUI.FONDO_PRINCIPAL);
        encabezado.setBorder(new EmptyBorder(22, 30, 12, 30));

        JLabel titulo = new JLabel("Administración de odontólogos");
        titulo.setFont(EstilosUI.FUENTE_TITULO);
        titulo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Consulte profesionales, especialidades y agendas de atención"
        );
        subtitulo.setFont(EstilosUI.FUENTE_NORMAL);
        subtitulo.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ayuda = new JLabel(
                "Utilice la búsqueda o presione los encabezados para ordenar la tabla"
        );
        ayuda.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        ayuda.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        ayuda.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(6));
        encabezado.add(subtitulo);
        encabezado.add(Box.createVerticalStrut(5));
        encabezado.add(ayuda);
        return encabezado;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.add(crearPanelBusqueda(), BorderLayout.NORTH);
        panel.add(crearContenidoTabla(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel contenedor = new JPanel(new BorderLayout(15, 8));
        contenedor.setBackground(EstilosUI.FONDO_PRINCIPAL);
        contenedor.setBorder(new EmptyBorder(0, 30, 10, 30));

        JPanel controles = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        controles.setOpaque(false);

        JLabel lblBuscar = EstilosUI.crearEtiqueta("Buscar:");
        txtBuscar = new JTextField(24);
        EstilosUI.prepararCampo(txtBuscar);
        txtBuscar.setToolTipText(
                "Buscar por nombre, apellido, matrícula o especialidad"
        );

        btnLimpiarBusqueda = EstilosUI.crearBotonSecundario(
                "Limpiar búsqueda"
        );

        controles.add(lblBuscar);
        controles.add(txtBuscar);
        controles.add(btnLimpiarBusqueda);

        lblContadorResultados = new JLabel("Mostrando 0 de 0 odontólogos");
        lblContadorResultados.setFont(EstilosUI.FUENTE_NORMAL);
        lblContadorResultados.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        lblContadorResultados.setHorizontalAlignment(SwingConstants.RIGHT);

        contenedor.add(controles, BorderLayout.WEST);
        contenedor.add(lblContadorResultados, BorderLayout.EAST);
        return contenedor;
    }

    private JPanel crearContenidoTabla() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(EstilosUI.FONDO_PRINCIPAL);
        contenedor.setBorder(new EmptyBorder(0, 30, 10, 30));
        contenedor.add(scrollPane, BorderLayout.CENTER);
        return contenedor;
    }

    private void crearTabla() {
        contenidoTabla = new DefaultTableModel(
                new Object[]{
                        "ID", "Nombre", "Apellido", "Matrícula",
                        "Especialidad", "Días", "Horario", "Duración"
                },
                0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                if (columna == 0 || columna == 3 || columna == 7) {
                    return Number.class;
                }
                return String.class;
            }
        };

        tabla = new JTable(contenidoTabla);
        EstilosUI.prepararTabla(tabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setFillsViewportHeight(true);
        configurarAlineacionTabla();

        ordenador = new TableRowSorter<>(contenidoTabla);
        tabla.setRowSorter(ordenador);

        scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE)
        );
        scrollPane.setBackground(EstilosUI.FONDO_SECUNDARIO);
        scrollPane.getViewport().setBackground(EstilosUI.FONDO_SECUNDARIO);
        scrollPane.getVerticalScrollBar().setBackground(EstilosUI.FONDO_SECUNDARIO);
        scrollPane.getHorizontalScrollBar().setBackground(EstilosUI.FONDO_SECUNDARIO);

        SwingUtilities.invokeLater(this::definirAnchoColumnas);
    }

    private void configurarAlineacionTabla() {
        DefaultTableCellRenderer encabezado =
                (DefaultTableCellRenderer) tabla.getTableHeader()
                        .getDefaultRenderer();
        encabezado.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int columna = 0; columna < tabla.getColumnCount(); columna++) {
            tabla.getColumnModel().getColumn(columna)
                    .setCellRenderer(centrado);
        }
    }

    private void definirAnchoColumnas() {
        if (tabla.getColumnCount() < 8) {
            return;
        }

        tabla.getColumnModel().getColumn(0).setPreferredWidth(42);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(190);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(80);
    }

    private void cargarOdontologos() {
        List<Odontologo> odontologos = odontologoService.listar();

        for (Odontologo odontologo : odontologos) {
            Object[] fila = {
                    odontologo.getId(),
                    odontologo.getNombre(),
                    odontologo.getApellido(),
                    odontologo.getMatricula(),
                    odontologo.getEspecialidad(),
                    formatearDias(odontologo.getDiasAtencion()),
                    formatearHorario(
                            odontologo.getHoraInicio(),
                            odontologo.getHoraFin()
                    ),
                    odontologo.getDuracionTurno()
            };
            contenidoTabla.addRow(fila);
        }
    }

    private String formatearDias(Set<DayOfWeek> dias) {
        if (dias == null || dias.isEmpty()) {
            return "Sin días";
        }

        StringBuilder texto = new StringBuilder();
        DayOfWeek[] orden = {
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
        };

        for (DayOfWeek dia : orden) {
            if (!dias.contains(dia)) {
                continue;
            }
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(abreviarDia(dia));
        }
        return texto.toString();
    }

    private String abreviarDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return "Lun";
            case TUESDAY: return "Mar";
            case WEDNESDAY: return "Mié";
            case THURSDAY: return "Jue";
            case FRIDAY: return "Vie";
            case SATURDAY: return "Sáb";
            case SUNDAY: return "Dom";
            default: return "";
        }
    }

    private String formatearHorario(LocalTime inicio, LocalTime fin) {
        return formatearHora(inicio) + " - " + formatearHora(fin);
    }

    private String formatearHora(LocalTime hora) {
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
        JPanel botonera = EstilosUI.crearPanelBotonera();
        botonera.setBorder(new EmptyBorder(10, 30, 18, 30));

        btnCrear = EstilosUI.crearBotonPrimario("Nuevo odontólogo");
        btnModificar = EstilosUI.crearBotonSecundario("Modificar");
        btnDesactivar = EstilosUI.crearBotonPeligro("Desactivar");
        btnVolver = EstilosUI.crearBotonSecundario("Volver");

        botonera.add(btnCrear);
        botonera.add(btnModificar);
        botonera.add(btnDesactivar);
        botonera.add(btnVolver);
        return botonera;
    }

    private void configurarEventos(String usuario) {
        btnCrear.addActionListener(
                evento -> panelManager.mostrarPanelFormularioOdontologo(usuario)
        );
        btnModificar.addActionListener(
                evento -> modificarOdontologo(usuario)
        );
        btnDesactivar.addActionListener(
                evento -> desactivarOdontologo(usuario)
        );
        btnVolver.addActionListener(
                evento -> panelManager.mostrarPanelPrincipalAdmin(usuario)
        );

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evento) {
                if (evento.getClickCount() == 2) {
                    modificarOdontologo(usuario);
                }
            }
        });

        tabla.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke("ENTER"),
                "modificarOdontologo"
        );
        tabla.getActionMap().put(
                "modificarOdontologo",
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evento) {
                        modificarOdontologo(usuario);
                    }
                }
        );

        configurarBusqueda();
    }

    private void configurarBusqueda() {
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                aplicarBusqueda();
            }
            @Override
            public void removeUpdate(DocumentEvent evento) {
                aplicarBusqueda();
            }
            @Override
            public void changedUpdate(DocumentEvent evento) {
                aplicarBusqueda();
            }
        });

        btnLimpiarBusqueda.addActionListener(
                evento -> limpiarBusqueda()
        );
    }

    private void aplicarBusqueda() {
        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(texto),
                            1, 2, 3, 4
                    )
            );
        }

        tabla.clearSelection();
        actualizarContadorResultados();
    }

    private void limpiarBusqueda() {
        txtBuscar.setText("");
        ordenador.setRowFilter(null);
        tabla.clearSelection();
        actualizarContadorResultados();
        txtBuscar.requestFocusInWindow();
    }

    private void actualizarContadorResultados() {
        if (lblContadorResultados == null) {
            return;
        }
        int visibles = tabla.getRowCount();
        int total = contenidoTabla.getRowCount();
        lblContadorResultados.setText(
                "Mostrando " + visibles + " de " + total + " "
                        + (visibles == 1 ? "odontólogo" : "odontólogos")
        );
    }

    private void modificarOdontologo(String usuario) {
        Long odontologoId = obtenerIdSeleccionado();
        if (odontologoId == null) {
            return;
        }

        try {
            Odontologo odontologo = odontologoService.buscar(odontologoId);
            if (odontologo == null) {
                mostrarError(
                        "El odontólogo seleccionado ya no existe o está inactivo."
                );
                refrescarLista(usuario);
                return;
            }
            panelManager.mostrarPanelFormularioOdontologo(
                    odontologo,
                    usuario
            );
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            mostrarError("No se pudo recuperar la información del odontólogo.");
        }
    }

    private void desactivarOdontologo(String usuario) {
        Long odontologoId = obtenerIdSeleccionado();
        if (odontologoId == null) {
            return;
        }

        try {
            Odontologo odontologo = odontologoService.buscar(odontologoId);
            if (odontologo == null) {
                mostrarError(
                        "El odontólogo seleccionado ya no existe o está inactivo."
                );
                refrescarLista(usuario);
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea desactivar al odontólogo "
                            + odontologo.getNombre() + " "
                            + odontologo.getApellido() + "?\n\n"
                            + "Se conservará el historial de turnos.",
                    "Confirmar desactivación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }

            odontologoService.eliminar(odontologoId);
            JOptionPane.showMessageDialog(
                    this,
                    "El odontólogo se desactivó correctamente.",
                    "Odontólogo desactivado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            refrescarLista(usuario);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            mostrarError("No se pudo desactivar al odontólogo.");
        }
    }

    private Long obtenerIdSeleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            mostrarError("Debe seleccionar un odontólogo.");
            return null;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        Object valorId = contenidoTabla.getValueAt(filaModelo, 0);
        if (!(valorId instanceof Number)) {
            mostrarError("No se pudo identificar al odontólogo seleccionado.");
            return null;
        }
        return ((Number) valorId).longValue();
    }

    private void refrescarLista(String usuario) {
        panelManager.mostrarPanelListaOdontologos(usuario);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Operación incorrecta",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
