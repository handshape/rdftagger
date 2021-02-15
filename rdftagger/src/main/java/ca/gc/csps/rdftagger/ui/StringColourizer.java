package ca.gc.csps.rdftagger.ui;

import javafx.scene.paint.Color;

/**
 *
 * @author jturner
 */
public class StringColourizer {

    private static String[] colours = new String[]{
        "#A3D6D4",
        "#F1E9CB",
        "#C2D5A7",
        "#B0ABCA",
        "#E2A9BE",
        "#E1C6AC",
        "#E0FEFE",
        "#C7CEEA",
        "#FFDAC1",
        "#FF9AA2", 
        "#FFFFD8", 
        "#B5EAD7", 
        "#FFFFFF", 
        "#DDDDDD"

    };

    public static String hexColorForString(String string) {
        return colours[Math.abs(Fnv1a.hash32(string) % colours.length)];
    }
}
