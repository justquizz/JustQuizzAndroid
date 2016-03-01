package ru.lightapp.justquizz.dataexchange;

import android.content.Context;
import java.util.ArrayList;
import ru.lightapp.justquizz.db.*;

/**
 * Created by eugen on 02.11.2015.
 *
 * Объект класса предоставляет доступ ко всем ресурсам программы:
 *  - база данных,
 *  - работа с файлами,
 *  - сервер в интернете.\
 *
 *
 *  Реализован патерн Singleton.
 *
 */
public class DataExchange {

    /*
    * Единственный экземпляр данного класса
    */
    private static DataExchange instance;

    /*
    * Объект для работы с файлами:
    */
    static private FileManager fileManager;

    /*
    * Объект для работы с базой данных,
    * и Context для нее:
    */
    static private DBManager db;
    //static Context context;

    /**
     * Объект для работы с внешним сервером:
     */
    static ServerManager server;


    /*
    * Путь к файлу с текущим тестом:
    */
    //static String pathToFile;





    /*
    * Скрываем конструктор,
    * для полной инициализации объекта нужно вызвать метод initDataExchange,
    * и дать ему необходимые параметры (Context context, String pathToFile)
    */
    private DataExchange(){

    }

    /*
    * Реализация Singleton c двойной блокировкой:
    *
    * - context     - нужен для работы с БД, обычно передается просто 'this';
    * - pathToFile  - полный путь к файлу с тестом. Если не известен, то передаем 'null'.
    */
    public static DataExchange getInstance(Context context, String pathToFile){
        if(instance == null){
            synchronized (DataExchange.class) {
                if(instance == null){
                    instance = new DataExchange();
                    System.out.println(" --- делаем объект DataExchange");
                }
            }
        }

        // инициализируем dataExchange перед отдачей:
        initDataExchange(context, pathToFile);

        System.out.println(" --- отдаем объект DataExchange");
        return instance;
    }


    /*
    * Метод получения имени файл-теста из БД:
    */
    public static String getPathToFile(String pathToFile){

        return db.getPathToFile();
    }


    /**
     * Метод инициализации объекта
     */
    private static void initDataExchange (Context context, String pathToFile){

        //DataExchange.context = context;

        //DataExchange.pathToFile = pathToFile;

        // создаем объект для работы с базой данных:
        if(DataExchange.db == null)
            DataExchange.db = new DBManager(context);

        // создаем объект для работы с файлами:
        if(DataExchange.fileManager == null)
            DataExchange.fileManager = new FileManager(pathToFile);

        // создаем объект для работы с сервером:
        if(DataExchange.server == null)
            DataExchange.server = new ServerManager();

    }



    ////////////////////////////////////////////////////////////////////////
    /*
    * Далее идут МЕТОДЫ для РАБОТЫ с ФАЙЛАМИ:
    */


    /*
    * Получить текст вопроса по его номеру:
    */
    public String getQuestion(int numberQuestion){

        return fileManager.getQuestion(numberQuestion);
    }

    /**
     * Получить количество вопросов в текущем тесте:
     */
    public int getQuantityAnswers(){

        return fileManager.getQuantityAnswers();
    }

    /**
     * Получить номер правильного ответа:
     */
    public int getTrueAnswer(int numberOfQuestion){

        return fileManager.getTrueAnswer(numberOfQuestion);
    }

    /*
     * Получить содержание варианта ответа определенного опроса,
     * метод получает на вход номер вопроса и номер варианта ответа.
     */
    public String getAnswer(int numberOfQuestion, int numberAnswer){

        return fileManager.getAnswer(numberOfQuestion, numberAnswer);
    }




    ////////////////////////////////////////////////////////////////////
    /*
    * Далее идут МЕТОДЫ для РАБОТЫ с БАЗОЙ ДАННЫХ:
    */


    /*
    * Сформировать и сохранить в БД путь к файлу текущего теста:
    */
    public void setPathToFile(String selectedTest){
        db.createPathToFile(selectedTest);
    }

    /*
    * Получить из БД путь к тест-файлу:
    */
    public String getPathToFile() {

        return db.getPathToFile();
    }

    /*
    * Получить з БД массив с названиями доступных тестов
    * */
    public ArrayList<String> getTestTitles() {
                return db.getTestTitles();
    }


    /*
    * Вставить в БД информацию о новом скачанном тесте:
    */
    public long insertNewTest(String currentTestTitle, String currentFileName, String selectedCategory, String currentAuthor, String currentLinkAuthor, String currentDescription) {
        return db.insertNewTest(currentTestTitle, currentFileName, selectedCategory, currentAuthor, currentLinkAuthor, currentDescription);
    }


    //////////////////////////////////////////////////////////////////
    /*
    * Далее идут МЕТОДЫ для РАБОТЫ с СЕРВЕРОМ:
    */


    /*
    * Получить все категории с сервера:
    */
    public ArrayList[] getCategories() {
        return server.getCategories();
    }


    /*
    * Получить все тесты определенной категории:
    */
    public ArrayList[] getTestsByCategory(Integer category) {
        return server.getTestsByCategory(category);
    }



}
