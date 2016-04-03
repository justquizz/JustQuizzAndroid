package ru.lightapp.justquizz.controller;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import ru.lightapp.justquizz.MainActivity;
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

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_start);

        server = ServerManager.getInstance();
        fileManager = FileManager.getInstance();
        db = DBManager.getInstance(this);
    }



    /*
    * Обработка нажатия кнопоки загрузки демо тестов:
    */
    public void onClickDownloadAllDemo(View view) {

        //System.out.println(" --- onClickDownloadAllDemo");


        ArrayList<SingleTest> arrayTests  = server.getTestsByCategory(1);


            // TODO получили массив со всеми именами файлов. Теперь проходим в цикле: скачиваем и заносим в базу.

        if(arrayTests != null){
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


                    long rowID = db.insertNewTest(titleTest, fileNameTest, category, author, linkAuthorPage, descrTest);

                    if(rowID != -1){
                        //System.out.println(" --- файл скачан! - " + titleTest + " вставка в БД" + rowID);

                    }else{
                        //System.out.println(" --- проблема с БД при вставке теста FirstStart.java ");
                    }

                }
            }
            db.resetFirstStart();

            toast = Toast.makeText(getApplicationContext(), R.string.toast_download_success, Toast.LENGTH_SHORT);
            toast.show();

            FirstStart.this.finish();

        }else{
            toast = Toast.makeText(getApplicationContext(), R.string.error_server_not_found, Toast.LENGTH_SHORT);
            toast.show();
            //System.out.println(" --- arrayTests == null");
        }

    }

    /*
    * Обработка нажатия клавиши самостоятельной загрузки тестов:
    */
    public void onClickDownloadTests(View view) {

        Intent intent = new Intent(FirstStart.this, LoaderTestFromServer.class);
        startActivityForResult(intent, 32);
        //FirstStart.this.finish();
    }

    /*
    * Метод вызывается по завершению активности для загрузки
    * новых тестов с сервера (LoaderTestFromServer).
    * Закрываем окно первого запуска, и тогда на MainActivity
    * должен обновитьс список тестов:
    * */
    @Override
    public void onActivityResult(int request, int request2, Intent intent){

        db.resetFirstStart();
        FirstStart.this.finish();
    }
}
