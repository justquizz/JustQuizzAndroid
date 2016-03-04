package ru.lightapp.justquizz.controller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.dataexchange.DBManager;

/*
 * Created by Eugen on 26.04.2015.
 *
 * Активити выводит результаты теста на экран.
 *
 */
public class ShowUserResult extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_user_result);


        TextView textField = (TextView) findViewById(R.id.result);

        DBManager db = DBManager.getInstance(this);
        String stringWithResult = db.getSavedResult();


        textField.setText(stringWithResult);

    }
}
