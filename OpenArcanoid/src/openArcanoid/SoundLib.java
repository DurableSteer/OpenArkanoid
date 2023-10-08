package openArcanoid;

import javafx.scene.media.AudioClip;

public class SoundLib {
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
		switch(effect) {
		case BLOCKKILL:{
			blockKill.play();
			break;
		}
		case PADHIT:{
			padHit.play();
			break;
		}
		case METALBLOCKHIT:{
			metalBlockHit.play();
			break;
		}
		case METALBLOCKKILL:{
			metalBlockKill.play();
			break;
		}
		case FIREBALL:{
			fireBall.play();
			break;
		}
		case FIRELASER:{
			fireLaser.play();
			break;
		}
		case BUMPBORDER:{
			if(!bumpBorder.isPlaying())
				bumpBorder.play();
			break;
		}
		case LASER:{
			laser.play();
			break;
		}
		case SIZEDOWN:{
			sizeDown.play();
			break;
		}
		case SIZEUP:{
			sizeUp.play();
			break;
		}
		case STICKY:{
			sticky.play();
			break;
		}
		case TRIPLE:{
			triple.play();
			break;
		}
		case EXTRALIVE:{
			extraLive.play();
			break;
		}
		case SLOWDOWN:{
			slowDown.play();
			break;
		}
		case BREAK:{
			breakOut.play();
			break;
		}
		case LIVELOST:{
			liveLost.play();
			break;
		}
		case LEVELCLEARED:{
			levelCleared.play();
			break;
		}
		case NEWLEVEL:{
			newLevel.play();
			break;
		}
		case GAMEOVER:{
			gameOver.play();
			break;
		}
		}
	}
}
