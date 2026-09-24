package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import negocio.EstadoTurno;
import negocio.Odontologo;
import negocio.Turno;
import servicio.OdontologoService;
import servicio.TurnoService;

public class PanelFormularioTurno extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int ANIOS_DISPONIBLES = 3;

    private JLabel lblTitulo;
    private JLabel lblOdontologo;
    private JLabel lblPaciente;
    private JLabel lblEspecialidad;
    private JLabel lblAgenda;
    private JLabel lblEstado;
    private JLabel lblHoraFin;
    private JLabel lblDisponibilidad;

    private JComboBox<Integer> comboDia;
    private JComboBox<Integer> comboMes;
    private JComboBox<Integer> comboAnio;
    private JComboBox<LocalTime> comboHorario;
    private JTextArea txtMotivoConsulta;
    private JTextArea txtObservaciones;

    private JButton btnGuardar;
    private JButton btnCancelar;

    private long id;
    private long odontologoId;
    private long pacienteId;
    private long usuarioId;
    private String nombreOdontologo;
    private String nombrePaciente;
    private EstadoTurno estado = EstadoTurno.PENDIENTE;
    private Odontologo odontologoSeleccionado;
    private boolean administrador;
    private boolean actualizandoFecha;

    private final PanelManager panelManager;
    private final TurnoService turnoService;
    private final OdontologoService odontologoService;

    public PanelFormularioTurno(PanelManager panelManager) {
        this.panelManager = panelManager;
        this.turnoService = new TurnoService();
        this.odontologoService = new OdontologoService();
    }

    public void armarPanelFormulario(String usuario) {
        removeAll();
        administrador = "admin".equalsIgnoreCase(usuario);
        setLayout(new BorderLayout());
        setBackground(EstilosUI.FONDO_PRINCIPAL);

        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(EstilosUI.FONDO_PRINCIPAL);
        contenedor.setBorder(new EmptyBorder(25, 60, 25, 60));

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weightx = 1.0;
        restricciones.weighty = 1.0;
        restricciones.fill = GridBagConstraints.BOTH;
        contenedor.add(crearTarjeta(), restricciones);
        add(contenedor, BorderLayout.CENTER);

        configurarEventos(usuario);
        establecerFechaInicial();
        actualizarInformacion();

        revalidate();
        repaint();
        setVisible(true);
    }

    public void llenarFormulario(Turno turno) {
        if (turno == null) {
            return;
        }

        id = turno.getId();
        odontologoId = turno.getOdontologoId();
        pacienteId = turno.getPacienteId();
        usuarioId = turno.getUsuarioId();
        nombreOdontologo = turno.getNomOdontologo();
        nombrePaciente = turno.getNomPaciente();
        estado = turno.getEstado() == null
                ? EstadoTurno.PENDIENTE
                : turno.getEstado();

        txtMotivoConsulta.setText(valorSeguro(turno.getMotivoConsulta()));
        txtObservaciones.setText(valorSeguro(turno.getObservaciones()));

        cargarOdontologo();
        actualizandoFecha = true;
        seleccionarFecha(turno.getDia(), turno.getMes(), turno.getAño());
        actualizandoFecha = false;
        actualizarHorarios(turno.getHoraInicio());
        lblTitulo.setText("Modificar turno");
        actualizarInformacion();
    }

    private JPanel crearTarjeta() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 18));
        tarjeta.setBackground(EstilosUI.FONDO_SECUNDARIO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE),
                new EmptyBorder(26, 36, 26, 36)
        ));

        tarjeta.add(crearEncabezado(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(crearContenido());
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        tarjeta.add(scroll, BorderLayout.CENTER);
        tarjeta.add(crearBotonera(), BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        lblTitulo = new JLabel("Nuevo turno");
        lblTitulo.setFont(EstilosUI.FUENTE_TITULO);
        lblTitulo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Seleccione una fecha, un horario libre y complete el motivo"
        );
        subtitulo.setFont(EstilosUI.FUENTE_NORMAL);
        subtitulo.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(7));
        panel.add(subtitulo);
        return panel;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);

        JPanel informacion = crearPanelInformacion();
        JPanel formulario = crearFormulario();
        informacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        informacion.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                informacion.getPreferredSize().height
        ));
        formulario.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                formulario.getPreferredSize().height
        ));

        contenido.add(informacion);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(formulario);
        return contenido;
    }

    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EstilosUI.FONDO_PRINCIPAL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE),
                new EmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints r = new GridBagConstraints();
        r.gridx = 0;
        r.weightx = 1.0;
        r.fill = GridBagConstraints.HORIZONTAL;
        r.anchor = GridBagConstraints.WEST;

        lblOdontologo = crearDato("Odontólogo: sin seleccionar");
        lblPaciente = crearDato("Paciente: sin seleccionar");
        lblEspecialidad = crearDato("Especialidad: sin identificar");
        lblAgenda = crearDato("Agenda: sin identificar");
        lblEstado = crearDato("Estado: PENDIENTE");

        JLabel[] etiquetas = {
                lblOdontologo, lblPaciente, lblEspecialidad,
                lblAgenda, lblEstado
        };
        for (int i = 0; i < etiquetas.length; i++) {
            r.gridy = i;
            r.insets = new Insets(3, 0, 3, 0);
            panel.add(etiquetas[i], r);
        }
        return panel;
    }

    private JLabel crearDato(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(EstilosUI.FUENTE_NORMAL);
        etiqueta.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        return etiqueta;
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        GridBagConstraints r = new GridBagConstraints();
        r.fill = GridBagConstraints.HORIZONTAL;
        r.anchor = GridBagConstraints.WEST;

        crearCombosFecha();
        agregarFila(formulario, r, 0, "Fecha del turno: *", crearPanelFecha());

        comboHorario = new JComboBox<>();
        prepararComboHorario();
        agregarFila(formulario, r, 1, "Horario disponible: *", comboHorario);

        lblHoraFin = EstilosUI.crearEtiqueta("Finaliza: --:--");
        agregarFila(formulario, r, 2, "Duración del turno:", lblHoraFin);

        lblDisponibilidad = new JLabel(
                "Seleccione una fecha para consultar disponibilidad."
        );
        lblDisponibilidad.setFont(EstilosUI.FUENTE_NORMAL);
        lblDisponibilidad.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        agregarFila(formulario, r, 3, "Disponibilidad:", lblDisponibilidad);

        txtMotivoConsulta = crearAreaTexto(
                "Describa brevemente el motivo de la consulta"
        );
        agregarFila(
                formulario,
                r,
                4,
                "Motivo de consulta:",
                crearScrollArea(txtMotivoConsulta)
        );

        txtObservaciones = crearAreaTexto(
                "Observaciones internas del administrador"
        );
        if (administrador) {
            agregarFila(
                    formulario,
                    r,
                    5,
                    "Observaciones administrativas:",
                    crearScrollArea(txtObservaciones)
            );
        }
        return formulario;
    }

    private JTextArea crearAreaTexto(String ayuda) {
        JTextArea area = new JTextArea(3, 28);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(EstilosUI.FUENTE_NORMAL);
        area.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        area.setBackground(EstilosUI.FONDO_SECUNDARIO);
        area.setCaretColor(EstilosUI.TEXTO_PRINCIPAL);
        area.setToolTipText(ayuda);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
        return area;
    }

    private JScrollPane crearScrollArea(JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(330, 78));
        scroll.setBorder(BorderFactory.createLineBorder(EstilosUI.BORDE));
        return scroll;
    }

    private void crearCombosFecha() {
        comboDia = new JComboBox<>();
        comboMes = new JComboBox<>();
        comboAnio = new JComboBox<>();

        for (int dia = 1; dia <= 31; dia++) {
            comboDia.addItem(dia);
        }
        for (int mes = 1; mes <= 12; mes++) {
            comboMes.addItem(mes);
        }
        int anioActual = LocalDate.now().getYear();
        for (int anio = anioActual;
                anio <= anioActual + ANIOS_DISPONIBLES;
                anio++) {
            comboAnio.addItem(anio);
        }

        prepararComboSimple(comboDia);
        prepararComboSimple(comboMes);
        prepararComboSimple(comboAnio);
    }

    private JPanel crearPanelFecha() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        panel.add(comboDia);
        panel.add(comboMes);
        panel.add(comboAnio);
        return panel;
    }

    private void prepararComboSimple(JComboBox<?> combo) {
        combo.setFont(EstilosUI.FUENTE_NORMAL);
        combo.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        combo.setBackground(EstilosUI.FONDO_SECUNDARIO);
        combo.setPreferredSize(new Dimension(90, 36));
    }

    private void prepararComboHorario() {
        comboHorario.setFont(EstilosUI.FUENTE_NORMAL);
        comboHorario.setForeground(EstilosUI.TEXTO_PRINCIPAL);
        comboHorario.setBackground(EstilosUI.FONDO_SECUNDARIO);
        comboHorario.setPreferredSize(new Dimension(220, 36));
        comboHorario.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean tieneFoco) {

                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, tieneFoco
                );
                if (valor instanceof LocalTime) {
                    setText(formatearHora((LocalTime) valor));
                }
                setBackground(seleccionado
                        ? EstilosUI.COLOR_PRIMARIO
                        : EstilosUI.FONDO_SECUNDARIO);
                setForeground(seleccionado
                        ? Color.WHITE
                        : EstilosUI.TEXTO_PRINCIPAL);
                setBorder(new EmptyBorder(5, 8, 5, 8));
                return this;
            }
        });
    }

    private void agregarFila(
            JPanel panel,
            GridBagConstraints r,
            int fila,
            String nombre,
            JComponent componente) {

        JLabel etiqueta = EstilosUI.crearEtiqueta(nombre);
        etiqueta.setLabelFor(componente);

        r.gridx = 0;
        r.gridy = fila;
        r.weightx = 0.0;
        r.insets = new Insets(8, 0, 8, 18);
        panel.add(etiqueta, r);

        r.gridx = 1;
        r.weightx = 1.0;
        r.insets = new Insets(8, 0, 8, 0);
        panel.add(componente, r);
    }

    private JPanel crearBotonera() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        panel.setOpaque(false);
        btnCancelar = EstilosUI.crearBotonSecundario("Cancelar");
        btnGuardar = EstilosUI.crearBotonPrimario("Guardar turno");
        panel.add(btnCancelar);
        panel.add(btnGuardar);
        return panel;
    }

    private void configurarEventos(String usuario) {
        btnGuardar.addActionListener(evento -> guardarTurno(usuario));
        btnCancelar.addActionListener(evento -> cancelar(usuario));
        comboDia.addActionListener(evento -> fechaModificada());
        comboMes.addActionListener(evento -> fechaModificada());
        comboAnio.addActionListener(evento -> fechaModificada());
        comboHorario.addActionListener(evento -> actualizarHoraFinal());
    }

    private void establecerFechaInicial() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        actualizandoFecha = true;
        seleccionarFecha(
                fecha.getDayOfMonth(),
                fecha.getMonthValue(),
                fecha.getYear()
        );
        actualizandoFecha = false;
    }

    private void seleccionarFecha(int dia, int mes, int anio) {
        comboDia.setSelectedItem(dia);
        comboMes.setSelectedItem(mes);
        comboAnio.setSelectedItem(anio);
    }

    private void fechaModificada() {
        if (!actualizandoFecha && odontologoId > 0) {
            actualizarHorarios(null);
        }
    }

    private LocalDate obtenerFecha() {
        try {
            return LocalDate.of(
                    (Integer) comboAnio.getSelectedItem(),
                    (Integer) comboMes.getSelectedItem(),
                    (Integer) comboDia.getSelectedItem()
            );
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private void cargarOdontologo() {
        if (odontologoId > 0) {
            odontologoSeleccionado = odontologoService.buscar(odontologoId);
        }
    }

    private void actualizarHorarios(LocalTime preferido) {
        comboHorario.removeAllItems();
        lblHoraFin.setText("Finaliza: --:--");
        LocalDate fecha = obtenerFecha();

        if (fecha == null) {
            mostrarDisponibilidad("La fecha no es válida.", true);
            btnGuardar.setEnabled(false);
            return;
        }

        try {
            List<LocalTime> horarios = turnoService.listarHorariosDisponibles(
                    odontologoId,
                    fecha,
                    id
            );
            for (LocalTime horario : horarios) {
                comboHorario.addItem(horario);
            }

            if (preferido != null && !horarios.contains(preferido)) {
                comboHorario.addItem(preferido);
            }
            if (preferido != null) {
                comboHorario.setSelectedItem(preferido);
            }

            boolean disponible = comboHorario.getItemCount() > 0;
            btnGuardar.setEnabled(disponible);
            if (disponible) {
                mostrarDisponibilidad(
                        comboHorario.getItemCount()
                                + " horarios disponibles.",
                        false
                );
                actualizarHoraFinal();
            } else {
                String mensaje = odontologoSeleccionado != null
                        && !odontologoSeleccionado.atiendeElDia(
                                fecha.getDayOfWeek()
                        )
                        ? "El odontólogo no atiende ese día."
                        : "No quedan horarios disponibles para esa fecha.";
                mostrarDisponibilidad(mensaje, true);
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            btnGuardar.setEnabled(false);
            mostrarDisponibilidad(
                    "No se pudo consultar la disponibilidad.",
                    true
            );
        }
    }

    private void mostrarDisponibilidad(String mensaje, boolean error) {
        lblDisponibilidad.setText(mensaje);
        lblDisponibilidad.setForeground(error
                ? EstilosUI.COLOR_PELIGRO
                : EstilosUI.COLOR_EXITO);
    }

    private void actualizarHoraFinal() {
        LocalTime inicio = (LocalTime) comboHorario.getSelectedItem();
        if (inicio == null || odontologoId <= 0) {
            lblHoraFin.setText("Finaliza: --:--");
            return;
        }
        LocalTime fin = turnoService.calcularHoraFin(odontologoId, inicio);
        lblHoraFin.setText("Finaliza: " + formatearHora(fin));
    }

    private void actualizarInformacion() {
        if (odontologoSeleccionado == null && odontologoId > 0) {
            cargarOdontologo();
        }

        lblOdontologo.setText(
                "Odontólogo: " + textoVisible(nombreOdontologo)
        );
        lblPaciente.setText(
                "Paciente: " + textoVisible(nombrePaciente)
        );
        lblEstado.setText(
                "Estado: " + (estado == null ? EstadoTurno.PENDIENTE : estado)
        );

        if (odontologoSeleccionado == null) {
            return;
        }

        lblEspecialidad.setText(
                "Especialidad: " + odontologoSeleccionado.getEspecialidad()
        );
        lblAgenda.setText(
                "Agenda: "
                        + formatearDias(odontologoSeleccionado.getDiasAtencion())
                        + " | "
                        + formatearHora(odontologoSeleccionado.getHoraInicio())
                        + " - "
                        + formatearHora(odontologoSeleccionado.getHoraFin())
                        + " | "
                        + odontologoSeleccionado.getDuracionTurno()
                        + " min"
        );
    }

    private String formatearDias(Set<DayOfWeek> dias) {
        if (dias == null || dias.isEmpty()) {
            return "Sin días";
        }
        StringBuilder texto = new StringBuilder();
        for (DayOfWeek dia : DayOfWeek.values()) {
            if (!dias.contains(dia)) {
                continue;
            }
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(abreviarDia(dia));
        }
        return texto.toString();
    }

    private String abreviarDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return "Lun";
            case TUESDAY: return "Mar";
            case WEDNESDAY: return "Mié";
            case THURSDAY: return "Jue";
            case FRIDAY: return "Vie";
            case SATURDAY: return "Sáb";
            case SUNDAY: return "Dom";
            default: return "";
        }
    }

    private void guardarTurno(String usuario) {
        try {
            Turno turno = construirTurno(usuario);
            turnoService.guardar(turno);
            JOptionPane.showMessageDialog(
                    this,
                    id == 0
                            ? "El turno se creó correctamente."
                            : "El turno se actualizó correctamente.",
                    "Turno guardado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            mostrarLista(usuario);
        } catch (IllegalArgumentException exception) {
            mostrarError(exception.getMessage());
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            mostrarError("No se pudo guardar el turno.");
        }
    }

    private Turno construirTurno(String usuario) {
        LocalDate fecha = obtenerFecha();
        LocalTime inicio = (LocalTime) comboHorario.getSelectedItem();

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha seleccionada no es válida."
            );
        }
        if (inicio == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un horario disponible."
            );
        }

        Turno turno = new Turno();
        turno.setId(id);
        turno.setOdontologoId(odontologoId);
        turno.setPacienteId(pacienteId);
        turno.setUsuarioId(usuarioId);
        turno.setDia(fecha.getDayOfMonth());
        turno.setMes(fecha.getMonthValue());
        turno.setAño(fecha.getYear());
        turno.setHoraInicio(inicio);
        turno.setHoraFin(turnoService.calcularHoraFin(odontologoId, inicio));
        turno.setNomOdontologo(nombreOdontologo);
        turno.setNomPaciente(nombrePaciente);
        turno.setNomUsuario(usuario);
        turno.setEstado(estado);
        turno.setMotivoConsulta(textoOpcional(txtMotivoConsulta.getText()));
        turno.setObservaciones(administrador
                ? textoOpcional(txtObservaciones.getText())
                : null);
        return turno;
    }

    private String textoOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String texto = valor.trim();
        if (texto.length() > 500) {
            throw new IllegalArgumentException(
                    "El motivo y las observaciones no pueden superar "
                            + "los 500 caracteres."
            );
        }
        return texto;
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private String textoVisible(String valor) {
        return valor == null || valor.trim().isEmpty()
                ? "sin seleccionar"
                : valor.trim();
    }

    private String formatearHora(LocalTime hora) {
        return hora == null
                ? "--:--"
                : String.format("%02d:%02d", hora.getHour(), hora.getMinute());
    }

    private void mostrarLista(String usuario) {
        if ("admin".equalsIgnoreCase(usuario)) {
            panelManager.mostrarPanelListaTurnosAdmin(usuario);
        } else {
            panelManager.mostrarPanelListaTurnosUsuario(usuario);
        }
    }

    private void cancelar(String usuario) {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea salir sin guardar los cambios?",
                "Cancelar turno",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (respuesta == JOptionPane.YES_OPTION) {
            mostrarLista(usuario);
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Datos incorrectos",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
