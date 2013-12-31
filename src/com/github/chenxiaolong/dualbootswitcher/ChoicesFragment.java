package com.github.chenxiaolong.dualbootswitcher;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ChoicesFragment extends ListFragment {
    public static final String[] mValues = new String[] { "CHOOSE_ROM",
            "SET_KERNEL", "REBOOT", "ABOUT", "EXIT" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ChoicesAdapter adapter = new ChoicesAdapter(inflater.getContext(),
                mValues);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mValues[position].equals("CHOOSE_ROM")) {
            showListDialog(ListDialogFragment.CHOOSE_ROM);
        } else if (mValues[position].equals("SET_KERNEL")) {
            showListDialog(ListDialogFragment.SET_KERNEL);
        } else if (mValues[position].equals("REBOOT")) {
            try {
                Commands.runCommand("reboot");
            } catch (Exception e) {
                showErrorAlert(getString(R.string.reboot_failed), e.getMessage());
            }
        } else if (mValues[position].equals("EXIT")) {
            getActivity().finish();
        }
    }

    private void showListDialog(int type) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ListDialogFragment prev = (ListDialogFragment) getFragmentManager().findFragmentByTag(
                ListDialogFragment.TAG);

        if (prev != null) {
            ft.remove(prev);
        }

        ListDialogFragment f = ListDialogFragment.newInstance(type);
        f.show(ft, ListDialogFragment.TAG);
    }

    private void showErrorAlert(String title, String message) {
        showAlert(title, message, null, null, getString(R.string.ok),
                new DismissDialog());
    }

    private void showAlert(String title, String message, String negativeText,
            DialogInterface.OnClickListener negative, String positiveText,
            DialogInterface.OnClickListener positive) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(
                AlertDialogFragment.TAG);

        if (prev != null) {
            ft.remove(prev);
        }

        AlertDialogFragment f = AlertDialogFragment.newInstance(title, message,
                negativeText, negative, positiveText, positive);
        f.show(ft, AlertDialogFragment.TAG);
    }

    public class DismissDialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Fragment prev = getFragmentManager().findFragmentByTag(
                    AlertDialogFragment.TAG);
            ((AlertDialogFragment) prev).dismiss();
        }
    }
}
