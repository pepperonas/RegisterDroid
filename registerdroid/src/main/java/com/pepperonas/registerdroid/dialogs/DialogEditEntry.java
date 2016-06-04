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

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.registerdroid.HistoryActivity;
import com.pepperonas.registerdroid.R;
import com.pepperonas.registerdroid.model.BalanceResult;
import com.pepperonas.registerdroid.model.Tag;
import com.pepperonas.registerdroid.various.Utils;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p>
 *         Edit how often a specific value in a balance exsits.
 *         <p>
 *         Dialog gets called from {@link HistoryActivity} when a item is clicked.
 */
public class DialogEditEntry {

    private static final String TAG = "DlgEditEntry";


    public DialogEditEntry(final HistoryActivity act, final BalanceResult cr, String tagName, final TextView tvAmount, final TextView tvSum) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(act);

        LayoutInflater inflater = act.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_entry, null);
        dialogBuilder.setView(dialogView);

        final Tag t = Tag.valueOf(tagName);
        int oldAmount = cr.getMoneyMap().get(t);

        TextView tvHeader = (TextView) dialogView.findViewById(R.id.dialog_edit_entry_tv_header);
        tvHeader.setText(String.format(act.getString(R.string.edit_field), t.toString()));

        String oldValueInfo = act.getString(R.string.old_value) + oldAmount + act.getString(R.string.x);

        TextView tvOldValue = (TextView) dialogView.findViewById(R.id.tvOldValue);
        final EditText etNewValue = (EditText) dialogView.findViewById(R.id.etNewValue);

        tvOldValue.setText(oldValueInfo);

        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etNewValue.getText() != null && !etNewValue.getText().toString().isEmpty()) {
                    int newValue = Integer.parseInt(etNewValue.getText().toString());

                    cr.getMoneyMap().put(t, newValue);
                    act.getDatabase().updateBalance(cr.getLinker(), t, newValue);
                    tvAmount.setText(String.format("%d%s", newValue, act.getString(R.string.x)));
                    tvSum.setText(Utils.formatSingleValue(act, newValue * t.getValue()));
                    act.initBalanceResultAndShowTotal(cr.getLinker());

                    dialog.dismiss();
                } else ToastUtils.toastShort(act.getString(R.string.to_enter_valid_data));

            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

}
