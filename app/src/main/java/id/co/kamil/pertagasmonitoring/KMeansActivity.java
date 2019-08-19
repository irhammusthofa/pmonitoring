package id.co.kamil.pertagasmonitoring;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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

import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_TOKEN;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponse;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponseString;

public class KMeansActivity extends AppCompatActivity {

    private static final String TAG = "KMeansActivity";
    private ListView listView;
    private ProgressDialog pDialog;
    private SessionManager session;
    private HashMap<String, String> userDetail;
    private EditText spinTgl;
    private DatePickerDialog picker;
    private String token;
    private List<KMeans> dataKmeans = new ArrayList<>();
    private List<KMeans> tempKmeans = new ArrayList<>();
    private List<KMeans> searchKmeans = new ArrayList<>();
    private Spinner spinFilter;
    private String[] dataFilter = new String[]{"Semua","C1","C2","C3"};
    private KMeansAdapter kmeansAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmeans);
        getSupportActionBar().setTitle("K-Means");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listView);
        spinFilter = (Spinner) findViewById(R.id.spinFilter);
        pDialog = new ProgressDialog(this);
        session = new SessionManager(this);
        userDetail = session.getUserDetails();
        if (!session.isLoggedIn()){
            goToLogin();
        }
        spinTgl = (EditText) findViewById(R.id.spinTgl);
        spinTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(KMeansActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                spinTgl.setText(year + "-" + + (monthOfYear + 1) + "-" + dayOfMonth);
                                loadData();
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        spinTgl.setText(formatter.format(date));

        token = userDetail.get(KEY_TOKEN);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,dataFilter);
        spinFilter.setAdapter(arrayAdapter);

        spinFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchKmeans.clear();
                for (int i=0;i<tempKmeans.size();i++){
                    if (position==0){
                        searchKmeans.add(tempKmeans.get(i));
                    }else if (position==1) {
                        if (tempKmeans.get(i).getCluster().equals("Cluster 1")){
                            searchKmeans.add(tempKmeans.get(i));
                        }
                    }else if (position==2) {
                        if (tempKmeans.get(i).getCluster().equals("Cluster 2")){
                            searchKmeans.add(tempKmeans.get(i));
                        }
                    }else if (position==3) {
                        if (tempKmeans.get(i).getCluster().equals("Cluster 3")){
                            searchKmeans.add(tempKmeans.get(i));
                        }
                    }
                }
                dataKmeans = searchKmeans;
                if (kmeansAdapter!=null){
                    kmeansAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadData();
    }
    private void goToLogin() {
        session.clearData();
        startActivity(new Intent(KMeansActivity.this,LoginActivity.class));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadData() {
        dataKmeans.clear();
        tempKmeans.clear();
        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/cluster")
                .buildUpon()
                .appendQueryParameter("date", spinTgl.getText().toString())
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

                        final JSONObject res = response.getJSONObject("data");
                        final JSONArray data = res.getJSONArray("data");
                        KMeans item = new KMeans();
                        for (int i = 0;i<data.length();i++){
                            final String perusahaan = data.getJSONObject(i).getString("s_perusahaan");
                            final String cluster = data.getJSONObject(i).getString("s_cluster");
                            final String status_cluster = data.getJSONObject(i).getString("s_status_cluster");

                            item = new KMeans();
                            item.setPerusahaan(perusahaan);
                            item.setCluster(cluster);
                            item.setStatus(status_cluster);
                            dataKmeans.add(item);
                            tempKmeans.add(item);
                        }

                        displayList();
                    }else{
                        new AlertDialog.Builder(KMeansActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(KMeansActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(KMeansActivity.this,error);
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

    private void hidePdialog() {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    private void displayList() {
        kmeansAdapter = new KMeansAdapter(this,R.layout.item_list_cluster,dataKmeans);
        listView.setAdapter(kmeansAdapter);
    }
}
