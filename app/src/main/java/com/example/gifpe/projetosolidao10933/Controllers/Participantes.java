package com.example.gifpe.projetosolidao10933.Controllers;
import com.example.gifpe.projetosolidao10933.Models.Participante;

import java.sql.Date;
import java.sql.Time;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Participantes {


    public static boolean AgendarEncontro(Date data, Time horas)
    {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Encontro");
            myRef.setValue(data);
            myRef.setValue(horas);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
