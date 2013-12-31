package com.github.chenxiaolong.dualbootswitcher;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ListDialogFragment extends DialogFragment {
    public static final String TAG = "list_dialog";
    public static final int CHOOSE_ROM = 1;
    public static final int SET_KERNEL = 2;

    private String[] mRoms;
    private int mType;

    private boolean mShowingProgress = false;
    private ProgressBar mProgressBar;
    private ListView mListView;

    private static ListDialogFragment newInstance() {
        ListDialogFragment f = new ListDialogFragment();

        return f;
    }

    public static ListDialogFragment newInstance(int type) {
        ListDialogFragment f = newInstance();

        Bundle args = new Bundle();
        args.putInt("type", type);

        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }

        View v = getActivity().getLayoutInflater().inflate(
                R.layout.list_dialog, container);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressbar);
        mListView = (ListView) v.findViewById(R.id.listview);

        if (mRoms == null) {
            mRoms = RomDetector.getRoms();
        }
        RomsAdapter adapter = new RomsAdapter(inflater.getContext(), mRoms);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new OnRomItemClickListener());

        updateView();

        if (mType == CHOOSE_ROM) {
            getDialog().setTitle(getString(R.string.choice_choose_rom));
        } else if (mType == SET_KERNEL) {
            getDialog().setTitle(getString(R.string.choice_set_kernel));
        }

        return v;
    }

    // Work around bug: http://stackoverflow.com/a/15444485/1064977
    @Override
    public void onDestroyView() {
        mListView = null;
        mProgressBar = null;

        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    private void updateView() {
        if (mShowingProgress) {
            setCancelable(false);
            mListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            setCancelable(true);
            mListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
        getDialog().setCanceledOnTouchOutside(false);
    }

    public class OnRomItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            mShowingProgress = true;
            updateView();

            if (mType == CHOOSE_ROM) {
                writeKernel(mRoms[position]);
            } else if (mType == SET_KERNEL) {
                backupKernel(mRoms[position]);
            }
        }
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

    private static final int EVENT_WROTE_KERNEL = 1;
    private static final int EVENT_BACKED_UP_KERNEL = 2;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
            case EVENT_WROTE_KERNEL: {
                Bundle data = msg.getData();
                boolean failed = data.getBoolean("failed");
                String failMsg = data.getString("failMsg");
                String rom = data.getString("rom");

                if (failed) {
                    showAlert(getString(R.string.write_kernel_failure),
                            failMsg, null, null, getString(R.string.ok), null);
                } else {
                    showAlert(getString(R.string.write_kernel_success),
                            String.format(getString(R.string.write_kernel_msg),
                                    RomDetector.getName(getActivity(), rom)),
                            null, null, getString(R.string.ok), null);
                }

                break;
            }

            case EVENT_BACKED_UP_KERNEL: {
                Bundle data = msg.getData();
                boolean failed = data.getBoolean("failed");
                String failMsg = data.getString("failMsg");
                String rom = data.getString("rom");

                if (failed) {
                    showAlert(getString(R.string.back_up_kernel_failure),
                            failMsg, null, null, getString(R.string.ok), null);
                } else {
                    showAlert(getString(R.string.back_up_kernel_success),
                            String.format(
                                    getString(R.string.back_up_kernel_msg),
                                    RomDetector.getName(getActivity(), rom)),
                            null, null, getString(R.string.ok), null);
                }

                break;
            }
            }

            mShowingProgress = false;
            dismiss();
        }
    };

    private void writeKernel(final String rom) {
        new Thread() {
            @Override
            public void run() {
                boolean failed = true;
                String failMsg = "";
                try {
                    DualBootUtils.writeKernel(rom);
                    failed = false;
                } catch (Exception e) {
                    failMsg = e.getMessage();
                }
                Message msg = mHandler.obtainMessage(EVENT_WROTE_KERNEL);
                Bundle data = new Bundle();
                data.putBoolean("failed", failed);
                data.putString("failMsg", failMsg);
                data.putString("rom", rom);
                msg.setData(data);
                msg.sendToTarget();
            }
        }.start();
    }

    private void backupKernel(final String rom) {
        new Thread() {
            @Override
            public void run() {
                boolean failed = true;
                String failMsg = "";
                try {
                    DualBootUtils.backupKernel(rom);
                    failed = false;
                } catch (Exception e) {
                    failMsg = e.getMessage();
                }
                Message msg = mHandler.obtainMessage(EVENT_BACKED_UP_KERNEL);
                Bundle data = new Bundle();
                data.putBoolean("failed", failed);
                data.putString("failmsg", failMsg);
                data.putString("rom", rom);
                msg.setData(data);
                msg.sendToTarget();
            }
        }.start();
    }
}
