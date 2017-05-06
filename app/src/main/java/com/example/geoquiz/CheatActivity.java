package com.example.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE="com.example.geoquiz.answer_is_true"; // extra im intent der eltern, was die antwort auf die frage beinhaltet
    private static final String EXTRA_ANSWER_SHOWN="com.example.geoquiz.answer_shown"; // teilt den eltern mit, ob gespickt wurde
    private static final String KEY_WAS_CHEATED="com.example.geoquiz.was_cheated";

    private boolean mAnswerIsTrue;
    private boolean mWasCheated=false;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    // erzeugt neuen intent mit der zusatzinformation (extra) answerIsTrue. genau das braucht die cheat-klasse
    // hier koennen noch weitere extras reingepackt werden...
    public static Intent newIntent(Context packageContext, boolean answerIsTrue){
        Intent intent=new Intent(packageContext,CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }

    // QuizActivity ruft diese Methode auf, um zu erfahren, ob gecheated wurde
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState!=null){
            mWasCheated=savedInstanceState.getBoolean(KEY_WAS_CHEATED);
            setAnswerShownResult(mWasCheated);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerTextView=(TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton=(Button) findViewById(R.id.show_answer_button);

        mAnswerIsTrue=getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false); // zweites argument ist ein default-wert, falls der schluessel nicht ausgelesen werden konnte

        mShowAnswerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mAnswerIsTrue){
                    mAnswerTextView.setText(R.string.true_button);
                }
                else{mAnswerTextView.setText(R.string.false_button);}
                setAnswerShownResult(true);

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) { // checke, ob diese Animation auf dem geraet laufen kann
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0); // dafuer brauchen wir mindestens API21 (Lollipop)
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else{mShowAnswerButton.setVisibility(View.INVISIBLE);}
            }
        });
    }

    // zeug, was zu quizactivity geschickt wird, wenn cheatactivity zerstoert wird
    private void setAnswerShownResult(boolean isAnswerShown){
        Intent data=new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown);
        setResult(RESULT_OK,data); // gibt den eltern das data-intent zurueck, wenn back-button gedrueckt wird
        mWasCheated=isAnswerShown;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_WAS_CHEATED,mWasCheated);
    }
}
