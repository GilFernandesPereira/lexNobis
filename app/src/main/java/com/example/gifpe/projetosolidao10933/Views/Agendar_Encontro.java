package com.example.gifpe.projetosolidao10933.Views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933.Controllers.Encontros;
import com.example.gifpe.projetosolidao10933.Models.Encontro;
import com.example.gifpe.projetosolidao10933.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Locale;

public class Agendar_Encontro extends AppCompatActivity implements View.OnClickListener {

    private Button btnHora;
    private Button btnData;
    private EditText etdata;
    private EditText etHora;
    private int dia, mes, ano, minutos, horas;
    private ImageView ivTeste;
    private TextToSpeech tts;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String idInstituicao, nifSenior;
    Boolean agendar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar__encontro);

        findViewById(R.id.btnHora).setOnClickListener(this);
        findViewById(R.id.btnData).setOnClickListener(this);
        findViewById(R.id.btnMarcarEncontro).setOnClickListener(this);
        etdata = (EditText) findViewById(R.id.etData);
        etHora = (EditText) findViewById(R.id.etHora);
        idInstituicao = getIntent().getStringExtra("idDaInstituicao");
        nifSenior = getIntent().getStringExtra("nif");

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.getDefault());//new Locale("pt", "POR")
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnData:
                final Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                ano = c.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        etdata.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }
                        , ano, mes, dia);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.btnHora:
                final Calendar cc = Calendar.getInstance();
                horas = cc.get(Calendar.HOUR_OF_DAY);
                minutos = cc.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int Minute) {
                        etHora.setText(hourOfDay + ":" + Minute);
                    }
                }, horas, minutos, true);
                timePickerDialog.show();
                break;
            case R.id.btnMarcarEncontro:
                String data = etdata.getText().toString();
                String hora = etHora.getText().toString();
                String estado = "Em aberto";
                if ((data.isEmpty())) {
                    etdata.setError("A data é obrigatória!");
                    etdata.requestFocus();
                    return;
                }
                if ((hora.isEmpty())) {
                    etHora.setError("A hora é obrigatorio!");
                    etHora.requestFocus();
                    return;
                }
                Encontro encontro = new Encontro(data, hora, estado, 0);
                if (idInstituicao == null) {
                    Toast.makeText(getApplicationContext(), "Não foi possível agendar a missão. Por favor, agende novamente!", Toast.LENGTH_SHORT).show();
                    Intent intentt = new Intent(getApplicationContext(), Menu_Paciente.class);
                    startActivity(intentt);
                } else {
                    agendar = Encontros.AgendarEncontro(encontro, idInstituicao, nifSenior);
                    if (agendar == true) {
                        String ToSpeak = "A missão ficou marcada para a data " + data + " às " + hora + ".";//O encontro ficou marcado para o dia "+dia+" do mês "+mes+" do ano "+ano+", às "+horas+" horas e "+ minutos+" minutos.
                        Toast.makeText(getApplicationContext(), "Missão marcada com sucesso!", Toast.LENGTH_SHORT).show();
                        tts.speak(ToSpeak, TextToSpeech.QUEUE_FLUSH, null);
                        Intent intent = new Intent(getApplicationContext(), Menu_Paciente.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Não foi possível marcar a missão com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Menu_Paciente.class);
                        startActivity(intent);
                    }
                }

                break;
        }
    }
}
