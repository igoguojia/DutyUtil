import json
import sysInteface.OracleDB as OracleDB
import sysInteface.sqlDemoList as sqlDemoList
import tools
from AuthToken.AccessContorl import AcsControl
import timeTask.DutyInform.IntefaceFcn as IntefaceFcn


# 返回所有值班信息主表
@AcsControl
def GetAllOnDuty(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    OracleDB.GetDB()

    one, title = OracleDB.SelectDB(sqlDemoList.sDcOnDutyQrySql)
    OracleDB.CloseDB()
    return json.dumps({
        'success': True,
        'data': tools.FormatDBToList(one, title, True)
    })


# 返回userID的所有值班信息主表
@AcsControl
def GetOnDuty(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    OracleDB.GetDB()

    sUserId = formDataMap['userID']
    one, title = OracleDB.SelectDB(sqlDemoList.sDcOnDutyQryByUserIdSql %
                                   {'userID': sUserId})
    OracleDB.CloseDB()
    return json.dumps({
        'success': True,
        'data': tools.FormatDBToList(one, title, True)
    })


# 返回duty_name的所有值班信息主表
@AcsControl
def GetOnDutyByName(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    OracleDB.GetDB()

    sdutyName = formDataMap['duty_name']
    one, title = OracleDB.SelectDB(sqlDemoList.sDcOnDutyQryByDutyNameSql %
                                   {'duty_name': sdutyName})
    OracleDB.CloseDB()
    return json.dumps({
        'success': True,
        'data': tools.FormatDBToList(one, title, True)
    })


# 返回group的所有值班信息主表
# @AcsControl
def GetOnDutyByGroup(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    OracleDB.GetDB()

    sdutyGroup = formDataMap['group_name2']
    one, title = OracleDB.SelectDB(sqlDemoList.sDcOnDutyQryByGroupSql %
                                   {'group_name2': sdutyGroup})
    OracleDB.CloseDB()
    return json.dumps({
        'success': True,
        'data': tools.FormatDBToList(one, title, True)
    })


# 新增值班信息主表
@AcsControl
def AddOnDuty(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    sDutyName = formDataMap['duty_name']
    # sGroupName = formDataMap['group_name']
    # sPeopleCount = formDataMap['people_count']
    # sDutyMsg = formDataMap['duty_msg']
    # sDutyPeople = formDataMap['duty_people']
    # # if 'userId' in formDataMap:
    # #     formDataMap['userID'] = formDataMap['userId']
    # sUserId = formDataMap['userID']
    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcOnDutyQryByDutyNameSql %
                                   {'duty_name': sDutyName})
    if len(one) > 0:
        OracleDB.CloseDB()
        return json.dumps({'success': False, 'msg': '值班名称已存在'})
    IsSecu, sErrorMsg = OracleDB.ExecDB(sqlDemoList.sDcOnDutyAddSql %
                                        formDataMap)
    OracleDB.CloseDB()
    IntefaceFcn.ResetDuty(sDutyName)
    return json.dumps({'success': IsSecu, 'msg': sErrorMsg})


# 修改值班信息主表
@AcsControl
def ModOnDuty(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    sDutyName = formDataMap['duty_name']
    sFlag: bool = formDataMap['flag']
    # sGroupName = formDataMap['group_name']
    # sPeopleCount = formDataMap['people_count']
    # sDutyMsg = formDataMap['duty_msg']
    # sDutyPeople = formDataMap['duty_people']
    # if 'userId' in formDataMap:
    #     formDataMap['userID'] = formDataMap['userId']
    # sUserId = formDataMap['userID']
    # sPubNoticeWeek = formDataMap['pub_notice_week']

    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcOnDutyQryByDutyNameSql %
                                   {'duty_name': sDutyName})
    if len(one) == 0:
        OracleDB.CloseDB()
        return json.dumps({'success': False, 'msg': '值班名称不存在'})

    if tools.FormatDBToList(one, title)[0]['USER_ID'] != formDataMap['userID']:
        return json.dumps({'success': False, 'msg': '只能修改自己创建的值班任务！'})

    IsSecu, sErrorMsg = OracleDB.ExecDB(sqlDemoList.sDcOnDutyModSql %
                                        formDataMap)
    OracleDB.CloseDB()
    if sFlag:
        IntefaceFcn.ResetDuty(sDutyName)
    return json.dumps({'success': IsSecu, 'msg': sErrorMsg})


# 删除值班信息主表
@AcsControl
def DelOnDuty(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    sDutyName = formDataMap['duty_name']
    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcOnDutyQryByDutyNameSql %
                                   {'duty_name': sDutyName})
    if len(one) == 0:
        OracleDB.CloseDB()
        return json.dumps({'success': True, 'msg': ''})

    if tools.FormatDBToList(one, title)[0]['USER_ID'] != formDataMap['userID']:
        return json.dumps({'success': False, 'msg': '只能删除自己创建的值班任务！'})

    IsSecu, sErrorMsg = OracleDB.ExecDB(sqlDemoList.sDcOnDutyDelSql %
                                        formDataMap)
    IsSecu, sMsg = IntefaceFcn.DelDuty(sDutyName)
    OracleDB.CloseDB()
    return json.dumps({'success': IsSecu, 'msg': sErrorMsg + ' ' + sMsg})


# 返回钉钉人员信息
# @AcsControl
def GetDingPersonInfo(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDingPersonInfoSql)
    OracleDB.CloseDB()
    return json.dumps({
        'success': True,
        'data': tools.FormatDBToList(one, title, True)
    })


# 返回钉钉通道
@AcsControl
def GetDingTalkInfo(formDataMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcDingTalkInfoQrySql)
    OracleDB.CloseDB()
    return json.dumps({
        'success': True,
        'data': tools.FormatDBToList(one, title, True)
    })
