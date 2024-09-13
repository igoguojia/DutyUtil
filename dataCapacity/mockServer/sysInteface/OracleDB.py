# 本代码实现oracle的相关操作
import cx_Oracle


##获取数据库连接
def GetDB(sUser='jn_asset',
          sIP='localhost',
          sPort='8080',
          sSerName='orcl'):
    global conn
    conn = cx_Oracle.connect(sUser, '123',
                             cx_Oracle.makedsn(sIP, sPort, None, sSerName))
    global cursor
    cursor = conn.cursor()


##关闭数据库连接
def CloseDB():
    cursor.close()
    conn.close()


##执行sql语句
def ExecDB(vSqlStr):
    IsSecu = True
    try:
        cursor.execute(vSqlStr)
        conn.commit()
    except Exception as e:
        print(vSqlStr)
        IsSecu = False
        return IsSecu, str(e)
    return IsSecu, ''


def chunks(list_, size):
    for i in range(0, len(list_), size):
        yield list_[i:i + size]


##带参数执行sql语句
def ExecParamDB(vSqlStr, param=[]):
    IsSecu = True
    try:
        cursor.executemany(vSqlStr, param)
        conn.commit()
    except Exception as e:
        print('批量处理报错了:' + str(e))
        if str(e).split(':', 1)[0] in ['DPI-1015', 'DPI-1001']:
            for par in list(chunks(param, 5000)):
                cursor.executemany(vSqlStr, par)
                conn.commit()
        else:
            for item in param:
                sSqlstr = vSqlStr
                for itemName in item:
                    sStrconst = "'" + str(item[itemName]) + "'"
                    sSqlstr = sSqlstr.replace(':' + itemName, sStrconst)
                IsSecu2, e2 = ExecDB(sSqlstr)
                if not IsSecu2:
                    e = str(e) + '------>' + str(e2) + '\n' + sSqlstr
                    break
            IsSecu = False
            return IsSecu, str(e)
    return IsSecu, ''


##查询sql语句
def SelectDB(vSqlStr):
    print(vSqlStr)
    cursor.execute(vSqlStr)
    one = cursor.fetchall()
    # 获取字段信息
    title = cursor.description
    return one, title
