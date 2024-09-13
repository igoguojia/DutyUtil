package com.jnu.tool.variable.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iceolive.util.StringUtil;
import com.jnu.tool.utils.ResultEntity;
import com.jnu.tool.variable.pojo.Variable;
import com.jnu.tool.variable.service.api.VariableService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author duya001
 */
@CrossOrigin
@RestController
@RequestMapping("/variable")
public class VariableController {
    private static final Logger LOGGER = Logger.getLogger("com.jnu.tool.variable.controller.VariableController");

    private final VariableService variableService;

    public VariableController(@Qualifier("variableService") VariableService variableService) {
        this.variableService = variableService;
    }

    @PostMapping("/retrieveVariable")
    public String retrieveVariable(@RequestParam(value = "variableName", defaultValue = "") String variableName,
                                   @RequestParam(value = "variableAlias", defaultValue = "") String variableAlias) throws JsonProcessingException {
        variableName = variableName.trim();
        variableAlias = variableAlias.trim();
        if (StringUtil.isBlank(variableName) && StringUtil.isBlank(variableAlias)) {
            List<Variable> variables = variableService.searchAllVariables();
            LOGGER.info("查询全配置成功！");
            return ResultEntity.successWithData(variables).returnResult();
        } else if ("".equals(variableAlias)){
            Variable variable = variableService.searchVariableByName(variableName);
            if (variable == null) {
                LOGGER.warning("数据库中无配置的信息！");
                return ResultEntity.failWithoutData("数据库中无配置的信息！").returnResult();
            }
            LOGGER.info("根配置名查询成功！");
            return ResultEntity.successWithData(variable).returnResult();
        } else if ("".equals(variableName)){
            List<Variable> variable = variableService.searchVariableByAlias(variableAlias);
            if (variable == null) {
                LOGGER.warning("数据库中无配置的信息！");
                return ResultEntity.failWithoutData("数据库中无配置的信息！").returnResult();
            }
            LOGGER.info("根据配置别名查询成功！");
            return ResultEntity.successWithData(variable).returnResult();
        } else {
            List<Variable> variable = variableService.searchVariableByNameAndAlias(variableName, variableAlias);
            if (variable == null) {
                LOGGER.warning("数据库中无配置的信息！");
                return ResultEntity.failWithoutData("数据库中无配置的信息！").returnResult();
            }
            LOGGER.info("根据配置名和别名联合查询成功！");
            return ResultEntity.successWithData(variable).returnResult();
        }
    }


    @PostMapping("/fuzzyVariable")
    public String fuzzyVariable(@RequestParam(value = "variableName", defaultValue = "") String variableName,
                                @RequestParam(value = "variableAlias", defaultValue = "") String variableAlias) throws JsonProcessingException {
        variableName = variableName.trim();
        variableAlias = variableAlias.trim();
        if (StringUtil.isBlank(variableName) && StringUtil.isBlank(variableAlias)) {
            List<Variable> variables = variableService.searchAllVariables();
            LOGGER.info("查询全配置成功！");
            return ResultEntity.successWithData(variables).returnResult();
        } else if ("".equals(variableAlias)){
            List<Variable> variable = variableService.searchVariableByNameFuzzy(variableName);
            if (variable == null) {
                LOGGER.warning("数据库中无配置的信息！");
                return ResultEntity.failWithoutData("数据库中无配置的信息！").returnResult();
            }
            LOGGER.info("根配置名查询成功！");
            return ResultEntity.successWithData(variable).returnResult();
        } else if ("".equals(variableName)){
            List<Variable> variable = variableService.searchVariableByAlias(variableAlias);
            if (variable == null) {
                LOGGER.warning("数据库中无配置的信息！");
                return ResultEntity.failWithoutData("数据库中无配置的信息！").returnResult();
            }
            LOGGER.info("根据配置别名查询成功！");
            return ResultEntity.successWithData(variable).returnResult();
        } else {
            List<Variable> variable = variableService.searchVariableByNameAndAlias(variableName, variableAlias);
            if (variable == null) {
                LOGGER.warning("数据库中无配置的信息！");
                return ResultEntity.failWithoutData("数据库中无配置的信息！").returnResult();
            }
            LOGGER.info("根据配置名和别名联合查询成功！");
            return ResultEntity.successWithData(variable).returnResult();
        }
    }
    @PostMapping("/createVariable")
    public String createVariable(@RequestParam(value = "variableName", defaultValue = "") String variableName,
                                 @RequestParam(value = "variableAlias", defaultValue = "") String variableAlias,
                                 @RequestParam(value = "variableValue", defaultValue = "") String variableValue,
                                 @RequestParam(value = "variableDescription", defaultValue = "") String variableDescription) throws JsonProcessingException {
        if ("".equals(variableName) || "".equals(variableValue)) {
            LOGGER.warning("添加配置时参数异常！");
            return ResultEntity.failWithoutData("参数异常！").returnResult();
        }
        if (variableService.searchVariableByName(variableName) != null) {
            LOGGER.warning("添加配置时已有同名配置项！");
            return ResultEntity.failWithoutData("已有同名配置项！").returnResult();
        }
        Variable variable = new Variable(variableName, variableAlias, variableValue, variableDescription);
        boolean success = variableService.addVariable(variable);
        if (success) {
            LOGGER.info("添加配置成功！");
            return ResultEntity.successWithoutData().returnResult();
        } else {
            LOGGER.warning("添加配置失败！");
            return ResultEntity.failWithoutData("添加配置失败！").returnResult();
        }
    }

    @PostMapping("/deleteVariable")
    public String deleteVariable(@RequestParam(value = "variableName", defaultValue = "") String variableName) throws JsonProcessingException {
        if ("".equals(variableName)) {
            LOGGER.warning("删除配置时参数异常！");
            return ResultEntity.failWithoutData("参数异常！").returnResult();
        }
        if (variableService.searchVariableByName(variableName) == null) {
            LOGGER.warning("删除配置时无该配置项！");
            return ResultEntity.failWithoutData("删除配置时无该配置项！").returnResult();
        }
        boolean success = variableService.removeVariableByName(variableName);
        if (success) {
            LOGGER.info("删除配置成功！");
            return ResultEntity.successWithoutData().returnResult();
        } else {
            LOGGER.warning("删除配置失败！");
            return ResultEntity.failWithoutData("删除配置失败！").returnResult();
        }
    }

    @PostMapping("updateVariable")
    public String updateVariable(@RequestParam(value = "variableName", defaultValue = "") String variableName,
                                 @RequestParam(value = "variableAlias", defaultValue = "") String variableAlias,
                                 @RequestParam(value = "variableValue", defaultValue = "") String variableValue,
                                 @RequestParam(value = "variableDescription", defaultValue = "") String variableDescription) throws JsonProcessingException {
        if ("".equals(variableName) || "".equals(variableValue)) {
            LOGGER.warning("修改配置时参数异常！");
            return ResultEntity.failWithoutData("参数异常！").returnResult();
        }
        if (variableService.searchVariableByName(variableName) == null) {
            LOGGER.warning("修改配置时无该配置项！");
            return ResultEntity.failWithoutData("修改配置时无该配置项！").returnResult();
        }
        Variable variable = new Variable(variableName, variableAlias, variableValue, variableDescription);
        boolean success = variableService.changeVariable(variable);
        if (success) {
            LOGGER.info("修改配置成功！");
            return ResultEntity.successWithoutData().returnResult();
        } else {
            LOGGER.warning("修改配置失败！");
            return ResultEntity.failWithoutData("修改配置失败！").returnResult();
        }
    }
}
