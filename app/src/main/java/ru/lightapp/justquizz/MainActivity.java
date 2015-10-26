package ru.lightapp.justquizz;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import ru.lightapp.justquizz.controller.*;
import ru.lightapp.justquizz.db.DBManager;
import ru.lightapp.justquizz.model.*;


public class MainActivity extends ActionBarActivity {

    // Список названий всех доступных тестов:
    ArrayList<String> testTitles = new ArrayList<>();

    // Строка с выбранным тестом:
    private String selectedTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        DBManager db = new DBManager(this);

        // Получаем массив с названиями всех доступных тестов:
        Tests tests = new Tests();
        //testTitles = tests.getTestTitles();

        testTitles = db.getTestTitles();

        if(!testTitles.isEmpty())  {

            // Находим список, создаем адаптер и присваеваем адаптер списку:
            ListView listTest = (ListView) findViewById(R.id.listTest);
            ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, testTitles);
            listTest.setAdapter(mAdapter);
            // Устанавливаем слушатель:
            listTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                    TextView textView = (TextView) itemClicked;
                    selectedTest = (String) textView.getText();
                }
            });
        } else{
            Button button_start = (Button) findViewById(R.id.button_start);
            button_start.setText(R.string.button_start_download);
        }

    }


    /*
    *   Обработка нажатия кнопки начала тестирования:
     */
    public void onClick(View view){
         // Если загружен список тестов, то:
        if(!testTitles.isEmpty()) {

            // Проверка - выбрал ли пользователь один из тестов
            if (selectedTest != null) {
                // Инициализируем программу выбранным тестом:
                Init init = Init.getInstance(selectedTest);
                //Init.resetNumberOfQuestion();

                Intent intent = new Intent(MainActivity.this, TestScreen.class);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Выберите один из тестов", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else{
            //Toast toast = Toast.makeText(getApplicationContext(), "Пусто", Toast.LENGTH_SHORT);
            //toast.show();
            startDownloadActivity();

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.download_test_from_server:
                startDownloadActivity();
                return true;

            case R.id.action_settings:
                showSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

        //return super.onOptionsItemSelected(item);
    }

    private void showSettings() {

    }

    private void startDownloadActivity() {

        Intent intent = new Intent(MainActivity.this, LoaderTestFromServer.class);
        startActivity(intent);
    }
}
