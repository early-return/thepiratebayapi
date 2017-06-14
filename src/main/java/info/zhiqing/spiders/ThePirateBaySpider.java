package info.zhiqing.spiders;

import info.zhiqing.entities.Torrent;
import info.zhiqing.entities.TorrentDetail;
import info.zhiqing.entities.TorrentType;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiqing on 17-6-13.
 */

@Repository
public class ThePirateBaySpider {
    @Value("app.url.base")
    public static final String BASE_URL = "https://thepiratebay.org";

    private OkHttpClient client;

    @Value("${app.proxy}")
    private boolean proxy;

    @Value("${app.proxy.host}")
    private String proxyHost;

    @Value("${app.proxy.port}")
    private int proxyPort;


    public ThePirateBaySpider() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //自动化管理网站Cookie
        builder.cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<>();
            }
        });

        //停用从定向功能
        builder.followRedirects(false)
                .followSslRedirects(false);

        //检测是否设置代理
        if(proxy) {
            builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort)));
        }

        client = builder.build();
    }


    //通过Url地址爬取页面
    private String fetchPageByUrl(String url) throws IOException {
        Request switchViewRequest = new Request.Builder()
                .url("https://thepiratebay.org/switchview.php?view=s")
                .addHeader("accept-language", "zh-CN,en-US;q=0.8,en;q=0.6,zh;q=0.4")
                .build();
        client.newCall(switchViewRequest).execute().close();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        response.close();
        return body;
    }

    //通过页面字符串提取种子信息
    private List<Torrent> collectTorrents(String body) throws IOException {
        Document doc = Jsoup.parse(body);

        //获取搜索结果表格
        Element result = doc.getElementById("searchResult");

        //获取搜索结果表格主体
        Element resultBody = result.getElementsByTag("tbody").first();

        //获取每一行
        Elements rows = resultBody.getElementsByTag("tr");

        List<Torrent> torrents = new ArrayList<>();

        //遍历每一行
        for(Element ele :  rows) {
            Torrent torrent = new Torrent();

            //获取每一行的每一列
            Elements cols = ele.getElementsByTag("td");

            if(cols.size() != 8) {
                continue;
            }

            //获取类型
            TorrentType type = new TorrentType();
            Element typeEle = cols.get(0).getElementsByTag("a").first();
            String typeLink = typeEle.attr("href").trim();
            torrent.setTypeCode(typeLink.substring(typeLink.lastIndexOf("/")+1));
            torrent.setTypeTitle(typeEle.text());

            //获取标题
            Element titleEle = cols.get(1).getElementsByTag("a").first();
            String title = titleEle.text();
            torrent.setTitle(title);

            //获取编号
            String code = titleEle.attr("href");
            code = code.substring(0, code.lastIndexOf("/"));
            code = code.substring(code.lastIndexOf("/")+1);
            torrent.setCode(code);

            //获取上传时间
            String time = cols.get(2).text().trim();
            torrent.setTime(time);

            //获取磁力链接
            Element linkEle = cols.get(3);
            Element nobrEle = linkEle.getElementsByTag("nobr").first();
            Element linkAEle = nobrEle.getElementsByTag("a").first();
            String link = linkAEle.attr("href");
            torrent.setLink(link);

            //获取文件大小
            String size = cols.get(4).text().trim();
            torrent.setSize(size);

            //获取做种数
            int seeders = Integer.parseInt(cols.get(5).text().trim());
            torrent.setSeeders(seeders);

            //获取下载数
            int leechers = Integer.parseInt(cols.get(6).text().trim());
            torrent.setLeechers(leechers);

            //获取上传者
            String author = cols.get(7).text().trim();
            torrent.setAuthor(author);

            torrents.add(torrent);

        }

        return torrents;
    }

    private List<Torrent> fetchTorrentsByUrl(String url) throws IOException {
        String body = fetchPageByUrl(url);
        return collectTorrents(body);
    }

    public List<Torrent> search(String keyword, int page) throws IOException {
        String url = BASE_URL + "/search/" + keyword + "/" + page;
        return fetchTorrentsByUrl(url);
    }

    public List<Torrent> browse(String typeCode, int page) throws IOException {
        String url = BASE_URL + "/browse/" + typeCode + "/" + page;
        return fetchTorrentsByUrl(url);
    }

    public List<Torrent> top(String typeCode, int page) throws IOException {
        String url = BASE_URL + "/top/" + typeCode + "/" + page;
        return fetchTorrentsByUrl(url);
    }

    public List<Torrent> user(String username, int page) throws IOException {
        String url = BASE_URL + "/top/" + username + "/" + page;
        return fetchTorrentsByUrl(url);
    }

    private TorrentDetail collectDetail(String body) {
        TorrentDetail detail = new TorrentDetail();
        Map<String, String> info = new HashMap<>();

        //获取种子信息
        Document doc = Jsoup.parse(body);
        Elements dtEles = doc.getElementsByTag("dt");
        Elements ddEles = doc.getElementsByTag("dd");
        if(dtEles.size() == ddEles.size()) {
            for(int i = 0; i < dtEles.size() - 1; i++) {
                String key = dtEles.get(i).text().trim();
                key = key.substring(0, key.length() - 1);
                String value = ddEles.get(i).text().trim();
                info.put(key, value);
            }
        }
        detail.setInfo(info);

        //获取种子的磁力链接
        Element linkEle = doc.select("div.download a").first();
        String link = linkEle.attr("href");
        detail.setLink(link);

        //获取种子的介绍
        Element introEle = doc.select("div.nfo pre").first();
        String intro = introEle.text();
        detail.setIntro(intro);

        return detail;
    }


    public TorrentDetail detail(String code) throws IOException {
        String url = BASE_URL + "/torrent/" + code;
        String body = fetchPageByUrl(url);
        return collectDetail(body);
    }


    public static void main(String[] args) {
        ThePirateBaySpider spider = new ThePirateBaySpider();
        try{
            System.out.println(spider.fetchPageByUrl(ThePirateBaySpider.BASE_URL));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
