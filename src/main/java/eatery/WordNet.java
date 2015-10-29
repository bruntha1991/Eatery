package eatery;

/**
 * Created by bruntha on 8/6/15.
 */

import domain.Syn;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

import java.util.ArrayList;
import java.util.List;

public class WordNet {

    private WordNetDatabase database;

    public static void main(String args[]) {
        WordNet wordNet=new WordNet();
        wordNet.getNounHypernym("love");
    }


    public WordNet() {
        System.setProperty("wordnet.database.dir", "/home/bruntha/Documents/Softwares/dict");
        database = WordNetDatabase.getFileInstance();
    }

    public List<NounSynset[]> getNounHypernym(String noun) {
        List<NounSynset[]> result = new ArrayList<NounSynset[]>();
        Synset[] synsets = database.getSynsets(noun, SynsetType.NOUN);
        for (Synset synset : synsets) {
            result.add(((NounSynset) synset).getHypernyms());
//            System.out.println(synset.+" "+synset.getDefinition());
        }
        return result;
    }

    public List<NounSynset[]> getNounHyponyms(String noun) {
        List<NounSynset[]> result = new ArrayList<NounSynset[]>();
        Synset[] synsets = database.getSynsets(noun, SynsetType.NOUN);
        for (Synset synset : synsets) {
            result.add(((NounSynset) synset).getHyponyms());
        }
        return result;
    }

    public List<NounSynset[]> getNounHyponyms(Synset[] synset) {
        List<NounSynset[]> result = new ArrayList<NounSynset[]>();
        for (Synset s : synset) {
            result.add(((NounSynset) s).getHyponyms());
        }
        return result;
    }

    public List<String> getNounSynonyms(String noun) {
        List<String> result = new ArrayList<String>();
        Synset[] synsets = database.getSynsets(noun, SynsetType.NOUN);
        for (Synset synset : synsets) {
            String[] synonyms = ((NounSynset) synset).getWordForms();
            for (String synonym : synonyms) {
                if (!result.contains(synonym)) {
                    result.add(synonym);
                }
            }
        }
        return result;
    }

    public List<Syn> getNounSynonymsWTagCount(String noun) {
        List<Syn> result = new ArrayList<Syn>();
        List<String> resultStringList = new ArrayList<String>();
        Synset[] synsets = database.getSynsets(noun, SynsetType.NOUN);
        for (Synset synset : synsets) {
            String[] synonyms = ((NounSynset) synset).getWordForms();
            for (String synonym : synonyms) {
                Syn syn=new Syn(synset.getTagCount(synonym),synonym);

                if (!resultStringList.contains(synonym)) {
                    resultStringList.add(synonym);
                    result.add(syn);
                }
            }
        }
        return result;
    }
}
