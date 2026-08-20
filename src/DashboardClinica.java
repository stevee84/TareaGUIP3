import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardClinica extends JFrame {

    // =========================================================
    // COLORES
    // =========================================================
    private final Color COLOR_MENU = new Color(18, 56, 50);
    private final Color COLOR_MENU_ACTIVO = new Color(29, 158, 117);
    private final Color COLOR_FONDO = new Color(246, 250, 248);
    private final Color COLOR_VERDE = new Color(15, 110, 86);
    private final Color COLOR_AZUL = new Color(24, 95, 165);
    private final Color COLOR_NARANJA = new Color(133, 79, 11);
    private final Color COLOR_ROJO = new Color(153, 45, 45);
    private final Color COLOR_BORDE = new Color(216, 226, 222);

    // =========================================================
    // COMPONENTES
    // =========================================================
    private JLabel lblFechaHora;
    private JTextField txtBuscarGlobal;

    private DefaultTableModel modeloPacientes;
    private JTable tablaPacientes;
    private JLabel lblCardPacientes;

    private JTextField txtIdentificacion;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtEdad;
    private JTextArea txtObservaciones;
    private boolean editandoPaciente;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public DashboardClinica() {

        setTitle("Sistema de Gestión de Pacientes");
        setSize(1350, 820);
        setMinimumSize(new Dimension(1200, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        configurarLookAndFeel();

        add(crearMenuLateral(), BorderLayout.WEST);
        add(crearContenidoPrincipal(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        iniciarReloj();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // =========================================================
    // LOOK AND FEEL
    // =========================================================
    private void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo cargar Look & Feel.");
        }
    }

    // =========================================================
    // MENÚ LATERAL
    // =========================================================
    private JPanel crearMenuLateral() {

        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(170, 0));
        menu.setBackground(COLOR_MENU);
        menu.setLayout(new BorderLayout());

        // Perfil
        JPanel pnlPerfil = new JPanel();
        pnlPerfil.setOpaque(false);
        pnlPerfil.setLayout(new BoxLayout(pnlPerfil, BoxLayout.Y_AXIS));
        pnlPerfil.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel lblAvatar = new JLabel("●");
        lblAvatar.setFont(new Font("Arial", Font.BOLD, 55));
        lblAvatar.setForeground(new Color(93, 202, 165));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombre = new JLabel("<html><center>Dr. Steven Alvarado</center></html>");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRol = new JLabel("Administrador");
        lblRol.setForeground(new Color(185, 214, 205));
        lblRol.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlPerfil.add(lblAvatar);
        pnlPerfil.add(Box.createVerticalStrut(6));
        pnlPerfil.add(lblNombre);
        pnlPerfil.add(Box.createVerticalStrut(4));
        pnlPerfil.add(lblRol);

        menu.add(pnlPerfil, BorderLayout.NORTH);

        // Opciones
        JPanel opciones = new JPanel();
        opciones.setOpaque(false);
        opciones.setLayout(new BoxLayout(opciones, BoxLayout.Y_AXIS));
        opciones.setBorder(new EmptyBorder(5, 10, 5, 10));

        opciones.add(crearBotonMenu("⌂", "Inicio", true));
        opciones.add(Box.createVerticalStrut(4));

        opciones.add(crearBotonMenu("👤", "Pacientes", false));
        opciones.add(Box.createVerticalStrut(4));

        opciones.add(crearBotonMenu("📅", "Citas", false));
        opciones.add(Box.createVerticalStrut(4));

        opciones.add(crearBotonMenu("🩺", "Consultas", false));
        opciones.add(Box.createVerticalStrut(4));

        opciones.add(crearBotonMenu("⚙", "Configuración", false));

        menu.add(opciones, BorderLayout.CENTER);

        // Cerrar sesion
        JPanel panelSalir = new JPanel(new BorderLayout());
        panelSalir.setOpaque(false);
        panelSalir.setBorder(new EmptyBorder(10, 10, 15, 10));

        JButton btnSalir = crearBoton("⏻  Cerrar sesión", new Color(35, 65, 58), new Color(255, 130, 130));
        btnSalir.addActionListener(e -> cerrarSesion());

        panelSalir.add(btnSalir);
        menu.add(panelSalir, BorderLayout.SOUTH);

        return menu;
    }

    // =========================================================
    // CONTENIDO PRINCIPAL header con tarjetas/tabla y formulario,
    // =========================================================
    private JPanel crearContenidoPrincipal() {

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(COLOR_FONDO);

        principal.add(crearHeader(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 15));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Fila superior: tarjetas (izquierda, angosto) + tabla de pacientes (derecha, ancho)
        JPanel filaSuperior = new JPanel(new BorderLayout(15, 0));
        filaSuperior.setOpaque(false);

        JPanel pnlTarjetas = crearPanelInicio();
        pnlTarjetas.setPreferredSize(new Dimension(280, 200));
        filaSuperior.add(pnlTarjetas, BorderLayout.WEST);

        JPanel pnlTabla = crearPanelTablaPacientes();
        filaSuperior.add(pnlTabla, BorderLayout.CENTER);

        cuerpo.add(filaSuperior, BorderLayout.NORTH);

        // Formulario de registro (rellena el espacio restante)
        JPanel pnlFormulario = crearPanelFormularioPacientes();
        cuerpo.add(pnlFormulario, BorderLayout.CENTER);

        principal.add(cuerpo, BorderLayout.CENTER);

        return principal;
    }

    // =========================================================
    // HEADER (buscador global con la fecha/hora y notificacion)
    // =========================================================
    private JPanel crearHeader() {

        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDE),
                new EmptyBorder(15, 25, 15, 25)
        ));


        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        derecha.setOpaque(false);

        lblFechaHora = new JLabel();
        lblFechaHora.setForeground(COLOR_VERDE);
        lblFechaHora.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel lblNotificacion = new JLabel("🔔");
        lblNotificacion.setFont(new Font("SansSerif", Font.PLAIN, 18));

        derecha.add(lblFechaHora);
        derecha.add(lblNotificacion);

        header.add(derecha, BorderLayout.EAST);

        return header;
    }

    private JButton crearBotonMenu(String icono, String texto, boolean activo) {

        JButton boton = new JButton(icono + "   " + texto);

        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        boton.setPreferredSize(new Dimension(150, 48));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        boton.setForeground(Color.BLUE);
        boton.setBackground(activo ? COLOR_MENU_ACTIVO : COLOR_MENU);
        boton.setBorder(new EmptyBorder(0, 14, 0, 8));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!activo) {
                    boton.setBackground(new Color(28, 79, 68));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!activo) {
                    boton.setBackground(COLOR_MENU);
                }
            }
        });


        boton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Seleccionó: " + texto)
        );

        return boton;
    }

    // =========================================================
    // BARRA DE ESTADO
    // =========================================================
    private JPanel crearBarraEstado() {

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(10, 25, 10, 25)
        ));

        JLabel lblEstado = new JLabel("●  Conectado");
        lblEstado.setForeground(new Color(15, 110, 86));
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel lblDesarrollado = new JLabel("© 2026 - Sistema de Gestión de Pacientes");
        lblDesarrollado.setForeground(Color.GRAY);
        lblDesarrollado.setFont(new Font("SansSerif", Font.PLAIN, 13));

        barra.add(lblEstado, BorderLayout.WEST);
        barra.add(lblDesarrollado, BorderLayout.EAST);

        return barra;
    }

    // =========================================================
    // RELOJ
    // =========================================================
    private void iniciarReloj() {
        Timer timer = new Timer(1000, e -> actualizarFecha());
        timer.start();
        actualizarFecha();
    }

    private void actualizarFecha() {
        Locale locale = new Locale("es", "CR");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd 'de' MMMM 'de' yyyy   hh:mm:ss a", locale
        );
        lblFechaHora.setText(LocalDateTime.now().format(formatter));
    }

    // =========================================================
    // CERRAR SESIÓN
    // =========================================================
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar la sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    // =========================================================
    // UTILIDADES DE ESTILO
    // =========================================================
    private JPanel crearPanelRedondeado() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(COLOR_BORDE, 1, true));
        return panel;
    }

    private JButton crearBoton(String texto, Color fondo, Color letra) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBackground(fondo);
        boton.setForeground(letra);
        boton.setFocusPainted(false);
        boton.setBorder(new CompoundBorder(
                new LineBorder(new Color(
                        Math.max(fondo.getRed() - 20, 0),
                        Math.max(fondo.getGreen() - 20, 0),
                        Math.max(fondo.getBlue() - 20, 0)
                ), 1, true),
                new EmptyBorder(9, 14, 9, 14)
        ));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // =========================================================
    //  PENDIENTES
    // =========================================================


    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel pnlTarjetas = crearPanelTarjetas();
        panel.add(pnlTarjetas, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelTarjetas() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280, 200));

        panel.add(crearTarjetas("👤", "156", "Pacientes", COLOR_VERDE));
        panel.add(crearTarjetas("📅", "12", "Citas hoy", COLOR_AZUL));
        panel.add(crearTarjetas("🩺", "8", "Consultas", COLOR_NARANJA));
        panel.add(crearTarjetas("❌", "3", "Canceladas", COLOR_ROJO));

        return panel;
    }

    private JPanel crearTarjetas(String icono, String valor, String descripcion, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 4, 0, 0, color),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblicono = new JLabel(icono);
        lblicono.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblicono.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        if ("Pacientes".equals(descripcion)) {
            lblCardPacientes = lblValor;
        }

        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDescripcion.setForeground(Color.GRAY);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(lblicono);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblValor);
        panel.add(Box.createVerticalStrut(2));
        panel.add(lblDescripcion);
        panel.add(Box.createVerticalGlue());

        return panel;
    }
    private JPanel crearPanelTablaPacientes() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        JLabel lblPacientes = new JLabel("Pacientes");
        lblPacientes.setForeground(COLOR_MENU);
        lblPacientes.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(lblPacientes, BorderLayout.NORTH);

        //Datos Quemados
        String[] columnas = {"ID", "Nombre", "Teléfono", "Edad"};
        Object[][] datos = {
                {1, "Scott Marchena", "48946167", 20},
                {2, "Steven Moya", "65898945", 21},
                {3, "Joshua Cabrera", "66349784", 20}
        };

        modeloPacientes = new DefaultTableModel(datos, columnas){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPacientes = new JTable(modeloPacientes);
        tablaPacientes.setFillsViewportHeight(true);
        tablaPacientes.setRowHeight(32);
        tablaPacientes.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaPacientes.setSelectionBackground(new Color(215, 240, 230));
        tablaPacientes.setSelectionForeground(Color.BLACK);
        tablaPacientes.setShowGrid(false);
        tablaPacientes.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane scroll = new JScrollPane(tablaPacientes);

        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        actualizarCardPacientes();

        return panel;
    }


    private JPanel crearPanelFormularioPacientes() {
        JPanel panel = crearPanelRedondeado();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(18, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel("📝  Registro de Pacientes");
        lblTitulo.setForeground(COLOR_MENU);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        txtIdentificacion = crearCampoTexto();
        txtNombre = crearCampoTexto();
        txtTelefono = crearCampoTexto();
        txtEmail = crearCampoTexto();
        txtEdad = crearCampoTexto();

        JPanel fila1 = new JPanel(new GridLayout(1, 2, 18, 0));
        fila1.setOpaque(false);
        fila1.add(crearCampo("Identificación", txtIdentificacion));
        fila1.add(crearCampo("Nombre completo", txtNombre));
        centro.add(fila1);
        centro.add(Box.createVerticalStrut(14));

        JPanel fila2 = new JPanel(new GridLayout(1, 2, 18, 0));
        fila2.setOpaque(false);
        fila2.add(crearCampo("Teléfono", txtTelefono));
        fila2.add(crearCampo("Email", txtEmail));
        centro.add(fila2);
        centro.add(Box.createVerticalStrut(14));

        JPanel fila3 = new JPanel(new GridLayout(1, 2, 18, 0));
        fila3.setOpaque(false);
        fila3.add(crearCampo("Edad", txtEdad));

        txtObservaciones = new JTextArea(5, 1);
        txtObservaciones.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setBorder(BorderFactory.createEmptyBorder());
        fila3.add(crearCampo("Observaciones", scrollObs));

        centro.add(fila3);
        centro.add(Box.createVerticalGlue());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton btnNuevo = crearBoton("＋  Nuevo", COLOR_AZUL, new Color(35, 45, 42));
        btnNuevo.addActionListener(e -> nuevoPaciente());
        JButton btnGuardar = crearBoton("💾  Guardar", COLOR_VERDE, new Color(35, 45, 42));
        btnGuardar.addActionListener(e -> guardarPaciente());
        JButton btnEditar = crearBoton("✎  Editar", COLOR_NARANJA, new Color(35, 45, 42));
        btnEditar.addActionListener(e -> editarPaciente());
        JButton btnEliminar = crearBoton("🗑  Eliminar", COLOR_ROJO, new Color(35, 45, 42));
        btnEliminar.addActionListener(e -> eliminarPaciente());
        JButton btnLimpiar = crearBoton("↺  Limpiar", new Color(102, 112, 108), new Color(35, 45, 42));
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        panel.add(centro, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearCampo(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(90, 100, 96));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(9, 10, 9, 10)
        ));
        return campo;
    }

    // =========================================================
    // ACCIONES DEL FORMULARIO
    // =========================================================
    private void nuevoPaciente() {
        limpiarFormulario();
        txtIdentificacion.requestFocusInWindow();
    }

    private void limpiarFormulario() {
        txtIdentificacion.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtEdad.setText("");
        txtObservaciones.setText("");
        editandoPaciente = false;
        if (tablaPacientes != null) {
            tablaPacientes.clearSelection();
        }
    }

    private void guardarPaciente() {
        String identificacion = txtIdentificacion.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String edadTexto = txtEdad.getText().trim();

        if (identificacion.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La identificación y el nombre son obligatorios.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int edad;
        try {
            edad = edadTexto.isEmpty() ? 0 : Integer.parseInt(edadTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "La edad debe ser un número válido.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editandoPaciente) {
            int fila = tablaPacientes.getSelectedRow();
            if (fila >= 0) {
                modeloPacientes.setValueAt(identificacion, fila, 0);
                modeloPacientes.setValueAt(nombre, fila, 1);
                modeloPacientes.setValueAt(telefono, fila, 2);
                modeloPacientes.setValueAt(edad, fila, 3);
                JOptionPane.showMessageDialog(this,
                        "Paciente actualizado correctamente.",
                        "Actualizar", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            modeloPacientes.addRow(new Object[]{
                    siguienteIdPaciente(), nombre, telefono, edad
            });
            JOptionPane.showMessageDialog(this,
                    "Paciente registrado correctamente.",
                    "Registrar", JOptionPane.INFORMATION_MESSAGE);
        }
        actualizarCardPacientes();
        limpiarFormulario();
    }

    private int siguienteIdPaciente() {
        int max = 0;
        for (int i = 0; i < modeloPacientes.getRowCount(); i++) {
            Object valor = modeloPacientes.getValueAt(i, 0);
            if (valor instanceof Number numero) {
                max = Math.max(max, numero.intValue());
            } else {
                try {
                    max = Math.max(max, Integer.parseInt(String.valueOf(valor)));
                } catch (NumberFormatException ignorado) {
                    // se ignora
                }
            }
        }
        return max + 1;
    }

    private void editarPaciente() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un paciente de la tabla para editar.",
                    "Editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtIdentificacion.setText(String.valueOf(modeloPacientes.getValueAt(fila, 0)));
        txtNombre.setText(String.valueOf(modeloPacientes.getValueAt(fila, 1)));
        txtTelefono.setText(String.valueOf(modeloPacientes.getValueAt(fila, 2)));
        txtEdad.setText(String.valueOf(modeloPacientes.getValueAt(fila, 3)));
        txtEmail.setText("");
        txtObservaciones.setText("");
        editandoPaciente = true;
        txtIdentificacion.requestFocusInWindow();
    }

    private void eliminarPaciente() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un paciente de la tabla para eliminar.",
                    "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el paciente seleccionado?",
                "Eliminar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            modeloPacientes.removeRow(fila);
            actualizarCardPacientes();
            limpiarFormulario();
        }
    }

    private void actualizarCardPacientes() {
        if (lblCardPacientes != null && modeloPacientes != null) {
            lblCardPacientes.setText(String.valueOf(modeloPacientes.getRowCount()));
        }
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardClinica ventana = new DashboardClinica();
        });
    }
}
