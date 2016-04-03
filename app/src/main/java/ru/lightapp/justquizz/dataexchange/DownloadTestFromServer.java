package ru.lightapp.justquizz.dataexchange;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.util.ByteArrayBuffer;
/**
 * Created by eugen on 07.09.2015.
 *
 *
 *
 */
public class DownloadTestFromServer extends Thread {

    /*
    * Путь к папке с тестами на сервере:
    */
    private String folderWithTest = "http://lightapp.ru/justquizz/test_md5/";

    /*
    * Имя файла, который нужно загрузить:
    */
    private String fileName;

    /*
    * Полный путь к файлу:
    * состоит из пути к папке на сервере + имя файла + расширение,
    * но отсутствует путь к файловой системе
    */
    private String urlForFile;


    public DownloadTestFromServer(String md5_name){

        String md5_nameLowerCase = md5_name.toLowerCase();

        // TODO получать расширение файла из ресурсов или из БД:
        this.fileName = md5_nameLowerCase + ".jqzz";


        this.urlForFile = folderWithTest + fileName;
        start();
    }


    @Override
    public void run() {

        //System.out.println(" --- конструктор " + fileName);

        try {

            //System.out.println(" --- " + urlForFile);

            File root = android.os.Environment.getExternalStorageDirectory();

            // TODO получать путь из ресурсов или БД:
            File dir = new File (root.getAbsolutePath() + "/justquizz/tests");
            if(!dir.exists()) {
                dir.mkdirs();
            }

            URL url = new URL(urlForFile); //you can write here any link
            File file = new File(dir, fileName);

           /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

           /*
            * Define InputStreams to read from the URLConnection.
            */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

           /*
            * Read bytes to the Buffer until there is nothing more to read(-1).
            */
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }


           /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();

            //System.out.println(" --- Done!");

        } catch (IOException e) {
            //System.out.println(" --- start trace");
            e.printStackTrace();
            //System.out.println(" --- end trace");

        }
    }


}
