/*
 * Copyright (c) 2016 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
