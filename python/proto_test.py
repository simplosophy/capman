#!/usr/bin/python
# coding=utf-8

import parser.plyproto_parser as plyproto

test1 = """package tutorial;"""

test2 = """package tutorial;
message Person {
  required string name = 1;
  required int32 id = 2;
  optional string email = 3;
}
"""

test3 = """package capman;
option java_package="io.capman.protobuf";

import "google/protobuf/descriptor.proto";
//extend google.
//文件扩展


//服务类型
enum EServiceType{
    EServiceType_Any = 0;//任意
    EServiceType_Logic = 1;//逻辑服务,默认不会保存状态,例如业务逻辑服务
    EServiceType_Storage = 2;//存储服务,用于保存状态,例如各种存储服务,队列服务
}

extend google.protobuf.ServiceOptions {
    optional string service_name = 10000;
    optional int32 service_type = 10001;
}

//方法扩展
enum EClusterStrategy{
    EClusterStrategy_FailOver = 1;//失败自动切换，当出现失败，重试其它服务器，通常用于读操作（推荐使用）
    EClusterStrategy_FailFast = 2;//快速失败，只发起一次调用，失败立即报错,通常用于非幂等性的写操作
    EClusterStrategy_FailRetry = 3;//失败重试，出现网络异常时，直接重试
    EClusterStrategy_Custom = 4;//自定义
}
enum ELoadbalanceStrategy{
    ELoadbalanceStrategy_ConsistentHash = 1;//一致性hash
    ELoadbalanceStrategy_Random = 2;//随机
    ELoadbalanceStrategy_RoundRobin = 3;//轮训
    ELoadbalanceStrategy_Custom = 4;//自定义
}


//


message RequestContext{
    required int32 uin = 1;
}




"""

parser = plyproto.ProtobufAnalyzer()
print(parser.parse_string(test3))
