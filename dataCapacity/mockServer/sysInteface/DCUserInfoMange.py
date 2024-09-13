# 本文件用来负责用户信息修改和注册
import sysInteface.OracleDB as OracleDB
import sysInteface.sqlDemoList as sqlDemoList
import tools
import json
import hashlib
from sysInteface.DCLogin import getUserAuth


# 查看用户信息
def usersQry(formDataMap, start_response):
    sUserID = formDataMap['userID']
    sUserID = str(int(sUserID))
    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcUsersQurySql % sUserID)
    OracleDB.CloseDB()
    one = tools.FormatDBToList(one, title)
    one[0]['AUTH'] = getUserAuth(sUserID)
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    return json.dumps({'success': True, 'data': one})


# 修改用户信息
def usersMod(formDataMap, start_response):
    sUserID = formDataMap['userID']
    sUserID = str(int(sUserID))

    # sPwd = formDataMap['userPwd']
    # sTeamname = formDataMap['groupName']
    sMobileTel = ''
    if 'mobileTel' in formDataMap:
        sMobileTel = formDataMap['mobileTel']
    sEMail = ''
    if 'eMail' in formDataMap:
        sEMail = formDataMap['eMail']

    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcUsersSearhSql % sUserID)
    # if len(sPwd) == 0:
    #     # 密码传空表示不修改
    #     sNewPwd = one[0][2]
    # else:
    #     if sPwd.strip() == '-1':
    #         sPwd = '1'
    #     md5Item = hashlib.md5()
    #     md5Item.update(sPwd.encode(encoding='utf-8'))
    #     sNewPwd = md5Item.hexdigest()

    if sMobileTel.strip() == '':
        sMobileTel = one[0][5]
    if sEMail.strip() == '':
        sEMail = one[0][4]

    bSuccess, e = OracleDB.ExecDB(
        sqlDemoList.sDcUsersUpdateSql % {
            'operator_no': sUserID,
            # 'operator_pwd': sNewPwd,
            # 'group_name': sTeamname,
            'mobileTel': sMobileTel,
            'eMail': sEMail
        })
    OracleDB.CloseDB()
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    if bSuccess:
        return json.dumps({'success': True, 'msg': '用户信息修改成功'})
    else:
        return json.dumps({'success': False, 'msg': '用户信息修改失败:' + e})
