package com.jnu.tool.variable.service.impl;

import com.jnu.tool.variable.mapper.VariableMapper;
import com.jnu.tool.variable.pojo.Variable;
import com.jnu.tool.variable.service.api.VariableService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author duya001
 */
@Scope("prototype")
@Service("variableService")
public class VariableServiceImpl implements VariableService {
    private final VariableMapper variableMapper;

    public VariableServiceImpl(@Qualifier("variableMapper") VariableMapper variableMapper) {
        this.variableMapper = variableMapper;
    }

    @Override
    public List<Variable> searchAllVariables() {
        return variableMapper.selectAllVariables();
    }

    @Override
    public Variable searchVariableByName(String variableName) {
        return variableMapper.selectVariableByName(variableName);
    }

    @Override
    public List<Variable> searchVariableByNameFuzzy(String variableName) {
        return variableMapper.searchVariableByNameFuzzy(variableName);
    }

    @Override
    public List<Variable> searchVariableByAlias(String variableAlias) {
        return variableMapper.selectVariableByAlias(variableAlias);
    }

    @Override
    public List<Variable> searchVariableByNameAndAlias(String variableName, String variableAlias) {
        return variableMapper.selectVariableByNameAndAlias(variableName, variableAlias);
    }

    @Override
    public boolean addVariable(Variable variable) {
        if (variable == null || "".equals(variable.getVariableName())) {
            return false;
        }
        return variableMapper.insertVariable(variable) != 0;
    }

    @Override
    public boolean removeVariableByName(String variableName) {
        if ("".equals(variableName)) {
            return false;
        }
        return variableMapper.deleteVariableByName(variableName) != 0;
    }

    @Override
    public boolean changeVariable(Variable variable) {
        if (variable == null || "".equals(variable.getVariableName())) {
            return false;
        }
        return variableMapper.updateVariable(variable) != 0;
    }
}
