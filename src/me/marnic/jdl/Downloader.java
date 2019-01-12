package me.marnic.jdl;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Copyright (c) 12.01.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class Downloader {

    private DownloadHandler downloadHandler;

    int downloadedBytes;
    int downloadLength;

    public Downloader(boolean createDefaultHandler) {
        if(createDefaultHandler) {
            downloadHandler = new DownloadHandler(this) {
                @Override
                public void onDownloadStart() {

                }

                @Override
                public void onDownloadFinish() {

                }

                @Override
                public void onDownloadError() {

                }
            };
        }
    }

    public void downloadFileToLocation(String urlStr, String pathToDownload) {

        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            downloadLength = con.getContentLength();

            InputStream in = con.getInputStream();
            FileOutputStream out = new FileOutputStream(pathToDownload);

            byte[] data = new byte[1024];
            int length;

            downloadHandler.onDownloadStart();

            while ((length = in.read(data,0,1024))!=-1)  {
                out.write(data,0,length);
                downloadedBytes+=length;
            }

            in.close();
            out.close();

            downloadHandler.onDownloadFinish();
        }catch (Exception e) {
            e.printStackTrace();
            downloadHandler.onDownloadError();
        }
    }

    public Object downloadObject(String urlStr) {

        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            downloadLength = con.getContentLength();

            ObjectInputStream in = new ObjectInputStream(con.getInputStream());

            byte[] data = new byte[1024];
            int length;

            downloadHandler.onDownloadStart();

            while ((length = in.read(data,0,1024))!=-1)  {
                downloadedBytes+=length;
            }

            in.close();

            downloadHandler.onDownloadFinish();
            return in.readObject();
        }catch (Exception e) {
            e.printStackTrace();
            downloadHandler.onDownloadError();
        }
        return null;
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }
}
