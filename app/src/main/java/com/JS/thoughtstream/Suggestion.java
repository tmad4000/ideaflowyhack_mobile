package com.JS.thoughtstream;

import java.util.ArrayList;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;

public class Suggestion {
	private final static String TAG = "Suggestion";
	private String mText;
	//mBoxes should be an arrayList because in the future we may want to add more boxes to send to
	private ArrayList<SuggestionBox> mBoxes;
	
	public Suggestion(String text, ArrayList<SuggestionBox> boxes) {
		mText = text;
		mBoxes = boxes;
	}
	
	public Spannable getText() {
		/*
		 * Apparently this, which to makes a huge amount of sense, isn't the way to do it...
		 * 
		SpannableStringBuilder builder = new SpannableStringBuilder();
		builder.append(mText);
		SpannableString spannedBox;
		for (SuggestionBox box : mBoxes) {
			builder.append("@");
			spannedBox = new SpannableString(box.getName());
			spannedBox.setSpan(new SuggestionBoxSpan(box), 0, box.getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			builder.append(spannedBox);
			builder.append(" ");
		}
		return builder.toString();
		*/
		
		//instead supposed to do it like this:
		
		StringBuilder b = new StringBuilder(mText);
		int startPos = b.length();
		Log.i(TAG, "building string: starting with:||" + b.toString() + "||");
		if (!b.toString().endsWith(" ")) {
			b.append(" "); //add a trailing space if not there
			startPos++;
		}
		for (int i = 0; i < mBoxes.size(); i++) {
			b.append("@");
			b.append(mBoxes.get(i).getName());
			b.append(" ");
		}
		SpannableStringBuilder str = new SpannableStringBuilder(b.toString());
		//This seems like a very brittle way of doing this
		//startPos should be the first @
		int end;
		for (int i = 0; i < mBoxes.size(); i++) {
			end = startPos + mBoxes.get(i).getName().length() + 1;
			str.setSpan(new SuggestionBoxSpan(mBoxes.get(i)), startPos, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			startPos = end + 1; //skip the trailing " "
		}
		
		return str;
		
	}
	
	public String[] getTargetMapids() {
		String[] mapids = new String[mBoxes.size()];
		for (int i = 0; i < mapids.length; i++) {
			mapids[i] = Integer.toString(mBoxes.get(i).getId());
		}
		return mapids;
	}

}

