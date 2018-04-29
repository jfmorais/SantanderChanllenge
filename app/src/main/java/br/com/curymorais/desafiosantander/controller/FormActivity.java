package br.com.curymorais.desafiosantander.controller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import br.com.curymorais.desafiosantander.R;
import br.com.curymorais.desafiosantander.Service.FormReadService;
import br.com.curymorais.desafiosantander.domain.dto.FieldDTO;
import br.com.curymorais.desafiosantander.domain.model.EditTextSantander;
import br.com.curymorais.desafiosantander.util.ViewBuilder;

public class FormActivity extends RootActivity{
    private static final String TAG = "FORM_ACTIVITY";
    private List<FieldDTO> listaFields = new ArrayList<>();
    Typeface typeFont ;
    List<View> listaObjetos;

    private View.OnClickListener startFormResponse = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(getApplicationContext(), FormResponseActivity.class);
            boolean verified = true;
            Log.i(TAG,"Verificando conteudo dos campos...");
            for (View x: listaObjetos){
                if (x instanceof EditTextSantander){
                    if(((EditTextSantander) x).isRequired() && ((EditTextSantander) x).getText().toString().equalsIgnoreCase("")){
                        verified = false;
                    }
                    myIntent.putExtra("field"+((EditTextSantander) x).getHint(), ((EditTextSantander) x).getText().toString());
                }
            }
            Log.i(TAG,"iniciar nova atividade? "+verified);
            if (verified) {
                startActivity(myIntent);
            }else{
                Toast.makeText(getApplicationContext(),"Necessario preencher as informacoes!",Toast.LENGTH_LONG).show();
            }
        }
    };

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean encontrado;
            Log.i(TAG, "Receiver recebeu!");
            listaFields = (List<FieldDTO>) intent.getSerializableExtra("fields");
            encontrado = (Boolean) intent.getSerializableExtra("encontrou");

            if (encontrado){
                Log.i(TAG, "Campos encontrados! ");
            }else{
                Log.i(TAG, "Campos NAO encontrados!");
            }
            buildView();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeFont = ResourcesCompat.getFont(getApplicationContext(), R.font.dinpromedium);
        setContentView(R.layout.form_view);
        startService(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("cells"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void startService( Context c) {
        Intent i = new Intent(c, FormReadService.class);
        c.startService(i);
    }

    @SuppressLint("ResourceType")
    public void buildView() {
        Log.i(TAG,"Construindo a view");
        ConstraintLayout layout = findViewById(R.id.layout_base);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        listaObjetos = new ArrayList<>();

        for (FieldDTO f : listaFields){
            switch (f.getType()) {
                case 1:
//                    listaObjetos.add(ViewBuilder.getEditTextFromField(f, this));
                    listaObjetos.add(ViewBuilder.getEditTextSantanderFromField(f,this));
                    break;
                case 2:
                    listaObjetos.add(ViewBuilder.getTextViewFromField(f,this));
                    break;
                case 4:
                    listaObjetos.add(ViewBuilder.getCheckBoxFromField(f,this));
                    break;
                case 5:
                    Button b = ViewBuilder.getButtonViewFromField(f,this);
                    b.setOnClickListener(startFormResponse);
                    listaObjetos.add(b);
                    break;
            }
        }

        for (int i = 0; i < listaObjetos.size();i++){
            if(i==0){
                layout.addView(listaObjetos.get(i));
                set.connect(listaObjetos.get(i).getId(), ConstraintSet.LEFT,ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(listaObjetos.get(i).getId(), ConstraintSet.RIGHT,ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.connect(listaObjetos.get(i).getId(), ConstraintSet.TOP,ConstraintSet.PARENT_ID, ConstraintSet.TOP, 600);
                set.constrainHeight(listaObjetos.get(i).getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(listaObjetos.get(i).getId(), ConstraintSet.WRAP_CONTENT);
                set.applyTo(layout);
            }
            else {
                layout.addView(listaObjetos.get(i));
                set.connect(listaObjetos.get(i).getId(), ConstraintSet.LEFT,ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(listaObjetos.get(i).getId(), ConstraintSet.RIGHT,ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.connect(listaObjetos.get(i).getId(), ConstraintSet.TOP,listaObjetos.get(i-1).getId(), ConstraintSet.BOTTOM, 0);
                set.constrainHeight(listaObjetos.get(i).getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(listaObjetos.get(i).getId(), ConstraintSet.WRAP_CONTENT);
                set.applyTo(layout);
            }
        }
    }

}
