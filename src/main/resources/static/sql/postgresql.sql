-- 安装UUID生成插件
create extension if not exists "uuid-ossp";
-- 使用方法生成UUID
select uuid_generate_v4();
-- 获取本地时间
select localtimestamp

-- 创建方法，批量插入测试数据（200W）
create or replace function insertData() returns
boolean AS
$BODY$
declare ii integer;
  begin
  II:=1;
  FOR ii IN 1..2000000	LOOP
  INSERT INTO tuser(uuid, ctime) VALUES (uuid_generate_v4(),localtimestamp);
  end loop;
  return true;
  end;
$BODY$
LANGUAGE plpgsql;

-- 执行方法
select count(*) from insertData() ;

-- 查看tuser表记录
select count(*) from tuser;

-- 随便查看个50条
select*from tuser limit 50;