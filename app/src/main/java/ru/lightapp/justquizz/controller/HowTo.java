package ru.lightapp.justquizz.controller;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import ru.lightapp.justquizz.R;

/**
 * Created by eugen on 05.03.2016.
 *
 */
public class HowTo extends ActionBarActivity {

    TextView outText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to);

        outText = (TextView) this.findViewById(R.id.how_to_text_view);
        outText.setMovementMethod(new ScrollingMovementMethod());

    }

}
