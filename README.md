# repository-oss

使用elasticsearch snapshot API可以备份您的集群，但是目前官方只支持 fs、azure、gcs、s3和hdfs5种仓库，该项目实现阿里OSS源

## 安装

### 下载
从[release页面](https://github.com/wujjpp/repository-oss/releases)下载对应版本zip包，如：

```shell
$ wget https://github.com/wujjpp/repository-oss/releases/download/6.3.2/repository-oss-6.3.2.zip

```

### 安装

#### 通过elasticsearch-plugin安装

```shell
$ elasticsearch-plugin install file:///<前面下载的zip包完整路径>
```

重启elasticsearch

#### 手动安装
在`<elasticsearch安装目录>/plugins`下创建 `repository-oss`目录，解开zip包，复制所有文件到`repository-oss`目录

重启elasticsearch


## 创建仓库
注：调用之前请先确保bucket是存在的

```
PUT http://<Your elasticsearch endpoint>/_snapshot/my_backup 
{
    "type": "oss",
    "settings": {
        "endpoint": "http://oss-cn-hangzhou-internal.aliyuncs.com",
        "access_key_id": "<yourAccessKeyId>", 
        "secret_access_key": "<yourAccessKeySecret>", 
        "bucket": "<yourBucketName>"
    }
}
```

具体endpoint可参考
- [阿里访问域名和数据中心文档](https://help.aliyun.com/document_detail/31837.html?spm=5176.doc31922.6.577.YxqZYt)


## 具体snapshot API请参考官方文档

[modules-snapshots](https://www.elastic.co/guide/en/elasticsearch/reference/6.3/modules-snapshots.html)

## 特别鸣谢
该项目的大部分代码来自
- [zhichen/elasticsearch-repository-oss](https://github.com/zhichen/elasticsearch-repository-oss)
- [项目结构参照](https://github.com/elastic/elasticsearch/tree/master/plugins/repository-azure)
- [AccessControl代码来自](https://github.com/elastic/elasticsearch/blob/master/plugins/repository-azure/src/main/java/org/elasticsearch/repositories/azure/SocketAccess.java)



Made with ♥ by Wu Jian Ping

Feel free to contact me if you have any problem [830390@qq.com](mailto:830390@qq.com)