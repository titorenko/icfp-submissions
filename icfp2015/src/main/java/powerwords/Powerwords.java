package powerwords;

import java.util.ArrayList;
import java.util.Iterator;

import fj.data.List;

public class Powerwords implements Iterable<Powerword>{
    public static final Powerwords INSTANCE = new Powerwords();
    
    private final java.util.List<Powerword> words; 
    
    
    public static List<String> load() { 
        return List.list(
        		"ia! ia!", 
        		"yuggoth", 
        		"blue hades", 
        		"ei!", 
        		"r'lyeh", 
        		"planet 10", 
                "tsathoggua", 
                "yogsothoth", 
                "john bigboote",  
                "cthulhu fhtagn!",
                "Ph'nglui mglw'nafh Cthulhu R'lyeh wgah'nagl fhtagn.",
                "vigintillion",
                "necronomicon",
                "monkeyboy",
                "yoyodyne",
                "the laundry",
        		"case nightmare green",
        		"in his house at r'lyeh dead cthulhu waits dreaming."
       );
    }
    
    private Powerwords() {
    	this.words = new ArrayList<Powerword>();
    	int idx = 0;
    	for (String r : load()) {
    		words.add(new Powerword(r, idx));
			idx++;
		}
    }

    @Override
    public Iterator<Powerword> iterator() {
        return words.iterator();
    }

    public static int scoreOf(int[] powerwordsSoFar) {
    	int sum = 0;
    	for (int i = 0; i < powerwordsSoFar.length; i++) {
    		int count = powerwordsSoFar[i];
    		if (count == 0) continue;
			sum += 2 * count * INSTANCE.ord(i).size() + 300;
		}
    	return sum;
    }

	public static int deltaScoreOf(int prevSpellScore, int prevCount, Powerword newPowerword) {
		if (prevCount == 0) {
			return prevSpellScore + 300 + 2 * newPowerword.size();
		} else {
			return prevSpellScore + 2 * newPowerword.size();
		}
	}

	public int size() {
		return words.size();
	}

	public Powerword ord(int idx) {
		return words.get(idx);
	}
}