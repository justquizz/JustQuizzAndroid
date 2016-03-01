package ru.lightapp.justquizz.dataexchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;


/**
 * Created by eugen on 02.08.2015.
 * -
 *
 */
public class DBManager {

    /*
    * Единственный экземпляр класса
    */
    private static DBManager instance;

    /*
    * Имя БД и ее версия:
    */
    private static final String DATABASE_NAME = "jqzz5.db";
    private static final int DATABASE_VERSION = 1;

    /*
    * Названия таблиц с тестами и с глобальными переменными:
    */
    private static final String TEST_TABLE = "tests";
    private static final String GLOBAL_STRINGS = "global_strings";

    private Context context;
    private SQLiteDatabase db;

    private SQLiteStatement insertStmt;

    OpenHelper openHelper;


    /*
    * Реализация Singleton c двойной блокировкой:
    * - context     - нужен для работы с БД, обычно передается просто 'this';
    */
    public static DBManager getInstance(Context context){
        if(instance == null){
            synchronized (DBManager.class) {
                if(instance == null){
                    instance = new DBManager(context);
                    System.out.println(" --- делаем объект DBManager");
                }
            }
        }
        System.out.println(" --- отдаем объект DBManager");
        return instance;
    }


    /*
    * Скрывем конструктор:
    */
    private DBManager(Context context){

        this.context = context;

        openHelper = new OpenHelper(this.context);
        // подключаемся к БД:
        this.db = openHelper.getWritableDatabase();

    }



    /*
    * Метод вставляет в БД информацию о новом тесте:
    */
    public long insertNewTest(String titleTest, String fileName, String category, String author, String link_author_page, String description){

        openHelper = new OpenHelper(this.context);

        System.out.println(" --- " + titleTest + " - " + fileName + " - " + category + " - " + author + " - " + link_author_page + " - " + description);

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("title_test", titleTest);
        contentValues.put("file_name", fileName);
        contentValues.put("category", category);
        contentValues.put("author", author);
        contentValues.put("link_author_page", link_author_page);
        contentValues.put("description", description);

        // вставляем запись и получаем ее ID:
        long rowID =  db.insert(TEST_TABLE, null, contentValues);

        System.out.println(" --- вставка в бд " + rowID);

        openHelper.close();
        return rowID;
    }


    /*
    * Метод получает из БД названия всех тестов и возвращает:
    */
    public ArrayList<String> getTestTitles() {

        openHelper = new OpenHelper(this.context);

        // Создадим массив, который будем возвращать:
        ArrayList<String> list = new ArrayList<>();

        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(TEST_TABLE,
                new String[] {"title_test"},
                null, null, null, null, null);

        /*
        * Если результат запроса существует, то заносим элемент в массив:
        */
        if(cursor.moveToFirst()){

            int columnTitleTest = cursor.getColumnIndex("title_test");

            do{
                list.add(cursor.getString(columnTitleTest));
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        openHelper.close();
        return list;
    }


    /*
    * Метод формирует полный путь к файлу и записывает его в базу данных.
    * Собираем строку: каталог с тестами + имя файла + расширение файла,
    * и записываем ее в БД в таблицу глобальных переменных.
    */
    public void createPathToFile(String selectedTest) {

        String pathToFileTest = getDirectoryMD5() + getFileName(selectedTest) + getFileExtension();
        System.out.println(" --- имя файла " + pathToFileTest);
        insertPathToFileInDataBase(pathToFileTest);

    }

    /*
    * Метод получает из таблицы глобальных переменных путь к папке с тестами
    */
    private String getDirectoryMD5() {

        String directory_md5 = "";

        // Делаем запрос в БД:
        Cursor cursor = db.rawQuery("select directory_md5 from " + GLOBAL_STRINGS + " where id = ?", new String[] { "1" });

        /*
        * Если результат запроса существует, то
        * получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("directory_md5");
            directory_md5 = cursor.getString(columnFileName);
        }

        System.out.println(" --- directory_md5 " + directory_md5);
        return directory_md5;
    }

    /*
    * Метод получает по названию теста его имя файла
    */
    private String getFileName(String selectedTest){

        openHelper = new OpenHelper(this.context);

        // Создадим строку, которую будем возвращать:
        String fileName = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(TEST_TABLE,
                new String[] {"file_name"},
                "title_test = ?",
                new String[]{selectedTest},
                null, null, null);

        /*
        * Если результат запроса существует, то
        * получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("file_name");

            fileName = cursor.getString(columnFileName);
        }

        cursor.close();
        openHelper.close();

        System.out.println(" ---  из базы данных - " + fileName);

        return fileName;
    }

    /*
    * Метод получет из таблицы глобальных переменных расширение теста-файла - .jqzz
    */
    private String getFileExtension() {

        return ".jqzz";
    }

    /*
    * Метод вставляет в таблицу глобальных перменных путь к тесту-файлу
    */
    private long insertPathToFileInDataBase(String pathToFileWithTest) {

        openHelper = new OpenHelper(this.context);

        System.out.println(" --- пишем путь к файлу - " + pathToFileWithTest);

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("path_to_file", pathToFileWithTest);

        int rowId = db.update(GLOBAL_STRINGS,
                contentValues,
                "id = ?",
                new String[]{"1"});

        System.out.println(" --- записали путь к файлу " + rowId);

        openHelper.close();
        return rowId;

    }

    /*
    * Получить из БД путь к тест-файлу:
    */
    public String getPathToFile() {

        System.out.println(" --- получаем имя файла из БД...  " );

        openHelper = new OpenHelper(this.context);

        // Создадим строку, которую будем возвращать:
        String pathToFile = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(GLOBAL_STRINGS,
                new String[] {"path_to_file"},
                "id = ?",
                new String[]{"1"},
                null, null, null);

        /*
        * Если результат запроса существует, то
        * получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("path_to_file");

            pathToFile = cursor.getString(columnFileName);
        }

        cursor.close();
        openHelper.close();

        System.out.println(" --- путь к файл-тесту  из базы данных - " + pathToFile);

        return pathToFile;
    }


    private  static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            /*
            * Создаем таблицу в которой будем хранить тесты
            */
            db.execSQL("CREATE TABLE " + TEST_TABLE + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title_test TEXT, " +
                    "file_name TEXT, " +
                    "category TEXT, " +
                    "author TEXT, " +
                    "link_author_page TEXT, " +
                    "description Text )");


            /*
            * Создаем таблицу в которой будем хранить настройки программы
            */
            db.execSQL("CREATE TABLE " + GLOBAL_STRINGS + "(" +
                    "id INTEGER, " +
                    "file_name TEXT, " +
                    "path_to_file TEXT, " +
                    "directory_md5 TEXT, " +
                    "file_extension TEXT)");

            /*
            * Создаем объект для наших данных и наполняем его:
            */
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", "1");
            contentValues.put("file_name", "");
            contentValues.put("path_to_file", "");
            contentValues.put("directory_md5", "/justquizz/tests/");
            contentValues.put("file_extension", ".jqzz");


            // вставляем запись и получаем ее ID:
            long rowID =  db.insert(GLOBAL_STRINGS, null, contentValues);

            System.out.println(" --- вставка в таблицу global_strings " + rowID);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

}
