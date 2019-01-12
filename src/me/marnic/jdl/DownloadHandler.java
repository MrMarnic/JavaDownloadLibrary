package me.marnic.jdl;

/**
 * Copyright (c) 12.01.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public abstract class DownloadHandler {

    protected Downloader downloader;

    public DownloadHandler(Downloader downloader) {
        this.downloader = downloader;
    }

    public abstract void onDownloadStart();
    public abstract void onDownloadFinish();
    public abstract void onDownloadError();
}
