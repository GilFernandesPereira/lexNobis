package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu,null);
    }

    private String mUserID;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private  DatabaseReference ref;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Declaração de variaveis
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserID=user.getUid();
        //endregion

        view.findViewById(R.id.btnAddSolitario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment= new AddParticipanteFragment();
                android.support.v4.app.FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
            }
        });

        view.findViewById(R.id.btnAddVoluntario).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                Fragment fragment= new AddVoluntarioFragment();//
                android.support.v4.app.FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
            }
        });

        view.findViewById(R.id.btnListarVoluntario).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                Fragment fragment= new ListarVoluntariosFragment();//
                android.support.v4.app.FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
            }
        });

        view.findViewById(R.id.btnListarSeniores).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                Fragment fragment= new ListarSenioresFragment();//
                android.support.v4.app.FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
            }
        });
        //Add to Activity
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        initFCM();//se quiseres apagar o token é aqui!!
    }

    private void sendRegistrationToServer(String token) {
        Log.d("TOKEN", "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUserID + "/token");
        reference.setValue(token);
    }


    private void initFCM(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token Inicial", "initFCM: token: " + token);
        sendRegistrationToServer(token);

    }
}
