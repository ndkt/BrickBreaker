package com.sabpisal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    final BrickBreaker game;
    OrthographicCamera camera;
    private Stage stage;
    String playButtonString = "PLAY (P)";
    String exitButtonString = "EXIT (E)";
    private Label title;


    public MainMenuScreen(final BrickBreaker game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1366, 768);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Skin quantumHorizonSkin = new Skin(Gdx.files.internal("skin/quantum-horizon-ui.json"));

        com.badlogic.gdx.scenes.scene2d.ui.Label title = new Label("Berry Brick-Breaker Deluxe",quantumHorizonSkin, "title");
        title.setPosition(camera.viewportWidth/2-300, camera.viewportHeight/2+200);
        stage.addActor(title);

        Button btnPlay = new TextButton(playButtonString, quantumHorizonSkin);
        btnPlay.setPosition(camera.viewportWidth/2, camera.viewportHeight/2);
        btnPlay.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                dispose();
                return true;
            }
        });
        stage.addActor(btnPlay);
        Button btnExit = new TextButton(exitButtonString, quantumHorizonSkin);
        btnExit.setPosition(camera.viewportWidth/2, camera.viewportHeight/2-100);
        btnExit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });
        stage.addActor(btnExit);
    }


    @Override
    public void render(float delta) {

        if(Gdx.input.isKeyPressed(Input.Keys.P)) {
            game.setScreen(new GameScreen(game));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            Gdx.app.exit();
        }

        // Clear the screen with dark blue color.
        ScreenUtils.clear(0,0,0.2f,1);
        // Camera - Update coordinate system for rendering.
        camera.update();
        stage.act();
        stage.draw();
    }



    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
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
        stage.dispose();
    }
}
