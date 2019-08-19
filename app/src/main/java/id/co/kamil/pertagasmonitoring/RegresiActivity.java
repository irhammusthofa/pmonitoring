package id.co.kamil.pertagasmonitoring;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static id.co.kamil.pertagasmonitoring.Utils.convertTerbilang;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponse;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponseString;

public class RegresiActivity extends AppCompatActivity {


    private static final String TAG = "RegresiActivity";
    private List<Regresi> dataRegresi = new ArrayList<>();
    private ProgressDialog pDialog;
    private ListView listView;
    private EditText edtTgl;
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regresi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pDialog = new ProgressDialog(this);
        listView = (ListView) findViewById(R.id.listRegresi);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long viewId = view.getId();

                if (viewId == R.id.btnDetail) {
                    String week = "";
                    try {
                        JSONArray arr = new JSONArray(dataRegresi.get(position).getWeek());
                        for (int i = 0; i<arr.length();i++){
                            week += convertTerbilang(i+1) + " : " + arr.get(i).toString()+ "\n";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String tmp = "Pendapatan per Bulan : " + dataRegresi.get(position).getBarrel() + " MSCF ($" + (Double.parseDouble(dataRegresi.get(position).getBarrel()) * Double.parseDouble(dataRegresi.get(position).getDollar()))  +") " + " \n\nPendapatan Per Minggu : \n" + week;
                    new AlertDialog.Builder(RegresiActivity.this)
                            .setMessage(tmp)
                            .setPositiveButton("OK",null)
                            .show();
                }else if(viewId == R.id.btnPrediksi){
                    String week = "";
                    Double bln = 0.0;
                    try {
                        JSONArray predict = new JSONArray(dataRegresi.get(position).getPredict());
                        for (int i = 0; i<predict.length();i++){
                            week += convertTerbilang(i+1) + " : " + predict.get(i).toString()+ "\n";
                            bln += Double.parseDouble(predict.get(i).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String tmp = "Prediksi Bulan depan:\n\nPendapatan per Bulan : " + bln + " MSCF ($" + (bln* Double.parseDouble(dataRegresi.get(position).getDollar()))  +") " + " \n\nPendapatan Per Minggu : \n" + week;
                    new AlertDialog.Builder(RegresiActivity.this)
                            .setMessage(tmp)
                            .setPositiveButton("OK",null)
                            .show();
                }
            }
        });
        edtTgl = (EditText) findViewById(R.id.edtTgl);
        edtTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RegresiActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edtTgl.setText(year + "-" + + (monthOfYear + 1));
                                loadData();
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        final Calendar cldr = Calendar.getInstance();
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        edtTgl.setText(year + "-" + + (month + 1));
        loadData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        dataRegresi.clear();

        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String,String> param = new HashMap<>();
        param.put("bulan",edtTgl.getText().toString());
        final JSONObject parameter = new JSONObject(param);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/regresi")
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
                    if (status){

                        final JSONArray data = response.getJSONArray("result");
                        Log.i(TAG,"data:"+ data);
                        Regresi item = new Regresi();
                        for (int i = 0;i<data.length();i++){
                            final String id_wilayah = data.getJSONObject(i).getString("id_wilayah");
                            final String nama_wilayah = data.getJSONObject(i).getString("nama_wilayah");
                            final JSONArray coef = data.getJSONObject(i).getJSONArray("Coefficients");
                            final JSONArray stdErr = data.getJSONObject(i).getJSONArray("StdErr");
                            final JSONArray coefP = data.getJSONObject(i).getJSONArray("Coef P");
                            final Double rSquare = data.getJSONObject(i).getDouble("RSquare");
                            final Double SSE = data.getJSONObject(i).getDouble("SSE");
                            final Double SSR = data.getJSONObject(i).getDouble("SSR");
                            final Double SSTO = data.getJSONObject(i).getDouble("SSTO");
                            final Double F = data.getJSONObject(i).getDouble("F");
                            final Double barrel = data.getJSONObject(i).getDouble("barrel");
                            final Double dollar = data.getJSONObject(i).getDouble("dollar");
                            final JSONArray TStats = data.getJSONObject(i).getJSONArray("TStats");
                            final JSONArray PValues = data.getJSONObject(i).getJSONArray("PValues");
                            final JSONArray week = data.getJSONObject(i).getJSONArray("pendapatan-minggu");
                            final JSONArray predict = data.getJSONObject(i).getJSONArray("prediksi");

                            item = new Regresi();
                            item.setId_wilayah(id_wilayah);
                            item.setNama_wilayah(String.valueOf(i+1) + ". " +nama_wilayah);
                            item.setCoefficients(coef.toString());
                            item.setStdErr(stdErr.toString());
                            item.setCoefP(coefP.toString());
                            item.setRSquare(String.valueOf(rSquare));
                            item.setSSE(String.valueOf(SSE));
                            item.setSSR(String.valueOf(SSR));
                            item.setSSTO(String.valueOf(SSTO));
                            item.setF(String.valueOf(F));
                            item.setTStats(TStats.toString());
                            item.setPValues(PValues.toString());
                            item.setBarrel(barrel.toString());
                            item.setWeek(week.toString());
                            item.setPredict(predict.toString());
                            item.setDollar(dollar.toString());
                            //Log.i(TAG,item.toString());
                            dataRegresi.add(item);
                            //Log.i(TAG,"data:"+ item.toString());
                        }

                    }
                    displayList();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegresiActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(RegresiActivity.this,error);
                Log.i(TAG,"response:" + errorResponseString(error));
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void displayList() {
        final RegresiAdapter adapter = new RegresiAdapter(this,R.layout.item_list_regresi,dataRegresi);
        listView.setAdapter(adapter);
    }

    private void hidePdialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
