package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import negocio.Paciente;
import servicio.PacienteService;

public class PanelListaPacientes extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable tabla;
    private DefaultTableModel contenidoTabla;
    private JScrollPane scrollPane;

    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnDesactivar;
    private JButton btnVolver;

    private final PanelManager panelManager;
    private final PacienteService pacienteService;

    public PanelListaPacientes(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.pacienteService = new PacienteService();
    }

    public void armarPanelListaPacientes(
            String usuario) {

        removeAll();

        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        add(
                crearEncabezado(),
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

        cargarPacientes();
        configurarEventos(usuario);

        revalidate();
        repaint();
        setVisible(true);
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
                        22,
                        30,
                        15,
                        30
                )
        );

        JLabel titulo = new JLabel(
                "Administración de pacientes"
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
                "Consulte, registre y modifique "
                        + "las fichas de pacientes"
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
                "Presione los encabezados para ordenar la tabla"
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
                        "DNI",
                        "Domicilio",
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
                        || columna == 5) {

                    return Number.class;
                }

                return String.class;
            }
        };

        tabla = new JTable(contenidoTabla);

        EstilosUI.prepararTabla(tabla);
        centrarContenidoTabla();

        tabla.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        tabla.setFillsViewportHeight(true);

        TableRowSorter<DefaultTableModel> ordenador =
                new TableRowSorter<>(
                        contenidoTabla
                );

        tabla.setRowSorter(ordenador);

        scrollPane = new JScrollPane(tabla);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        EstilosUI.BORDE
                )
        );

        scrollPane.getViewport().setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        SwingUtilities.invokeLater(
                this::definirAnchoColumnas
        );
    }

    private void definirAnchoColumnas() {
        if (tabla.getColumnModel()
                .getColumnCount() < 6) {

            return;
        }

        tabla.getColumnModel()
                .getColumn(0)
                .setMinWidth(45);

        tabla.getColumnModel()
                .getColumn(0)
                .setMaxWidth(65);

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(50);

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(150);

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(150);

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(110);

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(320);

        tabla.getColumnModel()
                .getColumn(5)
                .setMinWidth(55);

        tabla.getColumnModel()
                .getColumn(5)
                .setMaxWidth(80);

        tabla.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(60);
    }

    private void cargarPacientes() {
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

            contenidoTabla.addRow(fila);
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

        btnCrear =
                EstilosUI.crearBotonPrimario(
                        "Nuevo paciente"
                );

        btnModificar =
                EstilosUI.crearBotonSecundario(
                        "Modificar"
                );

        btnDesactivar =
                EstilosUI.crearBotonPeligro(
                        "Desactivar"
                );

        btnVolver =
                EstilosUI.crearBotonSecundario(
                        "Volver"
                );

        botonera.add(btnCrear);
        botonera.add(btnModificar);
        botonera.add(btnDesactivar);
        botonera.add(btnVolver);

        return botonera;
    }

    private void configurarEventos(
            String usuario) {

        btnCrear.addActionListener(
                evento -> crearPaciente(usuario)
        );

        btnModificar.addActionListener(
                evento -> modificarPaciente(usuario)
        );

        btnDesactivar.addActionListener(
                evento -> desactivarPaciente(usuario)
        );

        btnVolver.addActionListener(
                evento ->
                        panelManager
                                .mostrarPanelPrincipalAdmin(
                                        usuario
                                )
        );

        tabla.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent evento) {

                        if (evento.getClickCount() == 2) {
                            modificarPaciente(usuario);
                        }
                    }
                }
        );
    }

    private void crearPaciente(
            String usuario) {

        panelManager.mostrarPanelFormularioPaciente(
                usuario
        );
    }

    private void modificarPaciente(
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
                                + "ya no existe o está inactivo."
                );

                refrescarLista(usuario);
                return;
            }

            panelManager.mostrarPanelFormularioPaciente(
                    paciente,
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

    private void desactivarPaciente(
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
                                + "ya no existe o está inactivo."
                );

                refrescarLista(usuario);
                return;
            }

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            this,
                            "¿Desea desactivar al paciente "
                                    + paciente.getNombre()
                                    + " "
                                    + paciente.getApellido()
                                    + "?\n\n"
                                    + "Se conservará el historial "
                                    + "de turnos.",
                            "Confirmar desactivación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (respuesta
                    != JOptionPane.YES_OPTION) {

                return;
            }

            pacienteService.eliminar(
                    pacienteId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "El paciente se desactivó "
                            + "correctamente.",
                    "Paciente desactivado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refrescarLista(usuario);

        } catch (IllegalArgumentException exception) {
            mostrarError(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo desactivar "
                            + "al paciente."
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

        return ((Number) valorId).longValue();
    }

    private void refrescarLista(
            String usuario) {

        panelManager.mostrarPanelListaPacientes(
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
    private void centrarContenidoTabla() {
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
}