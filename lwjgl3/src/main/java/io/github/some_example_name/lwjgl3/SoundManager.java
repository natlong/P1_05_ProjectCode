package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


public class SoundManager {
	private Music gameMusic;
	private Music menuMusic;
	private Sound shootingSoundeffect;
	private Sound eatingSoundeffect;
	private Music currentMusic;
    private boolean isMuted;
    
    public SoundManager() {
        initiateMusic();
        initiateSound();
    }

private void initiateMusic() {
	
	menuMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMusic/mathematics.mp3"));
	menuMusic.setLooping(true);
	 gameMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMusic/guinea_gavin.mp3"));
	 gameMusic.setLooping(true);
	 
}

private void initiateSound() {
    shootingSoundeffect = Gdx.audio.newSound(Gdx.files.internal("gameMusic/shooting.mp3"));
    eatingSoundeffect = Gdx.audio.newSound(Gdx.files.internal("gameMusic/eating.mp3"));
}

public void playMenuMusic(){
    stopCurrentMusic();
    currentMusic = menuMusic;
    startCurrentMusic();
}


public void playGameMusic() {
	stopCurrentMusic();
    currentMusic = gameMusic;
    startCurrentMusic();
}



private void startCurrentMusic() {
    if (currentMusic != null && !isMuted) {
        currentMusic.play();
        currentMusic.setVolume(1f);
    }
}

private void stopCurrentMusic() {
    if (currentMusic != null) {
        currentMusic.stop();
    }
}

public void restartCurrentMusic() {
    if (currentMusic != null) {
        currentMusic.stop();
        if (!isMuted) {
            currentMusic.play();
            currentMusic.setVolume(1f);
        }
    }
}

public void playShootingSoundeffect(){
	 if (!isMuted && shootingSoundeffect != null) {
	        shootingSoundeffect.play();
	        }
}

public void playEatingSoundeffect() {
    if (!isMuted && eatingSoundeffect != null) {
        eatingSoundeffect.play();
    }
}


public boolean isMuted() {
    return isMuted;
}

public void toggleSound() {
    isMuted = !isMuted;
    if (currentMusic != null) {
        if (isMuted) {
            currentMusic.setVolume(0f);
            currentMusic.pause();
        } else {
            currentMusic.setVolume(1f);
            currentMusic.play();
        }
    }
}

public void resume() {
	if (currentMusic != null && !isMuted) {
		currentMusic.play();
    }
   }

public void pause() {
    if (currentMusic!= null) {
    	currentMusic.pause();
    }
   }

public void dispose() {
    if (gameMusic != null) {
        gameMusic.dispose();
    }
    if (menuMusic != null) {
        menuMusic.dispose();
    }
    if (shootingSoundeffect != null) {
        shootingSoundeffect.dispose();
    }
    if (eatingSoundeffect != null) {
        eatingSoundeffect.dispose();
    }
}

}