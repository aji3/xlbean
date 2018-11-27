package org.xlbean.definition.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.jparsec.Parser;
import org.jparsec.error.ParserException;
import org.junit.Test;

public class DefinitionParserTest {

    @Test
    public void test() {
        String name = DefinitionParser.VARIABLE_NAME.parse("test");
        System.out.println(name);

        String index = DefinitionParser.INDEX.parse("[100]");
        System.out.println(index);

        String variable = DefinitionParser.VARIABLE.parse("test[1][2]");
        System.out.println(variable);

        DefinitionOption option = DefinitionParser.OPTION.parse("optionKey=optionValue");
        System.out.println(option);

        DefinitionUnit unit = DefinitionParser.SINGLE_DEFINITION.parse("defname?opt1=val1&opt2=val2");
        System.out.println(unit);

        DefinitionUnit mark = DefinitionParser.TABLE_START_MARKER.parse("~");
        System.out.println(mark);

        DefinitionPair pair = DefinitionParser.TABLE_DEFINITION.parse("table#~");
        System.out.println(pair);

        InTableOptionUnit intableoption = DefinitionParser.IN_TABLE_OPTION_DEFINITION.parse("test?aaa");
        System.out.println(intableoption);

        DefinitionParser.parse("test?aaa=bbb&bbb=ccc");
        DefinitionParser.parse("test?aaa=bbb&bbb=ccc#aaa");
        DefinitionParser.parse("test?aaa=bbb&bbb=ccc#aaa?aaa=");
        DefinitionParser.parse("test?aaa");
        DefinitionParser.parse("table#~");
        // System.out.println(obj);
    }

    @Test
    public void test_VARIABLE_NAME() {

        assertParser(DefinitionParser.VARIABLE_NAME, "test", "test");
        assertParser(DefinitionParser.VARIABLE_NAME, "test1", "test1");
        assertParser(DefinitionParser.VARIABLE_NAME, "_12test1", "_12test1");
        assertFailure(DefinitionParser.VARIABLE_NAME, "12test", 1, 1);
        assertFailure(DefinitionParser.VARIABLE_NAME, "test=", 1, 5);
        assertFailure(DefinitionParser.VARIABLE_NAME, "-test", 1, 1);
        assertFailure(DefinitionParser.VARIABLE_NAME, "te+st", 1, 3);

    }

    @Test
    public void test_VALUE() {

        assertParser(DefinitionParser.VALUE, "test", "test");
        assertParser(DefinitionParser.VALUE, "test12", "test12");
        assertParser(DefinitionParser.VALUE, "true", "true");
        assertParser(DefinitionParser.VALUE, "0", "0");
        assertParser(DefinitionParser.VALUE, "123", "123");
        assertParser(DefinitionParser.VALUE, "-123", "-123");
        assertFailure(DefinitionParser.VALUE, "-123 abc", 1, 5);
        assertFailure(DefinitionParser.VALUE, "-123=abc", 1, 5);
        assertFailure(DefinitionParser.VALUE, "-123,", 1, 5);
    }

    @Test
    public void test_INDEX() {

        assertParser(DefinitionParser.INDEX, "[0]", "[0]");
        assertParser(DefinitionParser.INDEX, "[100]", "[100]");
        assertFailure(DefinitionParser.INDEX, "[-100]", 1, 1);
        assertFailure(DefinitionParser.INDEX, "[a]", 1, 1);
    }

    @Test
    public void test_VARIABLE() {

        assertParser(DefinitionParser.VARIABLE, "test", "test");
        String variable = DefinitionParser.VARIABLE.parse("test[0]");
        System.out.println(variable);
        System.out.println(DefinitionParser.VARIABLE.parse("test[0]"));
        assertParser(DefinitionParser.VARIABLE, "test[0]", "test[0]");
        assertParser(DefinitionParser.VARIABLE, "test[0][2]", "test[0][2]");
        assertFailure(DefinitionParser.VARIABLE, "test[0][2]-", 1, 11);
        assertFailure(DefinitionParser.VARIABLE, "test [0][2]", 1, 5);
    }

    @Test
    public void test_LAYERED_VARIABLE() {

        assertParser(DefinitionParser.LAYERED_VARIABLE, "test.aaa", "test.aaa");
        assertParser(DefinitionParser.LAYERED_VARIABLE, "test[1].aaa", "test[1].aaa");
        assertParser(DefinitionParser.LAYERED_VARIABLE, "test[1].aaa[2]", "test[1].aaa[2]");
        assertParser(DefinitionParser.LAYERED_VARIABLE, "test.aaa[2][0]", "test.aaa[2][0]");
        assertFailure(DefinitionParser.LAYERED_VARIABLE, "test.", 1, 6);
        assertFailure(DefinitionParser.LAYERED_VARIABLE, "test..aaa", 1, 6);
    }

    @Test
    public void test_OPTION() {

        assertParser(DefinitionParser.OPTION, "key=val1", new DefinitionOption("key", "val1"));
        assertParser(DefinitionParser.OPTION, "_key1=123", new DefinitionOption("_key1", "123"));
        assertParser(DefinitionParser.OPTION, "_key1=", new DefinitionOption("_key1", ""));
        assertFailure(DefinitionParser.OPTION, "_key1", 1, 6);
        assertFailure(DefinitionParser.OPTION, "-aaa=bbb", 1, 1);
    }

    @Test
    public void test_OPTIONS() {

        assertParser(DefinitionParser.OPTIONS, "key=val1", Arrays.asList(new DefinitionOption("key", "val1")));
        assertParser(
            DefinitionParser.OPTIONS,
            "key=val1&_key123=123",
            Arrays.asList(new DefinitionOption("key", "val1"), new DefinitionOption("_key123", "123")));
        assertFailure(DefinitionParser.OPTIONS, "key=val1,key2=val2", 1, 9);
    }

    @Test
    public void test_TABLE_START_MARKER() {

        assertParser(
            DefinitionParser.TABLE_START_MARKER,
            "~",
            new DefinitionUnit("~", Arrays.asList()));
    }

    @Test
    public void test_SINGLE_DEFINITION() {

        assertParser(
            DefinitionParser.SINGLE_DEFINITION,
            "singleDef",
            new DefinitionUnit("singleDef", Arrays.asList()));
        assertParser(
            DefinitionParser.SINGLE_DEFINITION,
            "singleDef?opt1=0&opt2=true&testValue=",
            new DefinitionUnit(
                "singleDef",
                Arrays.asList(
                    new DefinitionOption("opt1", "0"),
                    new DefinitionOption("opt2", "true"),
                    new DefinitionOption("testValue", ""))));
    }

    @Test
    public void test_TABLE_DEFINITION() {

        assertParser(
            DefinitionParser.TABLE_DEFINITION,
            "table#column",
            new DefinitionPair(
                new DefinitionUnit("table", Arrays.asList()),
                new DefinitionUnit("column", Arrays.asList())));

        assertParser(
            DefinitionParser.TABLE_DEFINITION,
            "table#~",
            new DefinitionPair(
                new DefinitionUnit("table", Arrays.asList()),
                new DefinitionUnit("~", Arrays.asList())));
    }

    @Test
    public void test_IN_CELL_OPTION() {
        assertParser(
            DefinitionParser.IN_TABLE_OPTION_DEFINITION,
            "test?opt",
            new InTableOptionUnit("test", "opt"));

        assertParser(
            DefinitionParser.IN_TABLE_OPTION_DEFINITION,
            "test.aaa.bbb?opt",
            new InTableOptionUnit("test.aaa.bbb", "opt"));

        assertParser(
            DefinitionParser.IN_TABLE_OPTION_DEFINITION,
            "test.aaa[0].bbb?opt",
            new InTableOptionUnit("test.aaa[0].bbb", "opt"));

        assertFailure(DefinitionParser.IN_TABLE_OPTION_DEFINITION, "key=val1", 1, 4);

        assertFailure(DefinitionParser.IN_TABLE_OPTION_DEFINITION, "key?val1=aaa", 1, 9);

    }

    @Test
    public void test_DEFINITION() {
        assertParser(
            DefinitionParser.DEFINITION,
            "singleDef",
            new DefinitionUnit("singleDef", Arrays.asList()));
        assertParser(
            DefinitionParser.DEFINITION,
            "singleDef?opt1=0&opt2=true&testValue=",
            new DefinitionUnit(
                "singleDef",
                Arrays.asList(
                    new DefinitionOption("opt1", "0"),
                    new DefinitionOption("opt2", "true"),
                    new DefinitionOption("testValue", ""))));

        assertParser(
            DefinitionParser.DEFINITION,
            "table#column",
            new DefinitionPair(
                new DefinitionUnit("table", Arrays.asList()),
                new DefinitionUnit("column", Arrays.asList())));

        assertParser(
            DefinitionParser.DEFINITION,
            "table#~",
            new DefinitionPair(
                new DefinitionUnit("table", Arrays.asList()),
                new DefinitionUnit("~", Arrays.asList())));

        assertParser(
            DefinitionParser.DEFINITION,
            "test?opt",
            new InTableOptionUnit("test", "opt"));

        assertParser(
            DefinitionParser.DEFINITION,
            "test.aaa.bbb?opt",
            new InTableOptionUnit("test.aaa.bbb", "opt"));

        assertParser(
            DefinitionParser.DEFINITION,
            "test.aaa[0].bbb?opt",
            new InTableOptionUnit("test.aaa[0].bbb", "opt"));

        assertFailure(DefinitionParser.DEFINITION, "key=val1", 1, 4);

    }

    static void assertParser(Parser<?> parser, String source, Object value) {
        assertEquals(value, parser.parse(source));
    }

    static void assertParser(Parser<?> parser, String source, DefinitionOption value) {
        assertEq(value, (DefinitionOption) parser.parse(source));
    }

    static void assertParser(Parser<?> parser, String source, DefinitionUnit value) {
        assertEq(value, (DefinitionUnit) parser.parse(source));
    }

    static void assertParser(Parser<?> parser, String source, DefinitionPair value) {
        DefinitionPair pair = (DefinitionPair) parser.parse(source);
        assertEq(value, pair);
    }

    static void assertParser(Parser<?> parser, String source, InTableOptionUnit value) {
        InTableOptionUnit option = (InTableOptionUnit) parser.parse(source);
        assertEq(value, option);
    }

    static void assertEq(DefinitionOption expected, DefinitionOption actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
    }

    static void assertEq(DefinitionUnit expected, DefinitionUnit actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEq(expected.getOptions(), actual.getOptions());
    }

    static void assertEq(DefinitionPair expected, DefinitionPair actual) {
        assertEq(expected.getLeft(), actual.getLeft());
        assertEq(expected.getRight(), actual.getRight());
    }

    static void assertEq(InTableOptionUnit expected, InTableOptionUnit actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getOptionKey(), actual.getOptionKey());
    }

    static void assertEq(List<DefinitionOption> expected, List<DefinitionOption> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEq(expected.get(i), actual.get(i));
        }
    }

    static void assertParser(Parser<?> parser, String source, List<DefinitionOption> expected) {
        @SuppressWarnings("unchecked")
        List<DefinitionOption> actual = (List<DefinitionOption>) parser.parse(source);
        assertEq(expected, actual);
    }

    static void assertFailure(Parser<?> parser, String source, int line, int column) {
        assertFailure(parser, source, line, column, "");
    }

    static void assertFailure(
            Parser<?> parser, String source, int line, int column, String errorMessage) {
        try {
            parser.parse(source);
            fail("Expected failure but succeed");
        } catch (ParserException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(errorMessage));
            assertEquals(line, e.getLocation().line);
            assertEquals(column, e.getLocation().column);
        }
    }

}
