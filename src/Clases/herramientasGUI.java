/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author luix_
 */
public class herramientasGUI {
    
    Image imagenR;
    int[] cambios = {0,0,0,0,0,0,0,0,0,0};
    Color azulF = new Color(25,30,111);
    Color gris = new Color(200,200,200);
    
    
    public BufferedImage crearQR(String datos, int ancho, int altura) throws WriterException {
        BitMatrix matrix;
        Writer escritor = new QRCodeWriter();
        matrix = escritor.encode(datos, BarcodeFormat.QR_CODE, ancho, altura);
        BufferedImage imagen = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < altura; y++) {
            for(int x = 0; x < ancho; x++) {
                int grayValue = (matrix.get(x, y) ? 0 : 1) & 0xff;
                imagen.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
            }
        }
        return imagen;        
    }
    
    public static BufferedImage rotateClockwise90(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();

        BufferedImage dest = new BufferedImage(height, width, src.getType());

        Graphics2D graphics2D = dest.createGraphics();
        graphics2D.translate((height - width) / 2, (height - width) / 2);
        graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
        graphics2D.drawRenderedImage(src, null);

        return dest;
    }

    public BufferedImage LeerImagen(){
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagenes","png","jpeg","bmp","jpg","jpe","jtif","tif","tiff");
        fc.addChoosableFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        if (fc.showOpenDialog(null) == 0) {
            try {
                BufferedImage imagen = ImageIO.read(fc.getSelectedFile());
                if(imagen.getHeight()>imagen.getWidth()){
                    imagen = rotateClockwise90(imagen);
                }
                if(imagen.getWidth()!=1280 || imagen.getHeight()!=960){
                    imagen = (BufferedImage)imagen.getScaledInstance(1280, 960, Image.SCALE_DEFAULT);
                }
                this.setImagenR(imagen.getScaledInstance(310, 200, Image.SCALE_DEFAULT));
                return imagen;
            } catch (IOException ex) {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public InputStream BufferedImage2InputStream(BufferedImage imagen) throws IOException{
        ByteArrayOutputStream osqr = new ByteArrayOutputStream();
        ImageIO.write(imagen, "jpeg", osqr);
        InputStream isqr = new ByteArrayInputStream(osqr.toByteArray());
        return isqr;
    }
    
    public BufferedImage Blob2BufferedImage(Blob image) throws SQLException, IOException{
        byte[] data = image.getBytes(1, (int)image.length());
        BufferedImage buffImage = ImageIO.read(new ByteArrayInputStream(data));
        return buffImage;
    }
    
    public void soloNumeros(java.awt.event.KeyEvent evt){
        int c = evt.getKeyChar();
        if(!(c>47 && c<58)){
           evt.consume();
        }
    }
    
    public void soloFlotantes(java.awt.event.KeyEvent evt, String numero){
        int c = evt.getKeyChar();
        if(!(c>45 && c<58 && c!=47)){
            evt.consume();
        }else if(numero.contains(".") && c==46){
            evt.consume();
            }
    }
    
    private int contarChar(String a, char b){
        int cont=0;
        for(int i =0;i<a.length();i++){
            if(a.charAt(i)==b){
                cont+=1;
            }
        }
        return cont;
    }
    
    private boolean comparacionTotal(){
        int cambio = cambios[0]+cambios[1]+cambios[2]+cambios[3]+cambios[4]+cambios[5]+cambios[6]+cambios[7];
        if(cambio==0){
            return false;
        }
        else{
            return true;
        }
    }
    
    public void compararClaveProducto(String claveB, String claveProducto){
        if(claveB.compareToIgnoreCase(claveProducto)!=0){
            cambios[0]=1;
            cambios[6]=1;
        }else{
            cambios[0]=0;
            cambios[6]=0;
        }
    } 
    
    public void compararNombre(String nombreB, String nombre){
        if(nombreB.compareToIgnoreCase(nombre)!=0){
            cambios[1]=1;
        }else{
            cambios[1]=0;
        }
    }
    
    public void compararMaquinaria(String maquinariaBCB, String maquinariaProducto){
        if(maquinariaBCB.compareToIgnoreCase(maquinariaProducto)!=0){
            cambios[2]=1;
        }else{
            cambios[2]=0;
        }
    }
    
    public void compararUso(String usoB, String usoProducto){
        if(usoB.compareToIgnoreCase(usoProducto)!=0){
            cambios[3]=1;
        }else{
            cambios[3]=0;
        }
    }
    
    public void compararDescripcion(String descripcionB, String descripcion){
        if(descripcionB.compareToIgnoreCase(descripcion)!=0){
            cambios[4]=1;
        }else{
            cambios[4]=0;
        }
    }
    
    public void compararUMedida(String uMedidaProductoB, String uMedidaProducto){
       if(uMedidaProductoB.compareToIgnoreCase(uMedidaProducto)!=0){
            cambios[5]=1;
        }else{
            cambios[5]=0;
        } 
    }
    
    public void compararModImagen(boolean modImagen){
        if(modImagen==true){
            cambios[7]=1;
        }else{
            cambios[7]=0;
        }
    }
    
    public void compararPrecio(Float precioB, Float precio){
        if(precioB!=precio){
            cambios[8]=1;
        }else{
            cambios[8]=0;
        }
    }
    
    public void compararMinimo
        (int minimosB, int minimo){
        if(minimosB!=minimo){
            cambios[9]=1;
        }else{
            cambios[9]=0;
        }
    }
        
    public String generaClave(String nombre, baseDeDatos bd) throws SQLException{
        Random r = new Random();
        String clave = "";
        do{
            String numero = r.nextInt(100000000)+"";
            while(numero.length()!=8){
                numero="0"+numero;
            }
            clave = nombre.substring(0,3)+"-"+numero;
            clave = clave+"ROG";
        }while(bd.existeProducto(clave)==true);
        return clave;
    }
        
    public ArrayList<String[]> cargaCSV() throws FileNotFoundException, IOException{
        ArrayList<String[]> lineas = new ArrayList<String[]>();
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo CSV","csv");
        fc.addChoosableFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        if (fc.showOpenDialog(null) == 0) {
            BufferedReader br =new BufferedReader(new FileReader(fc.getSelectedFile()));
            String line = br.readLine();
            while(null!=line){
                if(line.compareToIgnoreCase(",,,,,,,,")!=0 || !line.startsWith(",,,,,,,,")){
                    String [] fields = line.split(",");
                    lineas.add(fields);
                    line = br.readLine();
                }
            }
            return lineas;
        }
        return null;
    }
    
    public ArrayList<String[]> verificaCSV(ArrayList<String[]> valores, baseDeDatos bd) throws SQLException{
        ArrayList<String[]> valoresFinal = new ArrayList<String[]>();
        int[] indices = new int[9];
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Clave del producto")==0){
                indices[0]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Nombre")==0){
                indices[1]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Maquinaria")==0){
                indices[2]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Uso")==0){
                indices[3]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Descripcion")==0){
                indices[4]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Unidad de medida")==0){
                indices[5]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Cantidad disponible")==0){
                indices[6]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Precio")==0){
                indices[7]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=0;i<valores.get(0).length;i++){
            if(valores.get(0)[i].compareToIgnoreCase("Cantidad minima")==0){
                indices[8]=i;
                i=valores.get(0).length;
            }
        }
        for(int i=1;i<valores.size();i++){
            if(valores.get(i)[indices[1]].compareToIgnoreCase("")!=0){
                String[] aux = new String[9];
                if(valores.get(i)[indices[0]].compareToIgnoreCase("")==0){
                    aux[0]=generaClave(valores.get(i)[indices[1]], bd);
                }else{
                    aux[0]=valores.get(i)[indices[0]];
                }
                aux[1]=valores.get(i)[indices[1]];
                if(valores.get(i).length>2)
                    aux[2]=valores.get(i)[indices[2]];
                else
                    aux[2]="";
                if(valores.get(i).length>3)
                    aux[3]=valores.get(i)[indices[3]];
                else
                    aux[3]="";
                if(valores.get(i).length>4)
                    aux[4]=valores.get(i)[indices[4]];
                else
                    aux[4]="";
                if(valores.get(i).length>5)
                    aux[5]=valores.get(i)[indices[5]];
                else
                    aux[5]="PZ";
                if(valores.get(i).length>6 && valores.get(i)[indices[6]].compareTo("")!=0){
                    aux[6]=valores.get(i)[indices[6]];
                }else{
                    aux[6]="0";
                }
                if(valores.get(i).length>7 && valores.get(i)[indices[7]].compareTo("")!=0){
                    aux[7]=valores.get(i)[indices[7]];
                }else{
                    aux[7]="0";
                }
                if(valores.get(i).length>8 && valores.get(i)[indices[8]].compareTo("")!=0){
                    aux[8]=valores.get(i)[indices[8]];
                }else{
                    aux[8]="0";
                }
                valoresFinal.add(aux);
            }
        }
        return valoresFinal;
    }
     
    public boolean verificaEntero(String numero){
        try{
            int s2n = Integer.parseInt(numero);
            return true;
        }catch(Exception e){
            return false;    
        }
    }
    
    public boolean verificaFlotante(String numero){
        try{
            float s2n = Float.parseFloat(numero);
            return true;
        }catch(Exception e){
            return false;    
        }
    }
    
    public boolean verificaUMedida(String uMedida){
        if(uMedida.compareToIgnoreCase("KG")==0||uMedida.compareToIgnoreCase("PZ")==0||uMedida.compareToIgnoreCase("m")==0){
            return true;
        }
        return false;
    }
    
    private int comparaContraseña(String p1, String p2){
        if(p1.compareToIgnoreCase(p2)==0 && p1.length()>7){//Ambas contraseñas son iguales, no estan vacias y son mayores a 8
            return 0;       //Correcto
        }else if(p1.length()!=p2.length() || (p1.length()==0 && p2.length()==0)){ //Una de las contraseñas aun no termina de ingresarse o no hay nada ingresado en los campos
            return 1;       //Esta en proceso
        }else if(p1.compareToIgnoreCase(p2)==0 && p1.length()<8){//Ambas contraseñas son iguales pero tienen longitud menor a 8
            return 2;
        }else if(p1.compareToIgnoreCase(p2)!=0){    //Ambas contraseñas son diferentes
            return 3;       
        }
        return -1;  //No se encontro el error: NO DEBERIA PASAR
    }
    
    public void contrasenasGUI(String p1, String p2, JButton boton, JLabel error, JLabel error2){
        int re = this.comparaContraseña(p1, p2);
        if(re==0){
            boton.setEnabled(true);
            boton.setBackground(azulF);
            error.setVisible(false);
            error2.setVisible(false);
        }else if(re==1){
            boton.setEnabled(false);
            boton.setBackground(gris);
            error.setVisible(false);
            error2.setVisible(false);
        }
        else if(re==2){
            boton.setEnabled(false);
            boton.setBackground(gris);
            error.setVisible(false);
            error2.setVisible(true);
        }else if(re==3){
            boton.setEnabled(false);
            boton.setBackground(gris);
            error.setVisible(true);
            error2.setVisible(false);
        }else{
            boton.setEnabled(false);
            boton.setBackground(gris);
        }
    }
    
    public boolean contrasenasGUI(String p1, String p2, JLabel error, JLabel error2){
        int re = this.comparaContraseña(p1, p2);
        if(re==0){
            error.setVisible(false);
            error2.setVisible(false);
            return true;
        }else if(re==1){
            error.setVisible(false);
            error2.setVisible(false);
            return false;
        }
        else if(re==2){
            error.setVisible(false);
            error2.setVisible(true);
            return false;
        }else if(re==3){
            error.setVisible(true);
            error2.setVisible(false);
            return false;
        }else{
            return false;
        }
    }
    
    public Image getImagenR() {
        return imagenR;
    }

    public void setImagenR(Image imagenR) {
        this.imagenR = imagenR;
    }

    public int[] getCambios() {
        return cambios;
    }
    
}
