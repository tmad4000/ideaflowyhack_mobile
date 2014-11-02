package com.JS.thoughtstream;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;


/*
 * found basic code for this at 
 * http://ballardhack.wordpress.com/2011/07/25/customizing-the-android-edittext-behavior-with-spans/
 */

/*
 * this is only indirectly touched via SuggestionBox.toCharSequence()
 */
public class SuggestionBoxSpan extends UnderlineSpan{
	private final static String TAG = "SuggestionBoxSpan";
	
	private boolean mHighlight = false;
	
	private SuggestionBox mBox;

	public SuggestionBoxSpan(SuggestionBox box) {
		super();
		this.mBox = box;
	}
	
	public SuggestionBox getBox() {
		return mBox;
	}

	public void enableHighlight() {
		mHighlight = true;
	}
	
	public void disableHighlight() {
		mHighlight = false;
		Log.i(TAG, "Disabling");
	}
	
	@Override
	public void updateDrawState(TextPaint ds) {
		
		ds.setColor(Color.RED);
		ds.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
		
		if (mHighlight) {
			ds.bgColor = Color.rgb(220, 230, 255); //light blue
		}
		super.updateDrawState(ds);
	}

}
