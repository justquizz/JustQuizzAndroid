package ru.lightapp.justquizz.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.dataexchange.DownloadTestFromServer;
import ru.lightapp.justquizz.dataexchange.ServerManager;


/**
 * Created by Eugen on 20.07.2015.
 *
 * Активити формирует экран загрузки новых тестов с сервера
 */
public class LoaderTestFromServer extends Activity {

    // Элементы Activity:
    private TextView info;
    private ListView listCategories;
    private ListView listTests;
    private ArrayAdapter<String> mAdapterCategories;
    private ArrayAdapter<String> mAdapterTests;
    private Button button_back;
    private Button button_download;


    // Массив с тестами:
    ArrayList[] tests;

    // Строка с выбранной категорией:
    private String selectedCategory;

    // Строка с именем файла выбранного теста:
    private String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_test_from_server);

        /*
        * Инициализируем элементы экрана:
        * - кнопка НАЗАД;
        * - кнопка ЗАГРУЗИТЬ,
        * - информационное (TextView) поле;
        * - список, в который загрузим категории, доступные на севере;
        * - список, в который загрузим доступные на севере тесты для данной категории(скрываем его);
        */

        button_back = (Button) findViewById(R.id.button_back);
        button_back.setEnabled(false);

        button_download = (Button) findViewById(R.id.button_download);

        info = (TextView) findViewById(R.id.download_txt_info);
        info.setText(R.string.press_key);

        listCategories = (ListView) findViewById(R.id.listCategories);

        listTests = (ListView) findViewById(R.id.listTests);
        listTests.setVisibility(View.GONE);

    }


    /*
    * Обработчик кнопки ЗАГРУЗИТЬ:
    * - выводим Тост о начале загрузки,
    * - делаем неактивной кнопуку ЗАГРУЗИТЬ,
    * - обращаемся на сервер и получаем список категорий тестов,
    *
    */

    public void onClickGetCategories(final View view) {



        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.toast_download_start, Toast.LENGTH_SHORT);
        toast.show();


        if(fileName == null) {

        /*
        * Создаем объект для работы с сервером,
        * который и получит для нас массив с названиями категорий и их описанием
        */
            final ServerManager server = new ServerManager();
            ArrayList[] categories = server.getCategories();

        /*
        * Если получение списка категорий было неудачно (связь с инетом), то
        * в categories[3] будет содержатся список этих ошибок - выводим сообщение об ошибке.
        *
        * Если ошибок нет, то выводим на экран список категорий, полученных с сервера:
         */
            ArrayList<String> error = categories[3];
            if (error != null) {
                toast = Toast.makeText(getApplicationContext(),
                        R.string.error_server_not_found, Toast.LENGTH_LONG);
                toast.show();

            } else {
                final ArrayList<Integer> idCategory = categories[0];
                ArrayList<String> titleCategories = categories[1];
                final ArrayList<String> descriptionCategories = categories[2];

                try {

                    // Создаем адаптер и присваеваем адаптер списку категорий:
                    mAdapterCategories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleCategories);
                    listCategories.setAdapter(mAdapterCategories);
                /*
                * Обработчик нажатия на список категорий тестов:
                */
                    listCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                        /*
                        * При выборе категории теста, из массива извлекается  ее id.
                        * И по id с сервера получается масссив со список доступных тестов этой категории.
                        */
                            info.setText(descriptionCategories.get(position));
                            TextView textView = (TextView) itemClicked;
                            selectedCategory = (String) textView.getText();
                            tests = server.getTestsByCategory(idCategory.get(position));
                            ArrayList<String> titleTests = tests[0];

                        /*
                        * Создаем адаптер и выводим на экран этот список:
                        */
                            mAdapterTests = new ArrayAdapter<>(LoaderTestFromServer.this, android.R.layout.simple_list_item_1, titleTests);
                            listTests.setAdapter(mAdapterTests);

                        /*
                        * Меняем состояние элементов экран:
                        *  - скрываем список с категориями,
                        * - делаем активной кнопку НАЗАД,
                        * - делаем видимым список с тестами,
                        * - делаем неактивной кнопку ЗАГРУЗИТЬ:
                        */
                            listCategories.setVisibility(View.GONE);
                            button_back.setEnabled(true);
                            listTests.setVisibility(View.VISIBLE);
                            button_download.setEnabled(false);

                        }
                    });

                /*
                * Обработчик нажатия на выбранный тест:
                */
                    listTests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                            button_download.setEnabled(true);

                            ArrayList<String> fileNameArray = tests[1];

                            fileName = fileNameArray.get(position);

                            //System.out.println(" --- выбран файл - " + fileName);


                        }
                    });

                } catch (Exception e) {
                    System.out.println(" --- start trace");
                    e.printStackTrace();
                    System.out.println(" --- end trace");
                }

                // Выводим ообщение о необходимости выбора категории:
                info.setText(R.string.choice_category);
            }
        }else{

            DownloadTestFromServer loader = new DownloadTestFromServer(fileName);

            try {
                loader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(" --- поток завершен!");


        }
    }


    /*
    * Обработчик кноки НАЗАД.
    * - скрывает список с доступными тестами,
    * - показывает список с категориями,
    * - деактивизирует кнопку НАЗАД,
    * - выводит сообщение о необходимости выбора категории.
     */
    public void onClickButtonBack(View view) {

        listTests.setVisibility(View.GONE);
        listCategories.setVisibility(View.VISIBLE);
        button_back.setEnabled(false);
        info.setText(R.string.choice_category);
    }














    /*
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
    */

}