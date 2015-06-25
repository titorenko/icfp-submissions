package model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Metadata about environment and robot
 */
public class ElementsConfig {
    public int waterLevel = -1;
    public int floodingRate = Integer.MAX_VALUE;
    public int waterproof = 10;
    public int growthRate = 25;
    public int initialRazors = 0;

    public Collection<TrampolineConfig> trampolines = new ArrayList<TrampolineConfig>();

    public ElementsConfig() {
    }

    public ElementsConfig(int waterLevel, int floodingRate, int waterproof) {
        this.waterLevel = waterLevel;
        this.floodingRate = floodingRate;
        this.waterproof = waterproof;
    }
    
    public ElementsConfig(int growthRate, int initialRazors) {
    	this.growthRate = growthRate;
    	this.initialRazors = initialRazors;
    }

    public static class TrampolineConfig {
        public final char from;
        public final char to;

        public TrampolineConfig(char from, char to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TrampolineConfig that = (TrampolineConfig) o;
            return to == that.to && from == that.from;
        }

        @Override
        public int hashCode() {
            int result = (int) from;
            result = 31 * result + (int) to;
            return result;
        }
    }
}
