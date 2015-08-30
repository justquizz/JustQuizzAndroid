package ru.lightapp.justquizz.controller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.model.AnswerOfUser;
import ru.lightapp.justquizz.model.Init;

/*
 * Created by Eugen on 26.04.2015.
 *
 * Активити выводит результаты теста на экран.
 *
 */
public class ShowUserResult extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_test_result);


        TextView textField = (TextView) findViewById(R.id.result);
        StringBuilder stringWithResult = new StringBuilder();

        /*
        * Собираем строку для вывода на экран
        */
        stringWithResult.append(getString(R.string.text_your_result)); // "Ваши результаты: \n"
        for(AnswerOfUser oneItem : Init.getAnswersOfUser()){


            stringWithResult.append(getString(R.string.text_number_of_question)); // "Вопрос №"
            stringWithResult.append(oneItem.getNumberOfQuestion()); // Присоединяем номер вопроса
            stringWithResult.append(" - ");

            if(oneItem.isRightUserAnswer())
                stringWithResult.append("верно - " + oneItem.getTime() + ".\n");
            else
                stringWithResult.append("не верно - " + oneItem.getTime() + ".\n");
        }

        stringWithResult.append("\n \n");
        stringWithResult.append("Верных ответов - ");
        stringWithResult.append(Init.getQtyTrueAndFalseAnswers()[0]);
        stringWithResult.append("\n");
        stringWithResult.append("Неправильных ответов - ");
        stringWithResult.append(Init.getQtyTrueAndFalseAnswers()[1]);

        System.out.println(stringWithResult);

        textField.setText(stringWithResult);

    }
}
