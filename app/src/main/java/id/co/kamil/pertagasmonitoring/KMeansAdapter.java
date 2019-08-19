package id.co.kamil.pertagasmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class KMeansAdapter extends ArrayAdapter<KMeans> {
    private int _resource;
    public KMeansAdapter(Context context, int resource,  List<KMeans> objects) {
        super(context, resource, objects);
        this._resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(this._resource,parent,false);
        TextView perusahaan = (TextView) view.findViewById(R.id.perusahaan);
        TextView cluster = (TextView) view.findViewById(R.id.cluster);
        TextView status = (TextView) view.findViewById(R.id.status);

        KMeans item = getItem(position);
        perusahaan.setText(item.getPerusahaan());
        cluster.setText(item.getCluster());
        status.setText(item.getStatus());

        return view;
    }
}
