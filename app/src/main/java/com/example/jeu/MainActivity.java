package com.example.jeu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /* variables privées de la classes */
    private ImageView gard, gard2, gard3;              // images du personnage
    private TextView t1, t2, t3, score, life;                // textes d'affichage lors du jeu + score
    private Button b1, b2;                             // boutons
    private ImageView[] bugs_right = new ImageView[5]; // ensemble des insectes à droite, il ne peut pas y a voir plus de 5 insectes
    private ImageView[] bugs_left = new ImageView[5];  // ensemble des insectes &agrave; gauche
    private int score_value = 0;                       // valeur du score sous forme d'entier pour faciliter les calculs et garder une sauvegarde du score courant
    private int life_value = 10;                             // points de vie du joueur
    TranslateAnimation animation;                      // instance d'une animation (valable pour toutes les images)

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

        /* init des animations pour les insectes */
        // todo : reste à voir si on doit créer une seule animation pour tous les insectes ou une animation différente pour chaque insecte
        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 1800f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setFillAfter(true);
        animation.setDuration(8000);

        /* init des images des insectes */
        for (int i = 0; i < 5; i++) {
            bugs_right[i] = findViewById(R.id.bug);
            bugs_right[i].setVisibility(View.VISIBLE);
            bugs_left[i] = findViewById(R.id.bug2);
            bugs_left[i].setVisibility(View.VISIBLE);
            System.out.println("BUGS "+i+" CREATED");       // pour le débug
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();

        /* on lance l'animation */
        bugs_right[0].startAnimation(animation);
        bugs_left[0].startAnimation(animation);

        /* bouton de gauche */
        b1.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    /* on passe d'une jardinière au repos à une jardinière qui frappe à gauche */
                    gard.setVisibility(View.INVISIBLE);
                    gard2.setVisibility(View.VISIBLE);

                    /* boucle for pour traiter tous les insectes de gauche */
                    for (int i = 0; i < 5; i++){                          // on veut calculer la distance avec chaque insecte
                        float dist = getDist(gard2, bugs_left[i]);

                        /* cas où l'insecte est frappé mais pas correctement */
                        if (dist < 300 && dist > 200) {
                            bugs_left[i].setVisibility(View.INVISIBLE);
                            t2.setVisibility(View.VISIBLE);
                            score_value+=5;
                            score.setText(String.valueOf(score_value/5)); // div par 5 car la boucle for est gênante et multiplie le résultat par 5
                        }
                        /* cas où l'insecte est frappé au bon moment */
                        else if (dist < 200) {
                            bugs_left[i].setVisibility(View.INVISIBLE);
                            t1.setVisibility(View.VISIBLE);
                            score_value+=10;
                            score.setText(String.valueOf(score_value/5));
                        }
                        /* cas où on frappe à côté */
                        else {
                            // todo : bug : lorsqu'on rate pour la première fois
                            t3.setVisibility(View.VISIBLE);              // si on rate on perd une vie
                            life_value-=1;
                            life.setText(String.valueOf(life_value/5));
                            // todo : écrire la fonction de pénalité de temps lorsqu'on rate
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    /* la jardinière se met au repos */
                    gard2.setVisibility(View.INVISIBLE);
                    gard.setVisibility(View.VISIBLE);

                    /* les textes d'affichage redeviennent invisibles */
                    t1.setVisibility(View.INVISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                    t3.setVisibility(View.INVISIBLE);
                    //ojojjo
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
                    for (int i = 0; i < 5; i++){
                        t1.setVisibility(View.INVISIBLE);
                        t2.setVisibility(View.INVISIBLE);
                        t3.setVisibility(View.INVISIBLE);

                        float dist = getDist(gard2, bugs_right[i]);

                        if (dist < 300 && dist > 200) {
                            bugs_right[i].setVisibility(View.INVISIBLE);
                            t2.setVisibility(View.VISIBLE);
                            score_value+=5;
                            score.setText(String.valueOf(score_value/5));
                        } else if (dist < 200) {
                            bugs_right[i].setVisibility(View.INVISIBLE);
                            t1.setVisibility(View.VISIBLE);
                            score_value+=10;
                            score.setText(String.valueOf(score_value/5));
                        }
                        else {
                            t3.setVisibility(View.VISIBLE);
                            life_value-=1;
                            life.setText(String.valueOf(life_value/5));
                        }
                    }
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
