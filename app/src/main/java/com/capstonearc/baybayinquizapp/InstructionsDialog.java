package com.capstonearc.baybayinquizapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

public class InstructionsDialog extends Dialog {

    private int instructionPoints = 0;
    public InstructionsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_dialog_layout);

        final AppCompatButton continueBtn = findViewById(R.id.continueBtn);
        final TextView instructionsTV = findViewById(R.id.instructionsTV);

        setInstructionPoint(instructionsTV, "1. instruction chuchuchuchuchuchuch");
        setInstructionPoint(instructionsTV, "2. instruction2 chuchuchuchuchuchuch");

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setInstructionPoint(TextView instructionTV, String instructionPoint){

        if (instructionPoints == 0){
            instructionTV.setText(instructionPoint);
    }
        else {
            instructionTV.setText(instructionTV.getText()+"\n\n"+instructionPoint);
        }
    }

}
