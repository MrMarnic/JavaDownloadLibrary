# JavaDownloadLibrary
A Java library to download files and process the download speed, progress and other things

Features:

- Download files easy
- Check download speed
- Check download progress
- Directly cast download objects
- Download Text
- Use (Netscape format) cookies to download files that require login, optionally filter which domain name to load (to check if the cookie file contains the domain name that the programme expects)
- Able to cancel during download
- Get JSON (e.g. from a website API) as String objects
- Convert file sizes from bytes to KB, MB, GB dynamically

# Download:
https://github.com/MrMarnic/JavaDownloadLibrary/releases/

# Example Usage:
**Simple download, with auto filename retrieval (without cookies/progress/speed/custom filename/custom user agent string)**  
Note that a '\\' is expected at the end of the file path
```
Downloader downloader = new Downloader(true);
downloader.downloadFileToLocation("https://github.com/MrMarnic/JIconExtractReloaded/releases/download/v1.0/JIconExtractReloaded.jar", "C:\\demo\\test downloads\\");
```

**Download with file size preview, progress and speed display**
```
Downloader downloader = new Downloader(false);
downloader.setDownloadHandler(new CombinedSpeedProgressDownloadHandler(downloader) {
            @Override
            public void onDownloadStart() {
                super.onDownloadStart();
                // define custom actions to do before download starts
                System.out.println("Download starting...");
            }
            @Override
            public void onDownloadSpeedProgress(int downloaded, int maxDownload, int percent, int bytesPerSec) {
                // define actions to do on each progress update
                // (by default updates once every 250ms as defined in CombinedSpeedProgressDownloadHandler's onDownloadStart())
                System.out.println(SizeUtil.toHumanReadableFromBytes(bytesPerSec) + "/s " + percent + "%");
            }
            @Override
            public void onDownloadFinish() {
                super.onDownloadFinish();
                // define custom actions to do after download finishes
                System.out.println("Download finished");
            }
        });
System.out.println(SizeUtil.toHumanReadableFromBytes(downloader.getDownloadLength("https://github.com/MrMarnic/JIconExtractReloaded/releases/download/v1.0/JIconExtractReloaded.jar")));
downloader.downloadFileToLocation("https://github.com/MrMarnic/JIconExtractReloaded/releases/download/v1.0/JIconExtractReloaded.jar", "C:\\demo\\test downloads\\");
```

**Download files that require login using cookies, with custom file name**
```
Downloader downloader = new Downloader(true);
downloader.setCookies(new File("C:\\demo\\cookies-github-com.txt"));
downloader.downloadFileToLocation("https://github.com/settings/profile", "C:\\demo\\test downloads\\", "GitHub Settings page.html");
```

**Use custom agent string to pass through sites that block Java**
```
Downloader downloader = new Downloader(true);
downloader.setCustomUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
downloader.downloadFileToLocation("https://www.whatismybrowser.com/detect/what-is-my-user-agent/", "C:\\demo\\test downloads\\", "UserAgentTest.html");
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

**Explaination:**  
SizeUtil.toMBFB() = toMegaBytesFromBytes  
SizeUtil.toGBFB() = toGigiaBytesFromBytes  
SizeUtil.toHumanReadableFromBytes() = convert bytes to KB, MB, GB (e.g. for displaying download size / download speed);  

**Usage**
```
double mb = SizeUtil.toMBFB(2000000000);
double kb = SizeUtil.toKBFB(1000000);
String size = SizeUtil.toHumanReadableFromBytes(4096);
```
