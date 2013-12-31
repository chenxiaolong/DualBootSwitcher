package com.github.chenxiaolong.dualbootswitcher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChoicesAdapter extends ArrayAdapter<String> {
    private final String[] mValues;
    private final Context mContext;
    private final String mAppVer;

    public ChoicesAdapter(Context context, String[] values) {
        super(context, R.layout.list_row, values);
        mContext = context;
        mValues = values;
        String appVer;
        try {
            appVer = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVer = "ERROR";
        }
        mAppVer = appVer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_row, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.row_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.row_icon);

        String s = mValues[position];

        if (s.equals("CHOOSE_ROM")) {
            createChooseRomItem(textView, imageView);
        } else if (s.equals("SET_KERNEL")) {
            createSetKernelItem(textView, imageView);
        } else if (s.equals("REBOOT")) {
            createRebootItem(textView, imageView);
        } else if (s.equals("ABOUT")) {
            createAboutItem(textView, imageView);
        } else if (s.equals("EXIT")) {
            createExitItem(textView, imageView);
        }

        return rowView;
    }

    private void createChooseRomItem(TextView textView, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_launcher_check);
        textView.setText(R.string.choice_choose_rom);
    }

    private void createSetKernelItem(TextView textView, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_launcher_pin);
        textView.setText(R.string.choice_set_kernel);
    }

    private void createRebootItem(TextView textView, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_launcher_restart);
        textView.setText(R.string.choice_reboot);
    }

    private void createAboutItem(TextView textView, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_launcher_about);
        textView.setText(String.format(
                mContext.getString(R.string.choice_about), mAppVer));
    }

    private void createExitItem(TextView textView, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_launcher_exit);
        textView.setText(R.string.choice_exit);
    }
}
