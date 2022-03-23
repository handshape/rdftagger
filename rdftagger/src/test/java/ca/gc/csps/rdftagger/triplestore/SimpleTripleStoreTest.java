package ca.gc.csps.rdftagger.triplestore;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jturner
 */
public class SimpleTripleStoreTest {

    public SimpleTripleStoreTest() {
    }

    /**
     * Test of removeAll method, of class SimpleTripleStore.
     */
    @Test
    public void testIntegration() throws URISyntaxException, RDFTripleException {
        System.out.println("Integration test - triple stores");
        URI subject = new URI("http://csps.gc.ca/");
        URI predicate = new URI("http://csps.gc.ca/ext/is");
        Object objekt = "Cool";
        RDFTriple triple = new RDFTriple(subject, predicate, objekt);
        ITripleStore[] instances = new ITripleStore[]{new SimpleTripleStore(), new TrieTripleStore(), new JenaTripleStore()};
        for (ITripleStore instance : instances) {
            System.out.println("Testing triple store: " + instance.getClass().getSimpleName());
            instance.put(triple);
            assertTrue(instance.get(subject, predicate, objekt).contains(triple), "RDF that goes in also comes out.");
            assertTrue(instance.get(subject, null, null).contains(triple), "Partial selectors match.");
            assertTrue(instance.get(null, predicate, null).contains(triple), "Partial selectors match.");
            assertTrue(instance.get(null, null, objekt).contains(triple), "Partial selectors match.");
            assertTrue(instance.get(null, null, null).contains(triple), "Empty selectors match.");
            assertFalse(instance.get(new URI("http://canada.ca"), null, null).contains(triple), "Wrong selectors fail.");
            assertFalse(instance.get(null, new URI("http://canada.ca"), null).contains(triple), "Wrong selectors fail.");
            assertFalse(instance.get(null, null, new URI("http://canada.ca")).contains(triple), "Wrong selectors fail.");
            instance.removeAll(subject, predicate, objekt);
            assertFalse(instance.get(subject, predicate, objekt).contains(triple), "Ensure that what's removed is gone.");
        }
    }

}
