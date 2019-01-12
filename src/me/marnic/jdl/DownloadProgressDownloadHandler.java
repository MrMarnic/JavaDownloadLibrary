package me.marnic.jdl;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (c) 12.01.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public abstract class DownloadProgressDownloadHandler extends DownloadHandler{

    private Timer timer;

    public DownloadProgressDownloadHandler(Downloader downloader) {
        super(downloader);
        timer = new Timer();
    }


    @Override
    public void onDownloadStart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onDownloadProgress(downloader.downloadedBytes,downloader.downloadLength,(int)(((double)downloader.downloadedBytes/downloader.downloadLength)*100));
            }
        },0,1000);
    }

    public abstract void onDownloadProgress(int downloaded,int maxDownload,int percent);

    @Override
    public void onDownloadFinish() {
        timer.cancel();
    }

    @Override
    public void onDownloadError() {
        timer.cancel();
    }
}
