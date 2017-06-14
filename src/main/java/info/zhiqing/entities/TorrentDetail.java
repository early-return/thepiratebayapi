package info.zhiqing.entities;

import java.util.Map;

/**
 * Created by zhiqing on 17-6-14.
 */
public class TorrentDetail {
    Map<String, String> info;
    String link;
    String intro;

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
