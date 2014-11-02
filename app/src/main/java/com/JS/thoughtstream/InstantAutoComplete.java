package com.JS.thoughtstream;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.SpannedString;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.MultiAutoCompleteTextView;

public class InstantAutoComplete extends MultiAutoCompleteTextView {

	private final static String TAG = "InstantAutoComplete";
	
	private SuggestionBoxSpan mPreviouslySelectedSpan = null;
	
    public InstantAutoComplete(Context context) {
        super(context);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        
    }
    
  /*  @Override
    public void setThreshold(int threshold) {
       // if (threshold <= 0) {
      //      threshold = 1;
       // }

        mThreshold = threshold;
    }
    */
    
    @Override
    public int getThreshold() {
        return 0;
    }
    
    @Override
    public boolean enoughToFilter() {
        try {
        	return super.enoughToFilter();
        	
        } catch(NoAtException e){
        	return false;
        }
    }
    
    @Override
    protected void replaceText(CharSequence text) {
    	Log.i(TAG, "REPLACING TEXT");
    	super.replaceText(text);
    	
    }
    
    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
    	//only if it's a cursor not a selection
    	if (selStart == selEnd) {
    		SuggestionBoxSpan[] spans = getText().getSpans(selStart, selEnd, SuggestionBoxSpan.class);
    		if (spans.length > 0) {
    			if (mPreviouslySelectedSpan != null && mPreviouslySelectedSpan != spans[0]) { //different span
    				mPreviouslySelectedSpan.disableHighlight();
    			}
    			spans[0].enableHighlight();
    			mPreviouslySelectedSpan = spans[0];
    		} else { //we've left the span
    			if (mPreviouslySelectedSpan != null) {
    				mPreviouslySelectedSpan.disableHighlight();
    				mPreviouslySelectedSpan = null;
    			}
    		}
    	}
    }
    
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    	return new InstantAutoInputConnection(super.onCreateInputConnection(outAttrs),true);
    }

    @Override
    public CharSequence convertSelectionToString(Object obj) {
    	return new SpannableString(super.convertSelectionToString(obj));
    }
    
 /*
  * NOT IN USE BECAUSE OF EXTREMELY SLOW EXECUTION
  * 
  *    @Override
    public void onFilterComplete(int count) {
    	Log.i(TAG, "Count: " + count);
    	super.onFilterComplete(count);
    	if(count == 1) {
    		//clearListSelection();
    		//Log.i(TAG, "Is showing: " + isPopupShowing());
    		//setListSelection(0);
    		//Log.i(TAG, "Selected: "+getListSelection());
    		String lastOption = ((AutocompleteAdapter) getAdapter()).getLastRemaining();
    		Log.i(TAG, "Last one: " + lastOption);
    		replaceText(new SpannedString(lastOption)); //need to pass a spannedString for further down the road
    		dismissDropDown();
    	}
    }
    */
 
    
    private class InstantAutoInputConnection extends InputConnectionWrapper {
    	
    	public InstantAutoInputConnection(InputConnection target, boolean mutable) {
    		super(target, mutable);
    	}
    	
    	
    	//apparently this works for Jelly Bean
        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {       
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
        
    	
        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                Editable buffer = getText();
			    int start = Selection.getSelectionStart(buffer);
			    int end = Selection.getSelectionEnd(buffer);
			    if (start == end) {
			        SuggestionBoxSpan[] spans = buffer.getSpans(start, end, SuggestionBoxSpan.class);
			        if (spans.length > 0) {
			            buffer.replace(
			                    buffer.getSpanStart(spans[0])+1, //without the +1 it was also deleting the @
			                    buffer.getSpanEnd(spans[0]),
			                    ""
			            );
			            buffer.removeSpan(spans[0]);
			        }
			    }
                
                // Un-comment if you wish to cancel the backspace:
                // return false;
            }
            return super.sendKeyEvent(event);
        }
    }

    


}