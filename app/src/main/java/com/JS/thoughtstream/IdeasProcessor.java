package com.JS.thoughtstream;

import android.util.Log;

import org.shaded.apache.commons.codec.binary.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kesiena on 01/11/2014.
 */
public class IdeasProcessor {
    private Set<String> ideas;

    public IdeasProcessor(String text) {
        ideas = new HashSet<String>();
        String[] ideasArray = getIdeas(text);
        for(String s : ideasArray) {
            ideas.add(s);
        }
    }

    public void process(String text) {
        String[] newIdeas = getIdeas(text);
        for(String s : newIdeas) {
            if(!ideas.contains(s)) {
                processIdea(s);
                ideas.add(s);
            }
        }
    }

    public void processIdea(String s) {
        String handle = getHandle(s);
        if((handle != null) && !handle.equals("")) {
            Log.d("TETESTESTSTSTETESTESTESTETESTESTESTESTESTESTESTE", "SJFOIEGNIUOSBEIONSIGNSEIOBUOGISGOIENOFNESGNIE");
            sendIdeaToAPI(handle, s.substring(handle.length()).trim());
        }
    }

    private String[] getIdeas(String text) {
        return text.split("\\n\\n");
    }

    private String getHandle(String idea) {
        if(idea.contains("@")) {
            int start = idea.indexOf("@");
            int end = idea.indexOf(" ", start);
            if (end == -1) {
                end = idea.length();
            }
            return idea.substring(start, end);
        } else{
            return "";
        }
    }

    //TODO: Implement this method after deciding on the API we intend to use
    // With a text like "@facebook Hello World",
    // the handle will be "@facebook" while the idea will be "Hello World"
    private void sendIdeaToAPI(String handle, String idea) {
        System.out.println("Sending [" + idea + "] to [" + handle + "]");

    }
}
