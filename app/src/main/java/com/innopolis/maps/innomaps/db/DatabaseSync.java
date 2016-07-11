package com.innopolis.maps.innomaps.db;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EdgeTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomTypeDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EdgeType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.network.InternetAccessChecker;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.TypesSync;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/11/16.
 */
public class DatabaseSync extends IntentService {

    private static Context context;
    private SharedPreferences sPref;
    private NetworkController networkController;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DatabaseSync(Context context) {
        super("DatabaseSync");
        DatabaseSync.context = context;
    }

    public DatabaseSync() {
        super("DatabaseSync");
        DatabaseSync.context = MainActivity.getInstance().getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 1) { // code if connected
                    try {
                        performSyncWithServer();
                    } catch (ParseException e) {
                        Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
                    }
                    saveLastSyncDate(new Date());
                }
            }
        };

        InternetAccessChecker.isNetworkAvailable(h, Constants.INTERNET_CHECK_TIMEOUT, getApplicationContext());
        Log.d(Constants.SYNC, Constants.SYNC_FINISHED_ON + com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(new Date()));

        while (true) {
            final Handler finalHandler = h;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Actions to do after SYNCHRONIZATION_INTERVAL
                    InternetAccessChecker.isNetworkAvailable(finalHandler, Constants.INTERNET_CHECK_TIMEOUT, getApplicationContext());
                    Log.d(Constants.SYNC, Constants.SYNC_FINISHED_ON + com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(new Date()));
                }
            }, Constants.SYNCHRONIZATION_INTERVAL);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void performSyncWithServer() throws ParseException {
        synchronizeTypes();
    }

    private void synchronizeTypes() throws ParseException {
        networkController = new NetworkController();
        TypesSync receivedTypesSync = networkController.getTypesModifiedOnOrAfterDate(loadLastSyncDate());

        if (receivedTypesSync.getCoordinateTypeIds() != null && !receivedTypesSync.getCoordinateTypeIds().isEmpty()) {
            try {
                addNewCoordinateTypes(receivedTypesSync.getCoordinateTypeIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedTypesSync.getEdgeTypeIds() != null && !receivedTypesSync.getEdgeTypeIds().isEmpty()) {
            try {
                addNewEdgeTypes(receivedTypesSync.getEdgeTypeIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedTypesSync.getRoomTypeIds() != null && !receivedTypesSync.getRoomTypeIds().isEmpty()) {
            try {
                addNewRoomTypes(receivedTypesSync.getRoomTypeIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }
    }

    private void addNewCoordinateTypes(List<Integer> coordinateTypeIds) throws ParseException {
        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(context);
        networkController = new NetworkController();
        for (Integer coordinateTypeId : coordinateTypeIds) {
            if (coordinateTypeId != null) {
                CoordinateType newCoordinateType = networkController.getCoordinateTypeById(coordinateTypeId);
                coordinateTypeDAO.create(newCoordinateType);
            }
        }
    }

    private void addNewEdgeTypes(List<Integer> edgeTypeIds) throws ParseException {
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(context);
        networkController = new NetworkController();
        for (Integer edgeTypeId : edgeTypeIds) {
            if (edgeTypeId != null) {
                EdgeType newEdgeType = networkController.getEdgeTypeById(edgeTypeId);
                edgeTypeDAO.create(newEdgeType);
            }
        }
    }

    private void addNewRoomTypes(List<Integer> roomTypeIds) throws ParseException {
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(context);
        networkController = new NetworkController();
        for (Integer roomTypeId : roomTypeIds) {
            if (roomTypeId != null) {
                RoomType newRoomType = networkController.getRoomTypeById(roomTypeId);
                roomTypeDAO.create(newRoomType);
            }
        }
    }

    public void saveLastSyncDate(Date lastSyncDate) {
        sPref = context.getSharedPreferences(Constants.SYNC, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(Constants.LAST + Constants.TYPES + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
        ed.apply();
    }

    private Date loadLastSyncDate() throws ParseException {
        sPref = context.getSharedPreferences(Constants.SYNC, MODE_PRIVATE);
        String lastSyncDate = sPref.getString(Constants.LAST + Constants.TYPES + Constants.SYNC_DATE, "");

        if ("".equals(lastSyncDate))
            lastSyncDate = Constants.DEFAULT_SYNC_DATE;

        return com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(lastSyncDate);
    }

}