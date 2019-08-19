package id.co.kamil.pertagasmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ShipperAdapter extends ArrayAdapter<Shipper> {
    public ShipperAdapter(Context context, List<Shipper> objects) {
        super(context, R.layout.shipper_adapter_item_list, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.shipper_adapter_item_list,parent,false);
        TextView no = (TextView) view.findViewById(R.id.no);
        TextView sumber = (TextView) view.findViewById(R.id.sumber);
        TextView normal = (TextView) view.findViewById(R.id.normal);
        TextView dp = (TextView) view.findViewById(R.id.dp);
        TextView temp = (TextView) view.findViewById(R.id.temp);
        TextView pressure = (TextView) view.findViewById(R.id.pressure);
        TextView vol_hour = (TextView) view.findViewById(R.id.vol_hour);
        TextView vol_day = (TextView) view.findViewById(R.id.vol_day);
        TextView flow_rate = (TextView) view.findViewById(R.id.flow_rate);
        TextView comment = (TextView) view.findViewById(R.id.comment);
        TextView diff = (TextView) view.findViewById(R.id.diff);

        Shipper item = getItem(position);
        no.setText(item.getNo());
        sumber.setText(item.getSumber());
        normal.setText(item.getNormal());
        dp.setText(item.getDp());
        temp.setText(item.getTemp());
        pressure.setText(item.getPressure());
        vol_hour.setText(item.getVol_last_hour());
        vol_day.setText(item.getVol_last_day());
        flow_rate.setText(item.getFlow_rate());
        comment.setText(item.getComment());
        diff.setText(item.getDiff());

        return view;
    }
}
