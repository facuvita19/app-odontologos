package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalTime;

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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import negocio.EstadoTurno;
import negocio.Turno;
import servicio.TurnoService;

public class PanelFormularioTurno extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel lblTitulo;
    private JLabel lblSubtitulo;

    private JLabel lblOdontologoSeleccionado;
    private JLabel lblPacienteSeleccionado;
    private JLabel lblEstadoActual;

    private JTextField txtDia;
    private JTextField txtMes;
    private JTextField txtAnio;

    private JComboBox<LocalTime> comboInicio;
    private JComboBox<LocalTime> comboFin;

    private JButton btnGuardar;
    private JButton btnCancelar;

    private long id;
    private long odontologoId;
    private long pacienteId;
    private long usuarioId;

    private String nombreOdontologo;
    private String nombrePaciente;

    private EstadoTurno estado =
            EstadoTurno.PENDIENTE;

    private final PanelManager panelManager;
    private final TurnoService turnoService;

    public PanelFormularioTurno(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.turnoService = new TurnoService();
    }

    public void armarPanelFormulario(
            String usuario) {

        removeAll();

        setLayout(new BorderLayout());

        setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        JPanel contenedor =
                new JPanel(
                        new GridBagLayout()
                );

        contenedor.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        contenedor.setBorder(
                new EmptyBorder(
                        25,
                        35,
                        25,
                        35
                )
        );

        JPanel tarjeta =
                crearTarjetaFormulario();

        GridBagConstraints restricciones =
                new GridBagConstraints();

        restricciones.gridx = 0;
        restricciones.gridy = 0;

        restricciones.weightx = 1.0;
        restricciones.weighty = 1.0;

        restricciones.fill =
                GridBagConstraints.BOTH;

        restricciones.anchor =
                GridBagConstraints.CENTER;

        restricciones.insets =
                new Insets(
                        10,
                        10,
                        10,
                        10
                );

        contenedor.add(
                tarjeta,
                restricciones
        );

        add(
                contenedor,
                BorderLayout.CENTER
        );

        configurarEventos(usuario);
        actualizarInformacionSeleccionada();

        revalidate();
        repaint();
        setVisible(true);

        SwingUtilities.invokeLater(
                () -> txtDia.requestFocusInWindow()
        );
    }

    public void llenarFormulario(
            Turno turno) {

        if (turno == null) {
            return;
        }

        id = turno.getId();

        odontologoId =
                turno.getOdontologoId();

        pacienteId =
                turno.getPacienteId();

        usuarioId =
                turno.getUsuarioId();

        nombreOdontologo =
                turno.getNomOdontologo();

        nombrePaciente =
                turno.getNomPaciente();

        estado = turno.getEstado();

        if (estado == null) {
            estado = EstadoTurno.PENDIENTE;
        }

        if (turno.getDia() != 0) {
            txtDia.setText(
                    Integer.toString(
                            turno.getDia()
                    )
            );

            txtMes.setText(
                    Integer.toString(
                            turno.getMes()
                    )
            );

            txtAnio.setText(
                    Integer.toString(
                            turno.getAño()
                    )
            );
        }

        seleccionarHorario(
                comboInicio,
                turno.getHoraInicio()
        );

        seleccionarHorario(
                comboFin,
                turno.getHoraFin()
        );

        actualizarTituloEdicion();
        actualizarInformacionSeleccionada();

        SwingUtilities.invokeLater(
                () -> txtDia.requestFocusInWindow()
        );
    }

    private JPanel crearTarjetaFormulario() {
        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                20
                        )
                );

        tarjeta.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        tarjeta.setMinimumSize(
                new Dimension(
                        560,
                        500
                )
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        850,
                        650
                )
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                EstilosUI.BORDE
                        ),
                        new EmptyBorder(
                                26,
                                36,
                                26,
                                36
                        )
                )
        );

        tarjeta.add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        tarjeta.add(
                crearContenidoFormulario(),
                BorderLayout.CENTER
        );

        tarjeta.add(
                crearBotonera(),
                BorderLayout.SOUTH
        );

        return tarjeta;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado =
                new JPanel();

        encabezado.setLayout(
                new BoxLayout(
                        encabezado,
                        BoxLayout.Y_AXIS
                )
        );

        encabezado.setOpaque(false);

        lblTitulo =
                new JLabel(
                        "Nuevo turno"
                );

        lblTitulo.setFont(
                EstilosUI.FUENTE_TITULO
        );

        lblTitulo.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        lblTitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        lblSubtitulo =
                new JLabel(
                        "Complete la fecha y el horario "
                                + "de la reserva"
                );

        lblSubtitulo.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        lblSubtitulo.setForeground(
                EstilosUI.TEXTO_SECUNDARIO
        );

        lblSubtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        encabezado.add(lblTitulo);

        encabezado.add(
                Box.createVerticalStrut(7)
        );

        encabezado.add(lblSubtitulo);

        return encabezado;
    }

    private JPanel crearContenidoFormulario() {
        JPanel contenido =
                new JPanel();

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );

        contenido.setOpaque(false);

        JPanel informacion =
                crearPanelInformacion();

        JPanel formulario =
                crearFormulario();
        
        informacion.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        informacion.getPreferredSize().height
                )
        );

        formulario.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        formulario.getPreferredSize().height
                )
        );

        informacion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        formulario.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        contenido.add(informacion);

        contenido.add(
                Box.createVerticalStrut(20)
        );

        contenido.add(formulario);

        return contenido;
    }

    private JPanel crearPanelInformacion() {
        JPanel panel =
                new JPanel(
                        new GridLayout(
                                3,
                                1,
                                0,
                                8
                        )
                );

        panel.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                EstilosUI.BORDE
                        ),
                        new EmptyBorder(
                                14,
                                16,
                                14,
                                16
                        )
                )
        );

        lblOdontologoSeleccionado =
                crearDatoSeleccionado(
                        "Odontólogo: sin seleccionar"
                );

        lblPacienteSeleccionado =
                crearDatoSeleccionado(
                        "Paciente: sin seleccionar"
                );

        lblEstadoActual =
                crearDatoSeleccionado(
                        "Estado: Pendiente"
                );

        panel.add(
                lblOdontologoSeleccionado
        );

        panel.add(
                lblPacienteSeleccionado
        );

        panel.add(
                lblEstadoActual
        );

        return panel;
    }

    private JLabel crearDatoSeleccionado(
            String texto) {

        JLabel etiqueta =
                new JLabel(texto);

        etiqueta.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        etiqueta.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        return etiqueta;
    }

    private JPanel crearFormulario() {
        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setOpaque(false);

        GridBagConstraints restricciones =
                new GridBagConstraints();

        restricciones.fill =
                GridBagConstraints.HORIZONTAL;

        restricciones.anchor =
                GridBagConstraints.WEST;

        int fila = 0;

        JPanel panelFecha =
                crearPanelFecha();

        agregarComponenteCompleto(
                formulario,
                restricciones,
                fila++,
                "Fecha del turno: *",
                panelFecha
        );

        comboInicio =
                crearComboHorarios(false);

        agregarComponenteCompleto(
                formulario,
                restricciones,
                fila++,
                "Hora de inicio: *",
                comboInicio
        );

        comboFin =
                crearComboHorarios(true);

        agregarComponenteCompleto(
                formulario,
                restricciones,
                fila,
                "Hora de finalización: *",
                comboFin
        );

        return formulario;
    }

    private JPanel crearPanelFecha() {
        JPanel panelFecha =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                10,
                                0
                        )
                );

        panelFecha.setOpaque(false);

        txtDia = new JTextField();
        txtMes = new JTextField();
        txtAnio = new JTextField();

        EstilosUI.prepararCampo(
                txtDia
        );

        EstilosUI.prepararCampo(
                txtMes
        );

        EstilosUI.prepararCampo(
                txtAnio
        );

        txtDia.setToolTipText(
                "Día"
        );

        txtMes.setToolTipText(
                "Mes"
        );

        txtAnio.setToolTipText(
                "Año"
        );

        panelFecha.add(
                crearCampoConTitulo(
                        "Día",
                        txtDia
                )
        );

        panelFecha.add(
                crearCampoConTitulo(
                        "Mes",
                        txtMes
                )
        );

        panelFecha.add(
                crearCampoConTitulo(
                        "Año",
                        txtAnio
                )
        );

        return panelFecha;
    }

    private JPanel crearCampoConTitulo(
            String titulo,
            JTextField campo) {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                5
                        )
                );

        panel.setOpaque(false);

        JLabel etiqueta =
                new JLabel(titulo);

        etiqueta.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.PLAIN,
                        12
                )
        );

        etiqueta.setForeground(
                EstilosUI.TEXTO_SECUNDARIO
        );

        etiqueta.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        panel.add(
                etiqueta,
                BorderLayout.NORTH
        );

        panel.add(
                campo,
                BorderLayout.CENTER
        );

        return panel;
    }

    private void agregarComponenteCompleto(
            JPanel formulario,
            GridBagConstraints restricciones,
            int fila,
            String textoEtiqueta,
            JComponent componente) {

        JLabel etiqueta =
                EstilosUI.crearEtiqueta(
                        textoEtiqueta
                );

        etiqueta.setLabelFor(
                componente
        );

        restricciones.gridx = 0;
        restricciones.gridy = fila;
        restricciones.weightx = 0.0;

        restricciones.insets =
                new Insets(
                        10,
                        0,
                        10,
                        18
                );

        formulario.add(
                etiqueta,
                restricciones
        );

        restricciones.gridx = 1;
        restricciones.weightx = 1.0;

        restricciones.insets =
                new Insets(
                        10,
                        0,
                        10,
                        0
                );

        formulario.add(
                componente,
                restricciones
        );
    }

    private JComboBox<LocalTime> crearComboHorarios(
            boolean incluirCierre) {

        JComboBox<LocalTime> combo =
                new JComboBox<>();

        LocalTime horario =
                LocalTime.of(
                        8,
                        0
                );

        LocalTime limite =
                incluirCierre
                        ? LocalTime.of(20, 0)
                        : LocalTime.of(19, 30);

        while (!horario.isAfter(limite)) {
            combo.addItem(horario);

            horario =
                    horario.plusMinutes(30);
        }

        prepararComboHorario(combo);

        return combo;
    }

    private void prepararComboHorario(
            JComboBox<LocalTime> combo) {

        combo.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        combo.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        combo.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        combo.setPreferredSize(
                new Dimension(
                        220,
                        36
                )
        );

        combo.setRenderer(
                new DefaultListCellRenderer() {

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

                        if (valor
                                instanceof LocalTime) {

                            LocalTime hora =
                                    (LocalTime) valor;

                            setText(
                                    formatearHora(
                                            hora
                                    )
                            );
                        }

                        if (seleccionado) {
                            setBackground(
                                    EstilosUI.COLOR_PRIMARIO
                            );

                            setForeground(
                                    Color.WHITE
                            );

                        } else {
                            setBackground(
                                    EstilosUI.FONDO_SECUNDARIO
                            );

                            setForeground(
                                    EstilosUI.TEXTO_PRINCIPAL
                            );
                        }

                        setBorder(
                                new EmptyBorder(
                                        5,
                                        8,
                                        5,
                                        8
                                )
                        );

                        return this;
                    }
                }
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

    private void seleccionarHorario(
            JComboBox<LocalTime> combo,
            LocalTime horario) {

        if (combo == null
                || horario == null) {

            return;
        }

        combo.setSelectedItem(
                horario
        );
    }

    private JPanel crearBotonera() {
        JPanel botonera =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        botonera.setOpaque(false);

        btnCancelar =
                EstilosUI.crearBotonSecundario(
                        "Cancelar"
                );

        btnGuardar =
                EstilosUI.crearBotonPrimario(
                        "Guardar turno"
                );

        botonera.add(
                btnCancelar
        );

        botonera.add(
                btnGuardar
        );

        return botonera;
    }

    private void configurarEventos(
            String usuario) {

        btnGuardar.addActionListener(
                evento ->
                        guardarTurno(
                                usuario
                        )
        );

        btnCancelar.addActionListener(
                evento ->
                        cancelar(usuario)
        );

        txtDia.addActionListener(
                evento ->
                        txtMes.requestFocusInWindow()
        );

        txtMes.addActionListener(
                evento ->
                        txtAnio.requestFocusInWindow()
        );

        txtAnio.addActionListener(
                evento ->
                        comboInicio.requestFocusInWindow()
        );

        comboInicio.addActionListener(
                evento ->
                        sugerirHoraFinal()
        );
    }

    private void sugerirHoraFinal() {
        LocalTime horaInicio =
                (LocalTime)
                        comboInicio.getSelectedItem();

        if (horaInicio == null) {
            return;
        }

        LocalTime horaFinalSugerida =
                horaInicio.plusMinutes(30);

        if (horaFinalSugerida.isAfter(
                LocalTime.of(20, 0))) {

            return;
        }

        comboFin.setSelectedItem(
                horaFinalSugerida
        );
    }

    private void actualizarInformacionSeleccionada() {
        if (lblOdontologoSeleccionado == null
                || lblPacienteSeleccionado == null
                || lblEstadoActual == null) {

            return;
        }

        lblOdontologoSeleccionado.setText(
                "Odontólogo: "
                        + textoSeleccionado(
                                nombreOdontologo
                        )
        );

        lblPacienteSeleccionado.setText(
                "Paciente: "
                        + textoSeleccionado(
                                nombrePaciente
                        )
        );

        EstadoTurno estadoVisible =
                estado == null
                        ? EstadoTurno.PENDIENTE
                        : estado;

        lblEstadoActual.setText(
                "Estado: "
                        + estadoVisible
        );
    }

    private String textoSeleccionado(
            String valor) {

        if (valor == null
                || valor.trim().isEmpty()) {

            return "sin seleccionar";
        }

        return valor.trim();
    }

    private void actualizarTituloEdicion() {
        if (lblTitulo == null
                || lblSubtitulo == null) {

            return;
        }

        lblTitulo.setText(
                "Modificar turno"
        );

        lblSubtitulo.setText(
                "Actualice la fecha o el horario "
                        + "de la reserva seleccionada"
        );
    }

    private void guardarTurno(
            String usuario) {

        try {
            Turno turno =
                    construirTurno(usuario);

            turnoService.guardar(turno);

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeGuardado(),
                    "Turno guardado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            mostrarListaTurnos(usuario);

        } catch (IllegalArgumentException exception) {
            mostrarError(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo guardar el turno. "
                            + "Intente nuevamente."
            );
        }
    }

    private String obtenerMensajeGuardado() {
        if (id == 0) {
            return "El turno se creó correctamente.";
        }

        return "El turno se actualizó correctamente.";
    }

    private Turno construirTurno(
            String usuario) {

        validarCamposCompletos();

        int dia;
        int mes;
        int anio;

        try {
            dia =
                    Integer.parseInt(
                            txtDia.getText().trim()
                    );

            mes =
                    Integer.parseInt(
                            txtMes.getText().trim()
                    );

            anio =
                    Integer.parseInt(
                            txtAnio.getText().trim()
                    );

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "La fecha debe contener "
                            + "solamente números."
            );
        }

        LocalTime horaInicio =
                (LocalTime)
                        comboInicio.getSelectedItem();

        LocalTime horaFin =
                (LocalTime)
                        comboFin.getSelectedItem();

        if (horaInicio == null
                || horaFin == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar el horario "
                            + "de inicio y finalización."
            );
        }

        Turno turno = new Turno();

        turno.setId(id);
        turno.setOdontologoId(odontologoId);
        turno.setPacienteId(pacienteId);
        turno.setUsuarioId(usuarioId);

        turno.setDia(dia);
        turno.setMes(mes);
        turno.setAño(anio);

        turno.setHoraInicio(horaInicio);
        turno.setHoraFin(horaFin);

        turno.setNomOdontologo(
                nombreOdontologo
        );

        turno.setNomPaciente(
                nombrePaciente
        );

        turno.setNomUsuario(
                usuario
        );

        turno.setEstado(
                estado
        );

        return turno;
    }

    private void validarCamposCompletos() {
        if (txtDia.getText()
                .trim()
                .isEmpty()) {

            txtDia.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe ingresar el día."
            );
        }

        if (txtMes.getText()
                .trim()
                .isEmpty()) {

            txtMes.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe ingresar el mes."
            );
        }

        if (txtAnio.getText()
                .trim()
                .isEmpty()) {

            txtAnio.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe ingresar el año."
            );
        }
    }

    private void mostrarListaTurnos(
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

    private void cancelar(
            String usuario) {

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea salir sin guardar "
                                + "los cambios?",
                        "Cancelar turno",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta
                != JOptionPane.YES_OPTION) {

            return;
        }

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
                "Datos incorrectos",
                JOptionPane.ERROR_MESSAGE
        );
    }
}