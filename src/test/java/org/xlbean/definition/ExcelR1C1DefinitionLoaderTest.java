package org.xlbean.definition;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ExcelR1C1DefinitionLoaderTest extends ExcelR1C1DefinitionLoader {

    public ExcelR1C1DefinitionLoaderTest() {
        super(new Options());
    }

    @Test
    public void testValidationError() {
        ExcelR1C1DefinitionLoader loader = new ExcelR1C1DefinitionLoader(new Options());
        try {
            loader.load(null);
        } catch (IllegalArgumentException e) {
            assertThat(
                e.getMessage(),
                is("Definition source should be an instance of org.apache.poi.ss.usermodel.Workbook"));
        }
    }

    @Test
    public void testParseError() {
        ExcelR1C1DefinitionLoader loader = new ExcelR1C1DefinitionLoader(new Options());
        Object ret = loader.parse("aaa#bbb#");
        assertThat(ret, is(nullValue()));
    }

    @Test
    public void testDefinitionBuilderError() {
        ExcelR1C1DefinitionLoader loader = new ExcelR1C1DefinitionLoader(new Options());

        DefinitionBuilder builder = loader.getDefinitionBuilder("sample object");
        assertThat(builder, is(nullValue()));

    }
}
