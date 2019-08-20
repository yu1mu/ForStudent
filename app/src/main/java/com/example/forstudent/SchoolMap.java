package com.example.forstudent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchoolMap extends SupportMapFragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Geocoder geocoder;
    MainActivity main = (MainActivity)getActivity();
    Button testbutton;
    double latitude;
    double longitude;
    String schoolName;
    private String m_Text = "";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        geocoder = new Geocoder(activity);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        MainActivity main = (MainActivity)getActivity();
        testbutton = (Button)getView().findViewById(R.id.testbutton);
        schoolName= main.getUser().name;
        System.out.println(schoolName);
        main.centerToolbarTitle.setText("학교 지도");
        main.toolbar.setTitle("");
        main.invalidateOptionsMenu();
        main.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.FragmentRemove(SchoolMap.this);
            }
        });

        List<Address> addressList = null;
        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder.getFromLocationName(
                    schoolName, // 주소
                    1); // 최대 검색 결과 개수
            latitude = addressList.get(0).getLatitude();
            longitude = addressList.get(0).getLongitude();
        }
        catch (IOException e) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng SCHOOL = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SCHOOL);
        System.out.println(SCHOOL.toString());
        markerOptions.title(schoolName);
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SCHOOL,17));


    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_assignment,menu);
    }

}