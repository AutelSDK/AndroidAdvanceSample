package com.autel.sdksample.evo.mission.util;

import android.graphics.Bitmap;

import com.autel.sdksample.evo.mission.bean.AutelGPSLatLng;
import com.autel.sdksample.evo.mission.bean.MissionOrbitdBean;
import com.autel.sdksample.evo.mission.bean.MissionWaypointBean;
import com.autel.sdksample.evo.mission.bean.OrbitAdvanceDataBean;
import com.autel.sdksample.evo.mission.bean.WaypointAdvanceDataBean;

import java.util.List;


/**
 * Created by A15387 on 2017/9/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface MapModelImpl {
    void resetMapRotate();

    void setOnMapChangeListener(MapModelImpl.OnMapRotateListener onMapRotateListener);

    void rotateMap(float degree);

    void setMapType(int mapType);

    int getMapType();

    void setScaleChangeListener(MapModelImpl.OnScaleChangeListener onScaleChangeListener);

    void setDroneMarker(AutelGPSLatLng autelGPSLatLng, Bitmap droneBitmap);

    void updateDroneYaw(double yaw, boolean isCompassEnable);

    void setHomeMarker(AutelGPSLatLng homeLocation, Bitmap bitmap);

    void setPhoneLocation(AutelGPSLatLng phoneLocation, Bitmap bitmap);

    void initMapToPhoneLocation();

    void changeLocationToDrone();

    void changeLocationToHome();

    void updateFlyRoute(AutelGPSLatLng droneLocation);

    void clearFlyRoute();

    void updateFollowLine();

    void setOnMapClickListener(int mapClickWay);

    boolean setMissionLimitCircle(int circleRadius);

    void removeOrbitPoint();

    void clearOrbitMode();

    boolean isOrbitAdd();

    boolean addOrbitFromDrone(AutelGPSLatLng droneLocation);

    AutelGPSLatLng getOrbitMarkerPosition();

    void clearWaypoints();

    void projectMarker(List<AutelGPSLatLng> path);

    void setWaypointsListener(MapModelImpl.OnWaypointsChangeListener waypointsListener);

    void setMarkerDragListener();

    MissionWaypointBean getMissionWaypointBean();

    void deleteJustProjectWaypoint();

    void addWaypointUseDroneLocation();

    void waypointDelete();

    void setWaypointAdvanceData(WaypointAdvanceDataBean waypointAdvanceData);

    void setCurWaypointDataChange(WaypointAdvanceDataBean waypointAdvanceDataBean, int whichItem);

    void showWaypointFileOnMap(MissionWaypointBean missionWaypointBean);

    MissionOrbitdBean getMissionOrbitBean();

    void setCurOrbitDataChange(OrbitAdvanceDataBean orbitAdvanceDataBean, int whichItem);

    void setWaypointProjectDataChange(WaypointAdvanceDataBean waypointProjectDataChange);

    void saveAlreadyUpdateFlyRoute();

    boolean initMapToNetLocation(AutelGPSLatLng netLocation);

    WaypointAdvanceDataBean getLastWaypointData();

    WaypointAdvanceDataBean getNextWaypointData();

    void clearFollowLine();

    void changeLocationToMe();

    void removeLimitCircle();

    interface OnMapRotateListener {
        void onRotateChange(float degree);
    }

    interface OnScaleChangeListener {
        void onScaleChange(String description, int width);

        void onScaleIdle();
    }
    interface OnWaypointsChangeListener{
        void onWaypointsChange(int num, int totalDistance);
    }
}
