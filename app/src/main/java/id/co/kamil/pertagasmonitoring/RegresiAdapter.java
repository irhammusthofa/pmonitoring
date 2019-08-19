package id.co.kamil.pertagasmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static id.co.kamil.pertagasmonitoring.Utils.round;

public class RegresiAdapter extends ArrayAdapter<Regresi> {
    int _resource;
    public RegresiAdapter(Context context, int resource, List<Regresi> objects) {
        super(context, resource, objects);
        this._resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(this._resource,parent,false);
        TextView txtWilayah = (TextView) view.findViewById(R.id.namaWilayah);
        TextView rSquare =(TextView)  view.findViewById(R.id.rSquare);
        TextView bY = (TextView) view.findViewById(R.id.bY);
        TextView bY2 = (TextView) view.findViewById(R.id.bY2);
        TextView bX1 = (TextView) view.findViewById(R.id.bX1);
        TextView bX2 = (TextView) view.findViewById(R.id.bX2);
        TextView errY = (TextView) view.findViewById(R.id.errY);
        TextView errX1 = (TextView) view.findViewById(R.id.errX1);
        TextView errX2 = (TextView) view.findViewById(R.id.errX2);
        TextView txtBarrel = (TextView) view.findViewById(R.id.txtBarrel);
        Button btnDetail = (Button) view.findViewById(R.id.btnDetail);
        Button btnPrediksi = (Button) view.findViewById(R.id.btnPrediksi);

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position,0);
            }
        });
        btnPrediksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position,0);
            }
        });
        Regresi item = getItem(position);
        try {
            final JSONArray coef = new JSONArray(item.getCoefficients());
            final JSONArray stdErr = new JSONArray(item.getStdErr());
            bY.setText(String.valueOf(round(Double.parseDouble(coef.getString(0)),2)));
            bY2.setText(String.valueOf(round(Double.parseDouble(coef.getString(0)),2)));
            bX1.setText(String.valueOf(round(Double.parseDouble(coef.getString(1)),2)));
            bX2.setText(String.valueOf(round(Double.parseDouble(coef.getString(2)),2)));
            errY.setText(String.valueOf(round(Double.parseDouble(stdErr.getString(0)),2)));
            errX1.setText(String.valueOf(round(Double.parseDouble(stdErr.getString(1)),2)));
            errX2.setText(String.valueOf(round(Double.parseDouble(stdErr.getString(2)),2)));
            txtBarrel.setText("Pendapatan : " + item.getBarrel());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtWilayah.setText(item.getNama_wilayah());
        rSquare.setText("RSquare : " + String.valueOf(round(Double.parseDouble(item.getRSquare()),2)));

        return view;
    }
}
