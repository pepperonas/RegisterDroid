package com.pepperonas.registerdroid.dialogs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.pepperonas.registerdroid.MainActivity;
import com.pepperonas.registerdroid.R;
import com.pepperonas.registerdroid.various.Utils;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p/>
 *         Delete a balance from the database.
 */
public class DialogDeleteBalance {

    public DialogDeleteBalance(final MainActivity main, final String link) {
        new AlertDialog.Builder(main)
                .setTitle(main.getString(R.string.are_you_sure))
                .setMessage(main.getString(R.string.dlg_delete_msg_1) +
                            Utils.makePrintableTimeStamp(link) +
                            main.getString(R.string.dlg_delete_msg_2))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        main.getDatabase().deleteBalance(link);
                        main.makeFragmentTransaction(1);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
