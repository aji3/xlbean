package org.xlbean.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.xlbean.XlBean;
import org.xlbean.definition.Definition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.reader.XlBeanReaderContext;

public class XlBeanExampleForGithub5 {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/presidents.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBeanReaderContext context = reader.readContext(in);

        Map<String, Definition> definitionMap = context.getDefinitions().toMap();
        System.out.println(definitionMap.get("name"));
        System.out.println(((TableDefinition) definitionMap.get("presidents")).getAttributes().get("name"));
        System.out.println(((TableDefinition) definitionMap.get("presidents")).getAttributes().get("dateOfBirth"));
        System.out.println(((TableDefinition) definitionMap.get("presidents")).getAttributes().get("stateOfBirth"));

        System.out.println(context.getDefinitions());

        XlBean xlbean = context.getXlBean();

    }
}
