package ru.lightapp.justquizz.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.os.Handler;
import android.os.Message;
import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.TimerService;
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
    * - Поле с текстом вопроса;
    * - Поле, в котором тикают секунды и считают время ответа юзера;
    * - Кнопка "Ответить/Далее";
    * - Системный цвет кнопок;
    */
    private TextView nameQuestion;
    private static TextView textTimer;
    private Button button_next;
    private Drawable backgroundColor;

    /* TODO Разделить кнопку на две: "Ответить" и "Далее"
    * - экземпляр класса сдержит информацию о текущем вопросе;
    * - Масссив содержачий все checkbox;
    * - Флаг "Был ли обработан ответ юзера" (нажата кнопка "Ответить");
    * - Флаг ошибся ли юзер в ответе;
    * */
    private Question question;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>(4);
    private boolean isUserReply = false;
    private boolean rightAnswer = false;

    /*
    * - Сервис, считающий время ответа;
    * - handler для обмена данными с сервисом;
    * - Флаг, разрешающий вывод времени на экран;
    * - Флаг, указывающий, что таймер-сервис запущен;
    */
    private Intent timerService;
    private static Handler messageHandler = new MessageHandler();
    private static boolean printTime = false;
    private boolean isTimerRun = false;

    /*
    * Объект, содержащий все ответы и информацию о тесте:
    */
    private  ArrayUsersAnswer arrayUsersAnswer = new ArrayUsersAnswer();

    /*
    * Флаг, разрешающий увеличить в БД количество раз прохождения теста
    */
    private boolean isPermissionIncrementTrue = true;

    // Создаем Handler  для передачи секунд из потока в поток:
    private static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            if(printTime) {
                String text = (String) message.obj;
                textTimer.setText(text);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_screen);

        /*
        * - Инкрементруем количество запусков теста в БД;
        * - Выставляем флаг разрешение;
        */
        DBManager db = DBManager.getInstance(this);
        db.incrementStartTest();
        isPermissionIncrementTrue = true;

        /*
        * - Создаем, но пока не запускаем таймер-сервис;
        * - Передаем в него handler;
        */
        timerService = new Intent(this, TimerService.class);
        timerService.putExtra("MESSENGER", new Messenger(messageHandler));


        // Здесь получаем null, а заполнение объекта происходит методом question.nextQuestion();
        //question = Question.getInstance();
        question = new Question();


        /*
        * Описываем элементы на экране:
        */
        nameQuestion = (TextView) findViewById(R.id.nameQuestion);
        nameQuestion.setTextColor(getResources().getColor(R.color.text_question_color));
        //nameQuestion.setEnabled(true);
        //nameQuestion.setMinLines(3);
        //nameQuestion.setMaxLines(10);

        textTimer = (TextView) findViewById(R.id.timer);
        CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        button_next = (Button) findViewById(R.id.button_next);

        checkBoxes.add(checkBox1);
        checkBoxes.add(checkBox2);
        checkBoxes.add(checkBox3);
        checkBoxes.add(checkBox4);


        // Запоминаем фон кнопки, цвет системы:
        backgroundColor = button_next.getBackground();

        /*
        * Получаем текст первого вопроса и варианты ответов,
        * и загружаем их на экран:
        */
        question.nextQuestion();
        loadNextQuestion();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 55)
            TestScreen.this.finish();
    }


    /*
    * Обработка нажатия аппаратной кнопки НАЗАД.
    */
    @Override
    public void onBackPressed() {

        openQuitDialog();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopService(timerService);
        //System.out.println(" --- onDestroy() in TestScreen.class");
    }


    @Override
    public void finish(){
        super.finish();
        stopService(timerService);
        //System.out.println(" --- finish() in TestScreen.class");
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

                /*
                * Создаем объект, который будет содержать всю информацию об ответе юзера.
                * И далее заполняем его.
                */
                Answer answer = new Answer();

                /*
                * Работаем со временем:
                * - Сохраняем время;
                * - Выводим на экран сохраненное время;
                * - Останавливаем часы считающие секунды;
                * - Сбрасываем флаг, что таймер-сервис запущен;
                */
                answer.setTime(textTimer.getText().toString());
                textTimer.setText(answer.getTime());
                stopService(timerService);
                isTimerRun = false;

                // Сохраняем номер вопроса:
                answer.setNumberOfQuestion(question.getNumberOfQuestion());


                // устанавливаем флажок, что юзер дал верный ответ:
                if(checkBoxes.get(question.getTrueAnswer() - 1).isChecked()) {
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
                    button_next.setBackgroundColor(getResources().getColor(R.color.background_right_answer));
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
                // Запускаем экран с результатом и ждем от него команду:
                Intent intent = new Intent(TestScreen.this, ShowUserResult.class);
                startActivityForResult(intent, 77);
            }else {
                /*
                * Т.к. это не поледний вопрос в тесте, то:
                *  - Проверяем, отвечал ли юзер ранее на этот вопрос, если ДА,
                *   то выводим сохраненный вариант ответа на экран:
                *   иначе:
                *  - выставляем флаг, что ответ еще не обработан;
                *  - текст на кнопке меняем на "Ответить";
                *  - перерисовываем экран методом loadNextQuestion();
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
    * - записываем ее в БД;
    * - если есть разрешение, то увеличиваем кол-во
    */
    private void saveTestResult() {

        String result = buildTheString();

        //Объект для работы с БД:
        DBManager db = DBManager.getInstance(this);
        db.saveTestResult(result);

        if(isPermissionIncrementTrue) {
            db.incrementEndTest();
            isPermissionIncrementTrue = false;
        }
    }

    /*
    * Собираем строку для вывода на экран:
    * - строка содержит номер вопроса и указание "верно" или "не верно";
     * - общее кол-во правильных и неправильных ответов.
    */
    private String buildTheString() {

        StringBuilder stringWithResult = new StringBuilder();

        stringWithResult.append("<b>");
        stringWithResult.append(getString(R.string.text_your_result)); // "Ваши результаты:"
        stringWithResult.append("</b> <br/> <br/>");
        //stringWithResult.append("<br/> <br/>"); // "Ваши результаты: \n"

        for(Answer oneItem : arrayUsersAnswer.getAnswers()){

            stringWithResult.append(getString(R.string.text_number_of_question)); // "Вопрос №"
            stringWithResult.append(oneItem.getNumberOfQuestion()); // Присоединяем номер вопроса
            stringWithResult.append(" - ");

            if(oneItem.isRightUserAnswer()) {
                stringWithResult.append("верно - ");
                stringWithResult.append(oneItem.getTime());
                stringWithResult.append(".<br/>");
            }
            else {
                stringWithResult.append("не верно - ");
                stringWithResult.append(oneItem.getTime());
                stringWithResult.append(".<br/>");
            }
        }

        stringWithResult.append("<br/>");
        stringWithResult.append("Правильных ответов - ");
        stringWithResult.append(arrayUsersAnswer.getQtyTrueAndFalseAnswers()[0]);
        stringWithResult.append("<br/>");
        stringWithResult.append("Неправильных ответов - ");
        stringWithResult.append(arrayUsersAnswer.getQtyTrueAndFalseAnswers()[1]);

        //System.out.println(" --- на экран: " + stringWithResult);

        return stringWithResult.toString();

    }


    /*
    *   Обработчик нажатия клавиши "Назад"
    */
    public void onClickPrevious(View view){

        if(question.getNumberOfQuestion() == 1){
            /*
            * Подтверждение прерывания теста,
            * т.е. во время первого вопроса нажата клавиша НАЗАД
            */
            openQuitDialog();

        }else {

            /*
            * Если это не вопрос№1, то загружаем уже сохраненный ответ юзера:
            */
            // Делаем запрет на изменение TextField timer
            printTime = false;

            question.previousQuestion();
            loadSavedUserAnswer();
        }
    }


    /*
    * Подтверждение прерывания теста,
    * - устанавливаем слушатель на кнопку ДА;
    * - устанавливаем слушатель на кнопку НЕТ;
    */
    private void openQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.string_do_u_wanna_finish_test).setCancelable(false).
                setNegativeButton(R.string.string_yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        stopService(timerService);
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void loadNextQuestion(){

        // Стартуем таймер:
        // Разрешаем печатать время на экран!
        printTime = true;
        if(!isTimerRun) {
            //System.out.println(" --- !isTimerRun = false");
            startService(timerService);
            isTimerRun = true;
        }else{
            //System.out.println(" --- isTimerRun = true");
        }

        // возвращаем цвет кнопки на системный
        button_next.setBackground(backgroundColor);

        clearCheckBoxAndSetTitleOfQuestion();

        textTimer.setText(R.string.timer);

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
        textTimer.setText(savedTime);
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



}
