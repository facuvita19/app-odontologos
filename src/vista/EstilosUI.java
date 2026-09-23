package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public final class EstilosUI {

    public static final Color FONDO_PRINCIPAL =
            new Color(28, 32, 38);

    public static final Color FONDO_SECUNDARIO =
            new Color(39, 44, 52);

    public static final Color COLOR_PRIMARIO =
            new Color(40, 125, 220);

    public static final Color COLOR_PRIMARIO_HOVER =
            new Color(31, 105, 190);

    public static final Color COLOR_PELIGRO =
            new Color(190, 55, 55);

    public static final Color COLOR_PELIGRO_HOVER =
            new Color(160, 40, 40);

    public static final Color COLOR_EXITO =
            new Color(45, 145, 85);

    public static final Color TEXTO_PRINCIPAL =
            new Color(240, 242, 245);

    public static final Color TEXTO_SECUNDARIO =
            new Color(185, 190, 200);

    public static final Color BORDE =
            new Color(72, 78, 88);

    public static final Font FUENTE_TITULO =
            new Font(
                    Font.SANS_SERIF,
                    Font.BOLD,
                    24
            );

    public static final Font FUENTE_SUBTITULO =
            new Font(
                    Font.SANS_SERIF,
                    Font.BOLD,
                    16
            );

    public static final Font FUENTE_NORMAL =
            new Font(
                    Font.SANS_SERIF,
                    Font.PLAIN,
                    14
            );

    public static final Font FUENTE_BOTON =
            new Font(
                    Font.SANS_SERIF,
                    Font.BOLD,
                    13
            );

    private EstilosUI() {
    }

    public static JButton crearBotonPrimario(
            String texto) {

        JButton boton = crearBotonBase(texto);

        boton.setBackground(COLOR_PRIMARIO);

        agregarEfectoHover(
                boton,
                COLOR_PRIMARIO,
                COLOR_PRIMARIO_HOVER
        );

        return boton;
    }

    public static JButton crearBotonSecundario(
            String texto) {

        JButton boton = crearBotonBase(texto);

        boton.setBackground(FONDO_SECUNDARIO);
        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        new EmptyBorder(
                                8,
                                14,
                                8,
                                14
                        )
                )
        );

        agregarEfectoHover(
                boton,
                FONDO_SECUNDARIO,
                new Color(55, 61, 70)
        );

        return boton;
    }

    public static JButton crearBotonPeligro(
            String texto) {

        JButton boton = crearBotonBase(texto);

        boton.setBackground(COLOR_PELIGRO);

        agregarEfectoHover(
                boton,
                COLOR_PELIGRO,
                COLOR_PELIGRO_HOVER
        );

        return boton;
    }

    private static JButton crearBotonBase(
            String texto) {

        JButton boton = new JButton(texto);

        boton.setFont(FUENTE_BOTON);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setBorder(
                new EmptyBorder(
                        9,
                        15,
                        9,
                        15
                )
        );

        return boton;
    }

    private static void agregarEfectoHover(
            JButton boton,
            Color colorNormal,
            Color colorHover) {

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent evento) {

                        if (boton.isEnabled()) {
                            boton.setBackground(
                                    colorHover
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent evento) {

                        if (boton.isEnabled()) {
                            boton.setBackground(
                                    colorNormal
                            );
                        }
                    }
                }
        );
    }

    public static JLabel crearTitulo(
            String texto) {

        JLabel titulo = new JLabel(texto);

        titulo.setFont(FUENTE_TITULO);
        titulo.setForeground(TEXTO_PRINCIPAL);
        titulo.setBorder(
                new EmptyBorder(
                        15,
                        20,
                        15,
                        20
                )
        );

        return titulo;
    }

    public static JLabel crearEtiqueta(
            String texto) {

        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(FUENTE_NORMAL);
        etiqueta.setForeground(TEXTO_PRINCIPAL);

        return etiqueta;
    }

    public static void prepararCampo(
            JTextField campo) {

        campo.setFont(FUENTE_NORMAL);
        campo.setForeground(TEXTO_PRINCIPAL);
        campo.setBackground(FONDO_SECUNDARIO);
        campo.setCaretColor(Color.WHITE);

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        new EmptyBorder(
                                7,
                                8,
                                7,
                                8
                        )
                )
        );
    }

    public static void prepararTabla(
            JTable tabla) {

        tabla.setFont(FUENTE_NORMAL);
        tabla.setForeground(TEXTO_PRINCIPAL);
        tabla.setBackground(FONDO_SECUNDARIO);

        tabla.setSelectionForeground(Color.WHITE);
        tabla.setSelectionBackground(COLOR_PRIMARIO);

        tabla.setRowHeight(28);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(BORDE);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.getTableHeader().setFont(
                FUENTE_SUBTITULO
        );

        tabla.getTableHeader().setForeground(
                TEXTO_PRINCIPAL
        );

        tabla.getTableHeader().setBackground(
                FONDO_PRINCIPAL
        );

        tabla.getTableHeader().setReorderingAllowed(
                false
        );

        tabla.setAutoCreateRowSorter(true);
    }

    public static JPanel crearPanelBotonera() {
        JPanel panel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        10
                )
        );

        panel.setBackground(FONDO_PRINCIPAL);

        panel.setBorder(
                new EmptyBorder(
                        5,
                        10,
                        5,
                        10
                )
        );

        return panel;
    }
}
