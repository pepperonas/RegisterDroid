package com.pepperonas.registerdroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.registerdroid.various.Config;
import com.pepperonas.registerdroid.various.Utils;
import com.pepperonas.jbasx.format.TimeFormatUtils;
import com.pepperonas.jbasx.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 */
public class DatabaseHelper
        extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static String DATABASE_NAME = "register.db";

    private static final String R_ID = "r_id";
    private static final String R_TS = "r_ts";

    private String TBL_MONEY_BALANCE = "money_col";
    private String C_ID = "c_id";
    private String C_TS = "c_ts";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // register 1
        db.execSQL("CREATE TABLE " + Config.REGISTER_NAME_R1 + " (" + R_ID + " integer primary key, " + R_TS + " text);");

        // register 2
        db.execSQL("CREATE TABLE " + Config.REGISTER_NAME_R2 + " (" + R_ID + " integer primary key, " + R_TS + " text);");

        // register 3
        db.execSQL("CREATE TABLE " + Config.REGISTER_NAME_R3 + " (" + R_ID + " integer primary key, " + R_TS + " text);");

        // money collection
        db.execSQL("CREATE TABLE " + TBL_MONEY_BALANCE + " (" + C_ID + " integer primary key, " + C_TS + " text, " +
                   Tag.CENT_01.name() + " integer, " + Tag.CENT_02.name() + " integer, " + Tag.CENT_05.name() + " integer, " +
                   Tag.CENT_10.name() + " integer, " + Tag.CENT_20.name() + " integer, " + Tag.CENT_50.name() + " integer, " +
                   Tag.EURO_001.name() + " integer, " + Tag.EURO_002.name() + " integer, " + Tag.EURO_005.name() + " integer, " +
                   Tag.EURO_010.name() + " integer, " + Tag.EURO_020.name() + " integer, " + Tag.EURO_050.name() + " integer, " +
                   Tag.EURO_100.name() + " integer, " + Tag.EURO_200.name() + " integer, " + Tag.EURO_500.name() + " integer, " +
                   "_total integer); ");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Config.REGISTER_NAME_R1);
        db.execSQL("DROP TABLE IF EXISTS " + Config.REGISTER_NAME_R2);
        db.execSQL("DROP TABLE IF EXISTS " + Config.REGISTER_NAME_R3);
        onCreate(db);
    }


    @Override
    public synchronized void close() {
        super.close();
    }


    /**
     * Retrieve a list of all balances, which are linked to a specific {@link Register}.
     */
    public List<BalanceResult> getBalancesOfCurrentRegister() {

        List<String> timestamps = new ArrayList<String>();

        String selectQuery = "SELECT " + R_TS + " FROM " + Register.getId(AesPrefs.getInt(Config.ACTIVE_REGISTER, 0));

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                timestamps.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();

        selectQuery = "SELECT * FROM " + TBL_MONEY_BALANCE;

        db = this.getReadableDatabase();
        c = db.rawQuery(selectQuery, null);

        List<BalanceResult> results = new ArrayList<BalanceResult>();

        if (c.moveToFirst()) {
            do {
                String tmp = c.getString(1);
                for (String s : timestamps) {
                    if (s.equals(tmp)) {

                        Balance cc = new Balance(s);
                        cc.makeBalance(c.getInt(2), c.getInt(3), c.getInt(4),
                                       c.getInt(5), c.getInt(6), c.getInt(7),
                                       c.getInt(8), c.getInt(9), c.getInt(10),
                                       c.getInt(11), c.getInt(12), c.getInt(13),
                                       c.getInt(14), c.getInt(15), c.getInt(16));

                        results.add(new BalanceResult(cc, c.getInt(17)));
                    }
                }

            } while (c.moveToNext());
        }

        c.close();
        return results;
    }


    /**
     * Retrieve the content of one balance (such as '0,50€ -> 1x; 20€ -> 2x; TOTAL 20,50€').
     *
     * @param link The unique identifier {@link DatabaseHelper#C_TS} to resolve the query.
     */
    public BalanceResult getBalanceResultByLink(String link) {
        String selectQuery = "SELECT * FROM " + TBL_MONEY_BALANCE + " WHERE " + C_TS + "= '" + link + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Balance cc = new Balance(link);
                cc.makeBalance(c.getInt(2), c.getInt(3), c.getInt(4),
                               c.getInt(5), c.getInt(6), c.getInt(7),
                               c.getInt(8), c.getInt(9), c.getInt(10),
                               c.getInt(11), c.getInt(12), c.getInt(13),
                               c.getInt(14), c.getInt(15), c.getInt(16));

                return new BalanceResult(cc, c.getInt(17));
            }
            while (c.moveToNext());
        }
        c.close();
        return null;
    }


    public void addRegister(Register register) {
        String row = "INSERT OR REPLACE INTO " + Register.getId(AesPrefs.getInt(Config.ACTIVE_REGISTER, 0)) + " ("
                     + R_ID + ", " +
                     R_TS +
                     ") VALUES (" +
                     "" + null + ", " +
                     "'" + register.getLinker() + "');";
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(row);
    }


    /**
     * Store a balance.
     */
    public void addMoneyCollection(Balance balance, int total) {

        TreeMap<Tag, Integer> moneyMap = balance.getMoneyMap();

        String row = "INSERT OR REPLACE INTO " + TBL_MONEY_BALANCE + " (" +
                     C_ID + ", " +
                     C_TS + ", " +
                     Tag.CENT_01.name() + ", " +
                     Tag.CENT_02.name() + ", " +
                     Tag.CENT_05.name() + ", " +
                     Tag.CENT_10.name() + ", " +
                     Tag.CENT_20.name() + ", " +
                     Tag.CENT_50.name() + ", " +
                     Tag.EURO_001.name() + ", " +
                     Tag.EURO_002.name() + ", " +
                     Tag.EURO_005.name() + ", " +
                     Tag.EURO_010.name() + ", " +
                     Tag.EURO_020.name() + ", " +
                     Tag.EURO_050.name() + ", " +
                     Tag.EURO_100.name() + ", " +
                     Tag.EURO_200.name() + ", " +
                     Tag.EURO_500.name() + ", " +
                     "_total " +
                     ") VALUES (" +
                     "" + null + ", " +
                     "'" + balance.getLinker() + "', " +
                     "" + moneyMap.get(Tag.CENT_01) + ", " +
                     "" + moneyMap.get(Tag.CENT_02) + ", " +
                     "" + moneyMap.get(Tag.CENT_05) + ", " +
                     "" + moneyMap.get(Tag.CENT_10) + ", " +
                     "" + moneyMap.get(Tag.CENT_20) + ", " +
                     "" + moneyMap.get(Tag.CENT_50) + ", " +
                     "" + moneyMap.get(Tag.EURO_001) + ", " +
                     "" + moneyMap.get(Tag.EURO_002) + ", " +
                     "" + moneyMap.get(Tag.EURO_005) + ", " +
                     "" + moneyMap.get(Tag.EURO_010) + ", " +
                     "" + moneyMap.get(Tag.EURO_020) + ", " +
                     "" + moneyMap.get(Tag.EURO_050) + ", " +
                     "" + moneyMap.get(Tag.EURO_100) + ", " +
                     "" + moneyMap.get(Tag.EURO_200) + ", " +
                     "" + moneyMap.get(Tag.EURO_500) + ", " +
                     "" + total + ");";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);
    }


    /**
     * Delete a balance by a given link.
     *
     * @param link The link which identifies the balance.
     */
    public void deleteBalance(String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TBL_MONEY_BALANCE + " WHERE " + C_TS + "='" + link + "';");
        db.execSQL("DELETE FROM " + Register.getId(AesPrefs.getInt(Config.ACTIVE_REGISTER, 0)) + " WHERE " + R_TS + "='" + link + "';");

        ToastUtils.toastShort("Eintrag vom '" + TimeFormatUtils.formatTime(Long.valueOf(link), TimeFormatUtils.DEFAULT_FORMAT).replace("-", "/") + "' gelöscht");
    }


    public String generatePrintableRegisterOutput() {
        List<BalanceResult> balanceResults = getBalancesOfCurrentRegister();
        String result = "";
        for (BalanceResult cr : balanceResults) {
            result += Utils.makePrintableTimeStamp(cr.getLinker()) + "\n";
            result += cr.getMoneyMap().toString().replace("{", "").replace("}", "").replace("=", "\t-> ").replace(", ", "x\n") +
                      "\n\nGESAMT: " + (cr.getTotal() / 100f) + "€";
            result += "\n-----------------\n\n";
        }
        Log.d(TAG, "generatePrintableRegisterOutput  " + result);
        return result;
    }


    public void updateBalance(String linker, Tag tag, int newValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(tag.name(), newValue);
        db.update(TBL_MONEY_BALANCE, contentValues, C_TS + "= " + "'" + linker + "'", null);

        int newTotal = 0;

        BalanceResult cr = getBalanceResultByLink(linker);
        for (Tag t : cr.getMoneyMap().keySet()) {
            newTotal += (cr.getMoneyMap().get(t) * t.getValue());
        }

        contentValues.put("_total", newTotal);
        db.update(TBL_MONEY_BALANCE, contentValues, C_TS + "= " + "'" + linker + "'", null);
    }

}

