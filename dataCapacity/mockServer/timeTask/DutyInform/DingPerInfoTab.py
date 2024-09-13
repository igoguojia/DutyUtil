import logging
from datetime import date
from typing import Mapping, Tuple, Any, List, Union, KeysView, Sequence


def GetDingInfoTab(daysDutyMap: Mapping) -> Tuple[
    bool,
    Union[Tuple[List[date], List[List[str]], Union[Sequence[Mapping], str]], str]
]:
    """从dingpersoninfo表查询所需的员工号对应的钉钉号
    :dutyValues:[[no,...],...] 员工号列表组成的列表
    :dutyKeys: [Date,...] 日期的列表
    @param daysDutyMap:{Date:[no,...],...}日期和员工号列表 组成的字典
    @return: [1]:所有查询到的钉钉号字典列表[{},{},...} 子项中是dingpersoninfo表每条数据对应结构
    """
    dutyValues: List[List[str]] = list(daysDutyMap.values())
    dutyKeys: List[date] = list(daysDutyMap.keys())
    speople_no = str(set(sum(dutyValues, [])))[1:-1]
    rawData = Oracle.SelectDB(
        f'''select * from jn_asset.dingpersoninfo a where a.operator_no in ({speople_no})'''
    )
    IsSecu, sData = initF.RawOracleData2MapList(rawData)
    if IsSecu:
        return True, (dutyKeys, dutyValues, sData)
    else:
        logging.warning('jn_asset.dingpersoninfo表没有员工号：' + speople_no)
        return False, 'dingpersoninfo表查询失败!请检查日志'


if __name__ == "timeTask.DutyInform.DingPerInfoTab":
    import timeTask.DutyInform.OracleDB as Oracle
    import timeTask.DutyInform.initFcn as initF
else:
    import OracleDB as Oracle
    import initFcn as initF
