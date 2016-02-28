package ru.lightapp.justquizz.model;

import android.content.Context;
import java.util.ArrayList;
import ru.lightapp.justquizz.dataexchange.DataExchange;

/**
 * Created by Eugen on 22.03.2015.
 *
 * Класс описывает настройки программы, в основном константы.
 * С начала был Singleton, потом нет.
 *
 * -
 *
 */
public class Init extends Thread {

    private static String questionsFile;  // путь к файлу с вопросами -  path file with the questions
    //private static final String CONFIG_FILE="config.ini"; // имя файла с настройками - name of config file
    private static int qtyQuestions; // количество вопросов в тесте - Quantity questions in a test

    // Массив объектов, содержащих информацию об ответах юзера:
    private static ArrayList<AnswerOfUser> answersOfUser;
        // Массив содержащий кол-во верных и ошибочных вариантов ответов юзера:
    public static int[] qtyTrueAndFalseAnswers;

    /////////////////////////////////////////////////////////////////////////////////////////

    /*
    * Имя файла теста:
    */
    private static String selectedTest;

    /*
    * Путь к файловой системе
    */
    private String path;

    /*
    * Context для создание объекта БД:
    */
    private static Context context;

    /*
    * Путь к папке с тестами:
    * TODO перенести его в базу данных в таблицу init
     */
    public static String directoryMD5 = "/justquizz/tests/";




    /*
    * Метод делает инициализацию приложения в отдельном потоке:
    */
    public static void initialize(Context context, String selectedTest) {

        System.out.println("--- initialize begin...");

        Init.context = context;
        Init.selectedTest = selectedTest;

    }

    private Init(){

        //System.out.println(" --- конструктор Init");

        start();

    }


    @Override
    public void run() {

        System.out.println(" --- запустили поток...");

        DataExchange dataExchange = DataExchange.getInstance(context, selectedTest);
        //dataExchange.initDataExchange(context, selectedTest);

        /**
        * Формируем и сохраняем полный путь к файлу с тестом:
         * Метод под вопросом, путь к файлу передаем в конструкторе DataExchange
         */
        //dataExchange.createPathToFile(selectedTest);

        answersOfUser = new ArrayList<>();

        qtyQuestions = Integer.parseInt(new PropertyItemGetter().getItem("qtyQuestions", questionsFile));
        int qtyAnswers = Integer.parseInt(new PropertyItemGetter().getItem("qtyAnswers", questionsFile));


        // Сбрасываем кол-во правильных и неправильных ответов:
        qtyTrueAndFalseAnswers = new int[]{0, 0};


    }




    // GETTERS:

    public static int getQtyQuestions() {
        return qtyQuestions;
    }


    public static ArrayList<AnswerOfUser> getAnswersOfUser() {
        return answersOfUser;
    }

    public static int[] getQtyTrueAndFalseAnswers(){
        return qtyTrueAndFalseAnswers;
    }

    // SETTERS:



    ////////////////////////////////////////////////////////////////////




    // Добавить ответ юзера в массив
    public static void putAnswerOfUser(AnswerOfUser answerOfUser){
        Init.answersOfUser.add(answerOfUser);
    }

    public  static int getSizeAnswerOfUser(){
        return Init.answersOfUser.size();
    }

    public static void incrementRightUserAnswer(){
        Init.qtyTrueAndFalseAnswers[0]++;
    }

    public static void incrementFalseUserAnswer(){
        Init.qtyTrueAndFalseAnswers[1]++;
    }


}
