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

public class PanelPrincipalAdmin extends JPanel {

    private static final long serialVersionUID = 1L;

    private JButton btnPacientes;
    private JButton btnOdontologos;
    private JButton btnTurnos;
    private JButton btnCerrarSesion;

    private JMenuBar menuBar;

    private JMenu menuGestion;
    private JMenu menuRegistrar;
    private JMenu menuCuenta;

    private JMenuItem itemVerOdontologos;
    private JMenuItem itemVerPacientes;
    private JMenuItem itemVerTurnos;

    private JMenuItem itemNuevoOdontologo;
    private JMenuItem itemNuevoPaciente;
    private JMenuItem itemNuevoTurno;

    private JMenuItem itemCerrarSesion;

    private final PanelManager panelManager;

    public PanelPrincipalAdmin(
            PanelManager panelManager) {

        this.panelManager = panelManager;
    }

    public void armarPanelPrincipalAdmin(
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

        menuGestion =
                crearMenuEstilizado(
                        "Gestión"
                );

        menuRegistrar =
                crearMenuEstilizado(
                        "Registrar"
                );

        menuCuenta =
                crearMenuEstilizado(
                        "Cuenta"
                );

        itemVerPacientes =
                crearItemMenu(
                        "Ver pacientes"
                );

        itemVerOdontologos =
                crearItemMenu(
                        "Ver odontólogos"
                );

        itemVerTurnos =
                crearItemMenu(
                        "Ver turnos"
                );

        itemNuevoPaciente =
                crearItemMenu(
                        "Nuevo paciente"
                );

        itemNuevoOdontologo =
                crearItemMenu(
                        "Nuevo odontólogo"
                );

        itemNuevoTurno =
                crearItemMenu(
                        "Nuevo turno"
                );

        itemCerrarSesion =
                crearItemMenu(
                        "Cerrar sesión"
                );

        configurarAtajos();

        menuGestion.add(
                itemVerPacientes
        );

        menuGestion.add(
                itemVerOdontologos
        );

        menuGestion.add(
                itemVerTurnos
        );

        menuRegistrar.add(
                itemNuevoPaciente
        );

        menuRegistrar.add(
                itemNuevoOdontologo
        );

        menuRegistrar.add(
                itemNuevoTurno
        );

        menuCuenta.add(
                itemCerrarSesion
        );

        barra.add(
                menuGestion
        );

        barra.add(
                menuRegistrar
        );

        barra.add(
                menuCuenta
        );

        configurarEventosMenu(usuario);

        return barra;
    }

    private void configurarAtajos() {
        itemVerPacientes.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_P,
                        InputEvent.ALT_DOWN_MASK
                )
        );

        itemVerOdontologos.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_O,
                        InputEvent.ALT_DOWN_MASK
                )
        );

        itemVerTurnos.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_T,
                        InputEvent.ALT_DOWN_MASK
                )
        );

        itemNuevoPaciente.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_P,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        itemNuevoOdontologo.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_O,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        itemNuevoTurno.setAccelerator(
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
    }

    private void configurarEventosMenu(
            String usuario) {

        itemVerPacientes.addActionListener(
                evento ->
                        abrirPacientes(usuario)
        );

        itemVerOdontologos.addActionListener(
                evento ->
                        abrirOdontologos(usuario)
        );

        itemVerTurnos.addActionListener(
                evento ->
                        abrirTurnos(usuario)
        );

        itemNuevoPaciente.addActionListener(
                evento ->
                        panelManager
                                .mostrarPanelFormularioPaciente(
                                        usuario
                                )
        );

        itemNuevoOdontologo.addActionListener(
                evento ->
                        panelManager
                                .mostrarPanelFormularioOdontologo(
                                        usuario
                                )
        );

        itemNuevoTurno.addActionListener(
                evento ->
                        crearTurno(usuario)
        );

        itemCerrarSesion.addActionListener(
                evento ->
                        cerrarSesion()
        );
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

        JLabel titulo =
                new JLabel(
                        "Panel de administración"
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
                        "Gestione pacientes, "
                                + "odontólogos y turnos"
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

        JLabel sesion =
                new JLabel(
                        "Sesión administrativa: "
                                + usuario
                );

        sesion.setFont(
                EstilosUI.FUENTE_NORMAL
        );

        sesion.setForeground(
                EstilosUI.TEXTO_SECUNDARIO
        );

        sesion.setAlignmentX(
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

        encabezado.add(sesion);

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

        btnPacientes =
                crearTarjetaAccion(
                        "Pacientes",
                        "Registrar, consultar, modificar "
                                + "y administrar pacientes",
                        EstilosUI.COLOR_PRIMARIO
                );

        btnOdontologos =
                crearTarjetaAccion(
                        "Odontólogos",
                        "Registrar profesionales y "
                                + "administrar sus datos",
                        EstilosUI.COLOR_EXITO
                );

        btnTurnos =
                crearTarjetaAccion(
                        "Turnos",
                        "Consultar reservas, modificar "
                                + "horarios y cambiar estados",
                        new Color(
                                117,
                                83,
                                190
                        )
                );

        btnCerrarSesion =
                crearTarjetaAccion(
                        "Cerrar sesión",
                        "Finalizar la sesión administrativa "
                                + "y volver al inicio",
                        EstilosUI.COLOR_PELIGRO
                );

        panelAcciones.add(
                btnPacientes
        );

        panelAcciones.add(
                btnOdontologos
        );

        panelAcciones.add(
                btnTurnos
        );

        panelAcciones.add(
                btnCerrarSesion
        );

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

        configurarEventosBotones(usuario);

        return contenedor;
    }

    private void configurarEventosBotones(
            String usuario) {

        btnPacientes.addActionListener(
                evento ->
                        abrirPacientes(usuario)
        );

        btnOdontologos.addActionListener(
                evento ->
                        abrirOdontologos(usuario)
        );

        btnTurnos.addActionListener(
                evento ->
                        abrirTurnos(usuario)
        );

        btnCerrarSesion.addActionListener(
                evento ->
                        cerrarSesion()
        );
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

        agregarHoverTarjeta(
                boton
        );

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

    private void abrirPacientes(
            String usuario) {

        panelManager
                .mostrarPanelListaPacientes(
                        usuario
                );
    }

    private void abrirOdontologos(
            String usuario) {

        panelManager
                .mostrarPanelListaOdontologos(
                        usuario
                );
    }

    private void abrirTurnos(
            String usuario) {

        panelManager
                .mostrarPanelListaTurnosAdmin(
                        usuario
                );
    }

    private void crearTurno(
            String usuario) {

        JOptionPane.showMessageDialog(
                this,
                "Seleccione primero el odontólogo "
                        + "y luego el paciente.",
                "Crear turno",
                JOptionPane.INFORMATION_MESSAGE
        );

        panelManager
                .mostrarPanelSeleccionOdontologo(
                        usuario
                );
    }

    private void cerrarSesion() {
        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea cerrar la sesión "
                                + "administrativa?",
                        "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta
                == JOptionPane.YES_OPTION) {

            panelManager.mostrarPanelLogin();
        }
    }
}