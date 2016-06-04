package com.pepperonas.registerdroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.registerdroid.HistoryActivity;
import com.pepperonas.registerdroid.MainActivity;
import com.pepperonas.registerdroid.R;
import com.pepperonas.registerdroid.model.BalanceResult;
import com.pepperonas.registerdroid.dialogs.DialogDeleteBalance;
import com.pepperonas.registerdroid.interfaces.IDrawerHandler;
import com.pepperonas.registerdroid.various.Config;
import com.pepperonas.registerdroid.various.Utils;

import java.util.List;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p>
 *         Show a list with recent register information.
 */
public class FragmentHistory
        extends Fragment {

    private static final String TAG = "FragHistory";

    private MainActivity mMain;

    private IDrawerHandler mIDrawerHandler;


    public static FragmentHistory newInstance(int i) {
        FragmentHistory fragment = new FragmentHistory();
        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        mMain = (MainActivity) getActivity();
        mIDrawerHandler = mMain;

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<BalanceResult> results = mMain.getDatabase().getBalancesOfCurrentRegister();

        if (results.size() == 0) {
            ToastUtils.toastShort(R.string.no_data_found);
            return;
        }

        mIDrawerHandler.onCloseDrawer();

        final String[] dates = new String[results.size()];
        final String[] links = new String[results.size()];
        int i = 0;
        for (BalanceResult cr : results) {
            links[i] = cr.getLinker();
            dates[i++] = Utils.makePrintableTimeStamp(cr.getLinker());
        }

        final ListView lv = (ListView) getActivity().findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<String>(mMain, R.layout.history_list_item, dates));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getActivity(), HistoryActivity.class);
                i.putExtra(Config.BUNDLE_KEY_LINK, links[position]);
                startActivity(i);

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new DialogDeleteBalance(mMain, links[position]);
                return true;
            }
        });

    }

}
