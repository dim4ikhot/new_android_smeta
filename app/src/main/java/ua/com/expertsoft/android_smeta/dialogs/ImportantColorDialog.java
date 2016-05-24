package ua.com.expertsoft.android_smeta.dialogs;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.com.expertsoft.android_smeta.R;

public class ImportantColorDialog extends DialogFragment {

    public interface OnGetImpotrantColor {
        void onGetColor(int colorItem);
    }

    SimpleAdapter adp;
    ArrayList<Map<String, Object>> listAdp;

    String[] titles = new String[4];
    int[] colors = new int[4];

    String[] from = {"color","text"};
    int[] to = {R.id.imgNon, R.id.textNon};

    public ImportantColorDialog() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        titles[0] = getActivity().getResources().getString(R.string.importantNoText);
        titles[1] = getActivity().getResources().getString(R.string.importantTitleText);
        titles[2] = getActivity().getResources().getString(R.string.importantMediumText);
        titles[3] = getActivity().getResources().getString(R.string.importantVeryText);

        colors[0] = R.color.colorNoImpotent;
        colors[1] = R.color.colorLittleImpotent;
        colors[2] = R.color.colorMediumImpotent;
        colors[3] = R.color.colorVeryImpotent;

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_importent_color_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.fragmentColorTitle);
        //builder.setView(v);
        Map<String,Object> m;
        listAdp = new ArrayList<>();
        for(int i = 0; i < titles.length; i++){
            m = new HashMap<>();
            m.put("color", colors[i]);
            m.put("text", titles[i]);
            listAdp.add(m);
        }
        adp = new SimpleAdapter(getActivity(),listAdp, R.layout.fragment_importent_color_dialog,from, to);
        builder.setSingleChoiceItems(adp, -1, listener);
        // Inflate the layout for this fragment
        return builder.create();
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            OnGetImpotrantColor impColor = (OnGetImpotrantColor)getActivity();
            impColor.onGetColor(which);
            dialog.dismiss();
        }
    };
}
