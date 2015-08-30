package ru.lightapp.justquizz.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
* Created by Eugen on 19.04.2015.
*/
public class AnswerOfUser {

    // здесь хранится номер вопроса, на который отвечал пользователь:
    private int numberOfQuestion;

    // массив вариантов ответов - отметил ли юзер тот или иной вариант.
    // необходим для вывода на экран сохраненного ответа юзера:
    public Boolean[] checked = new Boolean[4];

    // флаг-признак, правильно ли юзер ответил на вопрос:
    private boolean isRightUserAnswer;

    // Время затрачнноей юзером на ответ:
    private String time;






    // SETTERS & GETTERS:

    public void setNumberOfQuestion(int numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }

    public void setRightUserAnswer() {
        this.isRightUserAnswer = true;
    }


    public int getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public Boolean[] getChecked() {
        return checked;
    }

    public boolean isRightUserAnswer() {
        return isRightUserAnswer;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }
}
