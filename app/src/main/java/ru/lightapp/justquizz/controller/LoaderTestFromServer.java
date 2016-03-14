package ru.lightapp.justquizz.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.dataexchange.DBManager;
import ru.lightapp.justquizz.dataexchange.DownloadTestFromServer;
import ru.lightapp.justquizz.dataexchange.FileManager;
import ru.lightapp.justquizz.dataexchange.ServerManager;


/**
 * Created by Eugen on 20.07.2015.
 *
 * Активити формирует экран загрузки новых тестов с сервера.
 *
 * Содержит методы:
 * - Получения от сервера категорий тестов,
 * - Обработки нажатия кнопки НАЗАД
 * - Обработки нажатия кнопки ЗАГРУЗИТЬ
 */
public class LoaderTestFromServer extends Activity {

    /*
    * Элементы Activity:
    */
    private TextView info;
    private ListView listCategories;
    private ListView listTests;
    private ArrayAdapter<String> mAdapterTests;
    private Button button_back;
    private Button button_download;
    //private ProgressBar progressBar;

    /*
    * Массив с тестами полученными от сервера:
    */
    private ArrayList[] tests;

    /*
    * Номер выбранного теста в массиве:
    */
    private int numberOfTest;

    /*
    * Сформированные поля выбранного теста для загрузки:
    */
    private String currentTestTitle;
    private String currentFileName;
    private String currentDescription;
    private String currentAuthor;
    private String currentLinkAuthor;

    /*
    * Строка с выбранной категорией:
    */
    private String selectedCategory;

    /*
    * Объект для обмена данными:
    */
    private ServerManager server;
    private DBManager db;

    // Элемент UI:
    private Toast toast;


    /*
    * Инициализируем элементы экрана:
    * - кнопка НАЗАД;
    * - кнопка ЗАГРУЗИТЬ,
    * - информационное (TextView) поле;
    * - список, в который загрузим категории, доступные на севере;
    * - список, в который загрузим доступные на севере тесты для данной категории(скрываем его);
    *
    * getCateogies() наполняет элементы экрана информацией.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_test_from_server);

        button_back = (Button) findViewById(R.id.button_back);
        //button_back.setVisibility(View.INVISIBLE);

        // делаем невидимым progressBarDownloader, пока что не нужен:
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarDownloader);
        progressBar.setVisibility(View.INVISIBLE);

        button_download = (Button) findViewById(R.id.button_download);
        button_download.setVisibility(View.INVISIBLE);

        info = (TextView) findViewById(R.id.download_txt_info);
        //info.setText(R.string.press_key);

        listCategories = (ListView) findViewById(R.id.listCategories);

        listTests = (ListView) findViewById(R.id.listTests);
        listTests.setVisibility(View.GONE);

        // Получаем объект для обмена данными с сервером:
        server = ServerManager.getInstance();
        db = DBManager.getInstance(this);

        // Загружаем категории тестов с сервера:
        getCategories();

    }


    /*
    * Метод получает от сервера список доступных категорий тестов:
    */
    public void getCategories() {

        /*
        * Создаем объект для работы с сервером,
        * который и получит для нас массив с названиями категорий и их описанием
        */
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
                info.setText(R.string.error_server_not_found);

            } else {
                final ArrayList<Integer> idCategory = categories[0];
                ArrayList<String> titleCategories = categories[1];
                final ArrayList<String> descriptionCategories = categories[2];

                try {

                    // Создаем адаптер и присваеваем адаптер списку категорий:
                    ArrayAdapter<String> mAdapterCategories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleCategories);
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
                        * Меняем состояние элементов экрана:
                        *  - скрываем список с категориями,
                        * - делаем видимой кнопку НАЗАД,
                        * - делаем видимым список с тестами,
                        * - делаем невидимой кнопку ЗАГРУЗИТЬ:
                        */
                            listCategories.setVisibility(View.GONE);
                            button_back.setVisibility(View.VISIBLE);
                            listTests.setVisibility(View.VISIBLE);
                            button_download.setVisibility(View.INVISIBLE);

                        }
                    });

                /*
                * Обработчик нажатия на выбранный тест:
                *
                * - делаем активной кнопку ЗАГРУЗИТЬ,
                * - получаем номер теста в массиве,
                * - и его имя файла
                *
                */
                    listTests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                            numberOfTest = position;
                            currentTestTitle = (String) tests[0].get(numberOfTest);
                            currentFileName = (String) tests[1].get(numberOfTest);
                            currentDescription = (String) tests[2].get(numberOfTest);
                            //currentAuthor = (String) tests[4].get(numberOfTest);
                            currentAuthor = "Jack";
                            currentLinkAuthor = "http://lightapp.ru/justquizz/people/id7";

                            System.out.println(" --- " + currentTestTitle + " - " + currentFileName);

                            if (db.isTestExist(currentFileName, currentTestTitle)) {
                                toast = Toast.makeText(getApplicationContext(),
                                        R.string.toast_test_exist, Toast.LENGTH_SHORT);
                                toast.show();
                                button_download.setVisibility(View.INVISIBLE);
                            } else {
                                button_download.setVisibility(View.VISIBLE);
                            }


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
    }


    /*
    * Обработчик кноки НАЗАД.
    * - скрывает список с доступными тестами,
    * - показывает список с категориями,
    * - деактивизирует кнопку НАЗАД,
    * - скрывает кнопку ЗАГРУЗИТЬ,
    * - выводит сообщение о необходимости выбора категории.
    */
    public void onClickButtonBack(View view) {

        if(listCategories.getVisibility() == View.VISIBLE)
            LoaderTestFromServer.this.finish();

        listTests.setVisibility(View.GONE);
        //button_back.setVisibility(View.INVISIBLE);
        listCategories.setVisibility(View.VISIBLE);
        button_download.setVisibility(View.INVISIBLE);
        info.setText(R.string.choice_category);
    }


    /*
    * Обработчик кнопки ЗАГРУЗИТЬ выбранный тест.
    * - загружаем тест-файл;
    * - вставляем в БД информацию о новом тесте;
    * - выводим toast сообщение об успешной загрузке:
    */
    public void onClickDownloadTest(View view) {


        DownloadTestFromServer loader = new DownloadTestFromServer(currentFileName);

        try {
            loader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        long num = db.insertNewTest(currentTestTitle, currentFileName, selectedCategory, currentAuthor, currentLinkAuthor, currentDescription);

        if (num > 0) {
            toast = Toast.makeText(getApplicationContext(), R.string.toast_download_success, Toast.LENGTH_SHORT);
            toast.show();
            button_download.setVisibility(View.INVISIBLE);
        }

        System.out.println(" --- файл скачан! - " + currentFileName);
    }

}