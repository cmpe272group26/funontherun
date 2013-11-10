package com.funontherun.entities;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Entitiy, Parcelable {
	private double lattitude;
	private double longitude;
	private String name;
	private boolean isOpen;
	private double rating;
	private String address;
	private String[] types;

	public Category() {

	}

	public double getLattitude() {
		return lattitude;
	}

	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	@Override
	public JSONObject serializeJSON() throws Exception {
		return null;
	}

	/**
	 * Method used to deserialize json for Category object
	 */
	@Override
	public void deserializeJSON(JSONObject jsonObject) throws Exception {

		JSONObject geometryObject = jsonObject.getJSONObject("geometry");
		JSONObject locationObject = geometryObject.getJSONObject("location");
		this.setLongitude(locationObject.has("lng") ? locationObject
				.getDouble("lng") : -1);
		this.setLattitude(locationObject.has("lat") ? locationObject
				.getDouble("lat") : -1);

		this.setName(jsonObject.has("name") ? jsonObject.getString("name") : "");
		if (jsonObject.has("opening_hours")) {
			JSONObject openHrJsonObject = jsonObject
					.getJSONObject("opening_hours");
			this.setOpen(openHrJsonObject.has("open_now") ? openHrJsonObject
					.getBoolean("open_now") : false);
		}
		this.setRating(jsonObject.has("rating") ? jsonObject
				.getDouble("rating") : 0.0);
		this.setAddress(jsonObject.has("vicinity") ? jsonObject
				.getString("vicinity") : "");
		//
		// this.setAddress(jsonObject.has("vicinity") ? jsonObject
		// .getString("vicinity") : "");

	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<Category> getCreator() {
		return CREATOR;
	}

	private Category(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * write Location Object to parcel
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeDouble(lattitude);
		out.writeDouble(longitude);
		out.writeDouble(rating);
		out.writeString(name);
		out.writeString(address);
		out.writeValue(isOpen);

	}

	/**
	 * read Reason Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		lattitude = in.readDouble();
		longitude = in.readDouble();
		isOpen = (Boolean) in.readValue(null);
		name = in.readString();
		address = in.readString();
		rating = in.readDouble();
	}

	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

}
