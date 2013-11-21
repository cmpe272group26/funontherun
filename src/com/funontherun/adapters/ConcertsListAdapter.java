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
import com.funontherun.entities.Concert;

public class ConcertsListAdapter extends BaseAdapter {
	private List<Concert> concertList = new ArrayList<Concert>();
	private Context context;

	/**
	 * 
	 * @param categoryList
	 * @param applicationContext
	 */
	public ConcertsListAdapter(List<Concert> concertList,
			Context applicationContext) {
		this.concertList = concertList;
		this.context = applicationContext;
	}

	/**
	 * Update the concert list
	 * 
	 * @param concertList
	 */
	public void setConcertList(List<Concert> concertList) {
		this.concertList = concertList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return concertList.size();
	}

	@Override
	public Object getItem(int position) {
		return concertList.get(position);
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
		Concert concert = concertList.get(position);

		contactsViewHolder.name.setText(concert.getTitle());

		convertView.setTag(R.id.fun_id, concert);
		convertView.setTag(contactsViewHolder);
		return convertView;
	}

	private class ContactsViewHolder {
		TextView name;
	}
}
