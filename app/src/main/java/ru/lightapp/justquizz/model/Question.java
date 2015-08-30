package ru.lightapp.justquizz.model;

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

    private static Question question = null;

    private int numberOfQuestion; // номер текущего вопроса на экране;
    private String titleQuestion; // Сам вопрос
    private int trueAnswer; //  Правильный ответ
    private String[] arrayAnswers = new String[Init.getQtyAnswers() + 1]; // Массив с вариантами ответов

    private Question(){

    }

    public static Question getInstance(){
        if(question == null)
            question = new Question();

        return question;

    }

    /*
    * Метод изменяет состояние объекта, наполняя его СЛЕДУЮЩИМ вопросом
     */
    public void nextQuestion(){

        Init.incrementNumberOfQuestion();
        numberOfQuestion = Init.getNumberOfQuestion();

        titleQuestion = new PropertyItemGetter().getItem("q" + numberOfQuestion, Init.getQuestionsFile());
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
