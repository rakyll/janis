/*
 * @author Burcu Dogan
 */
package org.janis.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class NotScrollingListView extends ListView {

	
	public NotScrollingListView(Context context) {
		super(context);
	}
	
	public NotScrollingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public NotScrollingListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * Sets a new ListAdapter to the listView. After,
	 * adapter is set, it calls setHeightBasedOnItems
	 * and changes the height of the listView depending
	 * on the number of the items. So, listView wont scroll 
	 * and can be used in a separate ScrollView. 
	 */
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		setHeightBasedOnItems();
	}
	
	/**
	 * Sets the height based on items.
	 */
	public void setHeightBasedOnItems() {
		ListAdapter adapter = getAdapter();
		// If no adapter, it's impossible to calculate
		// the height
		if (adapter == null) return;

		int height = 0;
		int width = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
		for (int i = 0; i < adapter.getCount(); i++) {
			// Create a row for measurements
			View itemView = adapter.getView(i, null, this);
			itemView.measure(width, MeasureSpec.UNSPECIFIED);
			height += itemView.getMeasuredHeight();
		}
		
		// The final height is row heights + divider heights
		int newHeight = height + (getDividerHeight() * (getCount() - 1));

		ViewGroup.LayoutParams params = getLayoutParams();
		if(params == null){
			params = new ViewGroup.LayoutParams(width, newHeight);
		} else {
			params.height = newHeight;
		}
		
		setLayoutParams(params);
		requestLayout();
	}

}
