package org.xlbean.data.value;

import org.xlbean.definition.Options;
import org.xlbean.util.Accessors;
import org.xlbean.util.Accessors.AccessorConfig;
import org.xlbean.util.Accessors.AccessorConfig.AccessorConfigBuilder;

/**
 * Logic for the following options:ignoreNull, ignoreBlankMap, ignoreBlankList,
 * ignoreBlank, ignoreNullBlank.
 * 
 * <p>
 * "ignoreNull": When true, key for null value will not be set to XlBean,
 * otherwise, the key will be set to XlBean with null value. For instance, if a
 * name of a field is "aaa" and value for this field is null, then the result
 * will be as follows:
 * <ul>
 * <li>ignoreNull == true: {}</li>
 * <li>ignoreNull == false: {"aaa": null}</li>
 * </ul>
 * </p>
 * 
 * <p>
 * "ignoreBlankMap": When true, a XlBean (or Map) instance without any key will
 * be treated as null. For instance, if a name of a field is "test.test" and
 * value for this field is null, then the result will be as follows:
 * <ul>
 * <li>ignoreBlankMap == true && ignoreNull == true: {}</li>
 * <li>ignoreBlankMap == false && ignoreNull == true: {"test":{}}</li>
 * </ul>
 * Using ignoreNull == true case because if ignoreNull == false, then the map
 * will become {"test":{"test": null}} which is not empty anyway.
 * </p>
 * 
 * <p>
 * "ignoreBlankList": When true, a List instance whose size is 0 will be treated
 * as null. For instance, if a name of field is "testList" and value for this
 * field is null, then the result will be as follows:
 * <ul>
 * <li>ignoreBlankList == true && ignoreNull == true: {}</li>
 * <li>ignoreBlankList == false && ignoreNull == true: {"testList":[]}</li>
 * </ul>
 * Using ignoreNull == true case because if ignoreNull == false, then the field
 * will become {"testList":[null]} which is not empty anyway.
 * </p>
 * 
 * @author tanikawa
 *
 */
public class IgnoreOptionProcessor {

    /**
     * Option to configure ignoreNull, ignoreBlankMap and ignoreBlankList of
     * {@link Accessors}. In addition to ignoreNull, ignoreBlankMap and
     * ignoreBlankList, the following keys are also valid.
     * 
     * <ul>
     * <li>ignoreNullBlank: ignoreNull, ignoreBlankMap, ignoreBlankList</li>
     * <li>ignoreBlank: ignoreBlankMap, ignoreBlankList</li>
     * </ul>
     * 
     * @param definition
     * @return
     */
    private IgnoreOptionAccessorConfig createConfig(Options options) {
        IgnoreOptionAccessorConfig.Builder builder = new IgnoreOptionAccessorConfig.Builder();
        String ignoreNullBlank = options.getOption("ignoreNullBlank");
        if (ignoreNullBlank != null) {
            if (Boolean.parseBoolean(ignoreNullBlank)) {
                builder.ignoreNull(true).ignoreBlankMap(true).ignoreBlankList(true);
            } else {
                builder.ignoreNull(false).ignoreBlankMap(false).ignoreBlankList(false);
            }
        }
        String ignoreBlank = options.getOption("ignoreBlank");
        if (ignoreBlank != null) {
            if (Boolean.parseBoolean(ignoreBlank)) {
                builder.ignoreBlankMap(true).ignoreBlankList(true);
            } else {
                builder.ignoreBlankMap(false).ignoreBlankList(false);
            }
        }
        String ignoreNull = options.getOption("ignoreNull");
        if (ignoreNull != null) {
            builder.ignoreNull(Boolean.parseBoolean(ignoreNull));
        }
        String ignoreBlankMap = options.getOption("ignoreBlankMap");
        if (ignoreBlankMap != null) {
            builder.ignoreBlankMap(Boolean.parseBoolean(ignoreBlankMap));
        }
        String ignoreBlankList = options.getOption("ignoreBlankList");
        if (ignoreBlankList != null) {
            builder.ignoreBlankList(Boolean.parseBoolean(ignoreBlankList));
        }
        return builder.build();
    }

    private AccessorConfig overrideAccessorConfig(AccessorConfig configToBeOverridden,
            IgnoreOptionAccessorConfig config) {
        AccessorConfigBuilder builder = new AccessorConfigBuilder();
        if (config.hasIgnoreNull()) {
            builder.ignoreNull(config.isIgnoreNull());
        } else {
            builder.ignoreNull(configToBeOverridden.isIgnoreNull());
        }
        if (config.hasIgnoreBlankMap()) {
            builder.ignoreBlankMap(config.isIgnoreBlankMap());
        } else {
            builder.ignoreBlankMap(configToBeOverridden.isIgnoreBlankMap());
        }
        if (config.hasIgnoreBlankList()) {
            builder.ignoreBlankList(config.isIgnoreBlankList());
        } else {
            builder.ignoreBlankList(configToBeOverridden.isIgnoreBlankList());
        }
        return builder.build();
    }

    public boolean hasOption(Options... option) {
        if (option == null) {
            return false;
        }
        for (Options opt : option) {
            IgnoreOptionAccessorConfig config = createConfig(opt);
            if (config.hasIgnoreOption()) {
                return true;
            }
        }
        return false;
    }

    public AccessorConfig getAccessorConfig(Options... options) {
        if (options == null) {
            return Accessors.getConfig();
        }
        AccessorConfig accessorConfig = Accessors.getConfig();
        for (Options opt : options) {
            accessorConfig = overrideAccessorConfig(accessorConfig, createConfig(opt));
        }
        return accessorConfig;
    }

    private static class IgnoreOptionAccessorConfig extends AccessorConfig {

        private boolean hasIgnoreNull;
        private boolean hasIgnoreBlankMap;
        private boolean hasIgnoreBlankList;

        public boolean hasIgnoreNull() {
            return hasIgnoreNull;
        }

        public void setHasIgnoreNull(boolean hasIgnoreNull) {
            this.hasIgnoreNull = hasIgnoreNull;
        }

        public boolean hasIgnoreBlankMap() {
            return hasIgnoreBlankMap;
        }

        public void setHasIgnoreBlankMap(boolean hasIgnoreBlankMap) {
            this.hasIgnoreBlankMap = hasIgnoreBlankMap;
        }

        public boolean hasIgnoreBlankList() {
            return hasIgnoreBlankList;
        }

        public void setHasIgnoreBlankList(boolean hasIgnoreBlankList) {
            this.hasIgnoreBlankList = hasIgnoreBlankList;
        }

        public boolean hasIgnoreOption() {
            return hasIgnoreNull || hasIgnoreBlankMap || hasIgnoreBlankList;
        }

        @Override
        public String toString() {
            return "IgnoreOptionAccessorConfig [hasIgnoreNull=" + hasIgnoreNull + ", hasIgnoreBlankMap="
                    + hasIgnoreBlankMap + ", hasIgnoreBlankList=" + hasIgnoreBlankList + ", isIgnoreNull()="
                    + isIgnoreNull() + ", isIgnoreBlankMap()=" + isIgnoreBlankMap() + ", isIgnoreBlankList()="
                    + isIgnoreBlankList() + "]";
        }

        private static class Builder {
            private IgnoreOptionAccessorConfig config = new IgnoreOptionAccessorConfig();

            public Builder ignoreNull(boolean ignoreNull) {
                config.setIgnoreNull(ignoreNull);
                config.setHasIgnoreNull(true);
                return this;
            }

            public Builder ignoreBlankMap(boolean ignoreBlankMap) {
                config.setIgnoreBlankMap(ignoreBlankMap);
                config.setHasIgnoreBlankMap(true);
                return this;
            }

            public Builder ignoreBlankList(boolean ignoreBlankList) {
                config.setIgnoreBlankList(ignoreBlankList);
                config.setHasIgnoreBlankList(true);
                return this;
            }

            public IgnoreOptionAccessorConfig build() {
                return config;
            }
        }
    }
}
