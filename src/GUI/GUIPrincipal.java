package GUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Clases.AutoCompletion;
import Clases.GhostText;
import Clases.HiloNotificaciones;
import Clases.JPanelGradient;
import Clases.PDF;
import Clases.Render;
import Clases.Tabla;
import Clases.UpperCaseDocumentFilter;
import Clases.baseDeDatos;
import Clases.herramientasGUI;
import com.google.zxing.WriterException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author luix_
 */
public class GUIPrincipal extends javax.swing.JFrame {

    Dimension screenSize;
    
    //Fuentes
    private static Font fuenteSans12B = new Font("Open Sans", Font.BOLD,12);
    private static Font fuenteSans12P = new Font("Open Sans", Font.PLAIN,12);
    private static Font fuenteSans14B = new Font("Open Sans", Font.BOLD,14);
    private static Font fuenteSans14P = new Font("Open Sans", Font.PLAIN,14);
    private static Font fuenteSans16P = new Font("Open Sans", Font.PLAIN,16);
    private static Font fuenteSans16B = new Font("Open Sans", Font.BOLD,16);
    
    //Colores
    Color azulF = new Color(25,30,111);
    Color gris = new Color(200,200,200);
    
    //BD
    private static String usuario;
    private static String url = "jdbc:mysql://192.168.1.10:3306/sci?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City&useSSL=false";
    
    //Clases
    private static baseDeDatos bd = new baseDeDatos();
    private static herramientasGUI herramientas = new herramientasGUI();
    
    //Variables GUI
    int contAdd;
    boolean autAgregarNombre = false;
    boolean rep=false;
    boolean modImagen = false;
    int estadoBusqueda = 1;
    Pattern correoPattern = Pattern.compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");
    boolean admin;
    int[] cambios;
    Tabla t = new Tabla();
    Date fechaBIn;
    Date fechaRegIn;
    Date fechaBFin;
    Date fechaRegFin;
    boolean logout = false;
    boolean imagenSel = false;
    
    //Consultar producto;
    DefaultListModel<String> listaUbicacionesB;
    DefaultListModel<String> listaProveedoresB;
    BufferedImage imagenProducto;
    
    
    //Tabla busqueda
    String[][] reBusquedaNombre;
    
    //Agregar producto;
    DefaultListModel<String> listaUbicaciones;
    DefaultListModel<String> listaProveedores;
    BufferedImage imagen;
    String zonaDefault = "ALMACEN";
    
    
    //Informacion del producto
    private static String[][] eSProducto;
    
    
    //Crear usuarios
    private static boolean usuarioExiste = true;
    private static boolean contrasenaUsuario = false;
    private static boolean nombreUsuario = false;
    private static boolean apellidoUsuario = false;
    
    
    //Crear proveedor
    private static boolean razonVacio = true;
    private static boolean representanteVacio = true;
    private static boolean email = false;
    private static boolean telefonoVacio = true;
    private static boolean direccionVacio = true;
    
    
    //Hilo
    private static HiloNotificaciones thread;

    
    /**
     * Creates new form GUIPrincipal
     */
    public GUIPrincipal() {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension reqMin = new Dimension(1024, 768);
        if(!(reqMin.getHeight()<screenSize.getHeight() && reqMin.getWidth()<screenSize.getWidth())){
            initComponents();
            resizeVentanas();
            iniciaCalendario();
            inicializaGhostText();
        }else{
            System.exit(0);
        }
    }
    
    private void resizePanelResultado(){
        //Panel resultado
        jspResultadoPMD.setSize(panelMostrarProducto.getWidth()-20, panelMostrarProducto.getHeight()-68);
            //EditarButton
            jpResultadoPMD.add(jtbEditarPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(jspResultadoPMD.getWidth()-80, 10, 40, 40));
            //BorrarButton
            jpResultadoPMD.add(jbBorrarPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(jspResultadoPMD.getWidth()-80, 60, 40, 40));
            //Clave producto
            jpResultadoPMD.add(jtfClaveProductoPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(jspResultadoPMD.getWidth()-360, 10, 270, 40));
            //ImagenCambio
            int x = jspResultadoPMD.getWidth()/2-50;
            int y = 220*x/310;
            jpResultadoPMD.add(jlImagenPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(jspResultadoPMD.getWidth()-40-x, 110, x, y));
            try {
                BufferedImage img = ImageIO.read(getClass().getResource("/Images/noImagen.png"));
                Image dimg = img.getScaledInstance(x, y,Image.SCALE_SMOOTH);
                jlImagenPMD.setIcon(new ImageIcon(dimg));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //Imprimir
            jpResultadoPMD.add(jbImprimirPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(jspResultadoPMD.getWidth()-(jspResultadoPMD.getWidth()/4)-45, 110+y+20, 90, 90));
            //PanelInfoProducto
            jpResultadoPMD.add(jpProductoInfoPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, jspResultadoPMD.getWidth()/2, 430));
            Dimension pIP = new Dimension(jspResultadoPMD.getWidth()/2-110, 40);
                //Nombre
                jtfNombrePMP.setSize(pIP);
                //Empleo
                jtfEmpleoPMD.setSize(pIP);
                //Descripcion
                jspDescripcionPMD.setSize(pIP.width,130);
                //Ubicaciones
                jtfUbicacionesPMD.setSize(pIP.width-40,pIP.height);
                jbAgregarUbicacionPMD.setLocation(jspResultadoPMD.getWidth()/2-40,290);
                jspUbicacionesPMD.setSize(pIP.width,100);
            //PanelProveedores
            jpResultadoPMD.add(jpProveedoresPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 660, jspResultadoPMD.getWidth()/2, 140));
            jcbProveedoresPMD.setSize(pIP.width-40,pIP.height);
            jbAgregarProveedorPMD.setLocation(jspResultadoPMD.getWidth()/2-40,0);
            jspProveedoresPMD.setSize(pIP.width,100);    
            //Boton guardar
             jpResultadoPMD.add(jbGuardarCambiosPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(jspResultadoPMD.getWidth()/2-60, 810, 120, 40));
            //Panel Inventario
            jpResultadoPMD.add(jpInventarioPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(jspResultadoPMD.getWidth()/2-210, 860, 420, 170));
            //Tabla
            jpResultadoPMD.add(jspEntradasSalidasPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 1100, jspResultadoPMD.getWidth()-40, 380));
        jpResultadoPMD.revalidate();
        jpResultadoPMD.repaint();
    }
    
    private void resizePanelAgregarProducto(){
        jpOpcionesPAP.setSize(panelAgregarProducto.getWidth(), panelAgregarProducto.getHeight());
        jpCargarProductoPAP.setSize(panelAgregarProducto.getWidth(), panelAgregarProducto.getHeight());
        jpInstruccionesPAP.setLocation(panelAgregarProducto.getWidth()/2-385, 60);
        jspErroresCargaPAP.setSize(panelAgregarProducto.getWidth()-44, 300);
        txtExitoCargaPAP.setSize(panelAgregarProducto.getWidth(),20);
        jpBotonCSVPAP.setLocation(panelAgregarProducto.getWidth()/2-65, 240);
        jpAgregarProductoPAP.setSize(panelAgregarProducto.getWidth(), panelAgregarProducto.getHeight());
        if(screenSize.getWidth()>1024){
            jpIdentificacionProductoPAP.setLocation(jpAgregarProductoPAP.getWidth()-400,20);
            jpInfoProductoPAP.setLocation(60,50);
            jpInfoProductoPAP.setSize(jpAgregarProductoPAP.getWidth()-480, 380);
            jtfNombrePAP.setSize(jpInfoProductoPAP.getWidth()-160,40);
            jtfClaveProductoPAP.setSize(jpInfoProductoPAP.getWidth()-160,40);
            jspDescripcionPAP.setSize(jpInfoProductoPAP.getWidth()-160,110);
            jtfEmpleoPAP.setSize(jpInfoProductoPAP.getWidth()-160,40);
            jcbMaquinariaPAP.setSize(jpInfoProductoPAP.getWidth()-160,40);
            jpProveedoresProductoPAP.setLocation(60, 440);
            jpInventarioPAP.setLocation(jpProveedoresProductoPAP.getWidth()+100, 460);
            jpInventarioPAP.setSize(jpAgregarProductoPAP.getWidth()-jpInventarioPAP.getX()-50,170);
            jcbUMedidaPAP.setLocation(jpInventarioPAP.getWidth()-50, 20);
            jtfCantidadPAP.setSize(jpInventarioPAP.getWidth()-220,40);
            jtfMinimoPAP.setSize(jpInventarioPAP.getWidth()-170,40);
            jtfPrecioEstimadoPAP.setSize(jpInventarioPAP.getWidth()-170,40);
            jbAgregarProductoPAP.setLocation(jpAgregarProductoPAP.getWidth()/2-85,650);
        }
    }
    
    public void resizePanelProveedor(){
        jtpPP.setSize(panelProveedor.getWidth()-20,panelProveedor.getHeight()-10);
        jpListaProveedoresPP.setSize(panelProveedor.getWidth()-20,panelProveedor.getHeight()-10);
        jpAgregarProveedorPP.setSize(panelProveedor.getWidth()-20,panelProveedor.getHeight()-10);
        jpInfoProveedoresPP.setLocation(panelProveedor.getWidth()/2-jpInfoProveedoresPP.getWidth()/2, 20);
        jbAgregarProveedorPP.setLocation(jpAgregarProveedorPP.getWidth()/2-50,450);
        jspTablaProveedoresPP.setSize(jpListaProveedoresPP.getWidth()-20, jpListaProveedoresPP.getHeight()-60);
    }
    
    public void resizePanelUsuario(){
        jtpPU.setSize(panelUsuario.getWidth()-20,panelUsuario.getHeight()-10);
        jpCrearUsuarioPU.setSize(panelUsuario.getWidth()-20,panelUsuario.getHeight()-10);
        jpAdministrarUsuariosPU.setSize(panelUsuario.getWidth()-20,panelUsuario.getHeight()-10);
        jpInformacionUsuarioPU.setLocation(jpCrearUsuarioPU.getWidth()/2-290, 40);
        jbCrearUsuarioPU.setLocation(jpCrearUsuarioPU.getWidth()/2-25, 450);
        jspUsuariosPU.setSize(jpAdministrarUsuariosPU.getWidth()-20,jpAdministrarUsuariosPU.getHeight()-10);
        jpUsuarioPU.setSize(jpAdministrarUsuariosPU.getWidth()-20,jpAdministrarUsuariosPU.getHeight()-10);
        jpInfoUsuarioPU.setLocation(jpUsuarioPU.getWidth()/2-280, 70);
        jbCambiarInfoUsuarioPU.setLocation(jpUsuarioPU.getWidth()/2-65, 500);
    }
    
    public void resizeVentanas(){
        login.setPreferredSize(screenSize);
        login.setSize(screenSize);
        panel.setSize(screenSize);
        gestaltAdm.setSize(200, screenSize.height);
        gestaltOp.setSize(200, screenSize.height);
        panelNombre.setSize(screenSize.width-270, 40);
        jbOpcionesPN.setLocation(panelNombre.getWidth()-46, 0);
        jbNotificacionesPN.setLocation(panelNombre.getWidth()-92, 0);
        txtBienvenidaPN.setLocation(panelNombre.getWidth()-742, 0);
        panelLogo.setSize(screenSize.width-200,screenSize.height-40);
        rogeriLogo.setSize(screenSize.width-200,screenSize.height-40);
        panelPrincipal.setSize(screenSize.width-200,screenSize.height-40);
        panelConsultaProductos.setSize(screenSize.width-200,screenSize.height-40);
        jspTablaPCP.setSize(panelConsultaProductos.getWidth()-20, panelConsultaProductos.getHeight()-100);
        panelMostrarProducto.setSize(screenSize.width-200,screenSize.height-40);
        resizePanelResultado();
        panelAgregarProducto.setSize(screenSize.width-200,screenSize.height-40);
        jbAgregarUnProductoPAP.setLocation(panelAgregarProducto.getWidth()/2-240, 190);
        txtAgregarUnProductoPAP.setLocation(panelAgregarProducto.getWidth()/2-240, 400);
        jbAgregarVariosProductoPAP.setLocation(panelAgregarProducto.getWidth()/2+40, 190);
        txtAgregarVariosProductoPAP.setLocation(panelAgregarProducto.getWidth()/2+40, 400);
        resizePanelAgregarProducto();
        panelProveedor.setSize(screenSize.width-200,screenSize.height-40);
        resizePanelProveedor();
        panelUsuario.setSize(screenSize.width-200,screenSize.height-40);
        resizePanelUsuario();
        panelMaquinaria.setSize(screenSize.width-200,screenSize.height-40);
        jpInfoMaquina.setLocation(panelMaquinaria.getWidth()/2-400, 40);
        panelRegistro.setSize(screenSize.width-200,screenSize.height-40);
        jspRegistrosPR.setSize(panelRegistro.getWidth()-20, panelRegistro.getHeight()-70);
        panelConfiguracion.setSize(screenSize.width-200,screenSize.height-40);
        jpPasswordPC.setLocation(panelConfiguracion.getWidth()/2-290, 70);
        jbCambiarPasswordPC.setLocation(panelConfiguracion.getWidth()/2-55, 350);
        panelMinimos.setSize(screenSize.width-200,screenSize.height-40);
        jspMinimosPMin.setSize(panelMinimos.getWidth()-20, panelMinimos.getHeight()-60);
        this.pack();
    }
    
    public void iniciaCalendario(){
        Date date = new Date();
        fechaBFin = date;
        fechaRegFin = date;
        jdcFechaFinalPMD.setDate(date);
        fechaFinal.setDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        fechaBIn = calendar.getTime();
        fechaRegIn = calendar.getTime();
        jdcFechaInicialPMD.setDate(calendar.getTime());
        fechaInicio.setDate(calendar.getTime());
    }
    
    public void inicializaGhostText(){
        GhostText ghostText = new GhostText(jtfBuscarPCP, "Buscar producto: Ej. BE-8458445");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgBotonesPrincipales = new javax.swing.ButtonGroup();
        opciones = new javax.swing.JPanel();
        minimizarVentana = new javax.swing.JButton();
        cerrarVentana = new javax.swing.JButton();
        login = new JPanelGradient(new Color(255,255,255,255),new Color(25, 30, 111,53));
        imagenUsuarioLogin = new javax.swing.JLabel();
        txtBienvenidaLogin = new javax.swing.JLabel();
        txtUsuarioLogin = new javax.swing.JLabel();
        JTFUsuarioLogin = new javax.swing.JTextField();
        txtPasswordLogin = new javax.swing.JLabel();
        PasswordLogin = new javax.swing.JPasswordField();
        PasswordToggleLogin = new javax.swing.JToggleButton();
        errorContraseñaLogin = new javax.swing.JLabel();
        iniciarSesion = new javax.swing.JButton();
        panel = new JPanelGradient(new Color(255,255,255,255),new Color(25, 30, 111,53));
        gestaltOp = new JPanelGradient(new Color(25, 30, 111,178),new Color(42,126,222,100));
        productosOp = new javax.swing.JToggleButton();
        txtProductosGOP = new javax.swing.JLabel();
        agregarProductosOp = new javax.swing.JToggleButton();
        txtAgregarProductosGOP = new javax.swing.JLabel();
        registroBoton = new javax.swing.JToggleButton();
        txtRegistroGOP = new javax.swing.JLabel();
        gestaltAdm = new JPanelGradient(new Color(25, 30, 111,178),new Color(42,126,222,100));
        ProductosAdm = new javax.swing.JToggleButton();
        txtProductosGA = new javax.swing.JLabel();
        agregarProductos = new javax.swing.JToggleButton();
        txtAgregarProductosGA = new javax.swing.JLabel();
        proveedorButton = new javax.swing.JToggleButton();
        txtProveedoresGA = new javax.swing.JLabel();
        agregarUsers = new javax.swing.JToggleButton();
        txtUsuariosGA = new javax.swing.JLabel();
        maquinaria = new javax.swing.JToggleButton();
        txtMaquinariaGA = new javax.swing.JLabel();
        registroBoton1 = new javax.swing.JToggleButton();
        txtRegistroGA = new javax.swing.JLabel();
        panelNombre = new javax.swing.JPanel();
        txtBienvenidaPN = new javax.swing.JLabel();
        popupOpciones = new JPopupMenu();
        JMenuItem menuItemConfiguracion = new JMenuItem("Configuraciones");
        menuItemConfiguracion.setFont(fuenteSans12P);
        menuItemConfiguracion.setForeground(azulF);
        popupOpciones.add(menuItemConfiguracion);
        JMenuItem menuItemSalir = new JMenuItem("Salir",new javax.swing.ImageIcon(getClass().getResource("/Images/logoutN.png")));
        menuItemSalir.setFont(fuenteSans12P);
        menuItemSalir.setForeground(azulF);
        popupOpciones.add(menuItemSalir);
        menuItemConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configuracionActionPerformed(evt);
            }
        });
        menuItemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salirActionPerformed(evt);
            }
        });
        ImageIcon iconPop = new ImageIcon(getClass().getResource("/Images/settingsN.png"));
        jbOpcionesPN = new javax.swing.JButton();
        popupNotificaciones = new JPopupMenu();
        jmiNotificaciones = new JMenuItem("No hay notificaciones");
        jmiNotificaciones.setFont(fuenteSans12P);
        jmiNotificaciones.setForeground(azulF);
        jmiNotificaciones.setBackground(gris);
        popupNotificaciones.add(jmiNotificaciones);
        jmiNotificaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotificacionesActionPerformed(evt);
            }
        });
        jbNotificacionesPN = new javax.swing.JButton();
        panelLogo = new javax.swing.JPanel();
        rogeriLogo = new javax.swing.JLabel();
        panelPrincipal = new javax.swing.JPanel();
        panelConsultaProductos = new javax.swing.JPanel();
        txtOrdenarPCP = new javax.swing.JLabel();
        UIManager.put("ComboBox.disabledBackground", Color.white);
        UIManager.put("ComboBox.disabledForeground", Color.black);
        jcbOrdenacionPCP = new javax.swing.JComboBox<>();
        txtBuscarPCP = new javax.swing.JLabel();
        jtfBuscarPCP = new javax.swing.JTextField();
        jtbSortPCP = new javax.swing.JToggleButton();
        txtSeleccionarPCP = new javax.swing.JLabel();
        jspTablaPCP = new javax.swing.JScrollPane();
        jtTablaPCP = new javax.swing.JTable();
        panelMostrarProducto = new javax.swing.JPanel();
        jbRegresarPMD = new javax.swing.JButton();
        jspResultadoPMD = new javax.swing.JScrollPane();
        jpResultadoPMD = new javax.swing.JPanel();
        jpProductoInfoPMD = new javax.swing.JPanel();
        txtNombrePMD = new javax.swing.JLabel();
        jtfNombrePMP = new javax.swing.JTextField();
        txtMaquinariaPMD = new javax.swing.JLabel();
        jcbMaquinariaPMD = new javax.swing.JComboBox<>();
        txtEmpleoPMD = new javax.swing.JLabel();
        jtfEmpleoPMD = new javax.swing.JTextField();
        txtDescripcionPMD = new javax.swing.JLabel();
        jspDescripcionPMD = new javax.swing.JScrollPane();
        jtaDescripcionPMD = new javax.swing.JTextArea();
        txtUbicacionesPMD = new javax.swing.JLabel();
        jtfUbicacionesPMD = new javax.swing.JTextField();
        jbAgregarUbicacionPMD = new javax.swing.JButton();
        jspUbicacionesPMD = new javax.swing.JScrollPane();
        jlUbicacionesPMD = new javax.swing.JList<>();
        txtUMedidaPMD = new javax.swing.JLabel();
        jtfPrecioPMD = new javax.swing.JTextField();
        txtMinimosPMD = new javax.swing.JLabel();
        jtfMinimosPMD = new javax.swing.JTextField();
        jtfClaveProductoPMD = new javax.swing.JTextField();
        jlImagenPMD = new javax.swing.JLabel();
        jbImprimirPMD = new javax.swing.JButton();
        jpProveedoresPMD = new javax.swing.JPanel();
        txtProveedoresPMD = new javax.swing.JLabel();
        jcbProveedoresPMD = new javax.swing.JComboBox<>();
        jbAgregarProveedorPMD = new javax.swing.JButton();
        jspProveedoresPMD = new javax.swing.JScrollPane();
        jlProveedoresPMD = new javax.swing.JList<>();
        jbGuardarCambiosPMD = new javax.swing.JButton();
        jpInventarioPMD = new javax.swing.JPanel();
        txtUniDisponiblesPMD = new javax.swing.JLabel();
        txtDisponiblesPMD = new javax.swing.JLabel();
        jpESPMD = new javax.swing.JPanel();
        jbEntradasPMD = new javax.swing.JButton();
        jbSalidasPMD = new javax.swing.JButton();
        txtMostrarPMD = new javax.swing.JLabel();
        jcbUniMedidaPMD = new javax.swing.JComboBox<>();
        txtPrecioPMD = new javax.swing.JLabel();
        jcbEleccionMostrarPMD = new javax.swing.JComboBox<>();
        jdcFechaFinalPMD = new com.toedter.calendar.JDateChooser();
        jdcFechaInicialPMD = new com.toedter.calendar.JDateChooser();
        txtHastaPMD = new javax.swing.JLabel();
        txtDesdePMD = new javax.swing.JLabel();
        jspEntradasSalidasPMD = new javax.swing.JScrollPane();
        jtEntradasSalidasPMD = new javax.swing.JTable();
        jtbEditarPMD = new javax.swing.JToggleButton();
        jbBorrarPMD = new javax.swing.JButton();
        panelAgregarProducto = new javax.swing.JPanel();
        jpOpcionesPAP = new javax.swing.JPanel();
        jbAgregarUnProductoPAP = new javax.swing.JButton();
        jbAgregarVariosProductoPAP = new javax.swing.JButton();
        txtAgregarVariosProductoPAP = new javax.swing.JLabel();
        txtAgregarUnProductoPAP = new javax.swing.JLabel();
        jpCargarProductoPAP = new javax.swing.JPanel();
        jbRegresarVariosProductoPAP = new javax.swing.JButton();
        jpInstruccionesPAP = new javax.swing.JPanel();
        txtTituloInstruccionesPAP = new javax.swing.JLabel();
        txtInstruccion1PAP = new javax.swing.JLabel();
        txtInstruccion2PAP = new javax.swing.JLabel();
        txtInstruccion3PAP = new javax.swing.JLabel();
        txtInstruccion4PAP = new javax.swing.JLabel();
        jpBotonCSVPAP = new javax.swing.JPanel();
        jbCargarCSVPAP = new javax.swing.JButton();
        txtSeleccionarPAP = new javax.swing.JLabel();
        jspErroresCargaPAP = new javax.swing.JScrollPane();
        erroresCSVPanel = new javax.swing.JEditorPane();
        txtExitoCargaPAP = new javax.swing.JLabel();
        jpAgregarProductoPAP = new javax.swing.JPanel();
        jbRegresarOneProductoPAP = new javax.swing.JButton();
        jpInfoProductoPAP = new javax.swing.JPanel();
        jtfNombrePAP = new javax.swing.JTextField();
        txtNombrePAP = new javax.swing.JLabel();
        txtClaveProductoPAP = new javax.swing.JLabel();
        jtfClaveProductoPAP = new javax.swing.JTextField();
        txtErrorIDPAP = new javax.swing.JLabel();
        txtDescripcionPAP = new javax.swing.JLabel();
        jspDescripcionPAP = new javax.swing.JScrollPane();
        jtaDescripcionPAP = new javax.swing.JTextArea();
        txtValoresRequeridosPAP = new javax.swing.JLabel();
        jtfEmpleoPAP = new javax.swing.JTextField();
        txtEmpleoPAP = new javax.swing.JLabel();
        jcbMaquinariaPAP = new javax.swing.JComboBox<>();
        txtMaquinariaPAP = new javax.swing.JLabel();
        jpProveedoresProductoPAP = new javax.swing.JPanel();
        jspProveedoresPAP = new javax.swing.JScrollPane();
        jlProveedoresPAP = new javax.swing.JList<>();
        jcbProveedoresPAP = new javax.swing.JComboBox<>();
        jbAgregarProveedorPAP = new javax.swing.JButton();
        jpIdentificacionProductoPAP = new javax.swing.JPanel();
        txtUbicacionesPAP = new javax.swing.JLabel();
        jtfUbicacionesPAP = new javax.swing.JTextField();
        jbAgregarUbicacionesPAP = new javax.swing.JButton();
        jspUbicacionesPAP = new javax.swing.JScrollPane();
        jlUbicacionesPAP = new javax.swing.JList<>();
        jlAgregarImagenPAP = new javax.swing.JLabel();
        jpInventarioPAP = new javax.swing.JPanel();
        jcbUMedidaPAP = new javax.swing.JComboBox<>();
        jtfCantidadPAP = new javax.swing.JTextField();
        txtCantidadPAP = new javax.swing.JLabel();
        jtfPrecioEstimadoPAP = new javax.swing.JTextField();
        txtPrecioPAP = new javax.swing.JLabel();
        jtfMinimoPAP = new javax.swing.JTextField();
        txtMinimoPAP = new javax.swing.JLabel();
        jbAgregarProductoPAP = new javax.swing.JButton();
        panelProveedor = new javax.swing.JPanel();
        UIManager.put("TabbedPane.contentOpaque", false);
        jtpPP = new javax.swing.JTabbedPane();
        jpAgregarProveedorPP = new javax.swing.JPanel();
        jpInfoProveedoresPP = new javax.swing.JPanel();
        txtCamposRequeridosPP = new javax.swing.JLabel();
        txtRazonPP = new javax.swing.JLabel();
        jtfRazonPP = new javax.swing.JTextField();
        txtMaquinariaPP = new javax.swing.JLabel();
        jcbMaquinariaPP = new javax.swing.JComboBox<>();
        txtRepresentantePP = new javax.swing.JLabel();
        jtfRepresentantePP = new javax.swing.JTextField();
        txtCorreoPP = new javax.swing.JLabel();
        jtfCorreoPP = new javax.swing.JTextField();
        txtCorreoNoValidoPP = new javax.swing.JLabel();
        txtTelefonoPP = new javax.swing.JLabel();
        jtfTelefonoPP = new javax.swing.JTextField();
        txtDireccionPP = new javax.swing.JLabel();
        jtfDireccionPP = new javax.swing.JTextField();
        jbAgregarProveedorPP = new javax.swing.JButton();
        jpListaProveedoresPP = new javax.swing.JPanel();
        jcbSortListaProveedoresPP = new javax.swing.JComboBox<>();
        jspTablaProveedoresPP = new javax.swing.JScrollPane();
        jtProveedoresPP = new javax.swing.JTable();
        txtOrdenarPP = new javax.swing.JLabel();
        jtfBuscarPP = new javax.swing.JTextField();
        txtBuscarPP = new javax.swing.JLabel();
        jtbSortProveedoresPP = new javax.swing.JToggleButton();
        panelUsuario = new javax.swing.JPanel();
        UIManager.put("TabbedPane.contentOpaque", false);
        jtpPU = new javax.swing.JTabbedPane();
        jpCrearUsuarioPU = new javax.swing.JPanel();
        jpInformacionUsuarioPU = new javax.swing.JPanel();
        txtNombreDeUsuarioPU = new javax.swing.JLabel();
        jtfUsuarioPU = new javax.swing.JTextField();
        txtUsuarioYaExistentePU = new javax.swing.JLabel();
        txtPasswordPU = new javax.swing.JLabel();
        jpfPasswordPU = new javax.swing.JPasswordField();
        txtConfirmarContrasenaPU = new javax.swing.JLabel();
        jpfConfirmarPasswordPu = new javax.swing.JPasswordField();
        txtNombrePU = new javax.swing.JLabel();
        jtfNombrePU = new javax.swing.JTextField();
        txtApellidosPU = new javax.swing.JLabel();
        jtfApellidosPU = new javax.swing.JTextField();
        txtTipoUsuarioPU = new javax.swing.JLabel();
        jcbTipoUsuarioPU = new javax.swing.JComboBox<>();
        txtPasswordDiferentePU = new javax.swing.JLabel();
        txtLogitudMenorPU = new javax.swing.JLabel();
        jbCrearUsuarioPU = new javax.swing.JButton();
        jpAdministrarUsuariosPU = new javax.swing.JPanel();
        jspUsuariosPU = new javax.swing.JScrollPane();
        jtUsuarios = new javax.swing.JTable();
        jpUsuarioPU = new javax.swing.JPanel();
        jbRegresarUsuarioPU = new javax.swing.JButton();
        jpInfoUsuarioPU = new javax.swing.JPanel();
        txtUsuarioInfoPU = new javax.swing.JLabel();
        txtUserNamePU = new javax.swing.JLabel();
        txtNombreInfoPU = new javax.swing.JLabel();
        txtNombreUsuarioPU = new javax.swing.JLabel();
        txtApellidosInfoPU = new javax.swing.JLabel();
        txtApellidosUsuarioPU = new javax.swing.JLabel();
        txtCambiarPasswordPU = new javax.swing.JLabel();
        txtContrasenaModificarPU = new javax.swing.JLabel();
        jpfPasswordUsuarioPU = new javax.swing.JPasswordField();
        txtContrasenaConfirmarModificarPU = new javax.swing.JLabel();
        jpfPasswordConfUsuarioPU = new javax.swing.JPasswordField();
        txtPasswordDiferenteUsuarioPU = new javax.swing.JLabel();
        txtPasswordLongitudMenorPU = new javax.swing.JLabel();
        jbCambiarInfoUsuarioPU = new javax.swing.JButton();
        panelMaquinaria = new javax.swing.JPanel();
        jpInfoMaquina = new javax.swing.JPanel();
        txtAgregarMaquinariaPM = new javax.swing.JLabel();
        txtNombrePM = new javax.swing.JLabel();
        jtfNombrePM = new javax.swing.JTextField();
        jbAgregarMaquinariaPM = new javax.swing.JButton();
        jspMaquinariaPM = new javax.swing.JScrollPane();
        jtMaquinariasPM = new javax.swing.JTable();
        panelRegistro = new javax.swing.JPanel();
        txtOrdenarPR = new javax.swing.JLabel();
        jcbOrdenacionPR = new javax.swing.JComboBox<>();
        txtMostrarPR = new javax.swing.JLabel();
        jcbMostrarPR = new javax.swing.JComboBox<>();
        txtDesdePR = new javax.swing.JLabel();
        fechaInicio = new com.toedter.calendar.JDateChooser();
        txtHastaPR = new javax.swing.JLabel();
        fechaFinal = new com.toedter.calendar.JDateChooser();
        jspRegistrosPR = new javax.swing.JScrollPane();
        jtRegistrosPR = new javax.swing.JTable();
        panelConfiguracion = new javax.swing.JPanel();
        jpPasswordPC = new javax.swing.JPanel();
        txtCambiarPasswordPC = new javax.swing.JLabel();
        txtPasswordPC = new javax.swing.JLabel();
        jpfPasswordPC = new javax.swing.JPasswordField();
        txtConfirmarContrasenaPC = new javax.swing.JLabel();
        jpfConfirmarPasswordPC = new javax.swing.JPasswordField();
        txtPasswordDiferentePC = new javax.swing.JLabel();
        txtLongitudMenorPC = new javax.swing.JLabel();
        txtPasswordCambiadaPC = new javax.swing.JLabel();
        jbCambiarPasswordPC = new javax.swing.JButton();
        panelMinimos = new javax.swing.JPanel();
        jtbSortPMin = new javax.swing.JToggleButton();
        UIManager.put("ComboBox.disabledBackground", Color.white);
        UIManager.put("ComboBox.disabledForeground", Color.black);
        jcbOrdenacionPMin = new javax.swing.JComboBox<>();
        txtOrdenarPMin = new javax.swing.JLabel();
        jspMinimosPMin = new javax.swing.JScrollPane();
        jtMinimosPMin = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SCI Rogeri");
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setIconImage(new ImageIcon(getClass().getResource("/Images/iconoRogeri.png")).getImage());
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(null);

        opciones.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        opciones.setOpaque(false);
        opciones.setLayout(null);

        minimizarVentana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/minN.png"))); // NOI18N
        minimizarVentana.setContentAreaFilled(false);
        minimizarVentana.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        minimizarVentana.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/minS.png"))); // NOI18N
        minimizarVentana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minimizarVentanaActionPerformed(evt);
            }
        });
        opciones.add(minimizarVentana);
        minimizarVentana.setBounds(0, 0, 30, 30);

        cerrarVentana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/closeN.png"))); // NOI18N
        cerrarVentana.setContentAreaFilled(false);
        cerrarVentana.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cerrarVentana.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/closeS.png"))); // NOI18N
        cerrarVentana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarVentanaActionPerformed(evt);
            }
        });
        opciones.add(cerrarVentana);
        cerrarVentana.setBounds(35, 0, 30, 30);

        getContentPane().add(opciones);
        opciones.setBounds(960, 0, 65, 30);
        opciones.setLocation(screenSize.width-65, 0);

        login.setLayout(null);

        imagenUsuarioLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imagenUsuarioLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/userIcon.png"))); // NOI18N
        login.add(imagenUsuarioLogin);
        imagenUsuarioLogin.setBounds(410, 150, 184, 196);
        imagenUsuarioLogin.setLocation(screenSize.width/2-92, screenSize.height/2-((screenSize.height/2)/2));

        txtBienvenidaLogin.setFont(fuenteSans16P);
        txtBienvenidaLogin.setForeground(new java.awt.Color(102, 102, 102));
        txtBienvenidaLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtBienvenidaLogin.setText("Bienvenido");
        login.add(txtBienvenidaLogin);
        txtBienvenidaLogin.setBounds(390, 350, 230, 14);
        txtBienvenidaLogin.setLocation(screenSize.width/2-115, imagenUsuarioLogin.getLocation().y+imagenUsuarioLogin.getHeight()+10);

        txtUsuarioLogin.setFont(fuenteSans12P);
        txtUsuarioLogin.setForeground(new java.awt.Color(120, 120, 120));
        txtUsuarioLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtUsuarioLogin.setText("Usuario:");
        login.add(txtUsuarioLogin);
        txtUsuarioLogin.setBounds(400, 380, 220, 14);
        txtUsuarioLogin.setLocation(screenSize.width/2-110, txtBienvenidaLogin.getLocation().y+txtBienvenidaLogin.getHeight()+15);

        JTFUsuarioLogin.setFont(fuenteSans12P);
        JTFUsuarioLogin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        JTFUsuarioLogin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JTFUsuarioLoginKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                JTFUsuarioLoginKeyReleased(evt);
            }
        });
        login.add(JTFUsuarioLogin);
        JTFUsuarioLogin.setBounds(370, 410, 300, 30);
        JTFUsuarioLogin.setLocation(screenSize.width/2-150, txtUsuarioLogin.getLocation().y+txtUsuarioLogin.getHeight()+5);

        txtPasswordLogin.setFont(fuenteSans12P);
        txtPasswordLogin.setForeground(new java.awt.Color(120, 120, 120));
        txtPasswordLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPasswordLogin.setText("Contraseña:");
        login.add(txtPasswordLogin);
        txtPasswordLogin.setBounds(390, 450, 250, 14);
        txtPasswordLogin.setLocation(screenSize.width/2-125, JTFUsuarioLogin.getLocation().y+JTFUsuarioLogin.getHeight()+10);

        PasswordLogin.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        PasswordLogin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        PasswordLogin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PasswordLoginKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                PasswordLoginKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                PasswordLoginKeyTyped(evt);
            }
        });
        login.add(PasswordLogin);
        PasswordLogin.setBounds(370, 490, 300, 30);
        PasswordLogin.setLocation(screenSize.width/2-150, txtPasswordLogin.getLocation().y+txtPasswordLogin.getHeight()+5);

        PasswordToggleLogin.setBackground(new java.awt.Color(255, 255, 255));
        PasswordToggleLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/show.png"))); // NOI18N
        PasswordToggleLogin.setContentAreaFilled(false);
        PasswordToggleLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PasswordToggleLogin.setRolloverEnabled(false);
        PasswordToggleLogin.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/hide.png"))); // NOI18N
        PasswordToggleLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PasswordToggleLoginActionPerformed(evt);
            }
        });
        login.add(PasswordToggleLogin);
        PasswordToggleLogin.setBounds(670, 490, 30, 30);
        PasswordToggleLogin.setLocation(screenSize.width/2+150, txtPasswordLogin.getLocation().y+txtPasswordLogin.getHeight()+5);

        errorContraseñaLogin.setFont(fuenteSans12P);
        errorContraseñaLogin.setForeground(new java.awt.Color(255, 0, 0));
        errorContraseñaLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        errorContraseñaLogin.setText("Contraseña o usuario incorrecto");
        errorContraseñaLogin.setVisible(false);
        login.add(errorContraseñaLogin);
        errorContraseñaLogin.setBounds(370, 520, 300, 14);
        errorContraseñaLogin.setLocation(screenSize.width/2-150, PasswordLogin.getLocation().y+PasswordLogin.getHeight()+2);

        iniciarSesion.setBackground(gris);
        iniciarSesion.setFont(fuenteSans14B);
        iniciarSesion.setForeground(new java.awt.Color(255, 255, 255));
        iniciarSesion.setText("Iniciar");
        iniciarSesion.setBorder(null);
        iniciarSesion.setBorderPainted(false);
        iniciarSesion.setContentAreaFilled(false);
        iniciarSesion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        iniciarSesion.setEnabled(false);
        iniciarSesion.setOpaque(true);
        iniciarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniciarSesionActionPerformed(evt);
            }
        });
        login.add(iniciarSesion);
        iniciarSesion.setBounds(470, 560, 90, 40);
        iniciarSesion.setLocation(screenSize.width/2-45, errorContraseñaLogin.getLocation().y+errorContraseñaLogin.getHeight()+15);

        getContentPane().add(login);
        login.setBounds(0, 0, 1024, 768);

        panel.setLayout(null);

        gestaltOp.setLayout(null);
        gestaltOp.setVisible(false);

        bgBotonesPrincipales.add(productosOp);
        productosOp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/InventoryN.png"))); // NOI18N
        productosOp.setBorderPainted(false);
        productosOp.setContentAreaFilled(false);
        productosOp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        productosOp.setFocusPainted(false);
        productosOp.setRolloverEnabled(false);
        productosOp.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/InventoryS.png"))); // NOI18N
        productosOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productosOpProductosAdmActionPerformed(evt);
            }
        });
        gestaltOp.add(productosOp);
        productosOp.setBounds(58, 100, 90, 90);

        txtProductosGOP.setForeground(new java.awt.Color(25, 30, 111));
        txtProductosGOP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtProductosGOP.setText("Productos");
        txtProductosGOP.setFont(fuenteSans12B);
        gestaltOp.add(txtProductosGOP);
        txtProductosGOP.setBounds(0, 190, 206, 14);

        bgBotonesPrincipales.add(agregarProductosOp);
        agregarProductosOp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/agregarProdN.png"))); // NOI18N
        agregarProductosOp.setBorderPainted(false);
        agregarProductosOp.setContentAreaFilled(false);
        agregarProductosOp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        agregarProductosOp.setFocusPainted(false);
        agregarProductosOp.setRolloverEnabled(false);
        agregarProductosOp.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/agregarProdS.png"))); // NOI18N
        agregarProductosOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarProductosActionPerformed(evt);
            }
        });
        gestaltOp.add(agregarProductosOp);
        agregarProductosOp.setBounds(58, 320, 90, 90);

        txtAgregarProductosGOP.setForeground(new java.awt.Color(25, 30, 111));
        txtAgregarProductosGOP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAgregarProductosGOP.setText("Agregar productos");
        txtAgregarProductosGOP.setFont(fuenteSans12B);
        gestaltOp.add(txtAgregarProductosGOP);
        txtAgregarProductosGOP.setBounds(0, 410, 206, 14);

        bgBotonesPrincipales.add(registroBoton);
        registroBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/RegistroN.png"))); // NOI18N
        registroBoton.setBorderPainted(false);
        registroBoton.setContentAreaFilled(false);
        registroBoton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        registroBoton.setFocusPainted(false);
        registroBoton.setRolloverEnabled(false);
        registroBoton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/RegistroS.png"))); // NOI18N
        registroBoton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registroBotonActionPerformed(evt);
            }
        });
        gestaltOp.add(registroBoton);
        registroBoton.setBounds(58, 540, 90, 90);

        txtRegistroGOP.setForeground(new java.awt.Color(25, 30, 111));
        txtRegistroGOP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtRegistroGOP.setText("Registro");
        txtRegistroGOP.setFont(fuenteSans12B);
        gestaltOp.add(txtRegistroGOP);
        txtRegistroGOP.setBounds(0, 630, 206, 14);

        panel.add(gestaltOp);
        gestaltOp.setBounds(0, 0, 200, 770);

        gestaltAdm.setLayout(null);
        gestaltAdm.setVisible(false);

        bgBotonesPrincipales.add(ProductosAdm);
        ProductosAdm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/InventoryN.png"))); // NOI18N
        ProductosAdm.setBorderPainted(false);
        ProductosAdm.setContentAreaFilled(false);
        ProductosAdm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProductosAdm.setFocusPainted(false);
        ProductosAdm.setRolloverEnabled(false);
        ProductosAdm.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/InventoryS.png"))); // NOI18N
        ProductosAdm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productosOpProductosAdmActionPerformed(evt);
            }
        });
        gestaltAdm.add(ProductosAdm);
        ProductosAdm.setBounds(55, 20, 90, 90);

        txtProductosGA.setFont(fuenteSans12P);
        txtProductosGA.setForeground(new java.awt.Color(25, 30, 111));
        txtProductosGA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtProductosGA.setText("Inventario");
        gestaltAdm.add(txtProductosGA);
        txtProductosGA.setBounds(0, 110, 200, 20);

        bgBotonesPrincipales.add(agregarProductos);
        agregarProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/agregarProdN.png"))); // NOI18N
        agregarProductos.setBorderPainted(false);
        agregarProductos.setContentAreaFilled(false);
        agregarProductos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        agregarProductos.setFocusPainted(false);
        agregarProductos.setRolloverEnabled(false);
        agregarProductos.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/agregarProdS.png"))); // NOI18N
        agregarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarProductosActionPerformed(evt);
            }
        });
        gestaltAdm.add(agregarProductos);
        agregarProductos.setBounds(55, 140, 90, 90);

        txtAgregarProductosGA.setFont(fuenteSans12P);
        txtAgregarProductosGA.setForeground(new java.awt.Color(25, 30, 111));
        txtAgregarProductosGA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAgregarProductosGA.setText("Agregar productos");
        gestaltAdm.add(txtAgregarProductosGA);
        txtAgregarProductosGA.setBounds(0, 240, 200, 20);

        bgBotonesPrincipales.add(proveedorButton);
        proveedorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/ProveedorN.png"))); // NOI18N
        proveedorButton.setBorderPainted(false);
        proveedorButton.setContentAreaFilled(false);
        proveedorButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        proveedorButton.setFocusPainted(false);
        proveedorButton.setRolloverEnabled(false);
        proveedorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/ProveedorS.png"))); // NOI18N
        proveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proveedorButtonActionPerformed(evt);
            }
        });
        gestaltAdm.add(proveedorButton);
        proveedorButton.setBounds(55, 260, 90, 90);

        txtProveedoresGA.setFont(fuenteSans12P);
        txtProveedoresGA.setForeground(new java.awt.Color(25, 30, 111));
        txtProveedoresGA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtProveedoresGA.setText("Proveedores");
        gestaltAdm.add(txtProveedoresGA);
        txtProveedoresGA.setBounds(0, 350, 200, 20);

        bgBotonesPrincipales.add(agregarUsers);
        agregarUsers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/person addN.png"))); // NOI18N
        agregarUsers.setBorderPainted(false);
        agregarUsers.setContentAreaFilled(false);
        agregarUsers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        agregarUsers.setFocusPainted(false);
        agregarUsers.setRolloverEnabled(false);
        agregarUsers.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/person addS.png"))); // NOI18N
        agregarUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarUsersActionPerformed(evt);
            }
        });
        gestaltAdm.add(agregarUsers);
        agregarUsers.setBounds(55, 380, 90, 90);

        txtUsuariosGA.setFont(fuenteSans12P);
        txtUsuariosGA.setForeground(new java.awt.Color(25, 30, 111));
        txtUsuariosGA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtUsuariosGA.setText("Usuarios");
        gestaltAdm.add(txtUsuariosGA);
        txtUsuariosGA.setBounds(0, 470, 200, 20);

        bgBotonesPrincipales.add(maquinaria);
        maquinaria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/MachineN.png"))); // NOI18N
        maquinaria.setBorderPainted(false);
        maquinaria.setContentAreaFilled(false);
        maquinaria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        maquinaria.setFocusPainted(false);
        maquinaria.setRolloverEnabled(false);
        maquinaria.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/MachineS.png"))); // NOI18N
        maquinaria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maquinariaActionPerformed(evt);
            }
        });
        gestaltAdm.add(maquinaria);
        maquinaria.setBounds(55, 500, 90, 90);

        txtMaquinariaGA.setFont(fuenteSans12P);
        txtMaquinariaGA.setForeground(new java.awt.Color(25, 30, 111));
        txtMaquinariaGA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtMaquinariaGA.setText("Maquinaria");
        gestaltAdm.add(txtMaquinariaGA);
        txtMaquinariaGA.setBounds(0, 590, 200, 20);

        bgBotonesPrincipales.add(registroBoton1);
        registroBoton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/RegistroN.png"))); // NOI18N
        registroBoton1.setBorderPainted(false);
        registroBoton1.setContentAreaFilled(false);
        registroBoton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        registroBoton1.setFocusPainted(false);
        registroBoton1.setRolloverEnabled(false);
        registroBoton1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/RegistroS.png"))); // NOI18N
        registroBoton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registroBotonActionPerformed(evt);
            }
        });
        gestaltAdm.add(registroBoton1);
        registroBoton1.setBounds(55, 620, 90, 90);

        txtRegistroGA.setFont(fuenteSans12P);
        txtRegistroGA.setForeground(new java.awt.Color(25, 30, 111));
        txtRegistroGA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtRegistroGA.setText("Registro");
        gestaltAdm.add(txtRegistroGA);
        txtRegistroGA.setBounds(0, 710, 200, 20);

        panel.add(gestaltAdm);
        gestaltAdm.setBounds(0, 0, 200, 770);

        panelNombre.setOpaque(false);
        panelNombre.setLayout(null);

        txtBienvenidaPN.setFont(fuenteSans14B);
        txtBienvenidaPN.setForeground(new java.awt.Color(25, 30, 111));
        txtBienvenidaPN.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        panelNombre.add(txtBienvenidaPN);
        txtBienvenidaPN.setBounds(0, 0, 640, 36);

        jbOpcionesPN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/settingsN.png"))); // NOI18N
        jbOpcionesPN.setToolTipText("Opciones");
        jbOpcionesPN.setBorderPainted(false);
        jbOpcionesPN.setContentAreaFilled(false);
        jbOpcionesPN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbOpcionesPN.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/settingsS.png"))); // NOI18N
        jbOpcionesPN.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/settingsS.png"))); // NOI18N
        jbOpcionesPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbOpcionesPNActionPerformed(evt);
            }
        });
        panelNombre.add(jbOpcionesPN);
        jbOpcionesPN.setBounds(710, 0, 36, 36);

        jbNotificacionesPN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/notificacionNadaN.png"))); // NOI18N
        jbNotificacionesPN.setContentAreaFilled(false);
        jbNotificacionesPN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbNotificacionesPN.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/notificacionS.png"))); // NOI18N
        jbNotificacionesPN.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/notificacionS.png"))); // NOI18N
        jbNotificacionesPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbNotificacionesPNActionPerformed(evt);
            }
        });
        panelNombre.add(jbNotificacionesPN);
        jbNotificacionesPN.setBounds(660, 0, 36, 36);

        panel.add(panelNombre);
        panelNombre.setBounds(200, 0, 750, 40);
        panelNombre.setVisible(false);

        panelLogo.setOpaque(false);
        panelLogo.setLayout(null);

        ImageIcon LogRog = new ImageIcon(new ImageIcon(getClass().getResource("/Images/Logo-Rogeri.png")).getImage().getScaledInstance(390, 546, Image.SCALE_SMOOTH));
        rogeriLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rogeriLogo.setIcon(LogRog);
        rogeriLogo.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        panelLogo.add(rogeriLogo);
        rogeriLogo.setBounds(0, 0, 824, 728);

        panel.add(panelLogo);
        panelLogo.setBounds(200, 40, 824, 728);

        panelPrincipal.setOpaque(false);
        panelPrincipal.setLayout(null);

        panelConsultaProductos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelConsultaProductos.setOpaque(false);
        panelConsultaProductos.setLayout(null);

        txtOrdenarPCP.setFont(fuenteSans12B);
        txtOrdenarPCP.setForeground(new java.awt.Color(25, 30, 111));
        txtOrdenarPCP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtOrdenarPCP.setText("Ordenar por:");
        panelConsultaProductos.add(txtOrdenarPCP);
        txtOrdenarPCP.setBounds(10, 20, 100, 40);

        jcbOrdenacionPCP.setBackground(Color.white);
        jcbOrdenacionPCP.setFont(fuenteSans12P);
        jcbOrdenacionPCP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Clave de producto", "Nombre", "Maquinaria", "Uso", "Precio" }));
        jcbOrdenacionPCP.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jcbOrdenacionPCP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jcbOrdenacionPCP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbOrdenacionPCPItemStateChanged(evt);
            }
        });
        panelConsultaProductos.add(jcbOrdenacionPCP);
        jcbOrdenacionPCP.setBounds(120, 20, 140, 40);

        txtBuscarPCP.setFont(fuenteSans12B);
        txtBuscarPCP.setForeground(new java.awt.Color(25, 30, 111));
        txtBuscarPCP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtBuscarPCP.setText("Buscar:");
        panelConsultaProductos.add(txtBuscarPCP);
        txtBuscarPCP.setBounds(320, 20, 100, 40);

        jtfBuscarPCP.setFont(fuenteSans12P);
        jtfBuscarPCP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfBuscarPCPKeyReleased(evt);
            }
        });
        panelConsultaProductos.add(jtfBuscarPCP);
        jtfBuscarPCP.setBounds(430, 20, 380, 40);

        jtbSortPCP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/sortdesc.png"))); // NOI18N
        jtbSortPCP.setContentAreaFilled(false);
        jtbSortPCP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtbSortPCP.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/sortasc.png"))); // NOI18N
        jtbSortPCP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jtbSortPCPItemStateChanged(evt);
            }
        });
        panelConsultaProductos.add(jtbSortPCP);
        jtbSortPCP.setBounds(270, 20, 40, 40);

        txtSeleccionarPCP.setFont(fuenteSans12B);
        txtSeleccionarPCP.setForeground(new java.awt.Color(25, 30, 111));
        txtSeleccionarPCP.setText("Seleccione un producto para continuar");
        panelConsultaProductos.add(txtSeleccionarPCP);
        txtSeleccionarPCP.setBounds(20, 70, 440, 14);

        jspTablaPCP.setFont(fuenteSans14B);
        jspTablaPCP.setOpaque(false);
        jspTablaPCP.setOpaque(false);
        jspTablaPCP.getViewport().setOpaque(false);

        jtTablaPCP.setFont(fuenteSans12P);
        jtTablaPCP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Clave", "Nombre", "Maquinaria", "Uso", "Descripción", "Disponible", "Precio", "Minimo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtTablaPCP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtTablaPCP.getTableHeader().setFont(fuenteSans12B);
        jtTablaPCP.getTableHeader().setOpaque(false);
        jtTablaPCP.getTableHeader().setBackground(azulF);
        jtTablaPCP.getTableHeader().setForeground(Color.white);
        jtTablaPCP.setRowHeight(30);
        jtTablaPCP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtTablaPCPMouseClicked(evt);
            }
        });
        jspTablaPCP.setViewportView(jtTablaPCP);

        panelConsultaProductos.add(jspTablaPCP);
        jspTablaPCP.setBounds(10, 100, 800, 630);

        panelConsultaProductos.setVisible(false);

        panelPrincipal.add(panelConsultaProductos);
        panelConsultaProductos.setBounds(0, 0, 824, 728);

        panelMostrarProducto.setOpaque(false);
        panelMostrarProducto.setVisible(false);
        panelMostrarProducto.setLayout(null);

        jbRegresarPMD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/returnN.png"))); // NOI18N
        jbRegresarPMD.setBorderPainted(false);
        jbRegresarPMD.setContentAreaFilled(false);
        jbRegresarPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbRegresarPMD.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/returnS.png"))); // NOI18N
        jbRegresarPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRegresarPMDActionPerformed(evt);
            }
        });
        panelMostrarProducto.add(jbRegresarPMD);
        jbRegresarPMD.setBounds(10, 10, 50, 50);

        jspResultadoPMD.setBackground(new java.awt.Color(255, 255, 255));
        jspResultadoPMD.setOpaque(false);
        jspResultadoPMD.getViewport().setOpaque(false);

        jpResultadoPMD.setBackground(new java.awt.Color(255, 255, 255));
        jpResultadoPMD.setOpaque(false);
        jpResultadoPMD.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpProductoInfoPMD.setOpaque(false);
        jpProductoInfoPMD.setLayout(null);

        txtNombrePMD.setFont(fuenteSans14B);
        txtNombrePMD.setForeground(new java.awt.Color(25, 30, 111));
        txtNombrePMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNombrePMD.setText("Nombre:");
        jpProductoInfoPMD.add(txtNombrePMD);
        txtNombrePMD.setBounds(0, 0, 100, 40);

        jtfNombrePMP.setFont(fuenteSans12P);
        jtfNombrePMP.setEnabled(false);
        jtfNombrePMP.setOpaque(false);
        jtfNombrePMP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfNombrePMPMouseClicked(evt);
            }
        });
        jtfNombrePMP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfNombrePMPKeyReleased(evt);
            }
        });
        jpProductoInfoPMD.add(jtfNombrePMP);
        jtfNombrePMP.setBounds(110, 0, 340, 40);

        txtMaquinariaPMD.setFont(fuenteSans14B);
        txtMaquinariaPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtMaquinariaPMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtMaquinariaPMD.setText("Maquinaria:");
        jpProductoInfoPMD.add(txtMaquinariaPMD);
        txtMaquinariaPMD.setBounds(0, 50, 100, 40);

        jcbMaquinariaPMD.setFont(fuenteSans12P);
        jcbMaquinariaPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jcbMaquinariaPMD.setOpaque(false);
        jcbMaquinariaPMD.setEnabled(false);
        jcbMaquinariaPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbMaquinariaPMDActionPerformed(evt);
            }
        });
        jpProductoInfoPMD.add(jcbMaquinariaPMD);
        jcbMaquinariaPMD.setBounds(110, 50, 340, 40);

        txtEmpleoPMD.setFont(fuenteSans14B);
        txtEmpleoPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtEmpleoPMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtEmpleoPMD.setText("Empleo:");
        jpProductoInfoPMD.add(txtEmpleoPMD);
        txtEmpleoPMD.setBounds(0, 100, 100, 40);

        jtfEmpleoPMD.setFont(fuenteSans12P);
        jtfEmpleoPMD.setEnabled(false);
        jtfEmpleoPMD.setOpaque(false);
        jtfEmpleoPMD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfEmpleoPMDMouseClicked(evt);
            }
        });
        jtfEmpleoPMD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfEmpleoPMDKeyReleased(evt);
            }
        });
        jpProductoInfoPMD.add(jtfEmpleoPMD);
        jtfEmpleoPMD.setBounds(110, 100, 340, 40);

        txtDescripcionPMD.setFont(fuenteSans14B);
        txtDescripcionPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtDescripcionPMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtDescripcionPMD.setText("Descripción:");
        jpProductoInfoPMD.add(txtDescripcionPMD);
        txtDescripcionPMD.setBounds(0, 150, 100, 40);

        jtaDescripcionPMD.setColumns(20);
        jtaDescripcionPMD.setFont(fuenteSans12P);
        jtaDescripcionPMD.setRows(5);
        jtaDescripcionPMD.setEnabled(false);
        jtaDescripcionPMD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtaDescripcionPMDMouseClicked(evt);
            }
        });
        jtaDescripcionPMD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtaDescripcionPMDKeyReleased(evt);
            }
        });
        jspDescripcionPMD.setViewportView(jtaDescripcionPMD);

        jpProductoInfoPMD.add(jspDescripcionPMD);
        jspDescripcionPMD.setBounds(110, 150, 340, 130);

        txtUbicacionesPMD.setFont(fuenteSans14B);
        txtUbicacionesPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtUbicacionesPMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtUbicacionesPMD.setText("Ubicaciones:");
        jpProductoInfoPMD.add(txtUbicacionesPMD);
        txtUbicacionesPMD.setBounds(0, 290, 100, 40);

        jtfUbicacionesPMD.setFont(fuenteSans12P);
        jtfUbicacionesPMD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtfUbicacionesPMDKeyPressed(evt);
            }
        });
        jpProductoInfoPMD.add(jtfUbicacionesPMD);
        jtfUbicacionesPMD.setBounds(110, 290, 300, 40);

        jbAgregarUbicacionPMD.setBackground(gris);
        jbAgregarUbicacionPMD.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jbAgregarUbicacionPMD.setForeground(new java.awt.Color(255, 255, 255));
        jbAgregarUbicacionPMD.setText("→");
        jbAgregarUbicacionPMD.setBorder(null);
        jbAgregarUbicacionPMD.setContentAreaFilled(false);
        jbAgregarUbicacionPMD.setEnabled(false);
        jbAgregarUbicacionPMD.setOpaque(true);
        jbAgregarUbicacionPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarUbicacionPMDActionPerformed(evt);
            }
        });
        jpProductoInfoPMD.add(jbAgregarUbicacionPMD);
        jbAgregarUbicacionPMD.setBounds(410, 290, 40, 40);

        jlUbicacionesPMD.setFont(fuenteSans12P);
        jlUbicacionesPMD.setModel(listaUbicacionesB = new DefaultListModel());
        jlUbicacionesPMD.setToolTipText("Doble click para eliminar una ubicación");
        jlUbicacionesPMD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlUbicacionesPMDMouseClicked(evt);
            }
        });
        jspUbicacionesPMD.setViewportView(jlUbicacionesPMD);

        jpProductoInfoPMD.add(jspUbicacionesPMD);
        jspUbicacionesPMD.setBounds(110, 330, 340, 100);

        jpResultadoPMD.add(jpProductoInfoPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 450, 430));
        jpResultadoPMD.remove(jpProductoInfoPMD);

        txtUMedidaPMD.setFont(fuenteSans14B);
        txtUMedidaPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtUMedidaPMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtUMedidaPMD.setText("Unidad de medida:");
        txtUMedidaPMD.setVisible(false);
        jpResultadoPMD.add(txtUMedidaPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 140, 40));

        jtfPrecioPMD.setFont(fuenteSans12P);
        jtfPrecioPMD.setEnabled(false);
        jtfPrecioPMD.setOpaque(false);
        jtfPrecioPMD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfPrecioPMDMouseClicked(evt);
            }
        });
        jtfPrecioPMD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfPrecioPMDKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfPrecioPMDKeyTyped(evt);
            }
        });
        jpResultadoPMD.add(jtfPrecioPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 600, 181, 40));

        txtMinimosPMD.setFont(fuenteSans14B);
        txtMinimosPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtMinimosPMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtMinimosPMD.setText("Minimos:");
        jpResultadoPMD.add(txtMinimosPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 550, 80, 40));

        jtfMinimosPMD.setFont(fuenteSans12P);
        jtfMinimosPMD.setEnabled(false);
        jtfMinimosPMD.setOpaque(false);
        jtfMinimosPMD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfMinimosPMDMouseClicked(evt);
            }
        });
        jtfMinimosPMD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfMinimosPMDKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfMinimosPMDKeyTyped(evt);
            }
        });
        jpResultadoPMD.add(jtfMinimosPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 550, 181, 40));

        jtfClaveProductoPMD.setFont(fuenteSans12P);
        jtfClaveProductoPMD.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtfClaveProductoPMD.setEnabled(false);
        jtfClaveProductoPMD.setOpaque(false);
        jtfClaveProductoPMD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfClaveProductoPMDMouseClicked(evt);
            }
        });
        jtfClaveProductoPMD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfClaveProductoPMDKeyReleased(evt);
            }
        });
        jpResultadoPMD.add(jtfClaveProductoPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 10, 270, 40));
        jpResultadoPMD.remove(jtfClaveProductoPMD);

        jlImagenPMD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/noImagen.png"))); // NOI18N
        jlImagenPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlImagenPMD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlImagenPMDMouseClicked(evt);
            }
        });
        jpResultadoPMD.add(jlImagenPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 110, 310, 220));
        jpResultadoPMD.remove(jlImagenPMD);

        jbImprimirPMD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Qr.png"))); // NOI18N
        jbImprimirPMD.setToolTipText("Imprimir QR");
        jbImprimirPMD.setBorderPainted(false);
        jbImprimirPMD.setContentAreaFilled(false);
        jbImprimirPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbImprimirPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbImprimirPMDActionPerformed(evt);
            }
        });
        jpResultadoPMD.add(jbImprimirPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 340, 90, 90));
        jpResultadoPMD.remove(jbImprimirPMD);

        jpProveedoresPMD.setOpaque(false);
        jpProveedoresPMD.setLayout(null);

        txtProveedoresPMD.setFont(fuenteSans14B);
        txtProveedoresPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtProveedoresPMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtProveedoresPMD.setText("Proveedores:");
        jpProveedoresPMD.add(txtProveedoresPMD);
        txtProveedoresPMD.setBounds(0, 0, 100, 40);

        jcbProveedoresPMD.setFont(fuenteSans12P);
        jpProveedoresPMD.add(jcbProveedoresPMD);
        jcbProveedoresPMD.setBounds(110, 0, 300, 40);

        jbAgregarProveedorPMD.setBackground(gris);
        jbAgregarProveedorPMD.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jbAgregarProveedorPMD.setForeground(new java.awt.Color(255, 255, 255));
        jbAgregarProveedorPMD.setText("→");
        jbAgregarProveedorPMD.setBorder(null);
        jbAgregarProveedorPMD.setContentAreaFilled(false);
        jbAgregarProveedorPMD.setEnabled(false);
        jbAgregarProveedorPMD.setOpaque(true);
        jbAgregarProveedorPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarProveedorPMDActionPerformed(evt);
            }
        });
        jpProveedoresPMD.add(jbAgregarProveedorPMD);
        jbAgregarProveedorPMD.setBounds(410, 0, 40, 40);

        jlProveedoresPMD.setFont(fuenteSans12P);
        jlProveedoresPMD.setModel(listaProveedoresB = new DefaultListModel());
        jlProveedoresPMD.setToolTipText("Doble click para eliminar un proveedor");
        jlProveedoresPMD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlProveedoresPMDMouseClicked(evt);
            }
        });
        jspProveedoresPMD.setViewportView(jlProveedoresPMD);

        jpProveedoresPMD.add(jspProveedoresPMD);
        jspProveedoresPMD.setBounds(110, 40, 340, 100);

        jpResultadoPMD.add(jpProveedoresPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 660, 450, 140));
        jpResultadoPMD.remove(jpProveedoresPMD);

        jbGuardarCambiosPMD.setBackground(gris);
        jbGuardarCambiosPMD.setFont(fuenteSans14B);
        jbGuardarCambiosPMD.setForeground(new java.awt.Color(255, 255, 255));
        jbGuardarCambiosPMD.setText("Guardar");
        jbGuardarCambiosPMD.setContentAreaFilled(false);
        jbGuardarCambiosPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbGuardarCambiosPMD.setEnabled(false);
        jbGuardarCambiosPMD.setOpaque(true);
        jbGuardarCambiosPMD.setVisible(false);
        jbGuardarCambiosPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbGuardarCambiosPMDActionPerformed(evt);
            }
        });
        jpResultadoPMD.add(jbGuardarCambiosPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 810, 120, 40));
        jpResultadoPMD.remove(jbGuardarCambiosPMD);

        jpInventarioPMD.setOpaque(false);
        jpInventarioPMD.setLayout(null);

        txtUniDisponiblesPMD.setFont(fuenteSans14B);
        txtUniDisponiblesPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtUniDisponiblesPMD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtUniDisponiblesPMD.setText("Unidades disponibles:");
        jpInventarioPMD.add(txtUniDisponiblesPMD);
        txtUniDisponiblesPMD.setBounds(0, 0, 420, 20);

        txtDisponiblesPMD.setFont(fuenteSans16P);
        txtDisponiblesPMD.setForeground(new java.awt.Color(0, 187, 0));
        txtDisponiblesPMD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDisponiblesPMD.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jpInventarioPMD.add(txtDisponiblesPMD);
        txtDisponiblesPMD.setBounds(0, 30, 420, 20);

        jpESPMD.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(25, 30, 111)), "Ingreso/salidas de productos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, fuenteSans14B, new java.awt.Color(25, 30, 111)));
        jpESPMD.setName(""); // NOI18N
        jpESPMD.setOpaque(false);

        jbEntradasPMD.setBackground(new java.awt.Color(25, 30, 111));
        jbEntradasPMD.setFont(fuenteSans12B);
        jbEntradasPMD.setForeground(new java.awt.Color(255, 255, 255));
        jbEntradasPMD.setText("Entradas");
        jbEntradasPMD.setContentAreaFilled(false);
        jbEntradasPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbEntradasPMD.setOpaque(true);
        jbEntradasPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbEntradasPMDActionPerformed(evt);
            }
        });

        jbSalidasPMD.setBackground(new java.awt.Color(25, 30, 111));
        jbSalidasPMD.setFont(fuenteSans12B);
        jbSalidasPMD.setForeground(new java.awt.Color(255, 255, 255));
        jbSalidasPMD.setText("Salidas");
        jbSalidasPMD.setContentAreaFilled(false);
        jbSalidasPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbSalidasPMD.setOpaque(true);
        jbSalidasPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSalidasPMDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpESPMDLayout = new javax.swing.GroupLayout(jpESPMD);
        jpESPMD.setLayout(jpESPMDLayout);
        jpESPMDLayout.setHorizontalGroup(
            jpESPMDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpESPMDLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jbEntradasPMD, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
                .addComponent(jbSalidasPMD, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
        jpESPMDLayout.setVerticalGroup(
            jpESPMDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpESPMDLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpESPMDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbEntradasPMD, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbSalidasPMD, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpInventarioPMD.add(jpESPMD);
        jpESPMD.setBounds(0, 80, 420, 83);

        jpResultadoPMD.add(jpInventarioPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 860, 420, 170));
        jpResultadoPMD.remove(jpInventarioPMD);

        txtMostrarPMD.setFont(fuenteSans12B);
        txtMostrarPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtMostrarPMD.setText("Mostrar:");
        jpResultadoPMD.add(txtMostrarPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 1050, 70, 40));

        jcbUniMedidaPMD.setFont(fuenteSans12P);
        jcbUniMedidaPMD.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PZ", "KG", "m" }));
        jcbUniMedidaPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jcbUniMedidaPMD.setOpaque(false);
        jcbUniMedidaPMD.setVisible(false);
        jpResultadoPMD.add(jcbUniMedidaPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 500, 80, 40));

        txtPrecioPMD.setFont(fuenteSans14B);
        txtPrecioPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtPrecioPMD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtPrecioPMD.setText("Precio: $");
        jpResultadoPMD.add(txtPrecioPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 600, 90, 40));

        jcbEleccionMostrarPMD.setFont(fuenteSans12P);
        jcbEleccionMostrarPMD.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entradas", "Salidas" }));
        jcbEleccionMostrarPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jcbEleccionMostrarPMD.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbEleccionMostrarPMDItemStateChanged(evt);
            }
        });
        jpResultadoPMD.add(jcbEleccionMostrarPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 1050, 90, 40));

        jdcFechaFinalPMD.setBackground(new java.awt.Color(25, 30, 111));
        jdcFechaFinalPMD.setDateFormatString("dd/MM/yyyy");
        jdcFechaFinalPMD.setFont(fuenteSans12P);
        jdcFechaFinalPMD.getJCalendar().setMaxSelectableDate(new Date());
        jdcFechaFinalPMD.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcFechaFinalPMDPropertyChange(evt);
            }
        });
        jpResultadoPMD.add(jdcFechaFinalPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 1050, 170, 40));

        jdcFechaInicialPMD.setBackground(new java.awt.Color(25, 30, 111));
        jdcFechaInicialPMD.setDateFormatString("dd/MM/yyyy");
        jdcFechaInicialPMD.setFont(fuenteSans12P);
        jdcFechaInicialPMD.getJCalendar().setMaxSelectableDate(jdcFechaFinalPMD.getDate());
        jdcFechaInicialPMD.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcFechaInicialPMDPropertyChange(evt);
            }
        });
        jpResultadoPMD.add(jdcFechaInicialPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 1050, 170, 40));

        txtHastaPMD.setFont(fuenteSans12B);
        txtHastaPMD.setForeground(new java.awt.Color(25, 30, 111));
        txtHastaPMD.setText("Hasta:");
        jpResultadoPMD.add(txtHastaPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 1050, 50, 40));

        txtDesdePMD.setFont(fuenteSans12B);
        txtDesdePMD.setForeground(new java.awt.Color(25, 30, 111));
        txtDesdePMD.setText("Desde:");
        jpResultadoPMD.add(txtDesdePMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 1050, 60, 40));

        jspEntradasSalidasPMD.setOpaque(false);
        jspEntradasSalidasPMD.getViewport().setOpaque(false);

        jtEntradasSalidasPMD.setFont(fuenteSans12P);
        jtEntradasSalidasPMD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Día", "Cantidad", "Detalles"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtEntradasSalidasPMD.setRowHeight(30);
        jtEntradasSalidasPMD.getTableHeader().setFont(fuenteSans12B);
        jtEntradasSalidasPMD.getTableHeader().setOpaque(false);
        jtEntradasSalidasPMD.getTableHeader().setBackground(azulF);
        jtEntradasSalidasPMD.getTableHeader().setForeground(Color.white);
        jspEntradasSalidasPMD.setViewportView(jtEntradasSalidasPMD);

        jpResultadoPMD.add(jspEntradasSalidasPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 1100, 770, 380));
        jpResultadoPMD.remove(jspEntradasSalidasPMD);

        jtbEditarPMD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/EditarOff.png"))); // NOI18N
        jtbEditarPMD.setToolTipText("Modificar producto");
        jtbEditarPMD.setBorderPainted(false);
        jtbEditarPMD.setContentAreaFilled(false);
        jtbEditarPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtbEditarPMD.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/editarON.png"))); // NOI18N
        jtbEditarPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbEditarPMDActionPerformed(evt);
            }
        });
        jpResultadoPMD.add(jtbEditarPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, 40, 40));
        jpResultadoPMD.remove(jtbEditarPMD);

        jbBorrarPMD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/borrarS.png"))); // NOI18N
        jbBorrarPMD.setToolTipText("Eliminar producto");
        jbBorrarPMD.setBorderPainted(false);
        jbBorrarPMD.setContentAreaFilled(false);
        jbBorrarPMD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbBorrarPMD.setEnabled(false);
        jbBorrarPMD.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/borrarN.png"))); // NOI18N
        jbBorrarPMD.setVisible(false);
        jbBorrarPMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbBorrarPMDActionPerformed(evt);
            }
        });
        jpResultadoPMD.add(jbBorrarPMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 60, 40, 40));
        jpResultadoPMD.remove(jbBorrarPMD);

        jspResultadoPMD.setViewportView(jpResultadoPMD);

        panelMostrarProducto.add(jspResultadoPMD);
        jspResultadoPMD.setBounds(10, 70, 800, 660);

        panelPrincipal.add(panelMostrarProducto);
        panelMostrarProducto.setBounds(0, 0, 824, 728);

        panelAgregarProducto.setOpaque(false);
        panelAgregarProducto.setPreferredSize(new java.awt.Dimension(1000, 768));
        panelAgregarProducto.setVisible(false);
        panelAgregarProducto.setLayout(null);

        jpOpcionesPAP.setOpaque(false);
        jpOpcionesPAP.setLayout(null);

        jbAgregarUnProductoPAP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/oProductN.png"))); // NOI18N
        jbAgregarUnProductoPAP.setContentAreaFilled(false);
        jbAgregarUnProductoPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbAgregarUnProductoPAP.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/oProductS.png"))); // NOI18N
        jbAgregarUnProductoPAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarUnProductoPAPActionPerformed(evt);
            }
        });
        jpOpcionesPAP.add(jbAgregarUnProductoPAP);
        jbAgregarUnProductoPAP.setBounds(140, 190, 200, 200);

        jbAgregarVariosProductoPAP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/vProductosN.png"))); // NOI18N
        jbAgregarVariosProductoPAP.setContentAreaFilled(false);
        jbAgregarVariosProductoPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbAgregarVariosProductoPAP.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/vProductosS.png"))); // NOI18N
        jbAgregarVariosProductoPAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarVariosProductoPAPActionPerformed(evt);
            }
        });
        jpOpcionesPAP.add(jbAgregarVariosProductoPAP);
        jbAgregarVariosProductoPAP.setBounds(480, 190, 200, 200);

        txtAgregarVariosProductoPAP.setFont(fuenteSans12B);
        txtAgregarVariosProductoPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtAgregarVariosProductoPAP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAgregarVariosProductoPAP.setText("Agregar varios productos");
        jpOpcionesPAP.add(txtAgregarVariosProductoPAP);
        txtAgregarVariosProductoPAP.setBounds(480, 400, 200, 30);

        txtAgregarUnProductoPAP.setFont(fuenteSans12B);
        txtAgregarUnProductoPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtAgregarUnProductoPAP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAgregarUnProductoPAP.setText("Agregar un solo producto");
        jpOpcionesPAP.add(txtAgregarUnProductoPAP);
        txtAgregarUnProductoPAP.setBounds(140, 400, 200, 30);

        panelAgregarProducto.add(jpOpcionesPAP);
        jpOpcionesPAP.setBounds(0, 0, 824, 728);

        jpCargarProductoPAP.setOpaque(false);
        jpCargarProductoPAP.setLayout(null);

        jbRegresarVariosProductoPAP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/returnN.png"))); // NOI18N
        jbRegresarVariosProductoPAP.setContentAreaFilled(false);
        jbRegresarVariosProductoPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbRegresarVariosProductoPAP.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/returnS.png"))); // NOI18N
        jbRegresarVariosProductoPAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRegresarVariosProductoPAPActionPerformed(evt);
            }
        });
        jpCargarProductoPAP.add(jbRegresarVariosProductoPAP);
        jbRegresarVariosProductoPAP.setBounds(0, 0, 50, 50);

        jpInstruccionesPAP.setOpaque(false);
        jpInstruccionesPAP.setLayout(null);

        txtTituloInstruccionesPAP.setFont(fuenteSans16B);
        txtTituloInstruccionesPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtTituloInstruccionesPAP.setText("Agregar multiples productos");
        jpInstruccionesPAP.add(txtTituloInstruccionesPAP);
        txtTituloInstruccionesPAP.setBounds(0, 0, 560, 30);

        txtInstruccion1PAP.setFont(fuenteSans14P);
        txtInstruccion1PAP.setForeground(new java.awt.Color(25, 30, 111));
        txtInstruccion1PAP.setText("Se cargara un archivo con extensión CSV");
        jpInstruccionesPAP.add(txtInstruccion1PAP);
        txtInstruccion1PAP.setBounds(0, 30, 670, 30);

        txtInstruccion2PAP.setFont(fuenteSans14P);
        txtInstruccion2PAP.setForeground(new java.awt.Color(25, 30, 111));
        txtInstruccion2PAP.setText("IMPORTANTE: Se verificara la integridad de los datos para posteriormente insertarlos");
        txtInstruccion2PAP.setToolTipText("");
        jpInstruccionesPAP.add(txtInstruccion2PAP);
        txtInstruccion2PAP.setBounds(40, 70, 630, 20);

        txtInstruccion3PAP.setFont(fuenteSans14P);
        txtInstruccion3PAP.setForeground(new java.awt.Color(25, 30, 111));
        txtInstruccion3PAP.setText("Productos que no posean clave se generara de la manera predeterminada");
        jpInstruccionesPAP.add(txtInstruccion3PAP);
        txtInstruccion3PAP.setBounds(40, 100, 630, 20);

        txtInstruccion4PAP.setFont(fuenteSans14P);
        txtInstruccion4PAP.setForeground(new java.awt.Color(25, 30, 111));
        txtInstruccion4PAP.setText("Productos sin nombre se descartaran");
        jpInstruccionesPAP.add(txtInstruccion4PAP);
        txtInstruccion4PAP.setBounds(40, 130, 630, 20);

        jpCargarProductoPAP.add(jpInstruccionesPAP);
        jpInstruccionesPAP.setBounds(80, 60, 670, 160);

        jpBotonCSVPAP.setOpaque(false);
        jpBotonCSVPAP.setLayout(null);

        jbCargarCSVPAP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/subirN.png"))); // NOI18N
        jbCargarCSVPAP.setBorderPainted(false);
        jbCargarCSVPAP.setContentAreaFilled(false);
        jbCargarCSVPAP.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/subirS.png"))); // NOI18N
        jbCargarCSVPAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCargarCSVPAPActionPerformed(evt);
            }
        });
        jpBotonCSVPAP.add(jbCargarCSVPAP);
        jbCargarCSVPAP.setBounds(20, 0, 90, 90);

        txtSeleccionarPAP.setFont(fuenteSans12B);
        txtSeleccionarPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtSeleccionarPAP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSeleccionarPAP.setText("Seleccionar archivo");
        jpBotonCSVPAP.add(txtSeleccionarPAP);
        txtSeleccionarPAP.setBounds(0, 90, 130, 20);

        jpCargarProductoPAP.add(jpBotonCSVPAP);
        jpBotonCSVPAP.setBounds(310, 240, 130, 120);

        erroresCSVPanel.setEditable(false);
        erroresCSVPanel.setFont(fuenteSans12P);
        jspErroresCargaPAP.setViewportView(erroresCSVPanel);

        jpCargarProductoPAP.add(jspErroresCargaPAP);
        jspErroresCargaPAP.setBounds(20, 380, 780, 300);
        jspErroresCargaPAP.setVisible(false);

        txtExitoCargaPAP.setFont(fuenteSans14B);
        txtExitoCargaPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtExitoCargaPAP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtExitoCargaPAP.setText("Se completó con éxito la carga de datos");
        jpCargarProductoPAP.add(txtExitoCargaPAP);
        txtExitoCargaPAP.setBounds(0, 400, 824, 14);
        txtExitoCargaPAP.setVisible(false);

        panelAgregarProducto.add(jpCargarProductoPAP);
        jpCargarProductoPAP.setBounds(0, 0, 824, 728);
        jpCargarProductoPAP.setVisible(false);

        jpAgregarProductoPAP.setOpaque(false);
        jpAgregarProductoPAP.setLayout(null);

        jbRegresarOneProductoPAP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/returnN.png"))); // NOI18N
        jbRegresarOneProductoPAP.setContentAreaFilled(false);
        jbRegresarOneProductoPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbRegresarOneProductoPAP.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/returnS.png"))); // NOI18N
        jbRegresarOneProductoPAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRegresarOneProductoPAPActionPerformed(evt);
            }
        });
        jpAgregarProductoPAP.add(jbRegresarOneProductoPAP);
        jbRegresarOneProductoPAP.setBounds(0, 0, 50, 50);

        jpInfoProductoPAP.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 30, 111), 1, true), "Información del producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, fuenteSans16B, new java.awt.Color(25, 30, 111)));
        jpInfoProductoPAP.setOpaque(false);
        jpInfoProductoPAP.setLayout(null);

        jtfNombrePAP.setFont(fuenteSans14P);
        jtfNombrePAP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfNombrePAPKeyReleased(evt);
            }
        });
        ((AbstractDocument)jtfNombrePAP.getDocument()).setDocumentFilter(new UpperCaseDocumentFilter());
        jpInfoProductoPAP.add(jtfNombrePAP);
        jtfNombrePAP.setBounds(150, 40, 300, 40);

        txtNombrePAP.setFont(fuenteSans14P);
        txtNombrePAP.setForeground(new java.awt.Color(25, 30, 111));
        txtNombrePAP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNombrePAP.setText("*Nombre:");
        jpInfoProductoPAP.add(txtNombrePAP);
        txtNombrePAP.setBounds(0, 40, 140, 40);

        txtClaveProductoPAP.setFont(fuenteSans14P);
        txtClaveProductoPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtClaveProductoPAP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtClaveProductoPAP.setText("Clave de producto:");
        jpInfoProductoPAP.add(txtClaveProductoPAP);
        txtClaveProductoPAP.setBounds(0, 90, 140, 40);

        jtfClaveProductoPAP.setFont(fuenteSans14P);
        jtfClaveProductoPAP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfClaveProductoPAPKeyReleased(evt);
            }
        });
        ((AbstractDocument)jtfClaveProductoPAP.getDocument()).setDocumentFilter(new UpperCaseDocumentFilter());
        jpInfoProductoPAP.add(jtfClaveProductoPAP);
        jtfClaveProductoPAP.setBounds(150, 90, 300, 40);

        txtErrorIDPAP.setFont(fuenteSans12B);
        txtErrorIDPAP.setForeground(new java.awt.Color(255, 0, 0));
        txtErrorIDPAP.setText("*Existe un producto con esta clave");
        txtErrorIDPAP.setVisible(false);
        jpInfoProductoPAP.add(txtErrorIDPAP);
        txtErrorIDPAP.setBounds(150, 130, 300, 30);

        txtDescripcionPAP.setFont(fuenteSans14P);
        txtDescripcionPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtDescripcionPAP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtDescripcionPAP.setText("Descripción:");
        jpInfoProductoPAP.add(txtDescripcionPAP);
        txtDescripcionPAP.setBounds(0, 160, 140, 40);

        jtaDescripcionPAP.setColumns(20);
        jtaDescripcionPAP.setFont(fuenteSans14P);
        jtaDescripcionPAP.setRows(5);
        ((AbstractDocument)jtaDescripcionPAP.getDocument()).setDocumentFilter(new UpperCaseDocumentFilter());
        jspDescripcionPAP.setViewportView(jtaDescripcionPAP);

        jpInfoProductoPAP.add(jspDescripcionPAP);
        jspDescripcionPAP.setBounds(150, 160, 300, 110);

        txtValoresRequeridosPAP.setFont(fuenteSans12B);
        txtValoresRequeridosPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtValoresRequeridosPAP.setText("*Valores requeridos");
        jpInfoProductoPAP.add(txtValoresRequeridosPAP);
        txtValoresRequeridosPAP.setBounds(10, 20, 440, 14);

        jtfEmpleoPAP.setFont(fuenteSans14P);
        jtfEmpleoPAP.setToolTipText("Uso del producto");
        ((AbstractDocument)jtfEmpleoPAP.getDocument()).setDocumentFilter(new UpperCaseDocumentFilter());
        jpInfoProductoPAP.add(jtfEmpleoPAP);
        jtfEmpleoPAP.setBounds(150, 280, 300, 40);

        txtEmpleoPAP.setFont(fuenteSans14P);
        txtEmpleoPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtEmpleoPAP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtEmpleoPAP.setText("Uso del producto:");
        jpInfoProductoPAP.add(txtEmpleoPAP);
        txtEmpleoPAP.setBounds(0, 280, 140, 40);

        jcbMaquinariaPAP.setFont(fuenteSans14P);
        jcbMaquinariaPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpInfoProductoPAP.add(jcbMaquinariaPAP);
        jcbMaquinariaPAP.setBounds(150, 330, 300, 40);

        txtMaquinariaPAP.setFont(fuenteSans14P);
        txtMaquinariaPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtMaquinariaPAP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtMaquinariaPAP.setText("Maquinaria:");
        jpInfoProductoPAP.add(txtMaquinariaPAP);
        txtMaquinariaPAP.setBounds(0, 330, 140, 40);

        jpAgregarProductoPAP.add(jpInfoProductoPAP);
        jpInfoProductoPAP.setBounds(0, 50, 460, 380);

        jpProveedoresProductoPAP.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 30, 111), 1, true), "Proveedores:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, fuenteSans16B, new java.awt.Color(25, 30, 111)));
        jpProveedoresProductoPAP.setOpaque(false);
        jpProveedoresProductoPAP.setLayout(null);

        jlProveedoresPAP.setFont(fuenteSans14P);
        jlProveedoresPAP.setModel(listaProveedores = new DefaultListModel());
        jlProveedoresPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlProveedoresPAP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlProveedoresPAPMouseClicked(evt);
            }
        });
        jspProveedoresPAP.setViewportView(jlProveedoresPAP);

        jpProveedoresProductoPAP.add(jspProveedoresPAP);
        jspProveedoresPAP.setBounds(10, 60, 440, 120);

        AutoCompletion.enable(jcbProveedoresPAP);
        jcbProveedoresPAP.setFont(fuenteSans14P);
        jcbProveedoresPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpProveedoresProductoPAP.add(jcbProveedoresPAP);
        jcbProveedoresPAP.setBounds(10, 20, 400, 40);

        jbAgregarProveedorPAP.setBackground(gris);
        jbAgregarProveedorPAP.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jbAgregarProveedorPAP.setForeground(new java.awt.Color(255, 255, 255));
        jbAgregarProveedorPAP.setText("→");
        jbAgregarProveedorPAP.setToolTipText("Agregar proveedor");
        jbAgregarProveedorPAP.setBorder(null);
        jbAgregarProveedorPAP.setContentAreaFilled(false);
        jbAgregarProveedorPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbAgregarProveedorPAP.setEnabled(false);
        jbAgregarProveedorPAP.setOpaque(true);
        jbAgregarProveedorPAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarProveedorPAPActionPerformed(evt);
            }
        });
        jpProveedoresProductoPAP.add(jbAgregarProveedorPAP);
        jbAgregarProveedorPAP.setBounds(410, 20, 40, 40);

        jpAgregarProductoPAP.add(jpProveedoresProductoPAP);
        jpProveedoresProductoPAP.setBounds(0, 440, 460, 190);

        jpIdentificacionProductoPAP.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 30, 111), 1, true), "Identificación del producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, fuenteSans16B, new java.awt.Color(25, 30, 111)));
        jpIdentificacionProductoPAP.setOpaque(false);
        jpIdentificacionProductoPAP.setLayout(null);

        txtUbicacionesPAP.setFont(fuenteSans14P);
        txtUbicacionesPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtUbicacionesPAP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtUbicacionesPAP.setText("Ubicaciones:");
        jpIdentificacionProductoPAP.add(txtUbicacionesPAP);
        txtUbicacionesPAP.setBounds(10, 240, 330, 40);

        jtfUbicacionesPAP.setFont(fuenteSans14P);
        jtfUbicacionesPAP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtfUbicacionesPAPFocusLost(evt);
            }
        });
        jtfUbicacionesPAP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtfUbicacionesPAPKeyPressed(evt);
            }
        });
        ((AbstractDocument)jtfUbicacionesPAP.getDocument()).setDocumentFilter(new UpperCaseDocumentFilter());
        jpIdentificacionProductoPAP.add(jtfUbicacionesPAP);
        jtfUbicacionesPAP.setBounds(10, 280, 290, 40);

        jbAgregarUbicacionesPAP.setBackground(gris);
        jbAgregarUbicacionesPAP.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jbAgregarUbicacionesPAP.setForeground(new java.awt.Color(255, 255, 255));
        jbAgregarUbicacionesPAP.setText("→");
        jbAgregarUbicacionesPAP.setToolTipText("Agregar Ubicación");
        jbAgregarUbicacionesPAP.setBorder(null);
        jbAgregarUbicacionesPAP.setContentAreaFilled(false);
        jbAgregarUbicacionesPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbAgregarUbicacionesPAP.setEnabled(false);
        jbAgregarUbicacionesPAP.setOpaque(true);
        jbAgregarUbicacionesPAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarUbicacionesPAPActionPerformed(evt);
            }
        });
        jpIdentificacionProductoPAP.add(jbAgregarUbicacionesPAP);
        jbAgregarUbicacionesPAP.setBounds(300, 280, 40, 40);

        jlUbicacionesPAP.setFont(fuenteSans14P);
        jlUbicacionesPAP.setModel(listaUbicaciones = new DefaultListModel());
        jlUbicacionesPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlUbicacionesPAP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlUbicacionesPAPMouseClicked(evt);
            }
        });
        jspUbicacionesPAP.setViewportView(jlUbicacionesPAP);

        jpIdentificacionProductoPAP.add(jspUbicacionesPAP);
        jspUbicacionesPAP.setBounds(10, 320, 330, 100);

        jlAgregarImagenPAP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlAgregarImagenPAP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/AgregarImgGrande.png"))); // NOI18N
        jlAgregarImagenPAP.setToolTipText("Agregar imagen");
        jlAgregarImagenPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlAgregarImagenPAP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlAgregarImagenPAPMouseClicked(evt);
            }
        });
        jpIdentificacionProductoPAP.add(jlAgregarImagenPAP);
        jlAgregarImagenPAP.setBounds(20, 20, 310, 200);

        jpAgregarProductoPAP.add(jpIdentificacionProductoPAP);
        jpIdentificacionProductoPAP.setBounds(470, 20, 350, 430);

        jpInventarioPAP.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 30, 111), 1, true), "Inventario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, fuenteSans16B, new java.awt.Color(25, 30, 111)));
        jpInventarioPAP.setOpaque(false);
        jpInventarioPAP.setLayout(null);

        jcbUMedidaPAP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PZ", "KG", "m" }));
        jcbUMedidaPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpInventarioPAP.add(jcbUMedidaPAP);
        jcbUMedidaPAP.setBounds(300, 20, 40, 40);

        jtfCantidadPAP.setFont(fuenteSans14P);
        jtfCantidadPAP.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jtfCantidadPAP.setText("0");
        jtfCantidadPAP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtfCantidadPAPFocusLost(evt);
            }
        });
        jtfCantidadPAP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfCantidadPAPMouseClicked(evt);
            }
        });
        jtfCantidadPAP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfCantidadPAPKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfCantidadPAPKeyTyped(evt);
            }
        });
        jpInventarioPAP.add(jtfCantidadPAP);
        jtfCantidadPAP.setBounds(160, 20, 140, 40);

        txtCantidadPAP.setFont(fuenteSans14P);
        txtCantidadPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtCantidadPAP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtCantidadPAP.setText("Cantidad disponible:");
        jpInventarioPAP.add(txtCantidadPAP);
        txtCantidadPAP.setBounds(0, 20, 150, 40);

        jtfPrecioEstimadoPAP.setFont(fuenteSans14P);
        jtfPrecioEstimadoPAP.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jtfPrecioEstimadoPAP.setText("0");
        jtfPrecioEstimadoPAP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtfPrecioEstimadoPAPFocusLost(evt);
            }
        });
        jtfPrecioEstimadoPAP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfPrecioEstimadoPAPMouseClicked(evt);
            }
        });
        jtfPrecioEstimadoPAP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfPrecioEstimadoPAPKeyTyped(evt);
            }
        });
        jpInventarioPAP.add(jtfPrecioEstimadoPAP);
        jtfPrecioEstimadoPAP.setBounds(160, 120, 180, 40);

        txtPrecioPAP.setFont(fuenteSans14P);
        txtPrecioPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtPrecioPAP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtPrecioPAP.setText("Precio estimado: $");
        jpInventarioPAP.add(txtPrecioPAP);
        txtPrecioPAP.setBounds(0, 120, 160, 40);

        jtfMinimoPAP.setFont(fuenteSans14P);
        jtfMinimoPAP.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jtfMinimoPAP.setText("0");
        jtfMinimoPAP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtfMinimoPAPFocusLost(evt);
            }
        });
        jtfMinimoPAP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfMinimoPAPMouseClicked(evt);
            }
        });
        jtfMinimoPAP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfMinimoPAPKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfMinimoPAPKeyTyped(evt);
            }
        });
        jpInventarioPAP.add(jtfMinimoPAP);
        jtfMinimoPAP.setBounds(160, 70, 180, 40);

        txtMinimoPAP.setFont(fuenteSans14P);
        txtMinimoPAP.setForeground(new java.awt.Color(25, 30, 111));
        txtMinimoPAP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtMinimoPAP.setText("Cantidad minima:");
        jpInventarioPAP.add(txtMinimoPAP);
        txtMinimoPAP.setBounds(0, 70, 150, 40);

        jpAgregarProductoPAP.add(jpInventarioPAP);
        jpInventarioPAP.setBounds(470, 460, 350, 170);

        jbAgregarProductoPAP.setBackground(new java.awt.Color(200, 200, 200));
        jbAgregarProductoPAP.setFont(fuenteSans16B);
        jbAgregarProductoPAP.setForeground(new java.awt.Color(255, 255, 255));
        jbAgregarProductoPAP.setText("Agregar");
        jbAgregarProductoPAP.setContentAreaFilled(false);
        jbAgregarProductoPAP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbAgregarProductoPAP.setEnabled(false);
        jbAgregarProductoPAP.setOpaque(true);
        jbAgregarProductoPAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarProductoPAPActionPerformed(evt);
            }
        });
        jpAgregarProductoPAP.add(jbAgregarProductoPAP);
        jbAgregarProductoPAP.setBounds(350, 650, 170, 70);

        panelAgregarProducto.add(jpAgregarProductoPAP);
        jpAgregarProductoPAP.setBounds(0, 0, 824, 728);
        jpAgregarProductoPAP.setVisible(false);

        panelPrincipal.add(panelAgregarProducto);
        panelAgregarProducto.setBounds(0, 0, 824, 728);

        panelProveedor.setOpaque(false);
        panelProveedor.setVisible(false);
        panelProveedor.setLayout(null);

        jtpPP.setBackground(new java.awt.Color(25, 30, 111));
        jtpPP.setForeground(new java.awt.Color(25, 30, 111));
        jtpPP.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jtpPP.setFont(fuenteSans16P);
        jtpPP.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtpPPStateChanged(evt);
            }
        });

        jpAgregarProveedorPP.setOpaque(false);
        jpAgregarProveedorPP.setLayout(null);

        jpInfoProveedoresPP.setOpaque(false);
        jpInfoProveedoresPP.setLayout(null);

        txtCamposRequeridosPP.setFont(fuenteSans12B);
        txtCamposRequeridosPP.setForeground(new java.awt.Color(25, 30, 111));
        txtCamposRequeridosPP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtCamposRequeridosPP.setText("*Campos requeridos");
        jpInfoProveedoresPP.add(txtCamposRequeridosPP);
        txtCamposRequeridosPP.setBounds(0, 0, 250, 46);

        txtRazonPP.setFont(fuenteSans14P);
        txtRazonPP.setForeground(new java.awt.Color(25, 30, 111));
        txtRazonPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtRazonPP.setText("*Razón social:");
        jpInfoProveedoresPP.add(txtRazonPP);
        txtRazonPP.setBounds(0, 60, 120, 40);

        jtfRazonPP.setFont(fuenteSans12P);
        jtfRazonPP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfRazonPPKeyReleased(evt);
            }
        });
        jpInfoProveedoresPP.add(jtfRazonPP);
        jtfRazonPP.setBounds(130, 60, 450, 40);

        txtMaquinariaPP.setFont(fuenteSans14P);
        txtMaquinariaPP.setForeground(new java.awt.Color(25, 30, 111));
        txtMaquinariaPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtMaquinariaPP.setText("Maquinaria:");
        jpInfoProveedoresPP.add(txtMaquinariaPP);
        txtMaquinariaPP.setBounds(0, 120, 120, 40);

        jcbMaquinariaPP.setFont(fuenteSans12P);
        jcbMaquinariaPP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpInfoProveedoresPP.add(jcbMaquinariaPP);
        jcbMaquinariaPP.setBounds(130, 120, 300, 40);

        txtRepresentantePP.setFont(fuenteSans14P);
        txtRepresentantePP.setForeground(new java.awt.Color(25, 30, 111));
        txtRepresentantePP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtRepresentantePP.setText("*Representante:");
        jpInfoProveedoresPP.add(txtRepresentantePP);
        txtRepresentantePP.setBounds(0, 180, 120, 40);

        jtfRepresentantePP.setFont(fuenteSans12P);
        jtfRepresentantePP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfRepresentantePPKeyReleased(evt);
            }
        });
        jpInfoProveedoresPP.add(jtfRepresentantePP);
        jtfRepresentantePP.setBounds(130, 180, 450, 40);

        txtCorreoPP.setFont(fuenteSans14P);
        txtCorreoPP.setForeground(new java.awt.Color(25, 30, 111));
        txtCorreoPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtCorreoPP.setText("*Correo:");
        jpInfoProveedoresPP.add(txtCorreoPP);
        txtCorreoPP.setBounds(0, 240, 120, 40);

        jtfCorreoPP.setFont(fuenteSans12P);
        jtfCorreoPP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtfCorreoPPKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfCorreoPPKeyReleased(evt);
            }
        });
        jpInfoProveedoresPP.add(jtfCorreoPP);
        jtfCorreoPP.setBounds(130, 240, 450, 40);

        txtCorreoNoValidoPP.setFont(fuenteSans12P);
        txtCorreoNoValidoPP.setForeground(new java.awt.Color(255, 0, 0));
        txtCorreoNoValidoPP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtCorreoNoValidoPP.setText("*Correo no valido");
        txtCorreoNoValidoPP.setVisible(false);
        jpInfoProveedoresPP.add(txtCorreoNoValidoPP);
        txtCorreoNoValidoPP.setBounds(130, 280, 140, 30);

        txtTelefonoPP.setFont(fuenteSans14P);
        txtTelefonoPP.setForeground(new java.awt.Color(25, 30, 111));
        txtTelefonoPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTelefonoPP.setText("*Telefono:");
        jpInfoProveedoresPP.add(txtTelefonoPP);
        txtTelefonoPP.setBounds(0, 310, 120, 40);

        jtfTelefonoPP.setFont(fuenteSans12P);
        jtfTelefonoPP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfTelefonoPPKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfTelefonoPPKeyTyped(evt);
            }
        });
        jpInfoProveedoresPP.add(jtfTelefonoPP);
        jtfTelefonoPP.setBounds(130, 310, 250, 40);

        txtDireccionPP.setFont(fuenteSans14P);
        txtDireccionPP.setForeground(new java.awt.Color(25, 30, 111));
        txtDireccionPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtDireccionPP.setText("*Dirección:");
        jpInfoProveedoresPP.add(txtDireccionPP);
        txtDireccionPP.setBounds(0, 370, 120, 40);

        jtfDireccionPP.setFont(fuenteSans12P);
        jtfDireccionPP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfDireccionPPKeyReleased(evt);
            }
        });
        jpInfoProveedoresPP.add(jtfDireccionPP);
        jtfDireccionPP.setBounds(130, 370, 450, 40);

        jpAgregarProveedorPP.add(jpInfoProveedoresPP);
        jpInfoProveedoresPP.setBounds(30, 20, 580, 440);

        jbAgregarProveedorPP.setBackground(gris);
        jbAgregarProveedorPP.setFont(fuenteSans14B);
        jbAgregarProveedorPP.setForeground(new java.awt.Color(255, 255, 255));
        jbAgregarProveedorPP.setText("Agregar");
        jbAgregarProveedorPP.setBorderPainted(false);
        jbAgregarProveedorPP.setContentAreaFilled(false);
        jbAgregarProveedorPP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbAgregarProveedorPP.setEnabled(false);
        jbAgregarProveedorPP.setOpaque(true);
        jbAgregarProveedorPP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarProveedorPPActionPerformed(evt);
            }
        });
        jpAgregarProveedorPP.add(jbAgregarProveedorPP);
        jbAgregarProveedorPP.setBounds(360, 550, 100, 46);

        jtpPP.addTab("Agregar proveedor", jpAgregarProveedorPP);

        jpListaProveedoresPP.setOpaque(false);
        jpListaProveedoresPP.setLayout(null);

        jcbSortListaProveedoresPP.setFont(fuenteSans12P);
        jcbSortListaProveedoresPP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Razon", "Maquinaria", "Representante", "Email", "Telefono", "Direccion" }));
        jcbSortListaProveedoresPP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jcbSortListaProveedoresPP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbSortListaProveedoresPPItemStateChanged(evt);
            }
        });
        jpListaProveedoresPP.add(jcbSortListaProveedoresPP);
        jcbSortListaProveedoresPP.setBounds(130, 10, 210, 40);

        jspTablaProveedoresPP.setBorder(null);
        jspTablaProveedoresPP.setOpaque(false);
        jspTablaProveedoresPP.setOpaque(false);
        jspTablaProveedoresPP.getViewport().setOpaque(false);

        jtProveedoresPP.setFont(fuenteSans12P);
        jtProveedoresPP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Razón social", "Maquinaria", "Representante", "Correo", "Telefono", "Dirrecion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtProveedoresPP.setOpaque(false);
        jtProveedoresPP.getTableHeader().setFont(fuenteSans12B);
        jtProveedoresPP.getTableHeader().setOpaque(false);
        jtProveedoresPP.getTableHeader().setBackground(azulF);
        jtProveedoresPP.getTableHeader().setForeground(Color.white);
        jtProveedoresPP.setRowHeight(30);
        jspTablaProveedoresPP.setViewportView(jtProveedoresPP);

        jpListaProveedoresPP.add(jspTablaProveedoresPP);
        jspTablaProveedoresPP.setBounds(10, 60, 780, 600);

        txtOrdenarPP.setFont(fuenteSans12B);
        txtOrdenarPP.setForeground(new java.awt.Color(25, 30, 111));
        txtOrdenarPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtOrdenarPP.setText("Ordenar por:");
        jpListaProveedoresPP.add(txtOrdenarPP);
        txtOrdenarPP.setBounds(10, 10, 110, 40);

        jtfBuscarPP.setFont(fuenteSans12P);
        jtfBuscarPP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfBuscarPPKeyReleased(evt);
            }
        });
        jpListaProveedoresPP.add(jtfBuscarPP);
        jtfBuscarPP.setBounds(520, 10, 270, 40);

        txtBuscarPP.setFont(fuenteSans12B);
        txtBuscarPP.setForeground(new java.awt.Color(25, 30, 111));
        txtBuscarPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtBuscarPP.setText("Buscar:");
        jpListaProveedoresPP.add(txtBuscarPP);
        txtBuscarPP.setBounds(410, 10, 100, 40);

        jtbSortProveedoresPP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/sortdesc.png"))); // NOI18N
        jtbSortProveedoresPP.setContentAreaFilled(false);
        jtbSortProveedoresPP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtbSortProveedoresPP.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/sortasc.png"))); // NOI18N
        jtbSortProveedoresPP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jtbSortProveedoresPPItemStateChanged(evt);
            }
        });
        jpListaProveedoresPP.add(jtbSortProveedoresPP);
        jtbSortProveedoresPP.setBounds(350, 10, 40, 40);

        jtpPP.addTab("Lista de proveedores", jpListaProveedoresPP);

        panelProveedor.add(jtpPP);
        jtpPP.setBounds(10, 10, 800, 720);

        panelPrincipal.add(panelProveedor);
        panelProveedor.setBounds(0, 0, 824, 728);

        panelUsuario.setVisible(false);
        panelUsuario.setOpaque(false);
        panelUsuario.setLayout(null);

        jtpPU.setForeground(new java.awt.Color(25, 30, 111));
        jtpPU.setFont(fuenteSans16P);
        jtpPU.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtpPUStateChanged(evt);
            }
        });

        jpCrearUsuarioPU.setOpaque(false);
        jpCrearUsuarioPU.setLayout(null);

        jpInformacionUsuarioPU.setOpaque(false);
        jpInformacionUsuarioPU.setLayout(null);

        txtNombreDeUsuarioPU.setFont(fuenteSans14B);
        txtNombreDeUsuarioPU.setForeground(new java.awt.Color(25, 30, 111));
        txtNombreDeUsuarioPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNombreDeUsuarioPU.setText("Nombre de usuario:");
        jpInformacionUsuarioPU.add(txtNombreDeUsuarioPU);
        txtNombreDeUsuarioPU.setBounds(0, 0, 170, 40);

        jtfUsuarioPU.setFont(fuenteSans14P);
        jtfUsuarioPU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfUsuarioPUKeyReleased(evt);
            }
        });
        jpInformacionUsuarioPU.add(jtfUsuarioPU);
        jtfUsuarioPU.setBounds(180, 0, 400, 40);

        txtUsuarioYaExistentePU.setFont(fuenteSans14P);
        txtUsuarioYaExistentePU.setForeground(new java.awt.Color(255, 0, 0));
        txtUsuarioYaExistentePU.setText("Ya existe un usuario con este nombre de usuario");
        txtUsuarioYaExistentePU.setVisible(false);
        jpInformacionUsuarioPU.add(txtUsuarioYaExistentePU);
        txtUsuarioYaExistentePU.setBounds(180, 40, 400, 20);

        txtPasswordPU.setFont(fuenteSans14B);
        txtPasswordPU.setForeground(new java.awt.Color(25, 30, 111));
        txtPasswordPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtPasswordPU.setText("Contraseña:");
        jpInformacionUsuarioPU.add(txtPasswordPU);
        txtPasswordPU.setBounds(0, 70, 170, 40);

        jpfPasswordPU.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jpfPasswordPU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jpfPasswordPUKeyReleased(evt);
            }
        });
        jpInformacionUsuarioPU.add(jpfPasswordPU);
        jpfPasswordPU.setBounds(180, 70, 400, 40);

        txtConfirmarContrasenaPU.setFont(fuenteSans14B);
        txtConfirmarContrasenaPU.setForeground(new java.awt.Color(25, 30, 111));
        txtConfirmarContrasenaPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtConfirmarContrasenaPU.setText("Confirmar contraseña:");
        jpInformacionUsuarioPU.add(txtConfirmarContrasenaPU);
        txtConfirmarContrasenaPU.setBounds(0, 130, 170, 40);

        jpfConfirmarPasswordPu.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jpfConfirmarPasswordPu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jpfConfirmarPasswordPuKeyReleased(evt);
            }
        });
        jpInformacionUsuarioPU.add(jpfConfirmarPasswordPu);
        jpfConfirmarPasswordPu.setBounds(180, 130, 400, 40);

        txtNombrePU.setFont(fuenteSans14B);
        txtNombrePU.setForeground(new java.awt.Color(25, 30, 111));
        txtNombrePU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNombrePU.setText("Nombre:");
        jpInformacionUsuarioPU.add(txtNombrePU);
        txtNombrePU.setBounds(0, 200, 170, 40);

        jtfNombrePU.setFont(fuenteSans14P);
        jtfNombrePU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfNombrePUKeyReleased(evt);
            }
        });
        ((AbstractDocument)jtfNombrePU.getDocument()).setDocumentFilter(new UpperCaseDocumentFilter());
        jpInformacionUsuarioPU.add(jtfNombrePU);
        jtfNombrePU.setBounds(180, 200, 400, 40);

        txtApellidosPU.setFont(fuenteSans14B);
        txtApellidosPU.setForeground(new java.awt.Color(25, 30, 111));
        txtApellidosPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtApellidosPU.setText("Apellidos:");
        jpInformacionUsuarioPU.add(txtApellidosPU);
        txtApellidosPU.setBounds(0, 260, 170, 40);

        jtfApellidosPU.setFont(fuenteSans14P);
        jtfApellidosPU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfApellidosPUKeyReleased(evt);
            }
        });
        ((AbstractDocument)jtfApellidosPU.getDocument()).setDocumentFilter(new UpperCaseDocumentFilter());
        jpInformacionUsuarioPU.add(jtfApellidosPU);
        jtfApellidosPU.setBounds(180, 260, 400, 40);

        txtTipoUsuarioPU.setFont(fuenteSans14B);
        txtTipoUsuarioPU.setForeground(new java.awt.Color(25, 30, 111));
        txtTipoUsuarioPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTipoUsuarioPU.setText("Tipo de usuario:");
        jpInformacionUsuarioPU.add(txtTipoUsuarioPU);
        txtTipoUsuarioPU.setBounds(0, 320, 170, 40);

        jcbTipoUsuarioPU.setFont(fuenteSans14P);
        jcbTipoUsuarioPU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador", "Operador" }));
        jcbTipoUsuarioPU.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpInformacionUsuarioPU.add(jcbTipoUsuarioPU);
        jcbTipoUsuarioPU.setBounds(180, 320, 190, 40);

        txtPasswordDiferentePU.setFont(fuenteSans14P);
        txtPasswordDiferentePU.setForeground(new java.awt.Color(255, 0, 0));
        txtPasswordDiferentePU.setText("La contraseña no coincide");
        txtPasswordDiferentePU.setVisible(false);
        jpInformacionUsuarioPU.add(txtPasswordDiferentePU);
        txtPasswordDiferentePU.setBounds(180, 170, 400, 20);

        txtLogitudMenorPU.setFont(fuenteSans14P);
        txtLogitudMenorPU.setForeground(new java.awt.Color(255, 0, 0));
        txtLogitudMenorPU.setText("Longitud de la contraseña muy corta");
        txtLogitudMenorPU.setVisible(false);
        jpInformacionUsuarioPU.add(txtLogitudMenorPU);
        txtLogitudMenorPU.setBounds(180, 170, 400, 20);

        jpCrearUsuarioPU.add(jpInformacionUsuarioPU);
        jpInformacionUsuarioPU.setBounds(50, 40, 580, 370);

        jbCrearUsuarioPU.setBackground(gris);
        jbCrearUsuarioPU.setFont(fuenteSans14B);
        jbCrearUsuarioPU.setForeground(new java.awt.Color(255, 255, 255));
        jbCrearUsuarioPU.setText("Crear usuario");
        jbCrearUsuarioPU.setBorder(null);
        jbCrearUsuarioPU.setBorderPainted(false);
        jbCrearUsuarioPU.setContentAreaFilled(false);
        jbCrearUsuarioPU.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbCrearUsuarioPU.setEnabled(false);
        jbCrearUsuarioPU.setOpaque(true);
        jbCrearUsuarioPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCrearUsuarioPUActionPerformed(evt);
            }
        });
        jpCrearUsuarioPU.add(jbCrearUsuarioPU);
        jbCrearUsuarioPU.setBounds(420, 450, 130, 50);

        jtpPU.addTab("Crear usuario", jpCrearUsuarioPU);

        jpAdministrarUsuariosPU.setOpaque(false);
        jpAdministrarUsuariosPU.setLayout(null);

        jspUsuariosPU.setOpaque(false);
        jspUsuariosPU.getViewport().setOpaque(false);

        jtUsuarios.setDefaultRenderer(Object.class, new Render());
        jtUsuarios.setFont(fuenteSans12P);
        jtUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre de usuario", "Nombre", "Apellidos", "Administrar"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtUsuarios.getTableHeader().setFont(fuenteSans12B);
        jtUsuarios.getTableHeader().setOpaque(false);
        jtUsuarios.getTableHeader().setBackground(azulF);
        jtUsuarios.getTableHeader().setForeground(Color.white);
        jtUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtUsuariosMouseClicked(evt);
            }
        });
        jspUsuariosPU.setViewportView(jtUsuarios);

        jpAdministrarUsuariosPU.add(jspUsuariosPU);
        jspUsuariosPU.setBounds(10, 10, 780, 680);

        jpUsuarioPU.setOpaque(false);
        jpUsuarioPU.setRequestFocusEnabled(false);
        jpUsuarioPU.setLayout(null);

        jbRegresarUsuarioPU.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/returnN.png"))); // NOI18N
        jbRegresarUsuarioPU.setBorderPainted(false);
        jbRegresarUsuarioPU.setContentAreaFilled(false);
        jbRegresarUsuarioPU.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/returnS.png"))); // NOI18N
        jbRegresarUsuarioPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRegresarUsuarioPUActionPerformed(evt);
            }
        });
        jpUsuarioPU.add(jbRegresarUsuarioPU);
        jbRegresarUsuarioPU.setBounds(0, 0, 50, 50);

        jpInfoUsuarioPU.setOpaque(false);
        jpInfoUsuarioPU.setLayout(null);

        txtUsuarioInfoPU.setFont(fuenteSans14P);
        txtUsuarioInfoPU.setForeground(new java.awt.Color(25, 30, 111));
        txtUsuarioInfoPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtUsuarioInfoPU.setText("Usuario:");
        jpInfoUsuarioPU.add(txtUsuarioInfoPU);
        txtUsuarioInfoPU.setBounds(0, 0, 170, 40);

        txtUserNamePU.setFont(fuenteSans14P);
        jpInfoUsuarioPU.add(txtUserNamePU);
        txtUserNamePU.setBounds(180, 0, 380, 40);

        txtNombreInfoPU.setFont(fuenteSans14P);
        txtNombreInfoPU.setForeground(new java.awt.Color(25, 30, 111));
        txtNombreInfoPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNombreInfoPU.setText("Nombre:");
        jpInfoUsuarioPU.add(txtNombreInfoPU);
        txtNombreInfoPU.setBounds(0, 60, 170, 40);

        txtNombreUsuarioPU.setFont(fuenteSans14P);
        jpInfoUsuarioPU.add(txtNombreUsuarioPU);
        txtNombreUsuarioPU.setBounds(180, 60, 380, 40);

        txtApellidosInfoPU.setFont(fuenteSans14P);
        txtApellidosInfoPU.setForeground(new java.awt.Color(25, 30, 111));
        txtApellidosInfoPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtApellidosInfoPU.setText("Apellidos:");
        jpInfoUsuarioPU.add(txtApellidosInfoPU);
        txtApellidosInfoPU.setBounds(0, 120, 170, 40);

        txtApellidosUsuarioPU.setFont(fuenteSans14P);
        jpInfoUsuarioPU.add(txtApellidosUsuarioPU);
        txtApellidosUsuarioPU.setBounds(180, 120, 380, 40);

        txtCambiarPasswordPU.setFont(fuenteSans14P);
        txtCambiarPasswordPU.setForeground(new java.awt.Color(25, 30, 111));
        txtCambiarPasswordPU.setText("Cambiar contraseña:");
        jpInfoUsuarioPU.add(txtCambiarPasswordPU);
        txtCambiarPasswordPU.setBounds(0, 180, 260, 40);

        txtContrasenaModificarPU.setFont(fuenteSans14P);
        txtContrasenaModificarPU.setForeground(new java.awt.Color(25, 30, 111));
        txtContrasenaModificarPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtContrasenaModificarPU.setText("Contraseña:");
        jpInfoUsuarioPU.add(txtContrasenaModificarPU);
        txtContrasenaModificarPU.setBounds(0, 240, 170, 40);

        jpfPasswordUsuarioPU.setFont(fuenteSans14P);
        jpfPasswordUsuarioPU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jpfPasswordUsuarioPUKeyReleased(evt);
            }
        });
        jpInfoUsuarioPU.add(jpfPasswordUsuarioPU);
        jpfPasswordUsuarioPU.setBounds(180, 240, 380, 40);

        txtContrasenaConfirmarModificarPU.setFont(fuenteSans14P);
        txtContrasenaConfirmarModificarPU.setForeground(new java.awt.Color(25, 30, 111));
        txtContrasenaConfirmarModificarPU.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtContrasenaConfirmarModificarPU.setText("Confirmar contraseña:");
        jpInfoUsuarioPU.add(txtContrasenaConfirmarModificarPU);
        txtContrasenaConfirmarModificarPU.setBounds(0, 300, 170, 40);

        jpfPasswordConfUsuarioPU.setFont(fuenteSans14P);
        jpfPasswordConfUsuarioPU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jpfPasswordConfUsuarioPUKeyReleased(evt);
            }
        });
        jpInfoUsuarioPU.add(jpfPasswordConfUsuarioPU);
        jpfPasswordConfUsuarioPU.setBounds(180, 300, 380, 40);

        txtPasswordDiferenteUsuarioPU.setFont(fuenteSans12P);
        txtPasswordDiferenteUsuarioPU.setForeground(new java.awt.Color(255, 0, 0));
        txtPasswordDiferenteUsuarioPU.setText("La contraseña no coincide");
        txtPasswordDiferenteUsuarioPU.setVisible(false);
        jpInfoUsuarioPU.add(txtPasswordDiferenteUsuarioPU);
        txtPasswordDiferenteUsuarioPU.setBounds(180, 340, 380, 14);

        txtPasswordLongitudMenorPU.setFont(fuenteSans12P);
        txtPasswordLongitudMenorPU.setForeground(new java.awt.Color(255, 0, 0));
        txtPasswordLongitudMenorPU.setText("Longitud de la contraseña muy corta");
        txtPasswordLongitudMenorPU.setVisible(false);
        jpInfoUsuarioPU.add(txtPasswordLongitudMenorPU);
        txtPasswordLongitudMenorPU.setBounds(180, 340, 380, 14);

        jpUsuarioPU.add(jpInfoUsuarioPU);
        jpInfoUsuarioPU.setBounds(130, 70, 560, 360);

        jbCambiarInfoUsuarioPU.setBackground(gris);
        jbCambiarInfoUsuarioPU.setFont(fuenteSans14P);
        jbCambiarInfoUsuarioPU.setText("Guardar");
        jbCambiarInfoUsuarioPU.setContentAreaFilled(false);
        jbCambiarInfoUsuarioPU.setEnabled(false);
        jbCambiarInfoUsuarioPU.setOpaque(true);
        jbCambiarInfoUsuarioPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCambiarInfoUsuarioPUActionPerformed(evt);
            }
        });
        jpUsuarioPU.add(jbCambiarInfoUsuarioPU);
        jbCambiarInfoUsuarioPU.setBounds(350, 500, 130, 60);

        jpUsuarioPU.setVisible(false);

        jpAdministrarUsuariosPU.add(jpUsuarioPU);
        jpUsuarioPU.setBounds(10, 10, 780, 680);

        jtpPU.addTab("Admistrar usuarios", jpAdministrarUsuariosPU);

        panelUsuario.add(jtpPU);
        jtpPU.setBounds(10, 10, 804, 730);

        panelPrincipal.add(panelUsuario);
        panelUsuario.setBounds(0, 0, 824, 728);

        panelMaquinaria.setOpaque(false);
        panelMaquinaria.setVisible(false);
        panelMaquinaria.setLayout(null);

        jpInfoMaquina.setOpaque(false);
        jpInfoMaquina.setLayout(null);

        txtAgregarMaquinariaPM.setFont(fuenteSans16B);
        txtAgregarMaquinariaPM.setForeground(new java.awt.Color(25, 30, 111));
        txtAgregarMaquinariaPM.setText("Agregar Maquinaria:");
        jpInfoMaquina.add(txtAgregarMaquinariaPM);
        txtAgregarMaquinariaPM.setBounds(22, 11, 423, 40);

        txtNombrePM.setFont(fuenteSans14P);
        txtNombrePM.setForeground(new java.awt.Color(25, 30, 111));
        txtNombrePM.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNombrePM.setText("Nombre:");
        jpInfoMaquina.add(txtNombrePM);
        txtNombrePM.setBounds(44, 89, 70, 40);

        jtfNombrePM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfNombrePMKeyReleased(evt);
            }
        });
        jpInfoMaquina.add(jtfNombrePM);
        jtfNombrePM.setBounds(121, 89, 441, 40);

        jbAgregarMaquinariaPM.setBackground(gris);
        jbAgregarMaquinariaPM.setFont(fuenteSans12B);
        jbAgregarMaquinariaPM.setForeground(new java.awt.Color(255, 255, 255));
        jbAgregarMaquinariaPM.setText("Agregar");
        jbAgregarMaquinariaPM.setContentAreaFilled(false);
        jbAgregarMaquinariaPM.setEnabled(false);
        jbAgregarMaquinariaPM.setOpaque(true);
        jbAgregarMaquinariaPM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAgregarMaquinariaPMActionPerformed(evt);
            }
        });
        jpInfoMaquina.add(jbAgregarMaquinariaPM);
        jbAgregarMaquinariaPM.setBounds(568, 89, 130, 40);

        jspMaquinariaPM.setOpaque(false);
        jspMaquinariaPM.getViewport().setOpaque(false);

        jtMaquinariasPM.setDefaultRenderer(Object.class, new Render());
        jtMaquinariasPM.setFont(fuenteSans12P);
        jtMaquinariasPM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Maquinaria"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtMaquinariasPM.getTableHeader().setFont(fuenteSans12B);
        jtMaquinariasPM.getTableHeader().setOpaque(false);
        jtMaquinariasPM.getTableHeader().setBackground(azulF);
        jtMaquinariasPM.getTableHeader().setForeground(Color.white);
        jtMaquinariasPM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtMaquinariasPMMouseClicked(evt);
            }
        });
        jspMaquinariaPM.setViewportView(jtMaquinariasPM);

        jpInfoMaquina.add(jspMaquinariaPM);
        jspMaquinariaPM.setBounds(100, 150, 600, 328);

        panelMaquinaria.add(jpInfoMaquina);
        jpInfoMaquina.setBounds(10, 40, 800, 510);

        panelPrincipal.add(panelMaquinaria);
        panelMaquinaria.setBounds(0, 0, 824, 728);

        panelRegistro.setOpaque(false);
        panelRegistro.setVisible(false);
        panelRegistro.setLayout(null);

        txtOrdenarPR.setFont(fuenteSans12B);
        txtOrdenarPR.setForeground(new java.awt.Color(25, 30, 111));
        txtOrdenarPR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtOrdenarPR.setText("Ordenar por:");
        panelRegistro.add(txtOrdenarPR);
        txtOrdenarPR.setBounds(10, 10, 80, 40);

        jcbOrdenacionPR.setFont(fuenteSans12P);
        jcbOrdenacionPR.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Clave producto", "Nombre", "Fecha", "Cantidad", "Total", "Detalles" }));
        jcbOrdenacionPR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbOrdenacionPRItemStateChanged(evt);
            }
        });
        panelRegistro.add(jcbOrdenacionPR);
        jcbOrdenacionPR.setBounds(100, 10, 140, 40);

        txtMostrarPR.setFont(fuenteSans12B);
        txtMostrarPR.setForeground(new java.awt.Color(25, 30, 111));
        txtMostrarPR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtMostrarPR.setText("Mostrar:");
        panelRegistro.add(txtMostrarPR);
        txtMostrarPR.setBounds(250, 10, 60, 40);

        jcbMostrarPR.setFont(fuenteSans12P);
        jcbMostrarPR.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entradas/salidas", "Entradas", "Salidas" }));
        jcbMostrarPR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbMostrarPRItemStateChanged(evt);
            }
        });
        panelRegistro.add(jcbMostrarPR);
        jcbMostrarPR.setBounds(320, 10, 130, 40);

        txtDesdePR.setFont(fuenteSans12B);
        txtDesdePR.setForeground(new java.awt.Color(25, 30, 111));
        txtDesdePR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtDesdePR.setText("Desde:");
        panelRegistro.add(txtDesdePR);
        txtDesdePR.setBounds(460, 10, 40, 40);

        fechaInicio.setBackground(new java.awt.Color(25, 30, 111));
        fechaInicio.setDateFormatString("dd/MM/yyyy");
        fechaInicio.setFont(fuenteSans12P);
        fechaInicio.getJCalendar().setMaxSelectableDate(fechaInicio.getDate());
        fechaInicio.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fechaInicioPropertyChange(evt);
            }
        });
        panelRegistro.add(fechaInicio);
        fechaInicio.setBounds(510, 10, 120, 40);

        txtHastaPR.setFont(fuenteSans12B);
        txtHastaPR.setForeground(new java.awt.Color(25, 30, 111));
        txtHastaPR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtHastaPR.setText("Hasta:");
        panelRegistro.add(txtHastaPR);
        txtHastaPR.setBounds(640, 10, 40, 40);

        fechaFinal.setBackground(new java.awt.Color(25, 30, 111));
        fechaFinal.setDateFormatString("dd/MM/yyyy");
        fechaFinal.setFont(fuenteSans12P);
        fechaFinal.getJCalendar().setMaxSelectableDate(new Date());
        fechaFinal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fechaFinalPropertyChange(evt);
            }
        });
        panelRegistro.add(fechaFinal);
        fechaFinal.setBounds(690, 10, 120, 40);

        jspRegistrosPR.setOpaque(false);
        jspRegistrosPR.getViewport().setOpaque(false);
        jspRegistrosPR.setFont(fuenteSans12P);

        jtRegistrosPR.setFont(fuenteSans12P);
        jtRegistrosPR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Clave producto", "Nombre", "Fecha", "Cantidad", "Total", "Detalles"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtRegistrosPR.getTableHeader().setFont(fuenteSans12B);
        jtRegistrosPR.getTableHeader().setOpaque(false);
        jtRegistrosPR.getTableHeader().setBackground(azulF);
        jtRegistrosPR.getTableHeader().setForeground(Color.white);
        jtRegistrosPR.setRowHeight(30);
        jspRegistrosPR.setViewportView(jtRegistrosPR);

        panelRegistro.add(jspRegistrosPR);
        jspRegistrosPR.setBounds(10, 70, 800, 665);

        panelPrincipal.add(panelRegistro);
        panelRegistro.setBounds(0, 0, 824, 728);

        panelConfiguracion.setOpaque(false);
        panelConfiguracion.setLayout(null);

        jpPasswordPC.setOpaque(false);
        jpPasswordPC.setLayout(null);

        txtCambiarPasswordPC.setFont(fuenteSans16B);
        txtCambiarPasswordPC.setForeground(new java.awt.Color(25, 30, 111));
        txtCambiarPasswordPC.setText("Cambiar contraseña");
        jpPasswordPC.add(txtCambiarPasswordPC);
        txtCambiarPasswordPC.setBounds(0, 0, 217, 40);

        txtPasswordPC.setFont(fuenteSans12P);
        txtPasswordPC.setForeground(new java.awt.Color(25, 30, 111));
        txtPasswordPC.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtPasswordPC.setText("Contraseña:");
        jpPasswordPC.add(txtPasswordPC);
        txtPasswordPC.setBounds(0, 80, 170, 40);

        jpfPasswordPC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jpfPasswordPCKeyReleased(evt);
            }
        });
        jpPasswordPC.add(jpfPasswordPC);
        jpfPasswordPC.setBounds(180, 80, 400, 40);

        txtConfirmarContrasenaPC.setFont(fuenteSans12P);
        txtConfirmarContrasenaPC.setForeground(new java.awt.Color(25, 30, 111));
        txtConfirmarContrasenaPC.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtConfirmarContrasenaPC.setText("Confirmar contraseña:");
        jpPasswordPC.add(txtConfirmarContrasenaPC);
        txtConfirmarContrasenaPC.setBounds(0, 160, 170, 40);

        jpfConfirmarPasswordPC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jpfConfirmarPasswordPCKeyReleased(evt);
            }
        });
        jpPasswordPC.add(jpfConfirmarPasswordPC);
        jpfConfirmarPasswordPC.setBounds(180, 160, 400, 40);

        txtPasswordDiferentePC.setFont(fuenteSans12P);
        txtPasswordDiferentePC.setForeground(new java.awt.Color(255, 0, 0));
        txtPasswordDiferentePC.setText("La contraseña no coincide");
        txtPasswordDiferentePC.setVisible(false);
        jpPasswordPC.add(txtPasswordDiferentePC);
        txtPasswordDiferentePC.setBounds(180, 200, 390, 20);

        txtLongitudMenorPC.setFont(fuenteSans12P);
        txtLongitudMenorPC.setForeground(new java.awt.Color(255, 0, 0));
        txtLongitudMenorPC.setText("Longitud de la contraseña muy corta");
        txtLongitudMenorPC.setVisible(false);
        jpPasswordPC.add(txtLongitudMenorPC);
        txtLongitudMenorPC.setBounds(180, 200, 390, 20);

        txtPasswordCambiadaPC.setFont(fuenteSans12P);
        txtPasswordCambiadaPC.setForeground(new java.awt.Color(25, 30, 111));
        txtPasswordCambiadaPC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPasswordCambiadaPC.setText("Contraseña cambiada con exito");
        txtPasswordCambiadaPC.setVisible(true);
        jpPasswordPC.add(txtPasswordCambiadaPC);
        txtPasswordCambiadaPC.setBounds(0, 240, 580, 20);

        panelConfiguracion.add(jpPasswordPC);
        jpPasswordPC.setBounds(80, 70, 580, 280);

        jbCambiarPasswordPC.setBackground(gris);
        jbCambiarPasswordPC.setFont(fuenteSans12B);
        jbCambiarPasswordPC.setForeground(new java.awt.Color(255, 255, 255));
        jbCambiarPasswordPC.setText("Aceptar");
        jbCambiarPasswordPC.setBorderPainted(false);
        jbCambiarPasswordPC.setContentAreaFilled(false);
        jbCambiarPasswordPC.setEnabled(false);
        jbCambiarPasswordPC.setOpaque(true);
        jbCambiarPasswordPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCambiarPasswordPCActionPerformed(evt);
            }
        });
        panelConfiguracion.add(jbCambiarPasswordPC);
        jbCambiarPasswordPC.setBounds(320, 350, 110, 50);

        panelConfiguracion.setVisible(false);

        panelPrincipal.add(panelConfiguracion);
        panelConfiguracion.setBounds(0, 0, 824, 728);

        panelMinimos.setOpaque(false);
        panelMinimos.setLayout(null);

        jtbSortPMin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/sortdesc.png"))); // NOI18N
        jtbSortPMin.setContentAreaFilled(false);
        jtbSortPMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtbSortPMin.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/sortasc.png"))); // NOI18N
        jtbSortPMin.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jtbSortPMinItemStateChanged(evt);
            }
        });
        panelMinimos.add(jtbSortPMin);
        jtbSortPMin.setBounds(270, 10, 40, 40);

        jcbOrdenacionPMin.setBackground(Color.white);
        jcbOrdenacionPMin.setFont(fuenteSans12P);
        jcbOrdenacionPMin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Clave de producto", "Nombre" }));
        jcbOrdenacionPMin.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jcbOrdenacionPMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jcbOrdenacionPMin.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbOrdenacionPMinItemStateChanged(evt);
            }
        });
        panelMinimos.add(jcbOrdenacionPMin);
        jcbOrdenacionPMin.setBounds(120, 10, 140, 40);

        txtOrdenarPMin.setFont(fuenteSans12B);
        txtOrdenarPMin.setForeground(new java.awt.Color(25, 30, 111));
        txtOrdenarPMin.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtOrdenarPMin.setText("Ordenar por:");
        panelMinimos.add(txtOrdenarPMin);
        txtOrdenarPMin.setBounds(10, 10, 100, 40);

        jspMinimosPMin.setFont(fuenteSans12P);

        jtMinimosPMin.setFont(fuenteSans12P);
        jtMinimosPMin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Clave de producto", "Nombre", "Disponibles", "Minimo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtMinimosPMin.getTableHeader().setFont(fuenteSans12B);
        jtMinimosPMin.getTableHeader().setOpaque(false);
        jtMinimosPMin.getTableHeader().setBackground(azulF);
        jtMinimosPMin.getTableHeader().setForeground(Color.white);
        jtMinimosPMin.setRowHeight(30);
        jtMinimosPMin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtMinimosPMinMouseClicked(evt);
            }
        });
        jspMinimosPMin.setViewportView(jtMinimosPMin);

        jspMinimosPMin.setOpaque(false);
        jspMinimosPMin.getViewport().setOpaque(false);

        panelMinimos.add(jspMinimosPMin);
        jspMinimosPMin.setBounds(10, 60, 800, 670);

        panelPrincipal.add(panelMinimos);
        panelMinimos.setBounds(0, 0, 824, 728);
        panelMinimos.setVisible(false);

        panel.add(panelPrincipal);
        panelPrincipal.setBounds(200, 40, 824, 728);

        getContentPane().add(panel);
        panel.setBounds(0, 0, 1024, 768);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void JTFUsuarioLoginKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JTFUsuarioLoginKeyReleased
        String cuenta = JTFUsuarioLogin.getText();
        String password = new String(PasswordLogin.getPassword());
        if(cuenta.length()>0 && password.length()>0){
            iniciarSesion.setEnabled(true);
            iniciarSesion.setBackground(azulF);
        }else{
            iniciarSesion.setEnabled(false);
            iniciarSesion.setBackground(gris);
        }
    }//GEN-LAST:event_JTFUsuarioLoginKeyReleased

    private void PasswordLoginKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PasswordLoginKeyReleased
        String cuenta = JTFUsuarioLogin.getText();
        String password = new String(PasswordLogin.getPassword());

        if(cuenta.length()>0 && password.length()>0){
            iniciarSesion.setEnabled(true);
            iniciarSesion.setBackground(azulF);
        }else{
            iniciarSesion.setEnabled(false);
            iniciarSesion.setBackground(gris);
        }
    }//GEN-LAST:event_PasswordLoginKeyReleased

    private void PasswordLoginKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PasswordLoginKeyTyped
        if(PasswordToggleLogin.isSelected()){
            PasswordToggleLogin.doClick();
        }
    }//GEN-LAST:event_PasswordLoginKeyTyped

    private void PasswordToggleLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PasswordToggleLoginActionPerformed
        if(PasswordToggleLogin.isSelected()){
            PasswordLogin.setEchoChar((char)0);
        }else{
            PasswordLogin.setEchoChar('*');
        }
    }//GEN-LAST:event_PasswordToggleLoginActionPerformed

    private void opcionesAdmin(boolean opc){
        jtbEditarPMD.setVisible(opc);
    }
     
    private void login(){
        usuario = JTFUsuarioLogin.getText();
        String clave = new String(PasswordLogin.getPassword());
        thread = new HiloNotificaciones();
        try {
            errorContraseñaLogin.setVisible(false);
            int re = bd.Login(url, usuario, clave);
            String nombre = bd.getNombreCuenta();
            txtBienvenidaPN.setText("Bienvenido, "+nombre);
            if(re!=0){
                if(re==1){
                    login.setVisible(false);
                    panel.setVisible(true);
                    panelNombre.setVisible(true);
                    gestaltAdm.setVisible(true);
                    opcionesAdmin(admin=true);
                    thread.setCon(bd.getCon());
                    thread.setJmiNotificaciones(jmiNotificaciones);
                    thread.setJbNotificacionesPN(jbNotificacionesPN);
                    thread.start();
                }else{;
                    login.setVisible(false);
                    panel.setVisible(true);
                    panelNombre.setVisible(true);
                    gestaltOp.setVisible(true);
                    opcionesAdmin(admin=false);
                    thread.setCon(bd.getCon());
                    thread.setJmiNotificaciones(jmiNotificaciones);
                    thread.setJbNotificacionesPN(jbNotificacionesPN);
                    thread.start();
                }
            }
        }catch (SQLException ex) {
            if(ex.getErrorCode()==1045){
                errorContraseñaLogin.setVisible(true);
            }
            else{
                if(ex.getCause()!=null){
                    if(ex.getCause().toString().compareToIgnoreCase("java.net.ConnectException: Connection refused: connect")==0){
                        JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog (null, ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog (null, "Error obteniendo la informacion del usuario", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void iniciarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniciarSesionActionPerformed
        login();
    }//GEN-LAST:event_iniciarSesionActionPerformed

    private void PasswordLoginKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PasswordLoginKeyPressed
        if(evt.getKeyCode()==8){
            errorContraseñaLogin.setVisible(false);
        }
        if(evt.getKeyCode()==10){
            login();
        }
    }//GEN-LAST:event_PasswordLoginKeyPressed

    private void minimizarVentanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizarVentanaActionPerformed
        this.setState(ICONIFIED);
    }//GEN-LAST:event_minimizarVentanaActionPerformed

    private void cerrarVentanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarVentanaActionPerformed
        System.exit(0);
    }//GEN-LAST:event_cerrarVentanaActionPerformed

    private void setNormalInventario(){
        jcbOrdenacionPCP.setSelectedIndex(0);
        if(jtbSortPCP.isSelected()){
            jtbSortPCP.doClick();
        }
        jtfBuscarPCP.setText("");
        if(jtbEditarPMD.isSelected()){
            jtbEditarPMD.doClick();
        }
        jspResultadoPMD.getVerticalScrollBar().setValue(0);
        panelMostrarProducto.setVisible(false);
        estadoBusqueda=1;
    }
    
    private void setNormalProductos(){
        limpiarCampos();
        jpCargarProductoPAP.setVisible(false);
        jpAgregarProductoPAP.setVisible(false);
        jpOpcionesPAP.setVisible(true);
    }
    
    private void setNormalProveedores(){
        jtfDireccionPP.setText("");
        jtfRazonPP.setText("");
        jtfRepresentantePP.setText("");
        jtfCorreoPP.setText("");
        jtfTelefonoPP.setText("");
        jtpPP.setSelectedIndex(0);
        if(jtbSortProveedoresPP.isSelected()){
            jtbSortProveedoresPP.doClick();
        }
        jcbSortListaProveedoresPP.setSelectedIndex(0);
        jtfBuscarPP.setText("");
    }
    
    private void setNormalUsuarios(){
        jtfUsuarioPU.setText("");
        jpfPasswordPU.setText("");
        jpfConfirmarPasswordPu.setText("");
        jtfNombrePU.setText("");
        jtfApellidosPU.setText("");
        jcbTipoUsuarioPU.setSelectedIndex(0);
        jtpPU.setSelectedIndex(0);
    }
    
    private void setNormalMaquinas(){
        jtfNombrePM.setText("");
    }
    
    private void setNormalRegistro(){
        jcbOrdenacionPR.setSelectedIndex(0);
        jcbMostrarPR.setSelectedItem(0);
    }
    
    private void setNormalGestalt(){
        gestaltAdm.setVisible(false);
        gestaltOp.setVisible(false);
    }
    
    private void setNormalConfiguracion(){
        jpfPasswordPC.setText("");
        jpfConfirmarPasswordPC.setText("");
    }
    
    private void setNormalMinimos(){
        jcbOrdenacionPMin.setSelectedIndex(0);
        if(jtbSortPMin.isSelected()){
            jtbSortPMin.doClick();
        }
    }
    
    private void logout(){
        setVisiblePanelesPrincipales(0);
        setNormalInventario();
        setNormalProductos();
        setNormalProveedores();
        setNormalMaquinas();
        setNormalUsuarios();
        setNormalRegistro();
        setNormalGestalt();
        setNormalConfiguracion();
        setNormalMinimos();
        bgBotonesPrincipales.clearSelection();
        login.setVisible(true);
        PasswordLogin.setText("");
        JTFUsuarioLogin.setText("");
        try {
            thread.stopThread();
            bd.cierraConexion();
            logout = false;
        }catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void salirActionPerformed(java.awt.event.ActionEvent evt){
        logout=true;
        logout();
    }
    
    private void configuracionActionPerformed(java.awt.event.ActionEvent evt){
        bgBotonesPrincipales.clearSelection();
        txtPasswordCambiadaPC.setVisible(false);
        txtPasswordDiferentePC.setVisible(false);
        txtLongitudMenorPC.setVisible(false);
        bgBotonesPrincipales.clearSelection();
        setVisiblePanelesPrincipales(8);
    }
    
    private void NotificacionesActionPerformed(java.awt.event.ActionEvent evt){
        if(jmiNotificaciones.getText().compareToIgnoreCase("No hay notificaciones")!=0){
            bgBotonesPrincipales.clearSelection();
            setVisiblePanelesPrincipales(9);
            actualizaTablaMinimos();
        }
    }
    
    private void jbOpcionesPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbOpcionesPNActionPerformed
        if(!popupOpciones.isShowing()){
            popupOpciones.show(panelNombre, jbOpcionesPN.getX()-jbOpcionesPN.getWidth(),jbOpcionesPN.getY()+jbOpcionesPN.getHeight());
        }else{
            popupOpciones.hide();
        }
    }//GEN-LAST:event_jbOpcionesPNActionPerformed

    private void setVisiblePanelesPrincipales(int index){
        boolean[] visualizacion = new boolean[10];
        visualizacion[index]=true;
        panelLogo.setVisible(visualizacion[0]);
        panelConsultaProductos.setVisible(visualizacion[1]);
        panelMostrarProducto.setVisible(visualizacion[2]);
        if(visualizacion[1]==true || visualizacion[2]==true){
            txtProductosGOP.setForeground(Color.WHITE);
            txtProductosGA.setForeground(Color.WHITE);
        }else{
            txtProductosGOP.setForeground(azulF);
            txtProductosGA.setForeground(azulF);
        }
        panelAgregarProducto.setVisible(visualizacion[3]);
        if(visualizacion[3]==true){
            txtAgregarProductosGOP.setForeground(Color.WHITE);
            txtAgregarProductosGA.setForeground(Color.WHITE);
        }else{
            txtAgregarProductosGOP.setForeground(azulF);
            txtAgregarProductosGA.setForeground(azulF);
        }
        panelProveedor.setVisible(visualizacion[4]);
        if(visualizacion[4]==true)
            txtProveedoresGA.setForeground(Color.WHITE);
        else
            txtProveedoresGA.setForeground(azulF);
        panelUsuario.setVisible(visualizacion[5]);
        if(visualizacion[5]==true)
            txtUsuariosGA.setForeground(Color.WHITE);
        else
            txtUsuariosGA.setForeground(azulF);
        panelMaquinaria.setVisible(visualizacion[6]);
        if(visualizacion[6]==true)
            txtMaquinariaGA.setForeground(Color.WHITE);
        else
            txtMaquinariaGA.setForeground(azulF);
        panelRegistro.setVisible(visualizacion[7]);
        if(visualizacion[7]==true){
            txtRegistroGA.setForeground(Color.WHITE);
            txtRegistroGOP.setForeground(Color.WHITE);
        }else{
            txtRegistroGA.setForeground(azulF);
            txtRegistroGOP.setForeground(azulF);
        }
        panelConfiguracion.setVisible(visualizacion[8]);
        panelMinimos.setVisible(visualizacion[9]);
    }
    
    private void agregarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarProductosActionPerformed
        setVisiblePanelesPrincipales(3);
    }//GEN-LAST:event_agregarProductosActionPerformed

    private void proveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proveedorButtonActionPerformed
        setVisiblePanelesPrincipales(4);
        try {
            jcbMaquinariaPP.removeAllItems();
            jcbMaquinariaPP.addItem("SELECCIONAR");
            ArrayList<String> maquinaria = bd.listarMaquinaria();
            for(int i=0;i<maquinaria.size();i++){
                jcbMaquinariaPP.addItem(maquinaria.get(i));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_proveedorButtonActionPerformed

    private void agregarUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarUsersActionPerformed
        setVisiblePanelesPrincipales(5);
    }//GEN-LAST:event_agregarUsersActionPerformed

    private void maquinariaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maquinariaActionPerformed
        setVisiblePanelesPrincipales(6);
        try {
            Object[][] maquinas = bd.obtenerMaquinariaMatriz();
            t.ver_tabla2(jtMaquinariasPM, maquinas);
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_maquinariaActionPerformed


    
    private void productosOpProductosAdmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productosOpProductosAdmActionPerformed
        
        if(estadoBusqueda==1){
            setVisiblePanelesPrincipales(1);
            estadoBusqueda=1;
        }else{
            setVisiblePanelesPrincipales(2);
        }
        try {
            String[][] datos = bd.listarProductosInicial();
            Tabla.tablaProductos(jtTablaPCP, datos);
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_productosOpProductosAdmActionPerformed

    private void registroBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registroBotonActionPerformed
        setVisiblePanelesPrincipales(7);
        try {
            //actualizar tabla
            DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String f1 = DF.format(fechaRegIn);
            f1 = f1+" 00:00:00";
            String f2 = DF.format(fechaRegFin);
            f2 = f2+" 23:59:59";
            if(jcbMostrarPR.getSelectedIndex()==0){
                String[][] res = bd.listarRegistroIn("idProducto", f1, f2);
                Tabla.estableceTablaRegistro(res,jtRegistrosPR);
            }else if(jcbMostrarPR.getSelectedIndex()==1){
                String[][] res = bd.listarRegistroEntradas("idProducto", f1, f2);
                Tabla.estableceTablaRegistroEntradas(res,jtRegistrosPR);
            }else{
                String[][] res = bd.listarRegistroSalidas("idProducto", f1, f2);
                Tabla.estableceTablaRegistro(res,jtRegistrosPR);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_registroBotonActionPerformed
    
    private void listaProductosAction(){
        String opc = "idProductos";
        String sort = "DESC";
        switch(jcbOrdenacionPCP.getSelectedIndex()){
            case 0:
                opc = "idProductos";
                break;
            case 1:
                opc = "nombre";
                break;
            case 2:
                opc = "maquinaria";
                break;
            case 3:
                opc = "uso";
                break;
            case 4:
                opc = "precio";
                break;
        }
        if(jtbSortPCP.isSelected()){
            sort = "ASC";
        }else{
            sort = "DESC";
        }
        try {
            String matchAux = jtfBuscarPCP.getText().toUpperCase();
            if(matchAux.compareToIgnoreCase("Buscar producto: Ej. BE-8458445")==0){
                matchAux = "";
            }
            String[][]res = bd.listarProductos(matchAux, opc,sort);
            Tabla.tablaProductos(jtTablaPCP, res);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void jcbOrdenacionPCPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbOrdenacionPCPItemStateChanged
        if(!logout){
            listaProductosAction();
        }
    }//GEN-LAST:event_jcbOrdenacionPCPItemStateChanged

    private void setInfo(String buscar) throws SQLException, IOException{
        bd.obtenerProducto(buscar);
        jtfClaveProductoPMD.setText(bd.getClaveProducto());
        jtfNombrePMP.setText(bd.getNombreProducto());
        jcbMaquinariaPMD.removeAllItems();
        ArrayList<String> maquinaria = bd.listarMaquinaria();
        jcbMaquinariaPMD.addItem("Seleccionar");
        for(int i=0;i<maquinaria.size();i++){
            jcbMaquinariaPMD.addItem(maquinaria.get(i));
        }
        jcbMaquinariaPMD.setSelectedItem(bd.getMaquinariaProducto());
        jtfEmpleoPMD.setText(bd.getUsoProducto());
        jtaDescripcionPMD.setText(bd.getDescripcionProducto());
        jtfPrecioPMD.setText(bd.getPrecio()+"");
        jtfMinimosPMD.setText(bd.getMinimoProducto()+"");
        txtDisponiblesPMD.setText(bd.getCantidadDisponible()+" "+bd.getuMedidaProducto());
        jcbUniMedidaPMD.setSelectedItem(bd.getuMedidaProducto());
        int x = jspResultadoPMD.getWidth()/2-50;
        int y = 220*x/310;
        if(bd.getImagenProducto()!=null){
            BufferedImage imagenProducto = bd.getImagenProducto();
            Image dimg = imagenProducto.getScaledInstance(x, y,Image.SCALE_SMOOTH);
            jlImagenPMD.setIcon(new ImageIcon(dimg));
        }else{
            BufferedImage img = ImageIO.read(getClass().getResource("/Images/noImagen.png"));
            Image dimg = img.getScaledInstance(x, y,Image.SCALE_SMOOTH);
            jlImagenPMD.setIcon(new ImageIcon(dimg));
        }
        listaProveedoresB.removeAllElements();
        ArrayList<String> proveedores = bd.getProveedores();
        if(proveedores!=null){
            for(int i=0;i<proveedores.size();i++){
                listaProveedoresB.addElement(proveedores.get(i));
            }
        }
        jcbProveedoresPMD.removeAllItems();
        proveedores = bd.listarProveedores();
        if(proveedores!=null){
            for(int i=0;i<proveedores.size();i++){
                jcbProveedoresPMD.addItem(proveedores.get(i));
            }
        }
        listaUbicacionesB.clear();
        ArrayList<String> ubicaciones = bd.getUbicaciones();
        for(int i=0;i<ubicaciones.size();i++){
            listaUbicacionesB.addElement(ubicaciones.get(i));
        }
        //Establecer tabla
        DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
        String f1 = DF.format(fechaBIn);
        f1 = f1+" 00:00:00";
        String f2 = DF.format(fechaBFin);
        f2 = f2+" 23:59:59";
        eSProducto=bd.obtenerInfoRegistros(bd.getClaveProducto(),f1,f2);
        Tabla.tablaRegistroProducto(eSProducto,jtEntradasSalidasPMD);
    }
    
    private void jtTablaPCPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtTablaPCPMouseClicked
        try {
            String buscar = (String) jtTablaPCP.getModel().getValueAt(jtTablaPCP.getSelectedRow(), 0);
            setInfo(buscar);
            for(int i=0;i<listaProveedoresB.size();i++){
                jcbProveedoresPMD.removeItem(listaProveedoresB.get(i));
            }
            if(jcbProveedoresPMD.getItemCount()>0){
                jbAgregarProveedorPMD.setEnabled(true);
                jbAgregarProveedorPMD.setBackground(azulF);
            }else{
                jbAgregarProveedorPMD.setEnabled(false);
                jbAgregarProveedorPMD.setBackground(gris);
            }
            setVisiblePanelesPrincipales(2);
            estadoBusqueda=2;
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jtTablaPCPMouseClicked

    private void jtfBuscarPCPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfBuscarPCPKeyReleased
        if(!logout){
            listaProductosAction();
        }
    }//GEN-LAST:event_jtfBuscarPCPKeyReleased

    private void jtbSortPCPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jtbSortPCPItemStateChanged
        if(!logout){
            listaProductosAction();
        }
    }//GEN-LAST:event_jtbSortPCPItemStateChanged

    private void jbRegresarPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRegresarPMDActionPerformed
        if(jtbEditarPMD.isSelected()){
            jtbEditarPMD.doClick();
        }
        jspResultadoPMD.getVerticalScrollBar().setValue(0);
        setVisiblePanelesPrincipales(1);
        estadoBusqueda=1;
    }//GEN-LAST:event_jbRegresarPMDActionPerformed

    private void jtfNombrePMPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfNombrePMPMouseClicked
        jtfNombrePMP.setCaretPosition(jtfNombrePMP.getText().length());
    }//GEN-LAST:event_jtfNombrePMPMouseClicked

    private void modificaGuardarCambios(boolean opc){
        jbGuardarCambiosPMD.setEnabled(opc);
        if(opc == true){
            jbGuardarCambiosPMD.setBackground(azulF);
        }else{
            jbGuardarCambiosPMD.setBackground(gris);
        }
    }
    
    private void verificarCambios(){
        int c=0;
        for(int i=0;i<cambios.length;i++){
            c+=cambios[i];
        }
        if(c>0){
            modificaGuardarCambios(true);
        }else{
            modificaGuardarCambios(false);
        }
    }
    
    private void jtfNombrePMPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfNombrePMPKeyReleased
        herramientas.compararNombre(jtfNombrePMP.getText(), bd.getNombreProducto());
        cambios = herramientas.getCambios();
        verificarCambios();
    }//GEN-LAST:event_jtfNombrePMPKeyReleased

    private void jtfEmpleoPMDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfEmpleoPMDMouseClicked
        jtfEmpleoPMD.setCaretPosition(jtfEmpleoPMD.getText().length());
    }//GEN-LAST:event_jtfEmpleoPMDMouseClicked

    private void jtfEmpleoPMDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfEmpleoPMDKeyReleased
        herramientas.compararUso(jtfEmpleoPMD.getText(), bd.getUsoProducto());
        cambios = herramientas.getCambios();
        verificarCambios();
    }//GEN-LAST:event_jtfEmpleoPMDKeyReleased

    private void jtaDescripcionPMDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtaDescripcionPMDMouseClicked
        jtaDescripcionPMD.setCaretPosition(jtaDescripcionPMD.getText().length());
    }//GEN-LAST:event_jtaDescripcionPMDMouseClicked

    private void jtaDescripcionPMDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtaDescripcionPMDKeyReleased
        herramientas.compararDescripcion(jtaDescripcionPMD.getText(), bd.getDescripcionProducto());
        cambios = herramientas.getCambios();
        verificarCambios();
    }//GEN-LAST:event_jtaDescripcionPMDKeyReleased

    private void jtfPrecioPMDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfPrecioPMDMouseClicked
        jtfPrecioPMD.setCaretPosition(jtfPrecioPMD.getText().length());
    }//GEN-LAST:event_jtfPrecioPMDMouseClicked

    private void jtfPrecioPMDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfPrecioPMDKeyReleased
        if(jtfPrecioPMD.getText().length()>0){
            herramientas.compararPrecio(Float.parseFloat(jtfPrecioPMD.getText()), bd.getPrecio());
            cambios = herramientas.getCambios();
            verificarCambios();
        }
    }//GEN-LAST:event_jtfPrecioPMDKeyReleased

    private void jtfPrecioPMDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfPrecioPMDKeyTyped
        herramientas.soloNumeros(evt);
    }//GEN-LAST:event_jtfPrecioPMDKeyTyped

    private void jtfMinimosPMDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfMinimosPMDMouseClicked
        jtfMinimosPMD.setCaretPosition(jtfMinimosPMD.getText().length());
    }//GEN-LAST:event_jtfMinimosPMDMouseClicked

    private void jtfMinimosPMDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfMinimosPMDKeyReleased
        if(jtfMinimosPMD.getText().length()>0){
            herramientas.compararMinimo(Integer.parseInt(jtfMinimosPMD.getText()), bd.getMinimoProducto());
            cambios = herramientas.getCambios();
            verificarCambios();
        }
    }//GEN-LAST:event_jtfMinimosPMDKeyReleased

    private void jtfMinimosPMDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfMinimosPMDKeyTyped
        herramientas.soloNumeros(evt);
    }//GEN-LAST:event_jtfMinimosPMDKeyTyped

    private void jtfClaveProductoPMDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfClaveProductoPMDMouseClicked
        jtfClaveProductoPMD.setCaretPosition(jtfClaveProductoPMD.getText().length());
    }//GEN-LAST:event_jtfClaveProductoPMDMouseClicked

    private void jtfClaveProductoPMDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfClaveProductoPMDKeyReleased
        herramientas.compararClaveProducto(jtfClaveProductoPMD.getText(), bd.getClaveProducto());
        cambios = herramientas.getCambios();
        verificarCambios();
    }//GEN-LAST:event_jtfClaveProductoPMDKeyReleased

    private void jlImagenPMDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlImagenPMDMouseClicked
        if(jtbEditarPMD.isSelected()){
            imagenProducto = herramientas.LeerImagen();
            if(imagenProducto!=null){
                Image imagenR = herramientas.getImagenR();
                int x = jspResultadoPMD.getWidth()/2-50;
                int y = 220*x/310;
                Image dimg = imagenR.getScaledInstance(x, y,Image.SCALE_SMOOTH);
                jlImagenPMD.setIcon(new ImageIcon(dimg));
                modImagen=true;
                herramientas.compararModImagen(modImagen);
                cambios = herramientas.getCambios();
                verificarCambios();
            }
        }
    }//GEN-LAST:event_jlImagenPMDMouseClicked

    private void jbImprimirPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbImprimirPMDActionPerformed
        try {
            PDF pd = new PDF(bd.getQrProducto(), bd.getNombreProducto(), bd.getUsoProducto());
        } catch (IOException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbImprimirPMDActionPerformed

    private void jtfUbicacionesPMDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfUbicacionesPMDKeyPressed
        if(jtfUbicacionesPMD.getText().length()>0){
            jbAgregarUbicacionPMD.setEnabled(true);
            jbAgregarUbicacionPMD.setBackground(azulF);
        }
        if(evt.getKeyCode()==10){
            try {
                String ubicacion = jtfUbicacionesPMD.getText();
                listaUbicacionesB.addElement(ubicacion);
                jtfUbicacionesPMD.setText("");
                bd.insertarUbicacion(ubicacion, bd.getClaveProducto());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jtfUbicacionesPMDKeyPressed

    private void jbAgregarUbicacionPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarUbicacionPMDActionPerformed
        try {
            String ubicacion = jtfUbicacionesPMD.getText();
            listaUbicacionesB.addElement(ubicacion);
            jtfUbicacionesPMD.setText("");
            bd.insertarUbicacion(ubicacion, bd.getClaveProducto());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jbAgregarUbicacionPMDActionPerformed

    private void jlUbicacionesPMDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlUbicacionesPMDMouseClicked
        if(evt.getClickCount()==2){
            String delUbicacion = jlUbicacionesPMD.getSelectedValue();
            if(delUbicacion.length()>0){
                try {
                    listaUbicacionesB.removeElement(jlUbicacionesPMD.getSelectedValue());
                    jlUbicacionesPMD.clearSelection();
                    bd.eliminarUbicacion(delUbicacion, bd.getClaveProducto());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jlUbicacionesPMDMouseClicked

    private void jbAgregarProveedorPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarProveedorPMDActionPerformed
        try {
            String aux = (String) jcbProveedoresPMD.getSelectedItem();
            listaProveedoresB.addElement(aux);
            jcbProveedoresPMD.removeItemAt(jcbProveedoresPMD.getSelectedIndex());
            bd.insertarProductoProveedor(aux);
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbAgregarProveedorPMDActionPerformed

    private void jlProveedoresPMDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlProveedoresPMDMouseClicked
        if(evt.getClickCount()>1){
            if(admin){
                String aux = jlProveedoresPMD.getSelectedValue();
                if(aux.length()>0){
                    try {
                        listaProveedoresB.removeElement(aux);
                        jcbProveedoresPMD.addItem(aux);
                        jlProveedoresPMD.clearSelection();
                        bd.eliminarProductoProveedor(aux, bd.getClaveProducto());
                    } catch (SQLException ex) {
                        Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_jlProveedoresPMDMouseClicked

    private void jbGuardarCambiosPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbGuardarCambiosPMDActionPerformed
        try {
            bd.guardarCambiosProducto(cambios, bd.getClaveProducto(), bd.getIdInventario(), (String)jcbMaquinariaPMD.getSelectedItem(), jtfClaveProductoPMD.getText(), jtfNombrePMP.getText(), jtfEmpleoPMD.getText(), jtaDescripcionPMD.getText(), (String)jcbUniMedidaPMD.getSelectedItem(),imagenProducto);
            imagenSel=false;
            bd.guardarCambiosInventario(cambios, jtfPrecioPMD.getText(), jtfMinimosPMD.getText());
            jbGuardarCambiosPMD.setEnabled(false);
            jbGuardarCambiosPMD.setBackground(gris);
            jtbEditarPMD.doClick();
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriterException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbGuardarCambiosPMDActionPerformed

    private void jbSalidasPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSalidasPMDActionPerformed
        try {
            salidasProducto sD = new salidasProducto(new javax.swing.JFrame(), true, bd.getClaveProducto(), bd.getCon(),usuario,bd.getPrecio(),bd.getCantidadDisponible());
            sD.setVisible(true);
            if(sD.isSalida()){
                ventanaFeed vf = new ventanaFeed(null, true, "Registro realizado con exito");
                vf.setVisible(true);
            }
            thread.interrupt();
            setInfo(bd.getClaveProducto());
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbSalidasPMDActionPerformed

    private void jbEntradasPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbEntradasPMDActionPerformed
        try {
            entradasProducto eD = new entradasProducto(new javax.swing.JFrame(), true, bd.getClaveProducto(), bd.getCon(),usuario);
            eD.setVisible(true);
            if(eD.isSalida()){
                ventanaFeed vf = new ventanaFeed(null, true, "Entrada realizado con exito");
                vf.setVisible(true);
            }
            thread.interrupt();
            setInfo(bd.getClaveProducto());
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbEntradasPMDActionPerformed

    private void jcbEleccionMostrarPMDItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbEleccionMostrarPMDItemStateChanged
        //Establecer tabla
        DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
        String f1 = DF.format(fechaBIn);
        f1 = f1+" 00:00:00";
        String f2 = DF.format(fechaBFin);
        f2 = f2+" 23:59:59";
        eSProducto=bd.obtenerInfoRegistros(bd.getClaveProducto(),f1,f2);
        if(jcbEleccionMostrarPMD.getSelectedIndex()==0){
            Tabla.tablaEntradasProducto(jtEntradasSalidasPMD);
        }
        else{
            Tabla.tablaSalidasProducto(jtEntradasSalidasPMD);
        }
    }//GEN-LAST:event_jcbEleccionMostrarPMDItemStateChanged

    private void jtbEditarPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbEditarPMDActionPerformed
        boolean opc;
        if(jtbEditarPMD.isSelected()){
            opc = true;
            jbGuardarCambiosPMD.setVisible(opc);
            if(eSProducto.length>0){
                jbBorrarPMD.setVisible(false);
                jbBorrarPMD.setEnabled(false);
            }else{
                jbBorrarPMD.setVisible(opc);
                jbBorrarPMD.setEnabled(opc);
            }
            try {
                int x = jspResultadoPMD.getWidth()/2-50;
                int y = 220*x/310;
                BufferedImage img = ImageIO.read(getClass().getResource("/Images/agregarImgGrande.png"));
                Image dimg = img.getScaledInstance(x, y,Image.SCALE_SMOOTH);
                jlImagenPMD.setIcon(new ImageIcon(dimg));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
            opc = false;
            jbGuardarCambiosPMD.setVisible(opc);
            jbBorrarPMD.setVisible(opc);
            jbBorrarPMD.setEnabled(opc);
        }
        jtfClaveProductoPMD.setOpaque(opc);
        jtfClaveProductoPMD.setEnabled(opc);
        jtfNombrePMP.setOpaque(opc);
        jtfNombrePMP.setEnabled(opc);
        jcbMaquinariaPMD.setOpaque(opc);
        jcbMaquinariaPMD.setEnabled(opc);
        jtfEmpleoPMD.setOpaque(opc);
        jtfEmpleoPMD.setEnabled(opc);
        jtaDescripcionPMD.setOpaque(opc);
        jtaDescripcionPMD.setEnabled(opc);
        jcbUniMedidaPMD.setVisible(opc);
        jtfPrecioPMD.setOpaque(opc);
        jtfPrecioPMD.setEnabled(opc);
        jtfMinimosPMD.setOpaque(opc);
        jtfMinimosPMD.setEnabled(opc);
        txtUMedidaPMD.setVisible(opc);
    }//GEN-LAST:event_jtbEditarPMDActionPerformed

    private void jbBorrarPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbBorrarPMDActionPerformed
        int re;
        re = JOptionPane.showConfirmDialog(null, "Esta acción no es reversible y borrara al producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
        if(re == 0){
            try {
                bd.eliminarProducto(bd.getClaveProducto());
                setVisiblePanelesPrincipales(1);
            } catch (SQLException ex) {
                Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jbBorrarPMDActionPerformed

    private void jcbMaquinariaPMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbMaquinariaPMDActionPerformed
        if(jcbMaquinariaPMD.isEnabled()){
            herramientas.compararMaquinaria((String)jcbMaquinariaPMD.getSelectedItem(), bd.getMaquinariaProducto());
        }
    }//GEN-LAST:event_jcbMaquinariaPMDActionPerformed

    private void jdcFechaFinalPMDPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcFechaFinalPMDPropertyChange
        if ("date".equals(evt.getPropertyName())) {
            if(evt.getNewValue().toString().compareTo(fechaBFin.toString())!=0){
                fechaBFin = jdcFechaFinalPMD.getDate();
                DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
                String f1 = DF.format(fechaBIn);
                f1 = f1+" 00:00:00";
                String f2 = DF.format(fechaBFin);
                f2 = f2+" 23:59:59";
                eSProducto=bd.obtenerInfoRegistros(bd.getClaveProducto(),f1,f2);
                Tabla.tablaRegistroProducto(eSProducto,jtEntradasSalidasPMD);
            }
        }
    }//GEN-LAST:event_jdcFechaFinalPMDPropertyChange

    private void jdcFechaInicialPMDPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcFechaInicialPMDPropertyChange
        if ("date".equals(evt.getPropertyName())) {
            if(evt.getNewValue().toString().compareTo(fechaBIn.toString())!=0){
                fechaBIn=jdcFechaInicialPMD.getDate();
                DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
                String f1 = DF.format(fechaBIn);
                f1 = f1+" 00:00:00";
                String f2 = DF.format(fechaBFin);
                f2 = f2+" 23:59:59";
                eSProducto=bd.obtenerInfoRegistros(bd.getClaveProducto(),f1,f2);
                Tabla.tablaRegistroProducto(eSProducto,jtEntradasSalidasPMD);
            }
        }
    }//GEN-LAST:event_jdcFechaInicialPMDPropertyChange

    private void jtfClaveProductoPAPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfClaveProductoPAPKeyReleased
        try {
            if(jtfClaveProductoPAP.getText().length()>0){
                if(bd.existeProducto(jtfClaveProductoPAP.getText())){
                    txtErrorIDPAP.setVisible(true);
                    rep=true;
                    jbAgregarProductoPAP.setEnabled(false);
                    jbAgregarProductoPAP.setBackground(gris);
                }
                else{
                    txtErrorIDPAP.setVisible(false);
                    rep=false;
                    if(autAgregarNombre==true && rep==false){
                        jbAgregarProductoPAP.setEnabled(true);
                        jbAgregarProductoPAP.setBackground(azulF);
                    }
                    else{
                        jbAgregarProductoPAP.setEnabled(false);
                        jbAgregarProductoPAP.setBackground(gris);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jtfClaveProductoPAPKeyReleased

    private void jtfNombrePAPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfNombrePAPKeyReleased
        if(jtfNombrePAP.getText().length()>0){
            autAgregarNombre=true;
        }
        else{
            autAgregarNombre=false;
        }
        if(autAgregarNombre==true && rep==false){
            jbAgregarProductoPAP.setEnabled(true);
            jbAgregarProductoPAP.setBackground(azulF);
        }
        else{
            jbAgregarProductoPAP.setEnabled(false);
            jbAgregarProductoPAP.setBackground(gris);
        }
    }//GEN-LAST:event_jtfNombrePAPKeyReleased

    private void jtfCantidadPAPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfCantidadPAPFocusLost
        if(jtfCantidadPAP.getText().length()==0){
            jtfCantidadPAP.setText("0");
        }
    }//GEN-LAST:event_jtfCantidadPAPFocusLost

    private void jtfCantidadPAPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfCantidadPAPMouseClicked
        jtfCantidadPAP.selectAll();
    }//GEN-LAST:event_jtfCantidadPAPMouseClicked

    private void jtfCantidadPAPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfCantidadPAPKeyReleased
        String text = jtfCantidadPAP.getText();
        if(text.length()>0){
            int aux = Integer.parseInt(text);
            jtfCantidadPAP.setText(aux+"");
            jtfCantidadPAP.setCaretPosition(jtfCantidadPAP.getText().length());
        }
    }//GEN-LAST:event_jtfCantidadPAPKeyReleased

    private void jtfCantidadPAPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfCantidadPAPKeyTyped
        herramientas.soloNumeros(evt);
    }//GEN-LAST:event_jtfCantidadPAPKeyTyped

    private void jtfMinimoPAPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfMinimoPAPFocusLost
        if(jtfMinimoPAP.getText().length()==0){
            jtfMinimoPAP.setText("0");
        }
    }//GEN-LAST:event_jtfMinimoPAPFocusLost

    private void jtfMinimoPAPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfMinimoPAPMouseClicked
        jtfMinimoPAP.selectAll();
    }//GEN-LAST:event_jtfMinimoPAPMouseClicked

    private void jtfMinimoPAPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfMinimoPAPKeyReleased
        String text = jtfMinimoPAP.getText();
        if(text.length()>0){
            int aux = Integer.parseInt(text);
            jtfMinimoPAP.setText(aux+"");
            jtfMinimoPAP.setCaretPosition(jtfMinimoPAP.getText().length());
        }
    }//GEN-LAST:event_jtfMinimoPAPKeyReleased

    private void jtfMinimoPAPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfMinimoPAPKeyTyped
        herramientas.soloNumeros(evt);
    }//GEN-LAST:event_jtfMinimoPAPKeyTyped

    private void jtfPrecioEstimadoPAPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfPrecioEstimadoPAPFocusLost
        if(jtfPrecioEstimadoPAP.getText().length()==0){
            jtfPrecioEstimadoPAP.setText("0");
        }else{
            String text = jtfPrecioEstimadoPAP.getText();
            if(text.length()>0){
                float aux = Float.parseFloat(text);
                jtfPrecioEstimadoPAP.setText(aux+"");
                jtfPrecioEstimadoPAP.setCaretPosition(jtfPrecioEstimadoPAP.getText().length());
            }
        }
    }//GEN-LAST:event_jtfPrecioEstimadoPAPFocusLost

    private void jtfPrecioEstimadoPAPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfPrecioEstimadoPAPMouseClicked
        jtfPrecioEstimadoPAP.selectAll();
    }//GEN-LAST:event_jtfPrecioEstimadoPAPMouseClicked

    private void jtfPrecioEstimadoPAPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfPrecioEstimadoPAPKeyTyped
        herramientas.soloFlotantes(evt, jtfPrecioEstimadoPAP.getText());
    }//GEN-LAST:event_jtfPrecioEstimadoPAPKeyTyped

    private void jlAgregarImagenPAPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlAgregarImagenPAPMouseClicked
        if(evt.getClickCount()>1){
            imagen = null;
            jlAgregarImagenPAP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/AgregarImgGrande.png")));
            imagenSel = false;
        }else{
            imagen = herramientas.LeerImagen();
            if(imagen!=null){
                Image imagenR = herramientas.getImagenR();
                jlAgregarImagenPAP.setIcon(new ImageIcon(imagenR));
                imagenSel = true;
            }
        }
    }//GEN-LAST:event_jlAgregarImagenPAPMouseClicked

    private void jbAgregarProveedorPAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarProveedorPAPActionPerformed
        listaProveedores.addElement((String) jcbProveedoresPAP.getSelectedItem());
        jcbProveedoresPAP.removeItem(jcbProveedoresPAP.getSelectedItem());
        if(jcbProveedoresPAP.getItemCount()==0){
            jbAgregarProveedorPAP.setEnabled(false);
            jbAgregarProveedorPAP.setBackground(gris);
        }
    }//GEN-LAST:event_jbAgregarProveedorPAPActionPerformed

    private void jlProveedoresPAPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlProveedoresPAPMouseClicked
        if(evt.getClickCount()==2){
            String proveedorEl = listaProveedores.getElementAt(jlProveedoresPAP.getSelectedIndex());
            listaProveedores.removeElementAt(jlProveedoresPAP.getSelectedIndex());
            jcbProveedoresPAP.addItem(proveedorEl);
            if(jcbProveedoresPAP.getItemCount()>0){
                jbAgregarProveedorPAP.setEnabled(true);
                jbAgregarProveedorPAP.setBackground(azulF);
            }else{
                jbAgregarProveedorPAP.setEnabled(false);
                jbAgregarProveedorPAP.setBackground(gris);
            }
        }
    }//GEN-LAST:event_jlProveedoresPAPMouseClicked

    private void jtfUbicacionesPAPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfUbicacionesPAPFocusLost
        listaUbicaciones.addElement(jtfUbicacionesPAP.getText());
        jtfUbicacionesPAP.setText("");
    }//GEN-LAST:event_jtfUbicacionesPAPFocusLost

    private void jtfUbicacionesPAPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfUbicacionesPAPKeyPressed
        if(jtfUbicacionesPAP.getText().length()>0){
            jbAgregarUbicacionesPAP.setEnabled(true);
            jbAgregarUbicacionesPAP.setBackground(azulF);
        }else{
            jbAgregarUbicacionesPAP.setEnabled(false);
            jbAgregarUbicacionesPAP.setBackground(gris);
        }
        if(evt.getKeyCode()==10){
            listaUbicaciones.addElement(jtfUbicacionesPAP.getText());
            jtfUbicacionesPAP.setText("");
        }
    }//GEN-LAST:event_jtfUbicacionesPAPKeyPressed

    private void jbAgregarUbicacionesPAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarUbicacionesPAPActionPerformed
        listaUbicaciones.addElement(jtfUbicacionesPAP.getText());
        jtfUbicacionesPAP.setText("");
    }//GEN-LAST:event_jbAgregarUbicacionesPAPActionPerformed

    private void jlUbicacionesPAPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlUbicacionesPAPMouseClicked
        if(evt.getClickCount()==2){
            listaUbicaciones.removeElement(jlUbicacionesPAP.getSelectedValue());
            jlUbicacionesPAP.clearSelection();
        }
    }//GEN-LAST:event_jlUbicacionesPAPMouseClicked

    private void limpiarCampos(){
        jtfClaveProductoPAP.setText("");
        jtfNombrePAP.setText("");
        jcbMaquinariaPAP.removeAllItems();
        jtfEmpleoPAP.setText("");
        jtaDescripcionPAP.setText("");
        jtfCantidadPAP.setText("0");
        jtfMinimoPAP.setText("0");
        jtfPrecioEstimadoPAP.setText("0");
        listaUbicaciones.clear();
        listaProveedores.clear();
        autAgregarNombre=false;
        rep = true;
        jbAgregarProductoPAP.setEnabled(false);
        jbAgregarProductoPAP.setBackground(gris);
    }
    
    private void jbAgregarProductoPAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarProductoPAPActionPerformed
        String claveProducto = "414c4d4143454e:"+jtfClaveProductoPAP.getText();
        String nombreProducto = jtfNombrePAP.getText();
        String maquinaria = jcbMaquinariaPAP.getSelectedItem()+"";
        int idMaquinaria = 0;
        String uso = jtfEmpleoPAP.getText();
        String descripcion = jtaDescripcionPAP.getText();
        int cantidadDisponible = Integer.parseInt(jtfCantidadPAP.getText());
        String unidadMedida = jcbUMedidaPAP.getSelectedItem().toString();
        int minimo = Integer.parseInt(jtfMinimoPAP.getText());
        float precio = Float.parseFloat(jtfPrecioEstimadoPAP.getText());
        BufferedImage qr = null;
        BufferedImage img = imagen;
        int idInventario;
        int idZona;
        boolean creacionProducto = false;
        if(maquinaria.compareToIgnoreCase("Seleccionar")==0){
            maquinaria = "";
            idMaquinaria=0;
        }else{
            idMaquinaria=bd.consultaIdMaquinaria(maquinaria);
        }
        if(claveProducto.length()==0){    //Si no hay clave, se procedera a generar una
            generarClave dialogGC = new generarClave(new javax.swing.JFrame(), true, nombreProducto, bd.getCon());
            dialogGC.setVisible(true);
            claveProducto=dialogGC.getClaveGenerada();
        }
        if(!claveProducto.equalsIgnoreCase("-1")){
            try {
                bd.autocommit();
                idInventario = bd.insertarInventario(cantidadDisponible, precio, minimo);
                idZona = bd.buscarZona(zonaDefault);//->Verificar
                qr = herramientas.crearQR(claveProducto.toUpperCase(), 400, 400);
                InputStream isqr = herramientas.BufferedImage2InputStream(qr);
                if(imagenSel==true){
                    InputStream isIM = herramientas.BufferedImage2InputStream(imagen);
                    bd.agregarProducto(claveProducto, nombreProducto, idMaquinaria, uso, descripcion, unidadMedida, isqr, isIM, idInventario, idZona, imagenSel);
                }else{
                    bd.agregarProducto(claveProducto, nombreProducto, idMaquinaria, uso, descripcion, unidadMedida, isqr, null, idInventario, idZona, imagenSel);
                }
                bd.commit();
                bd.autocommit();
                creacionProducto=true;
                bd.insertarProductosProveedores(listaProveedores, claveProducto);
                bd.insertarUbicaciones(listaUbicaciones, claveProducto);
                correctAddProduct dialog = new correctAddProduct(new javax.swing.JFrame(), true, qr,nombreProducto,uso);
                dialog.setVisible(true);
                limpiarCampos();
            } catch (WriterException ex) {
                Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                if(creacionProducto!=false){
                    JOptionPane.showMessageDialog (null, "Se a registrado el producto correctamente, pero ocurrio un problema durante el proceso", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog (null, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jbAgregarProductoPAPActionPerformed

    private void jbCargarCSVPAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCargarCSVPAPActionPerformed
        String erroresLineas = "Se han encontrado problemas\n";
        boolean creacionProducto = false;
        String col = "";
        int c=0;
        try {
            ArrayList<String[]> valores = herramientas.cargaCSV();
            if(valores!=null){
                valores = herramientas.verificaCSV(valores, bd);
                for(int i=1;i<valores.size();i++){
                    c=i;
                    boolean b1 = herramientas.verificaEntero(valores.get(i)[6]);
                    boolean b2 = herramientas.verificaFlotante(valores.get(i)[7]);
                    boolean b3 = herramientas.verificaEntero(valores.get(i)[8]);
                    boolean b5 = herramientas.verificaUMedida(valores.get(i)[5]);
                    boolean b6;
                    if(valores.get(i)[2].compareToIgnoreCase("")!=0){
                        b6 = bd.verificaMaquinaria(valores.get(i)[2]);
                    }else{
                        b6 = true;
                    }
                    if(b1 && b2 && b3 && b5 && b6){
                        String claveProducto = "414c4d4143454e:"+valores.get(i)[0];
                        String nombre = valores.get(i)[1];
                        int maquinaria = 0;
                        if(valores.get(i)[2].compareToIgnoreCase("")!=0){
                            maquinaria = bd.consultaIdMaquinaria(valores.get(i)[2]);
                        }
                        String uso = valores.get(i)[3];
                        String descripcion = valores.get(i)[4];
                        String uMedida = valores.get(i)[5];
                        int cantidadDisponible = Integer.parseInt(valores.get(i)[6]);
                        float precio = Float.parseFloat(valores.get(i)[7]);
                        int minimo = Integer.parseInt(valores.get(i)[8]);
                        if(!bd.existeProducto(claveProducto)){
                            BufferedImage BuffQR = herramientas.crearQR(claveProducto, 400, 400);
                            InputStream qr = herramientas.BufferedImage2InputStream(BuffQR);
                            bd.autocommit();
                            int idInventario = bd.insertarInventario(cantidadDisponible, precio, minimo);
                            int idZona = bd.buscarZona(zonaDefault);
                            bd.agregarProducto(claveProducto, nombre, maquinaria, uso, descripcion, uMedida, qr, null, idInventario, idZona, false);
                            bd.commit();
                            bd.autocommit();
                        }else{
                            col = "1\nError: Producto existente";
                            erroresLineas+="Fila: "+(i+1)+"\nColumnas: "+col+"\n";
                            erroresCSVPanel.setText(erroresLineas);
                        }
                    }else{
                        if(!b6){
                            col ="3, ";
                        }
                        if(!b5){
                            col = "6, ";
                        }
                        if(!b1){
                            col = "7, ";
                        }
                        if(!b2){
                            col = "8, ";
                        }
                        if(!b3){
                            col = "9, ";
                        }
                        if(col.endsWith(", ")){
                            col = col.substring(0, col.length()-2);
                        }
                        erroresLineas+="Fila: "+(i+1)+"\nColumnas: "+col+"\n";
                        erroresCSVPanel.setText(erroresLineas);
                    }
                }
                if(erroresCSVPanel.getText().length()>0){
                    jspErroresCargaPAP.setVisible(true);
                }else{
                    txtExitoCargaPAP.setVisible(true);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            //Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriterException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbCargarCSVPAPActionPerformed

    private void jbAgregarUnProductoPAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarUnProductoPAPActionPerformed
        try {
            jpAgregarProductoPAP.setVisible(true);
            jcbMaquinariaPAP.removeAllItems();
            jcbMaquinariaPAP.addItem("SELECCIONAR");
            ArrayList<String> maquinaria = bd.listarMaquinaria();
            for(int i=0;i<maquinaria.size();i++){
                jcbMaquinariaPAP.addItem(maquinaria.get(i));
            }
            jcbProveedoresPAP.removeAllItems();
            ArrayList<String> proveedores = bd.listarProveedores();
            if(proveedores.size()>0){
                for(int i=0;i<proveedores.size();i++){
                    jcbProveedoresPAP.addItem(proveedores.get(i));
                }
                jcbProveedoresPAP.setEnabled(true);
                jbAgregarProveedorPAP.setEnabled(true);
                jbAgregarProveedorPAP.setBackground(azulF);
            }else{
                jcbProveedoresPAP.setEnabled(false);
                jbAgregarProveedorPAP.setEnabled(false);
                jbAgregarProveedorPAP.setBackground(gris);
            }
            jpOpcionesPAP.setVisible(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jbAgregarUnProductoPAPActionPerformed

    private void jbRegresarOneProductoPAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRegresarOneProductoPAPActionPerformed
        jpOpcionesPAP.setVisible(true);
        jpAgregarProductoPAP.setVisible(false);
    }//GEN-LAST:event_jbRegresarOneProductoPAPActionPerformed

    private void jbAgregarVariosProductoPAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarVariosProductoPAPActionPerformed
        jpOpcionesPAP.setVisible(false);
        jspErroresCargaPAP.setVisible(false);
        txtExitoCargaPAP.setVisible(false);
        jpCargarProductoPAP.setVisible(true);
    }//GEN-LAST:event_jbAgregarVariosProductoPAPActionPerformed

    private void jbRegresarVariosProductoPAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRegresarVariosProductoPAPActionPerformed
        jpOpcionesPAP.setVisible(true);
        jpCargarProductoPAP.setVisible(false);
    }//GEN-LAST:event_jbRegresarVariosProductoPAPActionPerformed

    private void jtfDireccionPPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfDireccionPPKeyReleased
        String aux = jtfDireccionPP.getText();
        if(aux.length()!=0){
            direccionVacio=false;
        }
        else{
            direccionVacio=true;
        }
        if(!razonVacio && !representanteVacio && email && !telefonoVacio && !direccionVacio){
            jbAgregarProveedorPP.setEnabled(true);
            jbAgregarProveedorPP.setBackground(azulF);
        }else{
            jbAgregarProveedorPP.setEnabled(false);
            jbAgregarProveedorPP.setBackground(gris);
        }
    }//GEN-LAST:event_jtfDireccionPPKeyReleased

    private void jtfRazonPPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfRazonPPKeyReleased
        String aux = jtfRazonPP.getText();
        if(aux.length()!=0){
            razonVacio=false;
        }
        else{
            razonVacio=true;
        }
        if(!razonVacio && !representanteVacio && email && !telefonoVacio && !direccionVacio){
            jbAgregarProveedorPP.setEnabled(true);
            jbAgregarProveedorPP.setBackground(azulF);
        }else{
            jbAgregarProveedorPP.setEnabled(false);
            jbAgregarProveedorPP.setBackground(gris);
        }
    }//GEN-LAST:event_jtfRazonPPKeyReleased

    private void jtfRepresentantePPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfRepresentantePPKeyReleased
        String aux = jtfRepresentantePP.getText();
        if(aux.length()!=0){
            representanteVacio=false;
        }
        else{
            representanteVacio=true;
        }
        if(!razonVacio && !representanteVacio && email && !telefonoVacio && !direccionVacio){
            jbAgregarProveedorPP.setEnabled(true);
            jbAgregarProveedorPP.setBackground(azulF);
        }else{
            jbAgregarProveedorPP.setEnabled(false);
            jbAgregarProveedorPP.setBackground(gris);
        }
    }//GEN-LAST:event_jtfRepresentantePPKeyReleased

    private void jtfCorreoPPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfCorreoPPKeyReleased
        String correo = jtfCorreoPP.getText();
        if(correo.length()!=0){
            Matcher mather = correoPattern.matcher(correo);
            if(mather.find()==true){
                txtCorreoNoValidoPP.setVisible(false);
                email=true;
            }else{
                txtCorreoNoValidoPP.setVisible(true);
                email=false;
            }
        }
        if(!razonVacio && !representanteVacio && email && !telefonoVacio && !direccionVacio){
            jbAgregarProveedorPP.setEnabled(true);
            jbAgregarProveedorPP.setBackground(azulF);
        }else{
            jbAgregarProveedorPP.setEnabled(false);
            jbAgregarProveedorPP.setBackground(gris);
        }
    }//GEN-LAST:event_jtfCorreoPPKeyReleased

    private void jtfTelefonoPPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfTelefonoPPKeyReleased
        String aux = jtfTelefonoPP.getText();
        if(aux.length()!=0){
            telefonoVacio=false;
        }
        else{
            telefonoVacio=true;
        }
        if(!razonVacio && !representanteVacio && email && !telefonoVacio && !direccionVacio){
            jbAgregarProveedorPP.setEnabled(true);
            jbAgregarProveedorPP.setBackground(azulF);
        }else{
            jbAgregarProveedorPP.setEnabled(false);
            jbAgregarProveedorPP.setBackground(gris);
        }
    }//GEN-LAST:event_jtfTelefonoPPKeyReleased

    private void jtfTelefonoPPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfTelefonoPPKeyTyped
        herramientas.soloNumeros(evt);
    }//GEN-LAST:event_jtfTelefonoPPKeyTyped

    private void jbAgregarProveedorPPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarProveedorPPActionPerformed
        String razon = jtfRazonPP.getText();
        String maquinaria = (String) jcbMaquinariaPP.getSelectedItem();
        String representante = jtfRepresentantePP.getText();
        String correo = jtfCorreoPP.getText();
        String telefono = jtfTelefonoPP.getText();
        String direccion = jtfDireccionPP.getText();
        try {
            int idMaquinaria=0;
            if(maquinaria.compareToIgnoreCase("Seleccionar")!=0){
                idMaquinaria = bd.consultaIdMaquinaria(maquinaria);
            }
            bd.insertarProveedor(razon, idMaquinaria, representante, correo, telefono, direccion);
            ventanaFeed CCP = new ventanaFeed(null, true, "Proveedor agregado con exito");
            CCP.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jbAgregarProveedorPPActionPerformed

    private void ordenaProveedor(){
        String opc = "razon";
        String sort = "";
        switch(jcbSortListaProveedoresPP.getSelectedIndex()){
            case 1:
                opc = "razon";
                break;
            case 2:
                opc = "maquinaria";
                break;
            case 3:
                opc = "representante";
                break;
            case 4:
                opc = "email";
                break;
            case 5:
                opc = "telefono";
                break;
            case 6:
                opc = "direccion";
                break;
        }
        try {
            String matchAux = jtfBuscarPP.getText();
            if(jtbSortProveedoresPP.isSelected()){
                sort = "DESC";
            }else{
                sort = "ASC";
            }
            String[][] res = bd.listarProveedores(matchAux, opc, sort);
            Tabla.estableceTablaProveedores(res,jtProveedoresPP);
        } 
        catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void jcbSortListaProveedoresPPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbSortListaProveedoresPPItemStateChanged
        if(!logout)
        ordenaProveedor();
    }//GEN-LAST:event_jcbSortListaProveedoresPPItemStateChanged

    private void jtfBuscarPPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfBuscarPPKeyReleased
        if(!logout)
        ordenaProveedor();
    }//GEN-LAST:event_jtfBuscarPPKeyReleased

    private void jtbSortProveedoresPPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jtbSortProveedoresPPItemStateChanged
        if(!logout){
            ordenaProveedor();
        }
    }//GEN-LAST:event_jtbSortProveedoresPPItemStateChanged

    private void jtpPPStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpPPStateChanged
        if(jtpPP.getSelectedIndex()==1){
            ordenaProveedor();
        }
    }//GEN-LAST:event_jtpPPStateChanged

    private void jtfCorreoPPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfCorreoPPKeyPressed
        if(evt.getKeyCode()==8){
            txtCorreoNoValidoPP.setVisible(false);
        }
    }//GEN-LAST:event_jtfCorreoPPKeyPressed

    private void JTFUsuarioLoginKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JTFUsuarioLoginKeyPressed
        String user = new String(JTFUsuarioLogin.getText());
        String password = new String(PasswordLogin.getPassword());
        if(evt.getKeyCode()==10){
            if(user.length()>0 && password.length()>0){
                login();
            }
        }
    }//GEN-LAST:event_JTFUsuarioLoginKeyPressed

    private void jtfUsuarioPUKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfUsuarioPUKeyReleased
        String user = jtfUsuarioPU.getText();
        try {
            if(bd.verificaUsuario(user)){
                txtUsuarioYaExistentePU.setVisible(true);
                usuarioExiste=true;
            }else{
                txtUsuarioYaExistentePU.setVisible(false);
                usuarioExiste=false;
            }
            if(apellidoUsuario && nombreUsuario && contrasenaUsuario && !usuarioExiste){
                jbCrearUsuarioPU.setEnabled(true);
                jbCrearUsuarioPU.setBackground(azulF);
            }else{
                jbCrearUsuarioPU.setEnabled(false);
                jbCrearUsuarioPU.setBackground(gris);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jtfUsuarioPUKeyReleased

    private void jpfPasswordPUKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpfPasswordPUKeyReleased
        String clave1 = new String(jpfPasswordPU.getPassword());
        String clave2 = new String(jpfConfirmarPasswordPu.getPassword());
        contrasenaUsuario=herramientas.contrasenasGUI(clave1,clave2,txtPasswordDiferentePU,txtLogitudMenorPU);
        if(apellidoUsuario && nombreUsuario && contrasenaUsuario && !usuarioExiste){
            jbCrearUsuarioPU.setEnabled(true);
            jbCrearUsuarioPU.setBackground(azulF);
        }else{
            jbCrearUsuarioPU.setEnabled(false);
            jbCrearUsuarioPU.setBackground(gris);
        }
    }//GEN-LAST:event_jpfPasswordPUKeyReleased

    private void jpfConfirmarPasswordPuKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpfConfirmarPasswordPuKeyReleased
        String clave1 = new String(jpfPasswordPU.getPassword());
        String clave2 = new String(jpfConfirmarPasswordPu.getPassword());
        contrasenaUsuario=herramientas.contrasenasGUI(clave1,clave2,txtPasswordDiferentePU,txtLogitudMenorPU);
        if(apellidoUsuario && nombreUsuario && contrasenaUsuario && !usuarioExiste){
            jbCrearUsuarioPU.setEnabled(true);
            jbCrearUsuarioPU.setBackground(azulF);
        }else{
            jbCrearUsuarioPU.setEnabled(false);
            jbCrearUsuarioPU.setBackground(gris);
        }
    }//GEN-LAST:event_jpfConfirmarPasswordPuKeyReleased

    private void jtfNombrePUKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfNombrePUKeyReleased
        if(jtfNombrePU.getText().length()>0)
        nombreUsuario = true;
        if(apellidoUsuario && nombreUsuario && contrasenaUsuario && !usuarioExiste){
            jbCrearUsuarioPU.setEnabled(true);
            jbCrearUsuarioPU.setBackground(azulF);
        }else{
            jbCrearUsuarioPU.setEnabled(false);
            jbCrearUsuarioPU.setBackground(gris);
        }
    }//GEN-LAST:event_jtfNombrePUKeyReleased

    private void jtfApellidosPUKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfApellidosPUKeyReleased
        if(jtfApellidosPU.getText().length()>0)
        apellidoUsuario = true;
        if(apellidoUsuario && nombreUsuario && contrasenaUsuario && !usuarioExiste){
            jbCrearUsuarioPU.setEnabled(true);
            jbCrearUsuarioPU.setBackground(azulF);
        }else{
            jbCrearUsuarioPU.setEnabled(false);
            jbCrearUsuarioPU.setBackground(gris);
        }
    }//GEN-LAST:event_jtfApellidosPUKeyReleased

    private void jbCrearUsuarioPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCrearUsuarioPUActionPerformed
        try {
            ventanaFeed dialog = new ventanaFeed(new javax.swing.JFrame(), true, "Usuario creado con exito");
            String usuario = jtfUsuarioPU.getText();
            String contrasena= new String(jpfPasswordPU.getPassword());
            String nombre = jtfNombrePU.getText();
            String apellido = jtfApellidosPU.getText();
            bd.creaUsuario(jcbTipoUsuarioPU.getSelectedIndex(), usuario, contrasena, apellido, nombre);
            dialog.setVisible(true);
            jtfUsuarioPU.setText("");
            jpfPasswordPU.setText("");
            jpfConfirmarPasswordPu.setText("");
            jtfNombrePU.setText("");
            jtfApellidosPU.setText("");
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbCrearUsuarioPUActionPerformed

    private void jtUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtUsuariosMouseClicked
        int columna = jtUsuarios.getSelectedColumn();
        int fila = jtUsuarios.getSelectedRow();
        Object id = jtUsuarios.getValueAt(fila, columna);
        if(id instanceof JButton){
            if(columna == 3){//modificar
                ((JButton)id).doClick();
                JButton boton = (JButton) id;
                String idUsuario = (String) jtUsuarios.getValueAt(fila, 0);
                txtUserNamePU.setText(idUsuario);
                txtNombreUsuarioPU.setText((String) jtUsuarios.getValueAt(fila, 1));
                txtApellidosUsuarioPU.setText((String) jtUsuarios.getValueAt(fila, 2));
                jspUsuariosPU.setVisible(false);
                jpUsuarioPU.setVisible(true);
            }else if(columna == 4){//eliminar
                ((JButton)id).doClick();
                JButton boton = (JButton) id;
                String idUsuario = (String) jtUsuarios.getValueAt(fila, 0);
                int re;
                re = JOptionPane.showConfirmDialog(null, "Esta acción no es reversible", "Advertencia", JOptionPane.WARNING_MESSAGE);
                if(re == 0){
                    try {
                        bd.eliminaUsuario(idUsuario);
                        Object[][] usuarios = bd.listarUsuarios(usuario);
                        t.ver_tabla(jtUsuarios, usuarios);
                    } catch (SQLException ex) {
                        Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_jtUsuariosMouseClicked

    private void jtpPUStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpPUStateChanged
        if(jtpPU.getSelectedIndex()==1){
            try {
                Object[][] usuarios = bd.listarUsuarios(usuario);
                t.ver_tabla(jtUsuarios, usuarios);
            } catch (SQLException ex) {
                Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jtpPUStateChanged

    private void jbCambiarInfoUsuarioPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCambiarInfoUsuarioPUActionPerformed
        String p = new String(jpfPasswordUsuarioPU.getPassword());
        try {
            bd.cambiaContrasena(p, usuario);
            ventanaFeed vf = new ventanaFeed(null, true, "Contraseña cambiada con exito");
            vf.setVisible(true);
            jpfPasswordUsuarioPU.setText("");
            jpfPasswordConfUsuarioPU.setText("");
            jbCambiarInfoUsuarioPU.setBackground(gris);
            jbCambiarInfoUsuarioPU.setEnabled(false);
            jpUsuarioPU.setVisible(false);
            jspUsuariosPU.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jbCambiarInfoUsuarioPUActionPerformed

    private void jpfPasswordUsuarioPUKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpfPasswordUsuarioPUKeyReleased
        String p1 = new String(jpfPasswordUsuarioPU.getPassword());
        String p2 = new String(jpfPasswordConfUsuarioPU.getPassword());
        herramientas.contrasenasGUI(p1,p2,jbCambiarInfoUsuarioPU,txtPasswordDiferenteUsuarioPU,txtPasswordLongitudMenorPU);
    }//GEN-LAST:event_jpfPasswordUsuarioPUKeyReleased

    private void jpfPasswordConfUsuarioPUKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpfPasswordConfUsuarioPUKeyReleased
        String p1 = new String(jpfPasswordUsuarioPU.getPassword());
        String p2 = new String(jpfPasswordConfUsuarioPU.getPassword());
        herramientas.contrasenasGUI(p1,p2,jbCambiarInfoUsuarioPU,txtPasswordDiferenteUsuarioPU,txtPasswordLongitudMenorPU);
    }//GEN-LAST:event_jpfPasswordConfUsuarioPUKeyReleased

    private void jbRegresarUsuarioPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRegresarUsuarioPUActionPerformed
        jspUsuariosPU.setVisible(true);
        jpUsuarioPU.setVisible(false);
    }//GEN-LAST:event_jbRegresarUsuarioPUActionPerformed

    private void jtfNombrePMKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfNombrePMKeyReleased
        if(jtfNombrePM.getText().length()!=0){
            try {
                Object[][] maquinas = bd.obtenerMaquinariaLikeMatriz(jtfNombrePM.getText().toUpperCase());
                t.ver_tabla2(jtMaquinariasPM, maquinas);
            } catch (SQLException ex) {
                Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if(bd.verificaMaquinaria(jtfNombrePM.getText().toUpperCase())){
                    jbAgregarMaquinariaPM.setEnabled(false);
                    jbAgregarMaquinariaPM.setBackground(gris);
                }else{
                    jbAgregarMaquinariaPM.setEnabled(true);
                    jbAgregarMaquinariaPM.setBackground(azulF);
                }
            } catch (SQLException ex) {
                Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            jbAgregarMaquinariaPM.setEnabled(false);
            jbAgregarMaquinariaPM.setBackground(gris);
        }
    }//GEN-LAST:event_jtfNombrePMKeyReleased

    private void jbAgregarMaquinariaPMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAgregarMaquinariaPMActionPerformed
        try {
            jtfNombrePM.setText("");
            bd.insertaMaquinaria(jtfNombrePM.getText());
            Object[][] maquinas = bd.obtenerMaquinariaMatriz();
            t.ver_tabla2(jtMaquinariasPM, maquinas);
        } catch (SQLException ex) {
            Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbAgregarMaquinariaPMActionPerformed

    private void jtMaquinariasPMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtMaquinariasPMMouseClicked
        int columna = jtMaquinariasPM.getSelectedColumn();
        int fila = jtMaquinariasPM.getSelectedRow();
        Object id = jtMaquinariasPM.getValueAt(fila, columna);
        if(id instanceof JButton){
            ((JButton)id).doClick();
            JButton boton = (JButton) id;
            String nombreMaq = (String) jtMaquinariasPM.getValueAt(fila, 0);
            int re;
            re = JOptionPane.showConfirmDialog(null, "Esta acción no es reversible", "Advertencia", JOptionPane.WARNING_MESSAGE);
            if(re == 0){
                try {
                    bd.eliminaMaquinaria(nombreMaq);
                    Object[][] maquinas = bd.obtenerMaquinariaMatriz();
                    t.ver_tabla2(jtMaquinariasPM, maquinas);
                } catch (SQLException ex) {
                    Logger.getLogger(GUIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jtMaquinariasPMMouseClicked

    private void consultaInfoTablaRegistro(){
        Date fecha1 = fechaInicio.getDate();
        Date fecha2 = fechaFinal.getDate();
        String opc = "";
        switch(jcbOrdenacionPR.getSelectedIndex()){
            case 0:
                opc = "idProductos";
                break;
            case 1:
                opc = "nombre";
                break;
            case 2:
                opc = "fecha";
                break;
            case 3:
                opc = "cantidad";
                break;
            case 4:
                opc = "total";
                break;
            case 5:
                opc = "detalles";
                break;
        }
        try {
            //actualizar tabla
            DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
            String f1 = DF.format(fecha1);
            f1 = f1+" 00:00:00";
            String f2 = DF.format(fecha2);
            f2 = f2+" 23:59:59";
            if(jcbMostrarPR.getSelectedIndex()==0){
                String[][] res = bd.listarRegistroIn(opc, f1, f2);
                Tabla.estableceTablaRegistro(res,jtRegistrosPR);
            }else if(jcbMostrarPR.getSelectedIndex()==1){
                String[][] res = bd.listarRegistroEntradas(opc, f1, f2);
                Tabla.estableceTablaRegistroEntradas(res,jtRegistrosPR);
            }else{
                String[][] res = bd.listarRegistroSalidas(opc, f1, f2);
                Tabla.estableceTablaRegistro(res,jtRegistrosPR);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void jcbOrdenacionPRItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbOrdenacionPRItemStateChanged
        if(!logout)
            consultaInfoTablaRegistro();
    }//GEN-LAST:event_jcbOrdenacionPRItemStateChanged

    private void fechaFinalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fechaFinalPropertyChange
        if(!logout){
            if ("date".equals(evt.getPropertyName())) {
                if(evt.getNewValue().toString().compareTo(fechaRegFin.toString())!=0){
                    fechaRegFin=fechaFinal.getDate();
                    fechaInicio.getJCalendar().setMaxSelectableDate(fechaFinal.getDate());
                    consultaInfoTablaRegistro();
                }
            }
        }
    }//GEN-LAST:event_fechaFinalPropertyChange

    private void fechaInicioPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fechaInicioPropertyChange
        if(!logout){
            if ("date".equals(evt.getPropertyName())) {
                if(evt.getNewValue().toString().compareTo(fechaRegIn.toString())!=0){
                    fechaRegIn=fechaInicio.getDate();
                    consultaInfoTablaRegistro();
                }
            }
        }
            
    }//GEN-LAST:event_fechaInicioPropertyChange

    private void jcbMostrarPRItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbMostrarPRItemStateChanged
        if(!logout)
            consultaInfoTablaRegistro();
    }//GEN-LAST:event_jcbMostrarPRItemStateChanged

    private void jpfPasswordPCKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpfPasswordPCKeyReleased
        String p1 = new String(jpfPasswordPC.getPassword());
        String p2 = new String(jpfConfirmarPasswordPC.getPassword());
        herramientas.contrasenasGUI(p1, p2, jbCambiarPasswordPC, txtPasswordDiferentePC, txtLongitudMenorPC);
    }//GEN-LAST:event_jpfPasswordPCKeyReleased

    private void jpfConfirmarPasswordPCKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpfConfirmarPasswordPCKeyReleased
        String p1 = new String(jpfPasswordPC.getPassword());
        String p2 = new String(jpfConfirmarPasswordPC.getPassword());
        herramientas.contrasenasGUI(p1, p2, jbCambiarPasswordPC, txtPasswordDiferentePC, txtLongitudMenorPC);
    }//GEN-LAST:event_jpfConfirmarPasswordPCKeyReleased

    private void jbCambiarPasswordPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCambiarPasswordPCActionPerformed
        String p = new String(jpfPasswordPC.getPassword());
        try {
            bd.cambiaContrasena(p, usuario);
            txtPasswordCambiadaPC.setText("Contraseña cambiada con exito");
            txtPasswordCambiadaPC.setVisible(true);
            logout();
        } catch (SQLException ex) {
            txtPasswordCambiadaPC.setText("Ocurrio un error");
            txtPasswordCambiadaPC.setVisible(true);
        }
    }//GEN-LAST:event_jbCambiarPasswordPCActionPerformed

    private void jbNotificacionesPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNotificacionesPNActionPerformed
        if(!popupNotificaciones.isShowing()){
            popupNotificaciones.show(panelNombre, jbNotificacionesPN.getX()-jbNotificacionesPN.getWidth(),jbNotificacionesPN.getY()+jbNotificacionesPN.getHeight());
        }else{
            jbNotificacionesPN.hide();
        }
    }//GEN-LAST:event_jbNotificacionesPNActionPerformed

    private void actualizaTablaMinimos(){
        String order;
        if(jcbOrdenacionPMin.getSelectedIndex()==0){
            order = "idProductos";
        }else{
            order = "nombre";
        }
        String opc;
        if(jtbSortPMin.isSelected())
            opc="DESC";
        else
            opc="ASC";
        try {
            String res[][] = bd.obtenerMinimos(order, opc);
            Tabla.estableceTablaMinimos(res, jtMinimosPMin);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void jtbSortPMinItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jtbSortPMinItemStateChanged
        if(!logout){
            actualizaTablaMinimos();
        }
    }//GEN-LAST:event_jtbSortPMinItemStateChanged

    private void jcbOrdenacionPMinItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbOrdenacionPMinItemStateChanged
        if(!logout){
            actualizaTablaMinimos();
        }
    }//GEN-LAST:event_jcbOrdenacionPMinItemStateChanged

    private void jtMinimosPMinMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtMinimosPMinMouseClicked
        try {
            String buscar = (String) jtMinimosPMin.getModel().getValueAt(jtMinimosPMin.getSelectedRow(), 0);
            setInfo(buscar);
            for(int i=0;i<listaProveedoresB.size();i++){
                jcbProveedoresPMD.removeItem(listaProveedoresB.get(i));
            }
            if(jcbProveedoresPMD.getItemCount()>0){
                jbAgregarProveedorPMD.setEnabled(true);
                jbAgregarProveedorPMD.setBackground(azulF);
            }else{
                jbAgregarProveedorPMD.setEnabled(false);
                jbAgregarProveedorPMD.setBackground(gris);
            }
            if(admin==true)
                ProductosAdm.doClick();
            else
                productosOp.doClick();
            setVisiblePanelesPrincipales(2);
            estadoBusqueda=2;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jtMinimosPMinMouseClicked

    /**
     * @param args the command line arguments
     */
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUIPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUIPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUIPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUIPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUIPrincipal().setVisible(true);
            }
        });
    }
    
    public JMenuItem jmiNotificaciones;
    public JPopupMenu popupOpciones;
    private JPopupMenu popupNotificaciones;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField JTFUsuarioLogin;
    private javax.swing.JPasswordField PasswordLogin;
    private javax.swing.JToggleButton PasswordToggleLogin;
    private javax.swing.JToggleButton ProductosAdm;
    private javax.swing.JToggleButton agregarProductos;
    private javax.swing.JToggleButton agregarProductosOp;
    private javax.swing.JToggleButton agregarUsers;
    private javax.swing.ButtonGroup bgBotonesPrincipales;
    private javax.swing.JButton cerrarVentana;
    private javax.swing.JLabel errorContraseñaLogin;
    private javax.swing.JEditorPane erroresCSVPanel;
    private com.toedter.calendar.JDateChooser fechaFinal;
    private com.toedter.calendar.JDateChooser fechaInicio;
    private javax.swing.JPanel gestaltAdm;
    private javax.swing.JPanel gestaltOp;
    private javax.swing.JLabel imagenUsuarioLogin;
    private javax.swing.JButton iniciarSesion;
    private javax.swing.JButton jbAgregarMaquinariaPM;
    private javax.swing.JButton jbAgregarProductoPAP;
    private javax.swing.JButton jbAgregarProveedorPAP;
    private javax.swing.JButton jbAgregarProveedorPMD;
    private javax.swing.JButton jbAgregarProveedorPP;
    private javax.swing.JButton jbAgregarUbicacionPMD;
    private javax.swing.JButton jbAgregarUbicacionesPAP;
    private javax.swing.JButton jbAgregarUnProductoPAP;
    private javax.swing.JButton jbAgregarVariosProductoPAP;
    private javax.swing.JButton jbBorrarPMD;
    private javax.swing.JButton jbCambiarInfoUsuarioPU;
    private javax.swing.JButton jbCambiarPasswordPC;
    private javax.swing.JButton jbCargarCSVPAP;
    private javax.swing.JButton jbCrearUsuarioPU;
    private javax.swing.JButton jbEntradasPMD;
    private javax.swing.JButton jbGuardarCambiosPMD;
    private javax.swing.JButton jbImprimirPMD;
    private javax.swing.JButton jbNotificacionesPN;
    private javax.swing.JButton jbOpcionesPN;
    private javax.swing.JButton jbRegresarOneProductoPAP;
    private javax.swing.JButton jbRegresarPMD;
    private javax.swing.JButton jbRegresarUsuarioPU;
    private javax.swing.JButton jbRegresarVariosProductoPAP;
    private javax.swing.JButton jbSalidasPMD;
    private javax.swing.JComboBox<String> jcbEleccionMostrarPMD;
    private javax.swing.JComboBox<String> jcbMaquinariaPAP;
    private javax.swing.JComboBox<String> jcbMaquinariaPMD;
    private javax.swing.JComboBox<String> jcbMaquinariaPP;
    private javax.swing.JComboBox<String> jcbMostrarPR;
    private javax.swing.JComboBox<String> jcbOrdenacionPCP;
    private javax.swing.JComboBox<String> jcbOrdenacionPMin;
    private javax.swing.JComboBox<String> jcbOrdenacionPR;
    private javax.swing.JComboBox<String> jcbProveedoresPAP;
    private javax.swing.JComboBox<String> jcbProveedoresPMD;
    private javax.swing.JComboBox<String> jcbSortListaProveedoresPP;
    private javax.swing.JComboBox<String> jcbTipoUsuarioPU;
    private javax.swing.JComboBox<String> jcbUMedidaPAP;
    private javax.swing.JComboBox<String> jcbUniMedidaPMD;
    private com.toedter.calendar.JDateChooser jdcFechaFinalPMD;
    private com.toedter.calendar.JDateChooser jdcFechaInicialPMD;
    private javax.swing.JLabel jlAgregarImagenPAP;
    private javax.swing.JLabel jlImagenPMD;
    private javax.swing.JList<String> jlProveedoresPAP;
    private javax.swing.JList<String> jlProveedoresPMD;
    private javax.swing.JList<String> jlUbicacionesPAP;
    private javax.swing.JList<String> jlUbicacionesPMD;
    private javax.swing.JPanel jpAdministrarUsuariosPU;
    private javax.swing.JPanel jpAgregarProductoPAP;
    private javax.swing.JPanel jpAgregarProveedorPP;
    private javax.swing.JPanel jpBotonCSVPAP;
    private javax.swing.JPanel jpCargarProductoPAP;
    private javax.swing.JPanel jpCrearUsuarioPU;
    private javax.swing.JPanel jpESPMD;
    private javax.swing.JPanel jpIdentificacionProductoPAP;
    private javax.swing.JPanel jpInfoMaquina;
    private javax.swing.JPanel jpInfoProductoPAP;
    private javax.swing.JPanel jpInfoProveedoresPP;
    private javax.swing.JPanel jpInfoUsuarioPU;
    private javax.swing.JPanel jpInformacionUsuarioPU;
    private javax.swing.JPanel jpInstruccionesPAP;
    private javax.swing.JPanel jpInventarioPAP;
    private javax.swing.JPanel jpInventarioPMD;
    private javax.swing.JPanel jpListaProveedoresPP;
    private javax.swing.JPanel jpOpcionesPAP;
    private javax.swing.JPanel jpPasswordPC;
    private javax.swing.JPanel jpProductoInfoPMD;
    private javax.swing.JPanel jpProveedoresPMD;
    private javax.swing.JPanel jpProveedoresProductoPAP;
    private javax.swing.JPanel jpResultadoPMD;
    private javax.swing.JPanel jpUsuarioPU;
    private javax.swing.JPasswordField jpfConfirmarPasswordPC;
    private javax.swing.JPasswordField jpfConfirmarPasswordPu;
    private javax.swing.JPasswordField jpfPasswordConfUsuarioPU;
    private javax.swing.JPasswordField jpfPasswordPC;
    private javax.swing.JPasswordField jpfPasswordPU;
    private javax.swing.JPasswordField jpfPasswordUsuarioPU;
    private javax.swing.JScrollPane jspDescripcionPAP;
    private javax.swing.JScrollPane jspDescripcionPMD;
    private javax.swing.JScrollPane jspEntradasSalidasPMD;
    private javax.swing.JScrollPane jspErroresCargaPAP;
    private javax.swing.JScrollPane jspMaquinariaPM;
    private javax.swing.JScrollPane jspMinimosPMin;
    private javax.swing.JScrollPane jspProveedoresPAP;
    private javax.swing.JScrollPane jspProveedoresPMD;
    private javax.swing.JScrollPane jspRegistrosPR;
    private javax.swing.JScrollPane jspResultadoPMD;
    private javax.swing.JScrollPane jspTablaPCP;
    private javax.swing.JScrollPane jspTablaProveedoresPP;
    private javax.swing.JScrollPane jspUbicacionesPAP;
    private javax.swing.JScrollPane jspUbicacionesPMD;
    private javax.swing.JScrollPane jspUsuariosPU;
    private javax.swing.JTable jtEntradasSalidasPMD;
    private javax.swing.JTable jtMaquinariasPM;
    private javax.swing.JTable jtMinimosPMin;
    private javax.swing.JTable jtProveedoresPP;
    private javax.swing.JTable jtRegistrosPR;
    private javax.swing.JTable jtTablaPCP;
    private javax.swing.JTable jtUsuarios;
    private javax.swing.JTextArea jtaDescripcionPAP;
    private javax.swing.JTextArea jtaDescripcionPMD;
    private javax.swing.JToggleButton jtbEditarPMD;
    private javax.swing.JToggleButton jtbSortPCP;
    private javax.swing.JToggleButton jtbSortPMin;
    private javax.swing.JToggleButton jtbSortProveedoresPP;
    private javax.swing.JTextField jtfApellidosPU;
    private javax.swing.JTextField jtfBuscarPCP;
    private javax.swing.JTextField jtfBuscarPP;
    private javax.swing.JTextField jtfCantidadPAP;
    private javax.swing.JTextField jtfClaveProductoPAP;
    private javax.swing.JTextField jtfClaveProductoPMD;
    private javax.swing.JTextField jtfCorreoPP;
    private javax.swing.JTextField jtfDireccionPP;
    private javax.swing.JTextField jtfEmpleoPAP;
    private javax.swing.JTextField jtfEmpleoPMD;
    private javax.swing.JTextField jtfMinimoPAP;
    private javax.swing.JTextField jtfMinimosPMD;
    private javax.swing.JTextField jtfNombrePAP;
    private javax.swing.JTextField jtfNombrePM;
    private javax.swing.JTextField jtfNombrePMP;
    private javax.swing.JTextField jtfNombrePU;
    private javax.swing.JTextField jtfPrecioEstimadoPAP;
    private javax.swing.JTextField jtfPrecioPMD;
    private javax.swing.JTextField jtfRazonPP;
    private javax.swing.JTextField jtfRepresentantePP;
    private javax.swing.JTextField jtfTelefonoPP;
    private javax.swing.JTextField jtfUbicacionesPAP;
    private javax.swing.JTextField jtfUbicacionesPMD;
    private javax.swing.JTextField jtfUsuarioPU;
    private javax.swing.JTabbedPane jtpPP;
    private javax.swing.JTabbedPane jtpPU;
    private javax.swing.JPanel login;
    private javax.swing.JToggleButton maquinaria;
    private javax.swing.JButton minimizarVentana;
    private javax.swing.JPanel opciones;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panelAgregarProducto;
    private javax.swing.JPanel panelConfiguracion;
    private javax.swing.JPanel panelConsultaProductos;
    private javax.swing.JPanel panelLogo;
    private javax.swing.JPanel panelMaquinaria;
    private javax.swing.JPanel panelMinimos;
    private javax.swing.JPanel panelMostrarProducto;
    private javax.swing.JPanel panelNombre;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelProveedor;
    private javax.swing.JPanel panelRegistro;
    private javax.swing.JPanel panelUsuario;
    private javax.swing.JToggleButton productosOp;
    private javax.swing.JToggleButton proveedorButton;
    private javax.swing.JToggleButton registroBoton;
    private javax.swing.JToggleButton registroBoton1;
    private javax.swing.JLabel rogeriLogo;
    private javax.swing.JLabel txtAgregarMaquinariaPM;
    private javax.swing.JLabel txtAgregarProductosGA;
    private javax.swing.JLabel txtAgregarProductosGOP;
    private javax.swing.JLabel txtAgregarUnProductoPAP;
    private javax.swing.JLabel txtAgregarVariosProductoPAP;
    private javax.swing.JLabel txtApellidosInfoPU;
    private javax.swing.JLabel txtApellidosPU;
    private javax.swing.JLabel txtApellidosUsuarioPU;
    private javax.swing.JLabel txtBienvenidaLogin;
    private javax.swing.JLabel txtBienvenidaPN;
    private javax.swing.JLabel txtBuscarPCP;
    private javax.swing.JLabel txtBuscarPP;
    private javax.swing.JLabel txtCambiarPasswordPC;
    private javax.swing.JLabel txtCambiarPasswordPU;
    private javax.swing.JLabel txtCamposRequeridosPP;
    private javax.swing.JLabel txtCantidadPAP;
    private javax.swing.JLabel txtClaveProductoPAP;
    private javax.swing.JLabel txtConfirmarContrasenaPC;
    private javax.swing.JLabel txtConfirmarContrasenaPU;
    private javax.swing.JLabel txtContrasenaConfirmarModificarPU;
    private javax.swing.JLabel txtContrasenaModificarPU;
    private javax.swing.JLabel txtCorreoNoValidoPP;
    private javax.swing.JLabel txtCorreoPP;
    private javax.swing.JLabel txtDescripcionPAP;
    private javax.swing.JLabel txtDescripcionPMD;
    private javax.swing.JLabel txtDesdePMD;
    private javax.swing.JLabel txtDesdePR;
    private javax.swing.JLabel txtDireccionPP;
    private javax.swing.JLabel txtDisponiblesPMD;
    private javax.swing.JLabel txtEmpleoPAP;
    private javax.swing.JLabel txtEmpleoPMD;
    private javax.swing.JLabel txtErrorIDPAP;
    private javax.swing.JLabel txtExitoCargaPAP;
    private javax.swing.JLabel txtHastaPMD;
    private javax.swing.JLabel txtHastaPR;
    private javax.swing.JLabel txtInstruccion1PAP;
    private javax.swing.JLabel txtInstruccion2PAP;
    private javax.swing.JLabel txtInstruccion3PAP;
    private javax.swing.JLabel txtInstruccion4PAP;
    private javax.swing.JLabel txtLogitudMenorPU;
    private javax.swing.JLabel txtLongitudMenorPC;
    private javax.swing.JLabel txtMaquinariaGA;
    private javax.swing.JLabel txtMaquinariaPAP;
    private javax.swing.JLabel txtMaquinariaPMD;
    private javax.swing.JLabel txtMaquinariaPP;
    private javax.swing.JLabel txtMinimoPAP;
    private javax.swing.JLabel txtMinimosPMD;
    private javax.swing.JLabel txtMostrarPMD;
    private javax.swing.JLabel txtMostrarPR;
    private javax.swing.JLabel txtNombreDeUsuarioPU;
    private javax.swing.JLabel txtNombreInfoPU;
    private javax.swing.JLabel txtNombrePAP;
    private javax.swing.JLabel txtNombrePM;
    private javax.swing.JLabel txtNombrePMD;
    private javax.swing.JLabel txtNombrePU;
    private javax.swing.JLabel txtNombreUsuarioPU;
    private javax.swing.JLabel txtOrdenarPCP;
    private javax.swing.JLabel txtOrdenarPMin;
    private javax.swing.JLabel txtOrdenarPP;
    private javax.swing.JLabel txtOrdenarPR;
    private javax.swing.JLabel txtPasswordCambiadaPC;
    private javax.swing.JLabel txtPasswordDiferentePC;
    private javax.swing.JLabel txtPasswordDiferentePU;
    private javax.swing.JLabel txtPasswordDiferenteUsuarioPU;
    private javax.swing.JLabel txtPasswordLogin;
    private javax.swing.JLabel txtPasswordLongitudMenorPU;
    private javax.swing.JLabel txtPasswordPC;
    private javax.swing.JLabel txtPasswordPU;
    private javax.swing.JLabel txtPrecioPAP;
    private javax.swing.JLabel txtPrecioPMD;
    private javax.swing.JLabel txtProductosGA;
    private javax.swing.JLabel txtProductosGOP;
    private javax.swing.JLabel txtProveedoresGA;
    private javax.swing.JLabel txtProveedoresPMD;
    private javax.swing.JLabel txtRazonPP;
    private javax.swing.JLabel txtRegistroGA;
    private javax.swing.JLabel txtRegistroGOP;
    private javax.swing.JLabel txtRepresentantePP;
    private javax.swing.JLabel txtSeleccionarPAP;
    private javax.swing.JLabel txtSeleccionarPCP;
    private javax.swing.JLabel txtTelefonoPP;
    private javax.swing.JLabel txtTipoUsuarioPU;
    private javax.swing.JLabel txtTituloInstruccionesPAP;
    private javax.swing.JLabel txtUMedidaPMD;
    private javax.swing.JLabel txtUbicacionesPAP;
    private javax.swing.JLabel txtUbicacionesPMD;
    private javax.swing.JLabel txtUniDisponiblesPMD;
    private javax.swing.JLabel txtUserNamePU;
    private javax.swing.JLabel txtUsuarioInfoPU;
    private javax.swing.JLabel txtUsuarioLogin;
    private javax.swing.JLabel txtUsuarioYaExistentePU;
    private javax.swing.JLabel txtUsuariosGA;
    private javax.swing.JLabel txtValoresRequeridosPAP;
    // End of variables declaration//GEN-END:variables
}
