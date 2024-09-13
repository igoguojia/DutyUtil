package com.jnu.tool.variable.service.api;

import com.jnu.tool.variable.pojo.Variable;

import java.util.List;

/**
 * @author duya001
 */
public interface VariableService {
    /**
     * 查询全部变量
     * @return 变量对象列表
     */
    List<Variable> searchAllVariables();

    /**
     * 根据变量名精准查询满足条件的全部变量
     * @param variableName 要查询的变量名
     * @return 变量对象
     */
    Variable searchVariableByName(String variableName);

    /**
     * 根据变量名模糊查询满足条件的全部变量
     * @param variableName 要查询的变量名
     * @return
     */
    List<Variable> searchVariableByNameFuzzy(String variableName);


    /**
     * 根据配置别名查询配置
     * @param variableAlias 配置别名
     * @return 变量对象
     */
    List<Variable> searchVariableByAlias(String variableAlias);

    /**
     * 根据配置名和配置别名查询配置
     * @param variableName 配置名
     * @param variableAlias 配置别名
     * @return 变量对象
     */
    List<Variable> searchVariableByNameAndAlias(String variableName, String variableAlias);

    /**
     * 插入一条变量记录
     * @param variable 要插入的变量对象
     * @return 插入成功标志，成功返回true，失败返回false
     */
    boolean addVariable(Variable variable);

    /**
     * 根据变量名删除变量记录
     * @param variableName 要删除的变量名
     * @return 删除成功标志，成功返回true，失败返回false
     */
    boolean removeVariableByName(String variableName);

    /**
     * 修改一条变量记录
     * @param variable 修改后的变量对象
     * @return 修改成功标志，成功返回true，失败返回false
     */
    boolean changeVariable(Variable variable);

}
