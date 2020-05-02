package com.example.plowed;

public class User {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String uID;
    public User(){}
    public User(String name, String email, String phone, String address, String uID){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.uID = uID;
    }
    public String getAddress(){ return this.address; }
    public void setAddress(String address){ this.address = address; }
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
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getuID() { return uID; }
    public void setuID(String uID){ this.uID = uID; }

}
