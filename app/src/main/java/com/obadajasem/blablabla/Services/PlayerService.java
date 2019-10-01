package com.obadajasem.blablabla.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.obadajasem.blablabla.MainActivity;
import com.obadajasem.blablabla.R;
import com.obadajasem.blablabla.model.Station;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.obadajasem.blablabla.MainActivity.STATION_IMG;
import static com.obadajasem.blablabla.MainActivity.STATION_NAME;
import static com.obadajasem.blablabla.MainActivity.STATION_STATE;
import static com.obadajasem.blablabla.MainActivity.STATION_URL;
import static com.obadajasem.blablabla.Services.App.CHANNEL_ID;


public class PlayerService extends Service {
    public static final String BUFFERING = "BUFFERING";
    private SimpleExoPlayer exoPlayer = null;
    private PlayerNotificationManager playerNotificationManager;
    public static final String TAG = "PlayerService";
    private Bitmap notification_img;
    RemoteViews progressBar;


    @Override
    public void onCreate() {
        super.onCreate();

        progressBar = new RemoteViews(getPackageName(), R.layout.content_main);
        progressBar.setViewVisibility(R.id.progressbar,VISIBLE);

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        try {
//            station= intent.getParcelableExtra(STATION);
//
//            Log.d(TAG, "onStartCommand: "+station);
//            Log.d(TAG, "onStartCommand: "+station.get(0).getName());

            if (exoPlayer.isPlaying()) {
                exoPlayer.setPlayWhenReady(false);
            }

        } catch (Exception e) {
            Log.d(TAG, "onStartCommand: " + e.toString());
        }
        try {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.placeholder);

            notification_img = Glide.with(PlayerService.this)
                    .setDefaultRequestOptions(requestOptions)
                    .asBitmap().load(Uri.parse(intent.getStringExtra(STATION_IMG)))
                    .submit(1000, 1000).get();

        } catch (Exception e) {
            Log.d(TAG, "getCurrentLargeIcon: " + e.toString());
        }
        Log.d(TAG, "getCurrentLargeIcon: " + intent.getStringExtra(STATION_IMG));

        final String s = intent.getStringExtra(STATION_NAME);
        Log.d(TAG, "onStartCommand: service" + s);
        Log.d(TAG, "onStartCommand: ");
        Uri uri = Uri.parse(intent.getStringExtra(STATION_URL));
        TrackSelector trackSelector = new DefaultTrackSelector();
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "blablabla"));
        MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, renderersFactory, trackSelector, new DefaultLoadControl());
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();
        exoPlayer.setAudioAttributes(audioAttributes, true);
        exoPlayer.prepare(audioSource);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new Player.EventListener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                //do some code in here
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if (exoPlayer.isPlaying()) {
                            exoPlayer.setPlayWhenReady(false);
                        }
                        intent.putExtra(BUFFERING, "isBuffering");
                        if (progressBar != null) {
                            progressBar.setViewVisibility(R.id.progressbar, GONE);
                            Log.d(TAG, "onPlayerStateChanged: VISIBILE ");

                        }
                        break;

                    case Player.STATE_IDLE:
                        break;

                    case Player.STATE_READY:

                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        if (progressBar != null) {
                            progressBar.setViewVisibility(R.id.progressbar, GONE);
                        }
                        Log.d(TAG, "onPlayerStateChanged:  GONE");
//

                        break;

                    default:

                        break;

                }

            }

        });

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(PlayerService.this, CHANNEL_ID, R.string.app_name, R.string.app_name, 12, new PlayerNotificationManager.MediaDescriptionAdapter() {
            @Override
            public String getCurrentContentTitle(Player player) {

                return intent.getStringExtra(STATION_NAME);
            }

            @SuppressLint("WrongConstant")
            @Nullable
            @Override
            public PendingIntent createCurrentContentIntent(Player player) {
                Intent i = new Intent(PlayerService.this, MainActivity.class);
                return PendingIntent.getActivity(PlayerService.this, 0, i, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            @Nullable
            @Override
            public String getCurrentContentText(Player player) {
                return intent.getStringExtra(STATION_STATE);
            }

            @Nullable
            @Override
            public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                return notification_img;
            }
        }, new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                Log.d(TAG, "onDestroy: ");
                Log.d(TAG, "onNotificationCancelled: " + dismissedByUser + "  id  " + notificationId);
                //new ADDED
                stopForeground(true);
                stopSelf();

            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification,
                                             boolean ongoing) {
                startForeground(notificationId, notification);


            }

        });

        playerNotificationManager.setUseNavigationActions(false);
        playerNotificationManager.setUsePlayPauseActions(true);
        playerNotificationManager.setUseNavigationActionsInCompactView(false);
        playerNotificationManager.setPlayer(exoPlayer);
        playerNotificationManager.setUseStopAction(true);
//        playerNotificationManager.set

//        playerNotificationManager
        Log.d(TAG, "onCustomAction: ");


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        playerNotificationManager.setPlayer(null);
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.release();
        exoPlayer = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

