package org.xlbean.definition;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.xlbean.definition.parser.InTableOptionUnit;
import org.xlbean.excel.XlCellAddress;

public class InTableOptionDefinitionTest {

    private Definition createInTableOptionDefinition() {
        InTableOptionUnit unit = new InTableOptionUnit("testName", "testKey");
        Definition def = new InTableOptionDefinitionBuilder().build(
            unit,
            new XlCellAddress.Builder().row(1).column(5).build());
        return def;
    }

    @Test
    public void isBuildable() {
        assertThat(new InTableOptionDefinitionBuilder().isBuildable(null), is(false));
    }

    @Test
    public void test_validate_false() {
        Definition def = new InTableOptionDefinition();
        assertThat(def.validate(), is(false));
    }

    @Test
    public void test_validate_true() {
        Definition def = createInTableOptionDefinition();
        assertThat(def.validate(), is(true));
    }

    @Test
    public void test_merge_null() {

        Definition def = createInTableOptionDefinition();
        def.merge(null);

        assertThat(def.getName(), is("testName"));
    }

    @Test
    public void test_merge_otherdefinitionobject() {

        Definition def = createInTableOptionDefinition();

        PrintStream out = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        Definition other = new SingleDefinition();
        other.setName("other");

        def.merge(other);

        assertThat(
            new String(baos.toByteArray()),
            is(
                containsString(
                    "WARN  o.x.d.InTableOptionDefinition - Definition instance other cannot be merged to testName.")));

        System.setOut(out);
    }
}
