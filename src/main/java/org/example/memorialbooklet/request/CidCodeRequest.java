package org.example.memorialbooklet.request;

public class CidCodeRequest {
    private String cidCode;

    private Long personId;

    // Getters and Setters
    public String getCidCode() {
        return cidCode;
    }

    public void setCidCode(String cidCode) {
        this.cidCode = cidCode;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}
