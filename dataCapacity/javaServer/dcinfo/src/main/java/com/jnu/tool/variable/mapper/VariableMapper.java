package com.jnu.tool.variable.mapper;

import com.jnu.tool.variable.pojo.Variable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 */
@Mapper
@Repository("variableMapper")
public interface VariableMapper {
    /**
     * 查询全部变量
     * @return 变量对象列表
     */
    List<Variable> selectAllVariables();

    /**
     * 根据变量名查询满足条件的全部变量
     * @param variableName 要查询的变量名
     * @return 变量对象列表
     */
    Variable selectVariableByName(@Param("variableName") String variableName);

    /**
     * 根据变量别名精准查询满足条件的全部变量
     * @param variableAlias 要查询的变量别名
     * @return 变量对象列表
     */
    List<Variable> selectVariableByAlias(@Param("variableAlias") String variableAlias);

    /**
     * 根据变量别名模糊查询满足条件的全部变量
     * @param variableName 要查询的变量别名
     * @return
     */
    List<Variable> searchVariableByNameFuzzy(@Param("variableName") String variableName);

    /**
     * 根据变量名和变量名查询满足条件的全部变量
     * @param variableName 要查询的变量名
     * @param variableAlias 要查询的变量别名
     * @return 变量对象列表
     */
    List<Variable> selectVariableByNameAndAlias(@Param("variableName") String variableName,
                                                @Param("variableAlias") String variableAlias);

    /**
     * 插入一条变量记录
     * @param variable 要插入的变量对象
     * @return 影响的记录数
     */
    int insertVariable(Variable variable);

    /**
     * 根据变量名删除变量记录
     * @param variableName 要删除的变量名
     * @return 影响的记录数
     */
    int deleteVariableByName(@Param("variableName") String variableName);

    /**
     * 修改一条变量记录
     * @param variable 修改后的变量对象
     * @return 影响的记录数
     */
    int updateVariable(Variable variable);

}
