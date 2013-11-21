package com.funontherun.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.funontherun.ApplicationEx;
import com.funontherun.R;
import com.funontherun.entities.Category;

public class CategoryListAdapter extends BaseAdapter {
	private List<Category> categoryList = new ArrayList<Category>();
	private Context context;

	/**
	 * 
	 * @param categoryList
	 * @param applicationContext
	 */
	public CategoryListAdapter(List<Category> categoryList,
			Context applicationContext) {
		this.categoryList = categoryList;
		this.context = applicationContext;
	}

	/**
	 * Update the category list
	 * 
	 * @param categoryList
	 */
	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return categoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return categoryList.get(position);
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
		Category category = categoryList.get(position);
		if (ApplicationEx.key.equalsIgnoreCase("Sightseeing")
				|| ApplicationEx.key.equalsIgnoreCase("Services")) {
			String categoryStr = "<font color=#cc0029>" + category.getName()
					+ ", " + "</font> <font color=#ffcc00>"
					+ category.getTypes() + "</font>";
			contactsViewHolder.name.setText(Html.fromHtml(categoryStr));

			// contactsViewHolder.name.setText(category.getName() + ", "
			// + category.getTypes());
		} else
			contactsViewHolder.name.setText(category.getName());
		convertView.setTag(R.id.fun_id, category);
		convertView.setTag(contactsViewHolder);
		return convertView;
	}

	private class ContactsViewHolder {
		TextView name;
	}
}
