package vista;

import javax.swing.*;
import java.awt.*;

public class PanelImagen extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
    private ImageIcon imagen;

    public PanelImagen(String nombre){
        this.nombre=nombre;
    }

    public void paint(Graphics g){
        Dimension tamanio = getSize();
        imagen = new ImageIcon(getClass().getResource(nombre));
        g.drawImage(imagen.getImage(),0,0,tamanio.width,tamanio.height,this);

        setOpaque(false);
        super.paint(g);
    }
}
