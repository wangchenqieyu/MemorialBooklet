package dto;

public class UploadFileResponse {
    private final String cid;
    private final String message;

    public UploadFileResponse(String cid, String message) {
        this.cid = cid;
        this.message = message;
    }

    public String getCid() {
        return cid;
    }

    public String getMessage() {
        return message;
    }

}