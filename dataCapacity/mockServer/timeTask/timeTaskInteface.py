# 本文件处理定时任务接口
import json
from typing import List

import timeTask.OracleDB as OracleDB
import timeTask.DutyInform.initFcn as initFcn
import timeTask.DutyInform.IntefaceFcn as IntefaceFcn
from AuthToken.AccessContorl import AcsControl


# 判断功能路径是否属于定时任务接口支持的功能
def isTimeTask(sPathInfo):
    # return sPathInfo in ['/UpTmpDuty', '/QuryTmpDuty', '/QuryOneDay', '/ResetDuty', '/DelDuty']
    return sPathInfo in ['/UpTmpDuty', '/QuryTmpDuty']


# 功能登陆界面
def login(formDataMap, paramMap, start_response, sFunction):
    if sFunction == '/UpTmpDuty':
        return TmpDutyUp(formDataMap, start_response)
    elif sFunction == '/QuryTmpDuty':
        return TmpDutyQury(formDataMap, start_response)
    # elif sFunction == '/QuryOneDay':
    #     return TmpOneDayDutyQury(formDataMap, start_response)
    # elif sFunction == '/ResetDuty':
    #     return ResetDuty(formDataMap, start_response)
    # elif sFunction == '/DelDuty':
    #     return DelDuty(formDataMap, start_response)


# # @AcsControl
# def DelDuty(formDataMap, start_response):
#     start_response('200 OK', [('Content-Type', 'application/json'),
#                               ('Access-Control-Allow-Origin', '*')])
#     dutyName = formDataMap['dutyName']
#     # userID = formDataMap['userID']
#     IsSecu, sMsg = IntefaceFcn.DelDuty(dutyName)
#     return json.dumps({'success': IsSecu, 'msg': sMsg})
#
#
# # @AcsControl
# def ResetDuty(formDataMap, start_response):
#     start_response('200 OK', [('Content-Type', 'application/json'),
#                               ('Access-Control-Allow-Origin', '*')])
#     dutyName = formDataMap['dutyName']
#     # userID = formDataMap['userID']
#     # flag = formDataMap['flag']
#     IsSecu, sMsg = IntefaceFcn.ResetDuty(dutyName)
#     return json.dumps({'success': IsSecu, 'msg': sMsg})


# def TmpNameUp(formDataMap, start_response):
#     start_response('200 OK', [('Content-Type', 'application/json'),
#                               ('Access-Control-Allow-Origin', '*')])
#     dutyName = formDataMap['dutyName']
#     userID = formDataMap['userID']
#     newName = formDataMap['newName']
#
#     IsSecu, sMsg = IntefaceFcn.UpdateTmpName(dutyName, userID, newName)
#     return json.dumps({'success': IsSecu, 'msg': sMsg})


@AcsControl
def TmpDutyUp(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    dutyName = formDataMap['dutyName']
    # dutyPeople = formDataMap['dutyPeople']
    dutyDate = formDataMap['date']
    newDDidList: List = formDataMap['newPeople']
    if not newDDidList:
        newDDidList.append('None Duty')
    userID = formDataMap['userID']
    OracleDB.GetDB()
    sDcOnDutyQryByDutyNameSql = '''select * from jn_asset.dconduty a where a.duty_name = '%(duty_name)s' '''
    selectData = OracleDB.SelectDB(sDcOnDutyQryByDutyNameSql %
                                   {'duty_name': dutyName})
    OracleDB.CloseDB()
    if initFcn.RawOracleData2MapList(selectData)[1][0]['USER_ID'] == userID:
        IsSecu, sMsg = IntefaceFcn.UpdateTmpDuty(dutyName, dutyDate, newDDidList)
    else:
        IsSecu = False
        sMsg = '只能由值班任务创建者修改'
    return json.dumps({'success': IsSecu, 'msg': str(sMsg)})


# @AcsControl
def TmpDutyQury(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    dutyName = formDataMap['dutyName']
    # flag = formDataMap['flag']
    # userID = formDataMap['userID']
    IsSecu, data = IntefaceFcn.QuryDaysTmpDuty(dutyName)
    return json.dumps({'success': IsSecu, 'data': data})

# @AcsControl
# def TmpOneDayDutyQury(formDataMap, start_response):
#     start_response('200 OK', [('Content-Type', 'application/json'),
#                               ('Access-Control-Allow-Origin', '*')])
#     dutyName = formDataMap['dutyName']
#     userID = formDataMap['userID']
#     theday = formDataMap.get('date', '')
#
#     IsSecu, sMsg = IntefaceFcn.QuryOneDay(dutyName, userID, theday)
#     return json.dumps({'success': IsSecu, 'msg': sMsg})
