package com.example.kishanshah.contactsapp;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by kishanshah on 22/3/18.
 * Details of contact
 */

public class Contact implements Serializable, Comparable<Contact>,Parcelable{
    private String contactno, name;
    private double latitude,longitude;
    private int color;

    public Contact(){

    }

    public Contact(String contactno, String name, double latitude, double longitude) {
        this.contactno = contactno;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        Random rd=new Random();
        color= Color.argb(255,rd.nextInt(256),rd.nextInt(256),rd.nextInt(256));
    }

    public Contact(String json) throws JSONException {
        JSONObject jsonObject=new JSONObject(json);
        name=jsonObject.getString("name");
        contactno=jsonObject.getString("contactno");
        latitude=jsonObject.getDouble("latitude");
        longitude=jsonObject.getDouble("longitude");
    }

    protected Contact(Parcel in) {
        contactno = in.readString();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        color = in.readInt();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int compareTo(@NonNull Contact o) {

        return this.name.toUpperCase().compareTo(o.getName().toUpperCase());
    }

    public void addOrUpdateContact(){
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference ref=db.getReference("users");
        ref.child(uid).child("contacts").child(contactno).setValue(this);
    }

    @Override
    public String toString() {
        return "{contactno='"+contactno+"',name='"+name+"',latitude="+latitude+",longitude="+longitude+",color="+color+"}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactno);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(color);
    }
}
