package org.janis;

import java.lang.reflect.Field;

import android.app.Activity;
import android.view.View;

public abstract class BaseActivity extends Activity {
	
	/* Overriding the Activity.setContentView to auto
	 * bind activity's fields to layout xml View objects.
	 */
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		bind();
	}
	
	/**
	 * Binds the activity fields to instances created
	 * in the set layout xml.
	 */
	private void bind(){
		// bind members to xml view instances
		try {
			Field[] fields = getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++){
				Field field = fields[i];
            	
				// TODO: failsafe if identifier is not presented
				if(isFieldAView(field)){
					int identifier = getResources()
						.getIdentifier(field.getName(), "id", this.getPackageName());
					field.set(this, findViewById(identifier));
				} 
			}
		} catch (Throwable e) {
			System.err.println(e);
		}
	}
	
	/**
	 * Checks if given field's type is a subclass of View
	 * or View's itself.
	 *
	 * @param field
	 * @return true, if is field's type a View
	 */
	@SuppressWarnings("unchecked")
	private boolean isFieldAView(Field f){
		Class c = f.getType();
		while(c != Object.class){
			if(c == View.class) return true;
			c = c.getSuperclass();
		}
		return false;
	}
}
