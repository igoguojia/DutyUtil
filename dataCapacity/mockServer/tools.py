import socket
import pickle
import os
import time
import datetime
import uuid
import chardet
import pinyin
from pypinyin import lazy_pinyin


# 获取unix时间戳
def unix_time():
    return round(time.mktime(datetime.datetime.now().timetuple()))


# 获取IP地址
def GetIp():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(('8.8.8.8', 80))
        ip = s.getsockname()[0]
    finally:
        s.close()
    return ip


# 返回项目根目录
def getProjectPath():
    return os.path.abspath(os.path.dirname(os.path.dirname(__file__)))


# 返回GUID
def getGuid():
    sGuid = str(uuid.uuid1())
    sGuid = sGuid.replace('-', '')
    return sGuid


# 返回文件的编码
def GetEncoding(sFilePath: str):
    f3 = open(file=sFilePath, mode='rb')
    data = f3.read()
    f3.close()
    result = chardet.detect(data)
    return result['encoding']


# 原代码读取
def LoadSrcCode(sClasTestThreadPath):
    sEncoding = GetEncoding(sClasTestThreadPath)
    try:
        with open(sClasTestThreadPath, 'r', encoding=sEncoding) as rFile:
            sText = rFile.read().splitlines()
    except UnicodeDecodeError:
        if sEncoding == 'GB2312':
            sEncoding = 'GB18030'
        try:
            with open(sClasTestThreadPath, 'r', encoding=sEncoding) as rFile:
                sText = rFile.read().splitlines()
        except UnicodeDecodeError:
            with open(sClasTestThreadPath, 'r', encoding=sEncoding, errors='ignore') as rFile:
                sText = rFile.read().splitlines()

    return sText, sEncoding


# 获取拼音简称
def GetInteFaceAbbName(intefaceName):
    return pinyin.get_initial(intefaceName, delimiter="").upper()


# 根据中文名返回英文名
def ChineseToEnglish(sCNAme: str):
    name_list = lazy_pinyin(sCNAme)
    xin = name_list[0]
    ming_list = name_list[1:]
    ming = ""
    for y in ming_list:
        ming = ming + y
    en_name = ming + " " + xin
    en_name = en_name.title().lstrip()
    return en_name


# 根据标题和数据返回结果重新格式化成dict类型的list
def FormatDBToList(one, title, isLower=False):
    sRuseltList = []
    for item in one:
        yzItem = {}
        for iLoop in range(len(item)):
            sKey = title[iLoop][0]
            if isLower:
                sKey = str(sKey).lower()
            yzItem[sKey] = item[iLoop]
        sRuseltList.append(yzItem)
    return sRuseltList


if __name__ == "__main__":
    one = [('webservice', '1',
            'http://localhost:7783/webservice/creditreportquery?wsdl',
            'webservice服务，可用于远程获取服务', 'webservice', 'webservice')]
    title = [('SERVER_NAME', '<cx_Oracle.DbType DB_TYPE_VARCHAR>', 64, 256,
              None, None, 0),
             ('SERVER_TYPE', '<cx_Oracle.DbType DB_TYPE_VARCHAR>', 1, 4, None,
              None, 0),
             ('HTTPINFO', '<cx_Oracle.DbType DB_TYPE_VARCHAR>', 255, 1020,
              None, None, 0),
             ('REMARK', '<cx_Oracle.DbType DB_TYPE_VARCHAR>', 2000, 8000, None,
              None, 0),
             ('KEYWORDS', '<cx_Oracle.DbType DB_TYPE_VARCHAR>', 255, 1020,
              None, None, 0),
             ('POSITION_STR', '<cx_Oracle.DbType DB_TYPE_VARCHAR>', 64, 256,
              None, None, 0)]
    print(FormatDBToList(one, title))
