package eatery.synonyms;

import domain.Syn;
import eatery.SimilarityCalculation;
import eatery.WordNet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruntha on 8/6/15.
 */
public class Synonyms {
    final String filePathDictionary = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "dictionary.txt";

    ArrayList<String> words=new ArrayList<>();
    ArrayList<String> tags=new ArrayList<>();
    WordNet wordNet = new WordNet();


    public static void main(String args[]) {
        Synonyms synonyms=new Synonyms();
//        try {
//            synonyms.readFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        synonyms.getSynScore("employee");
//        synonyms.addSynonymsWordNet("acv", "bar");
//        synonyms.addSynonyms("acv", "bar");

    }

    private void readFile() throws IOException {
        File fileAnnotation = new File(filePathDictionary);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            int index=line.indexOf(" ");
            String tag=line.substring(0,index);
            String word=line.substring(index+1);

            words.add(word);
            tags.add(tag);
        }

        for (int i = 0; i < tags.size(); i++) {
            addSynonymsWordNet(tags.get(i),words.get(i));
        }
        br.close();
        fr.close();
        System.out.println("Done");
    }

    public void getSynScore(String word) {
        addSynonymsWordNet("",word);
    }

    private void addSynonymsWordNet(String tag, String word) {
        SimilarityCalculation similarityCalculation=new SimilarityCalculation();

        List<String> synonyms = wordNet.getNounSynonyms(word);

        System.out.println("######### "+word);
        for (int i = 0; i < synonyms.size(); i++) {
//            if(!words.contains(synonyms.get(i))){
                System.out.println(synonyms.get(i)+" "+similarityCalculation.compute(word,synonyms.get(i)));
//                System.out.println(synonyms.get(i));
//                tags.add(tag);
//                words.add(synonyms.get(i));
//                String lineToAdd=tag+" "+synonyms.get(i);
//                writePrintStream(lineToAdd,true);
//            }
        }



//        System.out.println("Done");
    }

    private void addSynonyms(String tag, String word) {
        SimilarityCalculation similarityCalculation=new SimilarityCalculation();

        List<Syn> x=wordNet.getNounSynonymsWTagCount(word);
        System.out.println("################### "+word+" ##########");
        for (int i = 0; i < x.size(); i++) {

            if (x.get(i).getTagCount() > 0) {

                System.out.println(x.get(i).getWord()+" "+similarityCalculation.compute(word,x.get(i).getWord())+" "+x.get(i).getTagCount());

//                if(!words.contains(x.get(i).getWord())){
//                System.out.println(x.get(i).getWord());
//                tags.add(tag);
//                words.add(x.get(i).getWord());
//                String lineToAdd=tag+" "+x.get(i).getWord();
//                writePrintStream(lineToAdd,true);
//            }
            }

        }
    }

    public void writePrintStream(String line, boolean isNextReview) {
        PrintStream fileStream = null;
        File file = new File(filePathDictionary);

        try {
            fileStream = new PrintStream(new FileOutputStream(file, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (isNextReview)
            fileStream.println(line);
        else
            fileStream.print(line);

        fileStream.close();
    }
}
