package com.funontherun.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.funontherun.R;
import com.funontherun.entities.Movie;

public class MoviesListAdapter extends BaseAdapter {
	private List<Movie> movieList = new ArrayList<Movie>();
	private Context context;

	/**
	 * 
	 * @param MovieList
	 * @param applicationContext
	 */
	public MoviesListAdapter(List<Movie> movieList, Context applicationContext) {
		this.movieList = movieList;
		this.context = applicationContext;
	}

	/**
	 * Update the Movie list
	 * 
	 * @param MovieList
	 */
	public void setMovieList(List<Movie> movieList) {
		this.movieList = movieList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return movieList.size();
	}

	@Override
	public Object getItem(int position) {
		return movieList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactsViewHolder contactsViewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_list_row, null);
			contactsViewHolder = new ContactsViewHolder();
			contactsViewHolder.name = (TextView) convertView
					.findViewById(R.id.item);
		} else {
			contactsViewHolder = (ContactsViewHolder) convertView.getTag();
		}
		Movie movie = movieList.get(position);

		contactsViewHolder.name.setText(movie.getName());

		convertView.setTag(R.id.fun_id, movie);
		convertView.setTag(contactsViewHolder);
		return convertView;
	}

	private class ContactsViewHolder {
		TextView name;
	}
}
