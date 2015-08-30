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
public class DBHelper {

    private static final String DATABASE_NAME = "jqzz.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEST_TABLE = "tests";

    private Context context;
    private SQLiteDatabase db;

    //private static final String INSERT = "insert into " + TEST_TABLE + " (test_name) values (?)";
    private SQLiteStatement insertStmt;

    OpenHelper openHelper;

    public DBHelper(Context context){

        this.context = context;
        openHelper = new OpenHelper(this.context);
        // ������������ � ��:
        this.db = openHelper.getWritableDatabase();

    }

    public List getAll(){

        ArrayList<String> list = new ArrayList<>();
        // ������ ������ � ���� ������:
        Cursor  cursor = this.db.query(TEST_TABLE, null, null, null, null, null, null);

        // ������ ������� ������� �� ������ ������ �������
        // ���� � ������� ��� �����, �������� false
        if (cursor.moveToFirst()) {

            // ���������� ������ �������� �� ����� � �������
            int idColIndex = cursor.getColumnIndex("id");
            int test_nameColIndex = cursor.getColumnIndex("test_name");
            int hashColIndex = cursor.getColumnIndex("hash");

            do {
                // �������� �������� �� ������� �������� � ����� ��� � ���
                System.out.println(" --- " + "ID = " + cursor.getInt(idColIndex) +
                                ", name = " + cursor.getString(test_nameColIndex) +
                                ", email = " + cursor.getString(hashColIndex));
                // ������� �� ��������� ������
                // � ���� ��������� ��� (������� - ���������), �� false - ������� �� �����
            } while (cursor.moveToNext());
        } else
            System.out.println(" --- 0 rows");
        cursor.close();

        list.add("one");
        list.add("two");
        list.add("three");

        openHelper.close();
        return list;
    }

    public long insertNewTest(String test_name, String hash){

        // ������� ������ ��� ������
        ContentValues contentValues = new ContentValues();
        contentValues.put("test_name", test_name);
        contentValues.put("hash", hash);
        // ��������� ������ � �������� �� ID:
        long rowID =  db.insert(TEST_TABLE, null, contentValues);

        openHelper.close();
        return rowID;

    }

    public int clearTable(){
        // ������� ��� ������
        int clearCount = db.delete(TEST_TABLE, null, null);

        openHelper.close();
        return clearCount;
    }



    private  static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TEST_TABLE + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "test_name TEXT, " +
                    "hash Text )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

    }

}
