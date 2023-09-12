package com.example.delservice;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

public class MainActivity extends FragmentActivity
{
    private MapView mapview;
    private Road road;
    private IMapController mapController;
    private RoadManager roadManager;
    
    ArrayList<Marker> markers_src = new ArrayList<>();
    ArrayList<Marker> markers_dests = new ArrayList<>();

    ArrayList<double[]> srcs = new ArrayList<>();
    ArrayList<double[]> dests = new ArrayList<>();

    Marker.OnMarkerClickListener marker_clk;

    boolean CLICK_ON_MARKER = false;

    Cursor userCursor;
    SQLiteDatabase db;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        DBHelper DB = new DBHelper(this);

        //Инициализация полей для парса данных
        TextView tv_for_num_of_order = findViewById(R.id.tV_for_parse_numoforder);
        TextView tv_for_username = findViewById(R.id.tV_for_parse_username);
        TextView tv_for_weight = findViewById(R.id.tV_for_parse_weight);
        TextView tv_for_profit = findViewById(R.id.tv_for_parse_profit);
        TextView tv_for_first_course = findViewById(R.id.tV_for_parse_first_course);
        TextView tv_for_second_course = findViewById(R.id.tV_for_parse_second_course);
        TextView tv_for_dessert = findViewById(R.id.tV_for_parse_dessert);
        TextView tv_for_drinks = findViewById(R.id.tV_for_parse_drinks);

        Button exit_button = findViewById(R.id.exit_button);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        mapview = (MapView) findViewById(R.id.mapview);

        mapview.setLayerType(View.LAYER_TYPE_HARDWARE, null );
        mapview.setTileSource(TileSourceFactory.MAPNIK);
        mapview.setMultiTouchControls(true);

        mapview.setBuiltInZoomControls(true);

        mapController = mapview.getController();
        mapController.setZoom(12f);

        roadManager = new OSRMRoadManager(this, BuildConfig.APPLICATION_ID);

        SQLiteOpenHelper helper = new DBHelper(this);
        double latitude = 0.0;
        double longitude = 0.0;

        db = helper.getReadableDatabase();

        userCursor = db.rawQuery("select * from orders", null);

        //Добавление маркеров из базы данных на карту
        while(userCursor.moveToNext()) {
            latitude = userCursor.getDouble(userCursor.getColumnIndex("latitude_rest"));
            longitude = userCursor.getDouble(userCursor.getColumnIndex("longitude_rest"));
            srcs.add(new double[]{latitude, longitude});

            latitude = userCursor.getDouble(userCursor.getColumnIndex("latitude_dest"));
            longitude = userCursor.getDouble(userCursor.getColumnIndex("longitude_dest"));
            dests.add(new double[]{latitude, longitude});
        }

        marker_clk = new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                List<Overlay> overlays = mapView.getOverlays();
                for (Overlay overlay : overlays)
                {
                    if ((overlay instanceof Marker)&&
                            (((Marker) overlay).getId().equals("source")))
                    {
                        if (!(marker.equals((Marker)overlay)))
                        {
                            mapView.getOverlays().remove(overlay);
                            mapView.invalidate();
                        }
                        else
                            {
                            int j = markers_src.indexOf(marker);
                            Marker marker_dest = markers_dests.get(j);
                            drawMarker(marker_dest.getPosition(), marker_dest);

                            double latitude_dest = 0.0;
                            double longitude_dest = 0.0;
                            double current_latitude = 0.0;
                            double current_longitude = 0.0;

                            userCursor.moveToFirst();
                            while(userCursor.moveToNext()) {
                                latitude_dest = userCursor.getDouble(userCursor.getColumnIndex("latitude_dest"));
                                longitude_dest = userCursor.getDouble(userCursor.getColumnIndex("longitude_dest"));
                                current_latitude = (marker_dest.getPosition()).getLatitude();
                                current_longitude = (marker_dest.getPosition()).getLongitude();

                                if ((Double.compare(latitude_dest, current_latitude) == 0) &&
                                        (Double.compare(longitude_dest, current_longitude) == 0)) {
                                    tv_for_num_of_order.setText(userCursor.getString(userCursor.getColumnIndex("num_of_order")));
                                    tv_for_num_of_order.invalidate();
                                    tv_for_username.setText(userCursor.getString(userCursor.getColumnIndex("username_of_deliverer")));
                                    tv_for_username.invalidate();
                                    tv_for_weight.setText(userCursor.getString(userCursor.getColumnIndex("weight_of_order")));
                                    tv_for_weight.invalidate();
                                    tv_for_profit.setText(userCursor.getString(userCursor.getColumnIndex("profit")));
                                    tv_for_profit.invalidate();
                                    tv_for_first_course.setText(userCursor.getString(userCursor.getColumnIndex("first_course")));
                                    tv_for_first_course.invalidate();
                                    tv_for_second_course.setText(userCursor.getString(userCursor.getColumnIndex("second_course")));
                                    tv_for_second_course.invalidate();
                                    tv_for_dessert.setText(userCursor.getString(userCursor.getColumnIndex("dessert")));
                                    tv_for_dessert.invalidate();
                                    tv_for_drinks.setText(userCursor.getString(userCursor.getColumnIndex("drinks")));
                                    tv_for_drinks.invalidate();
                                    userCursor.moveToFirst();
                                    break;
                                }
                            }

                            CLICK_ON_MARKER = true;
                            DrawWay task = new DrawWay(
                                    marker.getPosition(),
                                    marker_dest.getPosition(),
                                    road, roadManager, mapview);
                            Thread t = new Thread(task);
                            t.start();
                            while (t.isAlive())
                            {
                                try
                                {
                                    Thread.sleep(200);
                                } catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                MapEventsReceiver mapEventsReceiver = new MapEventsReceiver()
                {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p)
                    {
                        if (CLICK_ON_MARKER == true)
                        {
                            mapview.getOverlays().clear();
                            drawOrdersMarks();
                            CLICK_ON_MARKER = false;
                        }

                        tv_for_num_of_order.setText("Не определен");
                        tv_for_num_of_order.invalidate();
                        tv_for_username.setText("Не определен");
                        tv_for_username.invalidate();
                        tv_for_weight.setText("Не определен");
                        tv_for_weight.invalidate();
                        tv_for_profit.setText("Не определена");
                        tv_for_profit.invalidate();
                        tv_for_first_course.setText("Отсутствуют");
                        tv_for_first_course.invalidate();
                        tv_for_second_course.setText("Отсутствуют");
                        tv_for_second_course.invalidate();
                        tv_for_dessert.setText("Отсутствуют");
                        tv_for_dessert.invalidate();
                        tv_for_drinks.setText("Отсутствуют");
                        tv_for_drinks.invalidate();

                        return false;
                    }
                    @Override
                    public boolean longPressHelper(GeoPoint p)
                    {
                        return false;
                    }
                };
                MapEventsOverlay overlayEvents = new MapEventsOverlay(getBaseContext(), mapEventsReceiver);
                mapview.getOverlays().add(overlayEvents);
                return false;
            }
        };

        drawOrdersMarks();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }

    private void drawOrdersMarks()
    {
        for (int i = 0; i < srcs.size(); i++)
        {
            GeoPoint point_ord = new GeoPoint(srcs.get(i)[0], srcs.get(i)[1]);
            Marker marker_ord = createMarker(point_ord, "red");
            drawMarker(point_ord, marker_ord);
            GeoPoint point_dest = new GeoPoint(dests.get(i)[0], dests.get(i)[1]);
            Marker marker_dest = createMarker(point_dest, "blue");
        }
    }

    private Marker createMarker(GeoPoint position, String color)
    {
        Marker marker = new Marker(mapview);
        switch (color)
        {
            case("red"):
                marker.setIcon(getResources().getDrawable(R.drawable.osm_red_mark));
                marker.setId("source");
                markers_src.add(marker);
                break;
            case("blue"):
                marker.setIcon(getResources().getDrawable(R.drawable.osm_blue_mark));
                marker.setId("destination");
                markers_dests.add(marker);
                break;
            default:
                marker.setIcon(getResources().getDrawable(R.drawable.ic_location_pin));
                break;
        }
        marker.setPosition(position);
        marker.setOnMarkerClickListener(marker_clk);
        return marker;
    }

    private void drawMarker(GeoPoint point, Marker marker)
    {
        marker.setPosition(point);
        mapview.getOverlays().add(marker);
        mapController.animateTo(point);
        mapview.invalidate();
    }

    class DrawWay implements Runnable
    {

       private GeoPoint start;
       private GeoPoint end;
       private Road road;
       private RoadManager roadManager;
       private MapView map;

       public DrawWay(@NonNull GeoPoint start, @NonNull GeoPoint end, @NonNull Road road, @NonNull RoadManager roadManager, @NonNull MapView map)
       {
            this.start = start;
            this.end = end;
            this.road = road;
            this.roadManager = roadManager;
            this.map = map;
       }

        @Override
        public void run()
        {
            ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
            waypoints.add(start);
            waypoints.add(end);
            try
            {
                road = roadManager.getRoad(waypoints);
                if (road.mStatus != Road.STATUS_OK)
                {
                    //handle error... warn the user, etc.
                    Toast.makeText(getApplicationContext(), "Error with road (Check for Road.STATUS_OK)!", Toast.LENGTH_SHORT).show();
                }
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                roadOverlay.setId("road");
                map.getOverlays().add(roadOverlay);
                map.invalidate();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}