package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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

import negocio.Odontologo;
import servicio.OdontologoService;

public class PanelFormularioOdontologo extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel lblTitulo;
    private JLabel lblSubtitulo;

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtMatricula;
    private JTextField txtEdad;

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

        setLayout(
                new GridBagLayout()
        );

        setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        JPanel tarjeta =
                crearTarjetaFormulario();

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
            Odontologo odontologo) {

        if (odontologo == null) {
            return;
        }

        id = odontologo.getId();

        txtNombre.setText(
                valorSeguro(
                        odontologo.getNombre()
                )
        );

        txtApellido.setText(
                valorSeguro(
                        odontologo.getApellido()
                )
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

        actualizarTituloEdicion();

        SwingUtilities.invokeLater(
                () -> txtNombre
                        .requestFocusInWindow()
        );
    }

    private JPanel crearTarjetaFormulario() {
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
                        460
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
                crearEncabezado(),
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

        lblSubtitulo =
                new JLabel(
                        "Complete los datos del profesional"
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

    private void actualizarTituloEdicion() {
        if (lblTitulo == null
                || lblSubtitulo == null) {

            return;
        }

        lblTitulo.setText(
                "Modificar odontólogo"
        );

        lblSubtitulo.setText(
                "Actualice los datos "
                        + "del profesional seleccionado"
        );
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

        txtNombre =
                new JTextField(22);

        EstilosUI.prepararCampo(
                txtNombre
        );

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Nombre: *",
                txtNombre
        );

        txtApellido =
                new JTextField(22);

        EstilosUI.prepararCampo(
                txtApellido
        );

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Apellido: *",
                txtApellido
        );

        txtMatricula =
                new JTextField(22);

        EstilosUI.prepararCampo(
                txtMatricula
        );

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Matrícula: *",
                txtMatricula
        );

        txtEdad =
                new JTextField(22);

        EstilosUI.prepararCampo(
                txtEdad
        );

        agregarFila(
                formulario,
                restricciones,
                fila,
                "Edad: *",
                txtEdad
        );

        return formulario;
    }

    private void agregarFila(
            JPanel formulario,
            GridBagConstraints restricciones,
            int fila,
            String textoEtiqueta,
            JTextField campo) {

        JLabel etiqueta =
                EstilosUI.crearEtiqueta(
                        textoEtiqueta
                );

        etiqueta.setLabelFor(campo);

        restricciones.gridx = 0;
        restricciones.gridy = fila;
        restricciones.weightx = 0.0;

        restricciones.insets =
                new Insets(
                        9,
                        0,
                        9,
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
                        9,
                        0,
                        9,
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
                        guardarOdontologo(
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
                        txtMatricula
                                .requestFocusInWindow()
        );

        txtMatricula.addActionListener(
                evento ->
                        txtEdad
                                .requestFocusInWindow()
        );

        txtEdad.addActionListener(
                evento ->
                        guardarOdontologo(
                                usuario
                        )
        );
    }

    private void guardarOdontologo(
            String usuario) {

        try {
            Odontologo odontologo =
                    construirOdontologoDesdeFormulario();

            odontologoService.guardar(
                    odontologo
            );

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeGuardado(),
                    "Datos guardados",
                    JOptionPane.INFORMATION_MESSAGE
            );

            panelManager
                    .mostrarPanelListaOdontologos(
                            usuario
                    );

        } catch (IllegalArgumentException exception) {
            mostrarError(
                    exception.getMessage()
            );

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
            return "El odontólogo se guardó "
                    + "correctamente.";
        }

        return "Los datos del odontólogo "
                + "se actualizaron correctamente.";
    }

    private Odontologo
            construirOdontologoDesdeFormulario() {

        validarCamposCompletos();

        int matricula;
        int edad;

        try {
            matricula =
                    Integer.parseInt(
                            txtMatricula
                                    .getText()
                                    .trim()
                    );

            edad =
                    Integer.parseInt(
                            txtEdad
                                    .getText()
                                    .trim()
                    );

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "La matrícula y la edad deben "
                            + "contener solamente números."
            );
        }

        Odontologo odontologo =
                new Odontologo();

        odontologo.setId(id);

        odontologo.setNombre(
                txtNombre
                        .getText()
                        .trim()
        );

        odontologo.setApellido(
                txtApellido
                        .getText()
                        .trim()
        );

        odontologo.setMatricula(
                matricula
        );

        odontologo.setEdad(
                edad
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

        panelManager
                .mostrarPanelListaOdontologos(
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