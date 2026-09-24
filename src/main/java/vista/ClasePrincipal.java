package vista;

import javax.swing.SwingUtilities;

public final class ClasePrincipal {

    private ClasePrincipal() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> {
                    PanelManager manager =
                            new PanelManager();

                    manager.armarManager();
                    manager.mostrarPanelLogin();
                    manager.showFrame();
                }
        );
    }
}
