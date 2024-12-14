package com.rogeri.sci;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private static String user;
    private static String password;
    Connection con;
    private static String idProducto="";
    String url = "jdbc:mysql://192.168.0.12:3306/sci?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&useSSL=false";
    TaskLogin t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void iniciarSesion(View v) {
        TextView ETV = (TextView)findViewById(R.id.errorPassw);
        ETV.setVisibility(View.INVISIBLE);
        EditText usuarioET = (EditText) findViewById(R.id.usuarioTF);
        EditText passwordET = (EditText) findViewById(R.id.contrasenaTF);
        user = usuarioET.getText().toString();
        password = passwordET.getText().toString();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if(!user.isEmpty() && !password.isEmpty()){
                t1 = new TaskLogin();
                t1.execute();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
                        idProducto = result.getContents().toString();
                        Intent intent = new Intent(LoginActivity.this, products.class);
                        intent.putExtra("user", user);
                        intent.putExtra("password", password);
                        intent.putExtra("idProducto", idProducto);
                        intent.putExtra("url",url);
                        startActivityForResult(intent,99);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView ETV = (TextView)findViewById(R.id.errorPassw);
                        ETV.setVisibility(View.VISIBLE);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}