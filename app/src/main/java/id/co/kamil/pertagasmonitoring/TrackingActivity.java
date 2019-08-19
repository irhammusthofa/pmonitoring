package id.co.kamil.pertagasmonitoring;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_ID_PERUSAHAAN;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_ROLE;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_TOKEN;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponse;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponseString;

public class TrackingActivity extends AppCompatActivity {

    private static final String TAG = "TrackingActivity";
    private ListView listTracking;
    private Button btnTampilkan;
    private Spinner spinPerusahaan;
    private String[] dataPerusahaan;
    private String[] kodePerusahaan;
    private ProgressDialog pDialog;
    private String token;
    private SessionManager session;
    private HashMap<String, String> userDetail;
    private List<Tracking> dataTracking = new ArrayList<>();
    private List<Tracking> dataTrackingDetail = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        getSupportActionBar().setTitle("Tracking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(this);
        userDetail = session.getUserDetails();
        token = userDetail.get(KEY_TOKEN);

        pDialog = new ProgressDialog(this);
        String wilayah = getIntent().getStringExtra("id_wilayah");


        listTracking = (ListView) findViewById(R.id.listTracking);
        btnTampilkan = (Button) findViewById(R.id.btnTampilkan);
        spinPerusahaan = (Spinner) findViewById(R.id.spinPerusahaan);
        listTracking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                detail(dataTracking.get(position).getId());
            }
        });
        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id_perusahaan = kodePerusahaan[spinPerusahaan.getSelectedItemPosition()];
                tampilkan(id_perusahaan);
            }
        });
        loadPerusahaan(wilayah);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void tampilkan(final String id_perusahaan) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/tracking")
                .buildUpon()
                .appendQueryParameter("id_perusahaan",id_perusahaan)
                .toString();

        pDialog.setMessage("Loading...");
        pDialog.show();
        Log.i("Tracking", uri);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hidePdialog();
                try {
                    final boolean status = response.getBoolean("status");
                    final String message = response.getString("message");
                    if (status){
                        final JSONArray data = response.getJSONArray("data");
                        dataTracking.clear();
                        for (int i = 0;i<data.length();i++){
                            final String perusahaan = data.getJSONObject(i).getString("s_perusahaan");
                            final String id = data.getJSONObject(i).getString("tr_id");
                            final String tgl = data.getJSONObject(i).getString("tr_tgl");
                            final String tgl2 = data.getJSONObject(i).getString("td_tgl");
                            final String status_tracking = data.getJSONObject(i).getString("td_status");
                            final String vol = data.getJSONObject(i).getString("td_vol");
                            final String alamat  = data.getJSONObject(i).getString("s_alamat");
                            final String kategori = data.getJSONObject(i).getString("s_kategori");

                            Tracking tracking = new Tracking();
                            tracking.setId(id);
                            tracking.setAlamat(alamat);
                            tracking.setId_perusahaan(id_perusahaan);
                            tracking.setKategori(kategori);
                            tracking.setPerusahaan(perusahaan);
                            if (tgl2.equals(null) || tgl2.equals("null")){
                                tracking.setTgl("Terakhir Update : " + tgl);
                            }else{
                                tracking.setTgl("Terakhir Update  : " + tgl);
                            }
                            if (vol.equals(null) || vol.equals("null")){
                                tracking.setVol("Vol : Belum ada data");
                            }else{
                                tracking.setVol("Vol : " + vol);
                            }
                            if (status_tracking.equals(null) || status_tracking.equals("null")){
                                tracking.setStatus("Status : Belum ada data");
                            }else{
                                tracking.setStatus("Status : " + status_tracking);
                            }

                            dataTracking.add(tracking);
                        }
                        displayTracking();
                    }else{
                        new AlertDialog.Builder(TrackingActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TrackingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(TrackingActivity.this,error);
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
    private void detail(final String id_tracking) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/tracking/detail")
                .buildUpon()
                .appendQueryParameter("id_tracking",id_tracking)
                .toString();

        pDialog.setMessage("Loading...");
        pDialog.show();
        Log.i("Tracking", uri);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hidePdialog();
                try {
                    final boolean status = response.getBoolean("status");
                    final String message = response.getString("message");
                    if (status){
                        final JSONArray data = response.getJSONArray("data");
                        dataTrackingDetail.clear();
                        for (int i = 0;i<data.length();i++){
                            final String perusahaan = data.getJSONObject(i).getString("s_perusahaan");
                            final String id = data.getJSONObject(i).getString("tr_id");
                            final String tgl = data.getJSONObject(i).getString("tr_tgl");
                            final String tgl2 = data.getJSONObject(i).getString("td_tgl");
                            final String status_tracking = data.getJSONObject(i).getString("td_status");
                            final String vol = data.getJSONObject(i).getString("td_vol");
                            final String alamat  = data.getJSONObject(i).getString("s_alamat");
                            final String kategori = data.getJSONObject(i).getString("s_kategori");
                            final String id_perusahaan = data.getJSONObject(i).getString("id_perusahaan");

                            Tracking tracking = new Tracking();
                            tracking.setId(id);
                            tracking.setAlamat(alamat);
                            tracking.setId_perusahaan(id_perusahaan);
                            tracking.setKategori(kategori);
                            tracking.setPerusahaan(perusahaan);
                            if (tgl2.equals(null) || tgl2.equals("null")){
                                tracking.setTgl("Terakhir Update : " + tgl);
                            }else{
                                tracking.setTgl("Terakhir Update  : " + tgl);
                            }
                            if (vol.equals(null) || vol.equals("null")){
                                tracking.setVol("Vol : Belum ada data");
                            }else{
                                tracking.setVol("Vol : " + vol);
                            }
                            if (status_tracking.equals(null) || status_tracking.equals("null")){
                                tracking.setStatus("Status : Belum ada data");
                            }else{
                                tracking.setStatus("Status : " + status_tracking);
                            }

                            dataTrackingDetail.add(tracking);
                        }
                        displayTrackingDetail();
                    }else{
                        new AlertDialog.Builder(TrackingActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TrackingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(TrackingActivity.this,error);
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

    private void displayTrackingDetail() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TrackingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_list, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Detail Tracking");
        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        final TrackingAdapter adapter = new TrackingAdapter(this,dataTrackingDetail);
        lv.setAdapter(adapter);
        alertDialog.show();
    }

    private void displayTracking() {
        final TrackingAdapter adapter = new TrackingAdapter(this,dataTracking);
        listTracking.setAdapter(adapter);
    }

    private void loadPerusahaan(String id_wilayah) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String uri = "";
        if (!userDetail.get(KEY_ROLE).equals("4")){
            uri = Uri.parse("https://pmonitoring.kamil.co.id/perusahaan")
                    .buildUpon()
                    .appendQueryParameter("id_wilayah",id_wilayah)
                    .appendQueryParameter("id_kategori", "all_offtaker")
                    .toString();
        }else{
            uri = Uri.parse("https://pmonitoring.kamil.co.id/perusahaan")
                    .buildUpon()
                    .appendQueryParameter("id_wilayah",id_wilayah)
                    .appendQueryParameter("id_perusahaan",userDetail.get(KEY_ID_PERUSAHAAN))
                    .appendQueryParameter("id_kategori", "all_offtaker")
                    .toString();
        }

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
                        dataPerusahaan = new String[data.length()];
                        kodePerusahaan = new String[data.length()];
                        for (int i = 0;i<data.length();i++){
                            final String perusahaan = data.getJSONObject(i).getString("s_perusahaan");
                            final String id = data.getJSONObject(i).getString("s_id");
                            dataPerusahaan[i] = perusahaan;
                            kodePerusahaan[i] = id;

                        }
                        displayPerusahaan();
                    }else{
                        new AlertDialog.Builder(TrackingActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TrackingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(TrackingActivity.this,error);
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
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
