sDcUsersSearhSql = "select operator_no,operator_name,operator_pwd,group_name,e_mail,mobile_tel,is_leader from jn_asset.dcusers where operator_no = '%s'"
sDcUsersQurySql = "select operator_no,operator_name,group_name,e_mail,mobile_tel,is_leader from jn_asset.dcusers where operator_no = '%s'"

sDcUsersInsertSql = '''\
insert into jn_asset.dcusers(operator_no,operator_name,operator_pwd,group_name)
     values('%(operator_no)s','%(operator_name)s','%(operator_pwd)s','%(group_name)s')'''

sDcUsersUpdateSql = '''\
update jn_asset.dcusers
   set e_mail = '%(eMail)s',
       mobile_tel = '%(mobileTel)s'
 where operator_no = '%(operator_no)s'
'''
# sDcUsersUpdateSql = '''\
# update jn_asset.dcusers
#    set operator_pwd = '%(operator_pwd)s',
#        group_name = '%(group_name)s',
#        e_mail = '%(eMail)s',
#        mobile_tel = '%(mobileTel)s'
#  where operator_no = '%(operator_no)s'
# '''

sDcToolInfoSearhSql = '''\
select mod_operator_no,tool_name,server_path,product_series,remark,keywords 
  from dctoolinfo a 
 where a.product_series like '%%%(product_series)s%%' 
   and (   a.remark like '%%%(keywords)s%%' 
        or a.keywords like '%%%(keywords)s%%' 
        )'''

sDcServerNoticeAllSearhSql = '''select * from jn_asset.dcservernotice a'''
sDcServerNoticeSearhSql = '''\
select server_name,server_type,httpinfo,remark,keywords 
  from dcservernotice a 
 where trim('%(server_name)s') is null 
    or server_name = '%(server_name)s' '''

sDcServerNoticeSearhPageSql = '''\
select server_name,server_type,httpinfo,remark,keywords,position_str
  from (select server_name,server_type,httpinfo,remark,keywords,server_name as position_str
          from dcservernotice a 
         where (    remark like '%%%(keywords)s%%'
                or instr(','||keywords||',',','||'%(keywords)s'||',') > 0
                )
           and server_name > '%(position_str)s'
         order by server_name
       )
 where rownum <= %(request_num)s '''

sDcServerNoticeSearhPrePageCountSql = '''\
select count(0) as icount
  from (select server_name,server_type,httpinfo,remark,keywords,server_name as position_str
          from dcservernotice a 
         where (    remark like '%%%(keywords)s%%'
                or instr(','||keywords||',',','||'%(keywords)s'||',') > 0
                )
           and server_name < '%(position_str)s'
         order by server_name
       ) '''

sDcServerNoticeSearhPrePageSql = '''\
select server_name,server_type,httpinfo,remark,keywords,position_str 
  from (select server_name,server_type,httpinfo,remark,keywords,position_str,rownum as rn
          from (select server_name,server_type,httpinfo,remark,keywords,server_name as position_str
                  from dcservernotice a 
                 where (    remark like '%%%(keywords)s%%'
                        or instr(','||keywords||',',','||'%(keywords)s'||',') > 0
                       )
                   and server_name < '%(position_str)s'
                 order by server_name 
               )
       )
 where rn > %(request_num)s '''

sDcServerNoticeInsertSql = '''\
insert into dcservernotice(server_name,server_type,httpinfo,remark,keywords)
     values ('%(server_name)s','%(server_type)s','%(httpinfo)s','%(remark)s','%(keywords)s') '''

sDcServerNoticeUpdateSql = '''\
update dcservernotice
   set server_type = '%(server_type)s',
       httpinfo = '%(httpinfo)s',
       remark = '%(remark)s',
       keywords = '%(keywords)s'
 where server_name = '%(server_name)s' '''

sDcServerNoticeDeleteSql = '''\
delete dcservernotice where server_name = '%(server_name)s' '''

sTeammembersSearchSql = '''\
select a.teamname,a.employeecode,
       (select b.operator_name from jn_asset.TsOperators b where b.operator_no = a.employeecode and rownum = 1) as operator_name 
  from jn_asset.Teammembers a where employeecode = '%s' '''

sdcmicroServerinfoNameSearchSql = '''\
select micro_svr_name,svrIp,svrport,Licence_path from jn_asset.dcmicroServerinfo a where a.micro_svr_name = '%(micro_svr_name)s' '''

sdcmicroServerinfoIpSearchSql = '''\
select micro_svr_name,svrIp,svrport from jn_asset.dcmicroServerinfo a where a.svrIp = '%(svrIp)s' and a.svrport = '%(svrport)s' '''

sdcmicroServerinfoAddSql = '''\
insert into jn_asset.dcmicroServerinfo(micro_svr_name,svrIp,svrport,Licence_path)
     values('%(micro_svr_name)s','%(svrIp)s','%(svrport)s','%(Licence_path)s') '''

sdcmicroServerinfoModSql = '''\
update jn_asset.dcmicroServerinfo a
   set a.svrIp = '%(svrIp)s',
       a.svrport = '%(svrport)s',
       a.Licence_path = '%(Licence_path)s' 
 where micro_svr_name = '%(micro_svr_name)s' '''

sdcmicroServerinfoDelSql = '''\
delete from jn_asset.dcmicroServerinfo where micro_svr_name = '%(micro_svr_name)s' '''

sdcmicroServerinfoSearhPageSql = '''\
select micro_svr_name,svrIp,svrport,Licence_path,position_str
  from (select micro_svr_name,svrIp,svrport,Licence_path,micro_svr_name as position_str
          from dcmicroServerinfo a 
         where (trim('%(svrIp)s') is null or a.svrIp = '%(svrIp)s')
           and (trim('%(micro_svr_name)s') is null or a.micro_svr_name = '%(micro_svr_name)s')
           and micro_svr_name > '%(position_str)s'
         order by micro_svr_name
       )
 where rownum <= %(request_num)s '''

sDcSeriesInfoSearchSql = '''select a.ser_id,a.ser_name,a.parent_id from jn_asset.dcseriesinfo a order by a.ser_id'''

sDcMicroDbDelSql = '''delete from dcMicroDb where micro_svr_name = '%(micro_svr_name)s' '''

sDcMicroDbAddSql = '''insert into dcMicroDb(micro_svr_name,db_id)values('%(micro_svr_name)s','%(db_id)s') '''

sDcMicroDbSearchSvrSql = '''\
select a.micro_svr_name,b.db_type,b.db_ip,b.db_port,b.db_sid,b.db_user,b.db_pwd,b.db_name,b.keywords,b.db_id
  from jn_asset.dcmicrodb a left join dcdbserverinfo b on a.db_id = b.db_id 
 where a.micro_svr_name = '%(micro_svr_name)s' '''

sDcDbServerInfoAddSql = '''\
insert into dcDbServerInfo(db_id,db_type,db_ip,db_port,db_sid,db_name,db_user,db_pwd,keywords)
values('%(db_id)s','%(db_type)s','%(db_ip)s','%(db_port)s','%(db_sid)s','%(db_name)s','%(db_user)s','%(db_pwd)s','%(keywords)s')'''

sDcDbServerInfoDelSql = '''delete from dcDbServerInfo where db_id = '%(db_id)s' '''

sDcDbServerInfoModSql = '''\
update dcDbServerInfo
   set db_type = '%(db_type)s',
       db_ip = '%(db_ip)s',
       db_port = '%(db_port)s',
       db_sid = '%(db_sid)s',
       db_name = '%(db_name)s',
       db_user = '%(db_user)s',
       db_pwd = '%(db_pwd)s',
       keywords = '%(keywords)s'
 where db_id = '%(db_id)s' '''

sDcDbServerInfoBatchModSql = '''\
update dcDbServerInfo
   set db_type = '%(db_type)s',
       db_ip = '%(db_ip)s',
       db_port = '%(db_port)s',
       db_sid = '%(db_sid)s',
       db_name = '%(db_name)s',
       db_user = '%(db_user)s',
       db_pwd = '%(db_pwd)s',
       keywords = '%(keywords)s'
 where db_id = '%(old_db_ip)s'
   and db_type = '%(old_db_type)s'
   and db_port = '%(old_db_port)s'
   and db_sid = '%(old_db_sid)s'
   and db_name = '%(old_db_name)s' '''

sDcDbServerInfoBatchDelSql = '''\
delete dcDbServerInfo 
 where db_type = '%(db_type)s'
   and db_ip = '%(db_ip)s'
   and db_port = '%(db_port)s'
   and db_name = '%(db_name)s'
   and db_sid = '%(db_sid)s' '''

sDcDbServerInfoSearhPageSql = '''\
select db_ip,position_str
  from (select distinct db_ip,db_ip as position_str
          from dcDbServerInfo a 
         where (trim('%(svrIp)s') is null or a.db_ip like '%(svrIp)s%%')
           and (trim('%(db_type)s') is null or a.db_type like '%(db_type)s%%')
           and (trim('%(keywords)s') is null or a.keywords like '%(keywords)s%%')
           and db_ip > '%(position_str)s'
         order by db_ip
       )
 where rownum <= %(request_num)s '''

sDcDbServerInfoSearhSubSql = '''\
select db_id,db_type,db_ip,db_port,db_sid,db_name,db_user,db_pwd,keywords
  from dcDbServerInfo where db_ip = '%(svrIp)s' 
order by db_port,db_sid,db_name,db_user '''

sDcDbServerInfoSearhSql = '''\
select db_id,db_type,db_ip,db_port,db_sid,db_name,db_user,db_pwd,keywords
  from dcDbServerInfo 
 where db_ip = '%(svrIp)s' 
   and db_port ='%(db_port)s' 
   and (   db_sid = '%(db_sid)s' 
        or db_name = '%(db_name)s'
       )
   and db_user = '%(db_user)s' '''

sDcDbServerInfoSearhIDSql = '''\
select db_id,db_type,db_ip,db_port,db_sid,db_name,db_user,db_pwd,keywords
  from dcDbServerInfo 
 where db_id = '%(db_id)s' '''

sHepInterfaceTblSearchMicroSql = '''\
select micro_service,interface_name_en,interface_name_zh,belong_id,id,default_category_name,position_str
  from (select a.micro_service,a.interface_name_en,a.interface_name_zh,a.belong_id,a.id,a.default_category_name,a.id as position_str 
          from hep_interface_tbl a 
         where (   a.interface_name_en like '%%%(interface_name_en)s%%' 
                or a.interface_name_zh like '%%%(interface_name_zh)s%%' 
               )
           and a.micro_service = '%(micro_service)s'
           and a.id > '%(position_str)s'
         order by a.id
       )
 where rownum <= %(request_num)s '''

sHepInterfaceTblSearchNoMicroSql = '''\
select micro_service,interface_name_en,interface_name_zh,belong_id,id,default_category_name,position_str
  from (select a.micro_service,a.interface_name_en,a.interface_name_zh,a.belong_id,a.id,a.default_category_name,a.id as position_str 
          from hep_interface_tbl a 
         where (   a.interface_name_en like '%%%(interface_name_en)s%%' 
                or a.interface_name_zh like '%%%(interface_name_zh)s%%' 
               )
           and a.id > '%(position_str)s'
         order by a.id
       )
 where rownum <= %(request_num)s '''

sHepFunctionBaseInputSearchSql = '''\
select a.param_name_en as filedName,a.param_name_zh as filedCName,a.param_data_type as filedDocType,' ' as dictEntry
          from hep_interface_pub_params_tbl a 
         where a.belong_id='%(belong_id)s' 
           and a.micro_service = '%(micro_service)s' 
	         and a.category_name_en ='%(category_name_en)s' 
         order by a.id '''

sHepFunctionSelfInfoSearchSql = '''\
select b.field_name as filedName,b.chinese_name as filedCName,b.field_business_type as filedDocType,nvl(b.dict_id,' ') as dictEntry,param_return 
  from hep_interface_parameters_tbl a,standard_field_dept b
 where a.param_name_en = b.field_name 
   and a.belong_id = b.belong_id
   and a.interface_id='%(interface_id)s' 
   and a.belong_id = '%(belong_id)s' 
 order by a.param_return,a.param_sort '''

sHepMicroServiceTbl = '''\
select a.config_name,a.config_name_zh 
  from hep_micro_service_tbl a 
 where a.belong_id ='T20150180' 
   and a.micro_service_prefix = 'hsbroker' 
   and a.config_no is not NULL 
order by a.config_name
'''

sDcMenuSearchSql = '''select menu_caption,menu_url from dcmenu a '''

sDcOnDutyQrySql = '''select * from jn_asset.dconduty a '''
sDcOnDutyAddSql = '''
insert into jn_asset.dconduty(duty_name,group_name,group_name2,people_count,duty_msg,duty_people,user_id,pub_notice_week,auto_skip,self_holiday) 
     values ('%(duty_name)s','%(group_name)s','%(group_name2)s','%(people_count)s','%(duty_msg)s','%(duty_people)s','%(userID)s','%(pub_notice_week)s','%(auto_skip)s','%(self_holiday)s') '''
sDcOnDutyModSql = '''
update jn_asset.dconduty
   set group_name = '%(group_name)s',
       people_count = '%(people_count)s',
       duty_msg = '%(duty_msg)s',
       duty_people = '%(duty_people)s',
       pub_notice_week = '%(pub_notice_week)s',
       auto_skip= '%(auto_skip)s',
       self_holiday= '%(self_holiday)s'
 where duty_name = '%(duty_name)s' '''
sDcOnDutyDelSql = '''delete from jn_asset.dconduty where duty_name = '%(duty_name)s' '''
sDcOnDutyQryByUserIdSql = '''select * from jn_asset.dconduty a where a.user_id = '%(userID)s' '''
sDcOnDutyQryByDutyNameSql = '''select * from jn_asset.dconduty a where a.duty_name = '%(duty_name)s' '''
sDcOnDutyQryByGroupSql = '''select * from jn_asset.dconduty a where a.group_name2 = '%(group_name2)s' '''
sDingPersonInfoSql = 'select * from jn_asset.dingpersoninfo a'
sDcDingTalkInfoQrySql = 'select * from jn_asset.dcdingtalkinfo '
sDcUserAuthQrySql = '''\
select a.user_id,b.authority_id from jn_asset.dcusercharacter a,jn_asset.dccharacterauthority b 
where a.character_id = b.character_id
  and a.user_id = '%(userID)s'
 union
select a.user_id,a.authority_id from jn_asset.dcuserauthority a where a.user_id = '%(userID)s' '''
