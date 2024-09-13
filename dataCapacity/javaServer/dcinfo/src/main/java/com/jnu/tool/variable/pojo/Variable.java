package com.jnu.tool.variable.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duya001
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Variable {
    private String variableName;
    private String variableAlias;
    private String variableValue;
    private String variableDescription;
}
