package parser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import model.ElementsConfig;

import org.junit.Test;

public class TestElementsConfigParser {

    @Test
    public void testWaterArguments() {
        String configString = "Water 1\n" +
                "Flooding 8 \n" +
                "Waterproof 5";
        ElementsConfig config = getConfig(configString);
        assertEquals("Flood level", 0, config.waterLevel);
        assertEquals("Flood rate", 8, config.floodingRate);
        assertEquals("Waterproof", 5, config.waterproof);
    }

    private ElementsConfig getConfig(String text) {
        Input input = new StringInput(text);
        ElementsConfigParser parser = new ElementsConfigParser(input);
        return parser.parseConfig();
    }

    @Test
    public void testWaterDefaults() {
        String configString = "";
        ElementsConfig config = getConfig(configString);
        assertEquals("Flood level", -1, config.waterLevel);
        assertEquals("Flood rate", Integer.MAX_VALUE, config.floodingRate);
    }

    @Test
    public void testTrampolines() {
        String configString = "Trampoline A targets 1\n" +
                "Trampoline B targets 1\n" +
                "Trampoline C targets 2";
        ElementsConfig config = getConfig(configString);
        assertArrayEquals(new Object[]{new ElementsConfig.TrampolineConfig('A','1'),new ElementsConfig.TrampolineConfig('B','1'),new ElementsConfig.TrampolineConfig('C','2')},
                config.trampolines.toArray());
    }

    @Test
    public void testTrampolineSpaces() {
        String configString = "Trampoline A targets 1\n" +
                "Trampoline    B targets 1\n" +
                "Trampoline C targets   2";
        ElementsConfig config = getConfig(configString);
        assertArrayEquals(new Object[]{new ElementsConfig.TrampolineConfig('A','1'),new ElementsConfig.TrampolineConfig('B','1'),new ElementsConfig.TrampolineConfig('C','2')},
                config.trampolines.toArray());
    }

    @Test
    public void testTrampolineErrors() {
        String configString = "Trampoline A targets 1\n" +
                "Trampoline B tcargets 1\n" +
                "Trampoline C targets   2";
        ElementsConfig config = getConfig(configString);
        assertArrayEquals(new Object[]{new ElementsConfig.TrampolineConfig('A','1'),new ElementsConfig.TrampolineConfig('C','2')},
                config.trampolines.toArray());
    }
}
