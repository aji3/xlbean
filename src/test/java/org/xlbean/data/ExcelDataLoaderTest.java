package org.xlbean.data;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.xlbean.definition.InTableOptionDefinition;

public class ExcelDataLoaderTest {

    @Test
    public void getValueLoader() {
        ExcelDataLoader loader = new ExcelDataLoader();

        InTableOptionDefinition def = new InTableOptionDefinition();
        try {
            loader.getValueLoader(def);
        } catch (IllegalArgumentException e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(
                e.getMessage(),
                is("Unexpected Definition class. org.xlbean.definition.InTableOptionDefinition"));
        }
    }
}
