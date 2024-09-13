# 本模块处理DCONDUTY表
import logging
from typing import Tuple, Union, Dict


def GetOnDutyTab(dutyName: str) -> Tuple[bool, Union[Dict, str]]:
    """获取DCONDUTY 表中匹配的条目
    :dutyName 和表中DUTY_NAME字段进行匹配
    :result[1][0]:[{'DUTY_NAME': 'BOP值班助手', 'GROUP_NAME': '值班助手',...}]
    作为任务开始，返回匹配条目，后续函数皆操作该条目"""
    OraSelectOnDutyTable = f'''select * from DCONDUTY a where a.DUTY_NAME='{dutyName}' '''
    rawData = Oracle.SelectDB(OraSelectOnDutyTable)
    result = initF.RawOracleData2MapList(rawData)
    if result[0]:
        return True, result[1][0]
    else:
        logging.warning('DCONDUTY表没有该duty_name：' + dutyName)
        return False, 'DCONDUTY表 查询失败！请检查日志'


# def SetOnDutyTab(dutyName, dutyNow: str, dutyDate: str):
#     """将新的dutyNow和dutydate写入到表中"""
#     OraUpDutyNow = f'''update DCONDUTY a set a.DUTY_NOW='{dutyNow}',a.DUTY_DATE='{dutyDate}' where a.DUTY_NAME='{dutyName}' '''
#     reExec = Oracle.ExecDB(OraUpDutyNow)
#     if reExec[0]:
#         logging.info('DCONDUTY表DUTY_NOW,DUTY_DATE字段 成功更新为：' + dutyNow + ' ' + dutyDate)
#         return True, ''
#     else:
#         logging.warning('DCONDUTY表DUTY_NOW,DUTY_DATE字段 更新失败：' + reExec[1])
#         return False, 'DCONDUTY表 更新失败！请检查日志'


def SetDutyNow(dutyName, dutyNow: str):
    """将新的dutyNow写入到表中
    """
    OraUpDutyNow = f'''update DCONDUTY a set a.DUTY_NOW='{dutyNow}' where a.DUTY_NAME='{dutyName}' '''
    IsSecu, sMsg = Oracle.ExecDB(OraUpDutyNow)
    if IsSecu:
        logging.info('DCONDUTY表DUTY_NOW字段 成功更新为：' + dutyNow)
        return True, ''
    else:
        logging.warning('DCONDUTY表DUTY_NOW字段 更新失败：' + sMsg)
        return False, 'DCONDUTY表 更新失败！请检查日志'


if __name__ == "DCOnDutyTab":
    import OracleDB as Oracle
    import initFcn as initF
else:
    import timeTask.DutyInform.OracleDB as Oracle
    import timeTask.DutyInform.initFcn as initF
