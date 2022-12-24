package com.example.jeu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    /* variables privées de la classes */
    private ImageView gard, gard2, gard3, plant1, plant2;              // images du personnage
    private TextView t1, t2, t3, score, life;          // textes d'affichage lors du jeu + score
    private Button b1, b2;                             // boutons
    private ImageView[] bugs_right = new ImageView[5]; // ensemble des insectes à droite, il ne peut pas y a voir plus de 5 insectes
    private ImageView[] bugs_left = new ImageView[5];  // ensemble des insectes &agrave; gauche
    private int score_value = 0;                       // valeur du score sous forme d'entier pour faciliter les calculs et garder une sauvegarde du score courant
    private int life_value = 10;                       // points de vie du joueur
    private boolean b1isPressed, b2isPressed;          // variables partagées entre les threads et les listeners
    private Lock lock = new ReentrantLock();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* on utilise les images et les textes créés dans activity_main.xml */
        setContentView(R.layout.activity_main);

        /* option pour mettre l'application en plein écran */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /* initialisation des images et des textes du jeu */
        gard = findViewById(R.id.gardener_rest);
        gard2 = findViewById(R.id.gardener_hit_left);
        gard3 = findViewById(R.id.gardener_hit_right);
        plant1 = findViewById(R.id.plant1);
        plant2 = findViewById(R.id.plant2);
        b1 = findViewById(R.id.hit_left);
        b2 = findViewById(R.id.hit_right);
        t1 = findViewById(R.id.excellent);
        t2 = findViewById(R.id.good);
        t3 = findViewById(R.id.miss);
        score = (TextView) findViewById(R.id.score_value);
        life = (TextView) findViewById(R.id.life);

        /* init des images des insectes */
        for (int i = 0; i < 5; i++) {
            bugs_right[i] = findViewById(R.id.bug);
            bugs_right[i].setVisibility(View.VISIBLE);
            bugs_left[i] = findViewById(R.id.bug2);
            bugs_left[i].setVisibility(View.VISIBLE);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();
        // todo : l'application est très lente, trouver une solution

        /* thread pour faire avancer les ennemies */
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        // todo : faire un id dans activity_main pour chaque insecte ? ici ils partagent tous le même view dans activity_main.xml
                        for (int i = 0; i < 5; i++) {
                            bugs_right[i].setY(bugs_right[i].getY() + 10);
                            bugs_left[i].setY(bugs_left[i].getY() + 10);
                            sleep(100);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        //thread.start();

        /* thread pour évaluer les différents cas et calculer le score de la colonne de gauche */
        // todo : bug : les pv commencent à des valeurs aléatoires négatives, la boucle while incrémente trop les compteurs
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < 5; i++) {
                        if (b1isPressed) {
                            float dist = getDist(bugs_left[i], gard2);
                            Log.e("asd", "DISTANCE 1 = " + dist);
                            /* cas où l'insecte est frappé mais pas correctement */
                            if (dist < 300 && dist > 200) {
                                Log.e("asd", "GOOD");
                                int finalI = i;
                                runOnUiThread(() -> bugs_left[finalI].setVisibility(View.INVISIBLE));
                                runOnUiThread(() -> t2.setVisibility(View.VISIBLE));
                                score_value += 5;
                                runOnUiThread(() -> score.setText(String.valueOf(score_value)));
                            }
                            /* cas où l'insecte est frappé au bon moment */
                            else if (dist <= 200) {
                                Log.e("asd", "EXCELLENT");
                                int finalI1 = i;
                                runOnUiThread(() -> bugs_left[finalI1].setVisibility(View.INVISIBLE));
                                runOnUiThread(() -> t1.setVisibility(View.VISIBLE));
                                score_value += 10;
                                runOnUiThread(() -> score.setText(String.valueOf(score_value)));
                            }
                            /* cas où on frappe à côté */
                            else if (dist >= 300) {
                                Log.e("asd", "MISS");

                                runOnUiThread(() -> t3.setVisibility(View.VISIBLE));
                                life_value -= 1;
                                runOnUiThread(() -> life.setText(String.valueOf(life_value)));
                                // todo : écrire la fonction de pénalité de temps lorsqu'on rate
                            }
                        } /*else {
                            runOnUiThread(() -> {
                                t1.setVisibility(View.INVISIBLE);
                                t2.setVisibility(View.INVISIBLE);
                                t3.setVisibility(View.INVISIBLE);
                            });
                        }*/
                        boolean isAttacking = getDist(bugs_right[i], plant1) < 10;
                        if (isAttacking) {
                            life_value--;
                            runOnUiThread(() -> life.setText(String.valueOf(life_value)));
                        }
                    }
                }
            }
        };
        thread2.start();

        /* thread pour évaluer les différents cas et calculer le score de la colonne de droite */
        Thread thread3 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < 5; i++) {
                        if (b2isPressed) {
                            float dist = getDist(gard3, bugs_right[i]);
                            Log.e("asd", "DISTANCE 2 = " + dist);
                            if (dist < 300 && dist > 200) {
                                Log.e("asd", "GOOD");
                                int finalI1 = i;
                                runOnUiThread(() -> bugs_right[finalI1].setVisibility(View.INVISIBLE));
                                runOnUiThread(() -> t2.setVisibility(View.VISIBLE));
                                score_value += 5;
                                runOnUiThread(() -> score.setText(String.valueOf(score_value)));
                            } else if (dist <= 200) {
                                Log.e("asd", "EXCELLENT");
                                int finalI = i;
                                runOnUiThread(() -> bugs_right[finalI].setVisibility(View.INVISIBLE));
                                runOnUiThread(() -> t1.setVisibility(View.VISIBLE));
                                score_value += 10;
                                runOnUiThread(() -> score.setText(String.valueOf(score_value)));
                            } else if (dist >= 300) {
                                Log.e("asd", "MISS");
                                runOnUiThread(() -> t3.setVisibility(View.VISIBLE));
                                life_value -= 1;
                                runOnUiThread(() -> life.setText(String.valueOf(life_value)));
                            }
                        } /*else {
                            runOnUiThread(() -> {
                                t1.setVisibility(View.INVISIBLE);
                                t2.setVisibility(View.INVISIBLE);
                                t3.setVisibility(View.INVISIBLE);
                            });
                        }*/
                        boolean isAttacking = getDist(bugs_right[i], plant2) < 10;
                        if (isAttacking) {
                            life_value--;
                            runOnUiThread(() -> life.setText(String.valueOf(life_value)));
                        }
                    }
                }
            }
        };
        thread3.start();

        // todo : implémenter le GameOver lorsqu'on a perdu (vie <= 0)

        /* bouton de gauche */
        b1.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                b1isPressed = true;
                /* on passe d'une jardinière au repos à une jardinière qui frappe à gauche */
                gard.setVisibility(View.INVISIBLE);
                gard2.setVisibility(View.VISIBLE);
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                b1isPressed = false;
                /* la jardinière se met au repos */
                gard2.setVisibility(View.INVISIBLE);
                gard.setVisibility(View.VISIBLE);
            }
            return false;
        });

        /* bouton droit, fonctionne de la même manière que le bouton gauche */
        b2.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                b2isPressed = true;
                gard.setVisibility(View.INVISIBLE);
                gard3.setVisibility(View.VISIBLE);
                return false;
            } else if (action == MotionEvent.ACTION_UP) {
                b2isPressed = false;
                gard3.setVisibility(View.INVISIBLE);
                gard.setVisibility(View.VISIBLE);
            }
            return false;
        });

    }

    /* fonction qui calcule la distance entre deux images (instances de type View) à l'aide des coordonnées du centre de chaque image */
    public float getDist(View v1, View v2) {
        float x1 = v1.getWidth() / 2;
        float y1 = v1.getHeight() / 2;
        float x2 = v2.getWidth() / 2;
        float y2 = v2.getHeight() / 2;
        return (float) Math.sqrt((y1 - y2) * (y1 - y2) + (x1 - x2) * (x1 - x2));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}