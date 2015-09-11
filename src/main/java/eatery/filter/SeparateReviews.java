package eatery.filter;

import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by bruntha on 7/9/15.
 */
public class SeparateReviews {
    public static void main(String[] args) {
        SeparateReviews separateReviews = new SeparateReviews();
        separateReviews.getRandomReviews(10000);

    }


    final static String filePathRead = "/home/bruntha/Documents/FYP/Data/Samples/" +
            "review_100_C.json";
    final static String filePathWrite = "/home/bruntha/Documents/FYP/Data/Samples/" +
            "reviews_10000.txt";
    final static String filePathEnglishReviews = "/home/bruntha/Documents/FYP/Data/English/" +
            "englishReviews.json";
    ArrayList<Integer> randomIntegers=new ArrayList<>();
    int count=0;
    int written=0;



    public void separateReviews() {
        try {
            readLinesUsingFileReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getRandomReviews(int count){
        for (int i = 0; i < count; i++) {
            int rand=getRandInt(1,980528);
            if (randomIntegers.contains(rand)) {
                i--;
            }else {
                randomIntegers.add(rand);
            }
        }
        try {
            readJSONUsingFileReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getRandInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand=new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private void readLinesUsingFileReader() throws IOException {
        File file = new File(filePathRead);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            splitJson(line);    //splitting each json into ...
        }
        br.close();
        fr.close();
        System.out.println("Done...");
    }

    private void readJSONUsingFileReader() throws IOException {
        File file = new File(filePathEnglishReviews);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            count++;
            if (randomIntegers.contains(count)) {
                written++;
                splitJson(line);
            }
        }
        br.close();
        fr.close();
        System.out.println(written+" no of reviews written to file from "+count+" reviews");
    }

    private void writePrintStream(String line) {
        PrintStream fileStream = null;
        File file = new File(filePathWrite);

        try {
            fileStream = new PrintStream(new FileOutputStream(file, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileStream.println(line);
        fileStream.println();
        fileStream.close();


    }

    private void splitJson(String json) {
        org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();

        try {

            Object obj = parser.parse(json);

            JSONObject jsonObject = (JSONObject) obj;

            String review = (String) jsonObject.get("text");    // get review text from json
            String reviewWONewLine=review.replace("\n", "").replace("\r", "");
            System.out.println("R   "+reviewWONewLine);
            writePrintStream(reviewWONewLine); //write review as json if it is english

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }
}

