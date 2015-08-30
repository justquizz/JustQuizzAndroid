package ru.lightapp.justquizz.model;

import java.io.*;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Eugen
 *
 * Данный класс предоставляет доступ к файлам с настройками.
 * В качестве параметра принимает название поля и название файла, в котором находится поле.

 */
public class PropertyItemGetter {


    public String getItem(String nameItem, String nameFile){

        String item;

        FileInputStream fis = null;
        Reader reader = null;

        try {
            //load a properties file

            fis = new FileInputStream(nameFile);
            reader = new InputStreamReader(fis, "UTF-8");
            Properties prop = new Properties();
            prop.load(reader);

            // get item from file
            item = prop.getProperty(nameItem);

            fis.close();
            reader.close();

        } catch (FileNotFoundException e){
            System.out.println("error: FileNotFoundException!" + nameFile);
            item = null;

        } catch (IOException ex) {
            System.out.println("error: IOException!" + nameFile);
            item = null;

        } finally {
            // Close resource FileInputStream and Reader:
            //fis.close();
            //reader.close();
        }



        return item;
    }

}
