package vista;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import negocio.Odontologo;
import negocio.Paciente;
import negocio.Turno;

public class PanelManager extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frame;
    private PanelListaOdontologos panelListaOdontologos;
    private PanelFormularioOdontologo panelFormularioOdontologo;
    private PanelListaPacientes panelListaPacientes;
    private PanelFormularioPaciente panelFormularioPaciente;
    private PanelListaTurnosAdmin panelListaTurnosAdmin;
    private PanelListaTurnosUsuario panelListaTurnosUsuario;
    private PanelFormularioTurno panelFormularioTurno;
    private PanelLogin panelLogin;
    private PanelPrincipalAdmin panelPrincipalAdmin;
    private PanelPrincipalUsuario panelPrincipalUsuario;
    private PanelFormularioUsuario panelFormularioUsuario;
    private PanelSeleccionOdontologo panelSeleccionOdontologo;
    private PanelSeleccionPaciente panelSeleccionPaciente;


    public void armarManager() {
        frame = new JFrame(
                "Sistema de gestión odontológica"
        );

        frame.setSize(
                1000,
                650
        );

        frame.setMinimumSize(
                new Dimension(
                        850,
                        550
                )
        );

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(
                WindowConstants.EXIT_ON_CLOSE
        );

        frame.getContentPane().setBackground(
                EstilosUI.FONDO_PRINCIPAL
        );

        java.net.URL logoUrl =
                PanelManager.class.getResource(
                        "/images/logo.jpg"
                );

        if (logoUrl != null) {
            frame.setIconImage(
                    Toolkit.getDefaultToolkit()
                            .getImage(logoUrl)
            );
        }
    }
    public void mostrarPanelPrincipalUsuario(
            String usuario) {

        panelPrincipalUsuario =
                new PanelPrincipalUsuario(this);

        panelPrincipalUsuario
                .armarPanelPrincipalUsuario(usuario);

        mostrarPanel(panelPrincipalUsuario);
    }
    public void mostrarPanelPrincipalAdmin(
            String usuario) {

        panelPrincipalAdmin =
                new PanelPrincipalAdmin(this);

        panelPrincipalAdmin
                .armarPanelPrincipalAdmin(usuario);

        mostrarPanel(panelPrincipalAdmin);
    }
    public void mostrarPanelLogin() {
        panelLogin = new PanelLogin(this);
        panelLogin.armarPanelLogin();

        mostrarPanel(panelLogin);
    }

    public void mostrarPanelListaOdontologos(
            String usuario) {

        panelListaOdontologos =
                new PanelListaOdontologos(this);

        panelListaOdontologos
                .armarPanelListaOdontologos(usuario);

        mostrarPanel(panelListaOdontologos);
    }

    public void mostrarPanelListaTurnosAdmin(
            String usuario) {

        panelListaTurnosAdmin =
                new PanelListaTurnosAdmin(this);

        panelListaTurnosAdmin
                .armarPanelListaTurnos(usuario);

        mostrarPanel(panelListaTurnosAdmin);
    }

    public void mostrarPanelListaTurnosUsuario(
            String usuario) {

        panelListaTurnosUsuario =
                new PanelListaTurnosUsuario(this);

        panelListaTurnosUsuario
                .armarPanelListaTurnos(usuario);

        mostrarPanel(panelListaTurnosUsuario);
    }

    public void mostrarPanelListaPacientes(
            String usuario) {

        panelListaPacientes =
                new PanelListaPacientes(this);

        panelListaPacientes
                .armarPanelListaPacientes(usuario);

        mostrarPanel(panelListaPacientes);
    }

    public void mostrarPanelFormularioOdontologo(
            String usuario) {

        panelFormularioOdontologo =
                new PanelFormularioOdontologo(this);

        panelFormularioOdontologo
                .armarPanelFormulario(usuario);

        mostrarPanel(panelFormularioOdontologo);
    }

    public void mostrarPanelFormularioOdontologo(
            Odontologo odontologo,
            String usuario) {

        panelFormularioOdontologo =
                new PanelFormularioOdontologo(this);

        panelFormularioOdontologo
                .armarPanelFormulario(usuario);

        panelFormularioOdontologo
                .llenarFormulario(odontologo);

        mostrarPanel(panelFormularioOdontologo);
    }

    public void mostrarPanelFormularioTurno(
            String usuario) {

        panelFormularioTurno =
                new PanelFormularioTurno(this);

        panelFormularioTurno
                .armarPanelFormulario(usuario);

        mostrarPanel(panelFormularioTurno);
    }

    public void mostrarPanelFormularioTurno(
            Turno turno,
            String usuario) {

        panelFormularioTurno =
                new PanelFormularioTurno(this);

        panelFormularioTurno
                .armarPanelFormulario(usuario);

        panelFormularioTurno
                .llenarFormulario(turno);

        mostrarPanel(panelFormularioTurno);
    }

    public void mostrarPanelFormularioPaciente(
            String usuario) {

        panelFormularioPaciente =
                new PanelFormularioPaciente(this);

        panelFormularioPaciente
                .armarPanelFormulario(usuario);

        mostrarPanel(panelFormularioPaciente);
    }

    public void mostrarPanelFormularioPaciente(
            Paciente paciente,
            String usuario) {

        panelFormularioPaciente =
                new PanelFormularioPaciente(this);

        panelFormularioPaciente
                .armarPanelFormulario(usuario);

        panelFormularioPaciente
                .llenarFormulario(paciente);

        mostrarPanel(panelFormularioPaciente);
    }

    public void mostrarPanelFormularioUsuario() {

        panelFormularioUsuario =
                new PanelFormularioUsuario(this);

        panelFormularioUsuario
                .armarPanelFormulario();

        mostrarPanel(panelFormularioUsuario);
    }

    public void mostrarPanelSeleccionOdontologo(
            String usuario) {

        panelSeleccionOdontologo =
                new PanelSeleccionOdontologo(this);

        panelSeleccionOdontologo
                .armarPanelSeleccionOdontologo(usuario);

        mostrarPanel(panelSeleccionOdontologo);
    }

    public void mostrarPanelSeleccionPaciente(
            Turno turno,
            String usuario) {

        panelSeleccionPaciente =
                new PanelSeleccionPaciente(this);

        panelSeleccionPaciente
                .armarPanelSeleccionPaciente(
                        turno,
                        usuario
                );

        mostrarPanel(panelSeleccionPaciente);
    }
    
    private void mostrarPanel(JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    public void showFrame()
    {
        frame.setVisible(true);
    }

}
