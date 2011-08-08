/*
 * @author Burcu Dogan
 */

package com.dogan.androidutils.views;

import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * The Class LoadingListView.
 */
public class LoadingListView extends ListView {
	
	private OnMoreHandler mOnMoreHandler;
	private OnScrollListener mOnScrollListener;
	private BackgroundTask mCurrentBackgroundTask;
	
	/**
	 * Constructs a new LoadingListView
	 * and inits the default OnScrollListener 
	 * that auto fires OnMoreHandler.run()
	 *
	 * @param context
	 */
	public LoadingListView(Context context) {
		super(context);
		init();
	}
	
	/**
	 * Construct a new LoadingListView
	 * with xml tag attributes. Also inits 
	 * the default OnScrollListener that auto 
	 * fires OnMoreHandler.run()
	 *
	 * @param context
	 * @param attrs
	 */
	public LoadingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	/**
	 * Construct a new LoadingListView
	 * with xml tag attributes and style. 
	 * Also inits the default OnScrollListener 
	 * that auto fires OnMoreHandler.run()
	 *
	 * @param context 
	 * @param attrs 
	 * @param defStyle
	 */
	public LoadingListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	/**
	 * Inits the OnScollListener to detect when
	 * listView is scrolled down to the bottom.
	 * When list view reaches to bottom, an AsyncTask
	 * is started to be executed and performs
	 * actions set by setOnMoreHandler(...)
	 */
	private void init(){
		
		super.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(
					AbsListView view, int scrollState) {
				
				// If list view has its own OnScollListener,
				// perform its own onScrollStateChanged
				if(mOnScrollListener != null){
					mOnScrollListener
						.onScrollStateChanged(view, scrollState);
				}
			}
			
			@Override
			public void onScroll(
					AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				// If list view has its own OnScollListener,
				// perform its own onScroll
				if(mOnScrollListener != null){
					mOnScrollListener
						.onScroll(view, 
								firstVisibleItem, 
								visibleItemCount, 
								totalItemCount);
				}
				
				// If list view is reached to the bottom
				if (getLastVisiblePosition() == (totalItemCount - 1)){
					// start background task here
					// if there is no pending/running task
					if(mCurrentBackgroundTask == null
							|| mCurrentBackgroundTask.getStatus() == Status.FINISHED){
						mCurrentBackgroundTask = new BackgroundTask();
						mCurrentBackgroundTask.execute();
					}
				} // end of get last visible
			}
		});
	}	
	
	/**
	 * Gets the on more handler.
	 *
	 * @return the on more handler
	 */
	public OnMoreHandler getOnMoreHandler() {
		return mOnMoreHandler;
	}

	/**
	 * Gets the onScrollListener.
	 *
	 * @return onScrollListener
	 */
	public OnScrollListener getOnScrollListener() {
		return mOnScrollListener;
	}

	/**
	 * Sets the onMoreHandler.
	 *
	 * @param onMoreHandler
	 */
	public void setOnMoreHandler(OnMoreHandler onMoreHandler) {
		mOnMoreHandler = onMoreHandler;
	}
	
	/**
	 * Sets the onScrollListener
	 *
	 * @param onScrollListener
	 */
	public void setScrollListener(OnScrollListener listener){
		mOnScrollListener = listener;
	}
	
	/**
	 * OnMoreHandler interface should be implemented
	 * to set actions when list view scrolled to the
	 * bottom.
	 */
	public interface OnMoreHandler {
		
		/**
		 * Executed in the main thread before run
		 * is executed. 
		 *
		 * @param listView
		 */
		void onPreExecute(LoadingListView listView);
		
		/**
		 * Executed in the main thread after run
		 * is executed. Set new data to the list 
		 * adapter here.
		 *
		 * @param listView
		 */
		void onPostExecute(LoadingListView listView);
		
		/**
		 * The method that runs in a background
		 * thread that is responsible to fetch
		 * more data
		 *
		 * @param listView 
		 */
		void run(LoadingListView listView);
	}
	
	/**
	 * BackgroundTask is responsible to run
	 * OnMoreHandler methods as an AsyncTask.
	 */
	private class BackgroundTask extends AsyncTask<Void, Boolean, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			if(mOnMoreHandler != null){
				mOnMoreHandler.run(LoadingListView.this);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(mOnMoreHandler != null){
				mOnMoreHandler.onPostExecute(LoadingListView.this);
			}
			super.onPostExecute(result);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mOnMoreHandler != null){
				mOnMoreHandler.onPreExecute(LoadingListView.this);
			}
		}
	}

}
