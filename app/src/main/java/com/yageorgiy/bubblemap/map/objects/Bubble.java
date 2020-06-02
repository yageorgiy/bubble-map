package com.yageorgiy.bubblemap.map.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;

import com.yageorgiy.bubblemap.GetRandomMapNodesQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Bubble {

    private String caption;
    private String author;
    private Bitmap bitmap;

    private Integer x;
    private Integer y;
    private Integer w = 128;
    private Integer h = 128;
    private GetRandomMapNodesQuery.MapNode node;

    public Bubble(Integer x, Integer y, String caption, Bitmap bitmap, GetRandomMapNodesQuery.MapNode node) {
        this.x = x;
        this.y = y;
        this.caption = caption;
        this.bitmap = bitmap;
        this.node = node;
    }

    public Bubble(Integer x, Integer y, String caption, String url, GetRandomMapNodesQuery.MapNode node){
        this.x = x;
        this.y = y;
        this.caption = caption;
        this.setBitmapFromURLString(url);
        this.node = node;
    }






    /**
     *
     * @param canvas
     */
    public void onDraw(Canvas canvas, Paint paint, Integer rootRelativeX, Integer rootRelativeY) {
        int x1 = rootRelativeX + getX();
        int y1 = rootRelativeY + getY();
        int x2 = rootRelativeX + getX() + this.w;
        int y2 = rootRelativeY + getY() + this.h;


        // Render image
        Rect rect = new Rect(x1, y1, x2, y2);
        canvas.drawBitmap(this.bitmap, null, rect, paint);

        // Render text
        paint.setTextSize(21);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(this.caption, x2 - (this.w / 2.0f), y2 + 25, paint); //+" x: "+x1+"; y: "+y1
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    /**
     *
     * @return
     */
    public String getCaption() {
        return caption;
    }

    /**
     *
     * @param caption
     */
    // TODO: delete useless?
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     *
     * @return
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     */
    // TODO: delete useless?
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {

        // Compressing to JPEG 50% quality
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap final_bmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        // Resize bitmap
        final_bmap = Bitmap.createScaledBitmap(final_bmap, this.w, this.h, false);

        // Make corners
        final_bmap = getRoundedCornerBitmap(final_bmap, 128 * 2);

        this.bitmap = final_bmap;
    }

    /**
     *
     * @param src
     */
    public void setBitmapFromURLString(String src){
        try {
            URL url = new URL(src);
            setBitmapFromURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param url
     */
    public void setBitmapFromURL(URL url){
        class UpdateTask extends AsyncTask<URL, String, Bitmap> {
            protected Bitmap doInBackground(URL... urls) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) urls[0].openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    return BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        AsyncTask<URL, String, Bitmap> task = new UpdateTask();
        task.execute(url);
        try {
            this.setBitmap(task.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Integer getX() {
        return x;
    }

    // TODO: delete useless?
    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    // TODO: delete useless?
    public void setY(Integer y) {
        this.y = y;
    }

    public GetRandomMapNodesQuery.MapNode getNode() {
        return node;
    }

    public void setNode(GetRandomMapNodesQuery.MapNode node) {
        this.node = node;
    }
}
