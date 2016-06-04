package com.pepperonas.registerdroid.dialogs;

import android.app.Dialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.registerdroid.various.Config;
import com.pepperonas.registerdroid.MainActivity;
import com.pepperonas.registerdroid.R;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p/>
 *         Select which register is active.
 *         <p/>
 *         Dialog gets called from {@link MainActivity#onNavigationItemSelected}
 */
public class DialogSelectRegister implements View.OnClickListener {

    private final Dialog mDialog;


    public DialogSelectRegister(MainActivity main) {

        mDialog = new Dialog(main);
        mDialog.setContentView(R.layout.dialog_show_select_register);
        mDialog.setTitle(R.string.dialog_choose_register_title);
        mDialog.setCancelable(true);

        selectLastRbtn();

        setClickListener();

        mDialog.show();
    }


    private void setClickListener() {
        mDialog.findViewById(R.id.dialog_select_register_rbtn_1).setOnClickListener(this);
        mDialog.findViewById(R.id.dialog_select_register_rbtn_2).setOnClickListener(this);
        mDialog.findViewById(R.id.dialog_select_register_rbtn_3).setOnClickListener(this);
    }


    private void selectLastRbtn() {
        ((RadioButton) ((LinearLayout) mDialog
                .findViewById(R.id.dialog_select_register_ll))
                .getChildAt(AesPrefs.getInt(Config.ACTIVE_REGISTER, 0)))
                .setChecked(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_select_register_rbtn_1:
                AesPrefs.putInt(Config.ACTIVE_REGISTER, 0);
                break;
            case R.id.dialog_select_register_rbtn_2:
                AesPrefs.putInt(Config.ACTIVE_REGISTER, 1);
                break;
            case R.id.dialog_select_register_rbtn_3:
                AesPrefs.putInt(Config.ACTIVE_REGISTER, 2);
                break;
        }
        mDialog.dismiss();
    }

}
