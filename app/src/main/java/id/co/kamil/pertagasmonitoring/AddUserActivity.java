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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import static android.widget.Toast.LENGTH_SHORT;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_TOKEN;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponse;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponseString;

public class AddUserActivity extends AppCompatActivity {

    private static final String TAG = "AddUserActivity";
    private Boolean isEdit;
    private String kodeWilayah[];
    private String namaWilayah[];
    private ProgressDialog pDialog;
    private SessionManager session;
    private HashMap<String, String> userDetail;
    private String token;
    private Spinner spinWilayah;
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnSimpan;
    private String[] dataPerusahaan;
    private String[] kodePerusahaan;
    String URL;
    private Spinner spinPerusahaan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(this);
        userDetail = session.getUserDetails();
        token = userDetail.get(KEY_TOKEN);
        pDialog = (ProgressDialog) new ProgressDialog(this);
        spinWilayah = (Spinner) findViewById(R.id.spinWilayah);
        spinPerusahaan = (Spinner) findViewById(R.id.spinPerusahaan);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinWilayah.getSelectedItemPosition()<0){
                    Toast.makeText(AddUserActivity.this,"Wilayah belum dipilih",LENGTH_SHORT);
                }else if(TextUtils.isEmpty(edtUsername.getText())){
                    edtUsername.setError("Username belum diisi");
                    edtUsername.requestFocus();
                }else{
                    doSimpan();
                }
            }
        });
        loadWilayah();
        isEdit = (Boolean) getIntent().getBooleanExtra("edit",false);
        if (isEdit) {
            getSupportActionBar().setTitle("Edit User");
            URL = "https://pmonitoring.kamil.co.id/akun/update/" + getIntent().getIntExtra("id", 0);
            edtUsername.setText(getIntent().getStringExtra("username"));
            edtUsername.setSelection(edtUsername.getText().length());
            edtPassword.setHint("Kosongkan jika tidak ada perubahan");
        }else {
            getSupportActionBar().setTitle("Tambah User");
            URL = "https://pmonitoring.kamil.co.id/akun/insert";
        }
        spinWilayah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadPerusahaan(kodeWilayah[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void doSimpan() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String kdPerusahaan = "";
        if (kodePerusahaan.length>0){
            kdPerusahaan = kodePerusahaan[spinPerusahaan.getSelectedItemPosition()];
        }else{
            kdPerusahaan = "";
        }
        final HashMap<String,String> param = new HashMap<>();

        param.put("username",edtUsername.getText().toString());
        param.put("password",edtPassword.getText().toString());
        param.put("id_wilayah",kodeWilayah[spinWilayah.getSelectedItemPosition()]);
        param.put("id_perusahaan",kdPerusahaan);
        final JSONObject parameter = new JSONObject(param);
        final String uri = Uri.parse(URL)
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
                        new AlertDialog.Builder(AddUserActivity.this)
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
                        new AlertDialog.Builder(AddUserActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddUserActivity.this, e.getMessage(), LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(AddUserActivity.this,error);
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
    private void loadWilayah() {

        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/wilayah")
                .buildUpon()
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

                        final JSONArray data = response.getJSONArray("data");
                        kodeWilayah = new String[data.length()];
                        namaWilayah = new String[data.length()];

                        for (int i = 0;i<data.length();i++){
                            final String l_id = data.getJSONObject(i).getString("l_id");
                            final String l_lokasi = data.getJSONObject(i).getString("l_lokasi");

                            kodeWilayah[i] = l_id;
                            namaWilayah[i] = l_lokasi;
                        }
                        displayList();
                    }else{
                        new AlertDialog.Builder(AddUserActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddUserActivity.this, e.getMessage(), LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(AddUserActivity.this,error);
                Log.i(TAG,"response:" + errorResponseString(error));
            }
        }){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-API-KEY", token);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void displayList() {
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,namaWilayah);
        spinWilayah.setAdapter(adapter);
    }
    private void loadPerusahaan(String id_wilayah) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/perusahaan")
                .buildUpon()
                .appendQueryParameter("id_wilayah",id_wilayah)
                .appendQueryParameter("id_kategori", "all_offtaker")
                .toString();
        dataPerusahaan = new String[0];
        kodePerusahaan = new String[0];

        pDialog.setMessage("Loading...");
        pDialog.show();

        Log.i("Tracking Perusahaan", uri);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hidePdialog();
                try {
                    final boolean status = response.getBoolean("status");
                    final String message = response.getString("message");
                    if (status){

                        final JSONArray data = response.getJSONArray("data");
                        dataPerusahaan = new String[data.length()+1];
                        kodePerusahaan = new String[data.length()+1];
                        dataPerusahaan[0] = "Semua";
                        kodePerusahaan[0] = "";
                        for (int i = 0;i<data.length();i++){
                            final String perusahaan = data.getJSONObject(i).getString("s_perusahaan");
                            final String id = data.getJSONObject(i).getString("s_id");
                            dataPerusahaan[i+1] = perusahaan;
                            kodePerusahaan[i+1] = id;
                        }
                    }else{
                        new AlertDialog.Builder(AddUserActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                    displayPerusahaan();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(AddUserActivity.this,error);
                Log.i(TAG,"response:" + errorResponseString(error));
            }
        }){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-API-KEY", token);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
    private void displayPerusahaan() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, dataPerusahaan);
        spinPerusahaan.setAdapter(adapter);
    }
    private void hidePdialog() {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
