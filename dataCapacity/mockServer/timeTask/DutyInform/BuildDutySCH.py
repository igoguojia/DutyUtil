# 本模块负责生成值班安排表并返回 {星期/日期:员工号列表} 字典
import logging
from datetime import date, timedelta
from chinese_calendar import is_holiday
from typing import Tuple, Dict, List


def BuildTodayDuty(weekDutyMapGet, today: date):
    """ 获取今天的值班安排列表
    :return:因为Tmp表中存放的是钉钉号，所以返回星期号：钉钉号字典
    """
    try:
        dutyList = weekDutyMapGet[initF.strftime(today)]
    except KeyError:
        logging.warning(f'表中无 {today} 值班安排')
        return False, '无今天值班安排！请检查日志！'
    logging.info(f'成功获取 {today} 值班安排 {dutyList}')
    return True, {today: dutyList}


# def BuildDaysDuty(dutyPpList, dutyPpCnt: int,
#                   dutyNow, today: date,
#                   flag: int = 1) -> Tuple[date, Dict]:
#     """ 获取从today开始的复数天的值班安排列表
#     today对应dutynow后面的人
#     :dutyPpList: DUTY_PEOPLE生成的列表
#     :dutyPpCnt: PEOPLE_COUNT
#     :dutyNow: DUTY_NOW
#     :today:起始日期
#     :flag:1，2，3表示生成对应轮的值班表（出现0的情况则返回空值）
#     :return[DaysDutyMap]: {日期：员工号列表,...}
#     """
#     day1 = timedelta(days=1)
#     dateDay = today
#     lenPp = len(dutyPpList)
#     try:
#         indexNow = dutyPpList.index(dutyNow)
#     except ValueError:
#         indexNow = -1
#     index = indexNow
#     DaysDutyMap = {}
#     loops = 0
#     while True:
#         try:
#             bWorkDay = is_workday(dateDay)
#         except NotImplementedError:
#             dateDay -= day1
#             break
#         if bWorkDay and dateDay.weekday() not in [5, 6]:
#             dutyDate = []
#             for i in range(dutyPpCnt):
#                 index += 1
#                 dutyDatePp = dutyPpList[index % lenPp]
#                 if (index - indexNow) % lenPp == 0:
#                     loops += 1
#                 dutyDate.append(dutyDatePp)
#             DaysDutyMap[dateDay] = dutyDate
#         else:
#             DaysDutyMap[dateDay] = ['None Duty']
#         if loops == flag:
#             break
#         dateDay += day1
#     return dateDay, DaysDutyMap


def BuildDutyWeeks(dutyPpList: List[str], dutyPpCnt: int,
                   dutyNow: str, today: date,
                   skip: int, selfholidays: List[str],
                   ) -> Tuple[date, str, Dict[date, List[str]]]:
    """ 获取从today开始的weeks周的值班安排列表
    @param dutyPpList:DUTY_PEOPLE生成的列表
    @param dutyPpCnt:PEOPLE_COUNT
    @param dutyNow:DUTY_NOW
    @param today:起始日期
    @param skip:是否智能跳过节假日
    @param selfholidays: 自定义节假日 固定跳过不受skip影响
    @return: [0]:值班安排表日期 [1]:下次重新填充表日 前一天最后一位值班人员 [2]: {日期：员工号列表,...}
    """
    weeks = initF.weeks  # 生成值班表周数 计算本周，表尾为周结束日
    day1 = timedelta(days=1)
    dateDay = today
    lenPp = len(dutyPpList)
    # 查看是否为新建值班表
    try:
        index = dutyPpList.index(dutyNow)
    except ValueError:
        index = -1
    # index = indexNow
    DaysDutyMap = {}
    loops = 0
    sNowDayLastPeople: str = ''

    def addIndex():
        return index + 1

    while loops < weeks:
        DaysDutyMap[dateDay] = [initF.noneDuty]
        # 不开启智能节假日 节假日只有 自定义节假日 开启之后节假日 是法定节假日和自定义节假日
        # 只有不在自定义节假日并且 在跳过节假日时工作日 或是不跳过节假日
        # 只有在自定义节假日或是 开启智能跳过节假日时的法定节假日 才能不值班
        if not (str(dateDay.weekday()) in selfholidays or (skip and is_holiday(dateDay))):
            dutyDate = [dutyPpList[addIndex() % lenPp] for i in range(dutyPpCnt)]
            DaysDutyMap[dateDay] = dutyDate
        if dateDay.weekday() == initF.endWeekDay:
            loops += 1
            # 获取下次重新填充表日 前一天最后一位值班人员
        if (dateDay + day1).weekday() == initF.endWeekDay and loops == 1:
            sNowDayLastPeople = DaysDutyMap[dateDay][dutyPpCnt - 1]
        dateDay += day1
    return dateDay - day1, sNowDayLastPeople, DaysDutyMap


if __name__ == 'timeTask.DutyInform.BuildDutySCH':
    import timeTask.DutyInform.initFcn as initF
else:
    import initFcn as initF
