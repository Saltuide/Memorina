package com.example.memorina;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

class Card {
    Paint p = new Paint();
    private float width;
    private float height;
    private boolean visible = true;
//    public Card(float x, float y, float width, float height, int color) {
//        this.color = color;
//        this.x = x;
//        this.y = y;
//        this.width = width;
//        this.height = height;
//    }

    public Card(int color){
        this.color = color;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }
    public int getColor(){
        return this.color;
    }

    int color, backColor = Color.DKGRAY;
    boolean isOpen = false; // цвет карты
    float x, y;
    public void draw(Canvas c) {
        // нарисовать карту в виде цветного прямоугольника
        if (isOpen) {
            p.setColor(color);
        } else p.setColor(backColor);
        c.drawRect(x,y, x+width, y+height, p);
    }
    public boolean flip (float touch_x, float touch_y) {
        if (touch_x >= x && touch_x <= x + width && touch_y >= y && touch_y <= y + height) {
            isOpen = ! isOpen;
            return true;
        } else return false;
    }

}

public class TilesView extends View {
    // пауза для запоминания карт
    final int PAUSE_LENGTH = 1; // в секундах
    final int CARDS_AMOUNT = 10;
    boolean isOnPauseNow = false;

    int[] tiles = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.rgb(230, 0, 126),Color.rgb(128, 169, 255),
            Color.rgb(47, 11, 70), Color.rgb(255, 109, 0),
            Color.rgb(0, 255, 255), Color.rgb(139, 69, 19)};

    // число открытых карт
    int openedCard = 0;

    ArrayList<Card> cards;

    int width, height; // ширина и высота канвы

    public TilesView(Context context) {
        super(context);

    }

    public TilesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 1) заполнить массив tiles случайными цветами
        // сгенерировать поле 2*n карт, при этом
        // должно быть ровно n пар карт разных цветов
        newGame();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();

        float cardWidth = (width * 0.9f) / 4;
        float cardHeight = (height * 0.9f) / 5;
        float spaceW = width * 0.1f / 4;
        float spaceH = height * 0.1f / 5;

        int counter = 0;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 5; j++) {
                Card tmp = cards.get(counter);
                if (!tmp.isVisible()){
                    tmp.setWidth(0);
                    tmp.setHeight(0);
                    counter++;
                    continue;
                }
                tmp.setHeight(cardHeight);
                tmp.setWidth(cardWidth);
                tmp.setX(cardWidth * i + spaceW * (i + 1));
                tmp.setY(cardHeight * j + spaceH * (j + 1));
                counter++;
            }
        }

        // 2) отрисовка плиток
        // задать цвет можно, используя кисть
        Paint p = new Paint();
        for (Card c: cards) {
            c.draw(canvas);
        }
    }

    private boolean isAnyCardsLeft(){
        for (Card c : cards){
            if (c.isVisible())
                return true;
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 3) получить координаты касания
        int x = (int) event.getX();
        int y = (int) event.getY();
        // 4) определить тип события
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isOnPauseNow)
        {
            // палец коснулся экрана

            for (Card c: cards) {

                if (openedCard == 0) {
                    if (c.flip(x, y)) {
                        Log.d("mytag", "card flipped: " + openedCard);
                        openedCard ++;
                        invalidate();
                        return true;
                    }
                }

                if (openedCard == 1) {


                    // перевернуть карту с задержкой
                    if (c.flip(x, y)) {
                        openedCard ++;
                        // 1) если открылис карты одинакового цвета, удалить их из списка
                        // например написать функцию, checkOpenCardsEqual

                        // 2) проверить, остались ли ещё карты
                        // иначе сообщить об окончании игры

                        // если карты открыты разного цвета - запустить задержку
                        invalidate();
                        PauseTask task = new PauseTask();
                        task.execute(PAUSE_LENGTH);
                        isOnPauseNow = true;
                        return true;
                    }
                }

            }
        }


        // заставляет экран перерисоваться
        return true;
    }

    public void newGame() {
        // запуск новой игры

        cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(new Card(tiles[i]));
            cards.add(new Card(tiles[i]));
        }
        Collections.shuffle(cards);
        invalidate();
    }

    class PauseTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            Log.d("mytag", "Pause started");
            try {
                Thread.sleep(integers[0] * 1000); // передаём число секунд ожидания
            } catch (InterruptedException e) {}
            Log.d("mytag", "Pause finished");
            return null;
        }

        // после паузы, перевернуть все карты обратно


        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<Card> tmp = new ArrayList<>();
            for (Card c: cards) {
                if (c.isOpen) {
                    tmp.add(c);
                    c.isOpen = false;
                }
            }
            if (tmp.get(0).getColor() == tmp.get(1).getColor()){
                tmp.get(0).setVisible(false);
                tmp.get(1).setVisible(false);
            }
            if (!isAnyCardsLeft()){
                Toast.makeText(getContext(), "Поздравляю, вы выиграли",
                        Toast.LENGTH_LONG).show();

            }
            openedCard = 0;
            isOnPauseNow = false;
            invalidate();
        }
    }
}