package ru.lightapp.justquizz.controller;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.dataexchange.DBManager;

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
        setContentView(R.layout.show_user_result);


        TextView textField = (TextView) findViewById(R.id.result);

        DBManager db = DBManager.getInstance(this);
        String stringWithResult = db.getSavedResult();
        String quantityStart = db.getQuantityStart();
        String quantityEnd = db.getQuantityEnd();


        StringBuilder stringBuilder = new StringBuilder(stringWithResult);
        stringBuilder.append("<br/> <br/>");
        stringBuilder.append("<b> Статистика данного теста: </b> <br/>");
        stringBuilder.append("Был запущен ");
        stringBuilder.append(quantityStart);
        stringBuilder.append(getCount(quantityStart));
        stringBuilder.append("<br/>");
        stringBuilder.append("Был пройден до конца ");
        stringBuilder.append(quantityEnd);
        stringBuilder.append(getCount(quantityEnd));


        textField.setText(Html.fromHtml(stringBuilder.toString()));
    }


    /*
    * Метод различает, где нужно вставить "раз", а где "раза".
    * Например, "5 раз", либо "2 раза", либо "12 раз".
    * Логика: Если последняя цифра числа 2, 3, или 4, то return "раза".
    * Исключение: ХХХ12, ХХ13, Х14.
    */
    private String getCount(String numString){

        String count =" раз.";
        int length = numString.length();
        Character char1, char2;
        int num = 0;
        try {
            char1 = numString.charAt(length - 1);
            char2 = numString.charAt(length - 2);
            String str3 = String.valueOf(char2) + String.valueOf(char1);
            num = Integer.parseInt(str3);
            System.out.println(" --- str=" + str3 + " num=" + num);
        }catch (StringIndexOutOfBoundsException e){
            System.out.println(" --- число меньше 10");
        }

        if(num == 12 || num == 13 || num == 14) {
            count =" раз.";
        }else{
            char1 = numString.charAt(length - 1);
            num = Integer.parseInt(String.valueOf(char1));

            if (num == 2 || num == 3 || num == 4)
                count = " раза.";
        }
        return count;
    }
}
