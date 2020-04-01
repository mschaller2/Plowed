package com.example.plowed;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private boolean driver;

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public boolean getDriver(){
        return driver;
    }
    public void setDriver(boolean driver){
        this.driver = driver;
    }

}
