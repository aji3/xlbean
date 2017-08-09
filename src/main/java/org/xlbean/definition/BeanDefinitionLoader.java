package org.xlbean.definition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.xlbean.XlBean;
import org.xlbean.XlList;
import org.xlbean.converter.BeanConverter;
import org.xlbean.excel.XlCellAddress;

/**
 * Generates {@link DefinitionRepository} from a bean given as definitionSource.
 * 
 * <p>
 * Basically this class is needed only when you want to write out a bean into
 * blank excel sheet.
 * </p>
 * 
 * @author Kazuya Tanikawa
 *
 */
public class BeanDefinitionLoader extends DefinitionLoader<Object>{

    private BeanConverter converter = new BeanConverter();

    @Override
    public void initialize(Object definitionSource) {
        setDefinitionSource(definitionSource);
    }
    
    @Override
    public DefinitionRepository load() {
        Object obj = getDefinitionSource();
        Object xlBeanOrXlList = null;
        if (obj instanceof XlBean || obj instanceof XlList) {
            xlBeanOrXlList = obj;
        } else {
            xlBeanOrXlList = converter.toMap(obj);
        }
        DefinitionRepository definitions = loadInternal(xlBeanOrXlList, new BeanDefinitionLoaderContext());
        definitions.sort(Comparator.comparing(Definition::getName));
        
        setCellInfo(definitions);
        
        return definitions;
    }
    
    private void setCellInfo(DefinitionRepository definitions) {
        CellInfoGenerator generator = new CellInfoGenerator();
        definitions.forEach(it -> {
            if (it instanceof TableDefinition) {
                TableDefinition table = (TableDefinition)it;
                for (SingleDefinition attr : 
                    table.getAttributes().values().stream().sorted(Comparator.comparing(SingleDefinition::getName)).collect(Collectors.toList())) {
                    attr.setCell(generator.generateCellForTableColumn());
                }
                SingleDefinition start = new SingleDefinition();
                start.setName("~");
                start.setOriginalKeyString("~");
                start.setCell(new XlCellAddress.Builder().row(1).build());
                table.addAttribute(start);
            } else {
                // it instanceof SingleDefinition
                SingleDefinition single = (SingleDefinition)it;
                single.setCell(generator.generateCellForSingle());
            }
            it.setSheetName("data");
        });
    }
    
    private class CellInfoGenerator {
        private int columnForTables = 2;
        private int rowForSingles = 1;
        public XlCellAddress generateCellForTableColumn() {
            return new XlCellAddress.Builder().row(1).column(columnForTables++).build();
        }
        public XlCellAddress generateCellForSingle() {
            return new XlCellAddress.Builder().row(rowForSingles++).column(1).build();
        }
    }
    
    private DefinitionRepository loadInternal(Object obj, BeanDefinitionLoaderContext context) {
        DefinitionRepository definitions = new DefinitionRepository();
        if (obj instanceof XlBean) {
            ((Map<?, ?>)obj).forEach((key, value) -> {
                if (key == null) {
                    return;
                }
                context.push(key.toString());
                definitions.merge(loadInternal(value, context));
            });
        } else if (obj instanceof XlList) {
            loadTableDefinition(obj, context, definitions);
        } else {
            // This path is for the "obj" which is not XlBean nor XlList.
            // It should be String.
            loadSingleDefinition(context, definitions);
        }
        context.pop();
        return definitions;
    }

    private void loadSingleDefinition(BeanDefinitionLoaderContext context, DefinitionRepository definitions) {
        if (context.getDepth() == 0) {
            context.push("value");
        }
        SingleDefinition single = new SingleDefinition();
        single.setName(context.getCurrentName());
        single.setOriginalKeyString(context.getCurrentName());
        definitions.addDefinition(single);
    }

    private void loadTableDefinition(Object obj, BeanDefinitionLoaderContext context, DefinitionRepository definitions) {
        if (context.getDepth() == 0) {
            context.push("values");
        }
        TableDefinition table = new TableDefinition();
        table.setName(context.getCurrentName());
        table.setOriginalKeyString(context.getCurrentName());
        definitions.addDefinition(table);
        Map<String, Definition> attributesMap = new HashMap<>();
        for (XlBean bean : (XlList)obj) {
            DefinitionRepository attributes = loadInternal(bean, new BeanDefinitionLoaderContext());
            attributesMap.putAll(attributes.toMap());
        }
        attributesMap.values().forEach(it -> table.addAttribute((SingleDefinition)it));
    }
    
    /**
     * Context class used to create name of definitions in {@link #loadInternal}.
     * 
     * @author Kazuya Tanikawa
     *
     */
    private class BeanDefinitionLoaderContext {
        private List<String> keys = new ArrayList<>();
        public int getDepth() {
            return keys.size();
        }
        public void push(String key) {
            keys.add(key);
        }
        public String pop() {
            if (keys.size() == 0) {
                return null;
            }
            return keys.remove(keys.size() - 1);
        }
        public String getCurrentName() {
            StringBuilder sb = new StringBuilder();
            keys.forEach(it -> {
                if (sb.length() > 0) { 
                    sb.append(".");
                }
                sb.append(it);
            });
            return sb.toString();
        }
    }
}
