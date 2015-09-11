package eatery;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bruntha on 7/9/15.
 */
public class Annotation {

    final String filePathToAnnotate = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "review_100_D_Review.txt";  //the file path that need to be annotated
    final String filePathAlreadyAnnotated_Ann = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "review_100_D_Review.ann";  //annotation of the file that need to be annotated
    final String inputAnnFile = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "review.ann";
    final String filePathFoodNames = "/home/bruntha/Documents/FYP/Data/Foodlist/" +
            "Beverage_Sorted.txt";
    final String filePathDictionary = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "dictionary.txt";

    final String filePathDictionaryAuto = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "dictionaryAuto.txt";


    int test = 0;
    int tagCount = 0;
    int noOfTagsAlready = 0;
    int noOfTagsAlreadyMax = 0;
    ArrayList<String> listOfItems = new ArrayList<>();
    Hashtable<String, String> taggedItems = new Hashtable<>();
    Hashtable<String, String> dictionaryHashtable = new Hashtable<>(); //<key=word,value=tag>
    private ArrayList<String> words=new ArrayList();
    private ArrayList<String> tags=new ArrayList();
    private ArrayList<String> dictionary=new ArrayList();
    StanfordLemmatizer stanfordLemmatizer = new StanfordLemmatizer();

    public static void main(String[] args) {

        Annotation annotation = new Annotation();
        annotation.annotateUsingDictionary();
    }

    public void annotateUsingDictionary() {
        try {
            loadTags();
            tagCount = noOfTagsAlreadyMax;
            readFileForAnnotation(filePathDictionary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void annotateUsingAnnFile() {
        try {
            updateDictionary();
            loadDictionary(filePathDictionaryAuto);
            loadTags();
            tagCount = noOfTagsAlreadyMax;
            readFileForAnnotation(filePathDictionaryAuto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void annotateAsFile() {
        try {
            loadTags();
            tagCount = noOfTagsAlreadyMax;
            tag("F_Drinks",filePathFoodNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateDictionary() {
        try {
            loadDictionary(filePathDictionary);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < dictionary.size(); i++) {
            if (!dictionary.get(i).contains(" ")) {
                populateByStemmer(dictionaryHashtable.get(dictionary.get(i)), dictionary.get(i));
            }
        }
    }

    private void populateByStemmer(String tag, String word) {
        List<String> x = stanfordLemmatizer.lemmatize(word);

        for (int i = 0; i < x.size(); i++) {
            if (!dictionaryHashtable.containsKey(x.get(i).toLowerCase())) {
                dictionaryHashtable.put(x.get(i).toLowerCase(),tag);
                System.out.println("Stemmer: "+word+" "+x.get(i));
                String lineToAdd = tag + " " + x.get(i);
                writePrintStream(lineToAdd,filePathDictionary);
            }

        }


    }

//    private void loadDictionary() throws IOException {
//        File fileAnnotation = new File(filePathDictionary);
//        FileReader fr = new FileReader(fileAnnotation);
//        BufferedReader br = new BufferedReader(fr);
//        String line;
//
//        while ((line = br.readLine()) != null) {
//            int index=line.indexOf(" ");
//            String tag=line.substring(0,index);
//            String word=line.substring(index+1);
//
//            words.add(word);
//            tags.add(tag);
//        }
//
//        for (int i = 0; i < tags.size(); i++) {
//            populateByStemmer(tags.get(i), words.get(i));
//        }
//        br.close();
//        fr.close();
//        System.out.println("Done");
//    }

    private void readFileForAnnotation(String file) throws IOException {
        File fileAnnotation = new File(file);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            String[] annotations = line.split("[ \t]");
            String word = line.substring(line.indexOf(" ") + 1);
            annotate(annotations[0], word.toLowerCase());
        }
        br.close();
        fr.close();
        System.out.println("Done");
        print();
    }

    private void addToDictionary(String tag, String word) throws IOException {
        PrintStream fileStream = null;
        File file = new File(filePathDictionaryAuto);

        try {
            fileStream = new PrintStream(new FileOutputStream(file, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileStream.println(tag + " " + word);
        fileStream.close();
    }

    private void loadDictionary(String path) throws IOException {
        File fileAnnotation = new File(path);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            String[] dic = line.split("[ \t]");
            String word = line.substring(line.indexOf(" ") + 1);
            dictionaryHashtable.put(word.toLowerCase(), dic[0]);
            dictionary.add(word.toLowerCase());
            System.out.println("Loading dictionary : Word: " + word + "\tTag: " + dic[0]);
        }
        br.close();
        fr.close();
        System.out.println("Dictionary Loaded");
    }

    private void upgradeDictionary() throws IOException {
        File fileAnnotation = new File(inputAnnFile);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            String[] annotations = line.split("[ \t]");
            String word = line.substring(line.lastIndexOf("\t") + 1);
            if (dictionaryHashtable.containsKey(word.toLowerCase())) {
                if (!dictionaryHashtable.get(word.toLowerCase()).equals(annotations[1])) {
                    System.out.println("Conflict : word: " + word + " Dictionary Tag: " + dictionaryHashtable.get(word) + " New Tag: " + annotations[1]);
                }
            } else {
                dictionaryHashtable.put(word.toLowerCase(), annotations[1]);
                System.out.println("Updating Dictionary : Word: " + word + "\tTag: " + annotations[1]);
                addToDictionary(annotations[1], word.toLowerCase());
            }

        }
        br.close();
        fr.close();
        System.out.println("Done");
        print();
    }

    private void updateDictionary() {
        try {
            loadDictionary(filePathDictionary);
            upgradeDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private void tag(String tag,String path) throws IOException {
        File fileAnnotation = new File(path);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            annotate(tag, line);
        }
        br.close();
        fr.close();
        System.out.println("Done tagging");
    }

    public void print() {
        for (int i = 0; i < listOfItems.size(); i++) {
            System.out.println(listOfItems.get(i));
        }
    }

    private void loadTags() throws IOException {
        File fileAnnotation = new File(filePathAlreadyAnnotated_Ann);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            String[] annotations = line.split("[ \t]");
            String word = line.substring(line.lastIndexOf('\t') + 1);
            String[] words = word.split(" ");

            int start = Integer.parseInt(annotations[2]);
            if (words.length != 0) {
                for (int i = 0; i < words.length; i++) {
                    int end = start + words[i].length();
                    taggedItems.put(start + "-" + end, annotations[1]);
                    start = end + 1;
                }
            }
            taggedItems.put(annotations[2] + "-" + annotations[3], annotations[1]);

            noOfTagsAlready = Integer.parseInt(annotations[0].substring(1));
            if (noOfTagsAlreadyMax < noOfTagsAlready)
                noOfTagsAlreadyMax = noOfTagsAlready;
        }
        br.close();
        fr.close();
        System.out.println("Loaded tags");
    }

    private synchronized void annotate(String tag, String item) throws IOException {
        File fileAnnotation = new File(filePathToAnnotate);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;
        int indexTotal = 0;

        while ((line = br.readLine()) != null) {
            Pattern pattern = Pattern.compile("\\b(" + item + ")\\b");
            Matcher matcher = pattern.matcher(line.toLowerCase());
            while (matcher.find()) {

                int currentIndex = matcher.start();

                if (!alreadyAnnotated((currentIndex + indexTotal) + "-" + (currentIndex + indexTotal + item.length()), tag, line.substring(currentIndex, matcher.end()))) {
                    String newAnnotation = "T" + ++tagCount + "\t" + tag + " " + (currentIndex + indexTotal) + " " + (currentIndex + indexTotal + item.length()) + "\t" + line.substring(currentIndex, matcher.end());

                    String word = line.substring(currentIndex, matcher.end());
                    String[] words = word.split(" ");
                    int start = (currentIndex + indexTotal);

                    if (words.length != 0) {
                        for (int i = 0; i < words.length; i++) {
                            int end = start + words[i].length();
                            taggedItems.put(start + "-" + end, tag);
                            start = end + 1;
                        }
                    }

                    taggedItems.put((currentIndex + indexTotal) + "-" + (currentIndex + indexTotal + item.length()), tag);


                    System.out.println(newAnnotation);
                    writePrintStream(newAnnotation,filePathAlreadyAnnotated_Ann);
                }
            }
            indexTotal += line.length() + 1;
            test++;

        }
        br.close();
        fr.close();
    }

    public boolean alreadyAnnotated(String indeces, String tag, String word) {
        if (taggedItems.containsKey(indeces)) {
            if (!tag.equals(taggedItems.get(indeces)))
                System.out.println("Conflict : " + word + " Old tag: " + taggedItems.get(indeces) + " New tag: " + tag);
            return true;
        } else
            return false;
    }

    public synchronized void writePrintStream(String line,String path) {
        PrintStream fileStream = null;
        File file = new File(path);

        try {
            fileStream = new PrintStream(new FileOutputStream(file, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileStream.println(line);
        fileStream.close();
    }
}
