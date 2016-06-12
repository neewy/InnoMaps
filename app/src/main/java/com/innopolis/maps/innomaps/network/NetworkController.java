package com.innopolis.maps.innomaps.network;

import android.net.SSLCertificateSocketFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.innopolis.maps.innomaps.maps.LatLngGraphVertex;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static com.innopolis.maps.innomaps.network.Constants.*;


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

    private static String establishPostConnection(String targetURL, String urlParams) {
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
            return new findShortestPathTask().execute(urlParameters).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    private class findShortestPathTask extends AsyncTask<String, Void, List<LatLngGraphVertex>> {
        @Override
        protected List<LatLngGraphVertex> doInBackground(String... params) {
            String response =
                    establishPostConnection(String.format(shortest_path_url,
                            CONNECTION_PROTOCOL, IP, PORT), params[0]);
            ObjectMapper mapper = new ObjectMapper();
            response = response.substring(12, response.length() - 1);
            try {
                return mapper.readValue(response,
                        TypeFactory.defaultInstance().constructCollectionType(List.class,
                                LatLngGraphVertex.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }
}