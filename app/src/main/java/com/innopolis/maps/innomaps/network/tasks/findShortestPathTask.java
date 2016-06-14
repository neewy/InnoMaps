package com.innopolis.maps.innomaps.network.tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.innopolis.maps.innomaps.maps.LatLngGraphVertex;
import com.innopolis.maps.innomaps.network.NetworkController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.innopolis.maps.innomaps.network.Constants.CONNECTION_PROTOCOL;
import static com.innopolis.maps.innomaps.network.Constants.IP;
import static com.innopolis.maps.innomaps.network.Constants.PORT;
import static com.innopolis.maps.innomaps.network.Constants.shortest_path_url;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class findShortestPathTask extends AsyncTask<String, Void, List<LatLngGraphVertex>> {
    @Override
    protected List<LatLngGraphVertex> doInBackground(String... params) {
        String response =
                NetworkController.establishPostConnection(String.format(shortest_path_url,
                        CONNECTION_PROTOCOL, IP, PORT), params[0]);
        ObjectMapper mapper = new ObjectMapper();
        if(response != null)
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