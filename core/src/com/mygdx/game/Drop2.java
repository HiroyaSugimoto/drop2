package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop2 extends Game {

    public SpriteBatch batch;
    public BitmapFont font;

    public void create () {
        batch = new SpriteBatch();

        //LibGDXのデフォルトのArialフォントを使用
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this));
    }

    public void render () {
        super.render();
    }

    public void dispose () {
        batch.dispose();
        font.dispose();
    }
}
