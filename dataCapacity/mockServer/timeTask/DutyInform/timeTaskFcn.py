# 本模块根据不同时刻进行函数组合
# 临时值班表中存放一轮值班数据（若一轮值班不够initF.padDays个工作日则存放多轮直至足数）
# 定时任务时间 每天8点
# 若为临时表填充日:填充临时表
# 若为值班计划发布日：发布值班计划
# 若为工作日： 发布当天值班人员 并更新dutynow
# 全日：若没有值班计划则生成
# 值班日：工作日减去周末
import logging

from chinese_calendar import is_workday
from datetime import date, timedelta

from typing import List

day1 = timedelta(days=1)
today = date.today()


def ChargeNoticeDay(spubNotice: int, skip: int, selfholidays: List[str]):
    """判断是否为发布周值班计划日
    :spubNotice:ONDUTY 表中PUB_NOTICE_WEEK字段
    :skip:ONDUTY 表中auto_skip字段 是否智能跳过节假日
    :selfholidays:自定义节假日列表
    :return: 是否为周值班 安排发布日
    """
    IsNoticeDay = False  # 是否为指定发布值班安排日
    if skip:
        if spubNotice == 0:
            if is_workday(today) and str(today.weekday()) not in selfholidays:

                bfToday = today
                while bfToday.weekday() != 0:
                    bfToday -= day1
                    if is_workday(bfToday) and str(bfToday.weekday()) not in selfholidays:
                        IsNoticeDay = False
                        break
                else:
                    IsNoticeDay = True
        else:
            if today.weekday() == spubNotice - 1:
                IsNoticeDay = True
    else:
        if today.weekday() == spubNotice - 1:
            IsNoticeDay = True
    return IsNoticeDay


def PadTmpTab(dutyName, dutyMap, flag=True):
    """填充临时表
    @param flag: 是否保留旧有临时值班表条目
    @param dutyName: 值班名
    @param dutyMap: 值班主表数据字典
    @return: 错误报告列表 没有则为空
    """

    selfHolidayList = dutyMap['SELF_HOLIDAY'].split(',')
    # 将数据库中的人员条目 姓名和工号分开
    dutyPpList: List[str] = list(map(lambda itera: itera.split('_')[1], dutyMap['DUTY_PEOPLE'].split(',')))

    endDay, sNowDayLastPeople, DaysDutyMap = buildSCH.BuildDutyWeeks(dutyPpList, dutyMap['PEOPLE_COUNT'],
                                                                     dutyMap['DUTY_NOW'],
                                                                     today, dutyMap['AUTO_SKIP'], selfHolidayList)

    IsSecu, sData = dingInfoTab.GetDingInfoTab(DaysDutyMap)
    if not IsSecu:
        return [(sData, [])]
    dutyKeys, dutyValues, NoDDidList = sData
    _, DingDutyMap = proDing.BuildDingPersonInfo(dutyKeys, dutyValues, NoDDidList)

    logging.info(f'成功生成 {today} 至 {endDay} 的值班安排表\n' + str(DaysDutyMap))

    sDingDutyMap = initF.dictDate2Str(DingDutyMap)

    logging.info(f'成功生成 {today} 至 {endDay} 的钉钉值班安排表\n' + str(sDingDutyMap))

    if flag:
        IsSecu, TmpDutyMapGet = tmpDutyTab.GetTmpDutyTab(dutyName)
        if not IsSecu:
            TmpDutyMapGet = {}
        # 保留今日及之后的旧临时值班表条目
        sDingDutyMap.update({key: value for key, value in TmpDutyMapGet.items() if key >= initF.strftime(today)})

    IsSecu, sMsg = onDutyTab.SetDutyNow(dutyName, sNowDayLastPeople)
    if not IsSecu:
        return [(sMsg, [])]
    IsSecu, sMsg = tmpDutyTab.SetTmpDutyTab(sDingDutyMap, dutyName)
    if not IsSecu:
        return [(sMsg, [])]
    return []


# def updateDutyNow(dutyName, sDutyNow):
#     IsSecu, sMsg = onDutyTab.SetDutyNow(dutyName, sDutyNow)
#     if IsSecu:
#         return True, {'DUTY_NOW': sDutyNow}
#     else:
#         return False, sMsg


# def loopsDutyNow(dutyName, dutyMap):
#     """更新today的dutynow
#
#     """
#     dutyPpList = dutyMap['DUTY_PEOPLE'].split(',')
#     dutyPpCnt = dutyMap['PEOPLE_COUNT']
#     lenPp = len(dutyPpList)
#     dutyNow: str = dutyMap['DUTY_NOW']
#     dutyDate: str = dutyMap['DUTY_DATE']
#     dutyNow = initF.calDutyNow(dutyDate, today, day1, dutyPpList, dutyNow, dutyPpCnt, lenPp)
#     reSetDuty = onDutyTab.SetOnDutyTab(dutyName, dutyNow, initF.strftime(today))
#     if not reSetDuty[0]:
#         return False, reSetDuty[1]
#     else:
#         return True, {'DUTY_NOW': dutyNow, 'DUTY_DATE': dutyDate}


def buildMsgDaysTmp(endDay, TmpDutyMapGet, dutyMsg=''):
    """根据endDay获取临时值班表安排的通知内容
    ：endDay：结束日期的星期号
    会获取[today,endDay]闭区间的值班安排"""
    DingDutyMap = {}
    DDidList = []
    dateDay = today
    while True:
        sDate = initF.strftime(dateDay)
        if sDate in TmpDutyMapGet:
            DingDutyMap[sDate] = TmpDutyMapGet[sDate]
            # if is_workday(dateDay):
            DDidList += TmpDutyMapGet[sDate]
        else:
            DingDutyMap[sDate] = 'NULL'
        if dateDay.weekday() == endDay:
            break
        dateDay += day1
    DDidList = list(set(DDidList))
    if initF.noneDuty in DDidList:
        DDidList.remove(initF.noneDuty)
    sMsgContent = proDing.BuildDingContent(DingDutyMap, dutyMsg)
    return sMsgContent, DDidList


def timeTask(dutyName: str):
    returnTask = []
    reGetDuty = onDutyTab.GetOnDutyTab(dutyName)
    if not reGetDuty[0]:
        return [(reGetDuty[1], [])]
    dutyMap = reGetDuty[1]
    spubNotice = dutyMap['PUB_NOTICE_WEEK']
    sautoSkip = dutyMap['AUTO_SKIP']
    selfHolidayList = dutyMap['SELF_HOLIDAY'].split(',')

    # dutyPpList: List[str] = list(map(lambda itera: itera.split('_')[1], dutyMap['DUTY_PEOPLE'].split(',')))
    # dutyPpCnt: int = dutyMap['PEOPLE_COUNT']
    # dutyNow: str = dutyMap['DUTY_NOW']

    # 填充临时值班表日 此时dutynow没有更新为今日
    # if today.weekday() == initF.dutyNowDay:
    #     # 从今日开始重新填充，并且保留今天的值班安排
    #     returnList = PadTmpTab(dutyName, dutyMap)
    #     returnTask += returnList

    # 获取临时值班表
    IsSecu, sData = tmpDutyTab.GetTmpDutyTab(dutyName)
    # 若没有值班计划则生成 or 是填充临时值班表日
    if not IsSecu or today.weekday() == initF.dutyNowDay:
        returnList = PadTmpTab(dutyName, dutyMap)
        returnTask += returnList
        TmpDutyMapGet = tmpDutyTab.GetTmpDutyTab(dutyName)[1]
    else:
        TmpDutyMapGet = sData

    # # 更新dutynow，dutydate。dutydate同步为今日，dutynow同步为（含今天）最后一个值班人员
    # reLoopsNow = loopsDutyNow(dutyName, dutyMap)
    # if not reLoopsNow[0]:
    #     returnTask.append((reLoopsNow[1], []))

    if ChargeNoticeDay(spubNotice, sautoSkip, selfHolidayList):
        # endDay = initF.calEndDay(spubNotice)
        endDay = initF.endWeekDay
        returnTask.append(buildMsgDaysTmp(endDay, TmpDutyMapGet))
    # if is_workday(today) and today.weekday() not in [5, 6]:
    # 今日是否通知 如果今日的临时表查询结果不为[initF.noneDuty]
    if TmpDutyMapGet[initF.strftime(today)][0] != initF.noneDuty:
        endDay = today.weekday()
        returnTask.append(buildMsgDaysTmp(endDay, TmpDutyMapGet, dutyMap['DUTY_MSG']))

    return returnTask


if __name__ == 'timeTask.DutyInform.timeTaskFcn':
    import timeTask.DutyInform.DingPerInfoTab as dingInfoTab
    import timeTask.DutyInform.DCOnDutyTab as onDutyTab
    import timeTask.DutyInform.DCTmpDutyTab as tmpDutyTab
    import timeTask.DutyInform.BuildDutySCH as buildSCH
    import timeTask.DutyInform.ProcessDingTalk as proDing
    import timeTask.DutyInform.initFcn as initF
else:
    import DingPerInfoTab as dingInfoTab
    import DCOnDutyTab as onDutyTab
    import DCTmpDutyTab as tmpDutyTab
    import BuildDutySCH as buildSCH
    import ProcessDingTalk as proDing
    import initFcn as initF
