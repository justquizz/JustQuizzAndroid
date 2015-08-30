package ru.lightapp.justquizz.model;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Eugen
 *
 * Образец класса содержит:
 * - массив с названиями всех тестов из папки ..tests/
 * - статический путь к папке ..tests/
 *
 * Так же есть метод для проверки сущетвует ли указанный пользователем тест(файл .ini)
 */
public class Tests {

    private ArrayList<String> testTitles; // массив с названиями всех тестов из папки ..tests/
    //private String[] testTitlesArray;
    //public static final String PATH_OF_TEST_FILE = "tests/test";
    //public static final String PATH_OF_TEST_FILE = "/sdcard/tests/test"; // path file for Android       +API17      -API19
    //public static final String PATH_OF_TEST_FILE = "/storage/sdcard0/tests/test"; // path file for Android 4.2.2 API17
    //public static final String PATH_OF_TEST_FILE = "/mnt/extSdCard/tests/test"; // path file for Android    -API19
    //public static final String PATH_OF_TEST_FILE = "/storage/extSdCard/tests/test"; // path file for Android ---
    //public static final String PATH_OF_TEST_FILE = "/storage/emulated/0/tests/test"; // path file for Android   -API19
                                                     //storage/emulated/0/tests
    public static String PATH_OF_TEST_FILE = "";

    private final String FOLDER_WITH_TESTS = "tests";  // Папка с тестами
    private final String NAME_OF_TEST_FILE = "/test";   // имя тест-файла
    private final String FILE_EXTENSION = ".jqzz";   // имя тест-файла
    private final String TITLE = "title";



    public Tests(){

        PATH_OF_TEST_FILE = getPathToSdCard();

        String nameFile;
        String nameTest;
        testTitles = new ArrayList<String>();

        for(int i = 1; i <= 100; i++){

            nameFile = PATH_OF_TEST_FILE + i + FILE_EXTENSION;
            nameTest = new PropertyItemGetter().getItem(TITLE, nameFile);
            if(nameTest == null)
                continue;

            testTitles.add(i + "." + nameTest);
        }


    }

    public String getPathToSdCard(){

        String path = "";

        // проверяем доступность SD карты
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println(" --- error: SD card not mounted");

        } else{
            System.out.println(" --- error: SD card  mounted");
        }

        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        //File path = Environment.get
        // добавляем свой каталог к пути
        path = sdPath.getAbsolutePath() + "/" + FOLDER_WITH_TESTS + NAME_OF_TEST_FILE;


        return path;
    }

    // GETTERS:


    public ArrayList<String> getTestTitles() {
        return testTitles;
    }
}
