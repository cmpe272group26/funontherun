package com.funontherun.activities;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Toast;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.funontherun.ApplicationEx;
import com.funontherun.R;
import com.funontherun.adapters.ConcertsListAdapter;
import com.funontherun.entities.Concert;
import com.funontherun.entities.Location;
import com.funontherun.services.RetrieveConcertsService;
import com.funontherun.services.RetrieveConcertsService.RetrieveConcertsServiceListener;
import com.funontherun.utils.Constants;
import com.funontherun.utils.FunUtils;

public class ConcertsActivity extends FunBaseActivity implements
		RetrieveConcertsServiceListener {
	private ActionBar actionBarSherlock;
	/**
	 * List View to display Concerts based on user selected range
	 */
	private ListView concertListView;
	private ProgressDialog pd;
	private ConcertsListAdapter concertListAdapter;
	private List<Concert> concertList;
	private AlertDialog locationDialog;

	private final int LOCATION_DIALOG = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list);

		FunUtils.resetValues();
		actionBarSherlock = getSupportActionBar();

		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);
		// actionBarSherlock.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.nav_bar));

		actionBarSherlock.setTitle(getResources().getString(R.string.concerts));

		concertList = new ArrayList<Concert>();
		concertListView = (ListView) findViewById(R.id.category_list_view);
		concertListAdapter = new ConcertsListAdapter(concertList, this);
		concertListView.setAdapter(concertListAdapter);
		FunUtils.getEquidistantPoint();
		if (ApplicationEx.selectedLocation == 0) {
			pd = ProgressDialog.show(ConcertsActivity.this, "", "Loading...",
					true);
			getConcertsAlongRoute(ApplicationEx.srcLocation);
			getConcertsAlongRoute(ApplicationEx.destLocation);

		} else {
			getConcerts();
		}
		concertListView.setOnItemClickListener(onItemClickListener);

	}

	/**
	 * Web service call for Concerts
	 */
	public void getConcertsAlongRoute(Location location) {
		Constants.setEventLocation();
		Constants.setRangeInMeters();
		RetrieveConcertsService service = new RetrieveConcertsService(
				getApplicationContext(), location, 4);
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/**
	 * Web service call for categories
	 */
	public void getConcerts() {
		Constants.setEventLocation();
		pd = ProgressDialog.show(ConcertsActivity.this, "", "Loading...", true);
		RetrieveConcertsService service = new RetrieveConcertsService(
				getApplicationContext(), ApplicationEx.userSelectedLocation, 5);
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/**
	 * Action for selecting menu items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		case R.id.location:
			showDialog(LOCATION_DIALOG);
			break;
		case R.id.map:
			Intent intent = new Intent(ConcertsActivity.this,
					EventsOnMapActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * List view item click listener for the location details
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Concert concert = (Concert) view.getTag(R.id.fun_id);
			Intent intent = new Intent(ConcertsActivity.this,
					ConcertDetailActivity.class);
			ApplicationEx.concerts = concert;
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.concert_location_menu, menu);
		return true;
	}

	/*
	 * (non-Javadoc) Display Dialogs for selecting Locations & Range for
	 * locations
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case LOCATION_DIALOG:
			AlertDialog.Builder locationBuilder = new AlertDialog.Builder(this);
			locationBuilder.setTitle(R.string.select_location);
			locationBuilder.setSingleChoiceItems(ApplicationEx.locationItems,
					ApplicationEx.selectedLocation,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {

							switch (item) {
							case 0:
								ApplicationEx.selectedLocation = 0;
								locationDialog.dismiss();
								FunUtils.resetValues();
								pd = ProgressDialog.show(ConcertsActivity.this,
										"", "Loading...", true);
								getConcertsAlongRoute(ApplicationEx.srcLocation);
								getConcertsAlongRoute(ApplicationEx.destLocation);
								break;
							case 1:
								ApplicationEx.selectedLocation = 1;
								locationDialog.dismiss();
								FunUtils.resetValues();
								getConcerts();
								break;
							case 2:
								ApplicationEx.selectedLocation = 2;
								locationDialog.dismiss();
								FunUtils.resetValues();
								getConcerts();
								break;

							}
						}
					});
			locationDialog = locationBuilder.create();
			return locationDialog;
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	public void showMessage() {
		Toast.makeText(getApplicationContext(),
				"Please select a valid option...", Toast.LENGTH_SHORT).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.funontherun.services.RetrieveConcertsService.
	 * RetrieveConcertsServiceListener
	 * #onRetrieveConcertsFinished(com.funontherun.entities.Weather)
	 */
	@Override
	public void onRetrieveConcertsFinished(ArrayList<Concert> resultList) {
		ApplicationEx.count++;
		ArrayList<Concert> mainList = new ArrayList<Concert>();
		if (ApplicationEx.selectedLocation == 0 && resultList != null) {
			ApplicationEx.mainConcertList.add(resultList);
			if (ApplicationEx.count == 2) {
				for (int i = 0; i < ApplicationEx.mainConcertList.size(); i++) {
					for (int j = 0; j < ApplicationEx.mainConcertList.get(i)
							.size(); j++) {
						Concert concert = new Concert();
						concert = ApplicationEx.mainConcertList.get(i).get(j);
						mainList.add(concert);

					}
				}
				if (pd.isShowing() && pd != null)
					pd.cancel();
				concertListAdapter.setConcertList(mainList);
				System.out.println("***************" + mainList.size()
						+ "****************");
			}

		} else {
			if (pd.isShowing() && pd != null)
				pd.cancel();
			concertList = resultList;
			ApplicationEx.mapConcertList = resultList;
			if (concertList == null || concertList.size() == 0) {
				if (ApplicationEx.mainConcertList.size() > 0) {
					for (int i = 0; i < ApplicationEx.mainConcertList.size(); i++) {
						for (int j = 0; j < ApplicationEx.mainConcertList
								.get(i).size(); j++) {
							Concert concert = new Concert();
							concert = ApplicationEx.mainConcertList.get(i).get(
									j);
							mainList.add(concert);

						}
					}
					concertListAdapter.setConcertList(mainList);

				} else {
					if (ApplicationEx.selectedLocation == 1
							|| ApplicationEx.selectedLocation == 2) {
						concertListAdapter.setConcertList(mainList);
						Toast.makeText(ConcertsActivity.this,
								"No results found...", Toast.LENGTH_SHORT)
								.show();
					}

				}
			} else {
				concertListAdapter.setConcertList(concertList);
				System.out.println("***" + concertList.size() + "***");
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.funontherun.services.RetrieveConcertsService.
	 * RetrieveConcertsServiceListener#onRetrieveConcertsFailed(int,
	 * java.lang.String)
	 */
	@Override
	public void onRetrieveConcertsFailed(int error, String message) {
		if (pd.isShowing() && pd != null)
			pd.cancel();
		if (ApplicationEx.mainConcertList.size() == 0)
			Toast.makeText(ConcertsActivity.this, "No results found...",
					Toast.LENGTH_SHORT).show();

	}

}
