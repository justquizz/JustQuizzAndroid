package ru.lightapp.justquizz.model;

/**
 * Created by eugen on 14.03.2016.
 *
 * фдсКласс описывает свойства объекта, теста полученного от сервера:
 * - title - название теста;
 */
public class SingleTest {

    /*
    * Поля с информацией:
    */
    private String title;
    private String fileName;
    private String description;
    private String downloads;
    private String author;
    private String linkAuthorPage;

    public SingleTest(){
        //System.out.println(" --- конструктор SingleTest");
    }


    /*
    * GETTERS and SETTERS:
    */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLinkAuthorPage() {
        return linkAuthorPage;
    }

    public void setLinkAuthorPage(String linkAuthorPage) {
        this.linkAuthorPage = linkAuthorPage;
    }
}
