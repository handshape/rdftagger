package ca.gc.justice.rdftagger.triplestore;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

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
        URI subject = new URI("http://justice.gc.ca/");
        URI predicate = new URI("http://justice.gc.ca/ext/is");
        Object objekt = "Cool";
        RDFTriple triple = new RDFTriple(subject, predicate, objekt);
        ITripleStore[] instances = new ITripleStore[]{new SimpleTripleStore(), new TrieTripleStore(), new JenaTripleStore()};
        for (ITripleStore instance : instances) {
            System.out.println("Testing triple store: " + instance.getClass().getSimpleName());
            instance.put(triple);
            assertTrue("RDF that goes in also comes out.", instance.get(subject, predicate, objekt).contains(triple));
            assertTrue("Partial selectors match.", instance.get(subject, null, null).contains(triple));
            assertTrue("Partial selectors match.", instance.get(null, predicate, null).contains(triple));
            assertTrue("Partial selectors match.", instance.get(null, null, objekt).contains(triple));
            assertTrue("Empty selectors match.", instance.get(null, null, null).contains(triple));
            assertFalse("Wrong selectors fail.", instance.get(new URI("http://canada.ca"), null, null).contains(triple));
            assertFalse("Wrong selectors fail.", instance.get(null, new URI("http://canada.ca"), null).contains(triple));
            assertFalse("Wrong selectors fail.", instance.get(null, null, new URI("http://canada.ca")).contains(triple));
            instance.removeAll(subject, predicate, objekt);
            assertFalse("Ensure that what's removed is gone.", instance.get(subject, predicate, objekt).contains(triple));
        }
    }

}
