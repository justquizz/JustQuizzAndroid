package ru.lightapp.justquizz.model;

import android.content.Context;

import java.util.ArrayList;

import ru.lightapp.justquizz.dataexchange.DataExchange;
import ru.lightapp.justquizz.dataexchange.FileManager;
import ru.lightapp.justquizz.db.DBManager;


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

    private static Init init = null;

    private static final String SERVER = "http://lightapp.ru/justquizz/";

    //private static final String StorageDirectory;


    private static String questionsFile;  // путь к файлу с вопросами -  path file with the questions
    //private static final String CONFIG_FILE="config.ini"; // имя файла с настройками - name of config file
    private static int qtyQuestions; // количество вопросов в тесте - Quantity questions in a test
    private static int qtyAnswers; // количество вариантов ответов в каждом вопросе - Quantity answer in each questions

        // Имя пользователя:
    private String nameUser;
    //private static String titleTest; // Название теста выбранного юзером
        // Номер выбранного теста, файл /test[XXX].ini
    private static String numberOfTest;
        // Номер текущего вопроса
    private static int numberOfQuestion;
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

        if(init == null) {
            init = new Init();
        }
    }

    private Init(){

        //System.out.println(" --- конструктор Init");

        start();

    }


    @Override
    public void run() {

        //System.out.println(" --- запустили поток...");

        DataExchange dataExchange = DataExchange.getInstance(context);

        /*
        * Формируем и сохраняем полный путь к файлу с тестом:
        */
        dataExchange.createPathToFile(selectedTest);






        //Init.titleTest = titleTest; // Название теста
        //numberOfTest = parseNumberOfTest(titleTest); // Парсим в строке цифру - номер теста
        //questionsFile = Tests.PATH_OF_TEST_FILE + numberOfTest + ".ini"; // собираем путь к файлу с тестом

        answersOfUser = new ArrayList<>();

        qtyQuestions = Integer.parseInt(new PropertyItemGetter().getItem("qtyQuestions", questionsFile));
        qtyAnswers = Integer.parseInt(new PropertyItemGetter().getItem("qtyAnswers", questionsFile));





        // Сбрасывем номер вопроса на начало:
        numberOfQuestion = 0;

        // Сбрасываем кол-во правильных и неправильных ответов:
        qtyTrueAndFalseAnswers = new int[]{0, 0};


    }












    // Метод парсит строку, вытаскивая из названия выбранного теста цифры - номер теста:
    private String parseNumberOfTest(String titleTest) {
        String point = ".";
        String newStr = "";
        int numPoint = titleTest.indexOf(point);
        for(int i = 0; i < numPoint; i++){
            newStr += titleTest.charAt(i);
        }
        return newStr;
    }


    public static Init getInstance(String titleTest){

        init = new Init();
        return init;
    }




    // GETTERS:

    public static int getNumberOfQuestion() {

        return numberOfQuestion;
    }

    public static String getQuestionsFile() {
        return questionsFile;
    }

    public static int getQtyQuestions() {
        return qtyQuestions;
    }

    public static int getQtyAnswers() {
        return qtyAnswers;
    }

    //public String getNameUser() {
    //    return nameUser;
    //}

    public static ArrayList<AnswerOfUser> getAnswersOfUser() {
        return answersOfUser;
    }

    public static int[] getQtyTrueAndFalseAnswers(){
        return qtyTrueAndFalseAnswers;
    }

    public static String getSERVER() {
        return SERVER;
    }

    // SETTERS:

    public static void setFileName(String fileName){
        Init.selectedTest = fileName;
    }


    ////////////////////////////////////////////////////////////////////

    public static void incrementNumberOfQuestion() {
       numberOfQuestion++;
    }

    public static void decrementNumberOfQuestion() {
        numberOfQuestion--;
    }

    //public static void resetNumberOfQuestion() {
    //    numberOfQuestion = 0;
    //}

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    //public static void setTitleTest(String titleTest) { Init.titleTest = titleTest; }


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
