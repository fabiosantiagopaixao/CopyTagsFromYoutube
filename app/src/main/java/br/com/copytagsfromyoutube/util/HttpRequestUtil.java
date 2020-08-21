package br.com.copytagsfromyoutube.util;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestUtil {

    private Context context;
    private String url;
    private String result;
    private String resultErro;
    private int resultCode;
    public static String GET = "GET";

    /**
     * Connect request
     *
     * @param context
     * @param url
     */
    public HttpRequestUtil(Context context, String url) {
        this.context = context;
        this.url = url;
        this.connect();
    }

    public HttpRequestUtil connect() {
        try {
            // PUT image on S3
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(GET);

            this.resultCode = conn.getResponseCode();
            StringBuilder content = null;
            if (this.resultCode >= 200 && this.resultCode < 300) {
                try {
                    if (conn.getInputStream() != null) {

                        try (BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()))) {
                            String line;
                            content = new StringBuilder();
                            int i = 0;
                            while ((line = in.readLine()) != null) {
                                if (line.contains("\"keywords\":[\"")) {
                                    result = line
                                            .split("keywords")[1]
                                            .split("]")[0]
                                            .replace(":[\"", "")
                                            .replace("[", "")
                                            .replace("\\", "")
                                            .replace("\"", "");
                                    break;
                                }
                                content.append(line);
                                content.append(System.lineSeparator());
                            }
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    this.resultCode = 401;
                }

            } else {
                try {
                    if (conn.getErrorStream() != null) {
                        try (BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getErrorStream()))) {
                            String line;
                            content = new StringBuilder();
                            while ((line = in.readLine()) != null) {
                                content.append(line);
                                content.append(System.lineSeparator());
                            }
                        }
                        this.resultErro = content.toString();
                    }
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    this.resultCode = 401;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public boolean isSucess() {
        return this.getResultCode() >= 200 && this.getResultCode() < 300;
    }

    public String getResult() {
        return result;
    }

    public int getResultCode() {
        return resultCode;
    }


    public String getResultErro() {
        return resultErro;
    }
}
