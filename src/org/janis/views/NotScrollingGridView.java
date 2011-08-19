/*
 * @author Burcu Dogan
 */

package org.janis.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

public class NotScrollingGridView extends GridView {
	
	private int mNumCols;
	private int mVerticalSpacing;
	
	public NotScrollingGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}
	
	public NotScrollingGridView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	private void init(AttributeSet attrs){
		// TODO: Should janis ignore default Android
		// namespace or work with it.
		// It's not very common not to use default android ns,
		// but it's possible
		mNumCols = attrs
			.getAttributeIntValue(null, "numColumns", 0);
		mVerticalSpacing = attrs
			.getAttributeIntValue(null, "verticalSpacing", 0);
		
		setNumColumns(mNumCols);
		setVerticalSpacing(mVerticalSpacing);
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		setHeightBasedOnItems();
	}
	
	/**
	 * Sets the height based on items.
	 * So, grid view doesn't scroll. But this avoids
	 * item view caching and could cause out of memory
	 * space problems.
	 */
	public void setHeightBasedOnItems() {
		ListAdapter adapter = getAdapter();
		// If no adapter, it's impossible to calculate
		// the height
		
		if (adapter == null || mNumCols <= 0) return;

		int height = 0;
		int width = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
		int numVerticalItems = (int) Math.ceil(adapter.getCount()/mNumCols) + 1;
		
		for (int i = 0; i < numVerticalItems; i++) {
			// Create a grid item to measure height
			View itemView = adapter.getView(i, null, this);
			itemView.measure(width, MeasureSpec.UNSPECIFIED);
			height += itemView.getMeasuredHeight();
		}
		
		int newHeight = (2*numVerticalItems) * mVerticalSpacing + height;
		
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
