package domain;

/**
 * Created by bruntha on 12/25/15.
 */
public class Ann {
    int startIndex;
    int endIndex;
    String apsect;
    String word;

    public Ann() {
    }

    public Ann(int startIndex, int endIndex, String apsect, String word) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.apsect = apsect;
        this.word = word;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public String getApsect() {
        return apsect;
    }

    public void setApsect(String apsect) {
        this.apsect = apsect;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
