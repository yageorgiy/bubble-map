package com.yageorgiy.bubblemap.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.yageorgiy.bubblemap.BubbleMapApplication;
import com.yageorgiy.bubblemap.GetRandomMapNodesQuery;
import com.yageorgiy.bubblemap.MainActivity;
import com.yageorgiy.bubblemap.R;
import com.yageorgiy.bubblemap.map.objects.Bubble;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class BubbleMap extends View {

    Paint paint = new Paint();

    private float x = 0;
    private float y = 0;

    private float startTouchX = 0;
    private float startTouchY = 0;

    private float mapBeforeTouchX = 0;
    private float mapBeforeTouchY = 0;

    private HashMap<String, ArrayList<Bubble>> bubbles = new HashMap<>();
    private HashMap<String, Boolean> __cache_bubbles = new HashMap<>();
    private Context context;
    private Activity activity;

    private boolean moved = false;

    private boolean isMakingRequest = false;

    public BubbleMap(final Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;

        /* For test */

        this.requireNewNodes();
/*
        this.addBubble(new Bubble(
                0,
                0,
                "!!!!!!!!!!!!",
                "https://ru.lipsum.com/images/banners/black_250x250.gif",
                null
        ));

 */

        /*


        this.addBubble(new Bubble(
                500,
                0,
                "!!!!!!!!!!!!",
                "https://ru.lipsum.com/images/banners/black_250x250.gif"
        ));

        this.addBubble(new Bubble(
                0,
                500,
                "!!!!!!!!!!!!",
                "https://ru.lipsum.com/images/banners/black_250x250.gif"
        ));

        this.addBubble(new Bubble(
                500,
                500,
                "!!!!!!!!!!!!",
                "https://ru.lipsum.com/images/banners/black_250x250.gif"
        ));
         */




        final BubbleMap map = this;

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();

                //TODO: если массив Math.round координат пустой и запрос не выполняется, то грузим новую партию по координатам (в кубе координат) Math.round

                switch (motionEvent.getAction()){
                    // Start touching
                    case MotionEvent.ACTION_DOWN:
                        map.setStartTouchX(x);
                        map.setStartTouchY(y);

                        map.setMapBeforeTouchX(getX());
                        map.setMapBeforeTouchY(getY());

//                        map.setMoved(false);

                        break;

                    // End touching
                    case MotionEvent.ACTION_UP:

                        if(map.getMapBeforeTouchX() == map.getX() && map.getMapBeforeTouchY() == map.getY()){
//                            Toast.makeText(context, "Нажато", Toast.LENGTH_LONG).show();

                            //TODO: optimization

                            Integer true_coords_x = Math.round(Math.round(map.getX()) + x);
                            Integer true_coords_y = Math.round(Math.round(map.getY()) + y);

                            Integer _x = Math.round(true_coords_x / 500) * 500;
                            Integer _y = Math.round(true_coords_y / 500) * 500;

//                            Toast.makeText(map.context, "x: "+x+"; y"+y+"; map.x: "+map.getX()+"; map.y: "+map.getY(), Toast.LENGTH_LONG).show();
                            if(map.getBubbles().get(_x+"x"+_y) == null) {
                                break;
                            }

                            Integer found = -1;

                            //TODO: clear the mess (!!!)

                            synchronized (bubbles.values()) {
                                for (ArrayList<Bubble> arr_b : bubbles.values()) {
                                    for(Bubble b : arr_b){
                                        if(Math.round(motionEvent.getX()) + 128 >=  map.getX() + Math.round(b.getX()) &&
                                                Math.round(motionEvent.getX()) <=   map.getX() + Math.round(b.getX()) + 128 &&
                                                Math.round(motionEvent.getY()) + 128 >= map.getY() + Math.round(b.getY()) &&
                                                Math.round(motionEvent.getY()) <= map.getY() + Math.round(b.getY()) + 128){
                                            if(b.getNode() != null)
                                            found = Integer.parseInt(b.getNode().id());
                                        }
                                    }
                                }
                            }



                            if(found == -1){
//                                Toast.makeText(map.context, "Not found x: "+true_coords_x+"; y"+true_coords_y+"; map.x: "+map.getX()+"; map.y: "+map.getY(), Toast.LENGTH_LONG).show();
                                break;
                            }


//                            Toast.makeText(map.context, "Found: "+found, Toast.LENGTH_LONG).show();

                            Bundle bundle = new Bundle();
                            bundle.putInt("id", found);

                            ((MainActivity)map.activity).navController
                                    .navigate(R.id.nav_view_post, bundle);


                        }

//                        map.setMoved(false);
                        break;

                    // Moving
                    case MotionEvent.ACTION_MOVE:
                        map.setX(getMapBeforeTouchX() + (x - getStartTouchX()));
                        map.setY(getMapBeforeTouchY() + (y - getStartTouchY()));
                        map.invalidate();
                        map.requireNewNodes();

//                        map.setMoved(true);

                        break;

                    // Do nothing?
                    case MotionEvent.ACTION_BUTTON_PRESS:

                        break;

                }

                return true;
            }
        };

        // Register onTouchListener object to drawBallView.
        this.setOnTouchListener(onTouchListener);

    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void requireNewNodes(){

        Integer _tmp_x = Math.round(this.getX() / 500) * 500;
        Integer _tmp_y = Math.round(this.getY() / 500) * 500;

        if(this.bubbles.get(_tmp_x+"x"+_tmp_y) != null || this.isMakingRequest) return;
        if(this.bubbles.get(_tmp_x+"x"+_tmp_y) != null && this.bubbles.get(_tmp_x+"x"+_tmp_y).size() > 10) return;


        this.isMakingRequest = true;

        final BubbleMap _this = this;

        ((BubbleMapApplication)activity.getApplication()).apolloClient().query(
                GetRandomMapNodesQuery.builder()
                        .build()
        ).enqueue(new ApolloCall.Callback<GetRandomMapNodesQuery.Data>() {

            @Override
            public void onResponse(@NotNull Response<GetRandomMapNodesQuery.Data> dataResponse) {


                if(dataResponse.hasErrors()){
                    Toast.makeText(context, "Ошибка: " + dataResponse.getErrors().get(0), Toast.LENGTH_LONG).show();
                    _this.isMakingRequest = false;
                    return;
                }

                for (GetRandomMapNodesQuery.MapNode node : dataResponse.getData().mapNodes()) {

                    boolean cond = true;

                    int x = 0;
                    int y = 0;

                    double rand_x = 0;
                    double rand_y = 0;

                    int finalx = 0;
                    int finaly = 0;

                    //TODO: optimization
                    while(cond){

                        Integer pos = 0;

                        if(bubbles.get(x+"x"+y) != null){
                            pos = bubbles.get(x+"x"+y).size();
                        }


                        x = Math.round(_this.getX() / 500) * 500;
                        y = Math.round(_this.getY() / 500) * 500;

                        rand_x = Math.random() * 500;
                        rand_y = Math.random() * 500;

                        /*
                        if(rand_x > 450) rand_x = 450;
                        if(rand_y > 450) rand_y = 450;

                        if(rand_x < 100) rand_x = 100;
                        if(rand_y < 100) rand_y = 100;
                        */

                        if(rand_x > 30) rand_x = 30;
                        if(rand_x < 0) rand_x = 0;

                        if(rand_y > 30) rand_y = 30;
                        if(rand_y < 0) rand_y = 0;


                        finalx = (int)(x - 250 * pos);
                        finaly = (int)(y - 250 * pos);

                        if(__cache_bubbles.get(finalx+"x"+finaly) == null) cond = false;
                    }
                    __cache_bubbles.put(finalx+"x"+finaly, true);



                    _this.addBubble(new Bubble(
                            finalx,
                            finaly,
                            node.title(),
                            node.thumbnail_url(),
                            node
                    ));

                }


                _this.isMakingRequest = false;
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Toast.makeText(context, "Ошибка подгрузки карты", Toast.LENGTH_LONG).show();
                _this.isMakingRequest = false;
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawText("X: "+this.getX()+", y:"+this.getY(), 100, 100, paint);

        // Background
        canvas.drawColor(Color.argb(255, 235, 235, 235));

        // Draw all visible bubbles
        synchronized (bubbles.values()) {
            for (ArrayList<Bubble> arr_b : bubbles.values()) {
                for (Bubble b : arr_b) {
                    b.onDraw(canvas, paint, Math.round(getX()), Math.round(getY()));
                }
            }
        }
    }


    public void setMoved(boolean bool){
        this.moved = bool;
    }

    public boolean getMoved(){
        return this.moved;
    }




    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public float getStartTouchX() {
        return startTouchX;
    }

    public void setStartTouchX(float startTouchX) {
        this.startTouchX = startTouchX;
    }

    public float getStartTouchY() {
        return startTouchY;
    }

    public void setStartTouchY(float startTouchY) {
        this.startTouchY = startTouchY;
    }

    public float getMapBeforeTouchX() {
        return mapBeforeTouchX;
    }

    public void setMapBeforeTouchX(float mapBeforeTouchX) {
        this.mapBeforeTouchX = mapBeforeTouchX;
    }

    public float getMapBeforeTouchY() {
        return mapBeforeTouchY;
    }

    public void setMapBeforeTouchY(float mapBeforeTouchY) {
        this.mapBeforeTouchY = mapBeforeTouchY;
    }

    public void addBubble(Bubble obj){
        synchronized (bubbles.values()) {
            Integer x = Math.round(obj.getX() / 500) * 500;
            Integer y = Math.round(obj.getY() / 500) * 500;


            ArrayList<Bubble> select = this.bubbles.get(x + "x" + y);
            if (select == null) select = new ArrayList<Bubble>();

            select.add(obj);

            this.bubbles.put(x + "x" + y, select);
            this.invalidate(); // Force re-draw
        }
    }

    public HashMap<String, ArrayList<Bubble>> getBubbles() {
        return bubbles;
    }

    public void setBubbles(HashMap<String, ArrayList<Bubble>> bubbles) {
        this.bubbles = bubbles;
    }
}
