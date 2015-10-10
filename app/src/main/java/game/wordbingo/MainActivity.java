package game.wordbingo;

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

import java.util.Random;
import java.util.Timer;


public class MainActivity extends ActionBarActivity {

    String TAG = this.getClass().getSimpleName();
    GridView gridView;
    String swiped_word="", prev="";
    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, numbers);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        gridView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me) {

                int action = me.getActionMasked();  // MotionEvent types such as ACTION_UP, ACTION_DOWN
                float currentXPosition = me.getX();
                float currentYPosition = me.getY();
                int position = gridView.pointToPosition((int) currentXPosition, (int) currentYPosition);

                // Access text in the cell, or the object itself
                String s = (String) gridView.getItemAtPosition(position);
                TextView tv = (TextView) gridView.getChildAt(position);
                Rect rect = null;
                //if(me.getAction() == MotionEvent.ACTION_DOWN){
                    // Construct a rect of the view's bounds
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                //}
                if (me.getAction()==MotionEvent.ACTION_MOVE) {
                    if(rect.contains(v.getLeft() + (int) me.getX(), v.getTop() + (int) me.getY())) {
                        if((prev == "" || prev!=s) && s!=null ) {
                            swiped_word = swiped_word + s;
                            prev = s;
                        }
                        Log.d(TAG, "Swiped value-> " + swiped_word);
                    }
                }
                if (me.getAction()==MotionEvent.ACTION_UP) {
                    swiped_word = "";
                }
                return true;
            }
        });
    }
}
