package ru.lightapp.justquizz.dataexchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.model.PropertyItemGetter;

/**
 * Created by eugen on 20.10.2015.
 *
 * Класс предназначен для работы с файловой системой.
 * Предоставяет интерфейс обращения к файлам.
 *
 * Singleton
 *
 */
public class FileManager {

    /*
    * Единственный экземпляр данного класса
    */
    private static FileManager instance;


    /*
    * Путь к файлу текущего теста:
    */
    private  String pathToFile;

    /*
    * Объект для получения пути к файлу из БД:
    */
    //private DataExchange dataExchange;
    private DBManager db;

    /*
    * Реализация Singleton c двойной блокировкой:
    */
    public static FileManager getInstance(){
        if(instance == null){
            synchronized (FileManager.class) {
                if(instance == null){
                    System.out.println(" --- делаем объект FileManager");
                    instance = new FileManager();
                }
            }
        }
        System.out.println(" --- отдаем объект FileManager");
        return instance;
    }



    /*
    * Инициализируем основные переменные:
    * - путь к текущему тесту,
    */
    private FileManager(){
        System.out.println(" --- конструктор FileManager start ");
        db = DBManager.getInstance(null);

        /*
        * Формируем путь к файлу:
        * [путь_к_файловой_системе]/[directory_MD5]/[имя_тест-файла].jqzz
        */
        String absolutePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        this.pathToFile = absolutePath + db.getPathToFile();

        System.out.println(" --- " + this.pathToFile);

        System.out.println(" --- конструктор FileManager end");

    }

    ///////////////////////////////////////////////////////
    /*
    * GETTERS:
    */


    /*
    * Получаем текст вопроса по его номеру:
    */
    public String getQuestion(int numberQuestion){

        return getDataFromTest("q" + numberQuestion, pathToFile);
    }

    /*
    * Получаем количество вопросов в тесте:
    */
    public int getQuantityAnswers(){

        return Integer.parseInt(getDataFromTest("qtyAnswers", pathToFile));
    }

    /**
     * Получаем номер правильного ответа:
     */
    public int getTrueAnswer(int numberOfQuestion){

        return Integer.parseInt(getDataFromTest("q" + numberOfQuestion + ".true", pathToFile));
    }

    /**
     * Получить содержание варианта ответа определенного опроса,
     * метод получает на вход номер вопроса и номер варианта ответа.
     */
    public String getAnswer(int numberOfQuestion, int numberAnswer){

        return getDataFromTest("q" + numberOfQuestion + "." + numberAnswer, pathToFile);
    }





    /*
    * Метод для извлечения данных из файла с тестами.
    * Используется Properties.
    * Передается два параметра:
    *  - Имя ключа, значение которого нужно получить
    *  - Путь к файлу с данными.
    */
    private String getDataFromTest(String nameItem, String nameFile){

        String item;

        FileInputStream fis = null;
        Reader reader = null;

        try {
            //load a properties file
            fis = new FileInputStream(nameFile);
            reader = new InputStreamReader(fis, "UTF-8");
            Properties propertyFile = new Properties();
            propertyFile.load(reader);

            // get item from file
            item = propertyFile.getProperty(nameItem);

            fis.close();
            reader.close();

        } catch (FileNotFoundException e){
            System.out.println("error: FileNotFoundException!" + nameFile);
            item = null;

        } catch (IOException ex) {
            System.out.println("error: IOException!" + nameFile);
            item = null;

        } finally {
            // Close resource FileInputStream and Reader:
            //fis.close();
            //reader.close();
        }

        return item;
    }

}
