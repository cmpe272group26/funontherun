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
import com.funontherun.adapters.CategoryListAdapter;
import com.funontherun.entities.Category;
import com.funontherun.services.RetrieveEventsService;
import com.funontherun.services.RetrieveEventsService.RetrieveEventsServiceListener;
import com.funontherun.utils.Constants;
import com.funontherun.utils.FunUtils;

public class EventsActivity extends FunBaseActivity implements
		RetrieveEventsServiceListener {
	private ActionBar actionBarSherlock;
	/**
	 * List View to display events based on user selected range
	 */
	private ListView categoryListView;
	private ProgressDialog pd;
	private CategoryListAdapter categoryListAdapter;
	private List<Category> categoryList;
	private AlertDialog locationDialog;
	private AlertDialog rangeDialog;

	private final int LOCATION_DIALOG = 0, RANGE_DIALOG = 1;
	/**
	 * String that holds user selected search option
	 */
	private String searchQuery = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list);

		actionBarSherlock = getSupportActionBar();

		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);
		actionBarSherlock.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.nav_bar));

		Intent intent = getIntent();

		/**
		 * get values for web service call
		 */
		searchQuery = FunUtils.getEventValue(intent.getExtras().getString(
				getResources().getString(R.string.category)));
		actionBarSherlock.setTitle(intent.getExtras().getString(
				getResources().getString(R.string.category)));

		categoryList = new ArrayList<Category>();
		categoryListView = (ListView) findViewById(R.id.category_list_view);
		categoryListAdapter = new CategoryListAdapter(categoryList, this);
		categoryListView.setAdapter(categoryListAdapter);
		getCategories();
		categoryListView.setOnItemClickListener(onItemClickListener);

	}

	/**
	 * Web service call for categories
	 */
	public void getCategories() {
		Constants.setEventLocation();
		Constants.setRangeInMeters();
		pd = ProgressDialog.show(EventsActivity.this, "", "Loading...", true);
		RetrieveEventsService service = new RetrieveEventsService(
				getApplicationContext(), searchQuery,
				ApplicationEx.userSelectedLocation, ApplicationEx.rangeInMeters);
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
		case R.id.range:
			showDialog(RANGE_DIALOG);
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
			Category category = (Category) view.getTag(R.id.fun_id);
			Intent intent = new Intent(EventsActivity.this,
					EventsDetailActivity.class);
			ApplicationEx.category = category;
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.rangemenu, menu);
		return true;
	}

	/**
	 * Success callback of web service
	 */
	@Override
	public void onRetrieveEventsFinished(List<Category> resultList) {
		if (pd.isShowing() && pd != null)
			pd.cancel();
		categoryList = resultList;
		if (categoryList.size() == 0)
			Toast.makeText(EventsActivity.this, "Search not found...",
					Toast.LENGTH_SHORT).show();
		else
			categoryListAdapter.setCategoryList(categoryList);

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
								getCategories();
								break;
							case 1:
								ApplicationEx.selectedLocation = 1;
								locationDialog.dismiss();
								getCategories();
								break;
							case 2:
								ApplicationEx.selectedLocation = 2;
								locationDialog.dismiss();
								getCategories();
								break;

							}
						}
					});
			locationDialog = locationBuilder.create();
			return locationDialog;
		case RANGE_DIALOG:
			AlertDialog.Builder rangeBuilder = new AlertDialog.Builder(this);
			rangeBuilder.setTitle(R.string.select_range);
			rangeBuilder.setSingleChoiceItems(ApplicationEx.rangeItems,
					ApplicationEx.selectedRange,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							switch (item) {
							case 0:
								ApplicationEx.selectedRange = 0;
								rangeDialog.dismiss();
								getCategories();
								break;
							case 1:
								ApplicationEx.selectedRange = 1;
								rangeDialog.dismiss();
								getCategories();
								break;
							case 2:
								ApplicationEx.selectedRange = 2;
								rangeDialog.dismiss();
								getCategories();
								break;
							case 3:
								ApplicationEx.selectedRange = 3;
								rangeDialog.dismiss();
								getCategories();
								break;
							}
						}
					});
			rangeDialog = rangeBuilder.create();
			return rangeDialog;
		default:
		}
		return super.onCreateDialog(id);
	}

	/**
	 * Failure callback of web service
	 */
	@Override
	public void onRetrieveEventsFailed(int error, String message) {
		if (pd.isShowing() && pd != null)
			pd.cancel();
		FunUtils.showStatus(EventsActivity.this, message, "Error");
	}

}
