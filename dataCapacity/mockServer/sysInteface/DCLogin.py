# 本文件用来负责登陆功能
import sysInteface.OracleDB as OracleDB
import sysInteface.sqlDemoList as sqlDemoList
import sysInteface.tools as tools
import AuthToken.TokenFunc as tokenFunc
# import OracleDB
# import sqlDemoList
# import tools

import json
import hashlib
import re


# 根据用户名获取权限
def getUserAuth(sUserId: str):
    sUserAuthList = []
    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcUserAuthQrySql % {'userID': sUserId})
    OracleDB.CloseDB()
    one = tools.FormatDBToList(one, title, True)
    for item in one:
        if item['authority_id'] not in sUserAuthList:
            sUserAuthList.append(item['authority_id'])
    return sUserAuthList


# 根据父节点找到对应的子信息
# def LoadChildMenu(sParentID: str, sParentName: str, one):
#     resultListItem = []
#     for item in one:
#         if item['PARENT_ID'] == sParentID:
#             sSerName = item['SER_NAME']
#             sSerID = item['SER_ID']
#             resultMapItem = {}
#             yzList = LoadChildMenu(sSerID, sSerName, one)
#             sUrl = ''
#             if '离线工具' in sDcMenuMap:
#                 sUrl = sDcMenuMap['离线工具']
#             resultMapItem = {'name': sSerName, 'url': sUrl, 'children': yzList}
#             resultListItem.append(resultMapItem)
#
#     return resultListItem


# def GetSvrMangeMenu(sMenuList: list):
#     yzSerMangeList = []
#     for sName in sMenuList:
#         yzItem = {}
#         yzItem['name'] = sName
#         sUrl = ''
#         if sName in sDcMenuMap:
#             sUrl = sDcMenuMap[sName]
#         yzItem['url'] = sUrl
#         yzItem['children'] = []
#         if sUrl.strip() == '':
#             continue
#         yzSerMangeList.append(yzItem)
#     return yzSerMangeList


# 加载菜单信息
# def LoadMenu(sUserAuthList, sTeamname):
#     global uf20Menu, uf30Menu, sDcMenuMap
#     sDcMenuMap = {}
#     OracleDB.GetDB()
#     one, title = OracleDB.SelectDB(sqlDemoList.sDcSeriesInfoSearchSql)
#     menuOne, sMenuTitle = OracleDB.SelectDB(sqlDemoList.sDcMenuSearchSql)
#     OracleDB.CloseDB()
#     one = tools.FormatDBToList(one, title)
#     menuOne = tools.FormatDBToList(menuOne, sMenuTitle, True)
#     for item in menuOne:
#         sMenuCaption = item['menu_caption']
#         sMenuUrl = item['menu_url']
#         sDcMenuMap[sMenuCaption] = sMenuUrl
#
#     iUF30 = -1
#     iUF20 = -1
#
#     UF20OnLineBaseMenuList = ['bop前端在线开发工具', 'blobFile大字段解析']
#
#     if '10002001' in sUserAuthList or '数据能力' == sTeamname:
#         UF20OnLineBaseMenuList.append('钉钉值班管理')
#     if '10002002' in sUserAuthList or '数据能力' == sTeamname:
#         UF20OnLineBaseMenuList.append('账户挑包工具')
#
#     UF20OnLine = {
#         'name': '在线工具',
#         'url': '',
#         'children': GetSvrMangeMenu(UF20OnLineBaseMenuList)
#     }
#
#     UF30OnLineBaseMenuList = ['T3功能流程测试']
#     UF30OnLine = {
#         'name': '在线工具',
#         'url': '',
#         'children': GetSvrMangeMenu(UF30OnLineBaseMenuList)
#     }
#
#     sFloorTmpList = []
#     sSerIDList = []
#     for item in one:
#         sSerID = item['SER_ID']
#         sParentID = item['PARENT_ID']
#         if item['SER_NAME'] == 'UF30':
#             iUF30 = sSerID
#             uf30Menu = []
#         elif item['SER_NAME'] == 'UF20':
#             iUF20 = sSerID
#             uf20Menu = []
#         if sParentID not in sFloorTmpList:
#             sFloorTmpList.append(sParentID)
#         sSerIDList.append(sSerID)
#
#     uf20Menu = LoadChildMenu(iUF20, 'UF20', one)
#
#     if len(uf20Menu) > 0:
#         uf20Menu = [{
#             'name': '离线工具',
#             'url': '',
#             'children': uf20Menu
#         }, UF20OnLine]
#     else:
#         uf20Menu = [UF20OnLine]
#
#     UF20MangeBaseMenuList = ['钉钉通道管理']
#     if '10003001' in sUserAuthList or '数据能力' == sTeamname:
#         UF20MangeBaseMenuList.append('定时任务管理')
#
#     uf20Menu.append({
#         'name': '服务管理',
#         'url': '',
#         'children': GetSvrMangeMenu(UF20MangeBaseMenuList)
#     })
#
#     uf30Menu = LoadChildMenu(iUF30, 'UF30', one)
#
#     if len(uf30Menu) > 0:
#         uf30Menu = [{
#             'name': '离线工具',
#             'url': '',
#             'children': uf30Menu
#         }, UF30OnLine]
#     else:
#         uf30Menu = [UF30OnLine]
#
#     UF30MangeBaseMenuList = ['微服务信息管理', '数据库管理']
#     if '10003001' in sUserAuthList or '数据能力' == sTeamname:
#         UF30OnLineBaseMenuList.append('定时任务管理')
#
#     uf30Menu.append({
#         'name': '服务管理',
#         'url': '',
#         'children': GetSvrMangeMenu(UF30MangeBaseMenuList)
#     })


# 用户登陆
def Login(formDataMap, start_response):
    sUserID = formDataMap['userID']
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    if bool(re.search('[a-z]', sUserID)):
        return json.dumps({'success': 'false', 'msg': '用户找不到，请确认后重试或注册'})

    sUserID = str(int(sUserID))

    sPwd = formDataMap['userPwd']
    md5Item = hashlib.md5()
    md5Item.update(sPwd.encode(encoding='utf-8'))
    sNewPwd = md5Item.hexdigest()

    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcUsersSearhSql % sUserID)
    OracleDB.CloseDB()
    # LoadMenu()
    sOldPwd = one[0][2]
    sOperatorName = one[0][1]
    sTeamname = one[0][3]
    if sNewPwd == sOldPwd:
        authList = getUserAuth(sUserID)
        sUserAuth = ' '.join(authList)
        # 生成jwt
        token = tokenFunc.create_token(sUserID, sUserAuth)
        return json.dumps({'success': True, 'msg': '欢迎登录' + sOperatorName,
                           'data': {
                               'token': token}
                           })
    else:
        return json.dumps({'success': False, 'msg': '用户名或密码错误请重试'})



# 获取菜单
# def GetMenu(formDataMap, start_response):
#     sUserID = formDataMap['userID']
#     start_response('200 OK', [('Content-Type', 'application/json'),
#                               ('Access-Control-Allow-Origin', '*')])
#     if bool(re.search('[a-z]', sUserID)):
#         return json.dumps({'success': 'false', 'msg': '用户不存在'})
#
#     sUserID = str(int(sUserID))
#
#     OracleDB.GetDB()
#     one, title = OracleDB.SelectDB(sqlDemoList.sDcUsersSearhSql % (sUserID))
#     OracleDB.CloseDB()
#     if len(one) == 0:
#         return json.dumps({'success': 'false', 'msg': '用户不存在'})
#
#     sTeamname = one[0][3]
#
#     sUserAuthList = []
#     sUserAuthList = getUserAuth(sUserID)
#     LoadMenu(sUserAuthList, sTeamname)
#     # 登陆成功后返回菜单列表
#     menuJson = []
#     yz30Item = {}
#     yz30Item['name'] = 'UF30'
#     yz30Item['url'] = ''
#     yz30Item['children'] = uf30Menu
#
#     yz20Item = {}
#     yz20Item['name'] = 'UF20'
#     yz20Item['url'] = ''
#     yz20Item['children'] = uf20Menu
#
#     yzSerMange = {}
#     yzSerMange['name'] = '服务管理'
#     yzSerMange['url'] = ''
#     yzSerMange['children'] = GetSvrMangeMenu(
#         ['微服务信息管理', '友情链接管理', '服务公告管理', '数据库管理', '钉钉通道管理', '系统配置管理', '定时任务管理'])
#
#     if sTeamname in ['项目研发组', '其它', '管理员', '数据能力', 'UED'] or '10001' in sUserAuthList:
#         menuJson.append(yz30Item)
#
#     if sTeamname != '项目研发组' or '10002' in sUserAuthList:
#         menuJson.append(yz20Item)
#
#     if sTeamname == '管理员' or sTeamname == '数据能力' or sTeamname == 'UED' or '10003' in sUserAuthList:
#         menuJson.append(yzSerMange)
#     return json.dumps({'success': 'true', 'menu': menuJson})


# if __name__ == "__main__":
#     LoadMenu()
#     print(uf20Menu)
#     print(uf30Menu)
