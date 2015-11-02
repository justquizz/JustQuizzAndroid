package ru.lightapp.justquizz.dataexchange;

import java.io.File;

import ru.lightapp.justquizz.db.DBManager;

/**
 * Created by eugen on 20.10.2015.
 *
 * Класс предназначен для работы с файловой системой.
 *
 * Предоставяет интерфейс обращения к файлам.
 *
 * TODO make this class singleton!
 *
 */
public class FileManager {

    /*
    * Единственный экземпляр данного класса
    */
    private static FileManager instance;


    /*
    * Путь к файловой системе
    */
    private String storageDirectory;

    /*
    * Путь к папке с тестами на устройстве:
    */
    private String testDirectory;

    /*
    * Путь к файлу текущего теста:
    */
    private  String pathToFile;




    /*
    * Скрываем конструктор и инициализируем основные переменные:
    */
    private FileManager(){


        /*
        * Получаем путь к файловой системе:
        */
        File root = android.os.Environment.getExternalStorageDirectory();
        storageDirectory = root.getAbsolutePath();

        /*
        * Получаем путь к папке с тестами на устройстве:
        */




    }

    /*
    * Реализация Singleton c двойной блокировкой:
    */
    public static FileManager getInstance(){
        if(instance == null){
            synchronized (FileManager.class) {
                if(instance == null){
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }


    /*
    * GETTERS:
    */


    /*
    * Получаем путь к файловой системе
    */
    public String getStorageDirectory(){

        return this.storageDirectory;
    }


    /*
    * Получаем количество вопросов в тесте:
    */
    public int getQuantityAnswers(){

        return 1;
    }

    /*
    * Поучаем текст вопроса по его номеру:
    */
    public String getQuestion(int numberQuestion){

        return "";
    }



}
