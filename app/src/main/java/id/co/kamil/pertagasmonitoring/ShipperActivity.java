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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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

public class ShipperActivity extends AppCompatActivity {

    private static final String TAG = "ShipperActivity";
    private ListView listView;
    private List<Shipper> dataShipper = new ArrayList<>();
    private ProgressDialog pDialog;
    private String token;
    private SessionManager session;
    private HashMap<String, String> userDetail;
    private boolean isLoaded = false;
    private Switch realtime;
    private String id_wilayah, kategori;
    private EditText spinTgl;
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.listView);
        pDialog = new ProgressDialog(this);
        session = new SessionManager(this);
        userDetail = session.getUserDetails();
        if (!session.isLoggedIn()){
            goToLogin();
        }
        realtime = (Switch) findViewById(R.id.realtime);
        spinTgl = (EditText) findViewById(R.id.spinTgl);
        spinTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ShipperActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                spinTgl.setText(year + "-" + + (monthOfYear + 1) + "-" + dayOfMonth);
                                loadData(false);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        spinTgl.setText(formatter.format(date));

        token = userDetail.get(KEY_TOKEN);
        id_wilayah = getIntent().getStringExtra("id_wilayah");
        kategori = getIntent().getStringExtra("kategori");

        Log.i(TAG,"token:" + token);
        loadData(false);
        Timer myTimer = new Timer();
        //Set the schedule function and rate
        myTimer.scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            //Called at every 1000 milliseconds (1 second)
                                            if(realtime.isChecked()){
                                                loadData(true);
                                                Log.i("Shipper", "Repeated task");
                                            }

                                        }
                                    },
                //set the amount of time in milliseconds before first execution
                0,
                //Set the amount of time between each execution (in milliseconds)
                1000);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < dataShipper.size()-1){

                    new AlertDialog.Builder(ShipperActivity.this)
                            .setTitle("Contact Person")
                            .setMessage(dataShipper.get(position).getAlamat())
                            .setPositiveButton("OK",null)
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        session.clearData();
        startActivity(new Intent(ShipperActivity.this,LoginActivity.class));
        finish();
    }

    private void loadData(final boolean reload) {
        dataShipper.clear();
        if (isLoaded==false){
            isLoaded = true;
        }else{
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/shipper")
                .buildUpon()
                .appendQueryParameter("id_wilayah",id_wilayah)
                .appendQueryParameter("id_kategori", kategori)
                .appendQueryParameter("date", spinTgl.getText().toString())
                .toString();

        pDialog.setMessage("Loading...");
        if (reload==false){
            pDialog.show();
        }
        Log.i("Shipper", "Loading...");
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                hidePdialog();
                isLoaded = false;
                try {
                    final boolean status = response.getBoolean("status");
                    final String message = response.getString("message");
                    if (status){

                        final JSONArray data = response.getJSONArray("data");
                        Double totLHour,totLDay,totFlow;
                        totLHour = 0.0;
                        totLDay = 0.0;
                        totFlow = 0.0;
                        Shipper item = new Shipper();
                        for (int i = 0;i<data.length();i++){
                            final String no = data.getJSONObject(i).getString("no");
                            final String sumber = data.getJSONObject(i).getString("s_perusahaan");
                            final String alamat = data.getJSONObject(i).getString("s_alamat");
                            final String normal = data.getJSONObject(i).getString("normal");
                            final String dp = data.getJSONObject(i).getString("dp");
                            final String temp = data.getJSONObject(i).getString("temp");
                            final String pressure = data.getJSONObject(i).getString("pressure");
                            final String vol_hour = data.getJSONObject(i).getString("vol_last_hour");
                            final String vol_day = data.getJSONObject(i).getString("vol_last_day");
                            final String flow_rate = data.getJSONObject(i).getString("flow_rate");
                            final String comment = data.getJSONObject(i).getString("comment");
                            final String diff = data.getJSONObject(i).getString("diff");

                            item = new Shipper();
                            item.setNo(String.valueOf(i+1));
                            item.setSumber(sumber);
                            item.setAlamat(alamat);
                            item.setNormal(normal);
                            item.setDp(dp);
                            item.setTemp(temp);
                            item.setPressure(pressure);
                            item.setVol_last_hour(vol_hour);
                            item.setVol_last_day(vol_day);
                            item.setFlow_rate(flow_rate);
                            item.setComment(comment);
                            item.setDiff(diff);
                            totLHour += Double.parseDouble(vol_hour);
                            totLDay += Double.parseDouble(vol_day);
                            totFlow += Double.parseDouble(flow_rate);
                            dataShipper.add(item);
                            Log.i(TAG,"data:"+ item.toString());
                        }
                        item = new Shipper();
                        item.setNo("");
                        item.setSumber("TOTAL");
                        item.setNormal("");
                        item.setDp("");
                        item.setTemp("");
                        item.setPressure("");
                        item.setVol_last_hour(String.valueOf(totLHour));
                        item.setVol_last_day(String.valueOf(totLDay));
                        item.setFlow_rate(String.valueOf(totFlow));
                        item.setComment("");
                        item.setDiff("");

                        dataShipper.add(item);
                        displayList();
                    }else{
                        if (reload==false){
                            new AlertDialog.Builder(ShipperActivity.this)
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ShipperActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoaded = false;
                hidePdialog();
                errorResponse(ShipperActivity.this,error);
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
        final ShipperAdapter adapter = new ShipperAdapter(this,dataShipper);
        listView.setAdapter(adapter);
    }
}
