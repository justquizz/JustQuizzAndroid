package ru.lightapp.justquizz.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.dataexchange.DownloadFile;
import ru.lightapp.justquizz.db.DBHelper;

/**
 * Created by Eugen on 20.07.2015.
 *
 * Активити формирует экран загрузки новых тестов с сервера
 */
public class LoaderTestFromServer extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);

        Button download = (Button) findViewById(R.id.button_download);

    }


    public void onClickDownloadFile(View view) {

        Toast toast = Toast.makeText(getApplicationContext(),
                "Загрузка", Toast.LENGTH_SHORT);
        toast.show();

        Thread downloadFile = new Thread(new DownloadFile());
        downloadFile.start();

    }

    public void onClickInsertInDB(View view){

        DBHelper database = new DBHelper(this);
        long num = database.insertNewTest("Привет, как дела", "wrfwfq");
        System.out.println(" --- строка вставлена " + num);

    }

    public void onClickDeleteDB(View view){
        DBHelper database = new DBHelper(this);
        int numDelete = database.clearTable();
        System.out.println(" --- удалено строк " + numDelete);
    }

    public void onClickGetAll(View view){
        DBHelper database = new DBHelper(this);
        List<String> list = database.getAll();

        for(String item: list){
            System.out.println(" --- getAll:" + item);
        }
    }

}