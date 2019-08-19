package id.co.kamil.pertagasmonitoring;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_ID_PERUSAHAAN;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_ID_WILAYAH;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_ROLE;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_TOKEN;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_USER;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_WILAYAH;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponse;
import static id.co.kamil.pertagasmonitoring.Utils.errorResponseString;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private GridView gridView;
    private ArrayList<ModelGrid> listGrid;

    private static final String SHIPPER = "Shipper";
    private static final String OFFTAKER = "Offtaker";
    private static final String AKUN = "Akun Saya";
    private static final String AKUN_ADMIN = "Akun";
    private static final String REGRESI = "Regresi Linear";
    private static final String TRACKING = "Tracking";
    private static final String KMEANS = "K-Means";
    private static final String LOGOUT = "Logout";
    private HashMap<String, Integer> icon;
    private SessionManager session;
    private HashMap<String, String> userDetail;
    private String token;
    private ProgressDialog pDialog;
    private TextView txtWelcome;
    private String wilayah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Dashboard");
        session = new SessionManager(this);
        userDetail = session.getUserDetails();

        Log.i(TAG,userDetail.get(KEY_ID_PERUSAHAAN));
        if (!session.isLoggedIn()){
            goToLogin();
        }
        pDialog = new ProgressDialog(this);

        token = userDetail.get(KEY_TOKEN);
        wilayah = userDetail.get(KEY_WILAYAH);
        icon = new HashMap<>();
        icon.put(SHIPPER,R.drawable.ic_fire);
        icon.put(OFFTAKER,R.drawable.ic_fire);
        icon.put(AKUN,R.drawable.ic_account);
        icon.put(REGRESI,R.drawable.ic_print);
        icon.put(LOGOUT,R.drawable.ic_logout);
        icon.put(TRACKING,R.drawable.ic_tracking);


        gridView = (GridView) findViewById(R.id.gridView);
        txtWelcome = (TextView) findViewById(R.id.welcome);
        if (userDetail.get(KEY_ROLE).equals("1")) {
            txtWelcome.setText("Selamat datang di Aplikasi Pertagas Monitoring " + userDetail.get(KEY_USER));
        }else{

            txtWelcome.setText("Selamat datang di Aplikasi Pertagas Monitoring " + wilayah);
        }
//        txtWelcome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,RegresiActivity.class));
//            }
//        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listGrid.get(position).getTitle().equals(LOGOUT)){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Apakah anda yakin akan keluar dari Aplikasi ?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    logout();
                                }
                            })
                            .setNegativeButton("Tidak",null)
                            .show();
                }else if (listGrid.get(position).getTitle().equals(AKUN_ADMIN)){
                    startActivity(new Intent(MainActivity.this,AkunActivity.class));
                }else if (listGrid.get(position).getTitle().equals(AKUN)){
                    startActivity(new Intent(MainActivity.this, AkunSayaActivity.class));
                }else if (listGrid.get(position).getTitle().equals(REGRESI)){
                    startActivity(new Intent(MainActivity.this, RegresiActivity.class));
                }else if (listGrid.get(position).getTitle().equals(KMEANS)){
                    startActivity(new Intent(MainActivity.this, KMeansActivity.class));
                }else if (listGrid.get(position).getTitle().equals(SHIPPER)){
                    if (userDetail.get(KEY_ID_WILAYAH).equals("1") || userDetail.get(KEY_ROLE).equals("1")){
                        // setup the alert builder
                        loadWilayah("Shipper", "shipper","");
                    }else{
                        Intent intent = new Intent(MainActivity.this,ShipperActivity.class);
                        intent.putExtra("id_wilayah",userDetail.get(KEY_ID_WILAYAH));
                        intent.putExtra("title", "Shipper");
                        intent.putExtra("kategori", "shipper");
                        startActivity(intent);
                    }
                }else if (listGrid.get(position).getTitle().equals(OFFTAKER)){
                    final String[] menuOfftaker = new String[]{"Offtaker","Multi Offtaker"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Pilih Offtaker");

                    builder.setItems(menuOfftaker, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String kategori = "";
                            if (menuOfftaker[which].equals("Offtaker")){
                                kategori = "offtaker";
                            }else{
                                kategori = "multi";
                            }
                            if (userDetail.get(KEY_ID_WILAYAH).equals("1") || userDetail.get(KEY_ROLE).equals("1")){
                                // setup the alert builder
                                loadWilayah("Offtaker", kategori,"");
                            }else{

                                Intent intent = new Intent(MainActivity.this,ShipperActivity.class);
                                intent.putExtra("id_wilayah",userDetail.get(KEY_ID_WILAYAH));
                                intent.putExtra("title", menuOfftaker[which]);
                                intent.putExtra("kategori", kategori);
                                startActivity(intent);
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if (listGrid.get(position).getTitle().equals(TRACKING)){
                    if (userDetail.get(KEY_ID_WILAYAH).equals("1") || userDetail.get(KEY_ROLE).equals("1")){
                        // setup the alert builder
                        loadWilayah("Tracking", "all_offtaker","tracking");
                    }else{

                        Intent intent = new Intent(MainActivity.this,TrackingActivity.class);
                        intent.putExtra("id_wilayah",userDetail.get(KEY_ID_WILAYAH));
                        intent.putExtra("title", "Tracking");
                        intent.putExtra("kategori", "all_offtaker");
                        startActivity(intent);
                    }
                }
            }
        });
        setupGridView();
        displayGridView();
    }
    private void loadWilayah(final String title, final String kategori, final String tujuan) {

        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/wilayah")
                .buildUpon()
                .toString();

        pDialog.setMessage("Loading...");
        pDialog.show();
        Log.i("Shipper", "Loading...");
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                hidePdialog();
                try {
                    final boolean status = response.getBoolean("status");
                    final String message = response.getString("message");
                    if (status){

                        final JSONArray data = response.getJSONArray("data");
                        final String[] dataIdWilayah = new String[data.length()];
                        final String[] dataWilayah = new String[data.length()];
                        for (int i = 0; i < data.length(); i++){
                            final String id = data.getJSONObject(i).getString("l_id");
                            final String wilayah = data.getJSONObject(i).getString("l_lokasi");
                            dataIdWilayah[i] = id;
                            dataWilayah[i] = wilayah;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Pilih Wilayah");

                        builder.setItems(dataWilayah, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (tujuan.equals("tracking")){
                                    Intent intent = new Intent(MainActivity.this,TrackingActivity.class);
                                    intent.putExtra("id_wilayah",dataIdWilayah[which]);
                                    intent.putExtra("kategori", kategori);
                                    intent.putExtra("title", title);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(MainActivity.this,ShipperActivity.class);
                                    intent.putExtra("id_wilayah",dataIdWilayah[which]);
                                    intent.putExtra("kategori", kategori);
                                    intent.putExtra("title", title);
                                    startActivity(intent);
                                }

                            }
                        });

// create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else{
                        new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(MainActivity.this,error);
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
    private void logout() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String uri = Uri.parse("https://pmonitoring.kamil.co.id/logout")
                .buildUpon()
                .toString();

        pDialog.setMessage("Sedang keluar dari aplikasi...");
        pDialog.show();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hidePdialog();
                try {
                    final boolean status = response.getBoolean("status");
                    final String message = response.getString("message");
                    if (status){
                        goToLogin();
                    }else{
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        goToLogin();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePdialog();
                errorResponse(MainActivity.this,error);

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
    private void goToLogin() {
        session.clearData();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }

    private void setupGridView() {
        listGrid = new ArrayList<ModelGrid>();
        if (!userDetail.get(KEY_ROLE).equals("4")) {
            listGrid.add(new ModelGrid(SHIPPER, icon.get(SHIPPER)));
            listGrid.add(new ModelGrid(OFFTAKER, icon.get(OFFTAKER)));
        }
        listGrid.add(new ModelGrid(TRACKING, icon.get(TRACKING)));
        if (userDetail.get(KEY_ROLE).equals("1")) {
            listGrid.add(new ModelGrid(AKUN_ADMIN, icon.get(AKUN)));
        }else{
            listGrid.add(new ModelGrid(AKUN, icon.get(AKUN)));
        }
        if (userDetail.get(KEY_ID_WILAYAH).equals("1") || userDetail.get(KEY_ROLE).equals("1")) {

            listGrid.add(new ModelGrid(KMEANS, icon.get(REGRESI)));
            listGrid.add(new ModelGrid(REGRESI, icon.get(REGRESI)));
        }
        listGrid.add(new ModelGrid(LOGOUT, icon.get(LOGOUT)));
    }

    private void displayGridView(){
        final ModelGridAdapter gridAdapter = new ModelGridAdapter(this,R.layout.item_grid,listGrid);
        gridView.setAdapter(gridAdapter);
    }
}
