package com.tntstudios.feedyourpig.gameplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import com.tntstudios.feedyourpig.R;
import com.tntstudios.feedyourpig.service.SoundInterface;

import java.util.ArrayList;
import java.util.List;

public class GameView extends View {
    private SoundInterface soundInterface;
    private boolean isWin = false;
    private boolean isPrepareWin = false;
    private boolean isLose = false;
    protected boolean isPlaying = false;
    protected int isHelp=0;
    private List<Integer> game_help = new ArrayList<>();
    private GameInterface gameInterface;
    private Handler handler = new Handler();
    private Runnable runnable_draw;
    private Runnable runnable_win,runnable_lose;
    private float CONST_DPI;
    private Box box;
    private Pig pig;
    private int width_screen, height_screen;
    private int[][] game_array = new int[10][18];

    private final int GAME_BOX_STAR = 10;
    private final int GAME_BOX_IRON = 11;
    private final int GAME_BOX_WOOD = 12;
    private final int GAME_BOX_THORN = 13;
    private final int GAME_BOX_ICE = 14;
    private final int GAME_BOX_ICE_EFFECT = 141;
    private final int GAME_BOX_ICE_BREAK = 142;
    private final int GAME_BOX_ICE_STAR = 140;
    private final int GAME_BOX_ICE_STAR_EFFECT = 1401;
    private final int GAME_BOX_ICE_STAR_BREAK = 1402;


    private int[] box_wood = {-1, -1, 0};
    private int[] box_wood_position = {0, 0};
    private boolean box_thorn_kill = false;
    private int box_ice[] = {-1, -1};
    private int box_ice_break_frame = 0;
    private int box_icestar[] = {-1, -1};
    private int box_icestar_break_frame = 0;


    private int[] candy_p = new int[2];
    private int[] pig_p = new int[2];
    private int[] box_star = {-1, -1};
    private float pig_positionX = 0;
    private float pig_positionY = 0;
    private int box_size;
    private int candy_positionX = 0;
    private int candy_positionY = 0;
    protected int help_position=0;
    protected int candy_animation = 0;
    private int const_height = -1;
    private int const_width = -1;
    protected int star_count = 0;
    private Paint mLinePaint;

    public GameView(Context context, int width_screen, int height_screen) {
        super(context);
        soundInterface = new SoundInterface(context);
        gameInterface = (GameInterface) context;
        mLinePaint = new Paint();
        mLinePaint.setColor(context.getColor(R.color.colorGrid));
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStyle(Paint.Style.STROKE);
        CONST_DPI = ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        runnable_draw = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        runnable_win = new Runnable() {
            @Override
            public void run() {
                isWin = false;
                isPrepareWin = false;
                isLose = false;
                isPlaying = false;
                gameInterface.Win();
                candy_animation = 0;
                const_height = -1;
            }
        };
        runnable_lose = new Runnable() {
            @Override
            public void run() {
                gameInterface.Lose();
            }
        };
        box_size = (int) ((width_screen - dpToPixel(10)) / 11f);
        pig = new Pig(context, box_size * 2);
        box = new Box(context, box_size);
        this.width_screen = width_screen;
        this.height_screen = height_screen;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (box.isLoaded() && isPlaying) {
            if (const_height == -1) {
                const_width = (width_screen - box_size * 11) / 2;
                const_height = (int) (height_screen - box_size * 19 - dpToPixel(5));
                candy_positionX = const_width + box_size * candy_p[0];
                candy_positionY = const_height + box_size * candy_p[1];
                pig_positionX = const_width + box_size * (pig_p[0] - 0.5f);
                pig_positionY = const_height + box_size * (pig_p[1] - 0.5f);
            }
            for (int i = 0; i <= 19; i++)
                canvas.drawLine(const_width, const_height + i * box_size, const_width + box_size * 11, const_height + i * box_size, mLinePaint);
            for (int i = 0; i <= 11; i++)
                canvas.drawLine(const_width + i * box_size, const_height, const_width + i * box_size, const_height + box_size * 19, mLinePaint);

            for (int i = 0; i <= 10; i++)
                for (int j = 0; j <= 18; j++) {
                    switch (game_array[i][j]) {
                        case GAME_BOX_IRON: {
                            canvas.drawBitmap(box.Iron(), const_width + box_size * i, const_height + box_size * j, null);
                            break;
                        }
                        case GAME_BOX_STAR: {
                            canvas.drawBitmap(box.Star(), const_width + box_size * i, const_height + box_size * j, null);
                            break;
                        }
                        case GAME_BOX_WOOD: {
                            canvas.drawBitmap(box.Wood(), const_width + box_size * i, const_height + box_size * j, null);
                            break;
                        }
                        case GAME_BOX_THORN: {
                            canvas.drawBitmap(box.Hole(), const_width + box_size * i, const_height + box_size * j, null);
                            break;
                        }
                        case GAME_BOX_ICE: {
                            canvas.drawBitmap(box.Ice(), const_width + box_size * i, const_height + box_size * j, null);
                            break;
                        }
                        case GAME_BOX_ICE_EFFECT: {
                            if (Math.pow(candy_p[0] - i, 2) + Math.pow(candy_p[1] - j, 2) > 1) {
                                game_array[i][j] = GAME_BOX_ICE_BREAK;
                            } else
                                canvas.drawBitmap(box.IcePrepareBreak(), const_width + box_size * i, const_height + box_size * j, null);
                            break;
                        }
                        case GAME_BOX_ICE_BREAK: {
                            canvas.drawBitmap(box.IceBreak(box_ice_break_frame++ / 3), const_width + box_size * i, const_height + box_size * j, null);
                            if (box_ice_break_frame == 26) {
                                game_array[i][j] = 0;
                                box_ice_break_frame = 0;
                            }
                            break;
                        }
//                        icestar
                        case GAME_BOX_ICE_STAR: {
                            canvas.drawBitmap(box.IceStar(), const_width + box_size * i, const_height + box_size * j, null);
                            break;
                        }
                        case GAME_BOX_ICE_STAR_EFFECT: {
                            if (Math.pow(candy_p[0] - i, 2) + Math.pow(candy_p[1] - j, 2) > 1) {
                                game_array[i][j] = GAME_BOX_ICE_STAR_BREAK;
                            } else
                                canvas.drawBitmap(box.IceStarPrepareBreak(), const_width + box_size * i, const_height + box_size * j, null);
                            break;
                        }
                        case GAME_BOX_ICE_STAR_BREAK: {
                            canvas.drawBitmap(box.IceBreak(box_icestar_break_frame++ / 3), const_width + box_size * i, const_height + box_size * j, null);
                            if (box_icestar_break_frame == 26) {
                                game_array[i][j] = GAME_BOX_STAR;
                                box_icestar_break_frame = 0;
                            }
                            break;
                        }


                    }
                }
//            box_star
            if (box_star[0] != -1) {
                canvas.drawBitmap(box.Star(), const_width + box_size * box_star[0], const_height + box_size * box_star[1], null);
                if ((const_width + box_size * box_star[0] - box_size * 0.5) <= candy_positionX && candy_positionX <= (const_width + box_size * box_star[0] + box_size * 0.5)
                        && ((const_height + box_size * box_star[1] - box_size * 0.5) <= candy_positionY && candy_positionY <= (const_height + box_size * box_star[1] + box_size * 0.5))) {
                    box_star[0] = -1;
                    soundInterface.star(star_count);
                }
            }
//            box_wood
            if (box_wood[0] != -1) {
                if (candy_animation == 0)
                    switch (box_wood[2]) {
                        case 1: {
                            box_wood_position[1] -= dpToPixel(15);
                            if (box_wood_position[1] <= (const_height + box_size * (box_wood[1] - 1))) {
                                game_array[box_wood[0]][box_wood[1] - 1] = GAME_BOX_WOOD;
                                box_wood_position[1] = (const_height + box_size * (box_wood[1] - 1));
                                box_wood[0] = -1;
                            }
                            break;
                        }
                        case 2: {
                            box_wood_position[0] += dpToPixel(15);
                            if (box_wood_position[0] >= (const_width + box_size * (box_wood[0] + 1))) {
                                game_array[box_wood[0] + 1][box_wood[1]] = GAME_BOX_WOOD;
                                box_wood_position[0] = (const_width + box_size * (box_wood[0] + 1));
                                box_wood[0] = -1;
                            }
                            break;
                        }
                        case 3: {
                            box_wood_position[1] += dpToPixel(15);
                            if (box_wood_position[1] >= (const_height + box_size * (box_wood[1] + 1))) {
                                game_array[box_wood[0]][box_wood[1] + 1] = GAME_BOX_WOOD;
                                box_wood_position[1] = (const_height + box_size * (box_wood[1] + 1));
                                box_wood[0] = -1;
                            }
                            break;
                        }
                        case 4: {
                            box_wood_position[0] -= dpToPixel(15);
                            if (box_wood_position[0] <= (const_width + box_size * (box_wood[0] - 1))) {
                                game_array[box_wood[0] - 1][box_wood[1]] = GAME_BOX_WOOD;
                                box_wood_position[0] = (const_width + box_size * (box_wood[0] - 1));
                                box_wood[0] = -1;
                            }
                            break;
                        }
                    }
                canvas.drawBitmap(box.Wood(), box_wood_position[0], box_wood_position[1], null);
            }
//            @hole
            if (box_thorn_kill && candy_animation == 0) {
                onLose(-5);
            }
            //@tnt

            //@box_ice
            if (box_ice[0] != -1 && candy_animation == 0) {
                game_array[box_ice[0]][box_ice[1]] = GAME_BOX_ICE_EFFECT;
                box_ice[0] = -1;
            }
//            @box_ice_star
            if (box_icestar[0] != -1 && candy_animation == 0) {
                game_array[box_icestar[0]][box_icestar[1]] = GAME_BOX_ICE_STAR_EFFECT;
                box_icestar[0] = -1;
            }

//            @pig
            if (isWin) {
                if (candy_animation == 0 || candy_animation == 5) {
                    if(candy_animation==0){
                        soundInterface.eating();
                        candy_animation = 5;
                    }
                    canvas.drawBitmap(pig.eat_win(), pig_positionX, pig_positionY, null);
                } else {
                    canvas.drawBitmap(pig.eat_prepare_win(), pig_positionX, pig_positionY, null);
                }
            } else {
                if (isLose) {
                    canvas.drawBitmap(pig.sad(), pig_positionX, pig_positionY, null);
                } else if (isPrepareWin) {
                    canvas.drawBitmap(pig.eat_prepare_win(), pig_positionX, pig_positionY, null);
                } else {
                    canvas.drawBitmap(pig.stand(), pig_positionX, pig_positionY, null);
                }
            }
//            @candy
            switch (candy_animation) {
                case 0: {
                    canvas.drawBitmap(box.Candy(), const_width + box_size * candy_p[0], const_height + box_size * candy_p[1], null);
                    break;
                }
                case 1: {
                    candy_positionY -= dpToPixel(30);
                    if (candy_positionY <= (const_height + box_size * candy_p[1])) {
                        candy_positionY = (const_height + box_size * candy_p[1]);
                        candy_animation = 0;
                        canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    } else {
                        canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    }
                    break;
                }
                case 2: {
                    candy_positionX += dpToPixel(30);
                    if (candy_positionX >= (const_width + box_size * candy_p[0])) {
                        candy_positionX = (const_width + box_size * candy_p[0]);
                        candy_animation = 0;
                        canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    } else {
                        canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    }
                    break;
                }
                case 3: {
                    candy_positionY += dpToPixel(30);
                    if (candy_positionY >= (const_height + box_size * candy_p[1])) {
                        candy_positionY = (const_height + box_size * candy_p[1]);
                        candy_animation = 0;
                        canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    } else {
                        canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    }
                    break;
                }
                case 4: {
                    candy_positionX -= dpToPixel(30);
                    if (candy_positionX <= (const_width + box_size * candy_p[0])) {
                        candy_positionX = (const_width + box_size * candy_p[0]);
                        candy_animation = 0;
                        canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    } else {
                        canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    }
                    break;
                }
                case -1: {
                    candy_positionY -= dpToPixel(30);
                    if (candy_positionY + box_size < 0) {
                        candy_animation = 5;
                        isLose = true;
                        handler.postDelayed(runnable_lose,1000);
                    }
                    canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    break;
                }
                case -2: {
                    candy_positionX += dpToPixel(30);
                    if (candy_positionX + box_size > width_screen) {
                        candy_animation = 5;
                        isLose = true;
                        handler.postDelayed(runnable_lose,1000);
                    }
                    canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    break;
                }
                case -3: {
                    candy_positionY += dpToPixel(30);
                    if (candy_positionY + box_size > height_screen) {
                        candy_animation = 5;
                        isLose = true;
                        handler.postDelayed(runnable_lose,1000);
                    }
                    canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    break;
                }
                case -4: {
                    candy_positionX -= dpToPixel(30);
                    if (candy_positionX + box_size < 0) {
                        candy_animation = 5;
                        isLose = true;
                        handler.postDelayed(runnable_lose,1000);
                    }
                    canvas.drawBitmap(box.Candy(), candy_positionX, candy_positionY, null);
                    break;
                }
//                case -5: {
//                    if(!isLose)handler.postDelayed(runnable_lose,1000);
//                    isLose = true;
//                    canvas.drawBitmap(box.CandyExplosion(), candy_positionX, candy_positionY, null);
//                    break;
//                }
            }
            if(isHelp>0&&candy_animation==0){
                help_position+=dpToPixel(1f);
                if(help_position>box_size*2)help_position=0;
                switch (game_help.get(isHelp-1)){
                    case 1:{
                        canvas.drawBitmap(box.Help(),candy_positionX,candy_positionY-help_position,null);
                        break;
                    }
                    case 2:{
                        canvas.drawBitmap(box.Help(),candy_positionX+help_position,candy_positionY,null);
                        break;
                    }
                    case 3:{
                        canvas.drawBitmap(box.Help(),candy_positionX,candy_positionY+help_position,null);
                        break;
                    }
                    case 4:{
                        canvas.drawBitmap(box.Help(),candy_positionX-help_position,candy_positionY,null);
                        break;
                    }
                }

            }
        }
        handler.postDelayed(runnable_draw, 10);
    }

    public float dpToPixel(float dp) {
        return dp * CONST_DPI;
    }

    public void onSwipeTop() {
        for (int i = candy_p[1]; i >= 0; i--) {
            if (checkCandy(candy_p[0], i, 1)) break;
            if (i == 0) {
                candy_p[1] = 0;
                onLose(-1);
                return;
            }
        }
        candy_animation = 1;
    }

    public void onSwipeRight() {
        for (int i = candy_p[0]; i <= 10; i++) {
            if (checkCandy(i, candy_p[1], 2)) break;
            if (i == 10) {
                candy_p[0] = 10;
                onLose(-2);
                return;
            }
        }
        candy_animation = 2;
    }

    public void onSwipeBottom() {
        for (int i = candy_p[1]; i <= 18; i++) {
            if (checkCandy(candy_p[0], i, 3)) break;
            if (i == 18) {
                candy_p[1] = 18;
                onLose(-3);
                return;
            }
        }
        candy_animation = 3;
    }

    public void onSwipeLeft() {
        for (int i = candy_p[0]; i >= 0; i--) {
            if (checkCandy(i, candy_p[1], 4)) break;
            if (i == 0) {
                candy_p[0] = 0;
                onLose(-4);
                return;
            }
        }
        candy_animation = 4;
    }

    public boolean checkCandy(int candyX, int candyY, int swipe_position) {
        if (game_array[candyX][candyY] == GAME_BOX_STAR) {
            game_array[candyX][candyY] = 0;
            box_star[0] = candyX;
            box_star[1] = candyY;
            star_count++;
            return false;
        }
        if (game_array[candyX][candyY] == GAME_BOX_IRON) {
            switch (swipe_position) {
                case 1: {
                    candy_p[1] = candyY + 1;
                    break;
                }
                case 2: {
                    candy_p[0] = candyX - 1;
                    break;
                }
                case 3: {
                    candy_p[1] = candyY - 1;
                    break;
                }
                case 4: {
                    candy_p[0] = candyX + 1;
                    break;
                }
            }
            checkPrepareWin();
            return true;
        }
        if (game_array[candyX][candyY] == GAME_BOX_WOOD) {
            switch (swipe_position) {
                case 1: {
                    candy_p[1] = candyY + 1;
                    if ((candyY == 0) || ((game_array[candyX][candyY - 1] != 0)&&(game_array[candyX][candyY - 1] != 10))) {
                        checkPrepareWin();
                        return true;
                    }
                    break;
                }
                case 2: {
                    candy_p[0] = candyX - 1;
                    if ((candyX == 10) || ((game_array[candyX + 1][candyY] != 0)&&(game_array[candyX + 1][candyY] != 10))){
                        checkPrepareWin();
                        return true;
                    }
                    break;
                }
                case 3: {
                    candy_p[1] = candyY - 1;
                    if ((candyY == 18) || ((game_array[candyX][candyY + 1] != 0)&&(game_array[candyX][candyY + 1] != 10)) ){
                        checkPrepareWin();
                        return true;
                    }
                    break;
                }
                case 4: {
                    candy_p[0] = candyX + 1;
                    if ((candyX == 0) || ((game_array[candyX - 1][candyY] != 0)&&(game_array[candyX - 1][candyY] != 10)) ){
                        checkPrepareWin();
                        return true;
                    }
                    break;
                }
            }
            game_array[candyX][candyY] = 0;
            box_wood[0] = candyX;
            box_wood[1] = candyY;
            box_wood[2] = swipe_position;
            box_wood_position[0] = const_width + box_size * box_wood[0];
            box_wood_position[1] = const_height + box_size * box_wood[1];
            checkPrepareWin();
            return true;
        }
        if (game_array[candyX][candyY] == GAME_BOX_THORN) {
            candy_p[0] = candyX;
            candy_p[1] = candyY;
            box_thorn_kill = true;
            return true;
        }
        if (game_array[candyX][candyY] == GAME_BOX_ICE) {
            switch (swipe_position) {
                case 1: {
                    candy_p[1] = candyY + 1;
                    break;
                }
                case 2: {
                    candy_p[0] = candyX - 1;
                    break;
                }
                case 3: {
                    candy_p[1] = candyY - 1;
                    break;
                }
                case 4: {
                    candy_p[0] = candyX + 1;
                    break;
                }
            }
            box_ice[0] = candyX;
            box_ice[1] = candyY;
            checkPrepareWin();
            return true;
        }
        if (game_array[candyX][candyY] == GAME_BOX_ICE_EFFECT) {
            switch (swipe_position) {
                case 1: {
                    candy_p[1] = candyY + 1;
                    break;
                }
                case 2: {
                    candy_p[0] = candyX - 1;
                    break;
                }
                case 3: {
                    candy_p[1] = candyY - 1;
                    break;
                }
                case 4: {
                    candy_p[0] = candyX + 1;
                    break;
                }
            }
            return true;
        }
        if (game_array[candyX][candyY] == GAME_BOX_ICE_STAR) {
            switch (swipe_position) {
                case 1: {
                    candy_p[1] = candyY + 1;
                    break;
                }
                case 2: {
                    candy_p[0] = candyX - 1;
                    break;
                }
                case 3: {
                    candy_p[1] = candyY - 1;
                    break;
                }
                case 4: {
                    candy_p[0] = candyX + 1;
                    break;
                }
            }
            box_icestar[0] = candyX;
            box_icestar[1] = candyY;
            checkPrepareWin();
            return true;
        }
        if (game_array[candyX][candyY] == GAME_BOX_ICE_STAR_EFFECT) {
            switch (swipe_position) {
                case 1: {
                    candy_p[1] = candyY + 1;
                    break;
                }
                case 2: {
                    candy_p[0] = candyX - 1;
                    break;
                }
                case 3: {
                    candy_p[1] = candyY - 1;
                    break;
                }
                case 4: {
                    candy_p[0] = candyX + 1;
                    break;
                }
            }
            return true;
        }



        if (candyX == pig_p[0] && candyY == pig_p[1]) {

            isWin = true;
            onWin();
            candy_p[0] = candyX;
            candy_p[1] = candyY;
            return true;
        }
        return false;
    }



    private void checkPrepareWin() {
        int check = 0;
        if (candy_p[0] == pig_p[0]) {
            if (candy_p[1] < pig_p[1]) {
                for (int i = candy_p[1] + 1; i < pig_p[1]; i++) {
                    check += game_array[candy_p[0]][i];
                }
            }
            if (candy_p[1] > pig_p[1]) {
                for (int i = candy_p[1] - 1; i > pig_p[1]; i--) {
                    check += game_array[candy_p[0]][i];
                }
            }
            isPrepareWin = check == 0;
        } else if (candy_p[1] == pig_p[1]) {
            if (candy_p[0] < pig_p[0]) {
                for (int i = candy_p[0] + 1; i < pig_p[0]; i++) {
                    check += game_array[i][candy_p[1]];
                }
            }
            if (candy_p[0] > pig_p[0]) {
                for (int i = candy_p[0] - 1; i > pig_p[0]; i--) {
                    check += game_array[i][candy_p[1]];
                }
            }
            isPrepareWin = check == 0;
        } else {
            isPrepareWin = false;
        }
    }

    private void onLose(int lose) {
        soundInterface.lose();
        candy_animation = lose;
    }

    public void startGame(int[] prepare, int[][] game_array) {
        this.game_array = game_array;
        pig_p[0] = prepare[0];
        pig_p[1] = prepare[1];
        candy_p[0] = prepare[2];
        candy_p[1] = prepare[3];
        box_wood[0] = -1;
        box_thorn_kill = false;
        box_ice[0] = -1;
        box_icestar[0] = -1;

        isWin = false;
        isPrepareWin = false;
        isLose = false;
        isHelp =0;
        help_position=0;
        candy_animation = 0;
        const_height=-1;
        star_count=0;
        pig.reset();
        box.reset();
        isPlaying = true;
    }
    public void help(List<Integer> game_help){
        this.game_help.clear();
        this.game_help.addAll(game_help);
    }

    private void onWin() {
        handler.postDelayed(runnable_win, 1500);
    }
}
