package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;


public class SoundManager {
	private Music bgMusic;
    private boolean isMuted;
    
    public SoundManager() {
        initiateMusic();
    }

private void initiateMusic() {
    // bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/guinea_gavin.mp3"));
    // bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/garage_climber.mp3"));
       bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/mathematics.mp3"));
    //	bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/burn.mp3"));	      
       bgMusic.setLooping(true);
       bgMusic.play();
}

public boolean isMuted() {
    return isMuted;
}

public void toggleSound() {
    isMuted = !isMuted;
    if (isMuted) {
        bgMusic.setVolume(0f);
    } else {
        bgMusic.setVolume(1f);
    }
   }

public void resume() {
	if (bgMusic != null && !isMuted) {
        bgMusic.play();
    }
   }

public void pause() {
    if (bgMusic != null) {
        bgMusic.pause();
    }
   }

public void dispose() {
    if (bgMusic != null) {
        bgMusic.dispose();
    }
}

}