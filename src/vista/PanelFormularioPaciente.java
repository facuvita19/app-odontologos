package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import negocio.Paciente;
import servicio.PacienteService;
import servicio.UsuarioService;

public class PanelFormularioPaciente extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
            );

    private JLabel lblTitulo;
    private JLabel lblSubtitulo;

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtDomicilio;
    private JTextField txtEdad;

    private JButton btnGuardar;
    private JButton btnCancelar;

    private long id;
    private boolean esAdministrador;

    private final PanelManager panelManager;
    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;

    public PanelFormularioPaciente(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.pacienteService =
                new PacienteService();

        this.usuarioService =
                new UsuarioService();
    }

    public void armarPanelFormulario(
            String usuario) {

        removeAll();

        esAdministrador =
                "admin".equalsIgnoreCase(
                        usuario
                );

        setLayout(
                new GridBagLayout()
        );

        setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        JPanel tarjeta =
                crearTarjetaFormulario(
                        usuario
                );

        GridBagConstraints restricciones =
                new GridBagConstraints();

        restricciones.gridx = 0;
        restricciones.gridy = 0;

        restricciones.weightx = 1.0;
        restricciones.weighty = 1.0;

        restricciones.anchor =
                GridBagConstraints.CENTER;

        restricciones.fill =
                GridBagConstraints.NONE;

        restricciones.insets =
                new Insets(
                        25,
                        25,
                        25,
                        25
                );

        add(
                tarjeta,
                restricciones
        );

        configurarEventos(usuario);

        revalidate();
        repaint();
        setVisible(true);

        SwingUtilities.invokeLater(
                () -> txtNombre
                        .requestFocusInWindow()
        );
    }

    public void llenarFormulario(
            Paciente paciente) {

        if (paciente == null) {
            return;
        }

        id = paciente.getId();

        txtNombre.setText(
                valorSeguro(
                        paciente.getNombre()
                )
        );

        txtApellido.setText(
                valorSeguro(
                        paciente.getApellido()
                )
        );

        txtDni.setText(
                Integer.toString(
                        paciente.getDni()
                )
        );

        txtDomicilio.setText(
                valorSeguro(
                        paciente.getDomicilio()
                )
        );

        txtEdad.setText(
                Integer.toString(
                        paciente.getEdad()
                )
        );

        actualizarTituloEdicion();

        SwingUtilities.invokeLater(
                () -> txtNombre
                        .requestFocusInWindow()
        );
    }

    private JPanel crearTarjetaFormulario(
            String usuario) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                22
                        )
                );

        tarjeta.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        tarjeta.setPreferredSize(
                new Dimension(
                        560,
                        520
                )
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                EstilosUI.BORDE
                        ),
                        new EmptyBorder(
                                28,
                                38,
                                28,
                                38
                        )
                )
        );

        tarjeta.add(
                crearEncabezado(usuario),
                BorderLayout.NORTH
        );

        tarjeta.add(
                crearFormulario(),
                BorderLayout.CENTER
        );

        tarjeta.add(
                crearBotonera(),
                BorderLayout.SOUTH
        );

        return tarjeta;
    }

    private JPanel crearEncabezado(
            String usuario) {

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
                        obtenerTituloInicial(
                                usuario
                        )
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
                        obtenerSubtituloInicial(
                                usuario
                        )
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

        encabezado.add(
                lblTitulo
        );

        encabezado.add(
                Box.createVerticalStrut(7)
        );

        encabezado.add(
                lblSubtitulo
        );

        return encabezado;
    }

    private String obtenerTituloInicial(
            String usuario) {

        if ("admin".equalsIgnoreCase(
                usuario)) {

            return "Nuevo paciente";
        }

        return "Mis datos";
    }

    private String obtenerSubtituloInicial(
            String usuario) {

        if ("admin".equalsIgnoreCase(
                usuario)) {

            return "Complete los datos de la ficha "
                    + "del nuevo paciente";
        }

        return "Complete o actualice "
                + "su información personal";
    }

    private void actualizarTituloEdicion() {
        if (lblTitulo == null
                || lblSubtitulo == null) {

            return;
        }

        if (esAdministrador) {
            lblTitulo.setText(
                    "Modificar paciente"
            );

            lblSubtitulo.setText(
                    "Actualice los datos "
                            + "de la ficha seleccionada"
            );

        } else {
            lblTitulo.setText(
                    "Mis datos"
            );

            lblSubtitulo.setText(
                    "Consulte o actualice "
                            + "su información personal"
            );
        }
    }

    private JPanel crearFormulario() {
        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setOpaque(false);

        GridBagConstraints restricciones =
                new GridBagConstraints();

        restricciones.insets =
                new Insets(
                        8,
                        0,
                        8,
                        0
                );

        restricciones.fill =
                GridBagConstraints.HORIZONTAL;

        restricciones.anchor =
                GridBagConstraints.WEST;

        restricciones.weightx = 0.0;

        int fila = 0;

        txtNombre = new JTextField(22);
        EstilosUI.prepararCampo(txtNombre);

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Nombre:",
                txtNombre,
                true
        );

        txtApellido = new JTextField(22);
        EstilosUI.prepararCampo(txtApellido);

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Apellido:",
                txtApellido,
                true
        );

        txtDni = new JTextField(22);
        EstilosUI.prepararCampo(txtDni);

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "DNI:",
                txtDni,
                true
        );

        txtDomicilio = new JTextField(22);
        EstilosUI.prepararCampo(
                txtDomicilio
        );

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Domicilio:",
                txtDomicilio,
                false
        );

        txtEdad = new JTextField(22);
        EstilosUI.prepararCampo(txtEdad);

        agregarFila(
                formulario,
                restricciones,
                fila,
                "Edad:",
                txtEdad,
                true
        );

        return formulario;
    }

    private void agregarFila(
            JPanel formulario,
            GridBagConstraints restricciones,
            int fila,
            String textoEtiqueta,
            JTextField campo,
            boolean obligatorio) {

        String textoFinal =
                obligatorio
                        ? textoEtiqueta + " *"
                        : textoEtiqueta;

        JLabel etiqueta =
                EstilosUI.crearEtiqueta(
                        textoFinal
                );

        etiqueta.setLabelFor(campo);

        restricciones.gridx = 0;
        restricciones.gridy = fila;
        restricciones.weightx = 0.0;
        restricciones.insets =
                new Insets(
                        8,
                        0,
                        8,
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
                        8,
                        0,
                        8,
                        0
                );

        formulario.add(
                campo,
                restricciones
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
                        "Guardar"
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
                        guardarPaciente(
                                usuario
                        )
        );

        btnCancelar.addActionListener(
                evento ->
                        cancelar(usuario)
        );

        txtNombre.addActionListener(
                evento ->
                        txtApellido
                                .requestFocusInWindow()
        );

        txtApellido.addActionListener(
                evento ->
                        txtDni
                                .requestFocusInWindow()
        );

        txtDni.addActionListener(
                evento ->
                        txtDomicilio
                                .requestFocusInWindow()
        );

        txtDomicilio.addActionListener(
                evento ->
                        txtEdad
                                .requestFocusInWindow()
        );

        txtEdad.addActionListener(
                evento ->
                        guardarPaciente(
                                usuario
                        )
        );
    }

    private void guardarPaciente(
            String usuario) {

        try {
            Paciente paciente =
                    construirPacienteDesdeFormulario();

            pacienteService.guardar(
                    paciente
            );

            if (!"admin".equalsIgnoreCase(
                    usuario)) {

                usuarioService.vincularPaciente(
                        usuario,
                        paciente.getId()
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeGuardado(),
                    "Datos guardados",
                    JOptionPane.INFORMATION_MESSAGE
            );

            regresarDespuesDeGuardar(
                    usuario
            );

        } catch (IllegalArgumentException exception) {
            mostrarError(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudieron guardar los datos. "
                            + "Intente nuevamente."
            );
        }
    }

    private String obtenerMensajeGuardado() {
        if (esAdministrador) {
            if (id == 0) {
                return "El paciente se guardó "
                        + "correctamente.";
            }

            return "Los datos del paciente "
                    + "se actualizaron correctamente.";
        }

        return "Sus datos se guardaron "
                + "correctamente.";
    }

    private Paciente
            construirPacienteDesdeFormulario() {

        validarCamposCompletos();

        int dni;
        int edad;

        try {
            dni = Integer.parseInt(
                    txtDni.getText().trim()
            );

            edad = Integer.parseInt(
                    txtEdad.getText().trim()
            );

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "El DNI y la edad deben contener "
                            + "solamente números."
            );
        }

        Paciente paciente =
                new Paciente();

        paciente.setId(id);

        paciente.setNombre(
                txtNombre.getText().trim()
        );

        paciente.setApellido(
                txtApellido.getText().trim()
        );

        paciente.setDni(dni);

        paciente.setDomicilio(
                txtDomicilio.getText().trim()
        );

        paciente.setEdad(edad);

        paciente.setFechaAlta(
                recuperarFechaAlta()
        );

        return paciente;
    }

    private String recuperarFechaAlta() {
        if (id == 0) {
            return LocalDate.now()
                    .format(FORMATO_FECHA);
        }

        Paciente pacienteExistente =
                pacienteService.buscar(id);

        if (pacienteExistente != null
                && pacienteExistente.getFechaAlta()
                        != null
                && !pacienteExistente
                        .getFechaAlta()
                        .trim()
                        .isEmpty()) {

            return pacienteExistente
                    .getFechaAlta();
        }

        return LocalDate.now()
                .format(FORMATO_FECHA);
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

        if (estaVacio(txtDni)) {
            txtDni.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe ingresar el DNI."
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

        return campo.getText()
                .trim()
                .isEmpty();
    }

    private String valorSeguro(
            String valor) {

        return valor == null
                ? ""
                : valor;
    }

    private void regresarDespuesDeGuardar(
            String usuario) {

        if ("admin".equalsIgnoreCase(
                usuario)) {

            panelManager
                    .mostrarPanelListaPacientes(
                            usuario
                    );

        } else {
            panelManager
                    .mostrarPanelPrincipalUsuario(
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
                        "Cancelar edición",
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
                    .mostrarPanelListaPacientes(
                            usuario
                    );

        } else {
            panelManager
                    .mostrarPanelPrincipalUsuario(
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