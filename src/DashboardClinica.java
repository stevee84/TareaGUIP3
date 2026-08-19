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

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public DashboardClinica() {

        setTitle("Sistema de Gestión de Pacientes");
        setSize(1500, 930);
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
        principal.setBackground(Color.BLACK);

        principal.add(crearHeader(), BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Fila superior: tarjetas (izquierda, angosto) + tabla de pacientes (derecha, ancho)
        JPanel filaSuperior = new JPanel(new BorderLayout(15, 0));
        filaSuperior.setOpaque(false);
        filaSuperior.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlTarjetas = crearPanelInicio();
        pnlTarjetas.setPreferredSize(new Dimension(300, 220));
        filaSuperior.add(pnlTarjetas, BorderLayout.WEST);

        JPanel pnlTabla = crearPanelTablaPacientes();
        filaSuperior.add(pnlTabla, BorderLayout.CENTER);

        contenido.add(filaSuperior);
        contenido.add(Box.createVerticalStrut(15));

        // Formulario de registro
        JPanel pnlFormulario = crearPanelFormularioPacientes();
        pnlFormulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(pnlFormulario);

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        principal.add(scroll, BorderLayout.CENTER);

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
        JPanel panel = new JPanel();
        panel.add(new JLabel("Tarjetas - pendiente "));
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

        DefaultTableModel model = new DefaultTableModel(datos, columnas){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablaPacientes = new JTable(model);
        tablaPacientes.setRowHeight(30);
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

        return panel;
    }


    private JPanel crearPanelFormularioPacientes() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Formulario de pacientes - pendiente "));
        return panel;
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
