package vista;

import javax.swing.JFrame;

public class ClasePrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private PanelManager manager;

    public static void main(String[] args) {
        ClasePrincipal ppal = new ClasePrincipal();
        ppal.iniciarManager();
        ppal.showFrame();
    }

    public void iniciarManager() {
        manager = new PanelManager();
        manager.armarManager();
        manager.mostrarPanelLogin();
    }

    public void showFrame() {
        manager.showFrame();
    }

}
