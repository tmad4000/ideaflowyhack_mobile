package com.JS.thoughtstream;
import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.MultiAutoCompleteTextView;

public class InstantAutoComplete extends MultiAutoCompleteTextView {

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

 

    


}