/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import com.google.zxing.WriterException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author luix_
 */

public class baseDeDatos {
    
    private static herramientasGUI herramientas = new herramientasGUI();
    
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    
    private static String nombreCuenta;
    
    
    //Informacion producto
    private static String claveProducto;
    private static String nombreProducto;
    private static String maquinariaProducto;
    private static String usoProducto;
    private static String descripcionProducto;
    private static String uMedidaProducto;
    private static BufferedImage qrProducto;
    private static BufferedImage imagenProducto;
    private static int idInventario;
    private static int idZona;
    private static String zona;
    private static ArrayList<String> ubicaciones = new ArrayList<String>();
    private static ArrayList<String> proveedores = new ArrayList<String>();
    //private static ArrayList<String> proveedoresProducto;
    private static int cantidadDisponible;
    private static int minimoProducto;
    private static float precio;

    
    //Informacion global
    private static ArrayList<String> maquinariaNombres = new ArrayList<String>();
    
    public baseDeDatos(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int Login(String url, String cuenta, String contrasena) throws SQLException{
        con = DriverManager.getConnection(url,cuenta,contrasena);
        if(obtenerNombreCuenta(cuenta)){
            stmt = con.createStatement();
            rs = stmt.executeQuery("SHOW GRANTS FOR "+cuenta+"@'192.168.%.%'");
            rs.next();
            rs.next();
            String res = rs.getString(1);
            res = res.substring(0,res.indexOf(" ON"));
            if(res.compareToIgnoreCase("GRANT SELECT, INSERT, UPDATE, DELETE")==0){
                return 1;
            }else if(res.compareToIgnoreCase("GRANT SELECT")==0){
                return 2;
            }else{
                JOptionPane.showMessageDialog (null, "Error obteniendo datos del usuario", "Error", JOptionPane.ERROR_MESSAGE);
                return 0;
            }
        }else{
            return 0;
        }
    }
    
    private boolean obtenerNombreCuenta(String cuenta) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT nombre FROM usuario WHERE idUsuario = '"+cuenta+"'");
        rs.next();
        String nombre = rs.getString("nombre");
        if(nombre!=null){
            this.setNombreCuenta(nombre);
            return true;
        }else{
            return false;
        }
    }
    
    public static Object[][] listarUsuarios(String user) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) FROM listausuarios WHERE idUsuario!= '"+user+"'");
        rs.next();
        Object[][] usuarios = new Object[rs.getInt(1)][5];
        rs = stmt.executeQuery("SELECT * FROM listausuarios WHERE idUsuario!= '"+user+"'");
        int i=0;
        while(rs.next()){
            usuarios[i][0]=rs.getString("idUsuario");
            usuarios[i][1]=rs.getString("nombre");
            usuarios[i][2]=rs.getString("apellidos");
            JButton mod = new JButton("Modificar");
            mod.setBackground(new Color(25,30,111));
            mod.setFont(new Font("Open Sans", Font.BOLD,12));
            mod.setForeground(new Color(255,255,255));
            mod.setContentAreaFilled(false);
            mod.setOpaque(true);
            usuarios[i][3] = mod;
            JButton del = new JButton("Eliminar");
            del.setBackground(new Color(200,30,30));
            del.setFont(new Font("Open Sans", Font.BOLD,12));
            del.setForeground(new Color(255,255,255));
            del.setContentAreaFilled(false);
            del.setOpaque(true);
            usuarios[i][4] = del;
            i+=1;
        }
        return usuarios;
    }
    
    public static int insertarInventario(int cDisponible, float precio, int minimo) throws SQLException{
        int re;
        stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO inventario values(null,"+cDisponible+","+precio+","+minimo+")",Statement.RETURN_GENERATED_KEYS);
        rs = stmt.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }
    
    public static int buscarZona(String nombre) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT idZona from zona where nombre=upper('"+nombre+"')");
        rs.next();
        return rs.getInt("idZona");
    }
    
    public static boolean verificaZona(String nombre) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) from zona where nombre=upper('"+nombre+"')");
        rs.next();
        if(rs.getInt(1)==1){
            return true;
        }else{
            return false;
        }
    }
    
    public static void agregarProducto(String idProducto, String nombre, int Maquinaria, String uso, String descripcion, String uMedida, InputStream qr, InputStream imagen, int idInventario, int idZona, boolean imagenSel) throws SQLException{
        PreparedStatement st = con.prepareStatement("INSERT INTO productos VALUES(?,?,?,?,?,?,?,?,?,?)");
        st.setString(1, idProducto);
        st.setString(2, nombre);
        if(Maquinaria>0){
            st.setInt(3, Maquinaria);
        }else{
            st.setNull(3, java.sql.Types.INTEGER);
        }
        st.setString(4, uso);
        st.setString(5, descripcion);
        st.setString(6, uMedida);
        st.setBlob(7, qr);
        if(imagenSel==true){
            st.setBlob(8, imagen);
        }else{
            st.setNull(8, java.sql.Types.BLOB);
        }
        st.setInt(9, idInventario);
        st.setInt(10, idZona);
        st.execute();
    }
     
    public static void eliminarProducto(String claveProducto) throws SQLException{
        stmt.executeUpdate("DELETE FROM producto WHERE idProductos='"+claveProducto+"'");
    }
    
    public static boolean existeProducto(String claveProducto) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) FROM productos WHERE idProductos=upper('"+claveProducto+"')");
        rs.next();
        int num = rs.getInt("count(*)");
        if(num>0){
            return true;
        }else{
            return false;
        }
    }
    
    public static void insertarUbicaciones(DefaultListModel<String> listaDeUbicaciones, String idProducto) throws SQLException{
        stmt = con.createStatement();
        if(listaDeUbicaciones.getSize()>0){
            for(int i=0;i<listaDeUbicaciones.getSize();i++){
                String ubicacion = (String)listaDeUbicaciones.getElementAt(i);
                stmt.executeUpdate("INSERT INTO ubicacion values(null,'"+ubicacion+"','"+idProducto+"')");
            }
        }
    }
    
    public static void insertarUbicacion(String ubicacion, String claveProducto) throws SQLException{
        stmt = con.createStatement(); 
        stmt.executeUpdate("INSERT INTO ubicacion values(null,'"+ubicacion+"','"+claveProducto+"')");
    }
    
    public static void eliminarUbicacion(String delUbicacion, String claveProducto) throws SQLException{
        stmt = con.createStatement();
        stmt.executeUpdate("DELETE FROM ubicacion WHERE ubicacion='"+delUbicacion+"' AND idProducto='"+claveProducto+"'");
    }
    
    public static void insertarProductosProveedores(DefaultListModel<String> listaDeProveedores, String idProducto) throws SQLException{
        stmt = con.createStatement();
        if(listaDeProveedores.getSize()>0){
            for(int i=0;i<listaDeProveedores.getSize();i++){
                String razon = (String)listaDeProveedores.getElementAt(i);
                rs = stmt.executeQuery("SELECT idProveedor FROM proveedor WHERE CONCAT_WS(' - ', razon, maquinaria, representante) = '"+razon+"'");
                rs.next();
                String idProveedor = rs.getString(1);
                stmt.executeUpdate("INSERT INTO proveedorproducto values('"+idProveedor+"','"+idProducto+"')");
            }
        }
    }
    
    public static void insertarProductoProveedor(String aux) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT idProveedor FROM (proveedor LEFT JOIN maquinaria ON proveedor.idMaquinaria = maquinaria.idMaquinaria) WHERE CONCAT_WS(' - ', razon, nombre, representante) = '"+aux+"'");
        rs.next();
        int proveedor = rs.getInt(1);
        stmt.executeUpdate("INSERT INTO proveedorproducto VALUES("+proveedor+",'"+claveProducto+"')");
    }
    
    public static void eliminarProductoProveedor(String aux, String claveProducto) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT idProveedor FROM (proveedor LEFT JOIN maquinaria ON proveedor.idMaquinaria = maquinaria.idMaquinaria) WHERE CONCAT_WS(' - ', razon, nombre, representante) = '"+aux+"'");
        rs.next();
        int proveedor = rs.getInt(1);
        stmt.executeUpdate("DELETE FROM proveedorproducto WHERE idProveedor="+proveedor+" AND idProducto='"+claveProducto+"'");
    }
    
    public static int consultaIdMaquinaria(String nombreMaquinaria){
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT idMaquinaria FROM maquinaria WHERE nombre = upper('"+nombreMaquinaria+"')");
            rs.next();
            int re = rs.getInt(1);
            return re;
        } catch (SQLException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public static Object[][] obtenerMaquinariaMatriz() throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) FROM maquinaria");
        rs.next();
        Object[][] maquinas = new Object[rs.getInt(1)][2];
        rs = stmt.executeQuery("SELECT nombre FROM maquinaria");
        int i=0;
        while(rs.next()){
            maquinas[i][0]=rs.getString("nombre");
            JButton del = new JButton("Eliminar");
            del.setBackground(new Color(200,30,30));
            del.setFont(new Font("Open Sans", Font.BOLD,12));
            del.setForeground(new Color(255,255,255));
            del.setContentAreaFilled(false);
            del.setOpaque(true);
            maquinas[i][1] = del;
            i+=1;
        }
        return maquinas;
    }
    
    public static String[][] obtenerMaquinariaLike(String nombreMaquinaria) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) FROM maquinaria WHERE nombre LIKE '%"+nombreMaquinaria+"%'");
        rs.next();
        String[][] maquinas = new String[1][rs.getInt(1)];
        rs = stmt.executeQuery("SELECT nombre FROM maquinaria WHERE nombre LIKE '%"+nombreMaquinaria+"%'");
        int i = 0;
        while(rs.next()){
            maquinas[0][i]=rs.getString(1);
        }
        return maquinas;
    }
    
    public static Object[][] obtenerMaquinariaLikeMatriz(String nombreMaquinaria) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) FROM maquinaria WHERE nombre LIKE '%"+nombreMaquinaria+"%'");
        rs.next();
        Object[][] maquinas = new Object[rs.getInt(1)][2];
        rs = stmt.executeQuery("SELECT nombre FROM maquinaria WHERE nombre LIKE '%"+nombreMaquinaria+"%'");
        int i = 0;
        while(rs.next()){
            maquinas[i][0]=rs.getString(1);
            JButton del = new JButton("Eliminar");
            del.setBackground(new Color(200,30,30));
            del.setFont(new Font("Open Sans", Font.BOLD,12));
            del.setForeground(new Color(255,255,255));
            del.setContentAreaFilled(false);
            del.setOpaque(true);
            maquinas[i][1] = del;
        }
        return maquinas;
    }
    
    public static boolean verificaMaquinaria(String nombreMaquinaria) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) FROM maquinaria WHERE nombre = upper('"+nombreMaquinaria+"')");
        rs.next();
        int re = rs.getInt(1);
        if(re>0){
            return true;
        }else{
            return false;
        }
    }
    
    public static void insertaMaquinaria(String nombreMaquinaria) throws SQLException{
        stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO maquinaria VALUES(null,'"+nombreMaquinaria+"'");
    }
    
    public static void eliminaMaquinaria(String nombreMaquinaria) throws SQLException{
        stmt = con.createStatement();
        stmt.executeUpdate("DELETE FROM maquinaria WHERE nombre='"+nombreMaquinaria+"'");
    }
    
    public static void obtenerProducto(String idProducto) throws SQLException, IOException{
        if(!ubicaciones.isEmpty()){
            ubicaciones.clear();
        }if(!ubicaciones.isEmpty()){
            proveedores.clear();
        }
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM productos WHERE idProductos=upper('"+idProducto+"')");
        rs.next();
        claveProducto = rs.getString("idProductos");
        nombreProducto = rs.getString("nombre");
        String auxIdMaq = rs.getString("idMaquinaria");
        int idMaquinaria = 0;
        if(auxIdMaq!=null){
            idMaquinaria = Integer.parseInt(auxIdMaq);
        }
        usoProducto = rs.getString("uso");
        descripcionProducto = rs.getString("descripcion");
        uMedidaProducto = rs.getString("uMedida");
        qrProducto = herramientas.Blob2BufferedImage(rs.getBlob("QR"));
        Blob aux = rs.getBlob("imagen");
        if(aux != null){
            imagenProducto = herramientas.Blob2BufferedImage(aux);
        }else{
            imagenProducto = null;
        }
        idInventario = rs.getInt("idInventario");
        idZona = rs.getInt("idZona");
        if(idMaquinaria!=0){
            rs = stmt.executeQuery("SELECT nombre FROM maquinaria WHERE idMaquinaria="+idMaquinaria);
            rs.next();
            maquinariaProducto = rs.getString("nombre");
        }else{
            maquinariaProducto = "Seleccionar";
        }
        rs = stmt.executeQuery("SELECT nombre FROM zona WHERE idZona="+idZona);
        rs.next();
        zona = rs.getString("nombre");
        rs = stmt.executeQuery("SELECT ubicacion FROM ubicacion WHERE idProducto= upper('"+claveProducto+"')");
        while(rs.next()){
            ubicaciones.add(rs.getString("ubicacion"));
        }
        rs = stmt.executeQuery("SELECT CONCAT_WS(' - ', razon, maquinaria, representante) FROM relacionproveedorproducto WHERE idProductos= upper('"+claveProducto+"')");
        while(rs.next()){
            proveedores.add(rs.getString(1));
        }
        rs = stmt.executeQuery("SELECT disponibles, precio, minimo FROM inventario WHERE idInventario="+idInventario);
        rs.next();
        cantidadDisponible = rs.getInt("disponibles");
        minimoProducto = rs.getInt("minimo");
        precio = rs.getFloat("precio");
    }
    
    public static String[][] obtenerInfoRegistros(String idProducto, String fechaIn, String fechaFin){
        try {
            String[][] resultados;
            int tam;
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT count(*) FROM registro WHERE fecha>='"+fechaIn+"' AND fecha<='"+fechaFin+"' AND idProducto= upper('"+idProducto+"')");
            rs.next();
            tam=rs.getInt("count(*)");
            resultados = new String[tam][6];
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT fecha, cantidad, total, detalles, noNota, idUsuario FROM registro WHERE fecha>='"+fechaIn+"' AND fecha<='"+fechaFin+"' AND idProducto= upper('"+idProducto+"')");
            int i=0;
            while(rs.next()){
               resultados[i][0]=rs.getObject("fecha")+"";
               resultados[i][1]=rs.getInt("cantidad")+"";
               resultados[i][2]=rs.getFloat("total")+"";
               resultados[i][3]=rs.getString("detalles");
               resultados[i][4]=rs.getString("noNota");
               resultados[i][5]=rs.getString("idUsuario");
               i+=1;
            }
            return resultados;  
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog (null, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public static void guardarCambiosInventario(int cambios[], String precioB, String minimosB) throws SQLException{
        String query2 = "UPDATE inventario SET";
        String finQ2 = " WHERE idInventario = "+idInventario+"";
        if(cambios[8]==1 || cambios[9]==1){
            if(cambios[8]==1){  //precio
                query2=query2+" precio = ?,";
            }
            if(cambios[9]==1){  //minimo
                query2=query2+" minimo = ?";
            }
            if(query2.endsWith(",")){
                query2=query2.substring(0, query2.length()-1);
            }
            query2 = query2+finQ2;
            PreparedStatement st = con.prepareStatement(query2);
            if(cambios[8]==1){  //precio
                st.setString(cambios[8], precioB.substring(0,precioB.length()-1));
            }
            if(cambios[9]==1){  //minimo
                st.setString(cambios[8]+cambios[9], minimosB);
            }
            st.execute();
        }
    }
    
    public static void guardarCambiosProducto(int[] cambios, String idProducto, int idInventario, String maquinaria, String claveB, String nombreB, String usoB, String descripcionB, String uMedidaB, BufferedImage imagenProductoB) throws SQLException, WriterException, IOException{
        BufferedImage qr = null;
        String query1 = "UPDATE productos SET";
        String finQ1 = " WHERE idProductos = '"+idProducto+"'";
        int idMaquinariaAux=0;
        if(maquinaria.compareToIgnoreCase("Seleccionar")!=0){
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT idMaquinaria FROM maquinaria WHERE nombre = upper('"+maquinaria+"')");
            rs.next();
            String aux = rs.getString(1);
            if(aux!=null){
                idMaquinariaAux = rs.getInt(1);
            }else{
                idMaquinariaAux = 0;    //No deberia de pasar ya que se registro una maquinaria no existente
            }
        }else{
            idMaquinariaAux = 0;    //Si esta en posicion de seleccionar
        }
        if(cambios[0]==1 || cambios[1]==1 || cambios[2]==1 || cambios[3]==1 || cambios[4]==1 || cambios[5]==1 || cambios[6]==1 || cambios[7]==1){
            if(cambios[0]==1){  //clave producto
                query1=query1+" idProductos = ?,";
            }
            if(cambios[1]==1){  //nombre
                query1=query1+" nombre = ?,";
            }
            if(cambios[2]==1){  //maquinaria
                query1=query1+" idMaquinaria = ?,";
            }
            if(cambios[3]==1){  //uso
                query1=query1+" uso = ?,";
            }
            if(cambios[4]==1){  //descripcion
                query1=query1+" descripcion = ?,";
            }
            if(cambios[5]==1){  //unidad de medida
                query1=query1+" uMedida = ?,";
            }
            if(cambios[6]==1){  //qr
                query1=query1+" QR = ?,";
            }
            if(cambios[7]==1){  //imagen
                query1=query1+" imagen = ?";
            }
            if(query1.endsWith(",")){   //En caso de que termine con , 
                query1=query1.substring(0, query1.length()-1);
            }
            query1 = query1+finQ1;
            PreparedStatement st = con.prepareStatement(query1);
            if(cambios[0]==1){  //clave producto
                st.setString(cambios[0], claveB);
            }
            if(cambios[1]==1){  //nombre
                st.setString(cambios[0]+cambios[1], nombreB);
                }
            if(cambios[2]==1){  //maquinaria
                if(idMaquinariaAux!=0){
                    st.setInt(cambios[0]+cambios[1]+cambios[2], idMaquinariaAux);
                }else{
                    st.setNull(cambios[0]+cambios[1]+cambios[2], java.sql.Types.INTEGER);
                }
            }
            if(cambios[3]==1){  //uso
                st.setString(cambios[0]+cambios[1]+cambios[2]+cambios[3], usoB);
            }
            if(cambios[4]==1){  //descripcion
                st.setString(cambios[0]+cambios[1]+cambios[2]+cambios[3]+cambios[4], descripcionB);
            }
            if(cambios[5]==1){  //uMedida
                st.setString(cambios[0]+cambios[1]+cambios[2]+cambios[3]+cambios[4]+cambios[5], uMedidaB);
            }
            if(cambios[6]==1){  //Qr
                qr = herramientas.crearQR("414c4d4143454e:"+claveB, 400, 400);                       // Passing: ​(RenderedImage im, String formatName, OutputStream output)
                InputStream is = herramientas.BufferedImage2InputStream(qr);
                st.setBlob(cambios[0]+cambios[1]+cambios[2]+cambios[3]+cambios[4]+cambios[5]+cambios[6], is);
            }
            if(cambios[7]==1){  //imagenProducto
                InputStream is = herramientas.BufferedImage2InputStream(imagenProductoB);
                st.setBlob(cambios[0]+cambios[1]+cambios[2]+cambios[3]+cambios[4]+cambios[5]+cambios[6]+cambios[7], is);
            }
            st.execute();
        }
    }
    
    public static void insertarProveedor(String razon, int idMaquinaria, String representante, String email, String telefono, String direccion) throws SQLException{
        stmt = con.createStatement();
        if(idMaquinaria==0){
            stmt.executeUpdate("INSERT INTO proveedor VALUES(null,'"+razon+"',null,'"+representante+"','"+email+"','"+telefono+"','"+direccion+"')");
        }else{
            stmt.executeUpdate("INSERT INTO proveedor VALUES(null,'"+razon+"',"+idMaquinaria+",'"+representante+"','"+email+"','"+telefono+"','"+direccion+"')");
        }
        
    }
    
    public static ArrayList<String> listarZona() throws SQLException{
        ArrayList<String> zonas = new ArrayList<String>();
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT nombre from zona");
        while(rs.next()){
            zonas.add(rs.getString("nombre"));
        }
        return zonas;
    }
    
    public static ArrayList<String> listarMaquinaria() throws SQLException{
        ArrayList<String> maquinaria = new ArrayList<String>();
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT nombre FROM maquinaria");
        int i=0;
        while(rs.next()){
            maquinaria.add(rs.getString("nombre"));
            i+=1;
        }
        return maquinaria;
    }
    
    public static String[][] listarProveedores(String matchAux, String opc, String sort) throws SQLException{
        stmt = con.createStatement();
        if(matchAux.length()>0){
            rs = stmt.executeQuery("SELECT count(*) FROM proveedor WHERE razon LIKE '%"+matchAux+"%' OR maquinaria LIKE '%"+matchAux+"%'");
        }else{
            rs = stmt.executeQuery("SELECT count(*) FROM proveedor");
        }
        rs.next();
        int num = rs.getInt("count(*)");
        String[][] res = new String[num][6];
        if(matchAux.length()>0){
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM proveedor WHERE razon LIKE '%"+matchAux+"%' OR maquinaria LIKE '%"+matchAux+"%' ORDER BY "+opc+" "+sort);
        }else{
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM proveedor ORDER BY "+opc+" "+sort);
        }
        int i=0;
        while(rs.next()){
        res[i][0] = rs.getString(2);
        res[i][1] = rs.getString(3);
        res[i][2] = rs.getString(4);
        res[i][3] = rs.getString(5);
        res[i][4] = rs.getString(6);
        res[i][5] = rs.getString(7);
        i+=1;
        }
        return res;
    }
    
    public static String[][] listarProductos(String matchAux, String opc, String sort) throws SQLException{
        if(matchAux.length()>0){
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT count(*) FROM listarproductos WHERE idProductos LIKE '%"+matchAux+"%' OR nombre LIKE '%"+matchAux+"%'");
        }else{
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT count(*) FROM listarproductos");
        }
        rs.next();
        int num = rs.getInt(1);
        String[][] res = new String[num][8];
        if(matchAux.length()>0){
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM listarproductos WHERE idProductos LIKE '%"+matchAux+"%' OR nombre LIKE '%"+matchAux+"%' ORDER BY "+opc+" "+sort);
        }else{
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM listarproductos ORDER BY "+opc+" "+sort);
        }
        int i=0;
        while(rs.next()){
            res[i][0] = rs.getString(1);
            res[i][1] = rs.getString(2);
            res[i][2] = rs.getString(3);
            res[i][3] = rs.getString(4);
            res[i][4] = rs.getString(5);
            res[i][5] = rs.getString(6);
            res[i][6] = rs.getString(7);
            res[i][7] = rs.getString(8);
            i+=1;
        }
        return res;
    }
    
    public static String[][] listarProductosInicial() throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) FROM listarProductos");
        rs.next();
        int cant = rs.getInt(1);
        String[][] datos = new String[cant][8];
        rs = stmt.executeQuery("SELECT * FROM listarProductos order by idProductos DESC");
        int i=0;
        while(rs.next()){
            datos[i][0]=rs.getString(1);
            datos[i][1]=rs.getString(2);
            datos[i][2]=rs.getString(3);
            datos[i][3]=rs.getString(4);
            datos[i][4]=rs.getString(5);
            datos[i][5]=rs.getString(6);
            datos[i][6]=rs.getString(7);
            datos[i][7]=rs.getString(8);
            i++;
        }
        return datos;
    }
    
    public static void autocommit() throws SQLException{
        con.setAutoCommit(!con.getAutoCommit());
    }
    
    public static void commit() throws SQLException{
        con.commit();
    }
    
    public static void cierraConexion() throws SQLException{
        con.close();
    }
    
    public static String[][] listarRegistroIn(String opc, String fechai, String fechaf) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) from productos inner join registro ON productos.idProductos = registro.idProducto WHERE '"+fechai+"' <= fecha <= '"+fechaf+"'");
        rs.next();
        int num = rs.getInt("count(*)");
        String[][] res = new String[num][9];
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT idProducto, nombre, fecha, cantidad, total, detalles, noNota, receptor, idUsuario from productos inner join registro ON productos.idProductos = registro.idProducto WHERE '"+fechai+"' <= fecha <= '"+fechaf+"' ORDER BY "+opc+" DESC");
        int i=0;
        while(rs.next()){
            res[i][0] = rs.getString("idProducto");
            res[i][1] = rs.getString("nombre");
            res[i][2] = rs.getObject("fecha")+"";
            res[i][3] = rs.getInt("cantidad")+"";
            res[i][4] = rs.getFloat("total")+"";
            res[i][5] = rs.getString("detalles");
            res[i][6] = rs.getString("noNota");
            res[i][7] = rs.getString("receptor");
            res[i][8] = rs.getString("idUsuario");
            i+=1;
        }
        return res;
    }

    public static String[][] listarRegistroEntradas(String opc, String fechai, String fechaf) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) from productos inner join registro ON productos.idProductos = registro.idProducto WHERE '"+fechai+"' <= fecha <= '"+fechaf+"' AND cantidad>0");
        rs.next();
        int num = rs.getInt("count(*)");
        String[][] res = new String[num][8];
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT idProducto, nombre, fecha, cantidad, total, detalles, noNota, idUsuario from productos inner join registro ON productos.idProductos = registro.idProducto WHERE '"+fechai+"' <= fecha <= '"+fechaf+"' AND cantidad>0 ORDER BY "+opc+" DESC");
        int i=0;
        while(rs.next()){
            res[i][0] = rs.getString("idProducto");
            res[i][1] = rs.getString("nombre");
            res[i][2] = rs.getObject("fecha")+"";
            res[i][3] = rs.getInt("cantidad")+"";
            res[i][4] = rs.getFloat("total")+"";
            res[i][5] = rs.getString("detalles");
            res[i][6] = rs.getString("noNota");
            res[i][7] = rs.getString("idUsuario");
            i+=1;
        }
        return res;
    }
        
    public static String[][] listarRegistroSalidas(String opc, String fechai, String fechaf) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) from productos inner join registro ON productos.idProductos = registro.idProducto WHERE '"+fechai+"' <= fecha <= '"+fechaf+"' AND cantidad<0");
        rs.next();
        int num = rs.getInt("count(*)");
        String[][] res = new String[num][9];
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT idProducto, nombre, fecha, cantidad, total, detalles, noNota, receptor, idUsuario from productos inner join registro ON productos.idProductos = registro.idProducto WHERE '"+fechai+"' <= fecha <= '"+fechaf+"' AND cantidad<0 ORDER BY "+opc+" DESC");
        int i=0;
        while(rs.next()){
            res[i][0] = rs.getString("idProducto");
            res[i][1] = rs.getString("nombre");
            res[i][2] = rs.getObject("fecha")+"";
            res[i][3] = rs.getInt("cantidad")+"";
            res[i][4] = rs.getFloat("total")+"";
            res[i][5] = rs.getString("detalles");
            res[i][6] = rs.getString("noNota");
            res[i][7] = rs.getString("receptor");
            res[i][8] = rs.getString("idUsuario");
            i+=1;
        }
        return res;
    }

    
    public static ArrayList<String> listarProveedores() throws SQLException{
        ArrayList<String> proveedores = new ArrayList<String>();
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT CONCAT_WS(' - ', razon, maquinaria, representante)  as razon from proveedoresmaquinaria");
        while(rs.next()){
            proveedores.add(rs.getString("razon"));
        }
        return proveedores;
    }
    
    public static boolean verificaUsuario(String user) throws SQLException{
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) from usuario where idUsuario = '"+user+"'");
        rs.next();
        if(rs.getInt(1)>0){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static void creaUsuario(int tipoUsuario, String usuario, String contrasena, String apellido, String nombre) throws SQLException{
        stmt = con.createStatement();
        if(tipoUsuario==0){
            con.setAutoCommit(false);
            stmt.executeUpdate("CREATE USER "+usuario+"@'192.168.%.%' IDENTIFIED BY '"+contrasena+"'");
            stmt.executeUpdate("GRANT DELETE, INSERT, SELECT, UPDATE, GRANT OPTION ON sci.* TO "+usuario+"@'192.168.%.%'");
            stmt.executeUpdate("GRANT GRANT OPTION, CREATE USER, RELOAD, USAGE ON *.* TO "+usuario+"@'192.168.%.%' WITH MAX_QUERIES_PER_HOUR 99999 MAX_UPDATES_PER_HOUR 99999 MAX_CONNECTIONS_PER_HOUR 9999 MAX_USER_CONNECTIONS 1");
            stmt.executeUpdate("INSERT INTO usuario VALUES('"+usuario+"', '"+nombre+"', '"+apellido+"',default)");
            stmt.execute("FLUSH PRIVILEGES");
            con.commit();
            con.setAutoCommit(true);
            
        }else{
            con.setAutoCommit(false);
            stmt.executeUpdate("CREATE USER "+usuario+"@'192.168.%.%' IDENTIFIED BY '"+contrasena+"'");
            stmt.executeUpdate("GRANT SELECT ON sci.* TO "+usuario+"@'192.168.%.%'");
            stmt.executeUpdate("GRANT INSERT ON sci.inventario TO "+usuario+"@'192.168.%.%'");
            stmt.executeUpdate("GRANT INSERT ON sci.productos TO "+usuario+"@'192.168.%.%'");
            stmt.executeUpdate("GRANT INSERT ON sci.proveedorproducto TO "+usuario+"@'192.168.%.%'");
            stmt.executeUpdate("GRANT INSERT ON sci.registro TO "+usuario+"@'192.168.%.%'");
            stmt.executeUpdate("GRANT INSERT ON sci.ubicacion TO "+usuario+"@'192.168.%.%'");
            stmt.executeUpdate("GRANT USAGE ON *.* TO "+usuario+"@'192.168.%.%' WITH MAX_QUERIES_PER_HOUR 99999 MAX_UPDATES_PER_HOUR 99999 MAX_CONNECTIONS_PER_HOUR 9999 MAX_USER_CONNECTIONS 1");
            stmt.executeUpdate("INSERT INTO usuario VALUES('"+usuario+"', '"+nombre+"', '"+apellido+"',default)");
            stmt.execute("FLUSH PRIVILEGES");
            con.commit();
            con.setAutoCommit(true);
        }
    }
    
    public static void eliminaUsuario(String usuario) throws SQLException{
        stmt = con.createStatement();
        con.setAutoCommit(false);
        stmt.executeUpdate("DROP USER '"+usuario+"'@'192.168.%.%'");
        stmt.executeUpdate("UPDATE usuario SET disable = 1 WHERE idUsuario = '"+usuario+"'");
        con.commit();
        con.setAutoCommit(true);
    }
    
    public static void cambiaContrasena(String password, String usuario) throws SQLException{
        stmt = con.createStatement();
        stmt.executeUpdate("ALTER USER '"+usuario+"'@'192.168.%.%' IDENTIFIED BY '"+password+"'");
    }
    
    
    public static String[][] obtenerMinimos(String order, String opc) throws SQLException{
        String[][] re;
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT count(*) FROM minimoproductos WHERE minimo>disponibles");
        rs.next();
        re = new String[rs.getInt(1)][4];
        rs = stmt.executeQuery("SELECT * FROM minimoproductos WHERE minimo>disponibles ORDER BY "+order+" "+opc);
        int i=0;
        while(rs.next()){
            re[i][0]=rs.getString(1);
            re[i][1]=rs.getString(2);
            re[i][2]=rs.getInt(3)+"";
            re[i][3]=rs.getInt(4)+"";
            i+=1;
        }
        return re;
    }
    
    public static Connection getCon() {
        return con;
    }

    public static void setCon(Connection con) {
        baseDeDatos.con = con;
    }

    public static String getNombreCuenta() {
        return nombreCuenta;
    }

    public static void setNombreCuenta(String nombreCuenta) {
        baseDeDatos.nombreCuenta = nombreCuenta;
    }

    public static String getClaveProducto() {
        return claveProducto;
    }

    public static String getNombreProducto() {
        return nombreProducto;
    }

    public static String getMaquinariaProducto() {
        return maquinariaProducto;
    }

    public static String getUsoProducto() {
        return usoProducto;
    }

    public static String getDescripcionProducto() {
        return descripcionProducto;
    }

    public static String getuMedidaProducto() {
        return uMedidaProducto;
    }

    public static BufferedImage getQrProducto() {
        return qrProducto;
    }

    public static BufferedImage getImagenProducto() {
        return imagenProducto;
    }

    public static int getIdInventario() {
        return idInventario;
    }

    public static int getIdZona() {
        return idZona;
    }

    public static String getZona() {
        return zona;
    }

    public static ArrayList<String> getUbicaciones() {
        return ubicaciones;
    }

    public static ArrayList<String> getProveedores() {
        return proveedores;
    }

    public static int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public static int getMinimoProducto() {
        return minimoProducto;
    }

    public static float getPrecio() {
        return precio;
    }

    public static ArrayList<String> getMaquinariaNombres() {
        return maquinariaNombres;
    }

    
    
}
