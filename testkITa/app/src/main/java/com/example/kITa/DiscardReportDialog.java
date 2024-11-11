package com.example.kITa;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class DiscardReportDialog extends Dialog {

    public interface OnDiscardListener {
        void onDiscard();
    }

    public DiscardReportDialog(Context context, OnDiscardListener discardListener) {
        super(context);
        setContentView(R.layout.dialogbox_discardreport);
        setCancelable(true);

        Button discardButton = findViewById(R.id.discardReport);
        Button cancelButton = findViewById(R.id.cancelDiscart);

        discardButton.setOnClickListener(v -> {
            discardListener.onDiscard();
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }
}
