package ca.gc.csps.rdftagger.ui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author jturner
 */

public class Aliases {
private static PatriciaTrie<String> aliasStore = null;

    public static String getAlias(URI uri) {
        if (aliasStore == null) {
            init();
        }
        return aliasStore.getOrDefault(uri.toASCIIString(), uri.toASCIIString());
    }

    public static String getAlias(String subject) {
        try {
            return getAlias(new URI(subject));
        } catch (URISyntaxException ex) {
            return subject;
        }
    }

    public static void putAlias(URI uri, String alias) {
        if (aliasStore == null) {
            init();
        }
        aliasStore.put(uri.toASCIIString(), alias);
    }

    private static synchronized void init() {
        if (aliasStore == null) {
            aliasStore = new PatriciaTrie<>();
            try {
                Path path = Paths.get("./aliases.csv");
                if (Files.isRegularFile(path) && Files.isReadable(path)) {
                    Logger.getLogger(Aliases.class.getName()).log(Level.INFO, "Loading aliases from: " + path.toAbsolutePath());
                    CSVParser parse = CSVParser.parse(path, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
                    for (CSVRecord record : parse) {
                        try {
                            URI source = new URI(record.get(0));
                            String alias = record.get(1);
                            aliasStore.put(source.toASCIIString(), alias);
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(Aliases.class.getName()).log(Level.SEVERE, "Failed to parse URI: " + record.get(0), ex);
                        }

                    }
                } else {
                    Logger.getLogger(Aliases.class.getName()).log(Level.INFO, "No aliases found at: " + path.toAbsolutePath());
                }
            } catch (IOException ex) {
                Logger.getLogger(Aliases.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
