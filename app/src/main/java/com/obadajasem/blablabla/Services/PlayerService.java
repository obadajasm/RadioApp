package com.obadajasem.blablabla.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
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

import java.util.concurrent.ExecutionException;

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


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        try {
            if (exoPlayer.isPlaying()) {
                exoPlayer.setPlayWhenReady(false);
            }

        } catch (Exception e) {
            Log.d(TAG, "onStartCommand: " + e.toString());
        }
        
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
                        break;

                    case Player.STATE_IDLE:
                        break;

                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
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
                return PendingIntent.getActivity(PlayerService.this, 0, i, PendingIntent.FLAG_NO_CREATE);
            }

            @Nullable
            @Override
            public String getCurrentContentText(Player player) {
                return intent.getStringExtra(STATION_STATE);
            }

            @Nullable
            @Override
            public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run:  inside run");
                        try {
                            notification_img = Glide.with(PlayerService.this)
                                    .asBitmap().load(Uri.parse(intent.getStringExtra(STATION_IMG)))
                                    .fitCenter()
                                    .submit(1000, 1000).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        }

                    }
                });

                Log.d(TAG, "getCurrentLargeIcon: " + intent.getStringExtra(STATION_IMG));


                return notification_img;
            }
        }, new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                Log.d(TAG, "onDestroy: ");
                Log.d(TAG, "onNotificationCancelled: " + dismissedByUser + "  id  " + notificationId);
                stopForeground(true);
                stopSelf();

            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                startForeground(notificationId, notification);
            }

        });
        playerNotificationManager.setRewindIncrementMs(0);
        playerNotificationManager.setFastForwardIncrementMs(0);
        playerNotificationManager.setUsePlayPauseActions(true);
        playerNotificationManager.setUseStopAction(true);
        playerNotificationManager.setUseNavigationActions(false);
        playerNotificationManager.setPlayer(exoPlayer);

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

