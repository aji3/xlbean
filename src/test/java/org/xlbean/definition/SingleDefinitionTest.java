package org.xlbean.definition;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.xlbean.excel.XlCellAddress;

public class SingleDefinitionTest {

    @Test
    public void testValidation() {
        SingleDefinition def = new SingleDefinition();
        def.setName("test");
        def.setCell(new XlCellAddress.Builder().row(1).column(2).build());
        def.setSheetName("testSheet");

        SingleDefinition def2 = new SingleDefinition();
        def2.setName("test2");
        def2.setCell(new XlCellAddress.Builder().row(9).column(9).build());
        def2.setSheetName("testSheet2");

        def.merge(null);
        // check that def object is not changed
        assertThat(def.getName(), is("test"));
        assertThat(def.getCell().getRow(), is(1));
        assertThat(def.getCell().getColumn(), is(2));
        assertThat(def.getSheetName(), is("testSheet"));

        def.merge(def2);
        // check that def object is not changed
        assertThat(def.getName(), is("test"));
        assertThat(def.getCell().getRow(), is(1));
        assertThat(def.getCell().getColumn(), is(2));
        assertThat(def.getSheetName(), is("testSheet"));

        assertThat(
            def.toString(),
            is("SingleDefinition [name=test, cell=XlCellAddress [row=1, column=2], options={}]"));

    }
}
