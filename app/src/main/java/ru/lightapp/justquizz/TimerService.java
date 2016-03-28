package ru.lightapp.justquizz;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;


/**
 * Created by eugen on 20.03.2016.
 *
 */
public class TimerService extends Service {


    /*
    * - Поток в котором выполняется тайерм Clock;
    * - Строка, в которой собираем время в нужном формате;
    * - startId - номер потока сервиса;
    */
    private Thread threadTimer;
    private boolean isInterrupted = false;
    private int startId = 0;

    /*
    * - Объект Handler.
    * - Метод отправляет в Activity состояние таймера;
    */
    private Messenger messageHandler;
    private void sendMessage(String str) {
        Message message = Message.obtain();

        message.obj = str;
        try {
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /*
    * Старт сервиса.
    * - Если еще не запущен подсчет, то запускаем его;
    *   - Получаем handler;
    *   - Стартуем поток;
    */
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.startId = startId;

        if(startId == 1) {
            Bundle extras = intent.getExtras();
            messageHandler = (Messenger) extras.get("MESSENGER");

            Clock clock = new Clock();
            threadTimer = new Thread(clock);
            threadTimer.start();

            System.out.println(" --- старт сервиса onStartCommand " + startId);

        }
        return super.onStartCommand(intent, flags, startId);
    }


    public void onDestroy()
    {
        isInterrupted = true;
        threadTimer.interrupt();

        System.out.println(" --- clock.stopClock & onDestroy()");
        stopSelf(startId);
    }





    /*
    * Таймер-поток, считающий секунды.
    * Отправляет свое состояние в Activity
    */
    private class Clock extends Thread {

        /*
        * Переменные таймера:
        */
        private int hours = 0;
        private int minutes = 0;
        private int seconds = 0;

        /*
        * Флаг указывает, что таймер-поток уже работает:
        */
        private boolean isRun = false;

        /*
        * Строка, в которой собираем время в нужном формате:
        */
        private StringBuilder stringWithTime = new StringBuilder();



        @Override
        public void run() {

            if(!isRun) {
                System.out.println(" --- старт Clock!");
                isRun = true;
                while (!isInterrupted) {

                    countTime();
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        System.out.println(" --- error Thread.sleep in TimerService");
                        e.printStackTrace();
                    }
                }
            }
        }


        /*
        * - Если разрешено, то печатаем время на экран;
        * - делаем seconds++ и обрабатываем полученное значение
        */
        private void countTime() {

                String str = getTime();

                Message msg = new Message();
                msg.obj = str;

                System.out.println(" --- " + str);
                //handler.sendMessage(msg);
                sendMessage(str);


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






    /*
    * Класс TimerTask
    * отсчитывает секунды и передает их значение на экран:

    private class Clock extends TimerTask {

        private boolean ok = false;
        private int hours = 0;
        private int minutes = 0;
        private int seconds = 0;

        /*
        * - Флаг разрешающий подсчет времени;
        * - Флаг разрешающий изменения времени на экране;

        private boolean isRun = true;
        private boolean isPrintingTime = true;

        /*
        * Строка, в которой собираем время в нужном формате:

        private StringBuilder stringWithTime = new StringBuilder();



        @Override
        public void run() {
            if(isRun)
                countTime();

            this.ok = true;
            //System.out.println(" --- seconds in service:" + seconds);
        }

        /*
        * - Если разрешено, то печатаем время на экран;
        * - делаем seconds++ и обрабатываем полученное значение

        private void countTime() {


            if(isPrintingTime) {
                String str = getTime();

                Message msg = new Message();
                msg.obj = str;

                System.out.println(" --- " + str);
                //handler.sendMessage(msg);
                sendMessage(str);
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


        // Сброс значений часов на "ноль":
        public void resetClock(){
            this.hours = 0;
            this.minutes = 0;
            this.seconds = 0;

            System.out.println(" --- обнуление");
        }

        // Остановить подсчет времени:
        public void stopClock(){
            isRun = false;
            System.out.println(" --- TimerService стоп таймера");
        }

        // Запустить подсчет времени:
        public void startClock(){
            isRun = true;
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

        //
        public boolean isOk(){
            return this.ok;
        }
    }
     */



    /*
    public class MyBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
    */

}
