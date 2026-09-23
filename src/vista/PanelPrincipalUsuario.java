package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import negocio.Paciente;
import servicio.PacienteService;
import servicio.UsuarioService;

public class PanelPrincipalUsuario extends JPanel {

    private static final long serialVersionUID = 1L;

    private JButton btnMisDatos;
    private JButton btnReservarTurno;
    private JButton btnMisTurnos;
    private JButton btnCerrarSesion;

    private JMenuBar menuBar;
    private JMenu menuCuenta;
    private JMenu menuTurnos;

    private JMenuItem itemMisDatos;
    private JMenuItem itemNuevoTurno;
    private JMenuItem itemMisTurnos;
    private JMenuItem itemCerrarSesion;

    private final PanelManager panelManager;
    private final UsuarioService usuarioService;
    private final PacienteService pacienteService;

    public PanelPrincipalUsuario(
            PanelManager panelManager) {

        this.panelManager = panelManager;
        this.usuarioService = new UsuarioService();
        this.pacienteService = new PacienteService();
    }

    public void armarPanelPrincipalUsuario(
            String usuario) {

        removeAll();

        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        menuBar = crearMenu(usuario);

        JPanel encabezado =
                crearEncabezado(usuario);

        JPanel contenido =
                crearContenidoPrincipal(usuario);

        JPanel panelSuperior =
                new JPanel(
                        new BorderLayout()
                );

        panelSuperior.setOpaque(false);

        panelSuperior.add(
                menuBar,
                BorderLayout.NORTH
        );

        panelSuperior.add(
                encabezado,
                BorderLayout.CENTER
        );

        add(
                panelSuperior,
                BorderLayout.NORTH
        );

        add(
                contenido,
                BorderLayout.CENTER
        );

        revalidate();
        repaint();
        setVisible(true);
    }

    private JMenuBar crearMenu(
            String usuario) {

        JMenuBar barra = new JMenuBar();

        barra.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        barra.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        EstilosUI.BORDE
                )
        );

        menuCuenta = crearMenuEstilizado(
                "Cuenta"
        );

        menuTurnos = crearMenuEstilizado(
                "Turnos"
        );

        itemMisDatos = crearItemMenu(
                "Mis datos"
        );

        itemNuevoTurno = crearItemMenu(
                "Nuevo turno"
        );

        itemMisTurnos = crearItemMenu(
                "Mis turnos"
        );

        itemCerrarSesion = crearItemMenu(
                "Cerrar sesión"
        );

        itemMisDatos.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_D,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        itemNuevoTurno.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_N,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        itemMisTurnos.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_T,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        itemCerrarSesion.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_ESCAPE,
                        0
                )
        );

        menuCuenta.add(itemMisDatos);
        menuCuenta.addSeparator();
        menuCuenta.add(itemCerrarSesion);

        menuTurnos.add(itemNuevoTurno);
        menuTurnos.add(itemMisTurnos);

        barra.add(menuCuenta);
        barra.add(menuTurnos);

        itemMisDatos.addActionListener(
                evento -> abrirMisDatos(usuario)
        );

        itemNuevoTurno.addActionListener(
                evento -> reservarTurno(usuario)
        );

        itemMisTurnos.addActionListener(
                evento -> abrirMisTurnos(usuario)
        );

        itemCerrarSesion.addActionListener(
                evento -> cerrarSesion()
        );

        return barra;
    }

    private JMenu crearMenuEstilizado(
            String texto) {

        JMenu menu = new JMenu(texto);

        menu.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        menu.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        menu.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        return menu;
    }

    private JMenuItem crearItemMenu(
            String texto) {

        JMenuItem item =
                new JMenuItem(texto);

        item.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        item.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        item.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        return item;
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

        encabezado.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        encabezado.setBorder(
                new EmptyBorder(
                        30,
                        45,
                        20,
                        45
                )
        );

        JLabel titulo = new JLabel(
                "Bienvenido al sistema odontológico"
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
                "Sesión iniciada como: " + usuario
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
        encabezado.add(
                Box.createVerticalStrut(7)
        );
        encabezado.add(subtitulo);

        return encabezado;
    }

    private JPanel crearContenidoPrincipal(
            String usuario) {

        JPanel contenedor =
                new JPanel(
                        new GridBagLayout()
                );

        contenedor.setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        contenedor.setBorder(
                new EmptyBorder(
                        15,
                        45,
                        40,
                        45
                )
        );

        JPanel panelAcciones =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                18,
                                18
                        )
                );

        panelAcciones.setOpaque(false);

        btnMisDatos =
                crearTarjetaAccion(
                        "Mis datos",
                        "Consultar o modificar "
                                + "su información personal",
                        EstilosUI.COLOR_PRIMARIO
                );

        btnReservarTurno =
                crearTarjetaAccion(
                        "Reservar turno",
                        "Elegir odontólogo, fecha "
                                + "y horario de atención",
                        EstilosUI.COLOR_EXITO
                );

        btnMisTurnos =
                crearTarjetaAccion(
                        "Mis turnos",
                        "Consultar, modificar o cancelar "
                                + "sus reservas",
                        new Color(
                                117,
                                83,
                                190
                        )
                );

        btnCerrarSesion =
                crearTarjetaAccion(
                        "Cerrar sesión",
                        "Volver a la pantalla "
                                + "de inicio de sesión",
                        EstilosUI.COLOR_PELIGRO
                );

        panelAcciones.add(btnMisDatos);
        panelAcciones.add(btnReservarTurno);
        panelAcciones.add(btnMisTurnos);
        panelAcciones.add(btnCerrarSesion);

        GridBagConstraints restricciones =
                new GridBagConstraints();

        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weightx = 1.0;
        restricciones.weighty = 1.0;

        restricciones.fill =
                GridBagConstraints.BOTH;

        restricciones.insets =
                new Insets(
                        10,
                        10,
                        10,
                        10
                );

        contenedor.add(
                panelAcciones,
                restricciones
        );

        btnMisDatos.addActionListener(
                evento -> abrirMisDatos(usuario)
        );

        btnReservarTurno.addActionListener(
                evento -> reservarTurno(usuario)
        );

        btnMisTurnos.addActionListener(
                evento -> abrirMisTurnos(usuario)
        );

        btnCerrarSesion.addActionListener(
                evento -> cerrarSesion()
        );

        return contenedor;
    }

    private JButton crearTarjetaAccion(
            String titulo,
            String descripcion,
            Color colorAcento) {

        String textoHtml =
                "<html>"
                + "<div style='text-align:left;'>"
                + "<span style='font-size:17px;'>"
                + titulo
                + "</span>"
                + "<br><br>"
                + "<span style='font-size:12px;"
                + "font-weight:normal;'>"
                + descripcion
                + "</span>"
                + "</div>"
                + "</html>";

        JButton boton =
                new JButton(textoHtml);

        boton.setFont(
                EstilosUI.FUENTE_SUBTITULO
        );

        boton.setForeground(
                EstilosUI.TEXTO_PRINCIPAL
        );

        boton.setBackground(
                EstilosUI.FONDO_SECUNDARIO
        );

        boton.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        boton.setVerticalAlignment(
                SwingConstants.CENTER
        );

        boton.setFocusPainted(false);

        boton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setPreferredSize(
                new Dimension(
                        300,
                        150
                )
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                5,
                                0,
                                0,
                                colorAcento
                        ),
                        new EmptyBorder(
                                20,
                                22,
                                20,
                                22
                        )
                )
        );

        agregarHoverTarjeta(boton);

        return boton;
    }

    private void agregarHoverTarjeta(
            JButton boton) {

        Color colorNormal =
                EstilosUI.FONDO_SECUNDARIO;

        Color colorHover =
                new Color(
                        51,
                        57,
                        67
                );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent evento) {

                        boton.setBackground(
                                colorHover
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent evento) {

                        boton.setBackground(
                                colorNormal
                        );
                    }
                }
        );
    }

    private void reservarTurno(
            String usuario) {

        try {
            Long pacienteId =
                    usuarioService.buscarPacienteId(
                            usuario
                    );

            if (pacienteId == null) {
                int respuesta =
                        JOptionPane.showConfirmDialog(
                                this,
                                "Antes de reservar un turno "
                                        + "debe completar sus datos.\n"
                                        + "¿Desea hacerlo ahora?",
                                "Datos requeridos",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                if (respuesta
                        == JOptionPane.YES_OPTION) {

                    abrirMisDatos(usuario);
                }

                return;
            }

            panelManager
                    .mostrarPanelSeleccionOdontologo(
                            usuario
                    );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudo verificar "
                            + "su ficha de paciente."
            );
        }
    }

    private void abrirMisTurnos(
            String usuario) {

        panelManager
                .mostrarPanelListaTurnosUsuario(
                        usuario
                );
    }

    private void abrirMisDatos(
            String usuario) {

        try {
            Long pacienteId =
                    usuarioService.buscarPacienteId(
                            usuario
                    );

            if (pacienteId == null) {
                panelManager
                        .mostrarPanelFormularioPaciente(
                                usuario
                        );

                return;
            }

            Paciente paciente =
                    pacienteService.buscar(
                            pacienteId
                    );

            if (paciente == null) {
                mostrarError(
                        "La ficha vinculada no existe "
                                + "o se encuentra inactiva."
                );

                return;
            }

            panelManager
                    .mostrarPanelFormularioPaciente(
                            paciente,
                            usuario
                    );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            mostrarError(
                    "No se pudieron recuperar "
                            + "sus datos."
            );
        }
    }

    private void cerrarSesion() {
        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea cerrar la sesión actual?",
                        "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta
                == JOptionPane.YES_OPTION) {

            panelManager.mostrarPanelLogin();
        }
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