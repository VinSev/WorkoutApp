package nl.vinsev.workoutapp.model;

import java.io.Serializable;

public class Exercise implements Serializable {
    private int id;
    private String id_num;
    private String name;
    private String title;
    private String primer;
    private String type;
    private String primary;

    public Exercise() {

    }

    public int getId() {
        return id;
    }

    public String getId_num() {
        return id_num;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getPrimer() {
        return primer;
    }

    public String getType() {
        return type;
    }

    public String getPrimary() {
        return primary;
    }
}
