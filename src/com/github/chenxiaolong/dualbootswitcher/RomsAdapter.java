package com.github.chenxiaolong.dualbootswitcher;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RomsAdapter extends ArrayAdapter<String> {
    private final String[] mValues;
    private final Context mContext;

    public RomsAdapter(Context context, String[] values) {
        super(context, R.layout.list_row, values);
        mContext = context;
        mValues = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.rom_row, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.rom_title);
        TextView desc = (TextView) rowView.findViewById(R.id.rom_desc);

        String s = mValues[position];
        title.setText(RomDetector.getName(mContext, s));

        String version = RomDetector.getVersion(s);
        if (version == null) {
            desc.setText(mContext.getString(R.string.couldnt_determine_version));
        } else {
            desc.setText(version);
            desc.setSingleLine();
            desc.setEllipsize(TextUtils.TruncateAt.END);
        }

        return rowView;
    }
}
