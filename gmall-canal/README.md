# Canal
> 在Canal```1.1.4```版本后，可以使用界面UI方式进行管理
> 
> 也可直接以json的格式推送到kafka，故这个模块可不需写代码
> 
> 具体使用见官网 [Canal GitHub详情](https://github.com/alibaba/canal/wiki/Canal-Kafka-RocketMQ-QuickStart)
>
> 我的有道笔记 [详情点击查看Canal的安装以及使用](https://note.youdao.com/ynoteshare1/index.html?id=9377d3bedd9d1f8cb8ce9bd05a7db93c&type=note)

> topic的json格式示例

```
{
    "data": [
        {
            "id": "1",
            "username": "name111",
            "password": "password1",
            "age": "18",
            "sex": "1",
            "address": "adderss1"
        }
    ],
    "database": "hdsp_test",
    "es": 1582190645000,
    "id": 2,
    "isDdl": false,
    "mysqlType": {
        "id": "bigint(20)",
        "username": "varchar(120)",
        "password": "varchar(255)",
        "age": "int(11)",
        "sex": "tinyint(1)",
        "address": "varchar(255)"
    },
    "old": [
        {
            "username": "name11"
        }
    ],
    "pkNames": [
        "id"
    ],
    "sql": "",
    "sqlType": {
        "id": -5,
        "username": 12,
        "password": 12,
        "age": 4,
        "sex": -7,
        "address": 12
    },
    "table": "userinfo_text",
    "ts": 1582190749164,
    "type": "UPDATE"
}
```