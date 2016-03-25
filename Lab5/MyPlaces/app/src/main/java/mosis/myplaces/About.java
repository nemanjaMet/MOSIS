package mosis.myplaces;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class About extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceStats) {
        super.onCreate(savedInstanceStats);
        setContentView(R.layout.about);

        Button ok = (Button)findViewById(R.id.about_ok);
        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                finish();
            }
        });
    }
}
