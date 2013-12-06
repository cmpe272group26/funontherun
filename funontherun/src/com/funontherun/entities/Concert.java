package com.funontherun.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Concert implements Entitiy, Parcelable {
	private String title;
	private String artist;
	private String venue;
	private String address;
	private String StartDate;
	private String website;
	private double lattitude;
	private double longitude;

	public Concert() {

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStartDate() {
		return StartDate;
	}

	public void setStartDate(String startDate) {
		StartDate = startDate;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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
	 * Method used to deserialize json for Weather object
	 */
	@Override
	public void deserializeJSON(JSONObject jsonObject) throws Exception {

		this.setTitle(jsonObject.has("title") ? jsonObject.getString("title")
				: "");

		StringBuffer artists = new StringBuffer("");
		JSONObject artistObject = jsonObject.getJSONObject("artists");
		if (artistObject.has("artist: [")) {
			JSONArray artistArray = artistObject.getJSONArray("artist");
			for (int i = 0; i < artistArray.length(); i++) {
				artists.append(artistArray.getString(i) + ", ");
			}
			String artist = artists.toString().substring(0,
					artists.toString().length() - 2);
			this.setArtist(artist);

		} else {
			this.setArtist(artistObject.has("artist") ? artistObject.getString("artist")
					: "");
		}

		JSONObject venueObject = jsonObject.getJSONObject("venue");
		this.setVenue(venueObject.has("name") ? venueObject.getString("name")
				: "");

		JSONObject locationObject = venueObject.getJSONObject("location");
		JSONObject geoPointObject = locationObject.getJSONObject("geo:point");

		this.setLattitude(geoPointObject.has("geo:lat") ? geoPointObject
				.getDouble("geo:lat") : -1);
		this.setLongitude(geoPointObject.has("geo:long") ? geoPointObject
				.getDouble("geo:long") : -1);

		String city = (locationObject.has("city") ? locationObject
				.getString("city") : "");
		String country = (locationObject.has("country") ? locationObject
				.getString("country") : "");
		String street = (locationObject.has("street") ? locationObject
				.getString("street") : "");
		String postalCode = (locationObject.has("postalcode") ? locationObject
				.getString("postalcode") : "");

		this.setAddress(street + ", " + city + ", " + postalCode + ", "
				+ country);

		this.setWebsite(jsonObject.has("website") ? jsonObject
				.getString("website") : "");

		this.setStartDate(jsonObject.has("startDate") ? jsonObject
				.getString("startDate") : "");
	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<Concert> getCreator() {
		return CREATOR;
	}

	private Concert(Parcel in) {
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
		out.writeString(title);
		out.writeString(artist);
		out.writeString(venue);
		out.writeString(address);
		out.writeString(StartDate);
		out.writeString(website);
		out.writeDouble(lattitude);
		out.writeDouble(longitude);

	}

	/**
	 * read Reason Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		title = in.readString();
		artist = in.readString();
		venue = in.readString();
		address = in.readString();
		StartDate = in.readString();
		website = in.readString();
		lattitude = in.readDouble();
		longitude = in.readDouble();
	}

	public static final Parcelable.Creator<Concert> CREATOR = new Parcelable.Creator<Concert>() {
		public Concert createFromParcel(Parcel in) {
			return new Concert(in);
		}

		public Concert[] newArray(int size) {
			return new Concert[size];
		}
	};

}
