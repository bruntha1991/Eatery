package eatery.annotation;

import eatery.StanfordLemmatizer;
import eatery.synonyms.PyDictionary;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bruntha on 9/10/15.
 */
public class Annotation {
    final String filePathDictionary = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "dictionary.txt";
    final String filePathReview = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "autoNew.txt";  //the file path that need to be annotated
    final String filePathAlreadyAnnotated_Ann = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "autoNew.ann";  //annotation of the file that need to be annotated
    Hashtable<String, String> dictionaryHashtable = new Hashtable<>(); //<key=word,value=aspect>
    int characterCount = 0;
    StanfordLemmatizer stanfordLemmatizer = new StanfordLemmatizer();
    int tagCount = 0;
    int test = 0;
    Hashtable<String, String> taggedItems = new Hashtable<>();
    private int noOfTagsAlready;
    private int noOfTagsAlreadyMax;


    public static void main(String args[]) {
        Annotation annotation = new Annotation();
        annotation.automate();
    }

    public void automate() {
        try {
            loadDictionary();
            loadOldTags();
            tagCount = noOfTagsAlreadyMax;
            readReviews();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done..");
    }

    public void loadDictionary() throws IOException {
        File fileAnnotation = new File(filePathDictionary);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            String[] dic = line.split("[ \t]");
            String word = line.substring(line.indexOf(" ") + 1);
            dictionaryHashtable.put(word.toLowerCase(), dic[0]);
            System.out.println("Loading dictionary : Word: " + word + "\tAspect: " + dic[0]);
        }
        br.close();
        fr.close();
        System.out.println("Dictionary Loaded");
    }

    public void readReviews() throws IOException {
        File fileAnnotation = new File(filePathReview);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            analysReviewStemming(line);
        }
        br.close();
        fr.close();
    }

    public void analysReview(String review) {
        String[] reviewWords = review.split(" ");

        for (int i = 0; i < reviewWords.length; i++) {
            if (dictionaryHashtable.containsKey(reviewWords[i])) {
                System.out.println(reviewWords[i] + " " + dictionaryHashtable.get(reviewWords[i]) + " " + (characterCount + characterCount) + " " + (characterCount + characterCount + reviewWords[i].length()));
            }
            characterCount += reviewWords[i].length();
            characterCount++;
        }
    }

    public void analysReviewStemming(String review) {
        String[] reviewWords = new String[0];
        try {
            reviewWords = tokenize(review);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int localCount = 0;
        for (int i = 0; i < reviewWords.length; i++) {
            List<String> words = stanfordLemmatizer.lemmatize(reviewWords[i]);

            for (int j = 0; j < words.size(); j++) {
                if (dictionaryHashtable.containsKey(words.get(j))) {
                    localCount = review.indexOf(reviewWords[i], localCount + 1);
                    if (!taggedItems.containsKey((characterCount + localCount) + "-" + (characterCount + localCount + reviewWords[i].length()))) {
                        String newAnnotation = "T" + ++tagCount + "\t" + dictionaryHashtable.get(words.get(j)) + " " + (localCount + characterCount) + " " + (localCount + characterCount + reviewWords[i].length()) + "\t" + reviewWords[i];

                        taggedItems.put((localCount + characterCount) + "-" + (localCount + characterCount + reviewWords[i].length()), dictionaryHashtable.get(words.get(j)));


                        System.out.println(newAnnotation);
//                        writePrintStream(newAnnotation,filePathAlreadyAnnotated_Ann);
//                        System.out.println(reviewWords[i] + " " + dictionaryHashtable.get(words.get(j)) + " " + (localCount + characterCount) + " " + (localCount + characterCount + reviewWords[i].length()));
                    }
                }

            }
        }
        characterCount += review.length();
        characterCount++;
    }

    public void analysReviewSynonyms(String review) {
        String[] reviewWords = new String[0];
        try {
            reviewWords = tokenize(review);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int localCount = 0;
        for (int i = 0; i < reviewWords.length; i++) {
            ArrayList<String> words = PyDictionary.findSynonyms(reviewWords[i]);

            for (int j = 0; j < words.size(); j++) {
//                System.out.println(reviewWords[i]+" "+words.get(j));
                if (dictionaryHashtable.containsKey(words.get(j))) {
                    localCount = review.indexOf(reviewWords[i], localCount + 1);
                    if (!taggedItems.containsKey((characterCount + localCount) + "-" + (characterCount + localCount + reviewWords[i].length()))) {
                        String newAnnotation = "T" + ++tagCount + "\t" + dictionaryHashtable.get(words.get(j)) + " " + (localCount + characterCount) + " " + (localCount + characterCount + reviewWords[i].length()) + "\t" + reviewWords[i];
                        taggedItems.put((localCount + characterCount) + "-" + (localCount + characterCount + reviewWords[i].length()), dictionaryHashtable.get(words.get(j)));
                        System.out.println(newAnnotation);
                        //                        writePrintStream(newAnnotation,filePathAlreadyAnnotated_Ann);
                    }
                }
            }
        }
        characterCount += review.length();
        characterCount++;
    }


    public String[] tokenize(String review) throws IOException {
        InputStream is = new FileInputStream("en-token.bin");

        TokenizerModel model = new TokenizerModel(is);

        TokenizerME tokenizer = new TokenizerME(model);

        String tokens[] = tokenizer.tokenize(review);

        is.close();
        return tokens;
    }


    private void loadOldTags() throws IOException {
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
                    System.out.println("Loading old tags: " + start + "-" + end + "\t" + annotations[1]);
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

    public void annotateNormal() throws IOException {
        File fileAnnotation = new File(filePathDictionary);
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
    }

    public void annotate(String aspect, String item) throws IOException {
        File fileAnnotation = new File(filePathReview);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;
        int indexTotal = 0;

        while ((line = br.readLine()) != null) {
            Pattern pattern = Pattern.compile("\\b(" + item + ")\\b");
            Matcher matcher = pattern.matcher(line.toLowerCase());
            while (matcher.find()) {

                int currentIndex = matcher.start();

                if (!alreadyAnnotated((currentIndex + indexTotal) + "-" + (currentIndex + indexTotal + item.length()), aspect, line.substring(currentIndex, matcher.end()))) {
                    String newAnnotation = "T" + ++tagCount + "\t" + aspect + " " + (currentIndex + indexTotal) + " " + (currentIndex + indexTotal + item.length()) + "\t" + line.substring(currentIndex, matcher.end());

                    String word = line.substring(currentIndex, matcher.end());
                    String[] words = word.split(" ");
                    int start = (currentIndex + indexTotal);

                    if (words.length != 0) {
                        for (int i = 0; i < words.length; i++) {
                            int end = start + words[i].length();
                            taggedItems.put(start + "-" + end, aspect);
                            start = end + 1;
                        }
                    }

                    taggedItems.put((currentIndex + indexTotal) + "-" + (currentIndex + indexTotal + item.length()), aspect);


                    System.out.println(newAnnotation);
                    writePrintStream(newAnnotation, filePathAlreadyAnnotated_Ann);
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

    public void writePrintStream(String line, String path) {
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
