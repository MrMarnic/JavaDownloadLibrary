package me.marnic.jdl;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (c) 12.01.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public abstract class DownloadSpeedDownloadHandler extends DownloadHandler{

    private Timer timer;

    public DownloadSpeedDownloadHandler(Downloader downloader) {
        super(downloader);
        timer = new Timer();
    }

    int lastDownloadSize;
    int deltaDownload;

    @Override
    public void onDownloadStart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                deltaDownload = downloader.downloadedBytes-lastDownloadSize;
                onDownloadTickPerSec(deltaDownload);
                lastDownloadSize = downloader.downloadedBytes;
            }
        },0,1000);
    }

    public abstract void onDownloadTickPerSec(int bytesPerSec);

    @Override
    public void onDownloadFinish() {
        timer.cancel();
    }

    @Override
    public void onDownloadError() {
        timer.cancel();
    }
}
