package id.co.kamil.pertagasmonitoring;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.Map;

import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_TOKEN;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponse;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponseString;

public class UbahUsernameActivity extends AppCompatActivity {

    private static final String TAG = "UbahUsernameActivity";
    private EditText edtUsername,edtPassword;
    private Button btnSimpan;
    private SessionManager session;
    private HashMap<String, String> userDetail;
    private String token;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_username);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ubah Username");

        session = new SessionManager(this);
        userDetail = session.getUserDetails();
        token = userDetail.get(KEY_TOKEN);
        pDialog = new ProgressDialog(this);

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtUsername.getText())){
                    edtUsername.setError("Username baru tidak boleh kosong");
                    edtUsername.requestFocus();
                }else if (TextUtils.isEmpty(edtUsername.getText())) {
                    edtPassword.setError("Password belum diisi");
                    edtPassword.requestFocus();
                }else {
                    new AlertDialog.Builder(UbahUsernameActivity.this)
                            .setMessage("Apakah anda yakin akan mengubah username berikut?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doSimpan();
                                }
                            })
                            .setNegativeButton("Tidak", null)
                            .show();
                }
            }
        });
    }

    private void doSimpan() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final HashMap<String,String> param = new HashMap<>();
        final JSONObject parameter = new JSONObject(param);
        param.put("username",edtUsername.getText().toString());
        param.put("password",edtPassword.getText().toString());
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/akun/ubahusername")
                .buildUpon()
                .toString();

        pDialog.setMessage("Loading...");
        pDialog.show();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uri, parameter, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                hidePdialog();
                try {
                    final boolean status = response.getBoolean("status");
                    final String message = response.getString("message");
                    if (status){
                        session.updateUsername(edtUsername.getText().toString());
                        new AlertDialog.Builder(UbahUsernameActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }else{
                        new AlertDialog.Builder(UbahUsernameActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UbahUsernameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(UbahUsernameActivity.this,error);
                Log.i(TAG,"response:" + errorResponseString(error));
            }
        }){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                //headers.put("Content-Type", "application/json");
                headers.put("X-API-KEY", token);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void hidePdialog() {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
