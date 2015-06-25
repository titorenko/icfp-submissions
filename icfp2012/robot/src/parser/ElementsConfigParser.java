package parser;

import java.util.ArrayList;
import java.util.Collection;

import model.ElementsConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementsConfigParser {

    private static final Logger logger = LoggerFactory.getLogger(ElementsConfigParser.class);

    private final Input input;

    public ElementsConfigParser(Input input) {
        this.input = input;
    }

    public ElementsConfig parseConfig() {
        ElementsConfig config = new ElementsConfig();

        String line;
        while ((line = input.readLine()) != null) {
            String[] property = readPropertyWithFiltering(line);
            if(property.length == 0) continue;
            if ("Water".equalsIgnoreCase(property[0])) {
                config.waterLevel = getIntProperty(property, 0) - 1;
            } else if ("Flooding".equalsIgnoreCase(property[0])) {
                config.floodingRate = getIntProperty(property, 0);
                if (config.floodingRate == 0)
                    config.floodingRate = Integer.MAX_VALUE;
            } else if ("Waterproof".equalsIgnoreCase(property[0])) {
                config.waterproof = getIntProperty(property, 10);
            } else if ("Trampoline".equalsIgnoreCase(property[0])) {
                ElementsConfig.TrampolineConfig trampoline = getTrampolineProperty(property);
                if (trampoline != null) {
                    config.trampolines.add(trampoline);
                }
            } else if ("Growth".equalsIgnoreCase(property[0])) {
                config.growthRate = getIntProperty(property, 25);
            } else if ("Razors".equalsIgnoreCase(property[0])) {
                config.initialRazors = getIntProperty(property, 0);
            } else {
                logger.error("Unknown keyword.");
            }
        }
        return config;
    }

    private String[] readPropertyWithFiltering(String line) {
        Collection<String> components = new ArrayList<String>(7);
        for(String component : line.split(" ")) {
            if (!component.isEmpty()) {
                components.add(component.trim());
            }
        }
        return components.toArray(new String[components.size()]);
    }

    // Trampoline A targets 1
    private ElementsConfig.TrampolineConfig getTrampolineProperty(String[] property) {
        if (property.length != 4) {
            logger.error("Failed to parse trampoline property string.");
            return null;
        }
        if (!"targets".equalsIgnoreCase(property[2])) {
            logger.error("Failed to parse trampoline property string.");
            return null;
        }
        try {
            return new ElementsConfig.TrampolineConfig(property[1].charAt(0), property[3].charAt(0));
        } catch (NumberFormatException nfe) {
            logger.error("Failed to parse trampoline value.", nfe);
            return null;
        }
    }

    private int getIntProperty(String[] property, int defaults) {
        if (property.length == 2) {
            try {
                return Integer.parseInt(property[1]);
            } catch (NumberFormatException nfe) {
                logger.error("Failed to parse parameter value.", nfe);
                return defaults;
            }
        } else {
            logger.error("Failed to parse property string.");
            return defaults;
        }
    }
}
