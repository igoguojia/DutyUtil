package com.jnu.tool.task.timed.util;

/**
 * @author duya001
 */
public class ParamConstant {
    private static String currentParam;

    private static final String[] PARAM_TYPE =
            new String[]{"int", "short", "long", "float", "double", "char", "boolean", "String",
                    "Integer", "Short", "Long", "Float", "Double", "Character", "Boolean"};

    private static boolean isLegalParam(String param) {
        if (param == null || "".equals(param)) {
            return false;
        }
        for (String s : PARAM_TYPE) {
            if (param.equals(s)) {
                currentParam = param;
                return true;
            }
        }
        return false;
    }

    public static Class<?> getClass(String param) {
        if (!isLegalParam(param)) {
            return null;
        }
        switch (currentParam) {
            case "int":
                return int.class;
            case "short":
                return short.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "char":
                return char.class;
            case "boolean":
                return boolean.class;
            case "String":
                return String.class;
            case "Integer":
                return Integer.class;
            case "Short":
                return Short.class;
            case "Long":
                return Long.class;
            case "Float":
                return Float.class;
            case "Double":
                return Double.class;
            case "Character":
                return Character.class;
            case "Boolean":
                return Boolean.class;
            default:
                return null;
        }
    }
}
