package com.example.gifpe.projetosolidao10933.Views;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933.Controllers.Encontros;
import com.example.gifpe.projetosolidao10933.Controllers.ObjetosListViews;
import com.example.gifpe.projetosolidao10933.Models.Encontro;
import com.example.gifpe.projetosolidao10933.Models.ObjetosListView;
import com.example.gifpe.projetosolidao10933.Models.Participante;
import com.example.gifpe.projetosolidao10933.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Menu_Paciente extends AppCompatActivity implements View.OnClickListener {

    private TextToSpeech tts;
    private ImageView ivAgendar;
    private ImageView ivVisualizar, ivFotoAnimador;
    private static String mes, alturaDoDia, horas, alturaDoDiaBD, horasBD;
    private static int numeroDoMes, dia;
    private String falaDoUtilizador, mUserPhone, IDFINAL, NIFSenior;
    private final int ID_PARA_TEXTO_VOZ = 100;
    private final int ID_PARA_TEXTO_MES = 101;
    private final int ID_PARA_TEXTO_DIA = 102;
    private final int ID_PARA_TEXTO_HORA = 103;
    private TextView tvTextoFalado, tvNomeAnimador, tvDataMissao, tvHoraMissao, tvEstadoMissao, tvQuestionario;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference ref, myRef, reff;
    private String nrCompleto;
    StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__paciente);

        //region Declaração de variaveis
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Gil Pereira/ CR");

        tvTextoFalado = (TextView) findViewById(R.id.tvTextoFalado);
        tvNomeAnimador=findViewById(R.id.tvNomeAnimador);
        tvDataMissao=findViewById(R.id.tvDataMissao);
        tvEstadoMissao=findViewById(R.id.tvEstadoMissao);
        tvHoraMissao=findViewById(R.id.tvHoraMissao);
        tvQuestionario=findViewById(R.id.tvQuestionario);
        ivFotoAnimador=findViewById(R.id.ivFotoAnimador);
        findViewById(R.id.btnMarcarConsulta).setOnClickListener(this);
        findViewById(R.id.btnVerificarEncontros).setOnClickListener(this);
        findViewById(R.id.microfoneParaFalar).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserPhone = user.getPhoneNumber();
        //endregion

        //region Buscar informação
        ref = FirebaseDatabase.getInstance().getReference().child("Instituicoes");
        ref.addValueEventListener(new ValueEventListener() {
            boolean aux = false;
            String numeroVerifica;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    numeroVerifica = ds.getValue().toString();//child("nrTelemovel")
                    Log.d("################3", numeroVerifica);

                    //Vai buscar o Nif do senior - Podes apagar
                    String nifTodo = numeroVerifica.substring(numeroVerifica.indexOf(" nif="));
                    String ApenasONif = nifTodo.substring(5, 14);
                    //Vai buscar o id da respetiva instituicao
                    String substr = numeroVerifica.substring(numeroVerifica.indexOf(" idInstituicao=") + 1);
                    String substrr = substr.substring(14, 42);
                    nrCompleto = mUserPhone.substring(4, 13);
                    if(ds.getValue().toString().contains(nrCompleto)){
//                        Toast.makeText(getApplicationContext(),ds.getKey().toString(),Toast.LENGTH_SHORT).show();
                        IDFINAL=ds.getKey().toString();
                        Participante();
                        aux = true;
                        break;
                    }
                }
                if (!aux)
                    Toast.makeText(getApplicationContext(), "Não se encontra registado na plataforma!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        //endregion
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
            case R.id.btnMarcarConsulta:
                Intent AgendarEncontro = new Intent(Menu_Paciente.this, Agendar_Encontro.class);
                AgendarEncontro.putExtra("idDaInstituicao", IDFINAL);
                AgendarEncontro.putExtra("nif", NIFSenior);
                startActivity(AgendarEncontro);
                break;
            case R.id.btnVerificarEncontros:
                Intent ListarEncontro = new Intent(Menu_Paciente.this, Listar_Encontros.class);
                ListarEncontro.putExtra("idDaInstituicao", IDFINAL);
                ListarEncontro.putExtra("nif", NIFSenior);
                startActivity(ListarEncontro);
                break;
            case R.id.microfoneParaFalar:
                Intent iVoz = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                try {
                    startActivityForResult(iVoz, ID_PARA_TEXTO_VOZ);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Algo Deu Errado!", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.ativar:
////                Intent Ativar = new Intent(Menu_Paciente.this, Ativar_conta.class);
////                startActivity(Ativar);
//                break;
        }
    }

    @Override
    protected void onActivityResult(int id, int resultCode, Intent data) {
        super.onActivityResult(id, resultCode, data);
        switch (id) {

            case ID_PARA_TEXTO_VOZ:
                //region Case ID_PARA_TEXTO_VOZ
                Log.d("######################", "Entrou");
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("######################", "Entrou2");
                    falaDoUtilizador = result.get(0);
                    //Toast.makeText(getApplicationContext(),falaDoUtilizador,Toast.LENGTH_SHORT).show();
                    tvTextoFalado.setText(falaDoUtilizador);
                }
                agendarEncontro();
                //endregion
                break;

            case ID_PARA_TEXTO_MES:
                //region Case ID_PARA_TEXTO_MES
                boolean mesBoolean;
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    falaDoUtilizador = result.get(0);
                    //Toast.makeText(getApplicationContext(),falaDoUtilizador,Toast.LENGTH_SHORT).show();
                    tvTextoFalado.setText(falaDoUtilizador);
                }
                mesBoolean = agendarEncontroMes();
                if (mesBoolean == true) {
                    agendarEncontroDia();
                } else {
                    tts.speak("Diga de novo qual o mês que pretende para agendar a missão.", TextToSpeech.QUEUE_FLUSH, null);
                    while (tts.isSpeaking()) {
                        Log.d("##", "Não faças nada enquanto falas!");
                    }
                    Intent iVoz = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    try {
                        startActivityForResult(iVoz, ID_PARA_TEXTO_MES);
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(), "Algo Deu Errado!", Toast.LENGTH_SHORT).show();
                    }
                }
                //endregion
                break;

            case ID_PARA_TEXTO_DIA:
                //region Case ID_PARA_TEXTO_DIA
                boolean diaa;
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    falaDoUtilizador = result.get(0);
                    //Toast.makeText(getApplicationContext(),falaDoUtilizador,Toast.LENGTH_SHORT).show();
                    tvTextoFalado.setText(falaDoUtilizador);
                }
                diaa = agendarEncontroASeguirDia();
                if (diaa == true) {
                    agendarEncontroHora();
                } else {
                    tts.speak("Diga de novo qual o dia que pretende para agendar a missão.", TextToSpeech.QUEUE_FLUSH, null);
                    while (tts.isSpeaking()) {
                        Log.d("##", "Não faças nada enquanto falas!");
                    }
                    Intent iVoz = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    try {
                        startActivityForResult(iVoz, ID_PARA_TEXTO_DIA);
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(), "Algo Deu Errado!", Toast.LENGTH_SHORT).show();
                    }
                }
                //endregion
                break;

            case ID_PARA_TEXTO_HORA:
                //region Case ID_PARA_TEXTO_HORA
                String dataDoEncontro, horaDoEncontro;
                boolean agendar, marcarHora;
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    falaDoUtilizador = result.get(0);
                    //Toast.makeText(getApplicationContext(),falaDoUtilizador,Toast.LENGTH_SHORT).show();
                    tvTextoFalado.setText(falaDoUtilizador);
                }
                //agendarEncontroASeguirHora();
                marcarHora = agendarEncontroASeguirHora();
                if (marcarHora == true) {
                    //String pergunta="O encontro ficou marcado para o dia"+ dia +" do mês" +mes+ "às "+horas+" horas da "+ alturaDoDia + "!";
                    //tts.speak(pergunta, TextToSpeech.QUEUE_FLUSH, null);

                    dataDoEncontro = dia + "/" + numeroDoMes + "/" + Calendar.getInstance().get(Calendar.YEAR);
                    horaDoEncontro = horasBD + " " + alturaDoDiaBD;

                    //region Verifica se está antes do dia atual
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    String dataAtual = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Date dataAtuall=format.parse(dataAtual, new ParsePosition(0));
                    Date dataMissao=format.parse(dataDoEncontro, new ParsePosition(0));
//                    if(dataAtual.compareTo(dataDoEncontro)>0){
//                        tts.speak("A data que indicou não existe! Agende novamente um novo encontro!", TextToSpeech.QUEUE_FLUSH, null);
//                        while (tts.isSpeaking()) {
//                            Log.d("##", "Não faças nada enquanto falas!");
//                        }
//                    }
                    String substr=dataAtual.substring(dataAtual.indexOf(""));
                    String substrr=substr.substring(0,10);
                    //endregion

                    if ((dataAtuall.compareTo(dataMissao)>=0) || (dataDoEncontro.equals("29/2/2019")) || (dataDoEncontro.equals("30/2/2019")) || (dataDoEncontro.equals("31/2/2019")) || (dataDoEncontro.equals("31/4/2019")) || (dataDoEncontro.equals("31/6/2019")) || (dataDoEncontro.equals("31/9/2019")) || (dataDoEncontro.equals("31/11/2019"))) {
                        tts.speak("A data que indicou não está correta! Agende novamente uma nova missão!", TextToSpeech.QUEUE_FLUSH, null);
                        while (tts.isSpeaking()) {
                            Log.d("##", "Não faças nada enquanto falas!");
                        }
                    } else {
                        Encontro encontro = new Encontro(dataDoEncontro, horaDoEncontro, "Em aberto",0);
                        agendar = Encontros.AgendarEncontro(encontro, IDFINAL, NIFSenior);
                        if (agendar == true) {
                            String ToSpeak = "A missão ficou marcada para o dia" + dia + " do mês de " + mes + " às " + horas + " horas da " + alturaDoDia + "!";//O encontro ficou marcado para o dia "+dia+" do mês "+mes+" do ano "+ano+", às "+horas+" horas e "+ minutos+" minutos.
                            Toast.makeText(getApplicationContext(), "Missão marcada com sucesso!", Toast.LENGTH_SHORT).show();
                            Participante();//
                            tts.speak(ToSpeak, TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            Toast.makeText(getApplicationContext(), "Não foi possível marcar a missão com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    tts.speak("Diga de novo qual a hora que pretende para agendar a missão.", TextToSpeech.QUEUE_FLUSH, null);
                    while (tts.isSpeaking()) {
                        Log.d("##", "Não faças nada enquanto falas!");
                    }
                    Intent iVoz = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    try {
                        startActivityForResult(iVoz, ID_PARA_TEXTO_HORA);
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(), "Algo Deu Errado!", Toast.LENGTH_SHORT).show();
                    }
                }
                //endregion
                break;
        }

    }

    private void agendarEncontro() {
        String contemTexto;
        if ((falaDoUtilizador.contains("agendar")) || (falaDoUtilizador.contains("marcar")) || (falaDoUtilizador.contains("encontro")) || (falaDoUtilizador.contains("missão")) || (falaDoUtilizador.contains("queria")) || (falaDoUtilizador.contains("Agendar")) || (falaDoUtilizador.contains("Marcar")) || (falaDoUtilizador.contains("Encontro")) || (falaDoUtilizador.contains("Queria"))) {
            Log.d("######################", "Disse uma das palavras!");
            String pergunta = "Voçe pretende agendar uma missão?";
            tts.speak(pergunta, TextToSpeech.QUEUE_FLUSH, null);
            alertDialog();
        } else if ((falaDoUtilizador.contains("visualizar")) || (falaDoUtilizador.contains("mostar")) || (falaDoUtilizador.contains("marcados")) || (falaDoUtilizador.contains("listar")) || (falaDoUtilizador.contains("eventos")) || (falaDoUtilizador.contains("ver")) || (falaDoUtilizador.contains("verificar")) || (falaDoUtilizador.contains("quais")) || (falaDoUtilizador.contains("Ver"))) {
            Intent ListarEncontro = new Intent(Menu_Paciente.this, Listar_Encontros.class);
            ListarEncontro.putExtra("idDaInstituicao", IDFINAL);
            ListarEncontro.putExtra("nif", NIFSenior);
            startActivity(ListarEncontro);
        }
    }

    private void alertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = Menu_Paciente.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_confirmacao_agendar_encontro, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final Button button_ok = dialogView.findViewById(R.id.btnSim);
        final Button button_cancel = dialogView.findViewById(R.id.btnNao);
        final AlertDialog b = dialog.create();
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Selecionou Sim", Toast.LENGTH_SHORT).show();
                tts.speak("Diga qual o mês que pretende para agendar a missão.", TextToSpeech.QUEUE_FLUSH, null);
                while (tts.isSpeaking()) {
                    Log.d("##", "Não faças nada enquanto falas!");
                }
                b.dismiss();
                //escolha do mês
                Intent iVoz = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                try {
                    startActivityForResult(iVoz, ID_PARA_TEXTO_MES);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Algo Deu Errado!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });
        b.show();

    }

    private boolean agendarEncontroMes() {
        //region Verificar se disse os meses.
        if ((falaDoUtilizador.contains("Janeiro")) || (falaDoUtilizador.contains("janeiro"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Janeiro";
            numeroDoMes = 1;
            return true;
        } else if ((falaDoUtilizador.contains("Fevereiro")) || (falaDoUtilizador.contains("fevereiro"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Fevereiro";
            numeroDoMes = 2;
            return true;
        } else if ((falaDoUtilizador.contains("Março")) || (falaDoUtilizador.contains("março"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Março";
            numeroDoMes = 3;
            return true;
        } else if ((falaDoUtilizador.contains("Abril")) || (falaDoUtilizador.contains("abril"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Abril";
            numeroDoMes = 4;
            return true;
        } else if ((falaDoUtilizador.contains("Maio")) || (falaDoUtilizador.contains("maio"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Maio";
            numeroDoMes = 5;
            return true;
        } else if ((falaDoUtilizador.contains("Junho")) || (falaDoUtilizador.contains("junho"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Junho";
            numeroDoMes = 6;
            return true;
        } else if ((falaDoUtilizador.contains("Julho")) || (falaDoUtilizador.contains("julho"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Julho";
            numeroDoMes = 7;
            return true;
        } else if ((falaDoUtilizador.contains("Agosto")) || (falaDoUtilizador.contains("agosto"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Agosto";
            numeroDoMes = 8;
            return true;
        } else if ((falaDoUtilizador.contains("Setembro")) || (falaDoUtilizador.contains("setembro"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Setembro";
            numeroDoMes = 9;
            return true;
        } else if ((falaDoUtilizador.contains("Outubro")) || (falaDoUtilizador.contains("outubro"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Outubro";
            numeroDoMes = 10;
            return true;
        } else if ((falaDoUtilizador.contains("Novembro")) || (falaDoUtilizador.contains("novembro"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Novembro";
            numeroDoMes = 11;
            return true;
        } else if ((falaDoUtilizador.contains("Dezembro")) || (falaDoUtilizador.contains("dezembro"))) {
            Log.d("######################", "Disse uma das palavras!");
            mes = "Dezembro";
            numeroDoMes = 12;
            return true;
        }
        //endregion
        else {
            String pergunta = "Desculpe, Não entendemos qual o mês que pretende!";
            tts.speak(pergunta, TextToSpeech.QUEUE_FLUSH, null);
            while (tts.isSpeaking()) {
                Log.d("##", "Não faças nada enquanto falas!");
            }
            return false;
        }
    }

    private void agendarEncontroDia() {
        tts.speak("Diga qual o dia que pretende para agendar a missão.", TextToSpeech.QUEUE_FLUSH, null);
        while (tts.isSpeaking()) {
            Log.d("##", "Não faças nada enquanto falas!");
        }
        Intent iVoz = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(iVoz, ID_PARA_TEXTO_DIA);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Algo Deu Errado!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean agendarEncontroASeguirDia() {
        //region Todas os dias que o cliente pode escolher
        if ((falaDoUtilizador.contains("trinta e um")) || (falaDoUtilizador.contains("31"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 31;
            return true;
        } else if ((falaDoUtilizador.contains("trinta")) || (falaDoUtilizador.contains("30"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 30;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e nove")) || (falaDoUtilizador.contains("29"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 29;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e oito")) || (falaDoUtilizador.contains("28"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 28;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e sete")) || (falaDoUtilizador.contains("27"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 27;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e seis")) || (falaDoUtilizador.contains("26"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 26;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e cinco")) || (falaDoUtilizador.contains("25"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 25;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e quatro")) || (falaDoUtilizador.contains("24"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 24;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e três")) || (falaDoUtilizador.contains("23"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 23;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e dois")) || (falaDoUtilizador.contains("22"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 22;
            return true;
        } else if ((falaDoUtilizador.contains("vinte e um")) || (falaDoUtilizador.contains("21"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 21;
            return true;
        } else if ((falaDoUtilizador.contains("vinte")) || (falaDoUtilizador.contains("20"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 20;
            return true;
        } else if ((falaDoUtilizador.contains("dezanove")) || (falaDoUtilizador.contains("19"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 19;
            return true;
        } else if ((falaDoUtilizador.contains("dezoito")) || (falaDoUtilizador.contains("18"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 18;
            return true;
        } else if ((falaDoUtilizador.contains("dezassete")) || (falaDoUtilizador.contains("17"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 17;
            return true;
        } else if ((falaDoUtilizador.contains("dezasseis")) || (falaDoUtilizador.contains("16"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 16;
            return true;
        } else if ((falaDoUtilizador.contains("quinze")) || (falaDoUtilizador.contains("15"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 15;
            return true;
        } else if ((falaDoUtilizador.contains("catorze")) || (falaDoUtilizador.contains("14"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 14;
            return true;
        } else if ((falaDoUtilizador.contains("treze")) || (falaDoUtilizador.contains("13"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 13;
            return true;
        } else if ((falaDoUtilizador.contains("doze")) || (falaDoUtilizador.contains("12"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 12;
            return true;
        } else if ((falaDoUtilizador.contains("onze")) || (falaDoUtilizador.contains("11"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 11;
            return true;
        } else if ((falaDoUtilizador.contains("dez")) || (falaDoUtilizador.contains("10"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 10;
            return true;
        } else if ((falaDoUtilizador.contains("nove")) || (falaDoUtilizador.contains("9"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 9;
            return true;
        } else if ((falaDoUtilizador.contains("oito")) || (falaDoUtilizador.contains("8"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 8;
            return true;
        } else if ((falaDoUtilizador.contains("sete")) || (falaDoUtilizador.contains("7"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 7;
            return true;
        } else if ((falaDoUtilizador.contains("seis")) || (falaDoUtilizador.contains("6"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 6;
            return true;
        } else if ((falaDoUtilizador.contains("cinco")) || (falaDoUtilizador.contains("5"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 5;
            return true;
        } else if ((falaDoUtilizador.contains("quatro")) || (falaDoUtilizador.contains("4"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 4;
            return true;
        } else if ((falaDoUtilizador.contains("três")) || (falaDoUtilizador.contains("3"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 3;
            return true;
        } else if ((falaDoUtilizador.contains("dois")) || (falaDoUtilizador.contains("2"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 2;
            return true;
        } else if ((falaDoUtilizador.contains("um")) || (falaDoUtilizador.contains("1"))) {
            Log.d("######################", "Disse uma das palavras!");
            dia = 1;
            return true;
        }
        //endregion
        else {
            String pergunta = "Desculpe, Não entendemos qual o dia que pretende!";
            tts.speak(pergunta, TextToSpeech.QUEUE_FLUSH, null);
            while (tts.isSpeaking()) {
                Log.d("##", "Não faças nada enquanto falas!");
            }
            return false;
        }
    }

    private void agendarEncontroHora() {
        tts.speak("Diga qual a hora que pretende para agendar a missão.", TextToSpeech.QUEUE_FLUSH, null);
        while (tts.isSpeaking()) {
            Log.d("##", "Não faças nada enquanto falas!");
        }
        Intent iVoz = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        iVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(iVoz, ID_PARA_TEXTO_HORA);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Algo Deu Errado!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean agendarEncontroASeguirHora() {
        //region Todas as horas que o cliente pode escolher
        if ((falaDoUtilizador.contains("doze e quarenta e cinco"))  || (falaDoUtilizador.contains("12:45")) ||
                (falaDoUtilizador.contains("meio-dia e quarenta e cinco")) || (falaDoUtilizador.contains("uma menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "12:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "doze e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("doze e trinta")) || (falaDoUtilizador.contains("12:30")) ||
                (falaDoUtilizador.contains("meio-dia e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "12:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "doze e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("doze e quinze")) || (falaDoUtilizador.contains("12:15")) ||
                (falaDoUtilizador.contains("meio-dia e e quinze"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "12:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "doze e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("doze")) || (falaDoUtilizador.contains("12:00")) || (falaDoUtilizador.contains("meio-dia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "12:00";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "AM";
            horas = "doze";
            return true;
        } else if ((falaDoUtilizador.contains("onze e quarenta e cinco")) || (falaDoUtilizador.contains("11:45")) ||
                (falaDoUtilizador.contains("doze menos um quarto")) || (falaDoUtilizador.contains("meio-dia menos um quarto")) || (falaDoUtilizador.contains("12 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "11:45";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "onze e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("onze e trinta")) || (falaDoUtilizador.contains("11:30")) ||
                (falaDoUtilizador.contains("onze e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "11:30";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "onze e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("onze e quinze")) || (falaDoUtilizador.contains("11:15")) ||
                (falaDoUtilizador.contains("onze e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "11:15";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "onze e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("onze")) || (falaDoUtilizador.contains("11:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "11:00";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "onze";
            return true;
        } else if ((falaDoUtilizador.contains("dez e quarenta e cinco")) || (falaDoUtilizador.contains("10:45")) ||
                (falaDoUtilizador.contains("onze menos um quarto")) || (falaDoUtilizador.contains("11 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "10:45";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "dez e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("dez e trinta")) || (falaDoUtilizador.contains("10:30")) ||
                (falaDoUtilizador.contains("dez e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "10:30";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "dez e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("dez e quinze")) || (falaDoUtilizador.contains("10:15")) ||
                (falaDoUtilizador.contains("dez e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "10:15";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "dez e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("dez")) || (falaDoUtilizador.contains("10:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "10:00";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "dez";
            return true;
        } else if ((falaDoUtilizador.contains("nove e quarenta e cinco")) || (falaDoUtilizador.contains("9:45")) ||
                (falaDoUtilizador.contains("dez menos um quarto")) || (falaDoUtilizador.contains("10 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "9:45";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "nove e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("nove e trinta")) || (falaDoUtilizador.contains("9:30")) ||
                (falaDoUtilizador.contains("nove e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "9:30";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "nove e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("nove e quinze")) || (falaDoUtilizador.contains("9:15")) ||
                (falaDoUtilizador.contains("nove e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "9:15";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "nove e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("nove")) || (falaDoUtilizador.contains("9:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "9:00";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "nove";
            return true;
        } else if ((falaDoUtilizador.contains("oito e quarenta e cinco")) || (falaDoUtilizador.contains("8:45")) ||
                (falaDoUtilizador.contains("nove menos um quarto")) || (falaDoUtilizador.contains("9 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "8:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "oito e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("oito e trinta")) || (falaDoUtilizador.contains("8:30")) ||
                (falaDoUtilizador.contains("oito e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "8:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "oito e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("oito e quinze")) || (falaDoUtilizador.contains("8:15")) ||
                (falaDoUtilizador.contains("oito e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "8:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "oito e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("oito")) || (falaDoUtilizador.contains("8:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "8:00";
            alturaDoDia = "manhã";
            alturaDoDiaBD = "AM";
            horas = "oito";
            return true;
        } else if ((falaDoUtilizador.contains("sete e quarenta e cinco")) || (falaDoUtilizador.contains("7:45")) ||
                (falaDoUtilizador.contains("oito menos um quarto")) || (falaDoUtilizador.contains("8 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "7:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "sete e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("sete e trinta")) || (falaDoUtilizador.contains("7:30")) ||
                (falaDoUtilizador.contains("sete e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "7:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "sete e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("sete e quinze")) || (falaDoUtilizador.contains("7:15")) ||
                (falaDoUtilizador.contains("sete e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "7:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "sete e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("sete")) || (falaDoUtilizador.contains("7:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "7:00";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "sete";
            return true;
        } else if ((falaDoUtilizador.contains("seis e quarenta e cinco")) || (falaDoUtilizador.contains("6:45")) ||
                (falaDoUtilizador.contains("sete menos um quarto")) || (falaDoUtilizador.contains("7 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "6:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "seis e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("seis e trinta")) || (falaDoUtilizador.contains("6:30")) ||
                (falaDoUtilizador.contains("seis e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "6:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "seis e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("seis e quinze")) || (falaDoUtilizador.contains("6:15")) ||
                (falaDoUtilizador.contains("seis e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "6:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "seis e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("seis")) || (falaDoUtilizador.contains("6:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "6:00";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "seis";
            return true;
        } else if ((falaDoUtilizador.contains("cinco e quarenta e cinco")) || (falaDoUtilizador.contains("5:45")) ||
                (falaDoUtilizador.contains("seis menos um quarto")) || (falaDoUtilizador.contains("6 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "5:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "cinco e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("cinco e trinta")) || (falaDoUtilizador.contains("5:30")) ||
                (falaDoUtilizador.contains("cinco e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "5:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "cinco e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("cinco e quinze")) || (falaDoUtilizador.contains("5:15")) ||
                (falaDoUtilizador.contains("cinco e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "5:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "cinco e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("cinco")) || (falaDoUtilizador.contains("5:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "5:00";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "cinco";
            return true;
        } else if ((falaDoUtilizador.contains("quatro e quarenta e cinco")) || (falaDoUtilizador.contains("4:45")) ||
                (falaDoUtilizador.contains("cinco menos um quarto")) || (falaDoUtilizador.contains("5 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "4:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "quatro e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("quatro e trinta")) || (falaDoUtilizador.contains("4:30")) ||
                (falaDoUtilizador.contains("quatro e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "4:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "quatro e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("quatro e quinze")) || (falaDoUtilizador.contains("4:15")) ||
                (falaDoUtilizador.contains("quatro e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "4:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "quatro e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("quatro")) || (falaDoUtilizador.contains("4:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "4:00";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "quatro";
            return true;
        } else if ((falaDoUtilizador.contains("três e quarenta e cinco")) || (falaDoUtilizador.contains("3:45")) ||
                (falaDoUtilizador.contains("quatro menos um quarto")) || (falaDoUtilizador.contains("4 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "3:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "três e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("três e trinta")) || (falaDoUtilizador.contains("3:30")) ||
                (falaDoUtilizador.contains("três e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "3:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "três e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("trêS e quinze")) || (falaDoUtilizador.contains("3:15")) ||
                (falaDoUtilizador.contains("três e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "3:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "três e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("três")) || (falaDoUtilizador.contains("3:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "3:00";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "três";
            return true;
        } else if ((falaDoUtilizador.contains("duas e quarenta e cinco")) || (falaDoUtilizador.contains("2:45")) ||
                (falaDoUtilizador.contains("três menos um quarto")) || (falaDoUtilizador.contains("3 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "2:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "duas e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("duas e trinta")) || (falaDoUtilizador.contains("2:30")) ||
                (falaDoUtilizador.contains("duas e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "2:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "duas e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("duas e quinze")) || (falaDoUtilizador.contains("2:15")) ||
                (falaDoUtilizador.contains("duas e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "2:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "duas e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("duas")) || (falaDoUtilizador.contains("2:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "2:00";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "duas";
            return true;

        } else if ((falaDoUtilizador.contains("uma e quarenta e cinco")) || (falaDoUtilizador.contains("1:45")) ||
                (falaDoUtilizador.contains("duas menos um quarto")) || (falaDoUtilizador.contains("2 menos um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "1:45";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "uma e quarenta e cinco";
            return true;
        } else if ((falaDoUtilizador.contains("uma e trinta")) || (falaDoUtilizador.contains("1:30")) ||
                (falaDoUtilizador.contains("uma e meia"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "1:30";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "uma e trinta";
            return true;
        } else if ((falaDoUtilizador.contains("uma e quinze")) || (falaDoUtilizador.contains("1:15")) ||
                (falaDoUtilizador.contains("uma e um quarto"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "1:15";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "uma e quinze";
            return true;
        } else if ((falaDoUtilizador.contains("uma")) || (falaDoUtilizador.contains("1:00"))) {
            Log.d("######################", "Disse uma das palavras!");
            horasBD = "1:00";
            alturaDoDia = "tarde";
            alturaDoDiaBD = "PM";
            horas = "uma";
            return true;
        }
        //endregion
        else {
            String pergunta = "Desculpe, Não entendemos qual a hora que pretende!";
            tts.speak(pergunta, TextToSpeech.QUEUE_FLUSH, null);
            while (tts.isSpeaking()) {
                Log.d("##", "Não faças nada enquanto falas!");
            }
            return false;
        }
    }

    public void Participante() {
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/");
        myRef.addChildEventListener(new ChildEventListener() {
            boolean aux = false;
            String numeroVerifica;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot participantesSnapshot = dataSnapshot.child("Participantes");
                Iterable<DataSnapshot> partiChildreen = participantesSnapshot.getChildren();
                Participante participante;
                for (DataSnapshot ds : partiChildreen) {
                    participante = new Participante();
                    if (ds.getValue().toString().contains(nrCompleto)) {
                        participante.setNome(ds.child("nome").getValue().toString());
                        participante.setNif(ds.child("nif").getValue().toString());
                        participante.setContacto(ds.child("contacto").getValue().toString());
                        participante.setDataNascimentos(ds.child("dataDeNascimento").getValue().toString());
                        participante.setidInstituicao(ds.child("idInstituicao").getValue().toString());
                        participante.setMorada(ds.child("morada").getValue().toString());
                        Log.d("##############45", participante.getNif());
                        NIFSenior = participante.getNif();
                        ListarUltimaMissao(IDFINAL, NIFSenior);//testar
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void ListarUltimaMissao(String IDinstituicao, final String NIFSenior) {
        myRef = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDinstituicao + "/" + NIFSenior);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ObjetosListView obj = new ObjetosListView();

                obj.setData(dataSnapshot.child("data").getValue().toString());
                obj.setEstado(dataSnapshot.child("estado").getValue().toString());
                obj.setHoras(dataSnapshot.child("horas").getValue().toString());
                obj.setDataQueFoiAgendadaMissao(dataSnapshot.getKey().toString());

                //Carregar Foto
                if(dataSnapshot.child("nifVoluntario").getValue() !=null) {
                    String nifVolunt=dataSnapshot.child("nifVoluntario").getValue().toString();
                    storage = FirebaseStorage.getInstance().getReference().child(nifVolunt);
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("DOWNLOAD", uri.toString());
                            Picasso.with(getApplicationContext()).load(uri.toString()).into(ivFotoAnimador);
                        }
                    });
                }else{
                    ivFotoAnimador.setImageResource(R.drawable.ic_account_circle_black_24dp);
                }

                if (dataSnapshot.child("voluntario").getValue() !=null){
                    tvNomeAnimador.setText(dataSnapshot.child("voluntario").getValue().toString());
                }else{
                    tvNomeAnimador.setText("");
                }
                if (dataSnapshot.child("data").getValue() !=null){
                    tvDataMissao.setText(dataSnapshot.child("data").getValue().toString());
                }else{
                    tvDataMissao.setText("Agende uma nova missão!");
                }
                if (dataSnapshot.child("estado").getValue() !=null){
                    tvEstadoMissao.setText(dataSnapshot.child("estado").getValue().toString());
                }else{
                    tvEstadoMissao.setText("");
                }
                if (dataSnapshot.child("horas").getValue() !=null){
                    tvHoraMissao.setText(dataSnapshot.child("horas").getValue().toString());
                }else{
                    tvHoraMissao.setText("");
                }
                final String dataAgendadaMissao=obj.getDataQueFoiAgendadaMissao();
                //Teste
                if (dataSnapshot.child("feedbackAnimador").getValue()!=null ) {
                    if (dataSnapshot.child("feedbackSenior").getValue() != null) {
                        tvQuestionario.setText("");//agendar nova missao
                    } else {
                        tvQuestionario.setText("Questionário disponível");
                        tvQuestionario.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent listar_itens_encontro = new Intent(Menu_Paciente.this, Questionario.class);
                                listar_itens_encontro.putExtra("idDaInstituicao", IDFINAL);
                                listar_itens_encontro.putExtra("nif", NIFSenior);
                                listar_itens_encontro.putExtra("dataQueFoiAgendadaMissao", dataAgendadaMissao);
                                startActivity(listar_itens_encontro);
                            }
                        });
                    }
                }else{
                    tvQuestionario.setText("");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
