/*
 * @author Burcu Dogan
 */

package org.janis.views;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

// TODO: provide a DefaultDrawableCacheImpl
/**
 * LoadingImageView helps set an image url directly to 
 * an image view. It asynchronously downloads, caches and 
 * shows the image.
 */
public class LoadingImageView extends ImageView {
	
	private static DrawableCache mCache;
	private DownloadHandler mDownloadHandler;
	private AsyncTask<String, Boolean, Drawable> mCurrentBackgroundTask;
	final private Object taskLock = new Object(); 
	
	/**
	 * Constructs a new loading image view.
	 *
	 * @param context the context
	 */
	public LoadingImageView(final Context context) {
		super(context);
	}

	/**
	 * If you will create an instance in layout XML, 
	 * insert a "src" attribute to the LoadingImageView 
	 * tag and set it to the URL
	 * of the remote image.
	 * E.g.: <org.janis.LoadingImageView 
	 * src="http://.../images/...png" />
	 *
	 * @param context the context
	 * @param attrSet the attr set
	 */
	public LoadingImageView(final Context context, 
			final AttributeSet attrSet) {
		
		super(context, attrSet);
		final String url = attrSet.getAttributeValue(null, "src");
		setImageUrl(url);
	}

	/**
	 * Constructs a new loading image view with a remote
	 * image url.
	 *
	 * @param context the Activity context
	 * @param url the Image URL you wish to load
	 */
	public LoadingImageView(final Context context, final String url) {
		super(context);
		setImageUrl(url);        
	}

	
	/**
	 * Download handler includes callback methods.
	 *
	 * @param downloadHandlerImpl the new download handler
	 */
	public void setDownloadHandler(DownloadHandler downloadHandlerImpl){
		this.mDownloadHandler = downloadHandlerImpl;
	}
	
	/**
	 * Sets a new cache instance, if no cache is presented
	 * caching wont perform.
	 *
	 * @param cache the new cache
	 */
	public void setCache(DrawableCache cache){
		mCache = cache;
	}

	/**
	 * Set's the view's drawable, this uses the internet to retrieve the image
	 * don't forget to add the correct permissions to your manifest.
	 *
	 * @param imageUrl the url of the image you wish to load
	 */
	public void setImageUrl(final String imageUrl) {
		
		synchronized (taskLock) {
			// If there exists an existing task
			// cancel it, so only the lastly setted url
			// forks a download task
			if(mCurrentBackgroundTask != null){
				mCurrentBackgroundTask.cancel(true);
			}
			
			mCurrentBackgroundTask = new BackgroundTask();
			mCurrentBackgroundTask.execute(imageUrl);
		}
	}
	
	/**
	 * Gets the drawable with the given url from cache.
	 * If cache doesnt have the image, it returns null.
	 *
	 * @param url The url of the image
	 * @return drawable from the cache
	 */
	public static Drawable getFromCache(String url){
		if(mCache != null){
			return mCache.get(url);
		}
		return null;
	}
	
	/**
	 * Puts the drawable to the cache. If cache doesnt exist,
	 * it doesnt perform. If we recieve an OutOfMemoryException
	 * we clear the cache.
	 *
	 * @param url The url of the image
	 * @param drawable The drawable fetched from the url, cant be null
	 */
	public static void putToCache(String url, Drawable drawable){
		if(mCache != null && url != null && drawable != null){
			try {
				mCache.put(url, drawable);
			} catch(OutOfMemoryError e){ 
				// ignores the current put
				// TODO: may implement retry here              
				e.printStackTrace();
				mCache.clear();
			}
		}
	}

	/**
	 * Creates a new drawable from the given url. Checks the
	 * cache before hitting to url. If cache doesnt have the
	 * image already, it's fetched from the URL. Once IO
	 * is completed downloaded image is put to cache.
	 *
	 * @param url the url of the image
	 * @return a drawable
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws MalformedURLException the malformed url exception
	 */
	private static Drawable getDrawableFromUrl(final String url) 
		throws IOException, MalformedURLException {

		if(url == null){
			return null;
		}
		
		Drawable image = getFromCache(url);
		if(image == null){
			image = Drawable.createFromStream(((InputStream)
					new URL(url).getContent()), "name");
			putToCache(url, image);
		}
		return image;
	}
	
	
	/**
	 * DrawableCache interface forces you to implement a 
	 * drawable cache gets and puts images with a url.
	 */
	public interface DrawableCache {
		
		/**
		 * Gets a drawable by a url.
		 *
		 * @param url The image's url.
		 * @return the cached drawable if exists.
		 */
		Drawable get(String url);
		
		/**
		 * Puts a drawable to the cache.
		 *
		 * @param url The image's url
		 * @param imageDrawable The downloaded drawable.
		 */
		void put(String url, Drawable imageDrawable);
		
		/**
		 * Performs a full clean on the cache.
		 */
		void clear();
	}
	
	/**
	 * If you would like to notified when image download 
	 * is either finished or failed, you can set a DownloadHandler
	 * to subscribe onDrawableDownloaded and onException
	 * states.
	 */
	public interface DownloadHandler {
		
		/**
		 * Will be called once a drawable is downloaded
		 * You may retrieve the drawable, manipulate it
		 * and set it again.
		 *
		 * @param drawable The downloaded drawable.
		 */
		void onDrawableDownloaded(Drawable drawable);
		
		/**
		 * On exception.
		 *
		 * @param e The thrown exception during the download.
		 */
		void onException(Exception e);
	}
	
	/**
	 * Background task that download the image
	 * and sets the downloaded image to this view.
	 */
	private class BackgroundTask 
		extends AsyncTask<String, Boolean, Drawable>{
		
		private Exception e;

		/* The background task to download 
		 * the remote image as a Drawable.
		 */
		@Override
		protected Drawable doInBackground(String... params) {
			try {
				return getDrawableFromUrl(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				this.e = e;
			} 
			return null;
		}
		
		/* When download task is finished, run the handler
		 * callback and set the image drawable. 
		 * If an exception is thrown while downloading
		 * notify the handler.
		 */
		@Override
		protected void onPostExecute(Drawable result) {
			super.onPostExecute(result);
			
			// TODO: what to do if an download handler
			// is not set. Make the exception more visible.
			if (e != null) {
				if (mDownloadHandler != null){
					mDownloadHandler.onException(e);
				}
			} else {
				if (mDownloadHandler != null){
					mDownloadHandler.onDrawableDownloaded(result);
				}
				setImageDrawable(result);
			}
		}
	}
}
