package com.example.javachipnavigationbar.Running;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javachipnavigationbar.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class RunGuideActivity extends AppCompatActivity {
//https://github.com/PierfrancescoSoffritti/android-youtube-player#quick-start
    @Override



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_guide);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.enterFullScreen();
        youTubePlayerView.exitFullScreen();
        youTubePlayerView.isFullScreen();
        youTubePlayerView.toggleFullScreen();

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "4P-fUsQ3T-c";
                String videoId2 = "EB5gSa2L4vs";
                String videoId3 = "D52eI-G7ynQ";
                String videoId4 = "yyjOhsNEqtE";
                String videoId5 = "KmqFyFoYiZs";
                String videoId6 = "ZnPGsqD3d1I";

                youTubePlayer.loadVideo(videoId, 0);
                youTubePlayer.loadVideo(videoId2, 0);
                youTubePlayer.loadVideo(videoId3, 0);
                youTubePlayer.loadVideo(videoId4, 0);
                youTubePlayer.loadVideo(videoId5, 0);
                youTubePlayer.loadVideo(videoId6, 0);
            }
        });

    }

}