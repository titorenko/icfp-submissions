package model;

import com.google.common.collect.ImmutableList;

public class StaticBoardInfo {

    final ImmutableList<UnitTemplate> sources;
   	final int width;
   	final int height;

    public StaticBoardInfo(int width, int height, ImmutableList<UnitTemplate> sources) {
        this.height = height;
        this.width = width;
        this.sources = sources;
    }
}
