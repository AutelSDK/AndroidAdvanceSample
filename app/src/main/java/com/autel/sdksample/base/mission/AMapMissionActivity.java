package com.autel.sdksample.base.mission;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.autel.common.flycontroller.AttitudeInfo;
import com.autel.sdksample.R;

import java.util.ArrayList;
import java.util.List;


public class AMapMissionActivity extends MapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMapContentView(R.layout.activity_mission_amap);
    }


    @Override
    protected void phoneLocationChanged(Location location) {
        AutelLatLng all = MapRectifyUtil.wgs2gcj(new AutelLatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    protected void updateAircraftLocation(double lat, double lot, AttitudeInfo info) {
        AutelLatLng latLng = MapRectifyUtil.wgs2gcj(new AutelLatLng(lat, lot));
        LatLng lng = new LatLng(latLng.latitude, latLng.longitude);
    }


    protected ArrayList<Marker> mMarkerList = new ArrayList<>();

    @Override
    public void addWayPointMarker(double lat, double lot) {

    }


    Marker mOrbitMarker;

    @Override
    public void updateOrbit(double lat, double lot) {
        if (null != mOrbitMarker) {
            mOrbitMarker.destroy();
        }

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(lat, lot));
        markerOption.draggable(false);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.favor_marker_point);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

    }

    @Override
    public void resetMap() {
        if (null != mOrbitMarker) {
            mOrbitMarker.destroy();
        }
        if (null != polyLines) {
            for (Polyline line : polyLines) {
                line.remove();
            }
            polyLines.clear();

        }

        if (null != mMarkerList) {
            for (Marker marker : mMarkerList) {
                marker.destroy();
            }
            mMarkerList.clear();
        }
    }

    private List<Polyline> polyLines = new ArrayList<>();


}
