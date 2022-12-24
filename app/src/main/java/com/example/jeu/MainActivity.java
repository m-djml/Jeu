package com.example.jeu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /* variables privées de la classes */
    private ImageView gard, gard2, gard3;              // images du personnage
    private TextView t1, t2, t3, score, life;          // textes d'affichage lors du jeu + score
    private Button b1, b2;                             // boutons
    private ImageView[] bugs_right = new ImageView[5]; // ensemble des insectes à droite, il ne peut pas y a voir plus de 5 insectes
    private ImageView[] bugs_left = new ImageView[5];  // ensemble des insectes &agrave; gauche
    private int score_value = 0;                       // valeur du score sous forme d'entier pour faciliter les calculs et garder une sauvegarde du score courant
    private int life_value = 10;                       // points de vie du joueur

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
        // todo : l'application est super lente, trouver une solution
        // todo : ici : passer en asynchrone avec manip de thread ? oui ça marche
        // new BugMove (this, bugs_left, bugs_right, score_value).execute();

        /* thread pour faire avancer les ennemies */
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        for (int i = 0; i < 5; i++){
                            bugs_right[i].setY(bugs_right[i].getY()+10);
                            bugs_left[i].setY(bugs_left[i].getY()+10);
                            Log.d("asd", "I AM DOING MY ASYNC TASK");
                            sleep(50);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        /* thread pour évaluer les différents cas et calculer le score de la colonne de gauche */
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                while(true) {
                    if (gard2.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < 5; i++) {
                            float dist = getDist(bugs_left[i], gard2);
                            Log.e("asd", "DISTANCE = " + dist);
                            /* cas où l'insecte est frappé mais pas correctement */
                            if (dist < 300 && dist > 200) {
                                Log.e("asd", "GOOD");
                                int finalI = i;
                                runOnUiThread(() -> bugs_left[finalI].setVisibility(View.INVISIBLE));
                                runOnUiThread(() -> t2.setVisibility(View.VISIBLE));
                                score_value += 5;
                                runOnUiThread(() -> score.setText(String.valueOf(score_value / 5))); // div par 5 car la boucle for est gênante et multiplie le résultat par 5
                            }
                            /* cas où l'insecte est frappé au bon moment */
                            else if (dist <= 200) {
                                Log.e("asd", "EXCELLENT");
                                int finalI1 = i;
                                runOnUiThread(() -> bugs_left[finalI1].setVisibility(View.INVISIBLE));
                                runOnUiThread(() -> t1.setVisibility(View.VISIBLE));
                                score_value += 10;
                                runOnUiThread(() -> score.setText(String.valueOf(score_value / 5)));
                            }
                            /* cas où on frappe à côté */
                            else if (dist >= 300) {
                                Log.e("asd", "MISS");
                                // todo : bug : lorsqu'on rate pour la première fois
                                runOnUiThread(() -> t3.setVisibility(View.VISIBLE));              // si on rate on perd une vie
                                life_value -= 1;
                                runOnUiThread(() -> life.setText(String.valueOf(life_value / 5)));
                                // todo : écrire la fonction de pénalité de temps lorsqu'on rate
                            }
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
                while(true) {
                    if (gard3.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < 5; i++) {
                            float dist = getDist(gard3, bugs_right[i]);
                            Log.e("asd", "DISTANCE = " + dist);
                            if (dist < 300 && dist > 200) {
                                Log.e("asd", "GOOD");
                                int finalI1 = i;
                                runOnUiThread(() -> bugs_right[finalI1].setVisibility(View.INVISIBLE));
                                runOnUiThread(() -> t2.setVisibility(View.VISIBLE));
                                score_value += 5;
                                runOnUiThread(() -> score.setText(String.valueOf(score_value / 5)));
                            } else if (dist <= 200) {
                                Log.e("asd", "EXCELLENT");
                                int finalI = i;
                                runOnUiThread(() -> bugs_right[finalI].setVisibility(View.INVISIBLE));
                                runOnUiThread(() -> t1.setVisibility(View.VISIBLE));
                                score_value += 10;
                                runOnUiThread(() -> score.setText(String.valueOf(score_value / 5)));
                            } else if (dist >= 300) {
                                Log.e("asd", "MISS");
                                runOnUiThread(() -> t3.setVisibility(View.VISIBLE));
                                life_value -= 1;
                                runOnUiThread(() -> life.setText(String.valueOf(life_value / 5)));
                            }
                        }
                    }
                }
            }
        };
        thread3.start();

        // todo : bug : les insectes ne s'affichent pas
        // todo : implémenter le GameOverActivity lorsqu'on a perdu (vie <= 0)


        /* bouton de gauche */
        b1.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    /* on passe d'une jardinière au repos à une jardinière qui frappe à gauche */
                    gard.setVisibility(View.INVISIBLE);
                    gard2.setVisibility(View.VISIBLE);
                    break;
                case MotionEvent.ACTION_UP:
                    /* la jardinière se met au repos */
                    gard2.setVisibility(View.INVISIBLE);
                    gard.setVisibility(View.VISIBLE);
                    /* les textes d'affichage redeviennent invisibles */
                    t1.setVisibility(View.INVISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                    t3.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
            return false;
        });

        /* bouton droit, fonctionne de la même manière que le bouton gauche */
        b2.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    gard.setVisibility(View.INVISIBLE);
                    gard3.setVisibility(View.VISIBLE);

                    break;
                case MotionEvent.ACTION_UP:
                    gard3.setVisibility(View.INVISIBLE);
                    gard.setVisibility(View.VISIBLE);
                    t1.setVisibility(View.INVISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                    t3.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
            return false;
        });

    }

    /* fonction qui calcule la distance entre deux images (instances de type View) à l'aide des coordonnées du coin haut gauche de l'image */
    public float getDist(View v1, View v2){
        return (float) Math.sqrt((v1.getY() - v2.getY()) * (v1.getY() - v2.getY()) + (v1.getX() - v2.getX()) * (v1.getX() - v2.getX()));
    }
}
