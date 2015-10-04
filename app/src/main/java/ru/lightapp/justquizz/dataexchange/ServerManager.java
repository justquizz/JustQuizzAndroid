package ru.lightapp.justquizz.dataexchange;

import android.os.AsyncTask;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Eugen on 20.07.2015.
 *
 * Экземпляр класса предоставляет доступ к API сервера с тестами.
 * TODO make Singleton.
 *
 */
public class ServerManager {

    private String urlGetCategories = "http://lightapp.ru/justquizz/api.php?void=get_categories";
    private String urlGetTestsByCategory = "http://lightapp.ru/justquizz/api.php?void=get_tests&category=";


    public ArrayList[] getCategories(){
        //System.out.println(" --- start xml");
        long start = new Date().getTime();

        ArrayList[] categories = null;
        /*
        * Запускаем отдельнй поток, который получит xml-file с сервера,
        * разберет его и вернет все в виде массива.
        *
        * Затем возвращаем полученные массивы в активити (или куда там еще...)
         */
        GetCategoriesThread tread = new GetCategoriesThread();
        tread.execute();
        try {
            // Ждем и получаем результат работы потока:
            categories =  tread.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println(((new Date().getTime()) - start) + " ms --- xml processing......");

        return categories;
    }

    class GetCategoriesThread extends AsyncTask<Void, Void, ArrayList[]>{

        @Override
        protected ArrayList[] doInBackground(Void... voids) {
            /*
            * Готовим массивы:
            * - объект, содержащий два ArrayList'a;
            * - один ArrayList - содержит названия категорий;
            * - второй - описания каждой категории;
            * - третий - возможные ошибки.
             */
            ArrayList[] categories = new ArrayList[3];
            ArrayList<String> title = new ArrayList<>();
            ArrayList<String> description = new ArrayList<>();
            ArrayList<String> error = new ArrayList<>();

            try{
                /*
                готовим API, позволяющий выполнять разбор документа
                загружаем в парсер полученный ответ и вызываем метод parse
                */
                URL url = new URL(urlGetCategories);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Читаем из потока xml-данные:
                Document doc = db.parse(new InputSource(url.openStream()));
                // Что это?
                doc.getDocumentElement().normalize();
                // Получаем массив со всеми тегами <category />:
                NodeList nodeList = doc.getElementsByTagName("category");

                /*
                * Проходим в цикле по массиву с тегами и считывем атрибуты
                * с названием теста и его описанием.
                * Названия складываем в один массив, описание в другой:
                 */
                for(int i=0; i<nodeList.getLength(); i++){
                    // Получаем узел из массива:
                    Node node = nodeList.item(i);
                    // Затем список его атрибутов:
                    NamedNodeMap attributes = node.getAttributes();
                    //Получаем атрибут title и его значение:
                    Node titleAttribute  = attributes.getNamedItem("title");
                    String titleCategory = titleAttribute.getNodeValue();
                    //Получаем атрибут description и его значение:
                    Node descriptionAttribute  = attributes.getNamedItem("description");
                    String descriptionCategory = descriptionAttribute.getNodeValue();
                    // Кладем все полученное в массивы:
                    title.add(titleCategory);
                    description.add(descriptionCategory);

                    //System.out.println(titleCategory + " --- " + descriptionCategory);
                }

                categories[0] = title;
                categories[1] = description;
            }catch (UnknownHostException e) {
                System.out.println(" --- Unable to resolve host lightapp.ru");
                error.add("check your Internet connection.....");
                categories[2] = error;

            }catch (Exception e) {
                System.out.println(" --- Что еще за ошибка?)");
                e.printStackTrace();
                error.add("check your Internet connection.....");
                System.out.println(" --- end trace");
            }

            return categories;
        }
    }



    /*
    * Метод делает запрос на сервер на получение списка тестов
    * какой-то одной определенной категории:
    */
    public ArrayList[] getTestsByCategory(String selectedCategory) {

        String urlRequest = urlGetTestsByCategory + selectedCategory;

        long start = new Date().getTime();

        ArrayList[] tests = null;
        /*
        * Запускаем отдельнй поток, который получит xml-file с сервера,
        * разберет его и вернет все в виде массива.
        *
        * Затем возвращаем полученные массивы в активити (или куда там еще...)
         */
        GetTestsThread tread = new GetTestsThread();
        tread.execute();
        try {
            // Ждем и получаем результат работы потока:
            tests =  tread.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println(((new Date().getTime()) - start) + " ms --- xml processing......");


        return tests;
    }

    class GetTestsThread extends AsyncTask<Void, Void, ArrayList[]>{

        @Override
        protected ArrayList[] doInBackground(Void... voids) {
            /*
            * Готовим массивы:
            * - объект, содержащий два ArrayList'a;
            * - один ArrayList - содержит названия тестов;
            * - второй - описания каждого теста;
            * - третий - возможные ошибки.
             */
            ArrayList[] tests = new ArrayList[3];
            ArrayList<String> title = new ArrayList<>();
            ArrayList<String> description = new ArrayList<>();
            ArrayList<String> error = new ArrayList<>();

            try{
                /*
                готовим API, позволяющий выполнять разбор документа
                загружаем в парсер полученный ответ и вызываем метод parse
                */
                URL url = new URL(urlGetTestsByCategory);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Читаем из потока xml-данные:
                Document doc = db.parse(new InputSource(url.openStream()));
                // Что это?
                doc.getDocumentElement().normalize();
                // Получаем массив со всеми тегами <category />:
                NodeList nodeList = doc.getElementsByTagName("category");

                /*
                * Проходим в цикле по массиву с тегами и считывем атрибуты
                * с названием теста и его описанием.
                * Названия складываем в один массив, описание в другой:
                 */
                for(int i=0; i<nodeList.getLength(); i++){
                    // Получаем узел из массива:
                    Node node = nodeList.item(i);
                    // Затем список его атрибутов:
                    NamedNodeMap attributes = node.getAttributes();
                    //Получаем атрибут title и его значение:
                    Node titleAttribute  = attributes.getNamedItem("title");
                    String titleCategory = titleAttribute.getNodeValue();
                    //Получаем атрибут description и его значение:
                    Node descriptionAttribute  = attributes.getNamedItem("description");
                    String descriptionCategory = descriptionAttribute.getNodeValue();
                    // Кладем все полученное в массивы:
                    title.add(titleCategory);
                    description.add(descriptionCategory);

                    //System.out.println(titleCategory + " --- " + descriptionCategory);
                }

                //categories[0] = title;
                //categories[1] = description;
            }catch (UnknownHostException e) {
                System.out.println(" --- Unable to resolve host lightapp.ru");
                error.add("check your Internet connection.....");
                //categories[2] = error;

            }catch (Exception e) {
                System.out.println(" --- Что еще за ошибка?)");
                e.printStackTrace();
                error.add("check your Internet connection.....");
                System.out.println(" --- end trace");
            }

            return tests;
        }
    }


}
