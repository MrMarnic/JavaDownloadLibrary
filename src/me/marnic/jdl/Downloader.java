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
    private boolean bUseCookies = false;
    private boolean bUseDomainFilter = false;
    private String domainFilter;
    private boolean bIsCancelled = false;
    private boolean bUseCustomUserAgentString = false;
    private String customUserAgentString;

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

    public void setCookies(File cookiesFile) {
        this.bUseCookies = true;
        this.cookiesMap = readCookies(cookiesFile);
    }

    public void setDomainFilter(String domainFilter) {
        this.bUseDomainFilter = true;
        this.domainFilter = domainFilter;
    }

    public void setCustomUserAgentString(String userAgentString) {
        this.bUseCustomUserAgentString = true;
        this.customUserAgentString = userAgentString;
    }

    // get HashMap formatted cookies that have finished reading from a txt file
    public HashMap<String, String> getCookiesMap() {
        return cookiesMap;
    }

    // read HashMap formatted cookies from txt file for use with libraries like jsoup
    public HashMap<String, String> readCookies(File cookiesFile) {
        try {
            Scanner myReader = new Scanner(cookiesFile);
            String data;
            List<String> partList;
            HashMap<String, String> result = new HashMap<>();
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
                // skip comment and empty lines
                if (!data.contains("#") && !data.trim().isEmpty()) {
                    partList = Arrays.asList(data.split("\\s+")); // split line as array by space
                    if (bUseDomainFilter) {
                        if (partList.get(0).contains(this.domainFilter)) {
                            result.put(partList.get(5), partList.get(6)); // name and value fields in Netscape format cookies
                        }
                    } else {
                        result.put(partList.get(5), partList.get(6));
                    }
                }
            }
            myReader.close();
            return result;
        } catch (FileNotFoundException e) {
            System.err.println("Cookies not found");
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
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            System.err.println("An error occurred.");
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

    public String getServerFileName(String urlStr) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlStr);
            if (bUseCookies) {
                con = (HttpURLConnection) setupConnectionWithCookies(cookiesMap, urlStr);
            } else {
                con = (HttpURLConnection) url.openConnection();
            }
            if (bUseCustomUserAgentString) {
                con.addRequestProperty("User-Agent", customUserAgentString);
            }
            con.connect();

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("Server returned HTTP " + con.getResponseCode() + " " + con.getResponseMessage());
                return "error";
            }
        } catch (Exception e) {
            System.err.println("An error occurred.");
            return "error";
        } finally {
            if (con != null) con.disconnect();
        }

        String filename;
        try {
            if (con.getHeaderField("Content-Disposition") != null) {
                filename = con.getHeaderField("Content-Disposition").replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
            } else {
                filename = Paths.get(new URI(con.getURL().toString()).getPath()).getFileName().toString();
            }
            return filename;
        }
        catch (Exception e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    public void setbIsCancelled(boolean bIsCancelled) {
        this.bIsCancelled = bIsCancelled;
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

            if (bUseCustomUserAgentString) {
                con.addRequestProperty("User-Agent", customUserAgentString);
            }
            con.connect();
            downloadLength = con.getContentLength();
            return downloadLength;
        } catch (Exception e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    public void downloadFileToLocation(String urlStr, String pathToDownload) {
        handleDownloadFileToLocation(urlStr, pathToDownload, null);
    }

    public void downloadFileToLocation(String urlStr, String pathToDownload, String customFileName) {
        handleDownloadFileToLocation(urlStr, pathToDownload, customFileName);
    }

    private void handleDownloadFileToLocation(String urlStr, String pathToDownload, String customFileName) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con;
            if (bUseCookies) {
                con = (HttpURLConnection) setupConnectionWithCookies(cookiesMap, urlStr);
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            if (bUseCustomUserAgentString) {
                con.addRequestProperty("User-Agent", customUserAgentString);
            }
            con.connect();
            downloadLength = con.getContentLength();

            InputStream in = con.getInputStream();
            String fileName;
            if (customFileName != null) {
                fileName = customFileName;
            } else {
                fileName = getServerFileName(urlStr);
            }
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

            resetValues(); // for repeated use of the same downloader object, in order to get correct download speed values
            downloadHandler.onDownloadFinish();
        } catch (Exception e) {
            System.err.println("An error occurred.");
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

            if (bUseCustomUserAgentString) {
                con.addRequestProperty("User-Agent", customUserAgentString);
            }
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
            System.err.println("No Internet available");
            e.printStackTrace();
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
            System.err.println("An error occurred.");
            e.printStackTrace();
            downloadHandler.onDownloadError();
        }
        return null;
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }
}
