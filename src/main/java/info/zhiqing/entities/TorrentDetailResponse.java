package info.zhiqing.entities;

/**
 * Created by zhiqing on 17-6-14.
 */
public class TorrentDetailResponse {
    boolean error;
    String errorMessage;
    TorrentDetail detail;

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

    public TorrentDetail getDetail() {
        return detail;
    }

    public void setDetail(TorrentDetail detail) {
        this.detail = detail;
    }
}
