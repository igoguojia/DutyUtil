# 本模块负责对外接口
import logging

from datetime import date, timedelta, datetime
from functools import wraps

from chinese_calendar import is_workday
from typing import List

import timeTask.DutyInform.DCOnDutyTab as onDutyTab
import timeTask.DutyInform.DCTmpDutyTab as tmpDutyTab
import timeTask.DutyInform.DingPerInfoTab as dingInfoTab
import timeTask.DutyInform.BuildDutySCH as buildSCH
import timeTask.DutyInform.ProcessDingTalk as proDing
import timeTask.DutyInform.initFcn as initF
import timeTask.DutyInform.OracleDB as Oracle
from timeTask.DutyInform.LogitSetting import logit
import timeTask.DutyInform.timeTaskFcn as taskFcn

# 查询userid所有权限
sDcUserAuthQrySql = '''\
select a.user_id,b.authority_id from jn_asset.dcusercharacter a,jn_asset.dccharacterauthority b 
where a.character_id = b.character_id
  and a.user_id = '%(user_id)s'
 union
select a.user_id,a.authority_id from jn_asset.dcuserauthority a where a.user_id = '%(user_id)s' 
'''

# 查询值班任务创建人员id
sDcAdminIdQry = '''select a.user_id from DCONDUTY a
where a.duty_name='%(duty_name)s'
'''

# 查询user和dutyname值班任务创建人员是否为一组
sIsinAdminGroup = '''select b.OPERATOR_NO,b.group_name from jn_asset.dcusers b
where b.group_name in (select b.group_name from jn_asset.dcusers b,DCONDUTY a
where a.user_id=b.OPERATOR_NO and a.duty_name='%(duty_name)s') and b.OPERATOR_NO='%(user_id)s'
'''


#
# def DCAcsControl(flag):
#     """权限控制
#     ：user_ID：调用者id
#     ：flag：所需权限等级 超级权限为0级；修改为1级 ；查询为2级
#     :UserAccess: 用户权限等级 超级权限0级；值班任务创建者1级；组员2级
#     """
#
#     def DCACL(func):
#         @wraps(func)
#         def decorated(dutyName, sUserId, *args):
#             reInit = initF.init()
#             if not reInit[0]:
#                 return False, reInit[1]
#             sUserId = str(sUserId)
#             UserAccess = -1
#             sUserAuth = []
#             result = initF.RawOracleData2MapList(
#                 Oracle.SelectDB(sDcUserAuthQrySql % {'user_id': sUserId}))
#             if result[0]:
#                 for one in result[1]:
#                     sUserAuth.append(one['AUTHORITY_ID'])
#             if accessMap['Duty'] in sUserAuth:
#                 UserAccess = 0
#             else:
#                 InGroupList = initF.RawOracleData2MapList(
#                     Oracle.SelectDB(sIsinAdminGroup % {'duty_name': dutyName, 'user_id': sUserId}))
#                 if InGroupList[0]:
#                     AdminIDQryList = initF.RawOracleData2MapList(
#                         Oracle.SelectDB(sDcAdminIdQry % {'duty_name': dutyName}))
#                     if AdminIDQryList[0]:
#                         adminId = AdminIDQryList[1][0]['USER_ID']
#                         if adminId == sUserId:
#                             UserAccess = 1
#                         else:
#                             UserAccess = 2
#
#             if 0 <= UserAccess <= flag:
#                 f = func(dutyName, *args)
#             else:
#                 logging.info(f'用户 {sUserId} 操作 {dutyName} 值班任务失败 权限未通过')
#                 f = False, '用户权限不通过'
#             initF.CloseDB()
#             return f
#
#         return decorated
#
#     return DCACL


@logit('dutyInfo.log')
def DelDuty(dutyName):
    Oracle.GetDB()
    OraTru = f'''delete from jn_asset.DCTMPONDUTY a where a.DUTY_NAME='{dutyName}' '''
    IsSecu, sMsg = Oracle.ExecDB(OraTru)
    Oracle.CloseDB()
    if IsSecu:
        logging.info(f'成功清空临时表 dutyname= {dutyName} ')
        return True, '成功删除'
    else:
        logging.info(f'尝试清空临时表失败 dutyname= {dutyName} ')
        return False, sMsg


@logit('dutyInfo.log')
def ResetDuty(dutyName):
    """
    用于新建值班任务后立即生成值班安排 或是 编辑值班任务后的重置操作
    @param dutyName: 值班名
    @return:
    """
    logging.info(f'DUTYNAME={dutyName} 重置/初始化开始-------------------------')
    # # flag = int(flag)
    # day1 = timedelta(days=1)
    # today = date.today()
    Oracle.GetDB()
    IsSecu, sData = onDutyTab.GetOnDutyTab(dutyName)
    if not IsSecu:
        return False, sData
    dutyMap = sData
    OraTru = f'''delete from jn_asset.DCTMPONDUTY a where a.DUTY_NAME='{dutyName}' '''
    Oracle.ExecDB(OraTru)
    OraUpDutyNow = f'''update DCONDUTY a set a.duty_now=' ' where a.duty_name='{dutyName}' '''
    Oracle.ExecDB(OraUpDutyNow)

    sMsg = taskFcn.PadTmpTab(dutyName, dutyMap, False)
    # 定时任务已运行，保留今日值班安排，从明天开始生成新值班
    # padTeturn = taskFcn.PadTmpTab(dutyName, dutyMap)
    # else:
    #     if dutyDate == ' ' or (today - initF.strptime(dutyDate)).days == 1:
    #         # dutynow dutydate 为昨日数据/新建（为空），今日还未更新
    #         # 定时任务未运行，不保留今日值班安排，从今天开始生成新值班
    #         padTeturn = taskFcn.PadTmpTab(dutyName, dutyMap, today, bDayDuty=False)
    #         # 从今天开始生成值班 需要更新今天的dutynow
    #         taskFcn.loopsDutyNow(dutyName, dutyMap, today)
    #     else:
    #         return False, '值班系统紊乱，请检查数据库 DUTY_DATE 字段'
    Oracle.CloseDB()
    logging.info(f'DUTYNAME={dutyName} 重置/初始化结束-------------------------\n\n')

    return True, sMsg


# @logit('dutyInfo.log')
# @DCAcsControl(1)
# def UpdateTmpName(dutyName, newName):
#     UpdateOracle = f'''update jn_asset.DCTMPONDUTY a
# set a.duty_name='{newName}'
# where a.duty_name='{dutyName}'
# '''
#     resUpdate = Oracle.ExecDB(UpdateOracle)
#     if resUpdate[0]:
#         logging.info(
#             f'DCTMPONDUTY表更改 DUTY_NAME {dutyName} 为 {newName}'
#         )
#         return True, 'DCTMPONDUTY表更改值班名称成功'
#     else:
#         logging.warning('DCTMPONDUTY表更改值班名称' + str(resUpdate) + '\n' +
#                         UpdateOracle)
#         return False, 'DCTMPONDUTY表更改值班名称失败！请检查日志'


@logit('dutyInfo.log')
# def UpdateTmpDuty(dutyName, dutyPeople, dutyDate, newPeople):
def UpdateTmpDuty(dutyName, dutyDate, newDDidList: List):
    # dutyPeople = str(dutyPeople)
    # newPeople = str(newPeople)
    # UpdateOracle = f'''update jn_asset.DCTMPONDUTY a
    # set a.duty_people='{newPeople}'
    # where a.duty_name='{dutyName}'and a.duty_people='{dutyPeople}'and a.duty_date='{dutyDate}' and rownum=1
    # '''
    #
    # IsSecu, sMsg = Oracle.ExecDB(UpdateOracle)
    Oracle.GetDB()
    OraTru = f'''delete from jn_asset.DCTMPONDUTY a where a.DUTY_NAME='{dutyName}' and a.DUTY_DATE='{dutyDate}' '''
    Oracle.ExecDB(OraTru)
    OraInsert = 'insert into jn_asset.DCTMPONDUTY(duty_name,duty_people,duty_date) values (:duty_name,:duty_people,:duty_date)'
    OraInsertParam = []
    for dingid in newDDidList:
        OraInsertParam.append({
            'duty_name': dutyName,
            'duty_people': dingid,
            'duty_date': dutyDate
        })
    IsSecu, sMsg = Oracle.ExecParamDB(OraInsert, OraInsertParam)
    Oracle.CloseDB()
    if IsSecu:
        logging.info(
            f'DCTMPONDUTY表更改值班人员 {dutyName} {dutyDate}  为 {newDDidList}'
        )
        return True, '值班人员更改成功'
    else:
        logging.warning('DCTMPONDUTY表更改值班人员失败' + str(sMsg) + '\n')
        return False, 'DCTMPONDUTY表更改值班人员失败！请检查日志'


# @logit('dutyInfo.log')
# def QuryOneDay(dutyName, theday: str):
#     if theday == '':
#         theday = date.today().strftime('%Y-%m-%d')
#     else:
#         theday = datetime.strptime(theday,
#                                    '%Y-%m-%d').date().strftime('%Y-%m-%d')
#     QuryOracle = f'''select * from jn_asset.DCTMPONDUTY a
#     where a.duty_name='{dutyName}' and a.duty_date='{theday}'
#     '''
#     rawData = Oracle.SelectDB(QuryOracle)
#     result = initF.RawOracleData2MapList(rawData)
#     if not result[0]:
#         logging.warning('DCTMPONDUTY 表没有该条目：' + dutyName + ' 日期: ' + theday)
#         return False, 'DCTMPONDUTY 查询失败！请检查日志'
#
#     def GetDuty_People(dict_):
#         return dict_['DUTY_PEOPLE']
#
#     return True, list(map(GetDuty_People, result[1]))


@logit('dutyInfo.log')
# def QuryDaysTmpDuty(dutyName, flag):
def QuryDaysTmpDuty(dutyName):
    """
    查询临时表值班安排
    @param dutyName:
    @return:
    """
    # flag = int(flag)
    # if flag < 0:
    #     return False, 'flag非法！'
    Oracle.GetDB()
    IsSecu, sData = tmpDutyTab.GetTmpDutyTab(dutyName)
    Oracle.CloseDB()
    if not IsSecu:
        return False, sData
    TmpDutyMapGet = sData
    # day1 = timedelta(days=1)
    # today = date.today()
    # maxDate = '2021-01-01'
    # 去除当天之前的临时值班表条目
    # for key in list(TmpDutyMapGet.keys()):
    #     maxDate = max(maxDate, key)
    #     if key < initF.strftime(today):
    #         TmpDutyMapGet.pop(key)

    # if flag == 0:
    sDingDutyMap = TmpDutyMapGet
    # else:
    #     reGetDuty = onDutyTab.GetOnDutyTab(dutyName)
    #     if not reGetDuty[0]:
    #         return False, reGetDuty[1]
    #     dutyMap = reGetDuty[1]
    #     dutyPpList = dutyMap['DUTY_PEOPLE'].split(',')
    #     dutyPpCnt = dutyMap['PEOPLE_COUNT']
    #     dutyNow: str = dutyMap['DUTY_NOW']
    #     lenPp = len(dutyPpList)
    #     dutyDate: str = dutyMap['DUTY_DATE']
    #     maxDate = initF.strptime(maxDate)
    #     dutyNow = initF.calDutyNow(dutyDate, maxDate, day1, dutyPpList, dutyNow, dutyPpCnt, lenPp)
    #
    #     _, DaysDutyMap = buildSCH.BuildDaysDuty(dutyPpList, dutyPpCnt, dutyNow,
    #                                             maxDate + day1, flag=flag)
    #     reGetInfo = dingInfoTab.GetDingInfoTab(DaysDutyMap)
    #     if not reGetInfo[0]:
    #         return False, reGetInfo[1],
    #     _, dutyKeys, dutyValues, NoDDidList = reGetInfo
    #     _, DingDutyMap = proDing.BuildDingPersonInfo(dutyKeys, dutyValues,
    #                                                  NoDDidList)
    #     sDingDutyMap = initF.dictDate2Str(DingDutyMap)
    #     sDingDutyMap.update(TmpDutyMapGet)
    return True, sDingDutyMap
