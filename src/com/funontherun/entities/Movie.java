package com.funontherun.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Entitiy, Parcelable {
	private String name;
	private String address;
	private String matchLevel;
	private double relevance;
	private String matchType;
	private double lattitude;
	private double longitude;

	public Movie() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMatchLevel() {
		return matchLevel;
	}

	public void setMatchLevel(String matchLevel) {
		this.matchLevel = matchLevel;
	}

	public double getRelevance() {
		return relevance;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
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

	@Override
	public JSONObject serializeJSON() throws Exception {
		return null;
	}

	/**
	 * Method used to deserialize json for Movie object
	 */
	@Override
	public void deserializeJSON(JSONObject jsonObject) throws Exception {

		JSONArray locationArray = jsonObject.getJSONArray("loc");
		if (locationArray.length() > 0) {
			this.setLongitude(locationArray.getDouble(0));
			this.setLattitude(locationArray.getDouble(1));
		}

		this.setName(jsonObject.has("film") ? jsonObject.getString("film") : "");
		this.setMatchLevel(jsonObject.has("matchLevel") ? jsonObject
				.getString("matchLevel") : "");
		this.setMatchType(jsonObject.has("matchType") ? jsonObject
				.getString("matchType") : "");
		String street = (jsonObject.has("street") ? jsonObject
				.getString("street") : "");
		this.setRelevance(jsonObject.has("relevance") ? jsonObject
				.getDouble("relevance") : -1);

		String city = (jsonObject.has("city") ? jsonObject.getString("city")
				: "");
		String country = (jsonObject.has("country") ? jsonObject
				.getString("country") : "");
		String postalCode = (jsonObject.has("postalCode") ? jsonObject
				.getString("postalCode") : "");
		String state = (jsonObject.has("state") ? jsonObject.getString("state")
				: "");

		if (street.trim().length() == 0)
			street = "";
		else
			street = street + ", ";
		if (city.trim().length() == 0)
			city = "";
		else
			city = city + ", ";
		if (state.trim().length() == 0)
			state = "";
		else
			state = state + ", ";
		this.setAddress(street + city + state + country + postalCode);

	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<Movie> getCreator() {
		return CREATOR;
	}

	private Movie(Parcel in) {
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
		out.writeString(name);
		out.writeString(matchLevel);
		out.writeString(matchType);
		out.writeString(address);
		out.writeDouble(relevance);
		out.writeDouble(lattitude);
		out.writeDouble(longitude);

	}

	/**
	 * read Reason Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		name = in.readString();
		matchLevel = in.readString();
		matchType = in.readString();
		address = in.readString();
		relevance = in.readDouble();
		lattitude = in.readDouble();
		longitude = in.readDouble();
	}

	public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
		public Movie createFromParcel(Parcel in) {
			return new Movie(in);
		}

		public Movie[] newArray(int size) {
			return new Movie[size];
		}
	};

}
