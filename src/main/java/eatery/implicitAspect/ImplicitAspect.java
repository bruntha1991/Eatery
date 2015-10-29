package eatery.implicitAspect;

import domain.Lang;
import eatery.filter.DetectLanguageCom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by bruntha on 9/15/15.
 */
public class ImplicitAspect {
    DetectLanguageCom detectLanguageCom=new DetectLanguageCom();
    final String filePathAdjectives = "/home/bruntha/Documents/FYP/Data/" +
            "adjectives.txt";

    public static void main(String args[]) {
//        LanguageDetect languageDetect=new LanguageDetect();
//        System.out.println(languageDetect.isWordEnglish("eat"));
        ImplicitAspect implicitAspect=new ImplicitAspect();
        try {
            implicitAspect.readFile("/home/bruntha/Documents/FYP/Data/adjectives.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(String filePath) throws IOException {
        File fileAnnotation = new File(filePath);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            Lang lang=detectLanguageCom.getLanguage(line.trim());
            System.out.println(line+": "+lang.getLanguage());
        }
        br.close();
        fr.close();
        System.out.println("File read finished");
    }

}
