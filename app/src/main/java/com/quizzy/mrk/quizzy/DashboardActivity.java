package com.quizzy.mrk.quizzy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.DashboardModele;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RequestQueue requestQueue;
    private DashboardModele dashboardModele;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToolbar;

    private ListView lvQuizNotFinish;
    private ArrayList<Quiz> quizNotFinished;
    private Button bNewQuiz;

    private ListView lvQuizShared;
    private ArrayList<Quiz> quizShared;

    private TextView tvBadgeFriendsRequest;

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

        this.lvQuizNotFinish = findViewById(R.id.list_dashboard_quiz_not_finished);
        this.lvQuizShared = findViewById(R.id.list_dashboard_quiz_shared);
        this.manageLists();

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

    private void updateDataUserInNavigation(int friendsRequestCounter) {
        NavigationView navigationView = findViewById(R.id.dashboard_nav);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ImageView ivUserImg = headerView.findViewById(R.id.header_nav_img);
        Picasso.with(this).load(Session.getSession().getUser().getMedia()).into(ivUserImg);
        TextView tvNameUser = headerView.findViewById(R.id.header_nav_name);
        tvNameUser.setText(Session.getSession().getUser().getFirstName() + " " + Session.getSession().getUser().getLastName());
        TextView tvEmailUser = headerView.findViewById(R.id.header_nav_email);
        tvEmailUser.setText(Session.getSession().getUser().getEmail());

        tvBadgeFriendsRequest = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.menu_drawer_friends_request));
        tvBadgeFriendsRequest.setGravity(Gravity.CENTER_VERTICAL);
        tvBadgeFriendsRequest.setTypeface(null,Typeface.BOLD);
        tvBadgeFriendsRequest.setTextColor(getResources().getColor(R.color.red));
        if (friendsRequestCounter > 0) {
            tvBadgeFriendsRequest.setText("" + friendsRequestCounter);
        } else {
            tvBadgeFriendsRequest.setText("");
        }
    }

    private void manageLists(){
        this.dashboardModele.getQuizNotFinished(Session.getSession().getUser(), new DashboardModele.DashboardCallBack() {
            @Override
            public void onSuccess(ArrayList<Quiz> listQuizNotFinished, ArrayList<Quiz> listQuizShared, int friendsRequestCounter) {
                // Liste contenant les noms des quiz
                ArrayList<String> itemQuizNotFinished = new ArrayList<String>();
                ArrayList<String> itemQuizShared = new ArrayList<String>();
                quizNotFinished = listQuizNotFinished;
                quizShared = listQuizShared;

                // Boucle pour afficher seulement le nom des quiz dans le dashboard
                for(Quiz quiz : quizNotFinished) {
                    String quizName = quiz.getName();
                    itemQuizNotFinished.add(quizName);
                }
                for(Quiz quiz : quizShared) {
                    String quizName = quiz.getName();
                    itemQuizShared.add(quizName);
                }

                ArrayAdapter<String> adaptateurQuizNotFinished = new ArrayAdapter<String>(DashboardActivity.this, android.R.layout.simple_list_item_1, itemQuizNotFinished) ;
                ArrayAdapter<String> adaptateurQuizShared = new ArrayAdapter<String>(DashboardActivity.this, android.R.layout.simple_list_item_1, itemQuizShared) ;
                lvQuizNotFinish.setAdapter(adaptateurQuizNotFinished);
                lvQuizShared.setAdapter(adaptateurQuizShared);

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
                lvQuizShared.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle paquet = new Bundle();
                                paquet.putBoolean("new_quiz", false);
                                paquet.putParcelable("quiz", quizShared.get(position) );
                                Intent intent = new Intent(DashboardActivity.this, ResumQuizActivity.class);
                                intent.putExtras(paquet);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        }
                );
                updateDataUserInNavigation(friendsRequestCounter);
            }

            @Override
            public void onErrorNetwork() {
            }

            @Override
            public void onErrorVollet() {

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
            intent = new Intent(DashboardActivity.this, ProfilActivity.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_drawer_friend) { // item mes amis
            intent = new Intent(DashboardActivity.this, ListeAmisActivity.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_drawer_quiz) { // item mes quiz
            intent = new Intent(DashboardActivity.this, MesQuizActivity.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_drawer_friends_request) { // item mes quiz
            intent = new Intent(DashboardActivity.this, FriendsRequestActivity.class);
            startActivity(intent);
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
