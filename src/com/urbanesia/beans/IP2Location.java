package com.urbanesia.beans;

public class IP2Location {
	private String city = "", latitude = "", longitude = "";
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}
}
