package com.sabpisal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Arrays;
import java.util.Iterator;

public class GameScreen implements Screen {
    Boolean missileLoaded;
    boolean readyingSystem;
    Long missileArmedFinishedTime;
    boolean movingMissile;
    ParticleEffect pe;
    private Rectangle missile;
    private Texture missileImage;
    ShapeRenderer missileShape;
    Sprite missileSprite;
    boolean fuelEmpty;
    private int finishedGameCounter;
    private Label startGameLabel;
    private Label blocksHitLabel;
    private Label timeSurvivedLabel;
    private Label restartLabel;
    private Label launchLaserLabel;
    private Label launchMissileLabel;
    final BrickBreaker game;
    private int bbbwidth = 1366;
    private int bbbheight = 768;
    // The camera ensures that we can render using the target resolution of 800 * 480 pixels;
    private OrthographicCamera camera;
    private int initialBallX = bbbwidth/2;
    private int initialBallY = bbbheight/2;
    private Texture paddleImage;
    private Texture ballImage;
    private Texture heartEmptyImage;
    private Texture heartFullImage;
    // The SpriteBatch is a special class that is used to draw 2D images.
    private Circle ball;
    private Rectangle paddle;
    int xSpeed;
    int ySpeed;
    ShapeRenderer paddleShape;
    boolean gameReset;
    boolean inGame;
    private Stage stage;
    boolean collideWithBlock;
    int numberOfBlocksHit;
    int radius = 7;
    private Texture redTileImage;
    private Array<Rectangle> redTiles;
    private Array<Rectangle> hearts;
    private Array<Rectangle> emptyHearts;
    private int amountOfHeartsLost;
    int initialHeartLostX = 40;
    private Array<Rectangle> laserBeamsLeft;
    private Texture laserBeamImage;
    private Array<Rectangle> laserBeamsRight;
    Long startTime;
    Long currentTimeSec;
    Long finishedTime;
//    boolean gameStarted;

    public GameScreen(final BrickBreaker game) {
        startTime = System.currentTimeMillis();
        numberOfBlocksHit = 0;
        inGame = false;
        Skin quantumHorizonSkin = new Skin(Gdx.files.internal("skin/quantum-horizon-ui.json"));
        this.game = game;

        // load the images for the paddle, the ball, and the red tile.
        heartEmptyImage = new Texture(Gdx.files.internal("heartempty.png"));
        heartFullImage = new Texture(Gdx.files.internal("heartfull.png"));
        paddleImage = new Texture(Gdx.files.internal("paddle.png"));
        ballImage = new Texture(Gdx.files.internal("aquaball.png"));
        redTileImage = new Texture(Gdx.files.internal("redtile.png"));
        laserBeamImage = new Texture(Gdx.files.internal("beamyellow.png"));
        missileImage = new Texture(Gdx.files.internal("missile.png"));
        missileSprite = new com.badlogic.gdx.graphics.g2d.Sprite(missileImage);



        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, bbbwidth,bbbheight);
        stage = new Stage(new ScreenViewport());

        // Create a Rectangle to logically represent, the paddle and the red tile.
        paddle = new Rectangle(camera.viewportWidth/2-64/2,20,64,17);
        // Create a Circle to logically represent, the ball.
        ball = new Circle((paddle.x+(paddle.width/2)), paddle.height+3+15+5,radius);


        missile = new Rectangle();
        missile.x = paddle.x+(paddle.width/2);
        missile.y = paddle.height+3+15+5;
        missile.width = 50;
        missile.height = 65;

        missileLoaded = false;

        // Create the bricks array and spawn the bricks.
        redTiles = new Array<Rectangle>();

        // Create the hearts array
        hearts = new Array<Rectangle>();

        // Create the empty hearts array
        emptyHearts = new Array<Rectangle>();

        paddleShape = new ShapeRenderer();

        blocksHitLabel = new Label("Number of Blocks Hit: " + numberOfBlocksHit, quantumHorizonSkin);
        stage.addActor(blocksHitLabel);

        timeSurvivedLabel = new Label("Time Survived (Elapse): " + currentTimeSec, quantumHorizonSkin);
        timeSurvivedLabel.setPosition(blocksHitLabel.getX(), blocksHitLabel.getY()+20);
        stage.addActor(timeSurvivedLabel);

        restartLabel = new Label("", quantumHorizonSkin);
        restartLabel.setPosition(timeSurvivedLabel.getX(), timeSurvivedLabel.getY()+30);
        stage.addActor(restartLabel);


        startGameLabel = new Label("Press (S) to Start (Use Arrow Keys with Paddle)", quantumHorizonSkin);
        startGameLabel.setPosition(timeSurvivedLabel.getX(), timeSurvivedLabel.getY()+20);
        stage.addActor(startGameLabel);

        launchMissileLabel = new Label("Press (C)  to Call In Missile (Use WASD)", quantumHorizonSkin);
        launchMissileLabel.setPosition(startGameLabel.getX(), startGameLabel.getY()+30);
        stage.addActor(launchMissileLabel);

        launchLaserLabel = new Label("Press [SPACE] to Shoot Lasers", quantumHorizonSkin);
        launchLaserLabel.setPosition(launchMissileLabel.getX(), launchMissileLabel.getY()+30);
        stage.addActor(launchLaserLabel);



        // Create the laser beam array
        laserBeamsLeft = new Array<Rectangle>();
        laserBeamsRight = new Array<Rectangle>();

        generateRedTiles();
        generateHearts();

        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("Particles.party"),Gdx.files.internal(""));
        pe.start();
    }

    private void generateRedTiles() {
        for (int y = (int) (camera.viewportHeight/2); y < camera.viewportHeight; y += 20 + 20) {
            for (int x = 55; x < camera.viewportWidth - 155; x += 63 + 10) {
                Rectangle redTile = new Rectangle();
                redTile.x = x;
                redTile.y = y;
                redTile.width = 63;
                redTile.height = 20;
                redTiles.add(redTile);
            }
        }
    }

    private void shootLaserBeams() {
        // Shoot laser beam from both sides of the paddle;
        Rectangle laserbeamLeft = new Rectangle();
        Rectangle laserbeamRight = new Rectangle();
        laserbeamLeft.x = paddle.x;
        laserbeamRight.x = paddle.x+paddle.width - 10;
        laserbeamLeft.y = paddle.y + paddle.height;
        laserbeamRight.y = paddle.y + paddle.height;
        laserbeamLeft.width = 2;
        laserbeamLeft.width = 6;
        laserbeamRight.width = 2;
        laserbeamRight.width = 6;
        laserBeamsLeft.add(laserbeamLeft);
        laserBeamsRight.add(laserbeamRight);
    }

    private void generateHearts() {
        for (int n = 0; n < 41; n += 20) {
            System.out.println("N: " + n);
            Rectangle heart = new Rectangle();
            heart.y = camera.viewportHeight - 60;
            heart.x = n;
            heart.width = 10;
            heart.height = 10;
            hearts.add(heart);
            System.out.println("HEART BEGIN: " + Arrays.asList(hearts));
        }
    }

    private void generateEmptyHearts() {

        for (int n = 0; n <= amountOfHeartsLost; n++) {
            Rectangle emptyHeart = new Rectangle();
            emptyHeart.y = camera.viewportHeight - 60;
            emptyHeart.x = initialHeartLostX;
            emptyHeart.width = 10;
            emptyHeart.height = 10;
            emptyHearts.add(emptyHeart);
            initialHeartLostX -= 20;
            System.out.println("Empty Hearts: " + Arrays.asList(emptyHearts));
        }
//        while(initialHeartLostX >= 0) {
//            System.out.println("Init Heart Lost X: " + initialHeartLostX);
//            Rectangle emptyHeart = new Rectangle();
//            emptyHeart.y = camera.viewportHeight - 60;
//            emptyHeart.x = initialHeartLostX;
//            emptyHeart.width = 10;
//            emptyHeart.height = 10;
//            emptyHearts.add(emptyHeart);
//            initialHeartLostX -= 20;
//            System.out.println("Empty Hearts: " + Arrays.asList(emptyHearts));
//        }
//        for (int n = 0; n < amountOfHeartsLost; n++) {
//
//        }
    }

    @Override
    public void render(float delta) {
        currentTimeSec = (System.currentTimeMillis() - startTime) / 1000;
        timeSurvivedLabel.setText("Time Survived (Elapse): " + currentTimeSec);
        blocksHitLabel.setText("Number of Blocks Hit: " + numberOfBlocksHit);
        System.out.println("Heart Size: " + hearts.size);
        amountOfHeartsLost = 3 - hearts.size;

        ScreenUtils.clear(36/255f, 90/255f, 140/255f, 1);

        if(gameReset) {
//            System.out.println("Get a new Paddle Image!");
            ball.y += ySpeed * Gdx.graphics.getDeltaTime();
//            ball.x += xSpeed * Gdx.graphics.getDeltaTime();
            if (collideWithBlock) {
                gameReset = false;
                inGame = true;
            }
        } else if (inGame) {
            ball.x += xSpeed * Gdx.graphics.getDeltaTime();
            ball.y += ySpeed * Gdx.graphics.getDeltaTime();
        } else {
            ball.x = (paddle.x+(paddle.width/2));
            ball.y = paddle.height+3+15+5;
        }

        if (missileLoaded) {
            pe.update(Gdx.graphics.getDeltaTime());
            game.batch.begin();
            pe.getEmitters().first().setPosition(missile.x+50,missile.y+5);
            pe.draw(game.batch);
            missileSprite.draw(game.batch);
            missileSprite.setX(missile.x);
            missileSprite.setY(missile.y);
            game.batch.end();
        }

        if (currentTimeSec >= 10 && hearts.size == 3) {
            // If current time is 10 and still 3 hearts left than allow launching missile.
            readyingSystem = true;
        }

        if (pe.isComplete() && !movingMissile) {
            fuelEmpty = true;
            missile.y -= 500 * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            missileLoaded = true;
//            callInMissile();
        }

        while ((missile.x > camera.viewportWidth/2) && (movingMissile) && (currentTimeSec - missileArmedFinishedTime) > 2) {
            pe.allowCompletion();
            if (missileSprite.getRotation() != 90) {
                missileSprite.setRotation((float) 90);
            }
            System.out.println("Time since movng missile started: " + (currentTimeSec - missileArmedFinishedTime));
//            callInMissile();
        }




//        paddleShape.begin(ShapeRenderer.ShapeType.Filled);
//        paddleShape.rect(paddle.x, paddle.y,63,17);
//        paddleShape.end();




//        System.out.println("ballX: " + ball.x + ", " + "ballY: " + ball.y);
        // Tell the camera to update its matrices
        camera.update();
        // Tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // Begin a new batch, draw the paddle, the ball and the bricks.
        game.batch.begin();
        game.batch.draw(ballImage, ball.x, ball.y);
        game.batch.draw(paddleImage, paddle.x, paddle.y);
        for (Rectangle heart : hearts) {
            game.batch.draw(heartFullImage, heart.x,heart.y);
        }
        for (Rectangle emptyHeart : emptyHearts) {
            game.batch.draw(heartEmptyImage, emptyHeart.x, emptyHeart.y);
        }
        for (Rectangle laserBeam : laserBeamsLeft) {
            game.batch.draw(laserBeamImage, laserBeam.x, laserBeam.y);
        }
        for (Rectangle laserBeam : laserBeamsRight) {
            game.batch.draw(laserBeamImage, laserBeam.x, laserBeam.y);
        }


//        while (amountOfHeartsLost < )



        for (Rectangle redtile : redTiles) {
            game.batch.draw(redTileImage, redtile.x, redtile.y);
        }
        game.batch.end();


        if(ball.x-radius < 0 || ball.x+radius>camera.viewportWidth) {
            // Inverse the speed
            xSpeed = -xSpeed;
        }

        for (Iterator<Rectangle> heartIter = hearts.iterator(); heartIter.hasNext(); ) {
            Rectangle heart = heartIter.next();
//            System.out.println("Amount of Heart Lost: " + amountOfHeartsLost);
            if (ball.y - radius < 0) {
                hearts.pop();
                generateEmptyHearts();
                if (hearts.size > 0) {
                    // Inverse the speed
//                System.out.println("YOU DEAD");
//            ySpeed = -ySpeed;

                    xSpeed = 0;
                    ySpeed = 0;

                    inGame = false;
                    gameReset = false;
                } else {
                    game.setScreen(new GameScreen(game));
                }
            }
        }

        if (redTiles.size == 0) {
            if (finishedGameCounter == 0) {
                 finishedTime = currentTimeSec;
                finishedGameCounter += 1;
            }
//            game.setScreen(new GameScreen(game));
            restartLabel.setText("(R) to Restart!");
            startGameLabel.setText("");
            timeSurvivedLabel.setText("Time Survived (Elapse): " + finishedTime);
        }



        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            game.setScreen(new GameScreen(game));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            shootLaserBeams();
        }

        if (ball.y + radius > camera.viewportHeight) {
            collideWithBlock = true;
            ySpeed = -ySpeed;
        }



        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            paddle.x -= 600 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            paddle.x += 600 * Gdx.graphics.getDeltaTime();
        }


        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//			System.out.println("MissileSprite Rotation: " + missileSprite.getRotation());
            if (missileSprite.getRotation() != -50) {
                missileSprite.setRotation((float) -10);
            }
            missileSprite.setX(missile.x);
            missileSprite.setY(missile.y);
            missile.x += 400 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && !fuelEmpty) {
            if (missileSprite.getRotation() != 50) {
                missileSprite.setRotation((float) 10);
            }
            missileSprite.setX(missile.x);
            missileSprite.setY(missile.y);
            missile.x -= 400 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) && !fuelEmpty) {
            if (missileSprite.getRotation() != 0) {
                missileSprite.setRotation((float) 0);
            }
            missile.y += 1000 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) && !fuelEmpty) {
            if (missileSprite.getRotation() != 0) {
                missileSprite.setRotation((float) 0);
            }
            missile.y -= 1000 * Gdx.graphics.getDeltaTime();
        }

        // Make sure the paddle stays within the screen bounds
        if (paddle.x < 0) {
            paddle.x = 0;
        }
        if (paddle.x > camera.viewportWidth - 64) {
            paddle.x = camera.viewportWidth - 64;
        }

        if (Intersector.overlaps(ball,paddle)) {
            ySpeed = -ySpeed;
        }




        for (Iterator<Rectangle> iter = redTiles.iterator(); iter.hasNext(); ) {
            Rectangle redtile = iter.next();
            if (Intersector.overlaps(ball,redtile)) {
                collideWithBlock = true;
                numberOfBlocksHit += 1;
                ySpeed = -ySpeed;
                iter.remove();
            }
            if (Intersector.overlaps(missile, redtile)) {
                iter.remove();
                numberOfBlocksHit += 1;
            }
        }

        for (Iterator<Rectangle> laserBeamLeftIter = laserBeamsLeft.iterator(); laserBeamLeftIter.hasNext(); ) {
            Rectangle laserBeam = laserBeamLeftIter.next();
            laserBeam.y += 200 * Gdx.graphics.getDeltaTime();
            for (Iterator<Rectangle> iter = redTiles.iterator(); iter.hasNext(); ) {
                Rectangle redtile = iter.next();
                if (Intersector.overlaps(laserBeam,redtile)) {
                    numberOfBlocksHit += 1;
                    iter.remove();
                    laserBeamLeftIter.remove();
                }
            }
            if(laserBeam.y > camera.viewportHeight) laserBeamLeftIter.remove();
        }


        for (Iterator<Rectangle> laserBeamRightIter = laserBeamsRight.iterator(); laserBeamRightIter.hasNext(); ) {
            Rectangle laserBeam = laserBeamRightIter.next();
            laserBeam.y += 200 * Gdx.graphics.getDeltaTime();
            for (Iterator<Rectangle> iter = redTiles.iterator(); iter.hasNext(); ) {
                Rectangle redtile = iter.next();
                if (Intersector.overlaps(laserBeam,redtile)) {
                    numberOfBlocksHit += 1;
                    iter.remove();
                    laserBeamRightIter.remove();
                }
            }
            if(laserBeam.y > camera.viewportHeight) laserBeamRightIter.remove();
        }






        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.S) && !inGame) {
            this.xSpeed = 300;
            this.ySpeed = 300;
            gameReset = true;
            collideWithBlock = false;
            startGameLabel.setText("");
            restartLabel.setText("(R) to Restart!");
            startTime = System.currentTimeMillis();
//            gameStarted = true;
        }




        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {
    }

    private void callInMissile() {
        if (readyingSystem) {
            missile.x = camera.viewportWidth-100;
            missile.y = camera.viewportHeight/2-100;
            missileArmedFinishedTime = currentTimeSec;
            System.out.println("Missile Armed Time: " + missileArmedFinishedTime);
            readyingSystem = false;
            movingMissile = true;
        }

        while ((missile.x > camera.viewportWidth/2) && (movingMissile) &&(currentTimeSec - missileArmedFinishedTime) > 2) {
            missileSprite.setX(missile.x);
            missileSprite.setY(missile.y);
            missile.x -= 10 * Gdx.graphics.getDeltaTime();

            if (missile.x != camera.viewportWidth) {
                missile.x -= camera.viewportWidth/2;
                if (missileSprite.getRotation() != 0) {
                    missileSprite.setRotation((float) 0);
                }
                movingMissile = false;
                pe.reset();
            }
        }
    }

    @Override
    public void dispose() {
        ballImage.dispose();
        paddleImage.dispose();
        game.batch.dispose();
        paddleShape.dispose();
        redTileImage.dispose();
        heartEmptyImage.dispose();
        heartFullImage.dispose();
        laserBeamImage.dispose();
        missileImage.dispose();
        missileShape.dispose();
    }
}
