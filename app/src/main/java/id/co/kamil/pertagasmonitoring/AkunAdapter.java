package id.co.kamil.pertagasmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AkunAdapter extends ArrayAdapter<Akun> {
    int _resource;
    public AkunAdapter(Context context, int resource, List<Akun> objects) {
        super(context, resource, objects);
        this._resource = resource;
    }

    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(this._resource,parent,false);
        TextView txtUsername = view.findViewById(R.id.itemUsername);
        TextView txtWilayah = view.findViewById(R.id.itemWilayah);
        TextView txtPerusahaan = view.findViewById(R.id.itemPerusahaan);

        Akun akun = getItem(position);
        txtUsername.setText(akun.getUsername());
        txtWilayah.setText(akun.getWilayah());
        String perusahaan = akun.getPerusahaan();
        if (perusahaan.equals(null) || perusahaan.equals("null")){
            perusahaan = "Perusahaan : Semua";
        }else{
            perusahaan = "Perusahaan : " + perusahaan;
        }
        txtPerusahaan.setText(perusahaan);
        return view;
    }
}
