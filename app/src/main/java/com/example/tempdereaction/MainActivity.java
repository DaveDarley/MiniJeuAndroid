package com.example.tempdereaction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import android.os.Handler;

// Source bonne utilisation du postDelayed:
// https://stackoverflow.com/questions/32096278/android-postdelayed-method-doesnt-work

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private TextView monTexte;
    private TextView chrono2;

    private Handler H = new Handler();
    private Handler P = new Handler();
    private Handler h = new Handler();
    private Handler customHandler = new Handler(); //handler du chronometre

    private long elapsedMillis =0;
    int intervalleAffichage =0;
    int isRed=0;
    int isYellow=0;
    int isGreen=0;
    int startGame=1;

    double numberSecondsTotal = 0.0;
    String textToPrint = "";

    long startTime=0L, timeInMilliseconds=0L,timeSwapBuff=0L,updateTime=0L;

// Ce que je comprends de postDelayed: si on ecrit qqechose en bas du postDelayed ca l'execute
// en attendant que le nb de secondes soit passe pour ensuite entre dans le postDelayed!!!



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button2);
        monTexte = findViewById(R.id.affichEssai);
        chrono2 = findViewById(R.id.chronometer);
        clickListenBtn(); // mettre un listenner sur mon boutton des le debut du jeu!!

    }



    public void debutJeu(){
        chrono2.setText("0.000");
        isRed=1;
        Random random = new Random();
        intervalleAffichage = (random.nextInt((10 - 3) + 1) + 3) * 1000;
        h.postDelayed(yellowScreenRunnable,intervalleAffichage);
    }

    private Runnable yellowScreenRunnable = new Runnable() {
        @Override
        public void run() {
            btn.setBackgroundColor(Color.YELLOW);
            btn.setText("Veuillez cliquez sur le boutton");
            isYellow =1;
            isRed=0;
            isGreen=0;
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimer,0);


        }
    };
    private Runnable greenScreenRunnable = new Runnable(){
        @Override
        public void run() {
            String nbEssaiUser = monTexte.getText().toString();
            String[] array = nbEssaiUser.split(" ");
            int augmentationEssai = Integer.parseInt(array[1]) + 1;

            if (!(nbEssaiUser.equals("Essai 5 de 5"))) {
                btn.setBackgroundColor(Color.GRAY);
                monTexte.setText("Essai " + augmentationEssai + " de 5");
                btn.setText("Attendre que le boutton devienne jaune...");
                isRed=1;
                debutJeu();
            }else{
                affichAlertDialog();
            }
        }
    };

    private Runnable redScreenRunnable = new Runnable(){
        public void run(){
            btn.setBackgroundColor(Color.GRAY);
            btn.setText("Attendre que le boutton devienne jaune...");
            debutJeu();
        }
    };


    public void deroulementJeu() {
        btn.setBackgroundColor(Color.GREEN);
        btn.setText("Bravo, vous avez cliquez sur le boutton");
        P.postDelayed(greenScreenRunnable,1500);
    }


    public void affichRouge(){
        btn.setBackgroundColor(Color.RED);
        btn.setText("Vous devez attendre que le boutton soit jaune avant de clicker");
        // Si un autre clique se fait pendant ce postDelayed => mon isRed tjrs=1 donc ca re-rentre au meme if et change rien
        H.postDelayed(redScreenRunnable,1500);
    }

    public void clickListenBtn(){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRed==1 ){
                    h.removeCallbacks(yellowScreenRunnable); // si mon postDelayed d'ecran jaune avait deja commence qd on click sur l'ecran gris
                    affichRouge();
                }else if(isYellow==1) {
                    customHandler.removeCallbacks(updateTimer);
                    numberSecondsTotal += Double.parseDouble(textToPrint);
                    isYellow=0;
                    deroulementJeu();
                }else if(startGame==1){
                    btn.setText("Attendre que le boutton devienne jaune...");
                    startGame=0;
                    debutJeu();
                }else if(isGreen==1){ // on a plus besoin de ca
                    P.removeCallbacks(greenScreenRunnable);
                }
            }


        });
    }

//Source:https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
public void affichAlertDialog(){
    AlertDialog alertDialog = new AlertDialog.Builder(this)

            .setIcon(android.R.drawable.ic_dialog_alert)

            .setTitle("Test Complete")

            .setMessage("Temps de reaction moyen: "+(numberSecondsTotal/5)+" secondes")

            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    elapsedMillis=0; // reset mon compteur de secondes ecoules
                    isRed=0;
                    monTexte.setText("Essai 1 de 5");
                    btn.setBackgroundColor(Color.GRAY);
                    btn.setText("Cliquez pour commencer le jeu");
                    startGame=1; // pour attendre le premier onClick sur le boutton pour commencer le jeu
                    chrono2.setText("0.000");
                    numberSecondsTotal = 0.0;
                    clickListenBtn();
                }
            })
            .show();
}

// Source: https://www.youtube.com/watch?v=Dr-VtCbev10&t=424s&ab_channel=EDMTDev
Runnable updateTimer = new Runnable(){
        public void run(){
           timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
           updateTime=timeSwapBuff+timeInMilliseconds;
           int secs = (int)(updateTime/1000);
           int mins = secs/60;
           secs%=60;
           int milliseconds = (int)(updateTime%1000);
           textToPrint = secs+"."+String.format("%3d",milliseconds);
           chrono2.setText(textToPrint);
           customHandler.postDelayed(this,0);
        }
    };

}
