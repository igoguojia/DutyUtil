# 本文件用来接收转发系统内部的接口
import sysInteface.DCLogin as DCLogin
import sysInteface.DCOnDutyMange as DCOnDutyMange
import sysInteface.DCServerInfoMange as DCServerInfoMange
import sysInteface.DCUserInfoMange as DCUserInfoMange
import json


def systemInteface(formDataMap, paramMap, start_response, sFunction):
    if sFunction == '/DCLogin':
        return DCLogin.Login(formDataMap, start_response)
    # elif sFunction == '/DCGetMenu':
    #     return DCLogin.GetMenu(formDataMap, start_response)
    elif sFunction == '/DCUserQry':
        return DCUserInfoMange.usersQry(formDataMap, start_response)
    elif sFunction == '/DCUserMod':
        return DCUserInfoMange.usersMod(formDataMap, start_response)
    elif sFunction == '/DCServerAdd':
        return DCServerInfoMange.ServerAdd(formDataMap, start_response)
    elif sFunction == '/DCServerMod':
        return DCServerInfoMange.ServerMod(formDataMap, start_response)
    elif sFunction == '/DCServerDel':
        return DCServerInfoMange.ServerDel(formDataMap, start_response)
    elif sFunction == '/DCServerQuery':
        return DCServerInfoMange.ServerQuery(paramMap, start_response)
    elif sFunction == '/getAllOnDutyInfo':
        return DCOnDutyMange.GetAllOnDuty(formDataMap, start_response)
    elif sFunction == '/getOnDutyInfo':
        return DCOnDutyMange.GetOnDuty(formDataMap, start_response)
    elif sFunction == '/getOnDutyInfoByName':
        return DCOnDutyMange.GetOnDutyByName(formDataMap, start_response)
    elif sFunction == '/getOnDutyInfoByGroup':
        return DCOnDutyMange.GetOnDutyByGroup(formDataMap, start_response)
    elif sFunction == '/getDingPersonInfo':
        return DCOnDutyMange.GetDingPersonInfo(formDataMap, start_response)
    elif sFunction == '/getDingTalkInfo':
        return DCOnDutyMange.GetDingTalkInfo(formDataMap, start_response)
    elif sFunction == '/addOnDutyInfo':
        return DCOnDutyMange.AddOnDuty(formDataMap, start_response)
    elif sFunction == '/modOnDutyInfo':
        return DCOnDutyMange.ModOnDuty(formDataMap, start_response)
    elif sFunction == '/delOnDutyInfo':
        return DCOnDutyMange.DelOnDuty(formDataMap, start_response)
    else:
        print(sFunction)
        start_response('404 OK', [('Content-Type', 'text/html'),
                                  ('Access-Control-Allow-Origin', '*')])
        return '没有找到该功能'


def LoginFail(msg, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    return json.dumps({'success': False, 'msg': '失败！' + str(msg)})
