package id.co.kamil.pertagasmonitoring;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_ID_PERUSAHAAN;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponse;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponseString;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button btnLogin;
    private ProgressDialog pDialog;
    private EditText edtUsername,edtPassword;
    private SessionManager session;
    private HashMap<String, String> userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        session = new SessionManager(this);
        userDetail = session.getUserDetails();
        if (session.isLoggedIn()){
            goToMain();
        }

        pDialog = new ProgressDialog(this);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();

            }
        });
    }
    private void login() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/authentication/login")
                .buildUpon()
                .appendQueryParameter("username",edtUsername.getText().toString())
                .appendQueryParameter("password",edtPassword.getText().toString())
                .toString();

        pDialog.setMessage("Loading...");
        pDialog.show();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hidePdialog();
                try {
                    final boolean status = response.getBoolean("status");
                    final String message = response.getString("message");
                    if (status){
                        final String token = response.getString("token");
                        final String wilayah = response.getString("wilayah");
                        final String id_wilayah = response.getString("id_wilayah");
                        final String id_perusahaan = response.getString("id_perusahaan");
                        final String role = response.getString("role");
                        session.createLoginSession(edtUsername.getText().toString(),token,wilayah,id_wilayah, role,id_perusahaan);
                        goToMain();
                    }else{
                        new AlertDialog.Builder(LoginActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(LoginActivity.this,error);
                Log.i(TAG,errorResponseString(error));

            }
        });
        queue.add(jsonObjectRequest);
    }
    private void goToMain(){
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }
    private void hidePdialog() {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}
