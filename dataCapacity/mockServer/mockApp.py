# 服务转发和处理
import json
import urllib.parse
import timeTask.timeTaskInteface as timeTaskInteface
import sysInteface.DCIndex as DCIndex
import AuthToken.TokenFunc as tokenFunc


def getFromData(sByte: bytes):
    sBodyStr = str(sByte, encoding='utf-8')
    sKey = ''
    sValue = ''
    isValueBegin = False
    resultMap = {}
    for sLine in sBodyStr.splitlines():
        if '; name="' in sLine:
            sKey = sLine[sLine.find('; name="') + len('; name="'):]
            sKey = sKey[:sKey.find('"')]
            isValueBegin = True
        elif '----------------------------' in sLine or '------' in sLine:
            isValueBegin = False
            if sKey != '':
                resultMap[sKey] = sValue
            sValue = ''
        elif isValueBegin:
            sValue = sValue + sLine
    if len(resultMap) == 0 and len(sBodyStr) > 0:
        resultMap = json.loads(sBodyStr)
    return resultMap


# 获取param
def GetParams(sQueryString: str):
    if sQueryString == '':
        return {}
    sQueryString = urllib.parse.unquote(sQueryString)
    yzItem = {}
    for sQuery in sQueryString.split('&'):
        itemList = sQuery.split('=')
        sKey = itemList[0]
        sValue = itemList[1]
        yzItem[sKey] = sValue
    return yzItem


# 监听方法
def application(environ, start_response):
    # response_headers = [
    #     ('Access-Control-Allow-Origin', '*'),
    # ]
    # start_response("200 OK", response_headers)
    # start_response('200 OK', [('Content-Type', 'application/json'),
    #                           ('Access-Control-Allow-Origin', '*')])
    payload = {}
    sRequestMethod = environ['REQUEST_METHOD']
    if sRequestMethod == 'OPTIONS':
        start_response('200 OK', [('Content-Type', 'application/json'),
                                  ('Access-Control-Allow-Origin', '*'),
                                  ('Access-Control-Allow-Methods', 'GET,POST,OPTIONS'),
                                  ('Access-Control-Max-Age', '3600'),
                                  ('Access-Control-Allow-Headers', 'token,content-Type')])
        return json.dumps({'success': True, 'msg': ''})

    sPathInfo = environ['PATH_INFO']
    if sPathInfo != '/DCLogin':
        try:
            token = environ['HTTP_TOKEN']
        except KeyError as e:
            return DCIndex.LoginFail(e, start_response)
        _payload, msg = tokenFunc.validate_token(token)
        if msg:  # 验证未通过
            return DCIndex.LoginFail(msg, start_response)
        else:
            payload = _payload
    # sFunction = sPathInfo.replace('/g/', '')
    sFunction = sPathInfo
    sQueryString = environ['QUERY_STRING']
    # 获取param入参
    paramMap = GetParams(sQueryString)

    # 获取form_data入参
    try:
        request_body_size = int(environ.get('CONTENT_LENGTH', 0))
    except ValueError:
        request_body_size = 0

    request_body = environ['wsgi.input'].read(request_body_size)
    formDataMap = getFromData(request_body)
    if sPathInfo != '/DCLogin':
        formDataMap['authID'] = payload.get('authID', '')
        paramMap['authID'] = payload.get('authID', '')
        formDataMap['userID'] = payload.get('userID', '')
        paramMap['userID'] = payload.get('userID', '')

    if timeTaskInteface.isTimeTask(sPathInfo):  # 判断是否是定时任务接口
        return timeTaskInteface.login(formDataMap, paramMap, start_response,
                                      sPathInfo)
    else:  # 没有支持的功能返回信息出去
        return DCIndex.systemInteface(formDataMap, paramMap, start_response,
                                      sFunction)
