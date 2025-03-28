package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


public class SoundManager {
	private static SoundManager instance;
	
	private Music gameMusic;
	private Music menuMusic;
	private Music gameOverMusic;
	private Music gameClearMusic;
	private Sound shootingSoundeffect;
	private Sound eatingSoundeffect;
	private Music currentMusic;
    private boolean isMuted;
    private boolean isDisposed = false;
    
    private SoundManager() {
        initiateMusic();
        initiateSound();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    
	private void initiateMusic() {
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMusic/mathematics.mp3"));
		menuMusic.setLooping(true);
		 gameMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMusic/guinea_gavin.mp3"));
		 gameMusic.setLooping(true);
		 gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMusic/over.mp3"));
		 gameClearMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMusic/level_complete.wav"));
		 
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
	
	
	public void playGameMusic(float volume) {
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
	
	public void stopCurrentMusic() {
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
	
	public void playGameClearMusic(float volume){
		 if (!isMuted && gameClearMusic != null) {
			 gameClearMusic.play();
		        }
	}
	
	public void playGameOverMusic(float volume){
		stopCurrentMusic();
		 if (!isMuted && gameOverMusic != null) {
			 gameOverMusic.play();
		        }
	}
	
	
	
	public void playShootingSoundeffect(float volume){
		 if (!isMuted && shootingSoundeffect != null) {
		        shootingSoundeffect.play(volume);
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
	    if (isDisposed) {
	        return;
	    }
	    
	    if (gameMusic != null) {
	        gameMusic.dispose();
	        gameMusic = null;
	    }
	    if (menuMusic != null) {
	        menuMusic.dispose();
	        menuMusic = null;

	    if (gameOverMusic != null) {
	        gameOverMusic.dispose();
	        gameOverMusic = null;
	    }
	    if (gameClearMusic != null) {
	        gameClearMusic.dispose();
	        gameClearMusic = null;
	    }
	    if (shootingSoundeffect != null) {
	        shootingSoundeffect.dispose();
	        shootingSoundeffect = null;
	    }
	    if (eatingSoundeffect != null) {
	        eatingSoundeffect.dispose();
	        eatingSoundeffect = null;
	    }
	    
	    isDisposed = true;
	    currentMusic = null;
	    
	    instance = null;
	    }
	    }
	
	public static void shutdown() {
	    if (instance != null) {
	        instance.dispose();
	    }
	}

}