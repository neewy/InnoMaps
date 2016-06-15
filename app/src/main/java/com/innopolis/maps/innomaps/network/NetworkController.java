package com.innopolis.maps.innomaps.network;

import android.net.SSLCertificateSocketFactory;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.maps.LatLngGraphVertex;
import com.innopolis.maps.innomaps.network.tasks.FindClosestPointFromGraphTask;
import com.innopolis.maps.innomaps.network.tasks.FindShortestPathTask;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static com.innopolis.maps.innomaps.network.Constants.CONTENT_TYPE;
import static com.innopolis.maps.innomaps.network.Constants.CONTENT_TYPE_VALUE;
import static com.innopolis.maps.innomaps.network.Constants.ENCODING;
import static com.innopolis.maps.innomaps.network.Constants.LOG;
import static com.innopolis.maps.innomaps.network.Constants.PARAMETER_DELIMITER;
import static com.innopolis.maps.innomaps.network.Constants.PARAMETER_EQUALS_CHAR;
import static com.innopolis.maps.innomaps.network.Constants.POST;
import static com.innopolis.maps.innomaps.network.Constants.VERTEX_ONE_FLR;
import static com.innopolis.maps.innomaps.network.Constants.VERTEX_ONE_LAT;
import static com.innopolis.maps.innomaps.network.Constants.VERTEX_ONE_LNG;
import static com.innopolis.maps.innomaps.network.Constants.VERTEX_TWO_LAT;
import static com.innopolis.maps.innomaps.network.Constants.VERTEX_TWO_LNG;


@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkController {

    private static String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                try {
                    parametersAsQueryString.append(parameterName)
                            .append(PARAMETER_EQUALS_CHAR)
                            .append(URLEncoder.encode(parameters.get(parameterName), ENCODING));
                } catch (UnsupportedEncodingException e) {
                    Log.e(LOG, e.getMessage(), e.fillInStackTrace());
                }

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }

    public static String establishGetConnection(String urlString) {
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) connection;
                httpsConn.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                httpsConn.setHostnameVerifier(new AllowAllHostnameVerifier());
            }
            InputStream is = connection.getInputStream();

            return IOUtils.toString(is, ENCODING);

        } catch (IOException e) {
            Log.e(Constants.LOG, e.getMessage());
        }
        return null;
    }

    public static String establishPostConnection(String targetURL, String urlParams) {
        URL url;
        HttpsURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpsURLConnection) url.openConnection();

            connection.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
            connection.setHostnameVerifier(new AllowAllHostnameVerifier());

            if (urlParams != null) {
                connection.setRequestMethod(POST);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty(CONTENT_TYPE, CONTENT_TYPE_VALUE);

                connection.connect();

                //send the POST out
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParams);
                dStream.flush();

                int status = connection.getResponseCode();

                BufferedReader br;
                if (status >= HttpURLConnection.HTTP_BAD_REQUEST)
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                else
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                String response = responseOutput.toString();
                br.close();
                dStream.close();
                return response;
            }

        } catch (IOException e) {
            Log.e(LOG, e.getMessage(), e.fillInStackTrace());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public List<LatLngGraphVertex> findShortestPath(String vertexOneLatitude, String vertexOneLongitude, String vertexOneFloor,
                                                    String vertexTwoLatitude, String vertexTwoLongitude) throws UnsupportedEncodingException {
        Map<String, String> urlParametersMap = new HashMap<>();
        urlParametersMap.put(VERTEX_ONE_LAT, vertexOneLatitude);
        urlParametersMap.put(VERTEX_ONE_LNG, vertexOneLongitude);
        urlParametersMap.put(VERTEX_ONE_FLR, vertexOneFloor);
        urlParametersMap.put(VERTEX_TWO_LAT, vertexTwoLatitude);
        urlParametersMap.put(VERTEX_TWO_LNG, vertexTwoLongitude);
        String urlParameters = createQueryStringForParameters(urlParametersMap);

        try {
            return new FindShortestPathTask().execute(urlParameters).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    public LatLngFlr findClosestPointFromGraph(double latitude, double longitude, int floor) {
        try {
            return new FindClosestPointFromGraphTask().execute(String.valueOf(latitude), String.valueOf(longitude), String.valueOf(floor)).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }
}