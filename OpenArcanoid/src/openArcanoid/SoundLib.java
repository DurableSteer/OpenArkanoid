package openArcanoid;

import javafx.scene.media.AudioClip;

public class SoundLib {
	private double volume = 0.35;
	AudioClip blockKill = new AudioClip(getClass().getResource("/main/resources/sounds/blockKill.mp3").toString());
	AudioClip padHit = new AudioClip(getClass().getResource("/main/resources/sounds/padHit.mp3").toString());
	AudioClip metalBlockHit = new AudioClip(getClass().getResource("/main/resources/sounds/metalBlockHit.mp3").toString());
	AudioClip metalBlockKill = new AudioClip(getClass().getResource("/main/resources/sounds/metalBlockKill.mp3").toString());
	AudioClip fireBall = new AudioClip(getClass().getResource("/main/resources/sounds/fireBall.mp3").toString());
	AudioClip fireLaser= new AudioClip(getClass().getResource("/main/resources/sounds/fireLaser.mp3").toString());
	AudioClip bumpBorder = new AudioClip(getClass().getResource("/main/resources/sounds/bumpBorder.mp3").toString());
	AudioClip laser = new AudioClip(getClass().getResource("/main/resources/sounds/laser.mp3").toString());
	AudioClip sizeDown = new AudioClip(getClass().getResource("/main/resources/sounds/sizeDown.mp3").toString());
	AudioClip sizeUp = new AudioClip(getClass().getResource("/main/resources/sounds/sizeUp.mp3").toString());
	AudioClip sticky = new AudioClip(getClass().getResource("/main/resources/sounds/sticky.mp3").toString());
	AudioClip triple = new AudioClip(getClass().getResource("/main/resources/sounds/triple.mp3").toString());
	AudioClip extraLive = new AudioClip(getClass().getResource("/main/resources/sounds/extraLive.mp3").toString());
	AudioClip slowDown = new AudioClip(getClass().getResource("/main/resources/sounds/slowDown.mp3").toString());
	AudioClip breakOut = new AudioClip(getClass().getResource("/main/resources/sounds/break.mp3").toString());
	AudioClip liveLost = new AudioClip(getClass().getResource("/main/resources/sounds/liveLost.mp3").toString());
	AudioClip levelCleared = new AudioClip(getClass().getResource("/main/resources/sounds/levelCleared.mp3").toString());
	AudioClip newLevel = new AudioClip(getClass().getResource("/main/resources/sounds/newLevel.mp3").toString());
	AudioClip gameOver = new AudioClip(getClass().getResource("/main/resources/sounds/gameOver.mp3").toString());

	public void play(SoundEffect effect) {
		AudioClip selectedAudio = null;
		switch(effect) {
		case BLOCKKILL:{
			selectedAudio = blockKill;
			break;
		}
		case PADHIT:{
			selectedAudio = padHit;
			break;
		}
		case METALBLOCKHIT:{
			selectedAudio = metalBlockHit;
			break;
		}
		case METALBLOCKKILL:{
			selectedAudio = metalBlockKill;
			break;
		}
		case FIREBALL:{
			selectedAudio = fireBall;
			break;
		}
		case FIRELASER:{
			selectedAudio = fireLaser;
			break;
		}
		case BUMPBORDER:{
			if(!bumpBorder.isPlaying())
				selectedAudio = bumpBorder;
			break;
		}
		case LASER:{
			selectedAudio = laser;
			break;
		}
		case SIZEDOWN:{
			selectedAudio = sizeDown;
			break;
		}
		case SIZEUP:{
			selectedAudio = sizeUp;
			break;
		}
		case STICKY:{
			selectedAudio = sticky;
			break;
		}
		case TRIPLE:{
			selectedAudio = triple;
			break;
		}
		case EXTRALIVE:{
			selectedAudio = extraLive;
			break;
		}
		case SLOWDOWN:{
			selectedAudio = slowDown;
			break;
		}
		case BREAK:{
			selectedAudio = breakOut;
			break;
		}
		case LIVELOST:{
			selectedAudio = liveLost;
			break;
		}
		case LEVELCLEARED:{
			selectedAudio = levelCleared;
			break;
		}
		case NEWLEVEL:{
			selectedAudio = newLevel;
			break;
		}
		case GAMEOVER:{
			selectedAudio = gameOver;
			break;
		}
		}
		if(selectedAudio != null)
			selectedAudio.play(volume);
		
	}
	
	public double getVolume() {
		return volume;
	}
	public void setVolume(double newVolume) {
		volume = newVolume;
		play(SoundEffect.METALBLOCKHIT);
	}
}
