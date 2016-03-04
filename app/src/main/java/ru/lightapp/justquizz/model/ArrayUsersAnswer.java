package ru.lightapp.justquizz.model;

import java.util.ArrayList;

/**
 * Created by Eugen on 22.03.2015.
 *
 * Экземпляр класса хранит текущую информацию:
 * - Массив с ответами на каждыйй вопрос;
 * - Кол-во верных и ошибочных вариантов ответов.
 *
 */
public class ArrayUsersAnswer{

    /*
    * Массив объектов, содержащих информацию об ответах юзера:
    */
    private ArrayList<Answer> answers = new ArrayList<>();

    /*
    * Массив содержащий кол-во верных и ошибочных вариантов ответов юзера:
    */
    public int[] qtyTrueAndFalseAnswers;



    public ArrayUsersAnswer(){

        System.out.println(" --- конструктор ArrayUsersAnswer");

        // Сбрасываем кол-во правильных и неправильных ответов:
        qtyTrueAndFalseAnswers = new int[]{0, 0};

    }




    // GETTERS:

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public int[] getQtyTrueAndFalseAnswers(){
        return qtyTrueAndFalseAnswers;
    }


    ////////////////////////////////////////////////////////////////////


    // SETTERS:


    // Добавить ответ юзера в массив
    public void addAnswerOfUser(Answer answer){
        this.answers.add(answer);
    }

    // вернуть размер массива с ответами:
    public int getSizeAnswerOfUser(){
        return this.answers.size();
    }

    public void incrementRightUserAnswer(){
        this.qtyTrueAndFalseAnswers[0]++;
    }

    public void incrementFalseUserAnswer(){
        this.qtyTrueAndFalseAnswers[1]++;
    }

}
