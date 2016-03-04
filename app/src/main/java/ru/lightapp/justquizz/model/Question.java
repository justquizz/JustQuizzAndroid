package ru.lightapp.justquizz.model;

import ru.lightapp.justquizz.dataexchange.FileManager;

/**
 * Created with IntelliJ IDEA.
 * User: Eugen
 *
 * Экземпляр данного класса содержит:
 *  - номер текущего вопроса на экране;
 *  - вопрос, его содержание;
 *  - правильный ответ;
 *  - кол-во вариантов ответов в тесте
 *  - массив с вариантами ответов;
 *
 * - метод nextQuestion() меняет состояние объекта, наполняя его новыми данными
 *
 */
public class Question {

    /*
    * Поля, которые указываеют на элемента активити с TestScreen:
    * - Номер текущего вопроса на экране;
    * - Текст вопроса:
    * - Правильный ответ:
    * - Массив с вариантами ответов:
    */
    private int numberOfQuestion = 0;
    private String titleQuestion;
    private int trueAnswer;
    private String[] arrayAnswers;


    /**
     * Кол-во вариантов ответов в тесте
     */
    private int quantityAnswers;

    /*
    * Кол-во вопросов во всем тесте:
    */
    private int quantityQuestions;

    /*
    * Объект для работы с тест-файлом:
    */
    private FileManager fileManager;


    public Question(){

        fileManager = FileManager.getInstance();

        /*
        * Получаем общее количество вопросов в тесте
        * и создаем массив для хранения вариантов ответов:
        */
        quantityAnswers = fileManager.getQuantityAnswers();
        quantityQuestions = fileManager.getQuantityQuestions();

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

        titleQuestion = fileManager.getQuestion(numberOfQuestion);

        trueAnswer = fileManager.getTrueAnswer(numberOfQuestion);

        // Собираем массив с вариантами ответов
        for(int i = 1; i <= quantityAnswers; i++){
            arrayAnswers[i] = fileManager.getAnswer(numberOfQuestion, i);
            System.out.println(i + " --- " + arrayAnswers[i]);
        }
    }

    /*
    * Метод изменяет состояние объекта, наполняя его ПРЕДЫДУЩИМ вопросом
     */
    public void previousQuestion(){

        numberOfQuestion--;

        titleQuestion = fileManager.getQuestion(numberOfQuestion);

        trueAnswer = fileManager.getTrueAnswer(numberOfQuestion);

        // Собираем массив с вариантами ответов
        for(int i = 1; i <= quantityAnswers; i++){
            arrayAnswers[i] = fileManager.getAnswer(numberOfQuestion, i);
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

    public int getQuantityQuestions() {
        return quantityQuestions;
    }

}
