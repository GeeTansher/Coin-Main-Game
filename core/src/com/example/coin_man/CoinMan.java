package com.example.coin_man;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture dizzy_man;
	Rectangle manRect;
	int manState=0, pause=0;
	float gravity=0.4f;
	float velocity = 0;
	int manY=0;
	int score=0;
	int gameState=0;

	ArrayList<Integer> coinX = new ArrayList<>();
	ArrayList<Integer> coinY = new ArrayList<>();
	ArrayList<Rectangle> coinRect = new ArrayList<>();
	Texture coin;
	int coinCount=0;
	Random random;

	ArrayList<Integer> bombX = new ArrayList<>();
	ArrayList<Integer> bombY = new ArrayList<>();
	ArrayList<Rectangle> bombRect = new ArrayList<>();
	Texture bomb;
	int bombCount=0;

	BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		dizzy_man = new Texture("dizzy-1.png");

		manY=Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(6);
	}

	public void makeCoin(){
		float h = random.nextFloat() * Gdx.graphics.getHeight()-man[manState].getHeight();
		coinY.add((int)h);
		coinX.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float h = random.nextFloat() * Gdx.graphics.getHeight()-man[manState].getHeight();
		bombY.add((int)h);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		if(gameState==1){
			// game is live
			coinRect.clear();
			for(int i=0;i<coinX.size();i++){
				batch.draw(coin, coinX.get(i),coinY.get(i));
				coinX.set(i, coinX.get(i)-4);
				if(coinX.get(i) <= 0){
					coinX.remove(i);
					coinY.remove(i);
				}
				else{
					coinRect.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(), coin.getHeight()));
				}
			}

			// for bomb
			if(bombCount < 500){
				bombCount++;
			}
			else{
				bombCount = 0;
				makeBomb();
			}

			bombRect.clear();
			for(int i=0;i<bombX.size();i++){
				batch.draw(bomb, bombX.get(i),bombY.get(i));
				bombX.set(i, bombX.get(i)-6);
				if(bombX.get(i) <= 0){
					bombX.remove(i);
					bombY.remove(i);
				}
				else{
					bombRect.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(), bomb.getHeight()));
				}
			}

			// to jump
			if(Gdx.input.justTouched()){
				velocity = -20;
			}

			// to change state of man
			if(pause < 7){
				pause++;
			}
			else{
				pause=0;
				if(manState < 3){
					manState++;
				}
				else{
					manState=0;
				}
			}

			// some physics
			velocity += gravity;
			manY -= velocity;

			if(manY<=0){
				manY=0;
			}
			if(manY>=Gdx.graphics.getHeight()-man[manState].getHeight()){
				manY=Gdx.graphics.getHeight()-man[manState].getHeight();
				velocity=0;
			}

		}
		else if(gameState == 0){
			// waiting to start
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}
		else if(gameState == 2){
			// game over
			if(Gdx.input.justTouched()){
				score=0;
				velocity=0;
				gameState=0;
				manY=Gdx.graphics.getHeight()/2;
				coinX.clear();
				coinY.clear();
				coinRect.clear();
				coinCount=0;
				bombX.clear();
				bombY.clear();
				bombRect.clear();
				bombCount=0;
			}
		}
		// for coin
		if(coinCount < 120){
			coinCount++;
		}
		else{
			coinCount = 0;
			makeCoin();
		}


		// draw man
		if(gameState==2){
			batch.draw(dizzy_man, Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2-100, manY);
		}
		else{
			batch.draw(man[manState], Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2-100, manY);
		}
		batch.draw(man[manState], Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2-100, manY);
		manRect = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2-100, manY,
				man[manState].getWidth(), man[manState].getHeight());

		// collision with coins and bombs
		for(int i=0;i<coinRect.size();i++){
			if(Intersector.overlaps(manRect, coinRect.get(i))){
				score++;
				coinRect.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				break;
			}
		}

		for(int i=0;i<bombRect.size();i++){
			if(Intersector.overlaps(manRect, bombRect.get(i))){
//				score--;
//				bombRect.remove(i);
//				bombX.remove(i);
//				bombY.remove(i);
//				break;
				gameState = 2;
			}
		}

		// show score
		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
