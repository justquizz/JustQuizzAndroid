package ru.lightapp.justquizz.model;

import ru.lightapp.justquizz.dataexchange.DataExchange;

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

    /**
     * Кол-во вариантов ответов в тесте
     */
    private int quantityAnswers;

    /*
    * Массив с вариантами ответов:
     */
    private String[] arrayAnswers;

    /**
     * Объект для получения и передачи данных:
     */
    private DataExchange dataExchange;



    public Question(){

        //dataExchange = DataExchange.getInstance(null, "");

        /*
        * Получаем общее количество вопросов в тесте
        * и создаем массив для хранения вариантов ответов:
        */
        quantityAnswers = dataExchange.getQuantityAnswers();
        arrayAnswers = new String[quantityAnswers + 1];



    }


    /**
     * Метод изменяет состояние объекта, наполняя его СЛЕДУЮЩИМ вопросом
     * - увеличиваем номер вопроса на 1,
     * - получаем содержание вопроса,
     * - получаем номер правильного ответа,
     * - наполняем массив вариантами ответов(для вывода их на экран).
     */
    public void nextQuestion(){

        numberOfQuestion++;

        titleQuestion = dataExchange.getQuestion(numberOfQuestion);

        trueAnswer = dataExchange.getTrueAnswer(numberOfQuestion);

        // Собираем массив с вариантами ответов
        for(int i = 1; i <= quantityAnswers; i++){
            arrayAnswers[i] = dataExchange.getAnswer(numberOfQuestion, i);
            System.out.println(i + " --- " + arrayAnswers[i]);
        }
    }

    /*
    * Метод изменяет состояние объекта, наполняя его ПРЕДЫДУЩИМ вопросом
     */
    public void previousQuestion(){

        numberOfQuestion--;

        titleQuestion = dataExchange.getQuestion(numberOfQuestion);

        trueAnswer = dataExchange.getTrueAnswer(numberOfQuestion);

        // Собираем массив с вариантами ответов
        for(int i = 1; i <= quantityAnswers; i++){
            arrayAnswers[i] = dataExchange.getAnswer(numberOfQuestion, i);
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
