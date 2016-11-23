package com.vineSwipe.swipe.data.dataBase.dbModel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;


@DatabaseTable(tableName = "gif")
public class Gif {


    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    @DatabaseField
    private Date dateCreated;


    public Gif() {

    }

    public Gif(String id, String title, String desc, Date dateCreated) {

        this.id = id;
        this.title = title;
        this.description = desc;
        this.dateCreated = dateCreated;
    }


    public String getId() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


}