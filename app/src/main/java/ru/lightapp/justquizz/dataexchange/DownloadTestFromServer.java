package ru.lightapp.justquizz.dataexchange;

import ru.lightapp.justquizz.R;
import ru.lightapp.justquizz.model.Init;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by eugen on 07.09.2015.
 */
public class DownloadTestFromServer implements Runnable {

    private String xmlFolder = Init.getSERVER() + "xml";

    @Override
    public void run() {

    getCategory();




    }

    private void getCategory() {

        System.out.println(" --- start xml");

        long start = new Date().getTime();
        //TextView tv = (TextView) findViewById(R.id.textView888);

        try{
         /*
          определяем URL сервиса
          готовим API, позволяющий выполнять разбор документа
          загружаем в парсер полученный ответ и вызываем метод parse
          */
            URL url = new URL("http://lightapp.ru/justquizz/api.php?void=get_categories");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
             /* получаем агрегатный узел с дочерними узлами с атрибутами, хранящими значения валют;
             * в ответе всего два узла, мы возьмем первый, а при необходимости тут вполне можно
             * запустить цикл с nodeList.getLength
            */

            NodeList nodeList = doc.getElementsByTagName("categories");
            Node node = nodeList.item(0);
            // опускаемся на узел ниже и получаем список его атрибутов
            NamedNodeMap attributes = node.getFirstChild().getAttributes();
            //получаем значение атрибут buy
            Node currencyAttribEUR  = attributes.getNamedItem("name");
            // ... и его значение
            String currencyValueEUR = currencyAttribEUR.getNodeValue();

            System.out.println(currencyValueEUR + " --- ");

        }


        catch (Exception e) {
            System.out.println(" --- Не удалось выполнить операцию");
            e.printStackTrace();
        }

        System.out.println(((new Date().getTime()) - start) + " ms --- ");
    }
}
