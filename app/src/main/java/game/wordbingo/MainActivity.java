package game.wordbingo;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;


public class MainActivity extends ActionBarActivity {

    //String serverIP = "10.112.229.53";
    String serverIP = "192.168.43.31";
    String serverPort = "8881";
    InputStreamReader reader = null;
    BufferedReader read = null;
    PrintWriter writer = null;
    String TAG = this.getClass().getSimpleName();
    GridView gridView;
    public String button_values[] = new String[25];
    String swiped_word="", prev="";
    private boolean blnAplhabetSelctionMode = true;
    String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y"};

    private Hashtable strikedAlphabet = new Hashtable();
    private Hashtable nonstrikedAlphabet = new Hashtable();
    private MalibuCountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private TextView timer_text;
    private TextView help_text;
    private TextView score_text;
    private int intNumberofBINGO = 0;

    private TextView textView_B;
    private TextView textView_I;
    private TextView textView_N;
    private TextView textView_G;
    private TextView textView_O;

    private final long startTime = 5100;
    private final long interval = 1000;
    public Map<String, Integer> m = new HashMap<String, Integer>();
    int score=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);
        timer_text = (TextView) this.findViewById(R.id.textView_timer);
        countDownTimer = new MalibuCountDownTimer(startTime, interval);
        timer_text.setText(String.valueOf(startTime / 1000));
        help_text = (TextView)this.findViewById(R.id.textView_Help);
        score_text = (TextView)this.findViewById(R.id.textView_score);
        textView_B = (TextView)this.findViewById(R.id.textView_B);
        textView_I = (TextView)this.findViewById(R.id.textView_I);
        textView_N = (TextView)this.findViewById(R.id.textView_N);
        textView_G = (TextView)this.findViewById(R.id.textView_G);
        textView_O = (TextView)this.findViewById(R.id.textView_O);
        updateButtonValues();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, button_values);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Toast.makeText(getApplicationContext(),
                //((TextView) v).getText(), Toast.LENGTH_SHORT).show();
                if (blnAplhabetSelctionMode) {
                    TextView tv = (TextView) v;
                    for (int i = 0; i < 25; i++) {
                        View temp = gridView.getChildAt(i);
                        if (((TextView) temp) == tv) {
                            nonstrikedAlphabet.remove(i);
                            strikedAlphabet.put(i, 1);
                            Log.d(TAG, "Index: " + i);
                        }
                    }


                    Log.d(TAG, "Clicked Alphabet: " + tv.getText());
                    Log.d(TAG, "Clicked Alphabet ID: " + tv.getId());
                    writer.println(tv.getText());

                    int k =getNumberOfRowsStriked();
                    if (k - intNumberofBINGO > 0){
                        score += k*25;
                        intNumberofBINGO = k;
                        score_text.setText(String.valueOf(score));
                        if(k >= 5){
                            score += 500;       //BINGO
                        }

                        if(k>=1){
                            textView_B.setPaintFlags(textView_B.getPaintFlags() |  Paint.STRIKE_THRU_TEXT_FLAG);
                        }

                        if(k>=2){
                            textView_I.setPaintFlags(textView_I.getPaintFlags() |  Paint.STRIKE_THRU_TEXT_FLAG);
                        }

                        if(k>=3){
                            textView_N.setPaintFlags(textView_N.getPaintFlags() |  Paint.STRIKE_THRU_TEXT_FLAG);
                        }

                        if(k>=4){
                            textView_G.setPaintFlags(textView_G.getPaintFlags() |  Paint.STRIKE_THRU_TEXT_FLAG);
                        }

                        if(k>=5){
                            textView_O.setPaintFlags(textView_O.getPaintFlags() |  Paint.STRIKE_THRU_TEXT_FLAG);
                        }

                    }


                    //if(strikedAlphabet.size() > 2) {
                        toggleSelectAndSwipeMode();
                    //}
                    /*else{
                        countDownTimer.cancel();
                        countDownTimer = new MalibuCountDownTimer(5100, 1000);
                        timer_text.setText(String.valueOf(5100 / 1000));
                        countDownTimer.start();
                    }*/
                }
                
            }
        });
        makeHash();
        Thread clientThr = new Thread(new Client());
        clientThr.start();

        gridView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me) {
                if(!blnAplhabetSelctionMode) {

                    int action = me.getActionMasked();  // MotionEvent types such as ACTION_UP, ACTION_DOWN
                    float currentXPosition = me.getX();
                    float currentYPosition = me.getY();
                    int position = gridView.pointToPosition((int) currentXPosition, (int) currentYPosition);

                    // Access text in the cell, or the object itself
                    String s = (String) gridView.getItemAtPosition(position);
                    TextView tv = (TextView) gridView.getChildAt(position);
                    if (tv != null) {
                        if (!tv.isEnabled()) {
                            //swiped_word = "";
                            return false;
                        }
                        //tv.setEnabled(false);
                    }
                    Rect rect = null;
                    //if(me.getAction() == MotionEvent.ACTION_DOWN){
                    // Construct a rect of the view's bounds
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    //}
                    if (me.getAction() == MotionEvent.ACTION_MOVE) {
                        if (rect.contains(v.getLeft() + (int) me.getX(), v.getTop() + (int) me.getY())) {
                            if ((prev == "" || prev != s) && s != null) {
                                swiped_word = swiped_word + s;
                                prev = s;
                            }
                            Log.d(TAG, "Swiped value-> " + swiped_word);
                        }
                    }
                    if (me.getAction() == MotionEvent.ACTION_UP) {
                        if(swiped_word.length() > 2) {
                            Toast.makeText(getApplicationContext(),
                                    swiped_word, Toast.LENGTH_SHORT).show();
                            calculatescore(swiped_word);
                        }
                        swiped_word = "";
                        Log.d(TAG, "UP");
                    }
                }
                return false;
            }
        });
    }

    public void makeHash(){
        try {
            BufferedReader in = new BufferedReader(new FileReader("R.raw.dict.txt"));
            String str;
            while ((str = in.readLine()) != null)
                m.put(str,1);
            in.close();
        } catch (IOException e) {
        }
    }

	public  boolean isGoodWord(String word){
        System.out.println("word===>>>>> "+word);
        return m.containsKey(word);
    }

    public void calculatescore(String word){
        if (isGoodWord(word)){
            int len = word.length();
            score += len*10;
            score_text.setText(String.valueOf(score));
            Log.d(TAG, "Score:" + score);
        }
    }

    public int getNumberOfRowsStriked(){
        int k =0;
        if(strikedAlphabet.containsKey(0) && strikedAlphabet.containsKey(1) && strikedAlphabet.containsKey(2) && strikedAlphabet.containsKey(3) && strikedAlphabet.containsKey(4)){
            k++;
        }

        if(strikedAlphabet.containsKey(5) && strikedAlphabet.containsKey(6) && strikedAlphabet.containsKey(7) && strikedAlphabet.containsKey(8) && strikedAlphabet.containsKey(9)){
            k++;
        }

        if(strikedAlphabet.containsKey(10) && strikedAlphabet.containsKey(11) && strikedAlphabet.containsKey(12) && strikedAlphabet.containsKey(13) && strikedAlphabet.containsKey(14)){
            k++;
        }

        if(strikedAlphabet.containsKey(15) && strikedAlphabet.containsKey(16) && strikedAlphabet.containsKey(17) && strikedAlphabet.containsKey(18) && strikedAlphabet.containsKey(19)){
            k++;
        }

        if(strikedAlphabet.containsKey(0) && strikedAlphabet.containsKey(5) && strikedAlphabet.containsKey(10) && strikedAlphabet.containsKey(15) && strikedAlphabet.containsKey(20)){
            k++;
        }

        if(strikedAlphabet.containsKey(1) && strikedAlphabet.containsKey(6) && strikedAlphabet.containsKey(11) && strikedAlphabet.containsKey(16) && strikedAlphabet.containsKey(21)){
            k++;
        }

        if(strikedAlphabet.containsKey(2) && strikedAlphabet.containsKey(7) && strikedAlphabet.containsKey(12) && strikedAlphabet.containsKey(17) && strikedAlphabet.containsKey(22)){
            k++;
        }

        if(strikedAlphabet.containsKey(3) && strikedAlphabet.containsKey(8) && strikedAlphabet.containsKey(13) && strikedAlphabet.containsKey(18) && strikedAlphabet.containsKey(23)){
            k++;
        }

        if(strikedAlphabet.containsKey(4) && strikedAlphabet.containsKey(9) && strikedAlphabet.containsKey(14) && strikedAlphabet.containsKey(19) && strikedAlphabet.containsKey(24)){
            k++;
        }

        if(strikedAlphabet.containsKey(0) && strikedAlphabet.containsKey(6) && strikedAlphabet.containsKey(12) && strikedAlphabet.containsKey(18) && strikedAlphabet.containsKey(24)){
            k++;
        }

        if(strikedAlphabet.containsKey(4) && strikedAlphabet.containsKey(8) && strikedAlphabet.containsKey(12) && strikedAlphabet.containsKey(16) && strikedAlphabet.containsKey(20)){
            k++;
        }

        return  k;
    }
    public void toggleSelectAndSwipeMode() {
        if (blnAplhabetSelctionMode && strikedAlphabet.size() > 2) {
            //if(strikedAlphabet.size() > 2) {
                blnAplhabetSelctionMode = false;        //Swipe mode is on
            //}
            Set keyset = strikedAlphabet.keySet();
            Iterator it = keyset.iterator();
            while(it.hasNext()){
                TextView tempButton;
                tempButton = (TextView)gridView.getChildAt(Integer.parseInt(String.valueOf(it.next())));
                tempButton.setEnabled(true);
            }

            Set keyset1 = nonstrikedAlphabet.keySet();
            Iterator it1 = keyset1.iterator();
            while(it1.hasNext()){
                TextView tempButton;
                tempButton = (TextView)gridView.getChildAt(Integer.parseInt(String.valueOf(it1.next())));
                tempButton.setEnabled(false);
            }

            countDownTimer.cancel();
            countDownTimer = new MalibuCountDownTimer(10100, 1000);
            timer_text.setText(String.valueOf(10100/1000));
            countDownTimer.start();
            help_text.setText("Swipe Words");
        }
        else
        {
            blnAplhabetSelctionMode = true;

            Set keyset = strikedAlphabet.keySet();
            Iterator it = keyset.iterator();
            while(it.hasNext()){
                TextView tempButton;
                tempButton = (TextView)gridView.getChildAt(Integer.parseInt(String.valueOf(it.next())));
                tempButton.setEnabled(false);
            }

            Set keyset1 = nonstrikedAlphabet.keySet();
            Iterator it1 = keyset1.iterator();
            while(it1.hasNext()){
                TextView tempButton;
                tempButton = (TextView)gridView.getChildAt(Integer.parseInt(String.valueOf(it1.next())));
                tempButton.setEnabled(true);
            }
            //reset timer to 3 second

            countDownTimer.cancel();
            countDownTimer = new MalibuCountDownTimer(5100, 1000);
            timer_text.setText(String.valueOf(5100 / 1000));
            countDownTimer.start();
            help_text.setText("Choose Alphabet");
        }
    }
    public String getNextString(int num){
        Random random = new Random();
        String c;
        boolean isvalid;
        do {
            isvalid = true;
            c = String.valueOf((char) (random.nextInt(26)+'A'));
            for (int i = 0; i < num; i++) {
                if (button_values[i].equals(c)){
                    isvalid = false;
                    break;
                }
            }
        }while(!isvalid);
        return c;
    }

    public boolean updateButtonValues(){
        for(int i=0;i<25;i++){
            button_values[i] = getNextString(i);
            nonstrikedAlphabet.put(i, button_values[i]);
        }
        return true;
    }

	// CountDownTimer class
    public class MalibuCountDownTimer extends CountDownTimer
    {

        public MalibuCountDownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }

        @Override
        public void onFinish()
        {
            toggleSelectAndSwipeMode();
            timer_text.setText("0");
            Log.d(TAG, "up!");
            //timeElapsedView.setText("Time Elapsed: " + String.valueOf(startTime));
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            timer_text.setText(String.valueOf(millisUntilFinished/1000));
            //timeElapsed = startTime - millisUntilFinished;
            //imeElapsedView.setText("Time Elapsed: " + String.valueOf(timeElapsed));
        }
    }

    public class Client implements Runnable{
        String recvMessage = null;
        String IP = serverIP;
        int port = Integer.parseInt(serverPort);
        Socket clientSocket = null;

        public void run() {
            try {
                System.out.println(IP);
                System.out.println(port);
                clientSocket = new Socket(IP, port);

            } catch (IOException e) {
                System.out.println("Error creating Socket!");
            }
            try {
                reader = new InputStreamReader(clientSocket.getInputStream());
                read = new BufferedReader(reader);
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                int i = 0;
                while (true) {
                    recvMessage = read.readLine(); //work
                    runOnUiThread(new Runnable() {
                        public void run() {
                            System.out.println(recvMessage);
                            Toast.makeText(getApplicationContext(), "Received Message from Server : "
                                    + recvMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                    System.out.println("Receive" + recvMessage);
                }
            } catch (IOException e) {
                System.out.println("Error reading from Socket!");
            }
        }
    }
}
