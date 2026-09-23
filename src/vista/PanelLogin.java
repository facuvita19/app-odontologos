package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import servicio.UsuarioService;

public class PanelLogin extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField txtUsuario;
    private JPasswordField txtPassword;

    private JButton btnAceptar;
    private JButton btnCrear;
    private JButton btnSalir;

    private final PanelManager panelManager;
    private final UsuarioService usuarioService;

    public PanelLogin(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.usuarioService = new UsuarioService();
    }

    public void armarPanelLogin() {
        removeAll();

        setLayout(new GridBagLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        JPanel panelLogin = crearPanelLogin();

        GridBagConstraints restricciones =
                new GridBagConstraints();

        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weightx = 1.0;
        restricciones.weighty = 1.0;
        restricciones.anchor = GridBagConstraints.CENTER;
        restricciones.fill = GridBagConstraints.NONE;

        add(panelLogin, restricciones);

        configurarEventos();

        revalidate();
        repaint();
        setVisible(true);

        SwingUtilities.invokeLater(
                () -> txtUsuario.requestFocusInWindow()
        );
    }

    private JPanel crearPanelLogin() {
        JPanel panelLogin = new JPanel(
                new BorderLayout(
                        0,
                        20
                )
        );

        panelLogin.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        panelLogin.setPreferredSize(
                new Dimension(
                        430,
                        390
                )
        );

        panelLogin.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                EstilosUI.BORDE,
                                1
                        ),
                        new EmptyBorder(
                                28,
                                38,
                                28,
                                38
                        )
                )
        );

        JPanel encabezado =
                crearEncabezado();

        JPanel formulario =
                crearFormulario();

        JPanel botonera =
                crearBotonera();

        panelLogin.add(
                encabezado,
                BorderLayout.NORTH
        );

        panelLogin.add(
                formulario,
                BorderLayout.CENTER
        );

        panelLogin.add(
                botonera,
                BorderLayout.SOUTH
        );

        return panelLogin;
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

        JLabel titulo = new JLabel(
                "Sistema odontológico"
        );

        titulo.setFont(
                EstilosUI.FUENTE_TITULO
        );

        titulo.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        titulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel subtitulo = new JLabel(
                "Ingrese sus credenciales para continuar"
        );

        subtitulo.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        subtitulo.setForeground(
                EstilosUI.TEXTO_SECUNDARIO
        );

        subtitulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        encabezado.add(titulo);
        encabezado.add(
                Box.createVerticalStrut(8)
        );
        encabezado.add(subtitulo);

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

        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weightx = 1.0;
        restricciones.fill =
                GridBagConstraints.HORIZONTAL;

        restricciones.insets =
                new Insets(
                        7,
                        0,
                        5,
                        0
                );

        JLabel lblUsuario =
                EstilosUI.crearEtiqueta(
                        "Usuario"
                );

        formulario.add(
                lblUsuario,
                restricciones
        );

        restricciones.gridy++;

        txtUsuario =
                new JTextField(20);

        EstilosUI.prepararCampo(
                txtUsuario
        );

        lblUsuario.setLabelFor(
                txtUsuario
        );

        formulario.add(
                txtUsuario,
                restricciones
        );

        restricciones.gridy++;

        restricciones.insets =
                new Insets(
                        14,
                        0,
                        5,
                        0
                );

        JLabel lblPassword =
                EstilosUI.crearEtiqueta(
                        "Contraseña"
                );

        formulario.add(
                lblPassword,
                restricciones
        );

        restricciones.gridy++;

        restricciones.insets =
                new Insets(
                        7,
                        0,
                        5,
                        0
                );

        txtPassword =
                new JPasswordField(20);

        EstilosUI.prepararCampo(
                txtPassword
        );

        lblPassword.setLabelFor(
                txtPassword
        );

        formulario.add(
                txtPassword,
                restricciones
        );

        restricciones.gridy++;
        restricciones.weighty = 1.0;

        formulario.add(
                Box.createVerticalGlue(),
                restricciones
        );

        return formulario;
    }

    private JPanel crearBotonera() {
        JPanel contenedor =
                new JPanel();

        contenedor.setLayout(
                new BoxLayout(
                        contenedor,
                        BoxLayout.Y_AXIS
                )
        );

        contenedor.setOpaque(false);

        btnAceptar =
                EstilosUI.crearBotonPrimario(
                        "Iniciar sesión"
                );

        btnAceptar.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        btnAceptar.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        JPanel botonesSecundarios =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                0
                        )
                );

        botonesSecundarios.setOpaque(false);

        btnCrear =
                EstilosUI.crearBotonSecundario(
                        "Crear usuario"
                );

        btnSalir =
                EstilosUI.crearBotonSecundario(
                        "Salir"
                );

        botonesSecundarios.add(btnCrear);
        botonesSecundarios.add(btnSalir);

        contenedor.add(btnAceptar);
        contenedor.add(
                Box.createVerticalStrut(12)
        );
        contenedor.add(botonesSecundarios);

        return contenedor;
    }

    private void configurarEventos() {
        btnAceptar.addActionListener(
                evento -> intentarIngreso()
        );

        btnCrear.addActionListener(
                evento ->
                        panelManager
                                .mostrarPanelFormularioUsuario()
        );

        btnSalir.addActionListener(
                evento -> confirmarSalida()
        );

        txtUsuario.addActionListener(
                evento ->
                        txtPassword
                                .requestFocusInWindow()
        );

        txtPassword.addActionListener(
                evento -> intentarIngreso()
        );
    }

    private void intentarIngreso() {
        String nombreUsuario =
                txtUsuario.getText().trim();

        char[] passwordChars =
                txtPassword.getPassword();

        try {
            validarCampos(
                    nombreUsuario,
                    passwordChars
            );

            String password =
                    new String(passwordChars);

            int resultado =
                    usuarioService.validarIngreso(
                            nombreUsuario,
                            password
                    );

            procesarResultadoIngreso(
                    resultado,
                    nombreUsuario
            );

        } catch (IllegalArgumentException exception) {
            mostrarError(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo iniciar sesión. "
                            + "Verifique la conexión "
                            + "e intente nuevamente."
            );

        } finally {
            Arrays.fill(
                    passwordChars,
                    '\0'
            );
        }
    }

    private void validarCampos(
            String nombreUsuario,
            char[] password) {

        if (nombreUsuario.isEmpty()) {
            txtUsuario.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe ingresar el nombre de usuario."
            );
        }

        if (password.length == 0) {
            txtPassword.requestFocusInWindow();

            throw new IllegalArgumentException(
                    "Debe ingresar la contraseña."
            );
        }
    }

    private void procesarResultadoIngreso(
            int resultado,
            String nombreUsuario) {

        switch (resultado) {
            case 0:
            case 1:
                mostrarError(
                        "El usuario o la contraseña "
                                + "son incorrectos."
                );

                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
                break;

            case 2:
                panelManager
                        .mostrarPanelPrincipalUsuario(
                                nombreUsuario
                        );
                break;

            case 3:
                panelManager
                        .mostrarPanelPrincipalAdmin(
                                nombreUsuario
                        );
                break;

            default:
                mostrarError(
                        "El resultado del inicio de sesión "
                                + "no es válido."
                );

                txtPassword.setText("");
                break;
        }
    }

    private void confirmarSalida() {
        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea cerrar la aplicación?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta
                == JOptionPane.YES_OPTION) {

            System.exit(0);
        }
    }

    private void mostrarError(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Acceso denegado",
                JOptionPane.ERROR_MESSAGE
        );
    }
}