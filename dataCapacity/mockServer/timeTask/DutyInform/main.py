import sys
import logging

from timeTaskFcn import timeTask
from ProcessDingTalk import SendDingTalkMsg
from LogitSetting import logit
import initFcn as initF


@logit('dutyInfo.log')
def Duty(dutyName: str, sGroupName: str):
    logging.info(f'DUTYNAME={dutyName} 运行开始-----------------------------')
    reInit = initF.init()
    if not reInit[0]:
        return [(reInit[1], [])]
    returnList = timeTask(dutyName)
    initF.CloseDB()
    for msg in returnList:
        SendDingTalkMsg(sGroupName, msg[0], msg[1])
    logging.info(f'DUTYNAME={dutyName} 运行结束-----------------------------\n\n')


if __name__ == '__main__':
    try:
        dutyName_, sGroupName_ = sys.argv[1:3]
    except ValueError as e:
        Duty('Test', '测试')
    else:
        Duty(dutyName_, sGroupName_)
