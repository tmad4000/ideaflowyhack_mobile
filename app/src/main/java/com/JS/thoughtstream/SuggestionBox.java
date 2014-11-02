package com.JS.thoughtstream;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;

public class SuggestionBox {

	private final static String TAG = "SuggestionBox";
	private int mMapId;
	private String mName;
	
	public SuggestionBox(int id, String text) {
		mMapId = id;
		mName = text;
	}
	
	public int getId() {
		return mMapId;
	}
	
	public String getName() {
		return mName;
	}
	

	public CharSequence toCharSequence() {
		Log.i(TAG, "Text: " + getName());
	    SpannableString spannable = new SpannableString(getName());
	    int length = spannable.length();
	    if (length > 0) {
	        spannable.setSpan(
	                new SuggestionBoxSpan(this),
	                0,
	                length,
	                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
	        );
	    }
	    return spannable;
	}
	
	@Override
	public String toString() {
		return mName;
	}
}
 