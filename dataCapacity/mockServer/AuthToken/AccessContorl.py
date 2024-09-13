# 接口权限控制
import json
import logging
from functools import wraps
from LogitSetting import logit

# 初期设定，后期可修改
lenGroup = 3  # 组长度
lenAuth = 5  # IO权限长度

interfaceAuthID = {
    'GetAllOnDuty': {'auth': '00011', 'authChara': ['00100', '00200']},
    'GetOnDuty': {'auth': '00001', 'authChara': ['00100', '00200']},
    'GetOnDutyByName': {'auth': '00001', 'authChara': ['00100', '00200']},
    'GetOnDutyByGroup': {'auth': '00001', 'authChara': ['00100', '00200']},
    'AddOnDuty': {'auth': '00101', 'authChara': ['00100', '00200']},
    'ModOnDuty': {'auth': '01001', 'authChara': ['00100', '00200']},
    'DelOnDuty': {'auth': '10001', 'authChara': ['00100', '00200']},
    'GetDingPersonInfo': {'auth': '00011', 'authChara': ['00100', '00200']},
    'GetDingTalkInfo': {'auth': '00011', 'authChara': ['00100', '00200']},

    # 临时表操作
    # 'DelDuty': {'auth': '10001', 'authChara': ['00100', '00200']},
    # 'ResetDuty': {'auth': '11001', 'authChara': ['00100', '00200']},
    'TmpDutyUp': {'auth': '01001', 'authChara': ['00100', '00200']},
    'TmpDutyQury': {'auth': '00001', 'authChara': ['00100', '00200']},
    # 'TmpOneDayDutyQury': {'auth': '00001', 'authChara': ['00100', '00200']}

    # 服务公告
    'ServerAdd': {'auth': '00101', 'authChara': ['00100']},
    'ServerMod': {'auth': '01001', 'authChara': ['00100']},
    'ServerDel': {'auth': '10001', 'authChara': ['00100']},
    'ServerQuery': {'auth': '00001', 'authChara': ['00100']},

}


@logit('accessControl.log')
def AcsControl(func):
    """权限控制
    权限ID ：AUTHORITY_ID 总长10 IO权限+待定位+角色ID
    :auth:str 调用接口所需要的IO权限 【0：0：0：0：0  删除：修改：创建：群体：读取】比如10001 需要【删除】权限
            因为读取是默认权限所以如果不包含读取权限则报警异常权限
    :authChara: List[str] 调用接口所需要的组别ID 列表 待定位+组别【00 200】
    :formDataMap: 调用接口所需的入参，包含token解析出来的负载
    """

    @wraps(func)
    # def decorated(start_response, **kwargs):
    def decorated(reqDataMap, start_response):
        auth = interfaceAuthID[func.__name__]['auth']
        authChara = interfaceAuthID[func.__name__]['authChara']
        # reqDataMap = args[0]
        # start_response = args[1]
        # if 'formDataMap' in kwargs:
        #     reqDataMap = kwargs['formDataMap']
        # elif 'paramMap' in kwargs:
        #     reqDataMap = kwargs['paramMap']
        # else:
        #     return json.dumps({'success': 'false', 'msg': '权限控制器传参错误！'})
        authIDList: list = reqDataMap['authID'].split()
        for authId in authIDList:
            if authId[lenAuth - 1] != '1':
                logging.info(f'''用户 {reqDataMap['userID']} 操作 {func.__name__} 接口失败 异常权限''')
                start_response('200 OK', [('Content-Type', 'application/json'),
                                          ('Access-Control-Allow-Origin', '*')])
                return json.dumps({'success': 'false', 'msg': '异常权限！'})
            if authId[lenAuth:] in authChara:
                for io, io2 in zip(authId[:lenAuth], auth):
                    if io < io2:
                        # logging.info(f'''用户 {reqDataMap['userID']} 操作 {func.__name__} 接口失败 权限未通过''')
                        # return json.dumps({'success': 'false', 'msg': '权限不足！'})
                        break
                else:
                    break
        else:
            logging.info(f'''用户 {reqDataMap['userID']} 操作 {func.__name__} 接口失败 权限未通过''')
            start_response('200 OK', [('Content-Type', 'application/json'),
                                      ('Access-Control-Allow-Origin', '*')])
            return json.dumps({'success': 'false', 'msg': '权限不足！'})
        # f = func(reqDataMap, start_response=start_response)
        f = func(reqDataMap, start_response)
        return f

    return decorated
