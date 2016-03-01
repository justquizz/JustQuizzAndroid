package ru.lightapp.justquizz.dataexchange;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by eugen on 02.08.2015.
 *
 *
 */
public class DownloadFile implements Runnable {
    @Override
    public void run() {

        //DownloadFromUrl("http://lightapp.ru/pic/pc_config.png", "test1");


    }

    public void DownloadFromUrl(String DownloadUrl, String fileName) {

        try {
            File root = android.os.Environment.getExternalStorageDirectory();

            File dir = new File (root.getAbsolutePath() + "/justquizz/test");
            if(!dir.exists()) {
                dir.mkdirs();
            }

            URL url = new URL(DownloadUrl); //you can write here any link
            File file = new File(dir, fileName);

            long startTime = System.currentTimeMillis();
            //Log.d("DownloadManager", "download begining");
            //Log.d("DownloadManager", "download url:" + url);
            //Log.d("DownloadManager", "downloaded file name:" + fileName);

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
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }


           /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
            //Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

            System.out.println(" --- Done!");

        } catch (IOException e) {
            //Log.d("DownloadManager", "Error: " + e);
        }

    }
}
