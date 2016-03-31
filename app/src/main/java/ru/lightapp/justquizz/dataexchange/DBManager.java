package ru.lightapp.justquizz.dataexchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import ru.lightapp.justquizz.R;


/**
 * Created by eugen on 02.08.2015.
 *
 * Singleton.
 * Экземпляр класса предоставляет доступ к БД.
 *
 *
 *
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
    private static final String DATABASE_NAME = "jqzz14.db";
    private static final int DATABASE_VERSION = 1;

    /*
    * Названия таблиц с тестами и с глобальными переменными:
    */
    private static final String TEST_TABLE = "tests";
    private static final String GLOBAL_STRINGS = "global_strings";

    private Context context;
    private SQLiteDatabase db;


    OpenHelper openHelper;


    /*
    * Реализация Singleton c двойной блокировкой:
    * - context  нужен для работы с БД, обычно передается просто 'this';
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

        long rowID = 0;
        if(!isTestExist(fileName,titleTest)) {

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
            contentValues.put("start_test", "0");
            contentValues.put("end_test", "0");
            contentValues.put("description", description);
            //contentValues.put("deleted", "");


            // вставляем запись и получаем ее ID:
            rowID = db.insert(TEST_TABLE, null, contentValues);

            System.out.println(" --- вставка в бд " + rowID);

            openHelper.close();

        }
        return rowID;
    }


    /*
    * Метод получает из БД названия всех тестов и возвращает:
    */
    public ArrayList<String> getTestTitles() {

        //openHelper = new OpenHelper(this.context);

        // Создадим массив, который будем возвращать:
        ArrayList<String> list = new ArrayList<>();

        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(TEST_TABLE,
                new String[]{"title_test"},
                null,
                null,
                //"deleted = ?",
                //new String[]{""},
                null,
                null,
                null);


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
        //openHelper.close();
        return list;
    }


    /*
    * Инициализация при запуске теста:
    * - записываем в БД имя файла выбранного теста;
    * - записываем полный путь к этому файлу;
    */
    public void initSelectedTest(String selectedTest) {

        saveFileNameSelectedTest(selectedTest);
        createPathToFile(selectedTest);
    }

    /*
    * Пишем в БД имя файла текущего теста:
    */
    public void saveFileNameSelectedTest(String selectedTest){

        String fileName = getFileName(selectedTest);

        openHelper = new OpenHelper(this.context);

        System.out.println(" --- пишем имя файла - " + fileName);

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("file_name", fileName);

        int rowId = db.update(GLOBAL_STRINGS,
                contentValues,
                "_id = ?",
                new String[]{"1"});

        System.out.println(" --- записали имя файла " + rowId);

        openHelper.close();

    }


    /*
    * Метод формирует полный путь к файлу и записывает его в базу данных.
    * Собираем строку: каталог с тестами + имя файла + расширение файла,
    * и записываем ее в БД в таблицу глобальных переменных.
    */
    public void createPathToFile(String selectedTest) {

        String directoryMD5 = context.getString(R.string.directoryMD5);
        String fileExtension = context.getString(R.string.file_extension);
        String pathToFileWithTest = directoryMD5 + getFileName(selectedTest) + fileExtension;

        System.out.println(" --- имя файла " + pathToFileWithTest);

        openHelper = new OpenHelper(this.context);

        System.out.println(" --- пишем путь к файлу - " + pathToFileWithTest);

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("path_to_file", pathToFileWithTest);

        int rowId = db.update(GLOBAL_STRINGS,
                contentValues,
                "_id = ?",
                new String[]{"1"});

        System.out.println(" --- записали путь к файлу " + rowId);

        openHelper.close();

    }

    /*
    * Метод получает из таблицы глобальных переменных путь к папке с тестами

    private String getDirectoryMD5() {

        String directory_md5 = "";

        // Делаем запрос в БД:
        Cursor cursor = db.rawQuery("select directory_md5 from " + GLOBAL_STRINGS + " where _id = ?", new String[]{"1"});

        /*
        * Если результат запроса существует, то
        * получаем его:

        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("directory_md5");
            directory_md5 = cursor.getString(columnFileName);
        }

        System.out.println(" --- directory_md5 " + directory_md5);

        cursor.close();
        return directory_md5;
    }
    */


    /*
    * Метод получает по названию теста его имя файла
    */
    private String getFileName(String selectedTest){

        openHelper = new OpenHelper(this.context);

        // Создадим строку, которую будем возвращать:
        String fileName = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(TEST_TABLE,
                new String[]{"file_name"},
                //"title_test = ? or deleted = ?",
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
    TODO где хранить разрешение файла?
    private String getFileExtension() {

        return ".jqzz";
    }
    */


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
                "_id = ?",
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


    /*
    * Метод записывает в таблицу глобальных переменных строку с результатами теста:
    */
    public void saveTestResult(String result) {

        openHelper = new OpenHelper(this.context);

        System.out.println(" --- пишем результат теста");

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("test_result", result);

        int rowId = db.update(GLOBAL_STRINGS,
                contentValues,
                "_id = ?",
                new String[]{"1"});

        System.out.println(" --- записали путь к файлу " + rowId);

        openHelper.close();

    }

    /*
    * Получить из БД стрку с результатом теста:
    */
    public String getSavedResult() {

        System.out.println(" --- получаем строку с результатом теста из БД..." );

        openHelper = new OpenHelper(this.context);

        // Создадим строку, которую будем возвращать:
        String savedResult = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(GLOBAL_STRINGS,
                new String[] {"test_result"},
                "_id = ?",
                new String[]{"1"},
                null, null, null);

        /*
        * Если результат запроса существует, то
        * получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("test_result");

            savedResult = cursor.getString(columnFileName);
        }

        cursor.close();
        openHelper.close();

        System.out.println(" --- строка с результатом теста из БД получена");

        return savedResult;

    }

    /*
    * Метод проверяет существует ли тест в БД:
    * - пробуем получить имя тест-файла по названию теста;
    * - если оно равно имени файла с сервера, то тест существует:
    */
    public boolean isTestExist(String currentFileName, String currentTestTitle) {

        String gettingFileName = getFileName(currentTestTitle);
        
        return gettingFileName.equals(currentFileName);
    }


    /*
    * Удаляем запись о тесте из БД:
    */
    public void deleteTest(String selectedTest) {

        System.out.println(" --- здесь пишем код, помечаем запись о тесте как удаленную");

        openHelper = new OpenHelper(this.context);

        int rowId = db.delete(TEST_TABLE,
                "title_test = ?",
                new String[] {selectedTest});



        /*
        * Создаем объект для наших данных и наполняем его:

        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted", "1");

        // в этой строке ошибка
        int rowId = db.update(TEST_TABLE,
                contentValues,
                "title_test = ?",
                new String[]{selectedTest});
        */
        System.out.println(" --- запрос в БД при удалении теста - " + rowId);

        openHelper.close();


    }

    /*
    * Метод делает запрос в таблицу global_strings,
    *  и проверяет флаг первого запуска приложения:
    *  1 - первый запуск (true);
    *  0 - не первый запуск.
    */
    public String getFirstStart() {

        System.out.println(" --- получаем имя файла из БД...  " );

        openHelper = new OpenHelper(this.context);

        // Создадим строку, которую будем возвращать:
        String firstStart = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(GLOBAL_STRINGS,
                new String[] {"first_start"},
                "_id = ?",
                new String[]{"1"},
                null, null, null);

        /*
        * Если результат запроса существует, то
        * получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("first_start");
            firstStart = cursor.getString(columnFileName);
        }

        cursor.close();
        openHelper.close();

        System.out.println(" --- флаг признак первого запуска приложения - " + firstStart);

        return firstStart;
    }


    /*
    * Метод сбрасывает флаг первого запуска приложения,
    * т.е. делает его "0"
    */
    public void resetFirstStart() {

        openHelper = new OpenHelper(this.context);

        System.out.println(" --- сбрасываем флаг первого запуска");

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("first_start", "0");

        int rowId = db.update(GLOBAL_STRINGS,
                contentValues,
                "_id = ?",
                new String[]{"1"});

        System.out.println(" --- сбросили флаг первого запуска " + rowId);

        openHelper.close();

    }

    /*
    * Увеличиваем на 1 количество запусков текущего теста:
    */
    public void incrementStartTest() {

        openHelper = new OpenHelper(this.context);

        /*
        * TODO
        * - получить из БД имя текущего файла
        * - получить из БД кол-во запусков теста
        * - увеличить на 1
        * - записать в БД
        * */
        //String fileName = getCurrentFileName();
        // Создадим строку, которую будем возвращать:
        String fileName = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(GLOBAL_STRINGS,
                new String[]{"file_name"},
                //"title_test = ? or deleted = ?",
                "_id = ?",
                new String[]{"1"},
                null, null, null);
        /*
        * Если результат запроса существует, то получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("file_name");
            fileName = cursor.getString(columnFileName);
            System.out.println(" --- fileName on a incrementStartTest()" + fileName);
        }

        // Кол-во запусков:
        int quantityStart = 0;
        // Делаем запрос в базу данных:
        cursor = this.db.query(TEST_TABLE,
                new String[]{"start_test"},
                //"title_test = ? or deleted = ?",
                "file_name = ?",
                new String[]{fileName},
                null, null, null);
        /*
        * Если результат запроса существует, то получаем его:
        */
        if(cursor.moveToFirst()){

            int columnStartTest = cursor.getColumnIndex("start_test");
            String qString = cursor.getString(columnStartTest);
            System.out.println(" --- qString = " + qString);
            quantityStart = Integer.parseInt(qString);
            System.out.println(" --- quantityStart on a incrementStartTest() = " + quantityStart);
        }

        quantityStart++;
        //String quantityStartString = String.valueOf(quantityStart);

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("start_test", String.valueOf(quantityStart));

        int rowId = db.update(TEST_TABLE,
                contentValues,
                "file_name = ?",
                new String[]{fileName});



        cursor.close();
        openHelper.close();

        System.out.println(" --- инкремент запуска = " + quantityStart + " rowId=" + rowId);

        //return fileName;

    }

    /*
    * Увеличиваем на 1 количество запусков текущего теста:
    */
    public void incrementEndTest() {

        openHelper = new OpenHelper(this.context);

        /*
        * TODO
        * - получить из БД имя текущего файла
        * - получить из БД кол-во запусков теста
        * - увеличить на 1
        * - записать в БД
        * */
        //String fileName = getCurrentFileName();
        // Создадим строку, которую будем возвращать:
        String fileName = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(GLOBAL_STRINGS,
                new String[]{"file_name"},
                //"title_test = ? or deleted = ?",
                "_id = ?",
                new String[]{"1"},
                null, null, null);
        /*
        * Если результат запроса существует, то получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("file_name");
            fileName = cursor.getString(columnFileName);
            System.out.println(" --- fileName on a incrementEndTest()" + fileName);
        }

        // Кол-во запусков:
        int quantityEnd = 0;
        // Делаем запрос в базу данных:
        cursor = this.db.query(TEST_TABLE,
                new String[]{"end_test"},
                //"title_test = ? or deleted = ?",
                "file_name = ?",
                new String[]{fileName},
                null, null, null);
        /*
        * Если результат запроса существует, то получаем его:
        */
        if(cursor.moveToFirst()){

            int columnStartTest = cursor.getColumnIndex("end_test");
            String qString = cursor.getString(columnStartTest);
            System.out.println(" --- qString = " + qString);
            quantityEnd = Integer.parseInt(qString);
            System.out.println(" --- quantityEnd on a incrementEndTest() = " + quantityEnd);
        }

        quantityEnd++;
        //String quantityStartString = String.valueOf(quantityStart);

        /*
        * Создаем объект для наших данных и наполняем его:
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put("end_test", String.valueOf(quantityEnd));

        int rowId = db.update(TEST_TABLE,
                contentValues,
                "file_name = ?",
                new String[]{fileName});



        cursor.close();
        openHelper.close();

        System.out.println(" --- инкремент прохождения теста = " + quantityEnd + " rowId=" + rowId);

    }

    /*
    * Метод получает количество запусков текущего теста
    */
    public String getQuantityStart() {

        openHelper = new OpenHelper(this.context);
        String fileName = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(GLOBAL_STRINGS,
                new String[]{"file_name"},
                //"title_test = ? or deleted = ?",
                "_id = ?",
                new String[]{"1"},
                null, null, null);
        /*
        * Если результат запроса существует, то получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("file_name");
            fileName = cursor.getString(columnFileName);
            System.out.println(" --- fileName on a getQuantityStart()" + fileName);
        }

        // Кол-во запусков:
        String quantityStart = "";
        // Делаем запрос в базу данных:
        cursor = this.db.query(TEST_TABLE,
                new String[]{"start_test"},
                //"title_test = ? or deleted = ?",
                "file_name = ?",
                new String[]{fileName},
                null, null, null);
        /*
        * Если результат запроса существует, то получаем его:
        */
        if(cursor.moveToFirst()){

            int columnStartTest = cursor.getColumnIndex("start_test");
            quantityStart = cursor.getString(columnStartTest);
            //System.out.println(" --- qString = " + qString);
            //quantityStart = Integer.parseInt(qString);
            System.out.println(" --- quantityStart on a getQuantityStart() = " + quantityStart);
        }

        cursor.close();
        openHelper.close();

        return quantityStart;
    }

    /*
    * Метод получает количество полного окончания текущего теста
    */
    public String getQuantityEnd() {

        openHelper = new OpenHelper(this.context);
        String fileName = "";


        // Делаем запрос в базу данных:
        Cursor  cursor = this.db.query(GLOBAL_STRINGS,
                new String[]{"file_name"},
                //"title_test = ? or deleted = ?",
                "_id = ?",
                new String[]{"1"},
                null, null, null);
        /*
        * Если результат запроса существует, то получаем его:
        */
        if(cursor.moveToFirst()){

            int columnFileName = cursor.getColumnIndex("file_name");
            fileName = cursor.getString(columnFileName);
            System.out.println(" --- fileName on a getQuantityEnd()" + fileName);
        }

        // Кол-во запусков:
        String quantityEnd = "";
        // Делаем запрос в базу данных:
        cursor = this.db.query(TEST_TABLE,
                new String[]{"end_test"},
                //"title_test = ? or deleted = ?",
                "file_name = ?",
                new String[]{fileName},
                null, null, null);
        /*
        * Если результат запроса существует, то получаем его:
        */
        if(cursor.moveToFirst()){

            int columnStartTest = cursor.getColumnIndex("end_test");
            quantityEnd = cursor.getString(columnStartTest);
            //System.out.println(" --- qString = " + qString);
            //quantityStart = Integer.parseInt(qString);
            System.out.println(" --- quantityEnd on a getQuantityEnd() = " + quantityEnd);
        }

        cursor.close();
        openHelper.close();

        return quantityEnd;
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
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title_test TEXT, " +
                    "file_name TEXT, " +
                    "category TEXT, " +
                    "author TEXT, " +
                    "link_author_page TEXT, " +
                    "start_test TEXT, " +
                    "end_test TEXT, " +
                    "description TEXT)");
                    //"deleted TEXT)");


            /*
            * Создаем таблицу в которой будем хранить настройки программы
            */
            db.execSQL("CREATE TABLE " + GLOBAL_STRINGS + "(" +
                    "_id INTEGER, " +
                    "file_name TEXT, " +
                    "path_to_file TEXT, " +
                    "directory_md5 TEXT, " +
                    "file_extension TEXT, " +
                    "test_result TEXT, " +
                    "first_start TEXT )");

            /*
            * Создаем объект для наших данных и наполняем его:
            */
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", "1");
            contentValues.put("file_name", "");
            contentValues.put("path_to_file", "");
            contentValues.put("directory_md5", "/justquizz/tests/");
            contentValues.put("file_extension", ".jqzz");
            contentValues.put("test_result", "");
            contentValues.put("first_start", "1");


            // вставляем запись и получаем ее ID:
            long rowID =  db.insert(GLOBAL_STRINGS, null, contentValues);

            System.out.println(" --- вставка в таблицу global_strings " + rowID);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

}
