package ru.lightapp.justquizz.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eugen on 02.08.2015.
 */
public class DBManager {

    private static final String DATABASE_NAME = "jqzz3.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEST_TABLE = "tests";

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

    public List getAll(){

        ArrayList<String> list = new ArrayList<>();
        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(TEST_TABLE, null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = cursor.getColumnIndex("id");
            int test_nameColIndex = cursor.getColumnIndex("test_name");
            int hashColIndex = cursor.getColumnIndex("hash");

            /*
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                System.out.println(" --- " + "ID = " + cursor.getInt(idColIndex) +
                                ", name = " + cursor.getString(test_nameColIndex) +
                                ", email = " + cursor.getString(hashColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (cursor.moveToNext());
        } else
            System.out.println(" --- 0 rows");
        cursor.close();
        */
        }
        list.add("one");
        list.add("two");
        list.add("three");

        openHelper.close();
        return list;
    }
    /*
    * Метод вставляет в БД информацию о новом тесте:
    */
    public long insertNewTest(String titleTest, String fileName, String category, String author, String link_author_page, String description){

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

        System.out.println(" --- вставка в бд" + rowID);

        openHelper.close();
        return rowID;
    }

    public int clearTable(){
        // удаляем все записи
        int clearCount = db.delete(TEST_TABLE, null, null);

        openHelper.close();
        return clearCount;
    }


    /*
    * Метод получает из БД названия всех тестов и возвращает:
    */
    public ArrayList<String> getTestTitles() {

        // Создадим массив, который будем возвращать:
        ArrayList<String> list = new ArrayList<>();

        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(TEST_TABLE,
                new String[] {"title_test"},
                null, null, null, null, null);

        if(cursor.moveToFirst()){

            do{
                list.add(cursor.getString(0));

            }while(cursor.moveToNext());
        }

        cursor.close();

        //list.add("one");
        //list.add("two");
        //list.add("three");

        openHelper.close();
        return list;
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

    }

}
