package com.rogeri.sci;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private static String user;
    private static String password;
    Connection con;
    private static String clave ="";
    String url = "jdbc:mysql://192.168.1.10:3306/sci?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City&useSSL=false";
    TaskLogin t1;
    private static final int PERMISSION_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }
    }

    public void iniciarSesion(View v) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            TextView ETV = (TextView) findViewById(R.id.errorPassw);
            ETV.setVisibility(View.INVISIBLE);
            EditText usuarioET = (EditText) findViewById(R.id.usuarioTF);
            EditText passwordET = (EditText) findViewById(R.id.contrasenaTF);
            user = usuarioET.getText().toString();
            password = passwordET.getText().toString();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                if (!user.isEmpty() && !password.isEmpty()) {
                    t1 = new TaskLogin();
                    t1.execute();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"No existen permisos de camara",Toast.LENGTH_SHORT).show();
        }
    }

    public void escanearQR(){
        IntentIntegrator integrator = new IntentIntegrator(LoginActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setPrompt("Apunte hacia el codigo QR");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    public void errorButton(View v){
        LinearLayout L = (LinearLayout) findViewById(R.id.ErrorB);
        L.setVisibility(View.INVISIBLE);
        escanearQR();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 99:
                Intent i = new Intent();
                int returnV = data.getIntExtra("Existe",1);
                if(returnV == 1){
                    escanearQR();
                }else{
                    LinearLayout L = (LinearLayout) findViewById(R.id.ErrorB);
                    L.setVisibility(View.VISIBLE);
                }
                break;
            default:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() != null) {
                        clave = result.getContents().toString();
                        String actividad = clave.substring(0, clave.indexOf(":")+1);
                        clave = clave.substring(clave.indexOf(":")+1);
                        if(actividad.compareToIgnoreCase("414c4d4143454e:")==0) {
                            Intent intent = new Intent(LoginActivity.this, products.class);
                            intent.putExtra("user", user);
                            intent.putExtra("password", password);
                            intent.putExtra("idProducto", clave);
                            intent.putExtra("url", url);
                            startActivityForResult(intent, 99);
                        }else{
                            Toast.makeText(getApplicationContext(),"Codigo no reconocido",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Codigo no reconocido",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    class TaskLogin extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            conectar();
            return null;
        }

        public void conectar(){
            try{
                con = DriverManager.getConnection(url, user, password);
                con.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        escanearQR();
                    }
                });
            }catch (SQLException ex) {
                if(ex.getErrorCode()==1045) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView ETV = (TextView) findViewById(R.id.errorPassw);
                            ETV.setVisibility(View.VISIBLE);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error de conexión a la base de datos",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}