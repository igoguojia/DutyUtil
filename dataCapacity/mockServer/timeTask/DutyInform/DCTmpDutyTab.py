# 本模块处理DCTMPONDUTY 表相关操作
# duty_name Bop值班
# duty_people  18888... DingTalkId 为字符串
# duty_date 0-6 date.weekday()
import logging
from typing import Mapping, Tuple, Union, Dict


def SetTmpDutyTab(sDingDutyMap: Dict, dutyName):
    """插入数据到临时值班表
    :sDingDutyMap  键为str类型的  {日期：[DDid,...],...}
    应该仅用于「填充临时值班表函数 PadTmpTab」
    """
    OraTruncate = f'''delete from jn_asset.DCTMPONDUTY a where a.DUTY_NAME='{dutyName}' '''
    Oracle.ExecDB(OraTruncate)
    OraInsert = 'insert into jn_asset.DCTMPONDUTY(duty_name,duty_people,duty_date) values (:duty_name,:duty_people,:duty_date)'
    OraInsertParam = []
    for key, val in sDingDutyMap.items():
        for dingid in val:
            OraInsertParam.append({
                'duty_name': dutyName,
                'duty_people': dingid,
                'duty_date': key
            })
    resExParam = Oracle.ExecParamDB(OraInsert, OraInsertParam)
    if resExParam[0]:
        logging.info('值班表插入DCTMPONDUTY成功' + '\n' + str(sDingDutyMap))
        return True, ''
    else:
        logging.warning('值班表插入失败' + str(resExParam))
        return False, '值班表插入DCTMPONDUTY失败！请检查日志'


def GetTmpDutyTab(dutyName) -> Tuple[bool, Union[str, Dict]]:
    """从临时值班表获取DUTY_NAME=dutyName的所有值班安排
    :return[1]:{日期:[DDid,...],...} or Str
    会将同一天的合并在一起组成列表"""
    OracSelect = f'''select * from jn_asset.DCTMPONDUTY a where a.DUTY_NAME='{dutyName}' '''
    rawData = Oracle.SelectDB(OracSelect)
    IsSecu, sData = initF.RawOracleData2MapList(rawData)
    if IsSecu:
        todayDutyList = sData  # [{DUTY_PEOPLE:DingId,DUTY_DATE:0},]
    else:
        logging.warning(f'jn_asset.DCTMPONDUTY表没有该记录：DUTY_NAME={dutyName}')
        return False, 'DCTMPONDUTY表查询失败!请检查日志'

    def getDateList(dict_):  # List[Mapping]
        return dict_['DUTY_DATE']

    # 组装临时值班表 将多条 当天：一个人 组装成 当天：列表
    weekDutyMapGet = dict([(key, []) for key in map(getDateList, todayDutyList)])
    for dutylist in todayDutyList:
        weekDutyMapGet[dutylist['DUTY_DATE']].append(dutylist['DUTY_PEOPLE'])

    return True, weekDutyMapGet


if __name__ == "timeTask.DutyInform.DCTmpDutyTab":
    import timeTask.DutyInform.OracleDB as Oracle
    import timeTask.DutyInform.initFcn as initF
else:
    import OracleDB as Oracle
    import initFcn as initF
