package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import negocio.Login;
import servicio.UsuarioService;

public class PanelFormularioUsuario extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField txtUsuario;

    private JPasswordField txtPassword;
    private JPasswordField txtPassword2;

    private JButton btnCrearCuenta;
    private JButton btnVolver;

    private final PanelManager panelManager;
    private final UsuarioService usuarioService;

    public PanelFormularioUsuario(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.usuarioService =
                new UsuarioService();
    }

    public void armarPanelFormulario() {
        removeAll();

        setLayout(
                new GridBagLayout()
        );

        setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        JPanel tarjeta =
                crearTarjetaRegistro();

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

        configurarEventos();

        revalidate();
        repaint();
        setVisible(true);

        SwingUtilities.invokeLater(
                () -> txtUsuario
                        .requestFocusInWindow()
        );
    }

    private JPanel crearTarjetaRegistro() {
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
                        520,
                        470
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

        JLabel titulo =
                new JLabel(
                        "Crear una cuenta"
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
                        "Complete los datos para acceder "
                                + "al sistema de turnos"
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

        JLabel indicacion =
                new JLabel(
                        "La contraseña debe tener "
                                + "al menos 6 caracteres"
                );

        indicacion.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.ITALIC,
                        12
                )
        );

        indicacion.setForeground(
                EstilosUI.TEXTO_SECUNDARIO
        );

        indicacion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        encabezado.add(titulo);

        encabezado.add(
                Box.createVerticalStrut(7)
        );

        encabezado.add(subtitulo);

        encabezado.add(
                Box.createVerticalStrut(5)
        );

        encabezado.add(indicacion);

        return encabezado;
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

        txtUsuario =
                new JTextField(22);

        EstilosUI.prepararCampo(
                txtUsuario
        );

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Nombre de usuario: *",
                txtUsuario
        );

        txtPassword =
                new JPasswordField(22);

        EstilosUI.prepararCampo(
                txtPassword
        );

        agregarFila(
                formulario,
                restricciones,
                fila++,
                "Contraseña: *",
                txtPassword
        );

        txtPassword2 =
                new JPasswordField(22);

        EstilosUI.prepararCampo(
                txtPassword2
        );

        agregarFila(
                formulario,
                restricciones,
                fila,
                "Repetir contraseña: *",
                txtPassword2
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

        btnVolver =
                EstilosUI.crearBotonSecundario(
                        "Volver al login"
                );

        btnCrearCuenta =
                EstilosUI.crearBotonPrimario(
                        "Crear cuenta"
                );

        botonera.add(
                btnVolver
        );

        botonera.add(
                btnCrearCuenta
        );

        return botonera;
    }

    private void configurarEventos() {
        btnCrearCuenta.addActionListener(
                evento -> registrarUsuario()
        );

        btnVolver.addActionListener(
                evento -> volverAlLogin()
        );

        txtUsuario.addActionListener(
                evento ->
                        txtPassword
                                .requestFocusInWindow()
        );

        txtPassword.addActionListener(
                evento ->
                        txtPassword2
                                .requestFocusInWindow()
        );

        txtPassword2.addActionListener(
                evento -> registrarUsuario()
        );
    }

    private void registrarUsuario() {
        String nombreUsuario =
                txtUsuario.getText().trim();

        char[] passwordChars =
                txtPassword.getPassword();

        char[] confirmacionChars =
                txtPassword2.getPassword();

        try {
            validarCampos(
                    nombreUsuario,
                    passwordChars,
                    confirmacionChars
            );

            Login usuario = new Login();

            usuario.setUsuario(
                    nombreUsuario
            );

            usuario.setPassword(
                    new String(passwordChars)
            );

            usuarioService.guardar(
                    usuario
            );

            JOptionPane.showMessageDialog(
                    this,
                    "La cuenta se creó correctamente.\n"
                            + "Ya puede iniciar sesión.",
                    "Cuenta creada",
                    JOptionPane.INFORMATION_MESSAGE
            );

            panelManager.mostrarPanelLogin();

        } catch (IllegalArgumentException exception) {
            mostrarError(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo crear la cuenta. "
                            + "Intente nuevamente."
            );

        } finally {
            Arrays.fill(
                    passwordChars,
                    '\0'
            );

            Arrays.fill(
                    confirmacionChars,
                    '\0'
            );
        }
    }

    private void validarCampos(
            String nombreUsuario,
            char[] password,
            char[] confirmacion) {

        if (nombreUsuario.isEmpty()) {
            txtUsuario.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe ingresar un nombre de usuario."
            );
        }

        if (password.length == 0) {
            txtPassword.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe ingresar una contraseña."
            );
        }

        if (confirmacion.length == 0) {
            txtPassword2.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe repetir la contraseña."
            );
        }

        if (!Arrays.equals(
                password,
                confirmacion)) {

            txtPassword.setText("");
            txtPassword2.setText("");

            txtPassword.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Las contraseñas no coinciden."
            );
        }
    }

    private void volverAlLogin() {
        boolean hayDatosEscritos =
                !txtUsuario.getText()
                        .trim()
                        .isEmpty()
                || txtPassword.getPassword()
                        .length > 0
                || txtPassword2.getPassword()
                        .length > 0;

        if (!hayDatosEscritos) {
            panelManager.mostrarPanelLogin();
            return;
        }

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea volver al login?\n"
                                + "Los datos ingresados "
                                + "no se guardarán.",
                        "Cancelar registro",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta
                == JOptionPane.YES_OPTION) {

            limpiarPasswords();
            panelManager.mostrarPanelLogin();
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

    private void limpiarPasswords() {
        char[] password =
                txtPassword.getPassword();

        char[] confirmacion =
                txtPassword2.getPassword();

        Arrays.fill(
                password,
                '\0'
        );

        Arrays.fill(
                confirmacion,
                '\0'
        );

        txtPassword.setText("");
        txtPassword2.setText("");
    }
}