package ru.lightapp.justquizz.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.dataexchange.ServerManager;


/**
 * Created by Eugen on 20.07.2015.
 *
 * Активити формирует экран загрузки новых тестов с сервера
 */
public class LoaderTestFromServer extends Activity {

    //private TextView info = (TextView) findViewById(R.id.download_txt_info);

    // Строка с выбранной категорией:
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_test_from_server);

        //Button download = (Button) findViewById(R.id.button_download);
        TextView info = (TextView) findViewById(R.id.download_txt_info);
        info.setText(R.string.press_key);

    }


    public void onClickGetCategories(final View view) {

        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.toast_download_start, Toast.LENGTH_SHORT);
        toast.show();


        /*
        * Создаем объект для работы с сервером,
        * который и получит для нас массив с названиями категорий и их описанием
        */
        final ServerManager server = new ServerManager();
        ArrayList[] categories = server.getCategories();

        /*
        * Если получение списка категорий было неудачно (сзь с инетом), то
        * в categories[2] будет содержатся список этих ошибок - выводим сообщение об ошибке.
        *
        * Если ошибок нет, то выводим на экран список категорий, полученных с сервера:
         */
        ArrayList<String> error = categories[2];
        if(error != null){
            toast = Toast.makeText(getApplicationContext(),
                    R.string.error_server_not_found, Toast.LENGTH_LONG);
            toast.show();

        }else {

            ArrayList<String> titleCategories = categories[0];
            final ArrayList<String> descriptionCategories = categories[1];

            //System.out.println(titleCategories + " --- " + descriptionCategories);
            // Находим наш информационный TextView:
            final TextView info = (TextView) findViewById(R.id.download_txt_info);

            try {
                // Находим список, создаем адаптер и присваеваем адаптер списку:
                final ListView listCategories = (ListView) findViewById(R.id.listCategories);
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleCategories);
                listCategories.setAdapter(mAdapter);
                // Устанавливаем слушатель:
                listCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                        System.out.println(id + " --- " + position);
                        info.setText(descriptionCategories.get(position));
                        TextView textView = (TextView) itemClicked;
                        selectedCategory = (String) textView.getText();

                        System.out.println(" --- " + selectedCategory);

                        ArrayList[] tests = server.getTestsByCategory(selectedCategory);




                        /*
                        if (position == 1) {
                            listCategories.setVisibility(View.GONE);

                        }
                        if (position == 0) {
                            listCategories.setVisibility(View.VISIBLE);
                        }
                        */
                    }
                });




            } catch (Exception e) {
                System.out.println(" --- start trace");
                e.printStackTrace();
                System.out.println(" --- end trace");
            }

            info.setText(R.string.choice_category);
        }
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