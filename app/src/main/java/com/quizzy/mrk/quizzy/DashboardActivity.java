package com.quizzy.mrk.quizzy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.DashboardModele;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RequestQueue requestQueue;
    private DashboardModele dashboardModele;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToolbar;

    private ListView lvQuizNotFinish;
    private ArrayList<Quiz> quizNotFinished;
    private Button bNewQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_dashboard));

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.dashboardModele = new DashboardModele(this, this.requestQueue);

        this.mDrawerLayout = findViewById(R.id.dashboard_drawer);
        this.mToolbar = new ActionBarDrawerToggle(this, this.mDrawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);
        this.mDrawerLayout.addDrawerListener(this.mToolbar);
        this.mToolbar.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set element in navigation drawer
        NavigationView navigationView = findViewById(R.id.dashboard_nav);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView ivUserImg = headerView.findViewById(R.id.header_nav_img);
        Picasso.with(this).load(Session.getSession().getUser().getMedia()).into(ivUserImg);
        TextView tvNameUser = headerView.findViewById(R.id.header_nav_name);
        tvNameUser.setText(Session.getSession().getUser().getFirstName() + " " + Session.getSession().getUser().getLastName());
        TextView tvEmailUser = headerView.findViewById(R.id.header_nav_email);
        tvEmailUser.setText(Session.getSession().getUser().getEmail());

        this.lvQuizNotFinish = findViewById(R.id.list_dashboard_quiz_not_finished);
        this.manageQuizNotFinished();

        this.bNewQuiz = findViewById(R.id.btn_dashboard_new_quiz);
        this.bNewQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle paquet = new Bundle();
                paquet.putBoolean("new_quiz", true);
                Intent intent = new Intent(DashboardActivity.this, QuizActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
            }
        });

    }

    private void manageQuizNotFinished(){

        this.dashboardModele.getQuizNotFinished(Session.getSession().getUser(), new DashboardModele.DashboardCallBack() {
            @Override
            public void onSuccess(ArrayList<Quiz> listQuizNotFinished) {

                // Liste contenant les noms des quiz
                ArrayList<String> listItems = new ArrayList<String>();

                quizNotFinished = listQuizNotFinished;

                // Boucle pour afficher seulement le nom des quiz dans le dashboard
                for(Quiz quiz : quizNotFinished) {
                    String quizName = quiz.getName();
                    listItems.add(quizName);
                }
                ArrayAdapter<String> adaptateur = new ArrayAdapter<String>(DashboardActivity.this, android.R.layout.simple_list_item_1, listItems) ;
                lvQuizNotFinish.setAdapter(adaptateur);

                lvQuizNotFinish.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle paquet = new Bundle();
                                paquet.putBoolean("new_quiz", false);
                                paquet.putParcelable("quiz", quizNotFinished.get(position) );
                                Intent intent = new Intent(DashboardActivity.this, QuizActivity.class);
                                intent.putExtras(paquet);
                                startActivity(intent);
                            }
                        }
                );
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_connexion), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_connexion), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToolbar.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        if (menuItem.getItemId() == R.id.menu_drawer_profil) { // item profil

        } else if (menuItem.getItemId() == R.id.menu_drawer_friend) { // item mes amis

        } else if (menuItem.getItemId() == R.id.menu_drawer_quiz) { // item mes quiz

        } else if (menuItem.getItemId() == R.id.menu_drawer_a_propos) { // item à propos
            mDrawerLayout.closeDrawers();
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.txt_a_propos_title));
            alertDialog.setMessage(getString(R.string.txt_a_propos_message));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else if (menuItem.getItemId() == R.id.menu_drawer_logout) {  // item deconnexion
            Session.getSession().fermer();
            intent = new Intent(DashboardActivity.this, ConnexionActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
