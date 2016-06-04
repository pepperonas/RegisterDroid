package com.pepperonas.registerdroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.registerdroid.model.BalanceResult;
import com.pepperonas.registerdroid.model.DatabaseHelper;
import com.pepperonas.registerdroid.model.Tag;
import com.pepperonas.registerdroid.dialogs.DialogEditEntry;
import com.pepperonas.registerdroid.various.Config;
import com.pepperonas.registerdroid.various.Utils;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    private LinearLayout mLinearLayout;
    private DatabaseHelper mDb;
    private BalanceResult mBr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.report_in_register) + (AesPrefs.getInt(Config.ACTIVE_REGISTER, 0) + 1));

        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "onCreate " + e.toString());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLinearLayout = (LinearLayout) findViewById(R.id.ll_history_container);

        mDb = new DatabaseHelper(this);
        if (getIntent() != null) {
            loadHistory(getIntent());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mDb = new DatabaseHelper(this);
    }


    @Override
    protected void onPause() {
        mDb.close();

        super.onPause();
    }


    private void loadHistory(Intent intent) {
        String link = intent.getStringExtra(Config.BUNDLE_KEY_LINK);
        if (TextUtils.isEmpty(link)) return;

        TextView tvDate = (TextView) findViewById(R.id.tv_history_date);
        tvDate.setText(String.format("%s%s", getString(R.string.report_from_date), Utils.makePrintableTimeStamp(link)));

        initBalanceResultAndShowTotal(link);

        for (Tag t : mBr.getMoneyMap().keySet()) {
            inflateLayouts(mBr, t.name(), t.toString(), mBr.getMoneyMap().get(t), t);
        }
    }


    public void initBalanceResultAndShowTotal(String link) {
        mBr = mDb.getBalanceResultByLink(link);
        TextView tvTotal = (TextView) findViewById(R.id.tv_history_total);
        tvTotal.setText(Utils.formatTotal(this, mBr.getTotal()));
    }


    private void inflateLayouts(final BalanceResult br, final String key, final String name, int value, Tag t) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View v = inflater.inflate(R.layout.row_blance_history, null);

        TextView tvPos = (TextView) v.findViewById(R.id.frag_balance_tv_pos);
        final TextView tvAmount = (TextView) v.findViewById(R.id.frag_balance_tv_show_amount);
        final TextView tvSum = (TextView) v.findViewById(R.id.frag_balance_tv_show_sum);

        tvPos.setText(name);
        tvAmount.setText(String.format("%s%s", String.valueOf(value), getString(R.string.x)));

        tvSum.setText(Utils.formatSingleValue(this, br.getMoneyMap().get(t) * t.getValue()));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogEditEntry(HistoryActivity.this, br, key, tvAmount, tvSum);
            }
        });

        mLinearLayout.addView(v);
    }


    public DatabaseHelper getDatabase() {
        return mDb;
    }
}
