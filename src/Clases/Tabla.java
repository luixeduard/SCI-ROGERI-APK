/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author luix_
 */
public class Tabla {
    private static String[][] entradasProducto;
    private static String[][] salidasProducto;
    
    public void ver_tabla(JTable tabla, Object[][] datos){
        DefaultTableModel d = new DefaultTableModel(
                datos,
                new String [] {
                    "Nombre de usuario", "Nombre", "Apellidos", "Modificar", "Eliminar"
                }
            
        )
        {public boolean isCellEditable(int row, int column){
            return false;
        }};
        tabla.setModel(d);
        TableColumnModel columnModel = tabla.getColumnModel();//878
        columnModel.getColumn(0).setPreferredWidth(200);
        columnModel.getColumn(1).setPreferredWidth(225);
        columnModel.getColumn(2).setPreferredWidth(252);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        tabla.setRowHeight(30);
    }
    
    public void ver_tabla2(JTable tabla, Object[][] datos){
        DefaultTableModel d = new DefaultTableModel(
                datos,
                new String [] {
                    "Maquinaria", ""
                }
            
        )
        {public boolean isCellEditable(int row, int column){
            return false;
        }};
        tabla.setModel(d);
        TableColumnModel columnModel = tabla.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(300);
        tabla.setRowHeight(30);
    }
    
    public static void tablaProductos(JTable tabla, String[][] res){
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            res,
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
    }
    
    public static void tablaRegistroProducto(String[][] esProducto, JTable jtEntradasSalidas){
        int e=0;
        int s=0;
        for(int i=0;i<esProducto.length;i++){
            if(Integer.parseInt(esProducto[i][1])>0){
                e+=1;
            }
            else{
                s+=1;
            }
        }
        entradasProducto = new String[e][5];
        salidasProducto = new String[s][6];
        e=0;
        s=0;
        for(int i=0;i<esProducto.length;i++){
            if(Integer.parseInt(esProducto[i][1])>0){
                entradasProducto[e][0]=esProducto[i][0];
                entradasProducto[e][1]=esProducto[i][1];
                entradasProducto[e][2]=esProducto[i][3];
                entradasProducto[e][3]=esProducto[i][4];
                entradasProducto[e][4]=esProducto[i][5];
                e+=1;
            }
            else{
                salidasProducto[s][0]=esProducto[i][0];
                salidasProducto[s][1]=esProducto[i][1].substring(1);
                salidasProducto[s][3]=esProducto[i][2];
                salidasProducto[s][2]=esProducto[i][3];
                entradasProducto[e][4]=esProducto[i][4];
                entradasProducto[e][5]=esProducto[i][5];
                s+=1;
            }
        }
        jtEntradasSalidas.setModel(new javax.swing.table.DefaultTableModel(
            entradasProducto,
            new String [] {
                "Día", "Cantidad", "Detalles","Numero de nota","Usuario"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
    }
    
    public static void tablaEntradasProducto(JTable jtEntradasSalidas){
        jtEntradasSalidas.setModel(new javax.swing.table.DefaultTableModel(
            entradasProducto,
            new String [] {
                "Día", "Cantidad", "Detalles","Numero de nota","Usuario"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
    }
    
    public static void tablaSalidasProducto(JTable jtEntradasSalidas){
        jtEntradasSalidas.setModel(new javax.swing.table.DefaultTableModel(
            salidasProducto,
            new String [] {
                "Día", "Cantidad", "Detalles", "Total", "Numero de nota", "Usuario"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
    }
    
    public static void estableceTablaProveedores(String[][] res, JTable tablaProveedores){
        tablaProveedores.setModel(new javax.swing.table.DefaultTableModel(
            res,
            new String [] {
            "Razón social", "Maquinaria", "Representante", "Correo", "Teléfono", "Dirreción"
            }
        ) {
            boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
    }
    
    public static void estableceTablaRegistro(String[][] res, JTable tablaRegistros){
        tablaRegistros.setModel(new javax.swing.table.DefaultTableModel(
            res,
            new String [] {
                "Clave producto", "Nombre", "Fecha", "Cantidad", "Total", "Detalles", "Numero de nota", "Receptor", "Usuario"
            }
            ) {
                boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
                };
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
        });
    }
    
    public static void estableceTablaRegistroEntradas(String[][] res, JTable tablaRegistros){
        tablaRegistros.setModel(new javax.swing.table.DefaultTableModel(
            res,
            new String [] {
                "Clave producto", "Nombre", "Fecha", "Cantidad", "Total", "Detalles", "Numero de nota", "Usuario"
            }
            ) {
                boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
                };
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
        });
    }
    
    public static void estableceTablaMinimos(String[][] res, JTable jtMinimosPMin){
        jtMinimosPMin.setModel(new javax.swing.table.DefaultTableModel(
            res,
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
    }
}
