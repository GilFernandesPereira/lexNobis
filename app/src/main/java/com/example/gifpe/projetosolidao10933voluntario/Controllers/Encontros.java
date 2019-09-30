package com.example.gifpe.projetosolidao10933voluntario.Controllers;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Encontros {

    DatabaseReference reff, ref;
    private Context context;

    public Encontros (Context context){this.context = context;}

    public void AtualizaMissao( String IDFINAL, String nifParticipante, String dataEncontroMarcado, String feedback){
        reff = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/estado");
        reff.setValue("Terminado");
        ref= reff = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/feedback");
        ref.setValue(feedback).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Feedback submetido com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
