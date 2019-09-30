package com.example.gifpe.projetosolidao10933.Controllers;
import com.example.gifpe.projetosolidao10933.Models.Encontro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Encontros {

    public static boolean AgendarEncontro(Encontro encontro, String idInstituicao, String nifDoSenior)
    {
        try {
            String mUser;
            FirebaseAuth mAuth;
            FirebaseUser user;
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            mUser=user.getUid();
            Date date = new Date();
            Date newDate = new Date(date.getTime());
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            String stringdate = dt.format(newDate);
            //Fazer depois uma restrição, marcar uma por dia.
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("ProjetoSolidao/"+idInstituicao+"/"+nifDoSenior+"/"+stringdate);//"Encontro marcado no dia " -> era o que estava, agora está a data e a hora.|| Acresentei o nif, no futuro arranjar solução para isto
            myRef.setValue(encontro);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
