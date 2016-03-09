package ru.lightapp.justquizz;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import ru.lightapp.justquizz.dataexchange.*;


public class MainActivity extends ActionBarActivity {

    /*
    * Элементы Activity:
    */
    Button buttonTestStart;
    Button buttonDownloadTest;

    /*
    * Список названий всех доступных тестов:
    */
    ArrayList<String> testTitles = new ArrayList<>();

    private ListView listTest;

    /*
    * Строка с выбранным тестом:
    */
    private String selectedTest = "";

    /*
    * Обект для работы с базой данных:
    */
    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        buttonTestStart = (Button) findViewById(R.id.button_start_test);
        buttonDownloadTest = (Button) findViewById(R.id.button_download_test);

       /*
       * Создаем объект занимающийся работой с данными:
       */
       db = DBManager.getInstance(this);

        showTestTitles();

    }

    /*
    * Метод наполняет экран названиями тестов из БД:
    */
    private void showTestTitles(){
       /*
       * Получаем массив доступных тестов:
       */
        testTitles = db.getTestTitles();

        selectedTest = "";

        if(!testTitles.isEmpty())  {

            /*
            * Находим список, создаем адаптер и присваиваем адаптер списку:
            */
            listTest = (ListView) findViewById(R.id.listTest);
            ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, testTitles);
            listTest.setAdapter(mAdapter);

            /*
            * Устанавливаем слушатель на однократное нажатие на названии теста:
            */
            listTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                    TextView textView = (TextView) itemClicked;
                    selectedTest = (String) textView.getText();
                }
            });

            /*
            * Слушатель на долгое нажатие.
            * AlertDialog предлагат удалить тест:
            */
            listTest.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View itemClicked, int i, long l) {

                    TextView textView = (TextView) itemClicked;
                    selectedTest = (String) textView.getText();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.delete_test).setCancelable(false).
                            setNegativeButton(R.string.string_yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    System.out.println(" --- удаляем - " + selectedTest);
                                    deleteTest(selectedTest);
                                }
                            }).
                            setPositiveButton(R.string.string_no, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return false;
                }
            });

            listTest.setVisibility(View.VISIBLE);
            buttonTestStart.setText(R.string.button_start_test);
            buttonTestStart.setVisibility(View.VISIBLE);

        } else{
            buttonTestStart.setVisibility(View.INVISIBLE);

            if(listTest != null)
                listTest.setVisibility(View.INVISIBLE);
            //buttonTestStart.setText(R.string.button_download_test);

        }
    }


    /*
    * Метод удаляет выбранный тест,
    * затем обновляет список тестов на экране:
    */
    private void deleteTest(String selectedTest) {

        db.deleteTest(selectedTest);
        showTestTitles();
    }

    /*
    * Обработка нажатия кнопок:
    */
    public void onClick(View view){

        switch (view.getId()){

            case R.id.button_download_test:
                startDownloadActivity();
                break;


            case R.id.button_start_test:
                if (selectedTest != "") {
                    /*
                    * Записываем путь к файлу в БД,
                    * и вызываем экран с тестом:
                    */
                    db.createPathToFile(selectedTest);
                    Intent intent = new Intent(MainActivity.this, TestScreen.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Выберите один из тестов", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;

        }








        /* Если загружен список тестов, то:
        if(!testTitles.isEmpty()) {

            /*
            * Проверка - выбрал ли пользователь один из тестов:

            if (selectedTest != null) {
                /*
                * Записываем путь к файлу в БД,
                * и вызываем экран с тестом:

                db.createPathToFile(selectedTest);
                Intent intent = new Intent(MainActivity.this, TestScreen.class);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Выберите один из тестов", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else{

            startDownloadActivity();
        }
        */
    }


    /*
    * Метод вызывается по завершению активности для загрузки
    * новых тестов с сервера (LoaderTestFromServer).
    * И на экране обновляется список с названиями тестов:
    * */
    @Override
    public void onActivityResult(int request, int request2, Intent intent){

        showTestTitles();
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

            case R.id.how_to:
                showHowTo();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

        //return super.onOptionsItemSelected(item);
    }


    /*
    * Создаем активность, которая отображает описание как пользоваться приложением:
    */
    private void showHowTo() {

        Intent intent = new Intent(MainActivity.this, HowTo.class);
        startActivity(intent);
    }

    private void showSettings() {

    }

    /*
    * Создаем активность для загрузки новых тестов с сервера - LoaderTestFromServer.
    * По ее завершению запускается onActivityResult()
     * и на экране обновляется список с названиями тестов:
    * */
    private void startDownloadActivity() {

        Intent intent = new Intent(MainActivity.this, LoaderTestFromServer.class);
        startActivityForResult(intent, 1);
    }
}
