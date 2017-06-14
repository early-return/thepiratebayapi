# 海盗湾API

一个海盗湾的爬虫，通过Spring Mvc提供API。 你可以将本程序放到自己的服务器上或是使用作者部署好的服务器API。

本程序爬取了海盗湾的搜索、分类、热门、上传者、详情等板块。

## 使用方法

### 使用作者搭建好的API服务

1. 搜索： `http://tpbapi.zhiqing.info/search/{keyWord}/{pageIndex}`
2. 分类： `http://tpbapi.zhiqing.info/browse/{typeCode}/{pageIndex}`
3. 热门： `http://tpbapi.zhiqing.info/top/{typeCode}/{pageIndex}`
4. 上传者： `http://tpbapi.zhiqing.info/author/{userName}/{pageindex}`
5. 详情： `http://tpbapi.zhiqing.info/torrent/{torrentCode}`

### 上传至自己的服务器

```bash
git clone git@github.com:zhiqing-lee/thepiratebayapi.git
cd thepiratebayapi
gradle bootRepackage
```

在`thepiratebayapi/build/libs/`目录中找到`thepiratebay-0.0.1-SNAPSHOT.jar`文件，然后上传至服务器并运行。

## 接口格式

- `搜索`、`分类`、`热门`、`上传者`获取的Json数据格式为:
```javascript
{
    "error": false,     //boolean类型，为true表示出错
    "errorMessage": "", //错误消息
    "torrents": [       //结果数组
        {
        "typeTitle": "Games > PC",  //类型
        "typeCode": "401",          //类型代码,获取某一类型种子时需要
        "title": "Hello.Neighbor.Alpha.3-ALI213",   //标题 
        "code": "16659179",         //种子代码，获取详情时需要
        "time": "01-02 15:36",      //上传时间
        "size": "1.02 GiB",         //文件大小
        "link": "",                 //磁力链接
        "seeders": 925,             //做种数
        "leechers": 31,             //下载数
        "author": "KingKenn"        //上传者
        }, ...
    ]
}
```

- `详情`获取的Json数据格式为：
```javascript
{
    "error": false,
    "errorMessage": null,
    "detail": {
        "info": {       //字典型，包含种子的一些信息
            "Comment": "3  ",
            "Type": "Audio > Music",
            "Uploaded": "2017-05-27 00:19:18 GMT",
            "Size": "340.18 MiB (356705611 Bytes)",
            ...
        },
        "link": "",     //磁力链接
        "intro": ""     //介绍文字
    }
}
```
