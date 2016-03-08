package ru.lightapp.justquizz.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.os.Handler;
import android.os.Message;
import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.dataexchange.DBManager;
import ru.lightapp.justquizz.model.*;


/**
 * Created by Eugen on 22.03.2015.
 *
 * Класс управляет окном с вопросом и вариантами ответа
 *
 */
public class TestScreen extends Activity {

    /*
    * Элементы экрана:
    */
    private TextView nameQuestion;  // Поле с текстом вопроса
    private TextView timer;         // Поле, в котором тикают секунды и считают время ответа юзера
    private Button button_next;     // Кнопка "Ответить/Далее"

    private Question question;      // экземпляр класса сдержит информацию о текущем вопросе
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>(4); // Масссив содержачий все checkbox


    // Флаг "Был ли обработан ответ юзера" (нажата кнопка "Ответить")
    private boolean isUserReply = false;

    // Флаг ошибся ли юзер в ответе:
    private boolean rightAnswer = false;

    //  Поток считающий затраченное время на ответ каждого вопроса
    Clock clock;

    // Создаем Handler  для передачи секунд из потока в поток:
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String text = (String) msg.obj;
            timer.setText(text);
        }
    };


    /*
    * Объект, содержащий все ответы и информацию о тесте:
    */
    private  ArrayUsersAnswer arrayUsersAnswer = new ArrayUsersAnswer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_screen);

        // Здесь получаем null, а заполнение объекта происходит методом question.nextQuestion();
        //question = Question.getInstance();
        question = new Question();


        /*
        * Описываем элементы на экране:
        */
        nameQuestion = (TextView) findViewById(R.id.nameQuestion);
        timer = (TextView) findViewById(R.id.timer);
        CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        button_next = (Button) findViewById(R.id.button_next);

        checkBoxes.add(checkBox1);
        checkBoxes.add(checkBox2);
        checkBoxes.add(checkBox3);
        checkBoxes.add(checkBox4);

        // Создаем часы:
        clock = new Clock();

        /*
        * Получаем текст первого вопроса и варианты ответов,
        * и загружаем их на экран:
        */
        question.nextQuestion();
        loadNextQuestion();
    }

    /*
    *   Обработчик нажатия клавиши "Ответить/Далее"
    *
    *   Правая на экране имеет двойную функцию - сначала она работает как клавиша ответа на вопрос
    *   и имеет надпись "Ответить", после нажатия на нее происходит ее обработка. И в случае успешной
    *   обработки на ней появляется надпись "Далее" - при нажатии на которую происходит
    *   обработка и вывод на экран следующего вопроса из теста.
    *
    *   Распознание первого и второго нажатия происходит посредством флага "boolean isUserReply"
    *
    */
    public void onClickNext(View view){

        if(!isUserReply){

            /*
            *    Обрабатываем нажатие "Ответить".
            *
            *   т.е. юзер еще не дал ответ на вопрос:
            */


            /*
            * Делаем проверку, выбрал ли пользователь какой-либо вариант ответа:
            */
            boolean isChecked = false;
            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isChecked()){
                    isChecked = true;
                    break;
                }
            }
                // Если какой-либо вариант был выбран, то обрабатываем ответ:
            if(isChecked) {

                // Создаем объект, который будет содержать всю информацию об ответе юзера:
                Answer answer = new Answer();

                /*
                * Работаем со временем:
                * - Останавливаем часы считающие секунды;
                * - сохраняем время;
                * - выводим на экран сохраненное время;
                * - обнуляем счетчик;
                */
                clock.stopClock();
                answer.setTime(clock.getTime());
                timer.setText(answer.getTime());
                clock.resetClock();


                // Сохраняем номер вопроса:
                answer.setNumberOfQuestion(question.getNumberOfQuestion());


                // устанавливаем флажок, что юзер дал верный ответ:
                if(checkBoxes.get(question.getTrueAnswer() - 1).isChecked()) {
                    //System.out.println("You make right choice" + " --- ");
                    rightAnswer = true;
                }


                // проходим в цикле по всем чекбоксам:
                for (int i = 0; i < checkBoxes.size(); i++) {

                    // если чекбокс отмечен, то:
                    if(checkBoxes.get(i).isChecked()) {

                        // Если ответ правильный отмечаем "true", (цвет фона меняем позже):
                        if (i == question.getTrueAnswer() - 1) {
                            answer.checked[i] = true;
                        }

                        // Если ответ неправильный выделяем его, отмечаем "false" и сбрасываем флаг правильного ответа:
                        if (i != question.getTrueAnswer() - 1) {
                            checkBoxes.get(i).setBackgroundColor(getResources().getColor(R.color.background_wrong_answer));
                            answer.checked[i] = false;
                            rightAnswer = false;
                        }
                    }
                }

                /*
                * Если юзер дал верный ответ, то инкрементируем кол-во правильных ответов @ArrayUsersAnswer
                * и помечаем флаг вопроса - true,
                * иначе - инкрементируем кол-во непраправильных:
                */
                if(rightAnswer) {
                    arrayUsersAnswer.incrementRightUserAnswer();
                    answer.setRightUserAnswer();
                }
                else
                    arrayUsersAnswer.incrementFalseUserAnswer();


                // Выделяем правильный вариант:
                checkBoxes.get(question.getTrueAnswer() - 1).setBackgroundColor(getResources().getColor(R.color.background_right_answer));
                // Кладем ответ пользователя в массив:
                arrayUsersAnswer.addAnswerOfUser(answer);
                // устанавливаем флажок, что ответ юзера был обработан
                isUserReply = true;
                // Меняем надпись на кнопке
                button_next.setText(R.string.button_next);
            }
                // Если же выбранного варианта нет, то сообщаем об этом:
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.toast_must_make_choice, Toast.LENGTH_SHORT);
                toast.show();
            }



        }else{
            /*
            *    Обрабатываем нажатие "Далее"
            */


            /*
            * Если это последний вопрос теста, то
            * - записываем результат в БД;
            * - открываем окно с результатами.
            */
            if(question.getNumberOfQuestion() >= question.getQuantityQuestions()){

                saveTestResult();

                Intent intent = new Intent(TestScreen.this, ShowUserResult.class);
                startActivity(intent);
            }else {
                /*
                * Т.к. это не поледний вопрос в тесте, то:
                *  - Проверяем, отвечал ли юзер ранее на этот вопрос, если ДА,
                *   то выводим сохраненный вариант ответа на экран:
                *   иначе:
                *  - выставляем флаг, что ответ еще не обработан;
                *  - текст на кнопке меняем на "Ответить";
                *  - перисовываем экран методом loadNextQuestion();
                */

                if(arrayUsersAnswer.getSizeAnswerOfUser() > question.getNumberOfQuestion()){

                    question.nextQuestion();
                    loadSavedUserAnswer();

                }else {

                    isUserReply = false;
                    button_next.setText(R.string.button_answer);

                    // Получаем текст СЛЕДУЮЩЕГО вопроса и варианты ответов:
                    question.nextQuestion();
                    loadNextQuestion();

                }
            }
        }

    }

    /*
    * Метод сохраняет результат теста в виде строки в БД.
    * - получаем строку с результатом теста;
    * - записываем ее в БД.
    */
    private void saveTestResult() {

        String result = buildTheString();

        //Объект для работы с БД:
        DBManager db = DBManager.getInstance(this);
        db.saveTestResult(result);
    }

    /*
    * Собираем строку для вывода на экран:
    * - строка содержит номер вопроса и указание "верно" или "не верно";
     * - общее кол-во правильных и неправильных ответов.
    */
    private String buildTheString() {

        StringBuilder stringWithResult = new StringBuilder();


        stringWithResult.append(getString(R.string.text_your_result)); // "Ваши результаты: \n"
        for(Answer oneItem : arrayUsersAnswer.getAnswers()){


            stringWithResult.append(getString(R.string.text_number_of_question)); // "Вопрос №"
            stringWithResult.append(oneItem.getNumberOfQuestion()); // Присоединяем номер вопроса
            stringWithResult.append(" - ");

            if(oneItem.isRightUserAnswer())
                stringWithResult.append("верно - " + oneItem.getTime() + ".\n");
            else
                stringWithResult.append("не верно - " + oneItem.getTime() + ".\n");
        }

        stringWithResult.append("\n \n");
        stringWithResult.append("Правильных ответов - ");
        stringWithResult.append(arrayUsersAnswer.getQtyTrueAndFalseAnswers()[0]);
        stringWithResult.append("\n");
        stringWithResult.append("Неправильных ответов - ");
        stringWithResult.append(arrayUsersAnswer.getQtyTrueAndFalseAnswers()[1]);

        System.out.println(" --- на экран: " + stringWithResult);

        return stringWithResult.toString();

    }


    /*
    *   Обработчик нажатия клавиши "Назад"
    */
    public void onClickPrevious(View view){


        if(question.getNumberOfQuestion() == 1){
            /*
            * Подтверждение прерывания теста,
            * т.е. во время первого вопроса нажата клавиша НАЗАД:
            * - устанавливаем слушательн на кнопку ДА;
            * - устанавливаем слушательн на кнопку НЕТ;
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.string_do_u_wanna_finish_test).setCancelable(false).
                    setNegativeButton(R.string.string_yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            TestScreen.this.finish();
                        }


                    }).
                    setPositiveButton(R.string.string_no, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }

                    })
                    ;
            AlertDialog alert = builder.create();
            alert.show();


            //Toast toast = Toast.makeText(getApplicationContext(),
            //        R.string.toast_do_u_wanna_finish_test, Toast.LENGTH_SHORT);
            //toast.show();
            //this.finish();
        }else {

            /*
            * Если это не вопрос№1, то загружаем уже сохраненный ответ юзера:
            */
            clock.stopPrintingTime();
            question.previousQuestion();
            loadSavedUserAnswer();
        }
    }

    /*
    * Метод "очищает" chekbox'ы - снимат галочку, делает нейтральный фон
    * и заполняет поле с названием вопроса.
    */
    private  void clearCheckBoxAndSetTitleOfQuestion(){

        // Делаем наши checkbox без галочек и с белым фоном:
        for(CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
            checkBox.setBackgroundColor(getResources().getColor(R.color.background_answer_field));
        }

        // Заполняем текстом поле вопроса:
        nameQuestion.setText("Вопрос №" + question.getNumberOfQuestion() + ": \n " + question.getTitleQuestion());
    }


    /*
    *   Метод наполняет элементы экрана содержимым - т.е. следующим вопросом.
    *
    */
    private void loadNextQuestion(){

        // Запускаем часы и разрешаем вывод значений на экран:
        clock.startClock();
        clock.startPrintingTime();

        clearCheckBoxAndSetTitleOfQuestion();

        timer.setText("00:00");

        // Заполняем checkbox текстом:
        for(int i = 0; i < checkBoxes.size(); i++)
            checkBoxes.get(i).setText(question.getArrayAnswers()[i+1]);

    }


    /*
    * Метод загружает на экран сохраненный вариант ответа юзера
    */
    private void loadSavedUserAnswer(){

        // Выводим на экран сохраненное время ответа на данный вопрос:
        String savedTime = arrayUsersAnswer.getAnswers().get(question.getNumberOfQuestion() - 1).getTime();
        timer.setText(savedTime);
        clearCheckBoxAndSetTitleOfQuestion();

        /*
        * Проходим в цикле по всем checkbox'ам и заполняеем их информацией,
        * которая хранится в массиве ArrayUsersAnswer.getAnswers():
        * - ставим галочку там, где это необходимо;
        * - делам нужный фон, красный ошибка - зеленый верно;
         */

        for(int i = 0; i < checkBoxes.size(); i++) {
            // Заполняем checkbox текстом:
            checkBoxes.get(i).setText(question.getArrayAnswers()[i + 1]);

            // Это не выбранный юзером вариант, его не обрабатываем:
            if(arrayUsersAnswer.getAnswers().get(question.getNumberOfQuestion() - 1).getChecked()[i] == null) {
                continue;
            }

            // Правильный ответ юзера - ставим галочку,
            // (цвет фона правильного ответа устанавливается далее):
            // (изменить это место вставив else)
            if(arrayUsersAnswer.getAnswers().get(question.getNumberOfQuestion() - 1).getChecked()[i]) {
                checkBoxes.get(i).setChecked(true);
                //checkBoxes.get(i).setBackgroundColor(getResources().getColor(R.color.background_right_answer));
                continue;
            }
            // Неправильный вариант юзера - ставим галочку и меняем цвет фона
            if(!arrayUsersAnswer.getAnswers().get(question.getNumberOfQuestion() - 1).getChecked()[i]) {
                checkBoxes.get(i).setChecked(true);
                checkBoxes.get(i).setBackgroundColor(getResources().getColor(R.color.background_wrong_answer));
                //continue;
            }
        }

        // Выделяем правильный вариант:
        checkBoxes.get(question.getTrueAnswer() - 1).setBackgroundColor(getResources().getColor(R.color.background_right_answer));

        // Выставляе флаг, что юзер дал ответ на вопрос и меняем надпись на кнопке:
        isUserReply = true;
        button_next.setText(R.string.button_next);

    }



    public class Clock extends Thread {

        private int hours;
        private int minutes;
        private int seconds;

        private boolean isStart = true;
        private boolean isPrintingTime = true;

        private StringBuilder stringWithTime = new StringBuilder();

        public Clock() {

            resetClock();
            start();
        }


        public void run() {

            try {
                while (true){

                    if(isStart){
                        countTime();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println(" --- something wrong....hm");
            }
        }


        private void countTime() throws InterruptedException {

            if(isPrintingTime) {
                String str = getTime();

                Message msg = new Message();
                msg.obj = str;
                handler.sendMessage(msg);
            }

            seconds++;

            if (seconds >= 60) {
                seconds = 0;
                minutes++;
            }

            if (minutes >= 60) {
                minutes = 0;
                hours++;
            }

            if (hours >= 24) {
                hours = 0;
            }
        }


        // Сброс значений часов на "ноль":
        public void resetClock(){
            this.hours = 0;
            this.minutes = 0;
            this.seconds = 0;

            System.out.println(" --- обнуление");
        }

        // Остановить подсчет времени:
        public void stopClock(){
            isStart = false;
            System.out.println(" --- стоп таймера");
        }

        // Запустить подсчет времени:
        public void startClock(){
            isStart = true;
            System.out.println(" --- старт таймера");
        }

        // Разрешение вывода значений таймера на экран:
        public void startPrintingTime(){
            isPrintingTime = true;

        }

        // Запрет вывода значений таймера на экран:
        public void stopPrintingTime(){
            isPrintingTime = false;

        }

        // Получить значение часов:
        // Собираем строку в формате 00:05 или 01:05:00:
        public String getTime(){

            stringWithTime.setLength(0);

            if(hours > 0 && hours < 10){
                stringWithTime.append("0");
                stringWithTime.append(hours);
                stringWithTime.append(":");
            }

            if(hours > 0 && hours > 10){
                stringWithTime.append(hours);
                stringWithTime.append(":");
            }

            if(minutes == 0){
                stringWithTime.append("00");
                stringWithTime.append(":");
            }else {

                if (minutes < 10) {
                    stringWithTime.append("0");
                    stringWithTime.append(minutes);
                    stringWithTime.append(":");
                }

                if (minutes >= 10) {
                    stringWithTime.append(minutes);
                    stringWithTime.append(":");
                }
            }


            if(seconds < 10){
                stringWithTime.append("0");
                stringWithTime.append(seconds);
            }

            if(seconds >= 10){
                stringWithTime.append(seconds);
            }
            return stringWithTime.toString();
        }
    }
}
