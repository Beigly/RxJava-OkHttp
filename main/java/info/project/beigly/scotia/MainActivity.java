package info.project.beigly.scotia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;



import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Button btn;
    private static String TAG = " Nasa app.";

    private final String url = "https://api.nasa.gov/planetary/apod?api_key=NNKOjkoul8n1CH18TWA9gwngW1s1SmjESPjNoUFo";
    private OkHttpClient okHttpClient;
    private Request  request;
    private Response response;

    private Subscription subsc1;
    private Bitmap bitmap;
    private ImageView imageView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        tv.setMovementMethod(new ScrollingMovementMethod());


        btn = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                animaton();
                return true;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                subsc1 = imageTask()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {

                                Log.i(TAG, "complete rx   complet");
                                if (bitmap != null){
                                    imageView.setImageBitmap(bitmap);
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG, "err rx"+e.getMessage());
                            }

                            @Override
                            public void onNext(String s) {
                                tv.setText(s);
                                Log.i(TAG, "next rx " + s);
                            }
                        });

            }
        });
    }




    public Observable<String> imageTask() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                // task json parsing and image download

                okHttpClient = new OkHttpClient();
                request = new Request.Builder().url(url).build();
                response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

                Log.i(TAG, Integer.toString(response.code()));
                String title ="";
                String urlPath = "";

                if (response.code() != 200 && response.code() != 202) {
                    Log.e(TAG, "Special Err Resonce niether 200 nor 202");
                    subscriber.onNext("Special Err Resonce niether 200 nor 202");
                    subscriber.onCompleted();

                } else {
                    try {
                        String content = response.body().string();
                        JSONObject jobj  = new JSONObject(content);
                        title = jobj.getString("title");
                        urlPath = jobj.getString("url");

                        Log.i(TAG, "pathUrl=" + urlPath);
                        Log.i(TAG, "title=" + title);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        subscriber.onError(e1);
                    }
                    // load image by url

                    request = new Request.Builder()
                            .url(urlPath)
                            .build();

                    try {
                        response = okHttpClient.newCall(request).execute();
                        InputStream inputStream = response.body().byteStream();
                        bitmap = null ;
                        bitmap = BitmapFactory.decodeStream(inputStream);

                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }

                     subscriber.onNext(title);
                     subscriber.onCompleted();




                }
            }





        });
    }


    private void animaton() {

       btn.setVisibility(View.INVISIBLE);


        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
        tv.startAnimation(fadeIn);
        tv.startAnimation(fadeOut);
        fadeIn.setDuration(350);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(350);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(4200+fadeIn.getStartOffset());

        int y = bitmap.getHeight();
        int x = bitmap.getWidth() ;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        float wRetio = (float) width  / x ;
        float hRetio = (float) height / y ;
        float ritio = Math.max(wRetio,hRetio);
        Animation anim = new ScaleAnimation(
                1f, ritio, 1f, ritio , Animation.RELATIVE_TO_SELF, .5f,
                Animation.RELATIVE_TO_SELF, .5f);
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(350);
        imageView.startAnimation(anim);


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subsc1 != null && !subsc1.isUnsubscribed() ) subsc1.unsubscribe();
    }
}
