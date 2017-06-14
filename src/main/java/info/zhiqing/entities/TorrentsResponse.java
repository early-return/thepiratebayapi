package info.zhiqing.entities;

import java.util.List;

/**
 * Created by zhiqing on 17-6-13.
 */
public class TorrentsResponse {
    boolean error;
    String errorMessage;
    List<Torrent> torrents;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<Torrent> getTorrents() {
        return torrents;
    }

    public void setTorrents(List<Torrent> torrents) {
        this.torrents = torrents;
    }
}
