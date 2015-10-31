package in.recursion.connectfarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

public class CustomAdapter2 extends ArrayAdapter<ParseObject> {

    public CustomAdapter2(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CustomAdapter2(Context context, int resource, List<ParseObject> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.card1, null);
        }

        ParseObject p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.title);
            TextView tt2 = (TextView) v.findViewById(R.id.ppk);
            TextView tt3 = (TextView) v.findViewById(R.id.date);

            if (tt1 != null) {
                tt1.setText(p.getString("item_name")+"");
            }

            if (tt2 != null) {
                tt2.setText("\u20B9"+p.getDouble("ppk")+" /kg");
            }

            if (tt3 != null) {
                tt3.setText(p.getDouble("amount")+" kg available");
            }
            if (tt3 != null) {
                tt3.setText(p.getDouble("amount")+" kg available");
            }
        }

        return v;
    }

}