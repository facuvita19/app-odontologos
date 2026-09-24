package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import negocio.Especialidad;
import negocio.Odontologo;
import servicio.OdontologoService;

public class PanelFormularioOdontologo extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final LocalTime HORA_INICIAL =
            LocalTime.of(7, 0);

    private static final LocalTime HORA_FINAL =
            LocalTime.of(22, 0);

    private JLabel lblTitulo;
    private JLabel lblSubtitulo;

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtMatricula;
    private JTextField txtEdad;

    private JComboBox<Especialidad> comboEspecialidad;
    private JComboBox<LocalTime> comboHoraInicio;
    private JComboBox<LocalTime> comboHoraFin;
    private JComboBox<Integer> comboDuracion;

    private final Map<DayOfWeek, JCheckBox> checksDias =
            new EnumMap<>(DayOfWeek.class);

    private JButton btnGuardar;
    private JButton btnCancelar;

    private long id;

    private final PanelManager panelManager;
    private final OdontologoService odontologoService;

    public PanelFormularioOdontologo(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.odontologoService =
                new OdontologoService();
    }

    public void armarPanelFormulario(
            String usuario) {

        removeAll();

        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        JPanel contenedor = new JPanel(
                new GridBagLayout()
        );

        contenedor.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        contenedor.setBorder(
                new EmptyBorder(25, 70, 25, 70)
        );

        JPanel tarjeta = crearTarjetaFormulario();

        GridBagConstraints restricciones =
                new GridBagConstraints();

        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weightx = 1.0;
        restricciones.weighty = 1.0;
        restricciones.fill = GridBagConstraints.BOTH;
        restricciones.anchor = GridBagConstraints.CENTER;

        contenedor.add(tarjeta, restricciones);
        add(contenedor, BorderLayout.CENTER);

        configurarEventos(usuario);
        establecerValoresPredeterminados();

        revalidate();
        repaint();
        setVisible(true);

        SwingUtilities.invokeLater(
                () -> txtNombre.requestFocusInWindow()
        );
    }

    public void llenarFormulario(
            Odontologo odontologo) {

        if (odontologo == null) {
            return;
        }

        id = odontologo.getId();

        txtNombre.setText(
                valorSeguro(odontologo.getNombre())
        );

        txtApellido.setText(
                valorSeguro(odontologo.getApellido())
        );

        txtMatricula.setText(
                Integer.toString(
                        odontologo.getMatricula()
                )
        );

        txtEdad.setText(
                Integer.toString(
                        odontologo.getEdad()
                )
        );

        comboEspecialidad.setSelectedItem(
                odontologo.getEspecialidad()
        );

        comboHoraInicio.setSelectedItem(
                odontologo.getHoraInicio()
        );

        comboHoraFin.setSelectedItem(
                odontologo.getHoraFin()
        );

        comboDuracion.setSelectedItem(
                odontologo.getDuracionTurno()
        );

        cargarDiasSeleccionados(
                odontologo.getDiasAtencion()
        );

        actualizarTituloEdicion();

        SwingUtilities.invokeLater(
                () -> txtNombre.requestFocusInWindow()
        );
    }

    private JPanel crearTarjetaFormulario() {
        JPanel tarjeta = new JPanel(
                new BorderLayout(0, 18)
        );

        tarjeta.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        tarjeta.setMinimumSize(
                new Dimension(660, 610)
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                EstilosUI.BORDE
                        ),
                        new EmptyBorder(26, 36, 26, 36)
                )
        );

        tarjeta.add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        JScrollPane scrollFormulario =
                new JScrollPane(
                        crearContenidoFormulario()
                );

        scrollFormulario.setBorder(null);
        scrollFormulario.setOpaque(false);
        scrollFormulario.getViewport().setOpaque(false);
        scrollFormulario.getVerticalScrollBar()
                .setUnitIncrement(16);

        tarjeta.add(
                scrollFormulario,
                BorderLayout.CENTER
        );

        tarjeta.add(
                crearBotonera(),
                BorderLayout.SOUTH
        );

        return tarjeta;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();

        encabezado.setLayout(
                new BoxLayout(
                        encabezado,
                        BoxLayout.Y_AXIS
                )
        );

        encabezado.setOpaque(false);

        lblTitulo = new JLabel(
                "Nuevo odontólogo"
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

        lblSubtitulo = new JLabel(
                "Complete los datos profesionales "
                        + "y la agenda de atención"
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
        encabezado.add(Box.createVerticalStrut(7));
        encabezado.add(lblSubtitulo);

        return encabezado;
    }

    private void actualizarTituloEdicion() {
        if (lblTitulo == null
                || lblSubtitulo == null) {
            return;
        }

        lblTitulo.setText(
                "Modificar odontólogo"
        );

        lblSubtitulo.setText(
                "Actualice los datos profesionales "
                        + "y la agenda de atención"
        );
    }

    private JPanel crearContenidoFormulario() {
        JPanel contenido = new JPanel();

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );

        contenido.setOpaque(false);

        JPanel datosProfesionales =
                crearSeccionDatosProfesionales();

        JPanel agenda = crearSeccionAgenda();

        datosProfesionales.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        agenda.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        contenido.add(datosProfesionales);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(agenda);

        return contenido;
    }

    private JPanel crearSeccionDatosProfesionales() {
        JPanel seccion = crearSeccion(
                "Datos profesionales"
        );

        JPanel formulario = new JPanel(
                new GridBagLayout()
        );

        formulario.setOpaque(false);

        GridBagConstraints restricciones =
                crearRestriccionesBase();

        int fila = 0;

        txtNombre = new JTextField(22);
        EstilosUI.prepararCampo(txtNombre);
        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Nombre: *",
                txtNombre
        );

        txtApellido = new JTextField(22);
        EstilosUI.prepararCampo(txtApellido);
        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Apellido: *",
                txtApellido
        );

        txtMatricula = new JTextField(22);
        EstilosUI.prepararCampo(txtMatricula);
        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Matrícula: *",
                txtMatricula
        );

        txtEdad = new JTextField(22);
        EstilosUI.prepararCampo(txtEdad);
        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Edad: *",
                txtEdad
        );

        comboEspecialidad = new JComboBox<>(
                Especialidad.values()
        );

        prepararCombo(comboEspecialidad);

        agregarFila(
                formulario,
                restricciones,
                fila,
                "Especialidad: *",
                comboEspecialidad
        );

        seccion.add(
                formulario,
                BorderLayout.CENTER
        );

        return seccion;
    }

    private JPanel crearSeccionAgenda() {
        JPanel seccion = crearSeccion(
                "Agenda de atención"
        );

        JPanel contenido = new JPanel();

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );

        contenido.setOpaque(false);

        JPanel panelDias = crearPanelDias();
        JPanel panelHorarios = crearPanelHorarios();

        panelDias.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelHorarios.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        contenido.add(panelDias);
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(panelHorarios);

        seccion.add(
                contenido,
                BorderLayout.CENTER
        );

        return seccion;
    }

    private JPanel crearSeccion(
            String titulo) {

        JPanel seccion = new JPanel(
                new BorderLayout(0, 12)
        );

        seccion.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        seccion.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                EstilosUI.BORDE
                        ),
                        new EmptyBorder(15, 18, 15, 18)
                )
        );

        JLabel etiquetaTitulo = new JLabel(titulo);

        etiquetaTitulo.setFont(
                EstilosUI.FUENTE_SUBTITULO
        );

        etiquetaTitulo.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        seccion.add(
                etiquetaTitulo,
                BorderLayout.NORTH
        );

        return seccion;
    }

    private JPanel crearPanelDias() {
        JPanel panel = new JPanel(
                new BorderLayout(0, 8)
        );

        panel.setOpaque(false);

        JLabel etiqueta =
                EstilosUI.crearEtiqueta(
                        "Días de atención: *"
                );

        JPanel checks = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        10,
                        0
                )
        );

        checks.setOpaque(false);

        agregarCheckDia(
                checks,
                DayOfWeek.MONDAY,
                "Lun"
        );

        agregarCheckDia(
                checks,
                DayOfWeek.TUESDAY,
                "Mar"
        );

        agregarCheckDia(
                checks,
                DayOfWeek.WEDNESDAY,
                "Mié"
        );

        agregarCheckDia(
                checks,
                DayOfWeek.THURSDAY,
                "Jue"
        );

        agregarCheckDia(
                checks,
                DayOfWeek.FRIDAY,
                "Vie"
        );

        agregarCheckDia(
                checks,
                DayOfWeek.SATURDAY,
                "Sáb"
        );

        agregarCheckDia(
                checks,
                DayOfWeek.SUNDAY,
                "Dom"
        );

        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(checks, BorderLayout.CENTER);

        return panel;
    }

    private void agregarCheckDia(
            JPanel panel,
            DayOfWeek dia,
            String texto) {

        JCheckBox check = new JCheckBox(texto);

        check.setFont(EstilosUI.FUENTE_NORMAL);
        check.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        check.setOpaque(false);
        check.setFocusPainted(false);

        checksDias.put(dia, check);
        panel.add(check);
    }

    private JPanel crearPanelHorarios() {
        JPanel panel = new JPanel(
                new GridBagLayout()
        );

        panel.setOpaque(false);

        GridBagConstraints restricciones =
                crearRestriccionesBase();

        comboHoraInicio = crearComboHorarios();
        agregarFila(
                panel,
                restricciones,
                0,
                "Hora de inicio: *",
                comboHoraInicio
        );

        comboHoraFin = crearComboHorarios();
        agregarFila(
                panel,
                restricciones,
                1,
                "Hora de finalización: *",
                comboHoraFin
        );

        comboDuracion = new JComboBox<>(
                new Integer[]{15, 30, 45, 60, 90, 120}
        );

        prepararCombo(comboDuracion);
        comboDuracion.setRenderer(
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

                        if (valor instanceof Integer) {
                            setText(valor + " minutos");
                        }

                        aplicarColoresRenderer(
                                this,
                                seleccionado
                        );

                        return this;
                    }
                }
        );

        agregarFila(
                panel,
                restricciones,
                2,
                "Duración habitual: *",
                comboDuracion
        );

        return panel;
    }

    private JComboBox<LocalTime> crearComboHorarios() {
        JComboBox<LocalTime> combo =
                new JComboBox<>();

        LocalTime hora = HORA_INICIAL;

        while (!hora.isAfter(HORA_FINAL)) {
            combo.addItem(hora);
            hora = hora.plusMinutes(30);
        }

        prepararCombo(combo);

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

                        if (valor instanceof LocalTime) {
                            LocalTime horaSeleccionada =
                                    (LocalTime) valor;

                            setText(
                                    String.format(
                                            "%02d:%02d",
                                            horaSeleccionada.getHour(),
                                            horaSeleccionada.getMinute()
                                    )
                            );
                        }

                        aplicarColoresRenderer(
                                this,
                                seleccionado
                        );

                        return this;
                    }
                }
        );

        return combo;
    }

    private void prepararCombo(
            JComboBox<?> combo) {

        combo.setFont(EstilosUI.FUENTE_NORMAL);
        combo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        combo.setBackground(EstilosUI.FONDO_SECUNDARIO);
        combo.setPreferredSize(new Dimension(230, 36));
    }

    private void aplicarColoresRenderer(
            DefaultListCellRenderer renderer,
            boolean seleccionado) {

        if (seleccionado) {
            renderer.setBackground(
                    EstilosUI.COLOR_PRIMARIO
            );
            renderer.setForeground(java.awt.Color.WHITE);
        } else {
            renderer.setBackground(
                    EstilosUI.FONDO_SECUNDARIO
            );
            renderer.setForeground(
                    EstilosUI.TEXTO_PRINCIPAL
            );
        }

        renderer.setBorder(
                new EmptyBorder(5, 8, 5, 8)
        );
    }

    private GridBagConstraints crearRestriccionesBase() {
        GridBagConstraints restricciones =
                new GridBagConstraints();

        restricciones.fill =
                GridBagConstraints.HORIZONTAL;

        restricciones.anchor =
                GridBagConstraints.WEST;

        return restricciones;
    }

    private void agregarFila(
            JPanel formulario,
            GridBagConstraints restricciones,
            int fila,
            String textoEtiqueta,
            JComponent componente) {

        JLabel etiqueta =
                EstilosUI.crearEtiqueta(
                        textoEtiqueta
                );

        etiqueta.setLabelFor(componente);

        restricciones.gridx = 0;
        restricciones.gridy = fila;
        restricciones.weightx = 0.0;
        restricciones.insets =
                new Insets(8, 0, 8, 18);

        formulario.add(etiqueta, restricciones);

        restricciones.gridx = 1;
        restricciones.weightx = 1.0;
        restricciones.insets =
                new Insets(8, 0, 8, 0);

        formulario.add(componente, restricciones);
    }

    private JPanel crearBotonera() {
        JPanel botonera = new JPanel(
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
                        "Guardar"
                );

        botonera.add(btnCancelar);
        botonera.add(btnGuardar);

        return botonera;
    }

    private void configurarEventos(
            String usuario) {

        btnGuardar.addActionListener(
                evento -> guardarOdontologo(usuario)
        );

        btnCancelar.addActionListener(
                evento -> cancelar(usuario)
        );

        txtNombre.addActionListener(
                evento -> txtApellido.requestFocusInWindow()
        );

        txtApellido.addActionListener(
                evento -> txtMatricula.requestFocusInWindow()
        );

        txtMatricula.addActionListener(
                evento -> txtEdad.requestFocusInWindow()
        );

        txtEdad.addActionListener(
                evento -> comboEspecialidad.requestFocusInWindow()
        );
    }

    private void establecerValoresPredeterminados() {
        comboEspecialidad.setSelectedItem(
                Especialidad.ODONTOLOGIA_GENERAL
        );

        comboHoraInicio.setSelectedItem(
                LocalTime.of(8, 0)
        );

        comboHoraFin.setSelectedItem(
                LocalTime.of(20, 0)
        );

        comboDuracion.setSelectedItem(30);

        seleccionarDia(DayOfWeek.MONDAY, true);
        seleccionarDia(DayOfWeek.TUESDAY, true);
        seleccionarDia(DayOfWeek.WEDNESDAY, true);
        seleccionarDia(DayOfWeek.THURSDAY, true);
        seleccionarDia(DayOfWeek.FRIDAY, true);
        seleccionarDia(DayOfWeek.SATURDAY, false);
        seleccionarDia(DayOfWeek.SUNDAY, false);
    }

    private void cargarDiasSeleccionados(
            Set<DayOfWeek> dias) {

        for (Map.Entry<DayOfWeek, JCheckBox> entrada :
                checksDias.entrySet()) {

            entrada.getValue().setSelected(
                    dias != null
                            && dias.contains(entrada.getKey())
            );
        }
    }

    private void seleccionarDia(
            DayOfWeek dia,
            boolean seleccionado) {

        JCheckBox check = checksDias.get(dia);

        if (check != null) {
            check.setSelected(seleccionado);
        }
    }

    private Set<DayOfWeek> obtenerDiasSeleccionados() {
        Set<DayOfWeek> dias =
                EnumSet.noneOf(DayOfWeek.class);

        for (Map.Entry<DayOfWeek, JCheckBox> entrada :
                checksDias.entrySet()) {

            if (entrada.getValue().isSelected()) {
                dias.add(entrada.getKey());
            }
        }

        return dias;
    }

    private void guardarOdontologo(
            String usuario) {

        try {
            Odontologo odontologo =
                    construirOdontologoDesdeFormulario();

            odontologoService.guardar(odontologo);

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeGuardado(),
                    "Datos guardados",
                    JOptionPane.INFORMATION_MESSAGE
            );

            panelManager.mostrarPanelListaOdontologos(
                    usuario
            );

        } catch (IllegalArgumentException exception) {
            mostrarError(exception.getMessage());

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudieron guardar los datos "
                            + "del odontólogo. "
                            + "Intente nuevamente."
            );
        }
    }

    private String obtenerMensajeGuardado() {
        if (id == 0) {
            return "El odontólogo y su agenda "
                    + "se guardaron correctamente.";
        }

        return "Los datos y la agenda del odontólogo "
                + "se actualizaron correctamente.";
    }

    private Odontologo
            construirOdontologoDesdeFormulario() {

        validarCamposCompletos();

        int matricula;
        int edad;

        try {
            matricula = Integer.parseInt(
                    txtMatricula.getText().trim()
            );

            edad = Integer.parseInt(
                    txtEdad.getText().trim()
            );

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "La matrícula y la edad deben "
                            + "contener solamente números."
            );
        }

        Odontologo odontologo = new Odontologo();

        odontologo.setId(id);
        odontologo.setNombre(
                txtNombre.getText().trim()
        );
        odontologo.setApellido(
                txtApellido.getText().trim()
        );
        odontologo.setMatricula(matricula);
        odontologo.setEdad(edad);
        odontologo.setEspecialidad(
                (Especialidad)
                        comboEspecialidad.getSelectedItem()
        );
        odontologo.setHoraInicio(
                (LocalTime)
                        comboHoraInicio.getSelectedItem()
        );
        odontologo.setHoraFin(
                (LocalTime)
                        comboHoraFin.getSelectedItem()
        );
        odontologo.setDuracionTurno(
                (Integer)
                        comboDuracion.getSelectedItem()
        );
        odontologo.setDiasAtencion(
                obtenerDiasSeleccionados()
        );

        return odontologo;
    }

    private void validarCamposCompletos() {
        if (estaVacio(txtNombre)) {
            txtNombre.requestFocusInWindow();
            throw new IllegalArgumentException(
                    "Debe ingresar el nombre."
            );
        }

        if (estaVacio(txtApellido)) {
            txtApellido.requestFocusInWindow();
            throw new IllegalArgumentException(
                    "Debe ingresar el apellido."
            );
        }

        if (estaVacio(txtMatricula)) {
            txtMatricula.requestFocusInWindow();
            throw new IllegalArgumentException(
                    "Debe ingresar la matrícula."
            );
        }

        if (estaVacio(txtEdad)) {
            txtEdad.requestFocusInWindow();
            throw new IllegalArgumentException(
                    "Debe ingresar la edad."
            );
        }
    }

    private boolean estaVacio(
            JTextField campo) {

        return campo.getText().trim().isEmpty();
    }

    private String valorSeguro(
            String valor) {

        return valor == null ? "" : valor;
    }

    private void cancelar(
            String usuario) {

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea salir sin guardar "
                                + "los cambios?",
                        "Cancelar edición",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        panelManager.mostrarPanelListaOdontologos(
                usuario
        );
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
