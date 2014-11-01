package com.JS.thoughtstream;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView.Tokenizer;
import android.widget.TextView;
import com.firebase.client.Firebase;
import java.io.*;

public class MainActivity extends Activity {
    public final String aURL = "https://docs-examples.firebaseio.com";
    public final String FILENAME = "SAVE";
    private GestureDetectorCompat mDetector;
    EditText textBox;

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILENAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString + "\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        Firebase rootRef = new Firebase("https://docs-examples.firebaseio.com/web/data");
        String input = readFromFile();
        setContentView(R.layout.activity_main);
        final EditText aEditText = (EditText) findViewById(R.id.editText);
        aEditText.setText(input);

        TextView doubleTapDetect = (TextView) findViewById(R.id.textView);
        doubleTapDetect.setText("");
        doubleTapDetect.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                MyGestureListener a = new MyGestureListener(aEditText);
                return a.onDoubleTapEvent(event);
            }
        });
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        private EditText aEditText;

        MyGestureListener(EditText pEditText){
            aEditText = pEditText;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event){
            aEditText.getText().replace(0,0,"\n");
            return true;
        }
    }

    public void onDestroy(){
        final TextView output = (TextView) findViewById(R.id.editText);
        writeToFile(output.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
