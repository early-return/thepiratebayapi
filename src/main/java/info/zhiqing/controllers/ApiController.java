package info.zhiqing.controllers;

import info.zhiqing.entities.Torrent;
import info.zhiqing.entities.TorrentDetail;
import info.zhiqing.entities.TorrentDetailResponse;
import info.zhiqing.entities.TorrentsResponse;
import info.zhiqing.spiders.ThePirateBaySpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Created by zhiqing on 17-6-13.
 */

@RestController
public class ApiController {

    private ThePirateBaySpider spider;

    @Autowired
    public ApiController(ThePirateBaySpider spider) {
        this.spider = spider;
    }

    @RequestMapping("/search/{keyword}/{page}")
    public TorrentsResponse search(@PathVariable String keyword, @PathVariable int page) {
        TorrentsResponse response = new TorrentsResponse();
        try {
            List<Torrent> list = spider.search(keyword, page);
            response.setError(false);
            response.setTorrents(list);
        } catch (IOException ioe) {
            response.setError(true);
            response.setErrorMessage(ioe.toString());
        }
        return response;
    }

    @RequestMapping("/browse/{type}/{page}")
    public TorrentsResponse browse(@PathVariable String type, @PathVariable int page) {
        TorrentsResponse response = new TorrentsResponse();
        try {
            List<Torrent> list = spider.browse(type, page);
            response.setError(false);
            response.setTorrents(list);
        } catch (IOException ioe) {
            response.setError(true);
            response.setErrorMessage(ioe.toString());
        }
        return response;
    }

    @RequestMapping("/top/{type}/{page}")
    public TorrentsResponse top(@PathVariable String type, @PathVariable int page) {
        TorrentsResponse response = new TorrentsResponse();
        try {
            List<Torrent> list = spider.top(type, page);
            response.setError(false);
            response.setTorrents(list);
        } catch (IOException ioe) {
            response.setError(true);
            response.setErrorMessage(ioe.toString());
        }
        return response;
    }

    @RequestMapping("/user/{name}/{page}")
    public TorrentsResponse user(@PathVariable String name, @PathVariable int page) {
        TorrentsResponse response = new TorrentsResponse();
        try {
            List<Torrent> list = spider.user(name, page);
            response.setError(false);
            response.setTorrents(list);
        } catch (IOException ioe) {
            response.setError(true);
            response.setErrorMessage(ioe.toString());
        }
        return response;
    }

    @RequestMapping("/torrent/{code}")
    public TorrentDetailResponse detail(@PathVariable String code) {
        TorrentDetailResponse response = new TorrentDetailResponse();
        try {
            TorrentDetail detail = spider.detail(code);
            response.setError(false);
            response.setDetail(detail);
        } catch (IOException ioe) {
            response.setError(true);
            response.setErrorMessage(ioe.toString());
        }
        return response;
    }

}
