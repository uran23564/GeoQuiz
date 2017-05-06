package com.example.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG="QuizActivity"; // zum ausmachen der quelle einer debug-nachricht (hier der quizactivity)
    private static final String KEY_INDEX="index";  // zum speichern von mCurrentIndex
    private static final String KEY_CORRECTANSWERS="correctanswers"; // zum speichern von mCorrectAnswers
    private static final String KEY_WASANSWERED="wasanswered"; // zum speichern des bool-arrays der beantworteten fragen
    private static final String KEY_WAS_CHEATED="was_cheated"; // zum speichern, ob gecheated wurde
    private static final String KEY_CHEATS_LEFT="cheats_left"; // zum speichern, wie oft man noch cheaten darf
    private static final int REQUEST_CODE_CHEAT=0; // identifiziert child-activity CheatActivity beim aufruf von startActivityForResult
    private static final int ALLOWED_CHEATS=3;

    // knoepfe definieren
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPreviousButton;
    private Button mCheatButton;

    // textviews definieren
    private TextView mQuestionTextView;
    private TextView mNumberCorrectTextView;
    private TextView mCheatsLeft;

    private TrueFalse[] mQuestionBank=new TrueFalse[]{ // array mit fragen
            new TrueFalse(R.string.question_africa,false),
            new TrueFalse(R.string.question_americas,true),
            new TrueFalse(R.string.question_asia,true),
            new TrueFalse(R.string.question_mideast,false),
            new TrueFalse(R.string.question_oceans,true),
            new TrueFalse(R.string.question_turkey,false),
    };

    private int mCurrentIndex=0; // fragenzaehler initialisieren
    private int mCorrectAnswers=0; // zahl der richtig beantworteten fragen
    private int mCheats=ALLOWED_CHEATS; // man darf in einer runde maximal 3 mal cheaten
    // private boolean mIsCheater=false; // wurde gecheated?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState!=null){
            mCurrentIndex=savedInstanceState.getInt(KEY_INDEX,0);
            mCorrectAnswers=savedInstanceState.getInt(KEY_CORRECTANSWERS,0);
            mCheats=savedInstanceState.getInt(KEY_CHEATS_LEFT,0);
            for(int i=0;i<=mQuestionBank.length-1;i++){
                boolean tmpWasAnswered[]=savedInstanceState.getBooleanArray(KEY_WASANSWERED);
                boolean tmpWasCheated[]=savedInstanceState.getBooleanArray(KEY_WAS_CHEATED);
                for(int j=0;j<=mQuestionBank.length-1;j++){
                    mQuestionBank[j].setWasAnswered(tmpWasAnswered[j]);
                    mQuestionBank[j].setWasCheated(tmpWasCheated[j]);
                }
                //mQuestionBank[i].setWasAnswered(savedInstanceState.getBoolean(KEY_WASANSWERED));
            }
            // mIsCheater=savedInstanceState.getBoolean(KEY_WAS_CHEATED);
        }
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called"); // zum debuggen
        setContentView(R.layout.activity_quiz);

        mTrueButton=(Button)findViewById(R.id.true_button);
        mFalseButton=(Button)findViewById(R.id.false_button);
        mNextButton=(Button)findViewById(R.id.next_button);
        mPreviousButton=(Button)findViewById(R.id.previous_button);
        mCheatButton=(Button)findViewById(R.id.cheat_button);

        mQuestionTextView=(TextView)findViewById(R.id.question_text_view);
        mNumberCorrectTextView=(TextView)findViewById(R.id.number_correct);
        mCheatsLeft=(TextView)findViewById(R.id.cheats_left_text_view);

        updateQuestion();
        // mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getQuestion()); // initialisieren


        // setOnClickListener ist als anonyme klasse implementiert
        // listener interface verlangt, dass onClick(View) implementiert wird.. aber nicht, wie
        mTrueButton.setOnClickListener(new View.OnClickListener(){ // wahr-knopf
            @Override
            public void onClick(View v){
                // Toast.makeText(QuizActivity.this,R.string.incorrect_toast,Toast.LENGTH_SHORT).show();
                checkAnswer(true);
                mQuestionBank[mCurrentIndex].setWasAnswered((true));
                enableButtons(false);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener(){ // falsch-knopf
            @Override
            public void onClick(View v){
                // Toast.makeText(QuizActivity.this,R.string.correct_toast,Toast.LENGTH_SHORT).show();
                checkAnswer(false);
                mQuestionBank[mCurrentIndex].setWasAnswered((true));
                enableButtons(false);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener(){ // naechste frage
            @Override
            public void onClick(View v){
                // mCurrentIndex=mCurrentIndex+1 % mQuestionBank.length; // damit der counter nicht groesser als die zahl der fragen wird
                // wir fangen also wieder mit der ersten frage an.
                // mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getQuestion()); // neue frage anzeigen
                if(mCurrentIndex==mQuestionBank.length-1){
                    mNumberCorrectTextView.setText("Number of correct answers: "+String.valueOf(mCorrectAnswers));
                    mCorrectAnswers=0;
                    mCurrentIndex=0;
                    for(int i=0;i<=mQuestionBank.length-1;i++){ // neue runde: loesche alle daten
                        mQuestionBank[i].setWasAnswered(false);
                        mQuestionBank[i].setCorrectlyAnswered(false);
                        mQuestionBank[i].setWasCheated(false);
                        mCheats=ALLOWED_CHEATS;
                        mCheatButton.setEnabled(true);
                    }
                }
                else{
                    mCurrentIndex++;
                }
                // mIsCheater=false;
                updateQuestion();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener(){ // vorige frage
            @Override
            public void onClick(View v){
                if(mCurrentIndex==0){
                    mCurrentIndex=mQuestionBank.length-1;
                    mCorrectAnswers=0;
                }
                else{
                    mCurrentIndex--;
                }
                // mIsCheater=false;
                updateQuestion();
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener(){ // cheat-knopf -- startet neue activity
            @Override
            public void onClick(View v){
                // Intent intent=new Intent(QuizActivity.this,CheatActivity.class);
                // wir verwenden unsere newIntent-Methode, die uns sofort erlaubt extras reinzuschreiben
                Intent intent=CheatActivity.newIntent(QuizActivity.this,mQuestionBank[mCurrentIndex].isTrueQuestion());
                // startActivity(intent);
                startActivityForResult(intent,REQUEST_CODE_CHEAT); // kind-activity kann daten zuruecksenden. activities werden durch zweites argument voneinander unterschieden
            }
        });
    }


    // wird cheatactivity zerstoert, bekommt quizactivity eine letzte antwort von cheatactivity. diese besteht aus resultCode und dem Intent
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode!= Activity.RESULT_OK){ // wurde nicht gecheated, gehe raus
            return;
        }
        if(requestCode==REQUEST_CODE_CHEAT){
            if(data==null){ // wenn nix im intent steht, gehe raus
                return;
            }
            // mIsCheater=CheatActivity.wasAnswerShown(data);
            mQuestionBank[mCurrentIndex].setWasCheated(CheatActivity.wasAnswerShown(data));
            mCheats--;
        }
    }


    private void updateQuestion(){ // aktuelle frage anzeigen und eventuell knoepfe sperren
        if (mQuestionBank[mCurrentIndex].getWasAnswered()) {
            enableButtons(false);
        }
        else{
            enableButtons(true);
        }
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getQuestion());
        mCheatsLeft.setText("Left cheats: "+mCheats);
        if(mCheats==0){
            mCheatButton.setEnabled(false);
        }
    }

    private void checkAnswer(boolean userPressedTrue){
        int messageResId;
        //if(mIsCheater){
        if(mQuestionBank[mCurrentIndex].getWasCheated()){
            messageResId=R.string.judgment_toast; // frage wird nicht gewertet
        }
        else {
            if (userPressedTrue == mQuestionBank[mCurrentIndex].isTrueQuestion()) {
                messageResId = R.string.correct_toast;
                mCorrectAnswers++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    public void enableButtons(boolean state){ // sperrt oder entsperrt wahr/falsch-knoepfe und speichert den zustand
        if(state){
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
        else{
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG,"onSaveInstanceState() called");
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        savedInstanceState.putInt(KEY_CORRECTANSWERS,mCorrectAnswers);
        savedInstanceState.putInt(KEY_CHEATS_LEFT,mCheats);
        for(int i=0;i<=mQuestionBank.length;i++){
            boolean tmpWasAnswered[]=new boolean[mQuestionBank.length];
            boolean tmpWasCheated[]=new boolean[mQuestionBank.length];
            for(int j=0;j<=mQuestionBank.length-1;j++){
                tmpWasAnswered[j]=mQuestionBank[j].getWasAnswered();
                tmpWasCheated[j]=mQuestionBank[j].getWasCheated();
            }
            savedInstanceState.putBooleanArray(KEY_WASANSWERED,tmpWasAnswered);
            savedInstanceState.putBooleanArray(KEY_WAS_CHEATED,tmpWasCheated);
        }
        // savedInstanceState.putBoolean(KEY_WASANSWERED,mQuestionBank[mCurrentIndex].getWasAnswered());
        // savedInstanceState.putBoolean(KEY_WAS_CHEATED,mIsCheater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_quiz, menu);
        return true;
    }

    // zum debuggen - wir wollen erfahren, wann onStart, on Pause, onResume, onStop und unDestroy aufgerufen worden sind
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart() called");
    }
    public void onPause(){
        super.onPause();
        Log.d(TAG,"onStart() called");
    }
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onStart() called");
    }
    public void onStop(){
        super.onStop();
        Log.d(TAG,"onStart() called");
    }
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onStart() called");
    }

}
