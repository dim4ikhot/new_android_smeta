package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.sheet.SheetActivity;
import ua.com.expertsoft.android_smeta.sheet.SheetBody;

/*
 * Created by mityai on 20.05.2016.
 */
public class SheetAdapter extends BaseAdapter {

    ArrayList<SheetBody> sheetBodies;
    LayoutInflater inflater;
    Context context;
    DecimalFormat df = new DecimalFormat("#.####",new DecimalFormatSymbols(Locale.US));

    public SheetAdapter(Context context, ArrayList<SheetBody> sheet){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sheetBodies = sheet;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sheetBodies.size();
    }

    @Override
    public SheetBody getItem(int position) {
        return sheetBodies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        v = convertView != null ? convertView
                :inflater.inflate(R.layout.sheet_activity_list_item,parent,false);
        TextView name = (TextView) v.findViewById(R.id.editName);
        TextView measure = (TextView) v.findViewById(R.id.editMeasure);
        TextView count = (TextView) v.findViewById(R.id.editCount);
        TextView cost = (TextView) v.findViewById(R.id.editCost);
        TextView totalCost = (TextView) v.findViewById(R.id.editTotalCost);
        TextView cipher = (TextView) v.findViewById(R.id.editCipher);
        ImageView viewer = (ImageView)v.findViewById(R.id.imgViewConsists);
        SheetBody body = sheetBodies.get(position);

        name.setText(body.getName());
        measure.setText(body.getMeasure());
        count.setText(df.format(body.getCount()));
        cost.setText(df.format(body.getCost()));
        totalCost.setText(df.format(body.getTotalCost()));
        cipher.setText(body.getCipher());
        viewer.setTag(body);
        viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SheetActivity.viewGroup((SheetActivity) context, (SheetBody) v.getTag());
            }
        });
        v.setTag(body);
        return v;
    }
}
