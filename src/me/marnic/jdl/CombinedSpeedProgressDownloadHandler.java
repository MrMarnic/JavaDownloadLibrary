package me.marnic.jdl;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (c) 12.01.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public abstract class CombinedSpeedProgressDownloadHandler extends DownloadHandler{

    private Timer timer;

    public CombinedSpeedProgressDownloadHandler(Downloader downloader) {
        super(downloader);
        timer = new Timer();
    }

    int lastDownloadSize;
    int deltaDownload;
    int percent;

    @Override
    public void onDownloadStart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                deltaDownload = downloader.downloadedBytes-lastDownloadSize;
                onDownloadTickPerSec(deltaDownload);
                lastDownloadSize = downloader.downloadedBytes;
                percent = (int)(((double)downloader.downloadedBytes/downloader.downloadLength)*100);
                onDownloadProgress(downloader.downloadedBytes,downloader.downloadLength,percent);
                onDownloadSpeedProgress(downloader.downloadedBytes,downloader.downloadLength,percent,deltaDownload);
            }
        },0,1000);
    }

    public void onDownloadTickPerSec(int bytesPerSec) {}
    public void onDownloadProgress(int downloaded,int maxDownload,int percent) {}
    public abstract void onDownloadSpeedProgress(int downloaded,int maxDownload,int percent,int bytesPerSec);

    @Override
    public void onDownloadFinish() {
        timer.cancel();
    }

    @Override
    public void onDownloadError() {
        timer.cancel();
    }
}
