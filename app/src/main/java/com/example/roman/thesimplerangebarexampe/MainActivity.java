package com.example.roman.simplerangebar;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.roman.thesimplerangebar.SimpleRangeBar;
import com.example.roman.thesimplerangebar.SimpleRangeBarOnChangeListener;
import com.example.roman.thesimplerangebarexampe.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SimpleRangeBar simpleRangeBar;
    private TextView minText, maxText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleRangeBar = (SimpleRangeBar) findViewById(R.id.range_bar);
        minText = (TextView) findViewById(R.id.text_min);
        maxText = (TextView) findViewById(R.id.text_max);
        button = (Button) findViewById(R.id.button);

        simpleRangeBar.setOnSimpleRangeBarChangeListener(new SimpleRangeBarOnChangeListener() {
            @Override
            public void leftThumbValueChanged(final long min) {
                minText.setText(""+min);
                //Log.d("VALS","left: "+simpleRangeBar.getLeftThumbValue()+", right: "+simpleRangeBar.getRightThumbValue());
            }

            @Override
            public void rightThumbValueChanged(final long max) {
                maxText.setText(""+max);
                //Log.d("VALS","left: "+simpleRangeBar.getLeftThumbValue()+", right: "+simpleRangeBar.getRightThumbValue());
            }
        });

        // careful with the set methods, as the left thumb cannot be set higher than the right thumb
        simpleRangeBar.setThumbValues(20, 80);

        Log.d("MINMAX","min: "+simpleRangeBar.getMinRange()+", max: "+simpleRangeBar.getMaxRange());
        Log.d("VALS","left: "+simpleRangeBar.getLeftThumbValue()+", right: "+simpleRangeBar.getRightThumbValue());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);

                int min = 0;
                int max = 100;

                // also possible in xml
                simpleRangeBar.setRanges(min, max);

                simpleRangeBar.setThumbValues(min, max);

                Random r = new Random();
                int result = r.nextInt(max-min) + min;
                int result2 = r.nextInt(max-min) + min;

                //Log.d("RAND","random number for left: "+result);
                //Log.d("RAND","random number for right: "+result2);

                long time = SystemClock.currentThreadTimeMillis();
                simpleRangeBar.setThumbValues(result, result2);
                time = SystemClock.currentThreadTimeMillis()-time;
                Log.d("COMPUTETIME","for setting thumbs: "+time+" ms");

                Log.d("VALS","left: "+simpleRangeBar.getLeftThumbValue()+", right: "+simpleRangeBar.getRightThumbValue());

                button.setEnabled(true);
            }
        });

    }


}
