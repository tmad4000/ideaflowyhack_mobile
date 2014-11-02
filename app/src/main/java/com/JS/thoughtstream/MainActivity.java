package com.JS.thoughtstream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView.Tokenizer;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/* Uses hashmap to store ideas then everytime notes is initialized, the idea objects are added
 *  Everytime submit is pressed, we parse the text for @'s and then if the handle is in the map already, we don't do anything
 *  If the handle is not in the map, we add it to the map.
 *  Whenever something is changed, the relative Idea in the map is updated and the submit boolean within the idea is set to true meaning it will submit.
 *  Problems: Multiple handles in same file causes problems
 *            The suggestion must auto complete right now to work so when you reopen file, you must autocomplete the suggestions
 *            Colors has weird out of bounds exceptions
 *            Date should work, but disabled for now for clarity
 */

public class MainActivity extends Activity{
    private Date aDate;
    private InstantAutoComplete aEditText;
    private IdeasProcessor ideasProcessor;
    private ArrayList<Suggestion> mSuggestions;
    private HashMap<String, Idea> aIdeas = new HashMap<String, Idea>();

    // for the @ autocomplete
    private ArrayList<String> mAutocompleteOptions; // for the adatper
    private ArrayAdapter<String> mAutocompleteAdapter;
    private Map<String, JSONObject> mAutocompleteOptionsMap; // for looking up

    private final static String FILENAME = "SAVE";
    private final static String SUGGESTIONS_DATA_FILE = "data";
    private final static String AT_AUTCOMPLETE_FILE = "atPrefsFile";
    private final static String TAG = "MainActivity";
    private final static String FIRST_TIME_PREF = "FirstTime";
    private final static String SAVED_SUGGESTION_PREF = "SavedSuggestion";
    private final static String BASE_URL =
            "http://instadefine.com/IdeaOverflow/Outlinr-PHP/public_html/ideajoin/public_html/ajax/get_or_make_post.php";
    //

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
                int objects = 0;
                try{
                    objects = Integer.parseInt(bufferedReader.readLine());
                } catch(Exception e){
                    Log.e(TAG, "FILE IS NEW");
                }
                for(int i = 0; i < objects; i++){
                    String idea = bufferedReader.readLine();
                    Log.d(TAG, idea);
                    String[] params = idea.split(" ");
                    String handle = params[0];
                    int begin = Integer.parseInt(params[1]);
                    int end = Integer.parseInt(params[2]);
                    boolean submit = Boolean.parseBoolean(params[3]);
                    Idea aIdea = Idea.getIdea(handle);
                    aIdea.setBegin(begin);
                    aIdea.setEnd(end);
                    aIdea.setSubmit(submit);
                    aIdeas.put(handle, aIdea);
                }
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                /*long dateTime = new Date().getTime() - 864000000;
                try {
                    dateTime = Long.parseLong(bufferedReader.readLine());
                } catch(Exception e){
                    Log.d("login activity", "FILE IS NEW");
                }

                Date prevDate = new Date(dateTime);
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(prevDate);
                cal2.setTime(new Date());
                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR)){
                    stringBuilder.append(new Date() + "\n");
                }
                */
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

    /*public void setColors(Idea i){
            if(i.getSubmit()){
                SpannableStringBuilder sb = new SpannableStringBuilder(aEditText.getText().toString().substring(i.getBegin(), i.getEnd()));
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
                sb.setSpan(fcs, i.getBegin(), i.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                aEditText.getText().replace(i.getBegin(), i.getEnd(), sb);
            } else{
                SpannableStringBuilder sb = new SpannableStringBuilder(aEditText.getText().toString().substring(i.getBegin(), i.getEnd()));
                Log.d(TAG, String.valueOf(sb.toString().length()));
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(0, 0, 0));
                Log.d(TAG, String.valueOf(aEditText.getText().toString().charAt(i.getBegin())));
                Log.d(TAG, String.valueOf(aEditText.getText().toString().charAt(i.getEnd())));
                int test = i.getBegin();
                if(test != 0){
                    test -= 1;
                }
                sb.setSpan(fcs, test, i.getEnd()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                aEditText.getText().replace(i.getBegin(), i.getEnd(), sb);
            }
    }*/
    public void parseText(){
        String input = aEditText.getText().toString();
        for(int i = 0; i < input.length(); i++){
            if(input.charAt(i) == '@'){
                int begin = i;
                int end = i;
                if(begin != 0) {
                    while ((input.charAt(begin) != '\n') || (input.charAt(begin - 1) != '\n')) {
                        if (begin == 1) {
                            begin = 0;
                            break;
                        }
                        begin = begin - 1;
                    }
                }
                if(end != input.length() - 1) {
                    while (input.charAt(end) != '\n' || input.charAt(end + 1) != '\n') {
                        if (end == input.length() - 2) {
                            end = input.length() - 1;
                            break;
                        }
                        end = end + 1;
                    }
                }
                Log.d(TAG, aEditText.getText().toString().substring(begin, end));
                String handle = getHandle(aEditText.getText().toString().substring(begin, end));
                Idea potentialNewIdea = Idea.getIdea(handle);
                potentialNewIdea.setBegin(begin);
                potentialNewIdea.setEnd(end);
                if(aIdeas.containsValue(potentialNewIdea)){
                    aIdeas.get(handle).setBegin(begin);
                    aIdeas.get(handle).setEnd(end);
                } else{
                    aIdeas.put(handle, potentialNewIdea);
                }
                //setColors(potentialNewIdea);
            }
            //Log.d(TAG, String.valueOf(i));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String input = readFromFile();

        setContentView(R.layout.activity_main);
        //aEditText = (EditText) findViewById(R.id.editText);
        aEditText = (InstantAutoComplete) findViewById(R.id.editText);
        aEditText.setText(input, TextView.BufferType.SPANNABLE);
        Button aButton = (Button) findViewById(R.id.button);
        aButton.setText("Submit");
        aButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                parseText();
                for(Idea i : aIdeas.values()){
                    if(i.getSubmit()){
                        i.setSubmit(false);
                        Log.d(TAG, "IDEA HAS BEEN CHANGED YAAAAAAAAAAAAAAAAAAAAAAAAY");
                        sendQuery(i.getBegin(), i.getEnd());
                    }
                    //setColors(i);
                }
            }
        });

        aEditText.addTextChangedListener(new TextWatcher(){
            private Idea aIdea;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Layout layout = aEditText.getLayout();
                int pos = aEditText.getSelectionStart();
                int line = layout.getLineForOffset(pos);

                aEditText.setDropDownVerticalOffset(line + 50);

                int begin = start;
                if(begin == 0){
                    return;
                }
                int end = start;
                while((s.charAt(begin) != '\n') || (s.charAt(begin - 1) != '\n')){
                    if(begin == 1){
                        begin = 0;
                        break;
                    }
                    begin = begin - 1;
                }
                if(end == s.length() - 1){
                    return;
                }
                while(s.charAt(end) != '\n' || s.charAt(end + 1) != '\n'){
                    if(end == s.length() - 2){
                        end = s.length() -1;
                        break;
                    }
                    end = end + 1;
                }
                String handle = getHandle(aEditText.getText().toString().substring(begin, end));
                if(aIdeas.containsKey(handle)){
                    aIdea = aIdeas.get(handle);
                    aIdea.setBegin(begin);
                    aIdea.setEnd(end);
                    aIdea.setSubmit(true);
                    //setColors(aIdea);
                    Log.d(TAG, "SOMETHING CHANGED");
                }
                //sendQuery(begin, end);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        try {
            // load the list of suggestions made before and saved
            mSuggestions = loadSuggestions(getApplicationContext());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Data File doesn't exist yet");
            mSuggestions = new ArrayList<Suggestion>();
        }

        // initilize the autocomplete variables
        mAutocompleteOptions = new ArrayList<String>();
        mAutocompleteOptionsMap = new HashMap<String, JSONObject>();
        mAutocompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                mAutocompleteOptions);

        // load the possible @ autocompletes from shared preferences first
        buildAutocompleteSuggestions(getApplicationContext());

        // try{
        Log.i(TAG, "Size: " + mAutocompleteOptionsMap.size());
        Log.i(TAG, "test box id: " + mAutocompleteOptionsMap.get("Test box"));
        // } catch (JSONException e) {e.toString();}

        // meanwhile download an updated list off the internet
        AsyncGet get = new AsyncGet(new UpdateAutocompleteCallback());
        get.execute("http://instadefine.com/IdeaOverflow/Outlinr-PHP/public_html/ideajoin/public_html/ajax/get_or_make_post_ideamaps.php?mapid=0&newpost=");
        // Toast.makeText(this, "Downloading", Toast.LENGTH_SHORT).show();

        aEditText.setAdapter(mAutocompleteAdapter);
        aEditText.setThreshold(1); // Set number of characters before the
        // dropdown should be shown
        //aEditText.setMovementMethod(LinkMovementMethod.getInstance());

        // Create a new Tokenizer which will get text after '@' and terminate on
        // ' '
        aEditText.setTokenizer(new Tokenizer() {

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

                        String boxName = text.toString();
                        JSONObject box = mAutocompleteOptionsMap.get(boxName);
                        int id = 0;
                        try {
                            id = box.getInt("mapid");
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                            Log.e(TAG, "reverting to map id 0");
                        }
                        SuggestionBox boxObject = new SuggestionBox(id, boxName);
                        return boxObject.toCharSequence(); // we used to append
                        // a space with
                        // textutils but it
                        // was causing sort
                        // of error
                        // simplest solution
                        // was to just get
                        // rid of it. Fix it
                        // would be nice.
                        // TODO
                    } else {
                        return text + " ";
                    }
                }
            }

            @Override
            public int findTokenStart(CharSequence text, int cursor)
                    throws NoAtException {
                int i = cursor;

                while (i > 0 && text.charAt(i - 1) != '@') {
                    i--;
                }

                // Check if token really started with @, else we don't have a
                // valid token
                if (i < 1 || text.charAt(i - 1) != '@') {
                    throw new NoAtException("no @");

                    // return cursor;
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

        TextView TapDetect = (TextView) findViewById(R.id.textView);
        TapDetect.setText("");
        TapDetect.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                aEditText.getText().replace(0, 0, "\n");
                return true;
            }
        });
    }

    private void sendQuery(int start, int end){
        Spannable text = (Spannable) aEditText.getText().subSequence(start, end);

        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "No network...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (text.length() < 1) {
            Log.e(TAG, "No content!");
            return;
        }

        SuggestionBoxSpan[] suggestionSpans = text.getSpans(0,
                text.length(), SuggestionBoxSpan.class);
        for(SuggestionBoxSpan s: suggestionSpans){
            Log.d(TAG, s.getBox().getName());
        }
        ArrayList<SuggestionBox> boxes = new ArrayList<SuggestionBox>();

        String textExtract = text.toString();
        // cut out all the @SUGGESTION BOXES including leading @ and
        // trailing space
        // loop backward so we don't have to account for shifting string
        for (int i = suggestionSpans.length - 1; i >= 0; i--) {
            boxes.add(0, suggestionSpans[i].getBox());
            Log.d(TAG, suggestionSpans[i].getBox().getName());
            textExtract = textExtract.substring(0,
                    text.getSpanStart(suggestionSpans[i]) - 1)
                    + textExtract.substring(text
                    .getSpanEnd(suggestionSpans[i]));

        }
        textExtract = textExtract.trim();
        Log.i(TAG, "text of message:||" + textExtract + "||");
        if (textExtract.length() < 1) {
            Log.e(TAG, "No message written to send!");
            Toast.makeText(getApplicationContext(),
                    "Should probably write a message too...",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Suggestion suggestion = new Suggestion(textExtract, boxes);
        /*mSuggestionsAdapter.add(suggestion);
        mSuggestionsAdapter.notifyDataSetChanged();
        */
        String[] targetMapids = suggestion.getTargetMapids();
        Map<String, String> pairs = new HashMap<String, String>();
        // pairs.put("mapid", "HOLDER");
        pairs.put("newpost", textExtract);
        pairs.put("title", ""); // not needed
        pairs.put("uid", ""); // not needed either
        for (String s : targetMapids) {
            pairs.put("mapid", s);
            String getUrl = urlEncode(BASE_URL, pairs);
            AsyncGet get = new AsyncGet(new NewSuggestionCallback());
            get.execute(getUrl);
        }
    }

    public void onDestroy(){
        final TextView output = (TextView) findViewById(R.id.editText);

        String test = String.valueOf(aIdeas.size()) + "\n";
        Log.d(TAG, test);
        for(Idea i : aIdeas.values()){
            test += i.getHandle() + " " + i.getBegin() + " " + i.getEnd() + " " + i.getSubmit() + "\n";
        }
        test += output.getText().toString();
        writeToFile(test);
        ideasProcessor.process(test);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String urlEncode(String baseUrl, Map<String, String> values) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        String url = builder.build().toString();
        return url;

    }

    private String getHandle(String idea) {
        if(idea.contains("@")) {
            int start = idea.indexOf("@");
            int end = idea.indexOf(" ", start);
            int end2 = idea.indexOf("\n", start);
            if (end == -1) {
                end = idea.length();
            }
            if(end2 == -1){
                end2 = idea.length();
            }
            return idea.substring(start, Math.min(end, end2));
        } else{
            return "";
        }
    }

    /*
	 * Retrieves the suggestions we have saved from before
	 */
    public synchronized ArrayList<Suggestion> loadSuggestions(Context context)
            throws FileNotFoundException {
        ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
        BufferedReader file;
        try {
            // open a buffered reader of the indicated file
            file = new BufferedReader(new InputStreamReader(
                    context.openFileInput(SUGGESTIONS_DATA_FILE)));
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            throw new FileNotFoundException("No such file");
        }

        Gson gson = new Gson();

        try {
            String s = file.readLine();
            while (s != null) {
                suggestions.add(gson.fromJson(s, Suggestion.class));
                s = file.readLine();
                // read the next line. If it's blank, exit loop

            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return new ArrayList<Suggestion>();
        }
		/*
		 * } catch (NullPointerException e) { e.printStackTrace(); Log.e(TAG,
		 * "null pointer exception: " + e.toString()); }
		 */

        return suggestions;
    }

    /*
     * Saves the suggestions
     */
    public synchronized void writeSuggestions(Context context,
                                              ArrayList<Suggestion> suggestions) {
        BufferedWriter file;
        try {
            // open buffered writer for that new file
            file = new BufferedWriter(new OutputStreamWriter(
                    context.openFileOutput(SUGGESTIONS_DATA_FILE,
                            context.MODE_PRIVATE)));
        } catch (FileNotFoundException e) {
            Log.e(TAG,
                    "FAILED! Some file error creating new file \n"
                            + e.toString());
            return;
        }

        Gson gson = new Gson();

        try {
            for (Suggestion s : suggestions) {
                file.write(gson.toJson(s));
                file.write("\n"); // end of one entry
            }
            file.close();
        } catch (IOException e) {
            Log.e(TAG, "IO exception " + e.toString());
        }
    }

    // this should only be called in the onCreate
    public synchronized void buildAutocompleteSuggestions(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                AT_AUTCOMPLETE_FILE, 0);
        Map<String, String> optionsMap = (Map<String, String>) settings
                .getAll();

        mAutocompleteOptions.clear();
        mAutocompleteOptionsMap.clear();
        for (Map.Entry<String, String> s : optionsMap.entrySet()) {
            // Log.i(TAG, s.getKey());
            mAutocompleteOptions.add(s.getKey().trim());
            try {
                mAutocompleteOptionsMap.put(s.getKey().trim(),
                        new JSONObject(s.getValue()));
            } catch (JSONException e) {
                Log.e(TAG, "could not convert to JSONObject: " + s.getValue());
                Log.e(TAG, e.toString());
            }
        }
        Collections.sort(mAutocompleteOptions, Collections.reverseOrder());
        mAutocompleteAdapter.notifyDataSetChanged();
    }

    // maybe call this in onPause only
    public synchronized void saveAutocompleteSuggestions(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                AT_AUTCOMPLETE_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        for (Map.Entry<String, JSONObject> e : mAutocompleteOptionsMap
                .entrySet()) {
            editor.putString(e.getKey().trim(), e.getValue().toString());
        }
        editor.commit();
    }

    // ---------------------------------

    class Validator implements AutoCompleteTextView.Validator {

        @Override
        public boolean isValid(CharSequence text) {
            Log.i(TAG, "Checking if valid: " + text);

            return false;
        }

        @Override
        public CharSequence fixText(CharSequence invalidText) {
            Log.v("Test", "Returning fixed text");
            // docs say this must be in the list of valid words... hmmm
            return invalidText;
        }
    }

    class UpdateAutocompleteCallback implements AsyncGetCallback {
        private final static String TAG = "UpdateAutocompleteCallback";

        public UpdateAutocompleteCallback() {
        }

        public void callback(String response) {
            if (!response.startsWith("[{")) {
                Toast.makeText(getApplicationContext(),
                        "Network error, could not refresh suggestion boxes",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(),
                    "Successfully refreshed suggestion boxes",
                    Toast.LENGTH_SHORT).show();
            // prepare response for convert int to JSONObjects
            String[] map = response.split("\\},\\{");
            Log.i(TAG, map[0]);
            Log.i(TAG, map[1]);
            map[0] = map[0].substring(2); // cut out the leading [{
            map[map.length - 1] = map[map.length - 1].substring(0,
                    map[map.length - 1].length() - 2); // cut out last }]

            JSONObject tmp;
            mAutocompleteOptions.clear();
            mAutocompleteOptionsMap.clear();

            for (int i = 0; i < map.length; i++) {
                try {
                    tmp = new JSONObject("{" + map[i] + "}");
                    mAutocompleteOptions.add(tmp.getString("mapname").trim());
                    mAutocompleteOptionsMap.put(
                            tmp.getString("mapname").trim(), tmp);
                } catch (JSONException e) {
                    Log.e(TAG, "failed to convert to JSONObject: " + map[i]);
                    Log.e(TAG, e.toString());
                }
            }
            Collections.sort(mAutocompleteOptions, Collections.reverseOrder());
            mAutocompleteAdapter.notifyDataSetChanged();
        }
    }

    class NewSuggestionCallback implements AsyncGetCallback {
        public void callback(String response) {

        }
    }
}
