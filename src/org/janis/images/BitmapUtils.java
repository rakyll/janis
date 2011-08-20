/*
 * @author kfgal
 */

package org.janis.images;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	/**
	 * Returns the cornered bitmap
	 * out of a bitmap input.
	 *
	 * @param bitmap input bitmap
	 * @param cornerSize the corner size you wish to see in output
	 * @return the cornered bitmap
	 */
	public static Bitmap cornerBitmap(Bitmap bitmap, float cornerSize){

		Bitmap corneredBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		
		Canvas canvas = new Canvas(corneredBitmap); 
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xff000000);
		
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		
		// put a rounded rect
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(new RectF(rect), cornerSize, cornerSize, paint);
		
		// mask the image with rounded rect
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return corneredBitmap;
	}
}
