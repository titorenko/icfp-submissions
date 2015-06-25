package model;

import parser.ElementsConfigParser;
import parser.Input;
import parser.MapParser;
import parser.StreamInput;
import parser.StringInput;

public class MineFactory {

	public static model.Mine getMineFromResource(String mineResource) {
		StreamInput input = new StreamInput(MineFactory.class.getResourceAsStream(mineResource));
		return getMineFromInput(input);
	}
	
	public static model.Mine getMine(String mineSpec) {
		StringInput stringInput = new StringInput(mineSpec);
		return getMineFromInput(stringInput);
	}
	
	public static model.Mine getMine(String mineSpec, ElementsConfig config) {
		StringInput stringInput = new StringInput(mineSpec);
		return getMineFromInput(stringInput, config);
	}

	private static model.Mine getMineFromInput(Input input) {
		MapParser.MineInfo info = new MapParser(input).readMap();
		ElementsConfig config = new ElementsConfigParser(input).parseConfig();
		return new model.Mine(info.width, config, info.cells);
	}
	

	private static Mine getMineFromInput(Input input, ElementsConfig ec) {
		MapParser.MineInfo info = new MapParser(input).readMap();
		return new model.Mine(info.width, ec, info.cells);
	}

	public static Mine getMineFromResource(String mineResource, String moves) {
		Path path = Path.fromString(moves);
		return getMineFromResource(mineResource).makeMoves(path);
	}
}