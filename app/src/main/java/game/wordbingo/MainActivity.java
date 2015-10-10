package game.wordbingo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;


public class MainActivity extends ActionBarActivity {

	String TAG = this.getClass().getSimpleName();
    public String button_values[] = new String[25];
    public Button buttons[] = new Button[25];
    private MalibuCountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private TextView timer_text;
    private final long startTime = 5100;
    private final long interval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		init_buttons();
        updateButtonValues();
        setbuttonText();

        timer_text = (TextView) this.findViewById(R.id.textView_timer);
        countDownTimer = new MalibuCountDownTimer(startTime, interval);
        timer_text.setText(String.valueOf(startTime/1000));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


public void init_buttons(){

        buttons[0] = (Button)findViewById(R.id.button_01);
        buttons[1] = (Button)findViewById(R.id.button_00);
        buttons[2] = (Button)findViewById(R.id.button_02);
        buttons[3] = (Button)findViewById(R.id.button_03);
        buttons[4] = (Button)findViewById(R.id.button_04);

        buttons[5] = (Button)findViewById(R.id.button_10);
        buttons[6] = (Button)findViewById(R.id.button_11);
        buttons[7] = (Button)findViewById(R.id.button_12);
        buttons[8] = (Button)findViewById(R.id.button_13);
        buttons[9] = (Button)findViewById(R.id.button_14);

        buttons[10] = (Button)findViewById(R.id.button_20);
        buttons[11] = (Button)findViewById(R.id.button_21);
        buttons[12] = (Button)findViewById(R.id.button_22);
        buttons[13] = (Button)findViewById(R.id.button_23);
        buttons[14] = (Button)findViewById(R.id.button_24);

        buttons[15] = (Button)findViewById(R.id.button_30);
        buttons[16] = (Button)findViewById(R.id.button_31);
        buttons[17] = (Button)findViewById(R.id.button_32);
        buttons[18] = (Button)findViewById(R.id.button_33);
        buttons[19] = (Button)findViewById(R.id.button_34);

        buttons[20] = (Button)findViewById(R.id.button_40);
        buttons[21] = (Button)findViewById(R.id.button_41);
        buttons[22] = (Button)findViewById(R.id.button_42);
        buttons[23] = (Button)findViewById(R.id.button_43);
        buttons[24] = (Button)findViewById(R.id.button_44);
    }

    public String getNextString(int num){
        Random random = new Random();
        String c;
        boolean isvalid;
        do {
            isvalid = true;
            c = String.valueOf((char) (random.nextInt(26)+'a'));
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
        }
        return true;
    }

    public void setbuttonText(){
        for(int i=0;i<buttons.length;i++){
           buttons[i].setText(button_values[i]);
            Log.d(TAG,"UPdating button strings");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  boolean onClick_Start(View v){
        if (!timerHasStarted)
        {
            countDownTimer.start();
            timerHasStarted = true;
            //startB.setText("Start");
        }
        else
        {

            countDownTimer.cancel();
            timerHasStarted = false;
            //startB.setText("RESET");
        }
        return  true;
    }


    public String getButtonValue(int id){
        String text = null;
        for (int i=0;i<buttons.length;i++){
            if (buttons[i].getId() == id){
                text = String.valueOf(buttons[i].getText());
                //buttons[i].setBackgroundColor(0);
                break;
            }
        }
        return text;
    }

    public void onButtonClick(View v){
        String string= getButtonValue(v.getId());
        Log.d(TAG,"GOT value from button "+string);
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
            timer_text.setText("up!");
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
}
