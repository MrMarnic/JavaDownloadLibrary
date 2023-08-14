package me.marnic.jdl;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Copyright (c) 12.01.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class Downloader {
    private DownloadHandler downloadHandler;
    int downloadedBytes;
    int downloadLength;
    private HashMap<String, String> cookiesMap;
    private Boolean bUseCookies = false;
    private String cookiesLocation;
    private String domainFilter;
    private boolean bIsCancelled = false;

    public Downloader(boolean createDefaultHandler) {
        if (createDefaultHandler) {
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

    public Downloader(boolean createDefaultHandler, String cookiesLocation) {
        if (createDefaultHandler) {
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
        this.bUseCookies = true;
        this.cookiesLocation = cookiesLocation;
        this.cookiesMap = readCookies();
    }

    public Downloader(boolean createDefaultHandler, String cookiesLocation, String domainFilter) {
        if (createDefaultHandler) {
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
        this.bUseCookies = true;
        this.cookiesLocation = cookiesLocation;
        this.domainFilter = domainFilter;
        this.cookiesMap = readCookies();
    }

    public HashMap<String, String> getCookiesMap() {
        return cookiesMap;
    }

    public HashMap<String, String> readCookies() {
        try {
            File myObj = new File(this.cookiesLocation);
            Scanner myReader = new Scanner(myObj);
            String data;
            List<String> partList;
            HashMap<String, String> result = new HashMap<>();
            boolean bHasDomainFilter = (domainFilter != null) && !domainFilter.isEmpty();
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
                // skip comment and empty lines
                if (!data.contains("#") && !data.trim().isEmpty() && (bHasDomainFilter ? data.contains(this.domainFilter) : true)) {
                    partList = Arrays.asList(data.split("\\s+")); // split line as array by space
                    result.put(partList.get(5), partList.get(6)); // name and key columns in netscape format cookies
                }
            }
            myReader.close();
            return result;
        } catch (FileNotFoundException e) {
            System.out.println("Cookies not found");
            e.printStackTrace();
        }
        return null;
    }

    private URLConnection setupConnectionWithCookies(HashMap<String, String> cookiesMap, String urlStr) {
        URLConnection connection = null;
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        String cookies = "";

        for (Map.Entry<String, String> entry : cookiesMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            cookies += key + "=" + value + "; ";
            connection.addRequestProperty("Cookie", cookies);
        }
        return connection;
    }

    public String getServerFileName(String urlStr) throws UnsupportedEncodingException {
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlStr);
            if (bUseCookies) {
                con = (HttpURLConnection) setupConnectionWithCookies(cookiesMap, urlStr);
            } else {
                con = (HttpURLConnection) url.openConnection();
            }
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
            con.connect();

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + con.getResponseCode() + " " + con.getResponseMessage();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            return e.toString();
        } finally {
            if (con != null) con.disconnect();
        }

        String decodedURL = URLDecoder.decode(con.getURL().toString(), String.valueOf(StandardCharsets.UTF_8)); // for Chinese
        // characters
        return new File(decodedURL).getName();
    }

    public void cancelDownload() {
        bIsCancelled = true;
    }

    private void resetValues() {
        downloadedBytes = 0;
        downloadLength = 0;
    }

    public Integer getDownloadLength(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con;
            if (bUseCookies) {
                con = (HttpURLConnection) setupConnectionWithCookies(cookiesMap, urlStr);
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
            con.connect();
            downloadLength = con.getContentLength();
            return downloadLength;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadFileToLocation(String urlStr, String pathToDownload) {

        try {
            URL url = new URL(urlStr);
            HttpURLConnection con;
            if (bUseCookies) {
                con = (HttpURLConnection) setupConnectionWithCookies(cookiesMap, urlStr);
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
            con.connect();
            downloadLength = con.getContentLength();

            InputStream in = con.getInputStream();
            String fileName = getServerFileName(urlStr);
            Files.createDirectories(Paths.get(pathToDownload));
            FileOutputStream out = new FileOutputStream(pathToDownload + fileName);

            byte[] data = new byte[1024];
            int length;

            downloadHandler.onDownloadStart();

            while (!bIsCancelled && (length = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, length);
                downloadedBytes += length;
            }

            out.flush();
            in.close();
            out.close();

            resetValues(); // for repeated use of the same downloader object, in order to get correct download speed
            // values
            downloadHandler.onDownloadFinish();
        } catch (Exception e) {
            e.printStackTrace();
            downloadHandler.onDownloadError();
        }
    }

    public String downloadJSONString(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con;
            if (bUseCookies) {
                con = (HttpURLConnection) setupConnectionWithCookies(cookiesMap, urlStr);
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
            con.connect();
            downloadLength = con.getContentLength();

            InputStream in = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("No Internet");
        } catch (Exception e) {
            e.printStackTrace();
            downloadHandler.onDownloadError();
        }
        return null;
    }

    // This method never worked anyway
    public Object downloadObject(String urlStr) {

        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            downloadLength = con.getContentLength();

            ObjectInputStream in = new ObjectInputStream(con.getInputStream());

            byte[] data = new byte[1024];
            int length;

            downloadHandler.onDownloadStart();

            while ((length = in.read(data, 0, 1024)) != -1) {
                downloadedBytes += length;
            }

            in.close();

            downloadHandler.onDownloadFinish();
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            downloadHandler.onDownloadError();
        }
        return null;
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }
}
