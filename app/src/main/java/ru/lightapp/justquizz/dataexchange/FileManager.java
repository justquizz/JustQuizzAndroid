package ru.lightapp.justquizz.dataexchange;

import java.io.File;

import ru.lightapp.justquizz.db.DBManager;

/**
 * Created by eugen on 20.10.2015.
 *
 * ����� ������������ ��� ������ � �������� ��������.
 *
 * ������������ ��������� ��������� � ������.
 *
 * TODO make this class singleton!
 *
 */
public class FileManager {

    /*
    * ������������ ��������� ������� ������
    */
    private static FileManager instance;


    /*
    * ���� � �������� �������
    */
    private String storageDirectory;

    /*
    * ���� � ����� � ������� �� ����������:
    */
    private String testDirectory;

    /*
    * ���� � ����� �������� �����:
    */
    private  String pathToFile;




    /*
    * �������� ����������� � �������������� �������� ����������:
    */
    private FileManager(){


        /*
        * �������� ���� � �������� �������:
        */
        File root = android.os.Environment.getExternalStorageDirectory();
        storageDirectory = root.getAbsolutePath();

        /*
        * �������� ���� � ����� � ������� �� ����������:
        */




    }

    /*
    * ���������� Singleton c ������� �����������:
    */
    public static FileManager getInstance(){
        if(instance == null){
            synchronized (FileManager.class) {
                if(instance == null){
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }


    /*
    * GETTERS:
    */


    /*
    * �������� ���� � �������� �������
    */
    public String getStorageDirectory(){

        return this.storageDirectory;
    }


    /*
    * �������� ���������� �������� � �����:
    */
    public int getQuantityAnswers(){

        return 1;
    }

    /*
    * ������� ����� ������� �� ��� ������:
    */
    public String getQuestion(int numberQuestion){

        return "";
    }



}
