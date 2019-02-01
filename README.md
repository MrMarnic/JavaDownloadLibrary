# JavaDownloadLibrary
A java library to download files and process the download speed,progress and other things

Features:

- Download files easy
- Check download speed
- Check download progress
- Directly cast download objects
- Download Text
- Convert file sizes (MB;GB;KB...)

# Download:
https://github.com/MrMarnic/JDL/releases/download/1.0/JDL.jar


# Getting Started:

# Download File:

```
Downloader downloader = new Downloader(false);
downloader.downloadFileToLocation("https://github.com/MrMarnic/JIconExtract/releases/download/1.0/JIconExtract.jar","C:\\Downloads\\download.zip");
```
# Add Handler (Check Speed,progress...):

```
Downloader downloader = new Downloader(false);
downloader.setDownloadHandler(new CombinedSpeedProgressDownloadHandler(downloader) {
            @Override
            public void onDownloadSpeedProgress(int downloaded, int maxDownload, int percent, int bytesPerSec) {
                System.out.println(SizeUtil.toMBFB(bytesPerSec)+"/s " + percent + "%");
            }

            @Override
            public void onDownloadFinish() {
                super.onDownloadFinish();
                System.out.println("Download finished");
            }
        });
downloader.downloadFileToLocation("https://github.com/MrMarnic/JIconExtract/releases/download/1.0/JIconExtract.jar","C:\\Downloads\\download.zi");
```

# Handler

- DownloadSpeedDownloadHandler (check speed)
- DownloadProgressDownloadHandler (check progress)
- CombinedSpeedProgressDownloadHandler (check speed and progress)

# Create own Handler

```
public class ExampleDownloadHandler extends DownloadHandler{

    public DownloadProgressDownloadHandler(Downloader downloader) {
        super(downloader);
    }


    @Override
    public void onDownloadStart() {
      
    }

    @Override
    public void onDownloadFinish() {
        timer.cancel();
    }

    @Override
    public void onDownloadError() {
        timer.cancel();
    }
}
```

# Convert File sizes

Syntax: SizeUtil.toMBFB() = toMegaBytesFromBytes
        SizeUtil.toGBFB() = toGigiaBytesFromBytes



```
double mb = SizeUtil.toMBFB(2000000000);
double kb = SizeUtil.toKBFB(1000000);
```
