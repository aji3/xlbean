package org.xlbean.definition.parser;

import java.util.ArrayList;
import java.util.List;

import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.pattern.CharPredicates;
import org.jparsec.pattern.Patterns;

/**
 * Parse definition string defined in excel sheet.
 * 
 * @author tanikawa
 *
 */
public class DefinitionParser {

    /**
     * Parser for values of definition.
     * 
     * <p>
     * <ul>
     * <li>Alphanumeric</li>
     * <li>-.</li>
     * </ul>
     * </p>
     */
    static Parser<String> VALUE = Patterns
        .isChar(CharPredicates.IS_ALPHA_NUMERIC_)
        .or(Patterns.among("-."))
        .many()
        .toScanner("value")
        .source();

    /**
     * Parser for variable name.
     * 
     * <p>
     * It is the same as Java identifier.
     * 
     * e.g.
     * <ul>
     * <li>test</li>
     * <li>_sample1</li>
     * </ul>
     * </p>
     */
    static Parser<String> VARIABLE_NAME = Patterns
        .isChar(Character::isJavaIdentifierStart)
        .next(Patterns.isChar(Character::isJavaIdentifierPart).many())
        .toScanner("identifier")
        .source();

    /**
     * Parser for index for list.
     * 
     * <p>
     * e.g. [0]
     * </p>
     */
    static Parser<String> INDEX = Patterns
        .isChar('[')
        .next(Patterns.INTEGER)
        .next(Patterns.isChar(']'))
        .toScanner("index")
        .source();

    /**
     * Parser for variable.
     * 
     * e.g.
     * <p>
     * <ul>
     * <li>test</li>
     * <li>test[0]</li>
     * <li>test[0][1]</li>
     * </ul>
     * </p>
     */
    static Parser<String> VARIABLE = Parsers.sequence(
        VARIABLE_NAME,
        INDEX.many(),
        (name, indexes) -> name + String.join("", indexes));

    /**
     * Parser for variables connected by "."
     * 
     * e.g.
     * <ul>
     * <li>test</li>
     * <li>test.aaa</li>
     * <li>test[0].aaa[1]</li>
     * </ul>
     */
    static Parser<String> LAYERED_VARIABLE = VARIABLE.sepBy(Scanners.isChar('.')).map(vars -> String.join(".", vars));

    /**
     * Parser for IN_TABLE_OPTION.
     * 
     * e.g.
     * <ul>
     * <li>test?type</li>
     * <li>test.aaa?testOption</li>
     * <li>test[0].aaa[1]?testOption</li>
     * </ul>
     */
    static Parser<InTableOptionUnit> IN_TABLE_OPTION_DEFINITION = Parsers.sequence(
        LAYERED_VARIABLE,
        Scanners.isChar('?'),
        VARIABLE_NAME,
        (key, symbol, value) -> new InTableOptionUnit(key, value));

    /**
     * Parser for option.
     * 
     * e.g.
     * <ul>
     * <li>sampleOptionKey=sampleOptionValue123</li>
     * <li>_test=123</li>
     * </ul>
     */
    static Parser<DefinitionOption> OPTION = Parsers.sequence(
        VARIABLE_NAME,
        Scanners.isChar('='),
        VALUE,
        (key, eq, value) -> new DefinitionOption(key, value));

    /**
     * Parser for options connected by "&".
     * 
     * e.g.
     * <ul>
     * <li>key1=value123</li>
     * <li>key1=value123&key2=true</li>
     * </ul>
     */
    static Parser<List<DefinitionOption>> OPTIONS = OPTION.sepBy(Scanners.isChar('&'));

    /**
     * Parser for SingleDefinition class.
     * 
     * e.g.
     * <ul>
     * <li>test</li>
     * <li>test[0]</li>
     * <li>test[0][1]</li>
     * <li>test?key1=value1</li>
     * <li>test[0]?key1=value1&key2=value2</li>
     * </ul>
     */
    static Parser<DefinitionUnit> SINGLE_DEFINITION = Parsers.sequence(
        LAYERED_VARIABLE,
        Scanners
            .isChar('?')
            .next(OPTIONS)
            .asOptional(),
        (names, options) -> new DefinitionUnit(String.join(".", names), options.orElse(new ArrayList<>())));

    /**
     * Parser for start marker "~" of table.
     */
    static Parser<DefinitionUnit> TABLE_START_MARKER = Patterns
        .isChar('~')
        .toScanner("start_marker")
        .source()
        .map(mark -> new DefinitionUnit(mark, new ArrayList<>()));

    /**
     * Parser for TableDefinition.
     * 
     * e.g.
     * <ul>
     * <li>test#column</li>
     * <li>test[0]#column</li>
     * <li>test#column[0]</li>
     * <li>test#column[0]?columnOption=value</li>
     * <li>test?tableOption=value#column?columnOption=value</li>
     * </ul>
     * 
     */
    static Parser<DefinitionPair> TABLE_DEFINITION = Parsers.sequence(
        SINGLE_DEFINITION,
        Scanners.isChar('#'),
        TABLE_START_MARKER.or(SINGLE_DEFINITION),
        (left, sep, right) -> new DefinitionPair(left, right));

    /**
     * Entry point.
     */
    static Parser<?> DEFINITION = Parsers.longer(
        IN_TABLE_OPTION_DEFINITION,
        Parsers.or(TABLE_DEFINITION, SINGLE_DEFINITION));

    public static Object parse(String str) {
        return DEFINITION.parse(str);
    }
}
