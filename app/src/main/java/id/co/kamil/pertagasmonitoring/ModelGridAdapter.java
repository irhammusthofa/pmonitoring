package id.co.kamil.pertagasmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by irhammusthofa on 31/03/18.
 */

public class ModelGridAdapter extends BaseAdapter {

    private Context context;
    private int resource;
    private List<ModelGrid> list;

    public ModelGridAdapter(Context context, int resource, List<ModelGrid> objects) {
        this.context = context;
        this.resource = resource;
        this.list = objects;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ModelGrid getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(resource,parent,false);
        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        ImageView image = (ImageView) view.findViewById(R.id.itemImage);

        final ModelGrid modelGrid = getItem(position);
        title.setText(modelGrid.getTitle());
        image.setImageResource(modelGrid.getImage());

        return view;
    }
}
