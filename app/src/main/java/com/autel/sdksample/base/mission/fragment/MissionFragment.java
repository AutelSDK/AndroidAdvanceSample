package com.autel.sdksample.base.mission.fragment;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autel.common.mission.AutelMission;
import com.autel.sdksample.R;
import com.autel.sdksample.base.mission.MapActivity;
import com.autel.sdksample.base.mission.MapOperator;


public abstract class MissionFragment extends Fragment {
    protected MapOperator mMapOperator;

    public MissionFragment() {
    }
    public MissionFragment(MapOperator mMapOperator) {
        super();
        this.mMapOperator = mMapOperator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = createView(R.layout.fragment_mission_menu);
        return view;
    }


    protected View createView(@LayoutRes int resource) {
        View view = View.inflate(getContext(), resource, null);
        initUi(view);
        return view;
    }

    private void initUi(View view) {
        if (getActivity() != null) {
            ((MapActivity) getActivity()).updateMissionInfo("Mission state : ");
            ((MapActivity) getActivity()).updateLogInfo("RealTimeInfo : ");
        }
    }

    public abstract AutelMission createAutelMission();

    public abstract void onMapClick(double lat, double lot);
    public abstract void onMarkerClick(int position);

    public void onDestroy() {
        super.onDestroy();
    }
    protected boolean isEmpty(String value) {
        return null == value || "".equals(value);
    }
}
