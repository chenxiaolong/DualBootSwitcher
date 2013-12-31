package com.github.chenxiaolong.dualbootswitcher;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager fm = getFragmentManager();

        ChoicesFragment cf = (ChoicesFragment) fm
                .findFragmentByTag("choices_fragment");
        if (cf == null) {
            cf = new ChoicesFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, cf, "choices_fragment")
                    .commit();
        }
    }
}
