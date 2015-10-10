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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;


public class MainActivity extends ActionBarActivity {

    //String serverIP = "10.112.229.53";
    String serverIP = "192.168.43.31";
    String serverPort = "8887";
    InputStreamReader reader = null;
    BufferedReader read = null;
    PrintWriter writer = null;
    String TAG = this.getClass().getSimpleName();
    GridView gridView;
    public String button_values[] = new String[25];
    String swiped_word="", prev="";
    String[] numbers = new String[] {
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
        updateButtonValues();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, button_values);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        Thread clientThr = new Thread(new Client());
        clientThr.start();

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
                    writer.println(swiped_word);
                    swiped_word = "";
                }
                return true;
            }
        });
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
        }
        return true;
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
