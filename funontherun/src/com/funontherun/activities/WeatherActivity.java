package com.funontherun.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.funontherun.ApplicationEx;
import com.funontherun.R;
import com.funontherun.entities.Weather;
import com.funontherun.services.RetrieveWeatherService;
import com.funontherun.services.RetrieveWeatherService.RetrieveWeatherServiceListener;
import com.funontherun.utils.Constants;

public class WeatherActivity extends FunBaseActivity implements
		RetrieveWeatherServiceListener {
	private ActionBar actionBarSherlock;
	private TextView weatherTextView;
	private TextView descTextView;
	private ImageView weatherImageView;
	private TextView cTempTextView;
	private TextView minTempTextView;
	private TextView maxTempTextView;
	private TextView humidityTextView;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);

		actionBarSherlock = getSupportActionBar();

		// actionBarSherlock.setTitle(getResources().getString(
		// R.string.weather_title));
		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);

		weatherTextView = (TextView) findViewById(R.id.main_value);
		descTextView = (TextView) findViewById(R.id.desc_value);
		cTempTextView = (TextView) findViewById(R.id.c_temp_value);
		minTempTextView = (TextView) findViewById(R.id.min_temp_value);
		maxTempTextView = (TextView) findViewById(R.id.max_temp_value);
		humidityTextView = (TextView) findViewById(R.id.humidity_value);
		weatherImageView = (ImageView) findViewById(R.id.weather_icon);

		/**
		 * Web Service call for weather API
		 */
		getWeather();

	}

	/**
	 * Web service call for Weather
	 */
	public void getWeather() {
		Constants.setEventLocation();
		Constants.setRangeInMeters();
		pd = ProgressDialog.show(WeatherActivity.this, "", "Loading...", true);
		RetrieveWeatherService service = new RetrieveWeatherService(
				getApplicationContext(), ApplicationEx.destinationLoc);
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/**
	 * Menu selection Listener
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.funontherun.services.RetrieveWeatherService.
	 * RetrieveWeatherServiceListener
	 * #onRetrieveWeatherFinished(com.funontherun.entities.Weather)
	 */
	@Override
	public void onRetrieveWeatherFinished(Weather weather) {

		if (pd != null || pd.isShowing())
			pd.cancel();
		if (weather != null) {
			if (weather.getErrorCode() == 404) {
				Toast.makeText(WeatherActivity.this, "Place not found",
						Toast.LENGTH_SHORT).show();
			} else {
				String title = getResources().getString(R.string.weather_title)
						+ "<font color=#ffcc00>" + " " + weather.getName()
						+ "</font>";
				actionBarSherlock.setTitle(Html.fromHtml(title));
				weatherTextView.setText(weather.getMain());
				descTextView.setText(weather.getDescription());
				cTempTextView.setText(""
						+ Constants.convertKelvinToDegreeFahrenheit(weather
								.getCurrentTemp()) + "\u2109");
				minTempTextView.setText(""
						+ Constants.convertKelvinToDegreeFahrenheit(weather
								.getMinTemp()) + "\u2109");
				maxTempTextView.setText(""
						+ Constants.convertKelvinToDegreeFahrenheit(weather
								.getMaxTemp()) + "\u2109");
				humidityTextView.setText("" + weather.getHumidity());
				displayWeatherIcon(weather);
			}

		} else {
			Toast.makeText(WeatherActivity.this, "Network Error",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Display weather according to weather description & time
	 * 
	 * @param weather
	 */
	public void displayWeatherIcon(Weather weather) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		String str = sdf.format(new Date());
		Integer hours = Integer.parseInt(str);
		if ((hours <= 7 || hours > 18)
				&& weather.getMain().equalsIgnoreCase("Clear")) {
			weatherImageView.setImageResource(R.drawable.night_clear);
		} else if ((hours <= 7 || hours > 18)
				&& (weather.getMain().equalsIgnoreCase("Clouds")
						|| weather.getMain().equalsIgnoreCase("Haze") || weather
						.getMain().equalsIgnoreCase("Mist"))) {
			weatherImageView.setImageResource(R.drawable.night_cloudy);
		} else if ((hours <= 7 || hours > 18)
				&& weather.getMain().equalsIgnoreCase("Snow")) {
			weatherImageView.setImageResource(R.drawable.night_snowy);
		} else if ((hours <= 7 || hours > 18)
				&& weather.getMain().equalsIgnoreCase("Rain")) {
			weatherImageView.setImageResource(R.drawable.night_rainy);
		} else if ((hours > 7 || hours <= 18)
				&& weather.getMain().equalsIgnoreCase("Clear")) {
			weatherImageView.setImageResource(R.drawable.day_clear);
		} else if ((hours > 7 || hours <= 18)
				&& (weather.getMain().equalsIgnoreCase("Clouds")
						|| weather.getMain().equalsIgnoreCase("Haze") || weather
						.getMain().equalsIgnoreCase("Mist"))) {
			weatherImageView.setImageResource(R.drawable.day_cloudy);
		} else if ((hours > 7 || hours <= 19)
				&& weather.getMain().equalsIgnoreCase("Snow")) {
			weatherImageView.setImageResource(R.drawable.day_snowy);
		} else if ((hours > 7 || hours <= 19)
				&& weather.getMain().equalsIgnoreCase("Rain")) {
			weatherImageView.setImageResource(R.drawable.day_rainy);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.funontherun.services.RetrieveWeatherService.
	 * RetrieveWeatherServiceListener#onRetrieveWeatherFailed(int,
	 * java.lang.String)
	 */
	@Override
	public void onRetrieveWeatherFailed(int error, String message) {
		if (pd.isShowing() && pd != null)
			pd.cancel();
		Toast.makeText(WeatherActivity.this,
				Constants.FunOnTheRunDialogMessages.NETWORK_ERROR,
				Toast.LENGTH_SHORT).show();

	}
}
