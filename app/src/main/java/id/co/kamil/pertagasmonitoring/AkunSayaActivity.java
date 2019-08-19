package id.co.kamil.pertagasmonitoring;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_USER;
import static id.co.kamil.pertagasmonitoring.SessionManager.KEY_WILAYAH;

public class AkunSayaActivity extends AppCompatActivity {

    private static final int RESULT_UBAH_USERNAME = 10;
    private EditText edtWilayah;
    private EditText edtUsername;
    private SessionManager session;
    private HashMap<String, String> userDetail;
    private Button btnUbahUsername,btnUbahPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun_saya);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtWilayah = (EditText) findViewById(R.id.edtWilayah);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        btnUbahUsername = (Button) findViewById(R.id.btnUbahUsername);
        btnUbahPassword = (Button) findViewById(R.id.btnUbahPassword);

        session = new SessionManager(this);
        userDetail = session.getUserDetails();
        edtWilayah.setText(userDetail.get(KEY_WILAYAH));
        edtUsername.setText(userDetail.get(KEY_USER));

        btnUbahUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AkunSayaActivity.this,UbahUsernameActivity.class),RESULT_UBAH_USERNAME);
            }
        });
        btnUbahPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AkunSayaActivity.this,UbahPasswordActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_UBAH_USERNAME){
            if (resultCode==RESULT_OK){
                userDetail = session.getUserDetails();
                edtWilayah.setText(userDetail.get(KEY_WILAYAH));
                edtUsername.setText(userDetail.get(KEY_USER));
            }
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
