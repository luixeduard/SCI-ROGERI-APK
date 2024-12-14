/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JMenuItem;

/**
 *
 * @author luix_
 */
public class HiloNotificaciones extends Thread{

    private boolean continuar=true;
    private Connection con;
    public JMenuItem jmiNotificaciones;
    public JButton jbNotificacionesPN;
    
    public void stopThread(){
        continuar=false;
    }
    
    public void run() {
        Statement st;
        ResultSet rs;
        while(continuar){
            try {
                st = con.createStatement();
                rs = st.executeQuery("SELECT count(*) FROM minimoproductos WHERE minimo>disponibles");
                rs.next();
                int numero = rs.getInt(1);
                if(numero==0){
                    jmiNotificaciones.setText("No hay notificaciones");
                    jbNotificacionesPN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/notificacionNadaN.png")));
                }else{
                    jmiNotificaciones.setText("Advertencia: "+numero+" productos tienen un minimo de inventario");
                    jbNotificacionesPN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/notificacionMuchasN.png")));
                }
                HiloNotificaciones.sleep(300000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public void setJmiNotificaciones(JMenuItem jmiNotificaciones) {
        this.jmiNotificaciones = jmiNotificaciones;
    }

    public void setJbNotificacionesPN(JButton jbNotificacionesPN) {
        this.jbNotificacionesPN = jbNotificacionesPN;
    }
    
}
