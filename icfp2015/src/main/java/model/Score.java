package model;

public class Score {

    private int value;
    private int previousLines;
	private int spellScore = 0;

    public static Score initial() {
        return new Score(0,0,0);
    }

    public Score(int value, int previousLines, int spellScore) {
        this.value = value;
        this.previousLines = previousLines;
        this.spellScore = spellScore;
    }

    public Score addMove(int unitSize, int linesCleared) {
        int points = unitSize + 100 * (1 + linesCleared) * linesCleared / 2;
        int line_bonus = previousLines > 1 ?
                (previousLines - 1) * points / 10 :
                0;
        return new Score(value + points + line_bonus, linesCleared, spellScore);
    }
    
    public Score cloneScore() {
    	return new Score(value, previousLines, spellScore);
    }

    public int getScore() {
        return value + spellScore;
    }

	public void setSpellScore(int spellScore) {
		this.spellScore = spellScore;
	}
	
	public int getSpellScore() {
		return spellScore;
	}
}
