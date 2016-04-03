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
import ru.lightapp.justquizz.model.SingleTest;

/**
 * Created by Eugen on 20.07.2015.
 *
 * Экземпляр класса предоставляет доступ к API сервера с тестами.
 *
 */
public class ServerManager {

    /*
    * Единственный экземпляр класса:
    */
    private static ServerManager instance;



    private String urlGetCategories = "http://lightapp.ru/justquizz/api.php?void=get_categories";
    private String templateUrlGetTestsByCategory = "http://lightapp.ru/justquizz/api.php?void=get_tests_by_category&category=";
    private String urlGetTestsByCategory;


    /*
    * Скрываем конструктор
    * Возвращаем instance:
    */
    private  ServerManager(){

    }

    public static ServerManager getInstance(){
        if(instance == null){
            synchronized (ServerManager.class) {
                if(instance == null){
                    instance = new ServerManager();
                    //System.out.println(" --- делаем объект ServerManager");
                }
            }
        }
        //System.out.println(" --- отдаем объект ServerManager");
        return instance;
    }





    /*
    * Метод получает список категорий с тестами от сервера
    */
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //System.out.println(((new Date().getTime()) - start) + " ms --- xml processing......");

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
            ArrayList[] categories = new ArrayList[4];
            ArrayList<Integer> id = new ArrayList<>();
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
                * Проходим в цикле по массиву с тегами и считываем атрибуты
                * с названием теста и его описанием.
                * Названия складываем в один массив, описание в другой:
                 */
                for(int i=0; i<nodeList.getLength(); i++){
                    // Получаем узел из массива:
                    Node node = nodeList.item(i);
                    // Затем список его атрибутов:
                    NamedNodeMap attributes = node.getAttributes();

                    //Получаем атрибут id и его значение:
                    Node idAttribute  = attributes.getNamedItem("id");
                    int idCategory = Integer.parseInt(idAttribute.getNodeValue());

                    //Получаем атрибут title и его значение:
                    Node titleAttribute  = attributes.getNamedItem("title");
                    String titleCategory = titleAttribute.getNodeValue();

                    //Получаем атрибут description и его значение:
                    Node descriptionAttribute  = attributes.getNamedItem("description");
                    String descriptionCategory = descriptionAttribute.getNodeValue();

                    // Кладем все полученное в массивы:
                    id.add(idCategory);
                    title.add(titleCategory);
                    description.add(descriptionCategory);

                    //System.out.println(titleCategory + " --- " + descriptionCategory);
                }
                // Кладем наши масивы во внутрь массива categories:
                categories[0] = id;
                categories[1] = title;
                categories[2] = description;
            }catch (UnknownHostException e) {
                //System.out.println(" --- Unable to resolve host lightapp.ru");
                error.add("check your Internet connection.....");
                categories[3] = error;

            }catch (Exception e) {
                //System.out.println(" --- Что еще за ошибка?)");
                e.printStackTrace();
                error.add("Server have problem, try later.....");
                categories[3] = error;
               // System.out.println(" --- end trace");
            }

            return categories;
        }
    }



    /*
    * Метод делает запрос на сервер на получение списка тестов
    * какой-то одной определенной категории:
    */
    public ArrayList<SingleTest> getTestsByCategory(int selectedCategory) {

        urlGetTestsByCategory = templateUrlGetTestsByCategory + selectedCategory;

        long start = new Date().getTime();

        ArrayList<SingleTest> tests = null;
        /*
        * Запускаем отдельнй поток, который получит xml-file с сервера,
        * разберет его и вернет все в виде массива объектов.
        * Затем возвращаем полученные массивы в активити (или куда там еще...)
        */
        GetTestsThreadNew tread = new GetTestsThreadNew();
        tread.execute();
        try {
            // Ждем и получаем результат работы потока:
            tests =  tread.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            //System.out.println("--- ошибка исполнения потока, получающего тесты от сервера");

            e.printStackTrace();
        }

        //System.out.println(((new Date().getTime()) - start) + " ms --- xml processing......");

        /*
        ArrayList<String> title = tests[0];

        if(title != null) {

            for (String str : title) {
                System.out.println(" --- " + str);
            }
        }else{
            System.out.println(" --- null!");
        }
        */

        return tests;
    }

    class GetTestsThreadNew extends AsyncTask<Void, Void, ArrayList<SingleTest>>{

        @Override
        protected ArrayList<SingleTest> doInBackground(Void... voids) {
            /*
            * Готовим массивы:
            * - объект, содержащий два ArrayList'a;
            * - один ArrayList - содержит названия тестов;
            * - второй - имена файлов;
            * - третий - описание,
            * - четвертый - количество скачиваний,
            * - пятый - имя автора
            * - шестой - ошибки при загрузке.

            ArrayList[] tests = new ArrayList[6];
            ArrayList<String> titleArray = new ArrayList<>();
            ArrayList<String> fileNameArray = new ArrayList<>();
            ArrayList<String> descriptionArray = new ArrayList<>();
            ArrayList<Integer> downloadsArray = new ArrayList<>();
            ArrayList<String> authorArray = new ArrayList<>();

            ArrayList<String> error = new ArrayList<>();
            */

            ArrayList<SingleTest> arrayTest = new ArrayList<>();



            try{
                /*
                готовим API, позволяющий выполнять разбор документа
                загружаем в парсер полученный ответ и вызываем метод parse
                */
                //System.out.println(" --- " + urlGetTestsByCategory);

                URL url = new URL(urlGetTestsByCategory);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Читаем из потока xml-данные:
                Document doc = db.parse(new InputSource(url.openStream()));
                // Что это?
                doc.getDocumentElement().normalize();
                // Получаем массив со всеми тегами <test />:
                NodeList nodeList = doc.getElementsByTagName("test");

                //System.out.println(" --- кол-во элементов" + nodeList.getLength() );

                /*
                * Проходим в цикле по массиву с тегами и считывем атрибуты
                * с названием теста и его описанием.
                * Названия складываем в один массив, описание в другой:
                 */
                for(int i=0; i<nodeList.getLength(); i++){

                    //System.out.print(" --- " + i + " --- ");

                    SingleTest singleTest = new SingleTest();

                    // Получаем узел из массива:
                    Node node = nodeList.item(i);
                    // Затем список его атрибутов:
                    NamedNodeMap attributes = node.getAttributes();

                    //Получаем атрибут title и его значение:
                    Node titleAttribute  = attributes.getNamedItem("test_title");
                    singleTest.setTitle(titleAttribute.getNodeValue());
                    //String testTitle = titleAttribute.getNodeValue();

                    //Получаем атрибут file_name и его значение:
                    Node fileNameAttribute  = attributes.getNamedItem("file_name");
                    singleTest.setFileName(fileNameAttribute.getNodeValue());
                    //String fileName = fileNameAttribute.getNodeValue();

                    //Получаем атрибут description и его значение:
                    Node descriptionAttribute  = attributes.getNamedItem("description");
                    singleTest.setDescription(descriptionAttribute.getNodeValue());
                    //String description = descriptionAttribute.getNodeValue();

                    //Получаем атрибут downloads и его значение:
                    Node downloadsAttribute  = attributes.getNamedItem("downloads");
                    singleTest.setDownloads(downloadsAttribute.getNodeValue());
                    //int downloads = Integer.parseInt(downloadsAttribute.getNodeValue());

                    /* Кладем все полученное в массивы:
                    titleArray.add(testTitle);
                    fileNameArray.add(fileName);
                    descriptionArray.add(description);
                    downloadsArray.add(downloads);
                    */

                    // Кладем singleTest в массив с тестами:
                    arrayTest.add(singleTest);

                    //System.out.println(singleTest.getTitle() + " --- " + singleTest.getFileName());
                }

                /*
                tests[0] = titleArray;
                tests[1] = fileNameArray;
                tests[2] = descriptionArray;
                tests[3] = downloadsArray;
                */


            }catch (UnknownHostException e) {
                //System.out.println(" --- Unable to resolve host lightapp.ru");
                //error.add("check your Internet connection.....");
                //categories[2] = error;
                arrayTest = null;

            }catch (Exception e) {
                //System.out.println(" --- Что еще за ошибка?)");
                e.printStackTrace();
                //error.add("Something wrong.... hmm.");
                //System.out.println(" --- end trace");
                //tests[5] = error;
                arrayTest = null;
            }

            return arrayTest;
        }
    }


}
