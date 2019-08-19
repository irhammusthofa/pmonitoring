package id.co.kamil.pertagasmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class TrackingAdapter extends ArrayAdapter<Tracking> {
    public TrackingAdapter(Context context, List<Tracking> objects) {
        super(context, R.layout.tracking_adapter_item_list, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.tracking_adapter_item_list,parent,false);
        TextView tgl = (TextView) view.findViewById(R.id.itemTgl);
        TextView volterakhir = (TextView) view.findViewById(R.id.itemVolTerakhir);
        TextView statusterakhir = (TextView) view.findViewById(R.id.itemStatusTerakhir);

        Tracking item = getItem(position);
        tgl.setText(item.getTgl());
        volterakhir.setText(item.getVol());
        statusterakhir.setText(item.getStatus());

        return view;
    }
}
