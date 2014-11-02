package com.JS.thoughtstream;

import java.util.HashMap;

/**
 * Created by Kelley on 11/2/2014.
 */
public class Idea {
    private int begin;
    private int end;
    private String handle;
    private boolean submit = true;
    public static HashMap<String, Idea> aIdeas = new HashMap<String, Idea>();

    public Idea(String pHandle, int aBegin, int aEnd){
        begin = aBegin;
        end = aEnd;
        handle = pHandle;
    }

    public Idea(String pHandle){
        handle = pHandle;
    }

    public static Idea getIdea(String pHandle){
        if(aIdeas.containsKey(pHandle)){
            return aIdeas.get(pHandle);
        } else{
            Idea newIdea = new Idea(pHandle);
            aIdeas.put(pHandle, newIdea);
            return newIdea;
        }
    }

    public static boolean containsIdea(String pHandle){
        if(aIdeas.containsKey(pHandle)){
            return true;
        }
        return false;
    }

    public int getBegin(){
        return begin;
    }

    public int getEnd(){
        return end;
    }

    public boolean getSubmit(){
        return submit;
    }

    public void setBegin(int pBegin){
        begin = pBegin;
    }

    public void setEnd(int pEnd){
        end = pEnd;
    }

    public void setHandle(String pHandle){
        handle = pHandle;
    }

    public void setSubmit(boolean pSubmit){
        submit = pSubmit;
    }

    public String getHandle(){
        return handle;
    }
}
