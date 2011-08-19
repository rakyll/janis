/*
 * @author Burcu Dogan
 */

package org.janis.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ListAdapter;

public class NotScrollingGridView extends GridView {
	
	public NotScrollingGridView(Context context) {
		super(context);
	}

	public NotScrollingGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public NotScrollingGridView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		setHeightBasedOnItems();
	}
	
	/**
	 * Sets the height based on items.
	 */
	public void setHeightBasedOnItems() {
		// TODO
	}

}
