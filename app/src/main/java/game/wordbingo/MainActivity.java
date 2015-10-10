package game.wordbingo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;




public class MainActivity extends ActionBarActivity {

    private MalibuCountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private TextView timer_text;
    private final long startTime = 5100;
    private final long interval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
