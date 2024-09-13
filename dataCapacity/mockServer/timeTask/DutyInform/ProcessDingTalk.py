# 本模块处理发送钉钉消息流程
import json
import logging
import requests


def BuildDingPersonInfo(dutyKeys, dutyValues, NoDDidList):
    """获取员工钉钉信息，将员工号 转为 钉钉号
    @param dutyKeys: iterObject [Date,...] 日期的可迭代对象
    @param dutyValues: [[no,...],...] 员工号列表组成的列表
    @param NoDDidList: [{OPERATOR_NO:1191180128,DDID:ddid128},...] 由相关数据库返回的 字段：数据 组成的字典 作为子项的列表
    @return: 所需钉钉号列表 , 日期:值班员工列表 组成的字典 {Date:[ddid,...],...}
    """

    def getNoDDidFcn(dict_):  # List[Mapping]
        ddid = dict_['DDID']
        no = dict_['OPERATOR_NO']
        if ddid is None:  # 若员工没有ddid则ddid为None，转为no员工号防止无法写入数据库
            ddid = dict_['OPERATOR_NO']
        return no, ddid

    def No2DDidFcn(Nolist):
        return [No2DDidMap[no] if no in No2DDidMap else no for no in Nolist]

    # map:[('1191180128','ddid128'),...]
    # No2DDidMap:{'1191180128':'ddid128',...}
    No2DDidMap = dict(map(getNoDDidFcn, NoDDidList))
    return list(No2DDidMap.values()), dict(
        zip(dutyKeys, map(No2DDidFcn, dutyValues)))


def BuildDingContent(DingDutyMap, dutyMsg=''):
    """本函数负责制作钉钉消息json串
    :DingDutyMap: '钉钉表转换后的值班信息字典' {日期:[...]...}
    :dutyMsg: '日常通知字符串' 默认为空，表示周通知
    :return: 钉钉消息Json串['dingTalkMessage']['content']部分
    """
    # 消息头
    sMsgContent = initF.sMsgHeadMap.get(dutyMsg, initF.sMsgHeadMap['default'])
    if dutyMsg is None:
        dutyMsg = ''
    bLenJust = False
    for key, val in DingDutyMap.items():
        # val只有一个元素，且为 initF.noneDuty，则为不需要值班 不添加到值班安排消息中。但是临时值班表表里有。
        if val[0] == initF.noneDuty:
            continue
        sMsgTmp = f'''\n{key}: {dutyMsg} @{',@'.join(val)} '''
        # 补全消息头使消息宽度一致
        if not bLenJust:
            bLenJust = True
            sMsgContent = sMsgContent.center(len(sMsgTmp) - 1, '-')
        sMsgContent += sMsgTmp
    return sMsgContent


def SendDingTalkMsg(sGroupname, sMsgContent, DDidList):
    initF.MsgMap['groupName'] = sGroupname
    initF.MsgMap['msgBody']['content'] = sMsgContent
    initF.MsgMap['at']['atMobiles'] = DDidList
    MsgJson = json.dumps(initF.MsgMap)
    logging.info(MsgJson)
    response = requests.post(url=initF.DingTalkUrl,
                             headers=initF.headers,
                             data=MsgJson)
    if response.ok:
        logging.info('成功发送消息 ' + '\n' + sMsgContent)
    else:
        logging.warning('消息发送失败：' + str(response.status_code) + '\n' +
                        sMsgContent)


if __name__ == "timeTask.DutyInform.ProcessDingTalk":
    import timeTask.DutyInform.initFcn as initF
else:
    import initFcn as initF
