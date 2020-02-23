package com.interview.memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

class Create_Dialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle saveInstance)
  {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
      }
      ).setNegativeButton("취소", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
      });

        return  builder.create();
  }
}
