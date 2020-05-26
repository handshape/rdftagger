package ca.gc.justice.rdftagger.ui;

import javafx.scene.paint.Color;

/**
 *
 * @author jturner
 */
public class StringColourizer {

    private static String[] colours = new String[]{
        "#D46A6A",
        "#D49A6A",
        "#669999",
        "#88CC88",
        "#D4D46A",
        "#003399",
        "#7A85AD",
        "#003399",
        "#D4B46A",
        "#9775AA"

    };

    public static String hexColorForString(String string) {
        return colours[Math.abs(Fnv1a.hash32(string) % colours.length)];
    }
}
