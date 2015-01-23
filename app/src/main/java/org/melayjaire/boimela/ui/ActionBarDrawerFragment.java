package org.melayjaire.boimela.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.melayjaire.boimela.R;

public class ActionBarDrawerFragment extends Fragment {

    public ActionBarDrawerFragment() {
    }

    public static ActionBarDrawerFragment newInstance() {
        ActionBarDrawerFragment actionBarDrawerFragment = new ActionBarDrawerFragment();
        return actionBarDrawerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_action_bar_drawer, container, false);
    }
}
