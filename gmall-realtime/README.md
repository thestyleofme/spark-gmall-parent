> DBeaver连接phoenix 
>
> https://www.iteblog.com/archives/6625.html?from=like
>
> docker pull iteblog/hbase-phoenix-docker:1.0
> 
> docker run -it -d -p 8765:8765 -p 2182:2181 -p 60000:60000 -p 60010:60010 -p 60020:60020 -p 60030:60030 iteblog/hbase-phoenix-docker:1.0
>
> docker exec -it CONTAINER_ID /bin/bash
>
> jdbc:phoenix:thin:url=http://192.168.12.247:8765;serialization=PROTOBUF

> Phoenix语法
> https://phoenix.apache.org/language/index.html#upsert_values
```
CREATE TABLE gmall_dau( mid VARCHAR,
uid VARCHAR,
appid VARCHAR,
area VARCHAR,
os VARCHAR,
ch VARCHAR,
TYPE VARCHAR,
vs VARCHAR,
logDate VARCHAR,
logHour VARCHAR,
ts BIGINT CONSTRAINT dau_pk PRIMARY KEY(mid,
logDate) );
```