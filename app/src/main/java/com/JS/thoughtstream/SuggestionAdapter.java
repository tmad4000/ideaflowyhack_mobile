package com.JS.thoughtstream;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class SuggestionAdapter extends ArrayAdapter<Suggestion> {

	private final static String TAG = "SuggestionAdapter";
	
	private Context mContext;
	private int mResId;
	private List<Suggestion> mSuggestions;
	
	public SuggestionAdapter(Context context, int resId, List<Suggestion> suggestions) {
		super(context, resId, suggestions);
		mContext = context;
		mResId = resId;
		Log.i(TAG, "resId: " + resId);
		mSuggestions = suggestions;
	}
	
	/*public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SuggestionHolder holder = null; //more efficient for longer lists -- recycles references
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(mResId, null);
            holder = new SuggestionHolder();
            //this is the expensive operation
            holder.textView = (TextView)row.findViewById(R.id.suggestion_list_item_text);
            row.setTag(holder);
        }
        holder = (SuggestionHolder)row.getTag();
        
        Spannable text = new SpannableString("");
        if (position <= mSuggestions.size()) {
        	text = mSuggestions.get(position).getText();
        }
        holder.textView.setText(text, BufferType.SPANNABLE);
       
        return row;
    }*/
    
    static class SuggestionHolder {
    	TextView textView;
    }
}
