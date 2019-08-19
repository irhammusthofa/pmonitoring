package id.co.kamil.pertagasmonitoring;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_TOKEN;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponse;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponseString;

public class AkunActivity extends AppCompatActivity {

    private static final String TAG = "AkunActivity";
    private static final int REQ_ADD_USER = 20;
    private ListView listView;
    private List<Akun> dataAkun = new ArrayList<>();
    private ProgressDialog pDialog;
    private String token;
    private SessionManager session;
    private HashMap<String, String> userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);
        getSupportActionBar().setTitle("Akun");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(this);
        userDetail = session.getUserDetails();
        token = userDetail.get(KEY_TOKEN);

        pDialog = new ProgressDialog(this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AkunActivity.this, AddUserActivity.class);
                intent.putExtra("username", dataAkun.get(position).getUsername());
                intent.putExtra("wilayah", dataAkun.get(position).getWilayah());
                intent.putExtra("id", dataAkun.get(position).getId());
                intent.putExtra("edit", true);
                startActivityForResult(intent, REQ_ADD_USER);
            }
        });
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void loadData() {
        dataAkun.clear();

        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/akun")
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
                        Akun item = new Akun();
                        for (int i = 0;i<data.length();i++){
                            final Integer id = data.getJSONObject(i).getInt("u_id");
                            final String username = data.getJSONObject(i).getString("u_name");
                            final String wilayah = data.getJSONObject(i).getString("l_lokasi");
                            final String alamat = data.getJSONObject(i).getString("l_alamat");
                            final String perusahaan = data.getJSONObject(i).getString("s_perusahaan");

                            item = new Akun();
                            item.setId(id);
                            item.setUsername(username);
                            item.setWilayah(wilayah);
                            item.setAlamat(alamat);
                            item.setPerusahaan(perusahaan);
                            dataAkun.add(item);
                        }

                        displayList();
                    }else{
                        new AlertDialog.Builder(AkunActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AkunActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(AkunActivity.this,error);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ_ADD_USER){
            if(resultCode == RESULT_OK){
                loadData();
            }
        }
    }

    private void hidePdialog() {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    private void displayList() {
        final AkunAdapter adapter = new AkunAdapter(this,R.layout.item_akun, dataAkun);
        listView.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }else if(item.getItemId()==R.id.actionAkunSaya){
            startActivity(new Intent(AkunActivity.this,AkunSayaActivity.class));
        }else if(item.getItemId()==R.id.actionTambah){
            Intent intent = new Intent(AkunActivity.this,AddUserActivity.class);
            intent.putExtra("edit",false);
            startActivityForResult(intent,REQ_ADD_USER);
        }
        return super.onOptionsItemSelected(item);
    }
}
