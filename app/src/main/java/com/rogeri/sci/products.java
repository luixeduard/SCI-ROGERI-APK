package com.rogeri.sci;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class products extends AppCompatActivity{

    private static Connection con;
    public static String idProducto;
    public static String user;
    public static String password;
    public static String nombre;
    public static String descripcion;
    public static Bitmap image;
    public static int idInventario;
    public static int disponibles=0;
    public static float precio = 0;
    public static int minimo = 0;
    public static boolean agregarCan = true;
    public static int contador = 0;
    public static String detalles;
    public static String fecha;
    public static String noNota;
    public static String receptor;
    public static float total;
    private static TaskBuscar t1;
    private static String url;
    private static TaskRegistro t2;
    private static TaskCerrar t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        user = getIntent().getExtras().getString("user");
        password = getIntent().getExtras().getString("password");
        idProducto=getIntent().getExtras().getString("idProducto");
        url=getIntent().getExtras().getString("url");
        t1 = new TaskBuscar();
        t1.execute();
        EditText ETCantidad = (EditText) findViewById(R.id.valor);
        EditText ETnoNota = (EditText) findViewById(R.id.noNotaET);
        EditText ETreceptor = (EditText) findViewById(R.id.receptorET);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText ETCantidad = (EditText) findViewById(R.id.valor);
                EditText noNota = (EditText) findViewById(R.id.noNotaET);
                EditText recep = (EditText) findViewById(R.id.receptorET);
                if(!ETCantidad.getText().toString().isEmpty()){
                    if(Integer.parseInt(ETCantidad.getText().toString())>disponibles && !agregarCan){
                        ETCantidad.setText(disponibles+"");
                    }
                    else if(Integer.parseInt(ETCantidad.getText().toString())>0 && Integer.parseInt(ETCantidad.getText().toString())<disponibles && noNota.getText().toString().length()>0 && recep.getText().toString().length()>0 && !agregarCan){
                        Button BAceptar = (Button) findViewById(R.id.aceptarButton);
                        BAceptar.setBackgroundResource(R.drawable.ic_baseline_done_enable);
                        BAceptar.setEnabled(true);
                        contador=Integer.parseInt(ETCantidad.getText().toString());
                    }
                    else if(Integer.parseInt(ETCantidad.getText().toString())>0 && noNota.getText().toString().length()>0 && agregarCan){
                        Button BAceptar = (Button) findViewById(R.id.aceptarButton);
                        BAceptar.setBackgroundResource(R.drawable.ic_baseline_done_enable);
                        BAceptar.setEnabled(true);
                        contador=Integer.parseInt(ETCantidad.getText().toString());
                    }
                    else{
                        Button BAceptar = (Button) findViewById(R.id.aceptarButton);
                        BAceptar.setBackgroundResource(R.drawable.ic_baseline_done_disable);
                        BAceptar.setEnabled(false);
                    }
                }else{
                    if(!ETCantidad.isFocused()){
                        ETCantidad.setText("0");
                    }
                }
            }
        };
        ETCantidad.addTextChangedListener(watcher);
        ETnoNota.addTextChangedListener(watcher);
        ETreceptor.addTextChangedListener(watcher);
    }

    public void establecerDatos(){
        LinearLayout LL1 = (LinearLayout) findViewById(R.id.ventanaAP);
        LL1.setVisibility(View.INVISIBLE);
        LinearLayout LL2 = (LinearLayout) findViewById(R.id.ventanaDP);
        LL2.setVisibility(View.INVISIBLE);
        TextView TVCod = (TextView) findViewById(R.id.codigoProd);
        TVCod.setText("Código: "+idProducto);
        TextView TVNom = (TextView) findViewById(R.id.nombre);
        TVNom.setText(nombre);
        TextView TVDes = (TextView) findViewById(R.id.descripcion);
        TVDes.setText(descripcion);
        //intent.putExtra("imagen");
        TextView TVDis = (TextView) findViewById(R.id.disponibles);
        TVDis.setText(disponibles+"");
        TextView TVPrec = (TextView) findViewById(R.id.precio);
        TVPrec.setText(precio+"");
        if(image!=null) {
            ImageView IV = (ImageView) findViewById(R.id.imagenProd);
            Drawable d = new BitmapDrawable(getResources(), image);
            IV.setBackground(d);
        }else{
            ImageView IV = (ImageView) findViewById(R.id.imagenProd);
            IV.setBackground(getResources().getDrawable(R.drawable.ic_baseline_image_24));
        }
        reiniciarObjetos();
    }

    public void reiniciarObjetos(){
        contador=0;
        total=0;
        agregarCan=true;
        EditText EDD = (EditText) findViewById(R.id.descripcionET);
        EDD.setText("");
        EditText EDValor = (EditText) findViewById(R.id.valor);
        EDValor.setText("0");
        EditText numNota = (EditText) findViewById(R.id.noNotaET);
        numNota.setText("");
        EditText recep = (EditText) findViewById(R.id.receptorET);
        recep.setText("");
    }

    @Override
    public void onBackPressed() {
        t3 = new TaskCerrar();
        t3.execute();
        Intent resuIntent = new Intent();
        resuIntent.putExtra("Existe",1);
        setResult(99, resuIntent);
        finish();
        super.onBackPressed();
    }

    public void returnButton(View v){
        t3 = new TaskCerrar();
        t3.execute();
        Intent resuIntent = new Intent();
        resuIntent.putExtra("Existe",1);
        setResult(99, resuIntent);
        finish();
    }

    public void agregarButton(View v){
        Button b1 = (Button) findViewById(R.id.agregarButton);
        b1.setBackgroundColor(Color.parseColor("#191E6F"));
        Button b2 = (Button) findViewById(R.id.descontarButton);
        b2.setBackgroundColor(Color.parseColor("#8C8585"));
        agregarCan=true;
        EditText ETreceptor = (EditText) findViewById(R.id.receptorET);
        ETreceptor.setVisibility(View.GONE);
    }

    public void descontarButton(View v){
        Button b1 = (Button) findViewById(R.id.agregarButton);
        b1.setBackgroundColor(Color.parseColor("#8C8585"));
        Button b2 = (Button) findViewById(R.id.descontarButton);
        b2.setBackgroundColor(Color.parseColor("#191E6F"));
        agregarCan=false;
        EditText ET = (EditText) findViewById(R.id.valor);
        String valor = ET.getText().toString();
        if(valor.length()>0) {
            int val = Integer.parseInt(valor);
            if (disponibles < val) {
                contador = disponibles;
                ET.setText(disponibles + "");
            }
        }
        EditText ETreceptor = (EditText) findViewById(R.id.receptorET);
        ETreceptor.setVisibility(View.VISIBLE);
    }

    public void masButton(View v){
        EditText ETCont = (EditText) findViewById(R.id.valor);
        if(ETCont.getText().toString()!=null) {
            contador = Integer.parseInt(ETCont.getText().toString());
        }else{
            contador=0;
        }
        if(agregarCan==true) {
            contador += 1;
        }
        else{
            if(contador<disponibles){
                contador+=1;
            }
        }
        ETCont.setText(contador+"");
    }

    public void menosButton(View v){
        EditText ETCont = (EditText) findViewById(R.id.valor);
        if(ETCont.getText().toString()!=null) {
            contador = Integer.parseInt(ETCont.getText().toString());
        }else{
            contador=0;
        }
        if(contador>0) {
            contador -= 1;
        }
        ETCont.setText(contador+"");
    }

    public void aceptarButton(View v){
        LinearLayout LL;
        if(agregarCan == true){
            LL = (LinearLayout) findViewById(R.id.ventanaAP);
            EditText ET = (EditText) findViewById(R.id.valor);
            String val = ET.getText().toString();
            TextView TV = (TextView) findViewById(R.id.textViewVAP);
            TV.setText("Se agregaran\n"+val+"\nunidades");
            LL.setVisibility(View.VISIBLE);
        }
        else{
            LL = (LinearLayout) findViewById(R.id.ventanaDP);
            EditText ET = (EditText) findViewById(R.id.valor);
            String val = ET.getText().toString();
            TextView TV = (TextView) findViewById(R.id.textViewVDP);
            TV.setText("Se descontaran\n"+val+"\nunidades");
            LL.setVisibility(View.VISIBLE);
        }
    }

    public void cerrarVAP(View v){
        LinearLayout LL = (LinearLayout) findViewById(R.id.ventanaAP);
        LL.setVisibility(View.INVISIBLE);
    }

    public void aceptarButtonVAP(View v) {
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fecha = sdf.format(date);
        EditText editText = (EditText) findViewById(R.id.descripcionET);
        detalles = editText.getText().toString();
        EditText numNota = (EditText) findViewById(R.id.noNotaET);
        noNota = numNota.getText().toString();
        if (agregarCan == true) {
            contador = Math.abs(contador);
        }
        t2 = new TaskRegistro();
        t2.execute();

    }

    public void ventanaExito(){
        if(agregarCan==true) {
            LinearLayout LL = (LinearLayout) findViewById(R.id.ventanaEAP);
            LL.setVisibility(View.VISIBLE);
        }else{
            LinearLayout LL = (LinearLayout) findViewById(R.id.ventanaEDP);
            LL.setVisibility(View.VISIBLE);
        }
    }

    public void aceptarButtonVDP(View v){
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fecha = sdf.format(date);
        EditText editText = (EditText) findViewById(R.id.descripcionET);
        detalles = editText.getText().toString();
        EditText numNota = (EditText) findViewById(R.id.noNotaET);
        noNota = numNota.getText().toString();
        EditText recep = (EditText) findViewById(R.id.receptorET);
        receptor = recep.getText().toString();
        total = precio*contador;
        if(agregarCan==false){
            if(contador>0) {
                contador = contador * -1;
            }
        }
        t2 = new TaskRegistro();
        t2.execute();
    }

    public void cerrarVDP(View v){
        LinearLayout LL = (LinearLayout) findViewById(R.id.ventanaDP);
        LL.setVisibility(View.INVISIBLE);
    }

    public void aceptarErrorButton(View v){
        LinearLayout LL = (LinearLayout) findViewById(R.id.ventanaErrorR);
        LL.setVisibility(View.INVISIBLE);
    }

    public void aceptarButtonEAP(View view) {
        LinearLayout LL = (LinearLayout) findViewById(R.id.ventanaEAP);
        LL.setVisibility(View.INVISIBLE);
    }

    public void aceptarButtonEDP(View view) {
        LinearLayout LL = (LinearLayout) findViewById(R.id.ventanaEDP);
        LL.setVisibility(View.INVISIBLE);
    }

    class TaskCerrar extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class TaskBuscar extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            consultarID();
            return null;
        }

        public void consultarID(){
            int result=0;
            try {
                con = DriverManager.getConnection(url, user, password);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT count(*) FROM productos WHERE idProductos=upper('"+idProducto+"')");
                rs.next();
                result=rs.getInt("count(*)");
                if(result>0) {
                    st = con.createStatement();
                    rs = st.executeQuery("SELECT * from productos inner join inventario ON productos.idInventario = inventario.idInventario WHERE idProductos=upper('"+idProducto+"')");
                    rs.next();
                    nombre =  rs.getString("nombre");
                    descripcion = rs.getString("descripcion");
                    Blob aux = rs.getBlob("imagen");
                    if(aux!=null) {
                        byte[] byteArray = aux.getBytes(1, (int) aux.length());
                        image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    }else{
                        image = null;
                    }
                    idInventario = rs.getInt("idInventario");
                    disponibles = rs.getInt("disponibles");
                    precio =  rs.getFloat("precio");
                    minimo = rs.getInt("minimo");
                    //con.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            establecerDatos();
                        }
                    });
                }
                else{
                    Intent resuIntent = new Intent();
                    resuIntent.putExtra("Existe",0);
                    setResult(99, resuIntent);
                    finish();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    class TaskRegistro extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            registrarES();
            return null;
        }
        public void registrarES(){
            try {
                //con = DriverManager.getConnection(url, user, password);
                Statement stmt = con.createStatement();
                if(agregarCan) {
                    stmt.executeUpdate("INSERT INTO registro VALUES(null,'" + fecha + "'," + contador + ", null ,'" + detalles + "','" + noNota + "', null ,'" + user + "','" + idProducto + "')");
                }else{
                    stmt.executeUpdate("INSERT INTO registro VALUES(null,'" + fecha + "'," + contador + "," + total + ",'" + detalles + "','" + noNota  + "','" + receptor + "','" + user + "','" + idProducto + "')");
                }
                //con.close();
                actualizaDatos();
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout L = (LinearLayout) findViewById(R.id.ventanaErrorR);
                        L.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
        public void actualizaDatos(){
            int result=0;
            try {
                //con = DriverManager.getConnection(url, user, password);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT count(*) FROM productos WHERE idProductos=upper('"+idProducto+"')");
                rs.next();
                result=rs.getInt("count(*)");
                if(result>0) {
                    st = con.createStatement();
                    rs = st.executeQuery("SELECT * from productos inner join inventario ON productos.idInventario = inventario.idInventario WHERE idProductos=upper('"+idProducto+"')");
                    rs.next();
                    nombre =  rs.getString("nombre");
                    descripcion = rs.getString("descripcion");
                    idInventario = rs.getInt("idInventario");
                    disponibles = rs.getInt("disponibles");
                    precio =  rs.getFloat("precio");
                    minimo = rs.getInt("minimo");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ventanaExito();
                            establecerDatos();
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}