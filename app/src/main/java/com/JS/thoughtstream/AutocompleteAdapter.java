package com.JS.thoughtstream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/*
 * This is hacky, and slow. so slow it's not fun to use on the UI end
 * Therefore until this is fixed for the better, DO NOT USE
 * TODO
 */
public class AutocompleteAdapter extends ArrayAdapter<String> implements Filterable{

	private static final String TAG = "AutocompleteAdapter";
	private List<String> mCompletions;
	private List<String> mOriginalValues;
	private final Object mLock = new Object();

    private Filter mFilter;
    
    private String mLastRemaining = null;
    
	
	public AutocompleteAdapter(Context context, int resId, List<String> completions) {
		super(context, resId, completions);
		mCompletions = completions;
		
	}

	@Override
    public Filter getFilter() {

        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }
	
	
	public String getLastRemaining() {
		return mLastRemaining;
	}
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
        	
        	FilterResults results = new FilterResults();

        	
        	if (prefix.equals((CharSequence)mLastRemaining)) {
        		return	results;

        	}

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<String>(mCompletions);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<String> list;
                synchronized (mLock) {
                    list = new ArrayList<String>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase(Locale.US);
                
                ArrayList<String> values;
                synchronized (mLock) {
                    values = new ArrayList<String>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<String>();

                for (int i = 0; i < count; i++) {
                    final String value = values.get(i);
                    final String valueText = value.toString().toLowerCase(Locale.US);

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mCompletions = (List<String>) results.values;
            if (results.count > 0) {
            	if (results.count == 1) {
            		mLastRemaining = mCompletions.get(0);
            	}
            	Log.i(TAG, "notifying");
            	clear(); //this is now much slower than before :(
                for (String s : mCompletions) {
                	add(s);
                }
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}


