# 本模块负责初始化
#   和
# 配置公用数据
import logging
from datetime import datetime, date

from chinese_calendar import is_workday
from cx_Oracle import DatabaseError
from typing import Tuple, Union, Sequence, Mapping, Dict

# padDays = 10  # 临时表中至少保存10个工作日
dutyNowDay = 0  # 每周一重新填充临时表
endWeekDay = 6  # 每周结束日为周日
weeks = 2  # 生成两周值班安排
noneDuty = 'None Duty'
Num2WeekDayMap = {0: '一', 1: '二', 2: '三', 3: '四', 4: '五', 5: '六', 6: '日'}
sMsgHeadMap = {'': '本周值班安排', 'default': '今日值班安排'}
DingTalkUrl = ' http://localhost:7783/dingSend/sendDingTalk'
headers = {"Content-Type": "application/json ;charset=utf-8 "}
MsgMap = {
    "groupName": "系统",
    "msgType": "text",
    "at": {
        "atMobiles": [

        ],
        "atUserIds": [

        ],
        "isAtAll": False
    },
    "msgBody": {
        "content": ""
    }
}


def init():
    try:
        Oracle.GetDB()
    except DatabaseError as msg:
        logging.warning('数据库连接失败' + str(msg))
        return False, '数据库连接失败！请检查日志'
    else:
        return True, ''


def CloseDB():
    Oracle.CloseDB()


def RawOracleData2MapList(
        rawData: Tuple[Union[Sequence, Sequence[Tuple]], Sequence[Tuple]], toLower=False
) -> Tuple[bool, Union[Sequence[Mapping], str]]:
    """将select * 的Oracle原始返回数据转换成[{},{},...]的形式
      ：MapList：  由相关数据库返回的 字段：数据 组成的字典 作为子项的列表
    """
    if rawData[0]:
        MapList = []
        KeyList = [x[0] if not toLower else x[0].lower() for x in rawData[1]]
        for value in rawData[0]:
            MapList.append(dict(zip(KeyList, value)))
        return True, MapList
    else:
        return False, ''


def strftime(dateKey: date):
    return dateKey.strftime('%Y-%m-%d')


def strptime(strKey: str):
    return datetime.strptime(strKey, '%Y-%m-%d').date()


def calEndDay(spubNotice):
    """根据表中spub计算值班发布计划的结束日期（闭区间）
    ：spubNotice范围：1-7
    ：return ：星期号0-6
    """
    if spubNotice == 1:
        endDay = 6  # 周日
    else:
        endDay = spubNotice - 2  # spub 从1开始 weekday()从0开始
    return endDay


def calDutyNow(dutyDate, maxDate, day1, dutyPpList, dutyNow, dutyPpCnt, lenPp):
    """返回 maxDate当天的dutynow

    """
    if dutyDate == ' ':
        dutyDate: date = maxDate - day1
    else:
        dutyDate: date = strptime(dutyDate)
    gapDays = 0
    while dutyDate != maxDate:
        dutyDate += day1
        if is_workday(dutyDate) and dutyDate.weekday() not in [5, 6]:
            gapDays += 1
    try:
        index = dutyPpList.index(dutyNow)
    except ValueError:
        index = -1
    dutyNow = dutyPpList[(index + dutyPpCnt * gapDays) % lenPp]
    return dutyNow


def dictDate2Str(DingDutyMap: Mapping) -> Dict:
    """将字典中键值 从 Date类型转为Str类型

    """
    return dict(zip(map(strftime, DingDutyMap.keys()), DingDutyMap.values()))


if __name__ == "timeTask.DutyInform.initFcn":
    import timeTask.DutyInform.OracleDB as Oracle
else:
    import OracleDB as Oracle
