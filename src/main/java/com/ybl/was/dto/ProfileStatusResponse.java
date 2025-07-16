package com.ybl.was.dto;

public class ProfileStatusResponse {
    private boolean started;
    private String parmPath;

    public ProfileStatusResponse() {
    }

    public ProfileStatusResponse(boolean started, String parmPath) {
        this.started = started;
        this.parmPath = parmPath;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getParmPath() {
        return parmPath;
    }

    public void setParmPath(String parmPath) {
        this.parmPath = parmPath;
    }
}
