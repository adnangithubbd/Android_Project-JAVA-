package com.example.languagetranslator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.languagetranslator.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.languagetranslator.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

        ActivityMainBinding binding;
        private TranslatorOptions translatorOptions;
       private Translator translator;
       ArrayList<ModelLanguage> languageArrayList;
       private static final String TAG="MainTag";
       private String fromLanguageCode="";
       private String fromLanguageTitle="";
       private String toLanguageCode="";
       private String toLanguageTitle="";
       ProgressDialog pd;
    TextToSpeech tts;
    boolean isPlaying = false;
    private static final int REQUEST_CODE_SPEECH_INPUT=1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        loadAvaibleLanguage();
        binding.fromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceLanguageChosen();
            }
        });

        binding.toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationLanguageChosen();
            }
        });

        binding.translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTxt();
            }
        });

        binding.paused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            tts.setLanguage(Locale.US);
                            binding.paused.setVisibility(View.GONE);
                            binding.playing.setVisibility(View.VISIBLE);


                            if(binding.translatedText.getText().toString().trim().isEmpty()){
                               binding.sourceText.setError("Write your text before play..");
                            }else{
                                playTTS(binding.translatedText.getText().toString());
                            }
                        }
                    }
                });

            }
        });

        binding.playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTTS();
            }
        });


binding.voiceToText.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startVoiceInput();
        Toast.makeText(MainActivity.this, "voice to text clicked", Toast.LENGTH_SHORT).show();
    }
});

    binding.clearText.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.sourceText.setText("");
        }
    });


    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Log.d(TAG,a.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                   // binding.translatedText.setText(result.get(0));
                }
                break;
            }

        }
    }




    private void playTTS(String string) {
        tts.speak(string, TextToSpeech.QUEUE_ADD, null);
        isPlaying = true;
        updateButtonVisibility();
    }

    private void stopTTS() {
        tts.stop();
        isPlaying = false;
        updateButtonVisibility();
    }
    private void updateButtonVisibility() {
        if (isPlaying) {
            binding.playing.setVisibility(View.VISIBLE);
            binding.paused.setVisibility(View.GONE);
        } else if(tts!=null){
            binding.playing.setVisibility(View.GONE);
            binding.paused.setVisibility(View.VISIBLE);
        }

    }
    private void validateTxt() {
        if(binding.sourceText.getText().toString().trim().isEmpty()){
            binding.sourceText.setError("Please Write Something to translate...!!");
        }else if(binding.fromBtn.getText().toString().equals("FROM")||binding.toBtn.getText().toString().equals("TO")){
            Toast.makeText(this, "Please select the language to translate", Toast.LENGTH_SHORT).show();
        }
        else{
            binding.progressBar.setVisibility(View.VISIBLE);
            startTranslate();
        }
    }

    private void startTranslate() {

        translatorOptions = new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(toLanguageCode)
                        .build();
       translator = Translation.getClient(translatorOptions);


        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                translator.translate(binding.sourceText.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String translatedText) {
                                                binding.translatedText.setText("");
                                                binding.progressBar.setVisibility(View.GONE);
                                                binding.translatedText.setText(translatedText);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                            }

                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

    }


    private void destinationLanguageChosen(){
        PopupMenu popupMenu=new PopupMenu(this,binding.translateButton);

        for(int i=0;i<languageArrayList.size();i++){
            popupMenu.getMenu().add(Menu.NONE,i,i,languageArrayList.get(i).getLanguageTitle());
        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int position=menuItem.getItemId();
                toLanguageTitle=languageArrayList.get(position).languageTitle;
                toLanguageCode=languageArrayList.get(position).languageCode;
                binding.toBtn.setText(toLanguageTitle);
                return false;
            }
        });
    }


    private void sourceLanguageChosen(){
        PopupMenu popupMenu=new PopupMenu(this,binding.translateButton);

        for(int i=0;i<languageArrayList.size();i++){
            popupMenu.getMenu().add(Menu.NONE,i,i,languageArrayList.get(i).getLanguageTitle());
        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int position=menuItem.getItemId();
                fromLanguageTitle=languageArrayList.get(position).languageTitle;
                fromLanguageCode=languageArrayList.get(position).languageCode;
                binding.fromBtn.setText(fromLanguageTitle);
                return false;
            }
        });
    }

    private void loadAvaibleLanguage() {

        languageArrayList=new ArrayList<>();
        List<String> languageCodeList= TranslateLanguage.getAllLanguages();
        for(String languageCode:languageCodeList){
            String languageTitle=new Locale(languageCode).getDisplayLanguage();
            Log.d(TAG,"language Code "+languageCode+" language title "+languageTitle);

            ModelLanguage language=new ModelLanguage(languageCode,languageTitle);
            languageArrayList.add(language);

        }
    }



    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    }
