# 本文件用来管理服务公告信息栏
import sysInteface.OracleDB as OracleDB
import sysInteface.sqlDemoList as sqlDemoList
import json
import tools


# 服务新增
def ServerAdd(formDataMap, start_response):
    sServerName = formDataMap['server_name']
    sServerType = formDataMap['server_type']
    sHttpInfo = formDataMap['httpinfo']
    sRemark = formDataMap['remark']
    sKeyWords = formDataMap['keywords']
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    if str(sServerName).strip() == '':
        return json.dumps({'success': False, 'msg': 'Server服务名不允许为空'})

    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcServerNoticeSearhSql %
                                   {'server_name': sServerName})
    if len(one) > 0:
        OracleDB.CloseDB()
        return json.dumps({'success': False, 'msg': 'Server服务名已存在不允许重复新增'})
    else:
        bSuccess, e = OracleDB.ExecDB(
            sqlDemoList.sDcServerNoticeInsertSql % {
                'server_name': sServerName,
                'server_type': sServerType,
                'httpinfo': sHttpInfo,
                'remark': sRemark,
                'keywords': sKeyWords
            })
        OracleDB.CloseDB()
        if bSuccess:
            return json.dumps({'success': True, 'msg': 'Server服务新增成功'})
        else:
            return json.dumps({'success': False, 'msg': 'Server服务新增失败:' + e})


# 服务修改
def ServerMod(formDataMap, start_response):
    sServerName = formDataMap['server_name']
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    if str(sServerName).strip() == '':
        return json.dumps({'success': False, 'msg': 'Server服务名不允许为空'})
    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcServerNoticeSearhSql %
                                   {'server_name': sServerName})
    if len(one) == 1:
        bSuccess, e = OracleDB.ExecDB(sqlDemoList.sDcServerNoticeUpdateSql %
                                      formDataMap)
        OracleDB.CloseDB()
        if bSuccess:
            return json.dumps({'success': True, 'msg': 'Server服务修改成功'})
        else:
            return json.dumps({'success': False, 'msg': 'Server服务修改失败:' + e})
    else:
        OracleDB.CloseDB()
        return json.dumps({'success': False, 'msg': 'Server服务名不存在无法修改'})


# 服务删除
def ServerDel(formDataMap, start_response):
    sServerName = formDataMap['server_name']
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    if str(sServerName).strip() == '':
        return json.dumps({'success': False, 'msg': 'Server服务名不允许为空'})
    OracleDB.GetDB()
    one, title = OracleDB.SelectDB(sqlDemoList.sDcServerNoticeSearhSql %
                                   {'server_name': sServerName})
    if len(one) == 0:
        OracleDB.CloseDB()
        return json.dumps({'success': False, 'msg': 'Server服务名不存在不允许删除'})
    else:
        bSuccess, e = OracleDB.ExecDB(sqlDemoList.sDcServerNoticeDeleteSql %
                                      formDataMap)
        OracleDB.CloseDB()
        if bSuccess:
            return json.dumps({'success': True, 'msg': 'Server服务删除成功'})
        else:
            return json.dumps({'success': False, 'msg': 'Server服务删除失败:' + e})


# 服务查询
def ServerQuery(paramMap, start_response):
    start_response('200 OK', [('Content-Type', 'application/json'),
                              ('Access-Control-Allow-Origin', '*')])
    OracleDB.GetDB()

    one, title = OracleDB.SelectDB(sqlDemoList.sDcServerNoticeAllSearhSql)
    OracleDB.CloseDB()
    return json.dumps({
        'success': True,
        'data': tools.FormatDBToList(one, title)
    })
