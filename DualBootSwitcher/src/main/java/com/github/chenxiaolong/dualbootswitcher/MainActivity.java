package com.github.chenxiaolong.dualbootswitcher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;

public class MainActivity extends FragmentActivity {
    private LayoutInflater inflater;
    private View itemChooseRom,
                 itemChoosePrimaryRom,
                 itemChooseSecondaryRom,
                 itemBackupKernel,
                 itemBackupPrimaryKernel,
                 itemBackupSecondaryKernel,
                 itemAbout,
                 itemExit;

    private View[] itemsChooseMultiSlotRom,
                   itemsBackupMultiSlotKernel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedState.mActivity = new WeakReference<MainActivity>(this);

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ScrollView sv = (ScrollView)findViewById(R.id.scroll_view);
        LinearLayout ll = (LinearLayout)findViewById(R.id.main_menu);

        int n = getNumberOfRoms();
        itemsChooseMultiSlotRom = new View[n];
        itemsBackupMultiSlotKernel = new View[n];

        addChooseRomItem(ll);
        addBackupKernelItem(ll);
        addRebootItem(ll);
        addAboutItem(ll);
        addExitItem(ll);
        sv.setFillViewport(true);

        SharedState.mProgressDialog = new ProgressDialog((Context)this);
        SharedState.mProgressDialog.setCancelable(false);
        SharedState.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (SharedState.mProgressDialogVisible) {
            SharedState.mProgressDialog.setMessage(SharedState.mProgressDialogText);
            SharedState.mProgressDialog.setTitle(SharedState.mProgressDialogTitle);
            SharedState.mProgressDialog.show();
        }

        setRomChoiceExpanded(SharedState.romChoiceExpanded);
        setBackupKernelExpanded(SharedState.backupKernelExpanded);

        if (SharedState.mSimpleDialogVisible) {
            DialogUtils.showDialog(SharedState.mSimpleDialogText);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SharedState.mProgressDialog != null) {
            SharedState.mProgressDialog.dismiss();
            SharedState.mProgressDialog = null;
        }
        SharedState.mActivity = null;

        if (itemsChooseMultiSlotRom != null) {
            for (int i = 0; i < itemsChooseMultiSlotRom.length; i++) {
                itemsChooseMultiSlotRom[i] = null;
            }
            itemsChooseMultiSlotRom = null;
        }

        if (itemsBackupMultiSlotKernel != null) {
            for (int i = 0; i < itemsBackupMultiSlotKernel.length; i++) {
                itemsBackupMultiSlotKernel[i] = null;
            }
            itemsBackupMultiSlotKernel = null;
        }
    }

    private void addChooseRomItem(LinearLayout layout) {
        itemChooseRom = inflater.inflate(R.layout.list_item, null);
        layout.addView(itemChooseRom);

        TextView textView = (TextView)itemChooseRom.findViewById(R.id.label_list_item);
        textView.setText("Choose ROM");

        final ImageView imageView = (ImageView)itemChooseRom.findViewById(R.id.icon_list_item);
        imageView.setImageResource(R.drawable.ic_navigation_expand);

        addRomChoiceItems(layout);

        itemChooseRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRomChoiceExpansion();
            }
        });
    }

    private void addBackupKernelItem(LinearLayout layout) {
        itemBackupKernel = inflater.inflate(R.layout.list_item, null);
        layout.addView(itemBackupKernel);

        TextView textView = (TextView)itemBackupKernel.findViewById(R.id.label_list_item);
        //textView.setText("Backup Kernel");
        textView.setText("Set current kernel as ...");

        final ImageView imageView = (ImageView)itemBackupKernel.findViewById(R.id.icon_list_item);
        imageView.setImageResource(R.drawable.ic_navigation_expand);

        addBackupKernelItems(layout);

        itemBackupKernel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBackupKernelExpansion();
            }
        });
    }

    private void toggleRomChoiceExpansion() {
        if (SharedState.romChoiceExpanded == true) {
            setRomChoiceExpanded(false);
        }
        else {
            setRomChoiceExpanded(true);
        }
    }

    private void toggleBackupKernelExpansion() {
        if (SharedState.backupKernelExpanded == true) {
            setBackupKernelExpanded(false);
        }
        else {
            setBackupKernelExpanded(true);
        }
    }

    private synchronized void setRomChoiceExpanded(boolean expanded) {
        final ImageView imageView = (ImageView)itemChooseRom.findViewById(R.id.icon_list_item);

        if (expanded == true) {
            imageView.setImageResource(R.drawable.ic_navigation_collapse);
        }
        else {
            imageView.setImageResource(R.drawable.ic_navigation_expand);
        }

        refreshChoiceItems(expanded);
        SharedState.romChoiceExpanded = expanded;
    }

    private synchronized void setBackupKernelExpanded(boolean expanded) {
        final ImageView imageView = (ImageView)itemBackupKernel.findViewById(R.id.icon_list_item);

        if (expanded == true) {
            imageView.setImageResource(R.drawable.ic_navigation_collapse);
        }
        else {
            imageView.setImageResource(R.drawable.ic_navigation_expand);
        }

        refreshBackupItems(expanded);
        SharedState.backupKernelExpanded = expanded;
    }

    private void addRomChoiceItems(LinearLayout layout) {
        itemChoosePrimaryRom = inflater.inflate(R.layout.exp_list_item, null);
        itemChooseSecondaryRom = inflater.inflate(R.layout.exp_list_item, null);
        layout.addView(itemChoosePrimaryRom);
        layout.addView(itemChooseSecondaryRom);

        TextView textPrimaryRom = (TextView) itemChoosePrimaryRom.findViewById(R.id.label_list_item);
        TextView textSecondaryRom = (TextView) itemChooseSecondaryRom.findViewById(R.id.label_list_item);
        textPrimaryRom.setText("Primary ROM");
        textSecondaryRom.setText("Secondary ROM");

        ImageView imagePrimaryRom = (ImageView) itemChoosePrimaryRom.findViewById(R.id.icon_list_item);
        ImageView imageSecondaryRom = (ImageView) itemChooseSecondaryRom.findViewById(R.id.icon_list_item);
        imagePrimaryRom.setImageResource(R.drawable.ic_1);
        imageSecondaryRom.setImageResource(R.drawable.ic_2);

        itemChoosePrimaryRom.setVisibility(View.GONE);
        itemChooseSecondaryRom.setVisibility(View.GONE);

        itemChoosePrimaryRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeKernel(SharedState.KERNEL_PRIMARY);
            }
        });

        itemChooseSecondaryRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeKernel(SharedState.KERNEL_SECONDARY);
            }
        });

        for (int i = 0; i < itemsChooseMultiSlotRom.length; i++) {
            View temp = inflater.inflate(R.layout.exp_list_item, null);
            layout.addView(temp);

            TextView text = (TextView) temp.findViewById(R.id.label_list_item);
            text.setText("Extra ROM slot " + (i + 1));

            ImageView image = (ImageView) temp.findViewById(R.id.icon_list_item);
            image.setImageResource(R.drawable.ic_1);

            temp.setVisibility(View.GONE);

            final int j = i + 1;
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    writeKernel(j);
                }
            });

            itemsChooseMultiSlotRom[i] = temp;
        }
    }

    private void addBackupKernelItems(LinearLayout layout) {
        itemBackupPrimaryKernel = inflater.inflate(R.layout.exp_list_item, null);
        itemBackupSecondaryKernel = inflater.inflate(R.layout.exp_list_item, null);
        layout.addView(itemBackupPrimaryKernel);
        layout.addView(itemBackupSecondaryKernel);

        TextView textPrimaryKernel = (TextView) itemBackupPrimaryKernel.findViewById(R.id.label_list_item);
        TextView textSecondaryKernel = (TextView) itemBackupSecondaryKernel.findViewById(R.id.label_list_item);
        textPrimaryKernel.setText("Primary ROM's kernel");
        textSecondaryKernel.setText("Secondary ROM's kernel");

        ImageView imagePrimaryKernel = (ImageView) itemBackupPrimaryKernel.findViewById(R.id.icon_list_item);
        ImageView imageSecondaryKernel = (ImageView) itemBackupSecondaryKernel.findViewById(R.id.icon_list_item);
        imagePrimaryKernel.setImageResource(R.drawable.ic_1);
        imageSecondaryKernel.setImageResource(R.drawable.ic_2);

        itemBackupPrimaryKernel.setVisibility(View.GONE);
        itemBackupSecondaryKernel.setVisibility(View.GONE);

        itemBackupPrimaryKernel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backupKernel(SharedState.KERNEL_PRIMARY);
            }
        });

        itemBackupSecondaryKernel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backupKernel(SharedState.KERNEL_SECONDARY);
            }
        });

        for (int i = 0; i < itemsBackupMultiSlotKernel.length; i++) {
            View temp = inflater.inflate(R.layout.exp_list_item, null);
            layout.addView(temp);

            TextView text = (TextView) temp.findViewById(R.id.label_list_item);
            text.setText("Extra ROM slot " + (i + 1));

            ImageView image = (ImageView) temp.findViewById(R.id.icon_list_item);
            image.setImageResource(R.drawable.ic_1);

            temp.setVisibility(View.GONE);

            final int j = i + 1;
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    backupKernel(j);
                }
            });

            itemsBackupMultiSlotKernel[i] = temp;
        }
    }

    private void addRebootItem(LinearLayout layout) {
        View rowView = inflater.inflate(R.layout.list_item, null);
        layout.addView(rowView);

        final TextView textView = (TextView)rowView.findViewById(R.id.label_list_item);
        textView.setText("Reboot");

        ImageView imageView = (ImageView)rowView.findViewById(R.id.icon_list_item);
        imageView.setImageResource(R.drawable.ic_navigation_refresh);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DualBootUtils.reboot();
            }
        });
    }

    private void addAboutItem(LinearLayout layout) {
        View rowView = inflater.inflate(R.layout.list_item, null);
        layout.addView(rowView);

        final TextView textView = (TextView)rowView.findViewById(R.id.label_list_item);
        //textView.setText("About (not implemented yet)");
        try {
            textView.setText("Version " +
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            textView.setText("Failed to get version");
            e.printStackTrace();
        }

        ImageView imageView = (ImageView)rowView.findViewById(R.id.icon_list_item);
        imageView.setImageResource(R.drawable.ic_action_about);
    }

    private void addExitItem(LinearLayout layout) {
        View rowView = inflater.inflate(R.layout.list_item, null);
        layout.addView(rowView);

        TextView textView = (TextView)rowView.findViewById(R.id.label_list_item);
        textView.setText("Exit");

        ImageView imageView = (ImageView)rowView.findViewById(R.id.icon_list_item);
        imageView.setImageResource(R.drawable.ic_navigation_cancel);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) MainActivity.this).finish();
            }
        });
    }

    private void writeKernel(int which) {
        if (SharedState.busyWritingKernel) {
            DialogUtils.showDialog("How did you manage to get here?");
        }
        final WriteKernel task = new WriteKernel();
        task.execute(which);
    }

    private void backupKernel(int which) {
        if (SharedState.busyBackingUpKernel) {
            DialogUtils.showDialog("How did you manage to get here?");
        }
        final BackupKernel task = new BackupKernel();
        task.execute(which);
    }

    private boolean isExistsRom(String path) {
        File rawcache = new File("/raw-cache");
        File cache = new File("/cache");
        if ((rawcache.exists() && rawcache.canRead())
                || (cache.exists() && cache.canRead())) {
            File f = new File(path);
            if (f.exists() && f.isDirectory()) {
                return true;
            }
            return false;
        }
        else {
            int ret = 1;
            try {
                ret = DualBootUtils.runCommand("ls " + path);
            }
            catch (Exception e) {
            }
            return ret == 0;
        }
    }

    private boolean isExistsMultiRomSlot(int n) {
        if (isExistsRom("/raw-cache/multi-slot-" + n + "/system")
                || isExistsRom("/cache/multi-slot-" + n + "/system")) {
            return true;
        }
        return false;
    }

    private int getNumberOfRoms() {
        int counter = 1;
        // 10 ROMs should be enough, shouldn't it?
        int max = 10;

        for (int i = 0; i < max; i++) {
            if (isExistsMultiRomSlot(i)) {
                counter++;
            }
        }

        return counter;
    }

    private void refreshChoiceItems(boolean show) {
        if (show) {
            MainActivity.this.itemChoosePrimaryRom.setVisibility(View.VISIBLE);
        }
        else {
            MainActivity.this.itemChoosePrimaryRom.setVisibility(View.GONE);
        }

        if (isExistsRom("/raw-system/dual")
                || isExistsRom("/system/dual")) {
            if (show) {
                itemChooseSecondaryRom.setVisibility(View.VISIBLE);
            }
            else {
                itemChooseSecondaryRom.setVisibility(View.GONE);
            }
        }

        for (int i = 0; i < itemsChooseMultiSlotRom.length; i++) {
            if (isExistsMultiRomSlot(i + 1)) {
                if (show) {
                    itemsChooseMultiSlotRom[i].setVisibility(View.VISIBLE);
                }
                else {
                    itemsChooseMultiSlotRom[i].setVisibility(View.GONE);
                }
            }
        }
    }

    private void refreshBackupItems(boolean show) {
        if (show) {
            itemBackupPrimaryKernel.setVisibility(View.VISIBLE);
        }
        else {
            itemBackupPrimaryKernel.setVisibility(View.GONE);
        }

        if (isExistsRom("/raw-system/dual")
                || isExistsRom("/system/dual")) {
            if (show) {
                itemBackupSecondaryKernel.setVisibility(View.VISIBLE);
            }
            else {
                itemBackupSecondaryKernel.setVisibility(View.GONE);
            }
        }

        for (int i = 0; i < itemsBackupMultiSlotKernel.length; i++) {
            if (isExistsMultiRomSlot(i + 1)) {
                if (show) {
                    itemsBackupMultiSlotKernel[i].setVisibility(View.VISIBLE);
                }
                else {
                    itemsBackupMultiSlotKernel[i].setVisibility(View.GONE);
                }
            }
        }
    }
}
