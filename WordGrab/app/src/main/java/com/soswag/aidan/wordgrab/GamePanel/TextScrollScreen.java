package com.soswag.aidan.wordgrab.GamePanel;

/**
 * Created by Aidan on 2016-06-22.
 */
public class TextScrollScreen /*implements GameOverScreen*/{

/*private static final String TAG = "My_TextScroll";

    public static final int X = 0;
    public static final int Y = 1;

    Bitmap topOverlay;
    int topOLY;
    Bitmap bottomOverlay;
    int botOLY;
    Word[][] allWords;
    int absolutePosition = 0;
    int absoluteMax;
    float textSize;
    int spaceBetweenWords;
    int wordsPerPage;
    int totalWords;

    float scrollBarLength;
    float scrollBarWidth;
    float scrollX;

    int outLayHeight;
    int inlayHeight;
    int inlayWidth;
    int x0;
    int y0;

    Paint hl = new Paint();

    PlayAgainButton playAgainButton;

    public TextScrollScreen(ArrayList<Word> wordsThisGame, int pointsThisGame, String [][] topWords, int w, int h){
        Log.d(TAG, "Constructor called");
        Word.quickSort(wordsThisGame);

        Log.d(TAG, "sort complete");
        StatsDatabase.getInstance().insertDataToRack(pointsThisGame, wordsThisGame);
        StatsDatabase.GameData bestGame = StatsDatabase.getInstance().getBestRackGame();
        Log.d(TAG, "database operations complete");

        int wIn = TextScrollScreen.getInlayWidth(w);
        Bitmap top = Bitmap.createBitmap(wIn, h / 20 * 3, Bitmap.Config.ARGB_8888);
        top.eraseColor(ContextCompat.getColor(MainActivity.getContext(), R.color.colorSecondary));
        Canvas canvas = new Canvas(top);

        hl = new Paint();
        hl.setColor(0x99665599);

        Paint rightSideHighlight = new Paint();
        canvas.drawRect(top.getWidth()/2, 0, top.getWidth(), top.getHeight(), hl);

        int space = MyMath.roundToInt(1.0*wIn / 20);
        int wordSize = MyMath.roundToInt((1.0 * wIn - (topWords.length + 1) * space) / topWords.length);
        for(int i = 0; i < topWords.length; i++){
            for(int j = 0; j < topWords[i].length; j++)
            v2Tile.drawWord(topWords[i][j], new Rect(space + i * (space + wordSize), top.getHeight() / 3 * j, (i + 1) * (space + wordSize), top.getHeight() / 3 * (j  + 1)), canvas);
        }
        Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
        text.setTypeface(GameSelectionActivity.getTypeface());
        text.setTextSize(top.getHeight() * 0.2f);
        MyDrawing.drawTextCenteredAt(top.getWidth() / 4, top.getHeight() * 5 / 6, "" + pointsThisGame + "pts", text, canvas);
        MyDrawing.drawTextCenteredAt(top.getWidth() * 3 / 4, top.getHeight() * 5 / 6, "" + bestGame.points + "pts", text, canvas);

        Bitmap bot = Bitmap.createBitmap(wIn, h / 20, Bitmap.Config.ARGB_8888);
        bot.eraseColor(ContextCompat.getColor(MainActivity.getContext(), R.color.colorSecondary));
        canvas = new Canvas(bot);

        Word [] multis = {
                new Word("x2", new int [] {2, 2}),
                new Word("x3", new int [] {3, 3}),
                new Word("x4", new int [] {4, 4})};

        Word.getPaint().setTextSize(bot.getHeight() * 0.5f);
        for(int i = 0; i < multis.length; i++){
            multis[i].drawCenteredAt((int)((1.0 + i) / (1.0 + multis.length) * bot.getWidth()), bot.getHeight() / 2, canvas);
        }

        //NOTE
        //COPY PASTE FROM OTHER CONSTRUCTOR FROM HERE ON
        totalWords = wordsThisGame.size();
        if(bestGame.words.size() > totalWords)
            totalWords = bestGame.words.size();

        allWords = new Word [2][];
        allWords[0] = new Word [wordsThisGame.size()];
        wordsThisGame.toArray(allWords[0]);
        allWords[1] = new Word[bestGame.words.size()];
        bestGame.words.toArray(allWords[1]);

        int topHeight = 0;
        if(top != null){
            topOverlay = top;
            topHeight += top.getHeight();
        }
        int botHeight = 0;
        if(bot != null){
            bottomOverlay = bot;
            botHeight += bot.getHeight();
        }

        outLayHeight = h * 3 / 4;
        inlayHeight = outLayHeight - topHeight - botHeight;
        inlayWidth = w * 8 / 10;
        x0 = w / 10;
        y0 = h / 20;
        topOLY = y0;
        botOLY = y0 + topHeight + inlayHeight;
        y0 += topHeight;
        textSize = w / 20;
        spaceBetweenWords = h / 15;

        wordsPerPage = inlayHeight / spaceBetweenWords + 1;

        scrollBarWidth = w / 50;
        float ratio = 1.f * wordsPerPage / totalWords;
        if(ratio > 1.f)
            ratio = 1.f;
        scrollBarLength = inlayHeight * ratio - scrollBarWidth;

        scrollX = x0 + inlayWidth - scrollBarWidth * 1.f;


        absoluteMax = totalWords - wordsPerPage;
        absoluteMax *= spaceBetweenWords;
        if(absoluteMax < 1)
            absoluteMax = 1;

        int y00 = h / 20;
        playAgainButton = new PlayAgainButton(x0 + inlayWidth / 20, y00 + outLayHeight * 41 / 40, inlayWidth * 9 / 10, h - outLayHeight / 40 - (y00 + outLayHeight * 41 / 40));

        for(int i = 0; i < allWords.length; i++){
            for(int j = 0; j < allWords[i].length; j++)
                Log.d(TAG, allWords[i][j].getWord());
            Log.d(TAG, "END COLUMN " + i);
        }
    }

    public TextScrollScreen(Word [] words0, Word [] words1, Bitmap top, Bitmap bottom, int w, int h){
        totalWords = words0.length;
        if(words1.length > totalWords)
            totalWords = words1.length;

        allWords = new Word [2][totalWords];
        allWords[0] = words0;
        allWords[1] = words1;

        int topHeight = 0;
        if(top != null){
            topOverlay = top;
            topHeight += top.getHeight();
        }
        int botHeight = 0;
        if(bottom != null){
            bottomOverlay = bottom;
            botHeight += bottom.getHeight();
        }

        inlayHeight = h * 8 / 10 - topHeight - botHeight;
        inlayWidth = w * 8 / 10;
        x0 = w / 10;
        y0 = h / 10;
        topOLY = y0;
        botOLY = y0 + topHeight + inlayHeight;
        y0 += topHeight;
        textSize = w / 20;
        spaceBetweenWords = h / 20;

        wordsPerPage = inlayHeight / spaceBetweenWords + 1;

        scrollBarWidth = w / 50;
        float ratio = 1.f * wordsPerPage / totalWords;
        if(ratio > 1.f)
            ratio = 1.f;
        scrollBarLength = inlayHeight * ratio - scrollBarWidth;

        scrollX = x0 + inlayWidth - scrollBarWidth * 1.f;

        absoluteMax = totalWords - wordsPerPage;
        absoluteMax *= spaceBetweenWords;
        if(absoluteMax < 1)
            absoluteMax = 1;
    }

    public void draw(Canvas canvas){
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        hl.setColor(0x554488aa);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(MainActivity.getContext(), R.color.game_over_screen_overlay_color));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setColor(ContextCompat.getColor(MainActivity.getContext(), R.color.game_over_screen_innerlay_color));
        canvas.drawRect(x0, y0, x0 + inlayWidth, y0 + inlayHeight, paint);
        canvas.drawRect(x0 + inlayWidth * 0.5f, y0, x0 + inlayWidth, y0 + inlayHeight, hl);

        //Draw the scroll bar
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(scrollBarWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float scrollY = y0 + scrollBarWidth * 1.f + (inlayHeight - scrollBarLength - scrollBarWidth) * relativePosition();

        int longestColumn = allWords[0].length >= allWords[1].length ? 0 : 1;
        int inc = longestColumn == 0 ? 1 : -1;
        int start = absolutePosition / (wordsPerPage * spaceBetweenWords);
        Word.getPaint().setTextSize(textSize);
        for(int column = longestColumn; column < allWords.length && column >= 0; column += inc) {

            int x = inlayWidth / 4 + (column * inlayWidth / 2) + x0;
            for (int i = start, yIndex = 0; i < allWords[column].length && i < start + wordsPerPage; i++, yIndex++) {

                if(i % 2 == 0 && column == longestColumn){
                    canvas.drawRect(x0, y0 + yIndex * spaceBetweenWords, x0 + inlayWidth, y0 + (yIndex + 1) * spaceBetweenWords, hl);
                }

                if (allWords[column][i] == null)
                    break;

                int yToDraw = y0 + spaceBetweenWords / 2
                        + yIndex * spaceBetweenWords;

                //allWords[column][i].drawCenteredWithPointsAt(x, yToDraw, canvas);
                if(column == 0)
                    v2Tile.drawWordStraight(allWords[column][i], new Rect(x0 + inlayWidth / 2 * column, y0 + yIndex * spaceBetweenWords, x0 + inlayWidth / 2 * (column + 1), y0 + (yIndex + 1) * spaceBetweenWords), 0.1f, canvas);
                else
                    v2Tile.drawWordStraight(allWords[column][i], new Rect(x0 + inlayWidth / 2 * column, y0 + yIndex * spaceBetweenWords, x0 + inlayWidth / 2 * (column + 1) - (int)scrollBarWidth * 2, y0 + (yIndex + 1) * spaceBetweenWords), 0.1f, canvas);



            }
        }

        canvas.drawLine(scrollX, scrollY, scrollX, scrollY + scrollBarLength -  scrollBarWidth * 1.f, paint);

        if(topOverlay != null)
            canvas.drawBitmap(topOverlay, x0, topOLY, null);
        if(bottomOverlay != null)
            canvas.drawBitmap(bottomOverlay, x0, botOLY, null);

        playAgainButton.draw(canvas);

    }

    public void adjustPosition(int change){
        absolutePosition += change;
        if(absolutePosition < 0)
            absolutePosition = 0;
        if(absolutePosition > absoluteMax)
            absolutePosition = absoluteMax;
    }

    public void setTopOverlay(Bitmap bm){topOverlay = bm;}
    public float relativePosition(){
        //System.out.println("relpos = " + (1.f * absolutePosition / absoluteMax) + ", abspos = " + absolutePosition);
        return 1.f * absolutePosition / absoluteMax;
    }

    public static int getInlayWidth(int w){return 8 * w / 10;}

    public boolean playAgainButtonWasTouched(int xCoor, int yCoor){
        return playAgainButton.wasTouched(xCoor, yCoor);
    }

    @Override
    public void update(){}*/
}
