package com.dab.climbz;

import java.io.Serializable;
import java.util.Date;

public class RouteObject implements Serializable{
    private Date mDate;
	private String mGrade;
    private String mColor;
    private String mLocation;
    private String mSetter;
    private String mID;

	public RouteObject(Date date, String grade, String color, String location, String setter, String id){
		mDate = date;
		mGrade = grade;
		mColor = color;
		mLocation = location;
        mSetter = setter;
        mID = id;
	}

    public Date getDate() {
        return mDate;
    }

    public String getGrade() {
        return mGrade;
    }

    public String getSetter(){
        return mSetter;
    }
    public String getColor() {
        return mColor;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getID() {
        return mID;
    }

    public void setID(String id)  { mID = id; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RouteObject))
            return false;
        if (obj == this)
            return true;
        if (null == obj) {
            return false;
        }

        RouteObject routeObject = (RouteObject)obj;
        return (mGrade == routeObject.getGrade() &&
                mDate == routeObject.getDate() &&
                mColor == routeObject.getColor() &&
                mLocation == routeObject.getLocation()) &&
                mSetter == routeObject.getSetter() &&
                mID == routeObject.getID();
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + mDate.hashCode();
        hashCode = 31 * hashCode + mGrade.hashCode();
        hashCode = 31 * hashCode + mColor.hashCode();
        hashCode = 31 * hashCode + mLocation.hashCode();
        hashCode = 31 * hashCode + mID.hashCode();
        return hashCode;
    }
}
