package com.example.gifpe.projetosolidao10933instituicao.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.gifpe.projetosolidao10933instituicao.Fragments.DefinicoesFragment;
import com.example.gifpe.projetosolidao10933instituicao.Fragments.EventosFragment;
import com.example.gifpe.projetosolidao10933instituicao.Fragments.MenuFragment;
import com.example.gifpe.projetosolidao10933instituicao.Fragments.PerfilFragment;
import com.example.gifpe.projetosolidao10933instituicao.Fragments.AlterarPasswordFragment;
import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private TextView tvNomeInst;
    private TextView tvEmailEmpresa;
    private TextView tvNome;
    private FirebaseUser user;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DataSnapshot dataSnapshot;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mUserID;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);

        displaySelectedScreen(R.id.imgMenu);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserID=user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Instituicoes/"+mUserID);//child("ADICA").child(mUserID)

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
//        }

//        FloatingActionButton fab =  findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        drawer =  findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
        mDrawerToggle= new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headview=navigationView.getHeaderView(0);
        TextView tvEmail=headview.findViewById(R.id.tvEmailInstituicao);
        tvNome=headview.findViewById(R.id.tvNomeInstituicao);
        tvEmail.setText(user.getEmail());
        final String nomeInst;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ShowData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void ShowData(DataSnapshot data){
        for(DataSnapshot ds: data.getChildren()){
            Instituicao inst=new Instituicao();
            inst.setNome(ds.child("/Informações da Empresa").getValue(Instituicao.class).getNome());
            tvNome.setText(inst.getNome());
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão dos settings, caso queira aquela opcap
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id){
        Fragment fragment=null;

        if (id == R.id.imgPerfil) {
            fragment= new PerfilFragment();
        } else if (id == R.id.imgEventos) {
            fragment= new EventosFragment();
        } else if (id == R.id.imgProcurar) {
            fragment= new AlterarPasswordFragment();
        } else if (id == R.id.imgDefenicao) {
            fragment= new DefinicoesFragment();
        } else if (id == R.id.imgTerminarSessao) {
                new AlertDialog.Builder(this)
                        .setMessage("Terminar Sessão?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.getInstance().signOut();
                                Intent sair = new Intent(getApplicationContext(), Login.class);
                                sair.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                getApplicationContext().startActivity(sair);
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();


        } else if (id == R.id.imgMenu) {
            fragment= new MenuFragment();
        }
        if(fragment!=null){
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.container,fragment);
            fragmentTransaction.commit();//Fica cortado o layout, como viste na imagem
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.//ja aparece o icon do menu?nao ainda nao aparece
        int id = item.getItemId();

//        Fragment fragment=null;
//
//        if (id == R.id.imgPerfil) {
//             fragment= new PerfilFragment();
//        } else if (id == R.id.imgEventos) {
//
//        } else if (id == R.id.imgProcurar) {
//
//        } else if (id == R.id.imgDefenicao) {
//
//        } else if (id == R.id.imgTerminarSessao) {
//
//        } else if (id == R.id.imgMenu) {
//
//        }
//        if(fragment!=null){
//            FragmentManager fragmentManager=getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//
//            fragmentTransaction.replace(R.id.screenArea,fragment);
//            fragmentTransaction.commit();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        displaySelectedScreen(id);
        return true;
    }


    private void CarregarNomes(){

        tvEmailEmpresa=(TextView)findViewById(R.id.tvEmailInstituicao);
        tvNomeInst=(TextView)findViewById(R.id.tvNomeInstituicao);//estou a abrir o meu projeto para ver uma coisa ok
        Log.d("############",user.getEmail().toString());//antes isto ia buscar, agora ja da erro
//        tvEmailEmpresa.setText(user.getEmail().toString());
    }
}
