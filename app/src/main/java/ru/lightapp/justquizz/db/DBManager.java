package ru.lightapp.justquizz.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import ru.lightapp.justquizz.dataexchange.FileManager;
import ru.lightapp.justquizz.model.Init;

/**
 * Created by eugen on 02.08.2015.
 *
 * TODO Singleton;
 *
 *
 */
public class DBManager {

    private static final String DATABASE_NAME = "jqzz4.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEST_TABLE = "tests";
    private static final String INIT_TABLE = "init";

    private Context context;
    private SQLiteDatabase db;

    //private static final String INSERT = "insert into " + TEST_TABLE + " (test_name) values (?)";
    private SQLiteStatement insertStmt;

    OpenHelper openHelper;

    public DBManager(Context context){

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


    //////////////////////////////////////////////////////////////
    public int clearTable(){
        // удаляем все записи
        int clearCount = db.delete(TEST_TABLE, null, null);

        openHelper.close();
        return clearCount;
    }
    ///////////////////////////////////////////////////////////////



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
        * Если результат запроса существует, то
        * заносим каждый элемент в массив:
        */
        if(cursor.moveToFirst()){

            int columnTitleTest = cursor.getColumnIndex("title_test");

            do{
                //list.add(cursor.getString(0));
                list.add(cursor.getString(columnTitleTest));

            }
            while(cursor.moveToNext());
        }

        cursor.close();
        openHelper.close();
        return list;
    }

    /*
    * Метод формирует полный путь к файлу и записывает его в базу данных:
    */
    public void createPathToFile(String selectedTest) {




        String fileName = getFileName(selectedTest);

        //FileManager fileManager = FileManager.getInstance();
        //String pathToFileWithTest = fileManager.getStorageDirectory() + Init.directoryMD5 + fileName + ".jqzz";

        //insertPathToFileInDataBase(pathToFileWithTest);

    }


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

    private long insertPathToFileInDataBase(String pathToFileWithTest) {

        openHelper = new OpenHelper(this.context);

        System.out.println(" --- путь к файлу - " + pathToFileWithTest);

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("path_to_file", pathToFileWithTest);

        // вставляем запись и получаем ее ID:
        long rowID =  db.insert(INIT_TABLE, null, contentValues);

        System.out.println(" --- вставка в бд путь к файлу " + rowID + " - " + pathToFileWithTest);

        openHelper.close();
        return rowID;

    }


    private  static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TEST_TABLE + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title_test TEXT, " +
                    "file_name TEXT, " +
                    "category TEXT, " +
                    "author TEXT, " +
                    "link_author_page TEXT, " +
                    "description Text )");

            db.execSQL("CREATE TABLE " + INIT_TABLE + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "path_to_file TEXT, " +
                    "file_name TEXT, " +
                    "directory_md5 TEXT, " +
                    "author TEXT, " +
                    "link_author_page TEXT, " +
                    "description Text )");


        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

}
