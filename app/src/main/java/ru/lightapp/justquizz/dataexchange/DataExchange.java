package ru.lightapp.justquizz.dataexchange;

import android.content.Context;

import ru.lightapp.justquizz.db.DBManager;

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
    private FileManager fileManager = FileManager.getInstance();

    /*
    * Context для создания базы данных:
    */
    Context context;

    /*
    * Объект для работы с базой данных:
    */
    private DBManager db = new DBManager(context);




    /*
    * Скрываем конструктор,
    * и создаем необходимые объекты для обмена данными:
    */
    private DataExchange(Context context){

    this.context = context;


    }

    /*
    * Реализация Singleton c двойной блокировкой:
    */
    public static DataExchange getInstance(Context context){
        if(instance == null){
            synchronized (DataExchange.class) {
                if(instance == null){
                    instance = new DataExchange(context);
                }
            }
        }
        return instance;
    }




    /*
    * Далее идут МЕТОДЫ для РАБОТЫ с ФАЙЛАМИ:
    */


    /*
    * Получить текст вопроса по его номеру:
    */
    public String getQuestion(int numberQuestion){

        return "";
    }





    /*
    * Далее идут МЕТОДЫ для РАБОТЫ с БАЗОЙ ДАННЫХ:
    */


    /*
    * Сформировать и сохранить в БД путь к файлу текущего теста:
    */
    public void createPathToFile(String selectedTest){
        db.createPathToFile(selectedTest);
    }
    


}
