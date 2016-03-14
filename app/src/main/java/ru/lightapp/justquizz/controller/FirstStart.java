package ru.lightapp.justquizz.controller;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.dataexchange.DBManager;
import ru.lightapp.justquizz.dataexchange.FileManager;
import ru.lightapp.justquizz.dataexchange.ServerManager;
import ru.lightapp.justquizz.model.SingleTest;

/**
 * Created by eugen on 12.03.2016.
 *
 * Класс управляет активити первого запуска приложения first_start.xml
 */
public class FirstStart extends ActionBarActivity {

    private ServerManager server;
    private FileManager fileManager;
    private DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_start);

        server = ServerManager.getInstance();
        fileManager = FileManager.getInstance();
        db = DBManager.getInstance(this);
    }



    /*
    * Обработка нажатия кнопок:
    */
    public void onClickDownloadAllDemo(View view) {

        System.out.println(" --- onClickDownloadAllDemo");


        ArrayList<SingleTest> arrayTests  = server.getTestsByCategory(1);


        //if(tests[5] != null) {
            //ArrayList<String> titleTestArray = tests[0];
            //ArrayList<String> fileNameArray = tests[1];
            //ArrayList<String> descriptionArray = tests[2];
            //ArrayList<String> downloadsArray = tests[3];

            /*
            tests[0] = titleArray;
            tests[1] = fileNameArray;
            tests[2] = descriptionArray;
            tests[3] = downloadsArray;
            */


            // TODO получили массив со всеми именами файлов. Теперь проходим в цикле: скачиваем и заносим в базу.

            for (int i = 0; i <= arrayTests.size() - 1; i++) {

                if (fileManager.downloadTest(arrayTests.get(i).getFileName())) {

                    String titleTest = arrayTests.get(i).getTitle();
                    String fileNameTest = arrayTests.get(i).getFileName();
                    String category = "1";
                    String author = "Jack";
                    //String author = arrayTests.get(i).getAuthor();
                    String linkAuthorPage = "http://lightapp.ru/justquizz/people/id7";
                    //String linkAuthorPage = arrayTests.get(i).getLinkAuthorPage();
                    String descrTest = arrayTests.get(i).getDescription();


                    long num = db.insertNewTest(titleTest, fileNameTest, category, author, linkAuthorPage, descrTest);

                    if (num > 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_download_success, Toast.LENGTH_SHORT);
                        toast.show();

                    }

                    System.out.println(" --- файл скачан! - " + titleTest);
                }
            }
        //}else{
            //System.out.println(" --- tests == null" + tests[5].toString());
        //}
            db.resetFirstStart();
    }


}
