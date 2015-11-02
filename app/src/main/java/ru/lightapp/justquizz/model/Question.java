package ru.lightapp.justquizz.model;

import ru.lightapp.justquizz.dataexchange.FileManager;

/**
 * Created with IntelliJ IDEA.
 * User: Eugen
 *
 * Экземпляр данного класса содержит:
 *  - номер текущего вопроса на экране;
 *  - вопрос, его содержание;
 *  -  правильный ответ;
 *  - массив с вариантами ответов;
 *
 * - метод nextQuestion() меняет состояние объекта, наполняя его новыми данными
 *
 */
public class Question {

    //private static Question question = null;

    /*
    * Номер текущего вопроса на экране;
    */
    private int numberOfQuestion = 0;

    /*
    * Текст вопроса:
    */
    private String titleQuestion;

    /*
    * Правильный ответ:
    */
    private int trueAnswer;

    /*
    * Массив с вариантами ответов юзера:
     */
    private String[] arrayAnswers;

    /*
    * Объект для работы с файлами:
    */
    FileManager fileManager;



    public Question(){

        fileManager = FileManager.getInstance();

        /*
        * Получаем общее количество вопросов в тесте
        * и создаем массив для хранения ответов юзера:
        */
        int quantityAnswers = fileManager.getQuantityAnswers();
        arrayAnswers = new String[quantityAnswers + 1];



    }

    /*
    public static Question getInstance(){
        if(question == null)
            question = new Question();

        return question;

    }   */



    /*
    * Метод изменяет состояние объекта, наполняя его СЛЕДУЮЩИМ вопросом
     */
    public void nextQuestion(){

        numberOfQuestion++;
        //numberOfQuestion = Init.getNumberOfQuestion();

        //titleQuestion = new PropertyItemGetter().getItem("q" + numberOfQuestion, Init.getQuestionsFile());
        titleQuestion = fileManager.getQuestion(numberOfQuestion);
        trueAnswer = Integer.parseInt(new PropertyItemGetter().getItem("q" + numberOfQuestion + ".true", Init.getQuestionsFile()));

        // Собираем массив с вариантами ответов
        for(int i = 1; i <= Init.getQtyAnswers(); i++){
            arrayAnswers[i] = new PropertyItemGetter().getItem("q" + numberOfQuestion + "." + i, Init.getQuestionsFile());
            System.out.println(i + " --- " + arrayAnswers[i]);
        }
    }

    /*
    * Метод изменяет состояние объекта, наполняя его ПРЕДЫДУЩИМ вопросом
     */
    public void previousQuestion(){

        Init.decrementNumberOfQuestion();
        numberOfQuestion = Init.getNumberOfQuestion();

        titleQuestion = new PropertyItemGetter().getItem("q" + numberOfQuestion, Init.getQuestionsFile());
        trueAnswer = Integer.parseInt(new PropertyItemGetter().getItem("q" + numberOfQuestion + ".true", Init.getQuestionsFile()));

        // Собираем массив с вариантами ответов
        for(int i = 1; i <= Init.getQtyAnswers(); i++){
            arrayAnswers[i] = new PropertyItemGetter().getItem("q" + numberOfQuestion + "." + i, Init.getQuestionsFile());
            System.out.println(i + " --- " + arrayAnswers[i]);
        }
    }


    public int getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public String getTitleQuestion() {
        return titleQuestion;
    }

    public int getTrueAnswer() {
        return trueAnswer;
    }

    public String[] getArrayAnswers() {
        return arrayAnswers;
    }


}
