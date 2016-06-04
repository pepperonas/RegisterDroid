package com.pepperonas.registerdroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.andbasx.system.UsabilityUtils;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.registerdroid.MainActivity;
import com.pepperonas.registerdroid.R;
import com.pepperonas.registerdroid.model.Balance;
import com.pepperonas.registerdroid.model.Register;
import com.pepperonas.registerdroid.model.Tag;
import com.pepperonas.registerdroid.various.Config;
import com.pepperonas.registerdroid.various.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p/>
 *         Enter money.
 */
public class FragmentBalance extends Fragment implements View.OnFocusChangeListener {

    private static final String TAG = "FragmentBalance";

    private LinearLayout mLinearLayout;
    private EditText mFocusedEditText;
    private List<EditText> mEtAmountList = new ArrayList<EditText>();
    private Register mRegister;
    private int mTotal = 0;


    public static FragmentBalance newInstance(int i) {
        FragmentBalance fragment = new FragmentBalance();
        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView ");

        setHasOptionsMenu(true);

        if (getArguments().getInt("the_id", 0) == 0) {
            ((MainActivity) getActivity()).onCloseDrawer();
        }

        UsabilityUtils.keepKeyboardHidden(getActivity());

        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        mLinearLayout = (LinearLayout) view.findViewById(R.id.frag_balance_ll_container);

        mEtAmountList.clear();

        mRegister = new Register(String.valueOf(System.currentTimeMillis()));

        Balance cc = mRegister.getBalance();
        TreeMap<Tag, Integer> moneyMap = cc.getMoneyMap();

        for (Tag t : moneyMap.keySet()) {
            inflateLayouts(savedInstanceState, t.name(), t.toString());
        }

        /**
         * Loop through the layout and set the 'OnFocusChangeListener'.
         * */
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            if (mLinearLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) mLinearLayout.getChildAt(i);
                for (int j = 0; j < ll.getChildCount(); j++) {
                    if (ll.getChildAt(j) instanceof EditText) {
                        ll.getChildAt(j).setOnFocusChangeListener(this);
                    }
                }
            }
        }

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        menu.add(0, 0, 0, R.string.save).setIcon(R.drawable.ic_marked_circle_white_24dp)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                storeData();
                return true;
            }
        });
    }


    private void inflateLayouts(Bundle savedInstanceState, String key, String text) {
        final View v = getLayoutInflater(savedInstanceState).inflate(R.layout.row_blance_input, null);

        TextView tvPos = (TextView) v.findViewById(R.id.frag_balance_tv_pos);
        final EditText etAmount = (EditText) v.findViewById(R.id.frag_balance_et_amount);
        TextView tvSum = (TextView) v.findViewById(R.id.frag_balance_tv_show_sum);

        tvPos.setText(text);

        etAmount.setTag(Config.TAG_AMOUNT + key);
        etAmount.setHint(R.string.amnt);

        tvSum.setTag(Config.TAG_SUM + key);
        tvSum.setText(R.string.empty_value);

        mEtAmountList.add(etAmount);

        mLinearLayout.addView(v);
    }


    @Override
    public void onFocusChange(final View v, boolean hasFocus) {

        if (hasFocus) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            mFocusedEditText = (EditText) v;
            final String baseTag = mFocusedEditText.getTag().toString().replace(Config.TAG_AMOUNT, "");
            LinearLayout ll = (LinearLayout) v.getParent();
            final TextView tvSum = (TextView) ll.findViewWithTag(Config.TAG_SUM + baseTag);

            mFocusedEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }


                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }


                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty()) return;

                    Tag t = Tag.valueOf(baseTag);
                    int value = t.getValue() * Integer.parseInt(s.toString());
                    tvSum.setText(Utils.formatSingleValue(getActivity(), value));

                    mTotal = 0;

                    for (TextView tvAmount : mEtAmountList) {
                        if (!tvAmount.getText().toString().isEmpty()) {

                            String tag = tvAmount.getTag().toString().replace(Config.TAG_AMOUNT, "");
                            int amounts = Integer.parseInt(tvAmount.getText().toString());

                            Tag tmpC = Tag.valueOf(tag);
                            mTotal += tmpC.getValue() * amounts;

                            updateToolbarTitle();
                        }
                    }
                }
            });
        }
    }


    public void fireAnUpdate(int i) {
        int currentValue = 0;
        if (!mFocusedEditText.getText().toString().isEmpty()) {
            currentValue = Integer.parseInt(mFocusedEditText.getText().toString());
        }
        currentValue += i;
        if (currentValue < 0) {
            return;
        }

        mFocusedEditText.setText(String.valueOf(currentValue));
    }


    /**
     * Store current values in a {@link Bundle}.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Integer> tmpValues = new ArrayList<Integer>(Balance.AMOUNT_VALUES);
        for (TextView tvAmount : mEtAmountList) {
            if (!tvAmount.getText().toString().isEmpty()) {
                int amount = Integer.parseInt(tvAmount.getText().toString());
                tmpValues.add(amount);
            } else tmpValues.add(0);
        }

        outState.putIntegerArrayList("values", tmpValues);
    }


    /**
     * Restore the state.
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        restoreTemporaryData(savedInstanceState);
    }


    public void restoreTemporaryData(Bundle savedInstanceState) {
        // not when starting...
        if (savedInstanceState == null) return;

        ArrayList<Integer> tmpValues = savedInstanceState.getIntegerArrayList("values");

        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            if (mLinearLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) mLinearLayout.getChildAt(i);
                if (ll.getChildAt(1) instanceof EditText) {
                    ((EditText) ll.getChildAt(1)).setText(String.valueOf(tmpValues.get(i)));
                }
                if (ll.getChildAt(2) instanceof TextView) {
                    if (tmpValues.get(i) != 0) {
                        Tag t = Tag.valueOf(ll.getChildAt(2).getTag().toString().replace(Config.TAG_SUM, ""));
                        int value = t.getValue() * Integer.parseInt(((EditText) ll.getChildAt(1)).getText().toString());
                        ((TextView) ll.getChildAt(2)).setText(Utils.formatSingleValue(getActivity(), value));
                    } else ((TextView) ll.getChildAt(2)).setText(getString(R.string.empty_value));
                }
            }
        }
    }


    private void updateToolbarTitle() {
        MainActivity main = (MainActivity) getActivity();
        main.showTotalAsToolbarTitle(mTotal);
    }


    private void storeData() {
        int[] values = new int[Balance.AMOUNT_VALUES];
        int i = 0;

        for (TextView tvAmount : mEtAmountList) {
            if (!tvAmount.getText().toString().isEmpty()) {
                int amounts = Integer.parseInt(tvAmount.getText().toString());
                values[i] = amounts;
            } else values[i] = 0;
            i++;
        }

        MainActivity main = (MainActivity) getActivity();
        mRegister.makeCollection(values);
        main.getDatabase().addRegister(mRegister);
        main.getDatabase().addMoneyCollection(mRegister.getBalance(), mTotal);

        reset();
    }


    /**
     * Reset (clear) all fields to get ready for the next input.
     */
    private void reset() {
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            if (mLinearLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) mLinearLayout.getChildAt(i);
                for (int j = 0; j < ll.getChildCount(); j++) {
                    if (ll.getChildAt(j) instanceof EditText) {
                        ((EditText) ll.getChildAt(j)).setText("");
                    }
                    if (ll.getChildAt(j) instanceof TextView) {
                        if (((TextView) ll.getChildAt(j)).getText().toString().contains(" €")) {
                            ((TextView) ll.getChildAt(j)).setText(getString(R.string.empty_value));
                        }
                    }
                }
            }
        }

        ToastUtils.toastShort(getString(R.string.save_data));

        mRegister = new Register(String.valueOf(System.currentTimeMillis()));
        mTotal = 0;
    }

}