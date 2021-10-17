package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    final Drop2 game;

    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered;

    public GameScreen(final Drop2 gam) {
        this.game = gam;

        //雨粒とバケットの画像をそれぞれ64x64ピクセルでロード
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        /* サウンドデータがないため一旦未実装
        //雨粒のSEと雨のBGMの"music"データをロードする
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);
        */

        //cameraとSpritebatchを作成
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        //バケツの初期位置とサイズを設定
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2; //バケツをx軸の中央に配置
        bucket.y = 20; //バケツをy軸の20ピクセルに配置
        bucket.width = 64;
        bucket.height = 64;

        //雨粒のArrayListを作成し、最初の雨粒をスポーン
        raindrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    //ひとつの雨粒の大きさと表示位置を設定し、ArrayListに追加
    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {

        //画面の背景色を設定
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //cameraの更新
        camera.update();

        //カメラによって指定された座標系でレンダリングするようにSpriteBatchに指示
        game.batch.setProjectionMatrix(camera.combined);

        //batchを開始し、バケツと全ての雨粒の描画
        game.batch.begin();
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
        game.batch.draw(bucketImage, bucket.x, bucket.y);
        for(Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();


        //ユーザー入力の処理
        //タッチ、マウス操作
        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }

        //キー操作（移動速度：毎秒200ピクセル）
        if (Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        //バケツが画面外にはみ出さないようにする処理
        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;

        //雨粒の新規生成が必要かチェックし、生成する
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRaindrop();

        //IteratorListを使用して雨粒を繰り返し生成
        Iterator<Rectangle> iter = raindrops.iterator();
        while(iter.hasNext()) {
            Rectangle raindrop = iter.next();

          //毎秒200ピクセルの速度で落下
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();

            //画面外まで落ちたら削除
            if(raindrop.y + 64 < 0)
                iter.remove();
            //バケツに重なったら削除
            if(raindrop.overlaps(bucket)) {
                dropsGathered++;
                //dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        //画面が表示されたとき、BGMを再生する
        //rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
