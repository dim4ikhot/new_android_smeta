package ua.com.expertsoft.android_smeta;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import ua.com.expertsoft.android_smeta.settings.FragmentSettings;
import ua.com.expertsoft.android_smeta.standard_project.parsers.ZmlParser;
import ua.com.expertsoft.android_smeta.static_data.SelectedResource;

public class EditResourceActivity extends AppCompatActivity {

    AppCompatButton okButton;
    EditText measure,name,cipher,count,cost,totalCost;
    boolean isRusLanguage = true;
    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_resource);
        initControls();
    }

    private void initControls(){
        okButton = (AppCompatButton)findViewById(R.id.applyChanges);
        measure = (EditText)findViewById(R.id.resourceMeasure);
        name = (EditText)findViewById(R.id.resourceName);
        cipher = (EditText)findViewById(R.id.resourceCipher);
        count = (EditText)findViewById(R.id.resourceCount);
        cost = (EditText)findViewById(R.id.resourceCost);
        totalCost = (EditText)findViewById(R.id.resourceTotalCost);
        bar = getSupportActionBar();
        fillControls();
    }

    private void fillControls(){
        if(FragmentSettings.isDataLanguageRus(this)) {
            name.setText(SelectedResource.resource.getWrNameRus());
            measure.setText(SelectedResource.resource.getWrMeasuredRus());
            isRusLanguage = true;
        }
        else{
            name.setText(SelectedResource.resource.getWrNameUkr());
            measure.setText(SelectedResource.resource.getWrMeasuredUkr());
            isRusLanguage = false;
        }
        cipher.setText(SelectedResource.resource.getWrCipher());
        count.setText(String.valueOf(SelectedResource.resource.getWrCount()));
        cost.setText(String.valueOf(SelectedResource.resource.getWrCost()));
        totalCost.setText(String.valueOf(SelectedResource.resource.getWrTotalCost()));
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
        okButton.setOnClickListener(okButtonListener);
        cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    float newCost = Float.parseFloat(s.toString());
                    float newTotalCost = ZmlParser.roundTo(SelectedResource.resource.getWrCount() * newCost, 4);
                    totalCost.setText(String.valueOf(newTotalCost));
                }
                else{
                    totalCost.setText("0");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateResourceParams(){
        SelectedResource.resource.setWrCipher(cipher.getText().toString());
        if(isRusLanguage) {
            SelectedResource.resource.setWrNameRus(name.getText().toString());
            SelectedResource.resource.setWrMeasuredRus(measure.getText().toString());
        }
        else{
            SelectedResource.resource.setWrNameUkr(name.getText().toString());
            SelectedResource.resource.setWrMeasuredUkr(measure.getText().toString());
        }
        SelectedResource.resource.setWrCount(Float.parseFloat(count.getText().toString()));
        SelectedResource.resource.setWrCost(Float.parseFloat(cost.getText().toString()));
        SelectedResource.resource.setWrTotalCost(Float.parseFloat(totalCost.getText().toString()));
    }

    View.OnClickListener okButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateResourceParams();
            setResult(RESULT_OK);
            finish();
        }
    };
}
