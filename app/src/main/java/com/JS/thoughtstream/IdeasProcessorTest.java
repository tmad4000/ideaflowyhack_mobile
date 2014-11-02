package com.JS.thoughtstream;
/**
 * Created by Kesiena on 01/11/2014.
 */
public class IdeasProcessorTest {
    /**

     public static void main(String[] args) {
     System.out.println(">>>Start!");
     new HandleProcessorTest().testGetIdeas();
     new HandleProcessorTest().testGetHandle();

     HandleProcessor p = new HandleProcessor(
     "@facebook 1st fb message\n\n@twitter 1st twitter message\n\nRandom message");
     System.out.println(">>>Initialized processor!");
     p.process("@facebook 2nd fb message\n\n@facebook 1st fb message\n\n" +
     "@twitter 1st twitter message\n\nRandom message");
     System.out.println(">>>Finished processing!");
     p.process("@facebook 2nd fb message\n\n@facebook 1st fb message\n\n" +
     "@facebook 3rd facebook message\n\n@twitter 1st twitter message\n\n" +
     "Random message\n\n@twitter 2nd twitter message.");
     System.out.println(">>>Finished processing!");
     System.out.println(">>>Successful!");
     }

     public void testGetIdeas() {
     HandleProcessor hp = new HandleProcessor("");
     String[] ideas = hp.getIdeas("Hello there.\n\nI am happy\n\n");
     if(ideas.length != 2) {
     throwError("Expected lenght of 2, found: " + ideas.length);
     }
     assertEqual("Hello there.", ideas[0]);
     assertEqual("I am happy", ideas[1]);
     }

     public void testGetHandle() {
     HandleProcessor hp = new HandleProcessor("");
     assertEqual("@facebook", hp.getHandle("@facebook This is my first facebook post."));
     }

     public void throwError(String message) {
     throw new RuntimeException(message);
     }

     private void assertEqual(String expected, String actual) {
     if((actual == null) || !actual.equals(expected)) {
     throwError("Expected: " + expected + ". Found: " + actual);
     }
     }
     */
}
