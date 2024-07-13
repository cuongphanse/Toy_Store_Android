package com.example.toy_store.data.model;

import com.google.gson.annotations.SerializedName;

public class Local {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("local")
    private String local;

    // General constructor (can be modified to take specific arguments)
    public Local() {
        this.id = 0;
        this.name = "";
        this.local = "";
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocal() {
        return local;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    // toString method for a human-readable representation
    @Override
    public String toString() {
        return "Local{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", local='" + local + '\'' +
                '}';
    }
}
