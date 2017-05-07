package com.example.geoquiz;


public class TrueFalse extends Object {
    private int mQuestion; // referenziert auf den string der frage
    private boolean mTrueQuestion; // indiziert, ob aussage wahr oder falsch ist
    private boolean mWasAnswered; // wurde die frage bereits beantwortet?
    private boolean mCorrectlyAnswered; // wurde die frage korrekt beantwortet?
    private boolean mWasCheated; // wurde bei der frage gespickt?

    public TrueFalse(int question, boolean trueQuestion){ // konstruktor
        mQuestion=question;
        mTrueQuestion=trueQuestion;
        mWasAnswered=false;
        mCorrectlyAnswered=false;
        mWasCheated=false;
    }

    // getter und setter
    public int getQuestion(){
        return mQuestion;
    }

    public void setQuestion(int question){
        mQuestion=question;
    }

    public boolean isTrueQuestion(){
        return mTrueQuestion;
    }

    public void setTrueQuestion(boolean trueQuestion){
        mTrueQuestion=trueQuestion;
    }

    public boolean getWasAnswered() {return mWasAnswered;}

    public void setWasAnswered(boolean wasAnswered) {mWasAnswered=wasAnswered;}

    public boolean getCorrectlyAnswered() {return mCorrectlyAnswered;}

    public void setCorrectlyAnswered(boolean correctlyAnswered) {mCorrectlyAnswered=correctlyAnswered;}

    public boolean getWasCheated() {return mWasCheated;}

    public void setWasCheated(boolean wasCheated) {mWasCheated=wasCheated;}

}
