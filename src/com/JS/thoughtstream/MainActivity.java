package com.JS.thoughtstream;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

public class MainActivity extends Activity {

	EditText textBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//textBox = (EditText)findViewById(R.id.text_box);
		
		final InstantAutoComplete inputEditText = (InstantAutoComplete) findViewById(R.id.multi_box);

		String[] COUNTRIES = new String[] { "Belgium", "France", "Italy", "Germany", "Spain" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
		inputEditText.setAdapter(adapter);
		inputEditText.setThreshold(1); //Set number of characters before the dropdown should be shown

		//Create a new Tokenizer which will get text after '@' and terminate on ' '
		inputEditText.setTokenizer(new Tokenizer() {

		  @Override
		  public CharSequence terminateToken(CharSequence text) {
		    int i = text.length();

		    while (i > 0 && text.charAt(i - 1) == ' ') {
		      i--;
		    }

		    if (i > 0 && text.charAt(i - 1) == ' ') {
		      return text;
		    } else {
		      if (text instanceof Spanned) {
		        SpannableString sp = new SpannableString(text + " ");
		        TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
		        return sp;
		      } else {
		        return text + " ";
		      }
		    }
		  }

		  @Override
		  public int findTokenStart(CharSequence text, int cursor) throws NoAtException {
		    int i = cursor;

		    while (i > 0 && text.charAt(i - 1) != '@') {
		      i--;
		    }

		    //Check if token really started with @, else we don't have a valid token
		    if (i < 1 || text.charAt(i - 1) != '@') {
		    	throw new NoAtException("no @");
		    
		    //  return cursor;
		    }

		    return i;
		  }

		  @Override
		  public int findTokenEnd(CharSequence text, int cursor) {
		    int i = cursor;
		    int len = text.length();

		    while (i < len) {
		      if (text.charAt(i) == ' ') {
		        return i;
		      } else {
		        i++;
		      }
		    }

		    return len;
		  }
		});
	
		inputEditText.addTextChangedListener(new TextWatcher() {

			  @Override
			  public void onTextChanged(CharSequence s, int start, int before, int count) {
			    Layout layout = inputEditText.getLayout();
			    int pos = inputEditText.getSelectionStart();
			    int line = layout.getLineForOffset(pos);
			    int baseline = layout.getLineBaseline(line);

			    int bottom = inputEditText.getHeight();

			    inputEditText.setDropDownVerticalOffset(0);

			  }

			  @Override
			  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			  }

			  @Override
			  public void afterTextChanged(Editable s) {
			  }
			});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
