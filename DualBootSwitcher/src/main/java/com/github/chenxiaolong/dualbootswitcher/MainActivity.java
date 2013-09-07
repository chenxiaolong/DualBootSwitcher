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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedState.mActivity = new WeakReference<MainActivity>(this);

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ScrollView sv = (ScrollView)findViewById(R.id.scroll_view);
        LinearLayout ll = (LinearLayout)findViewById(R.id.main_menu);

        addChooseRomItem(ll);
        addBackupKernelItem(ll);
        //addRebootItem(ll);
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

        if (!isSecondRomInstalled()) {
            setRomChoiceExpanded(false);
            itemChooseRom.setVisibility(View.GONE);
            itemBackupSecondaryKernel.setVisibility(View.GONE);
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

        if (expanded == true && isSecondRomInstalled()) {
            imageView.setImageResource(R.drawable.ic_navigation_collapse);
            MainActivity.this.itemChoosePrimaryRom.setVisibility(View.VISIBLE);
            MainActivity.this.itemChooseSecondaryRom.setVisibility(View.VISIBLE);
            SharedState.romChoiceExpanded = true;
        }
        else {
            imageView.setImageResource(R.drawable.ic_navigation_expand);
            MainActivity.this.itemChoosePrimaryRom.setVisibility(View.GONE);
            MainActivity.this.itemChooseSecondaryRom.setVisibility(View.GONE);
            SharedState.romChoiceExpanded = false;
        }
    }

    private synchronized void setBackupKernelExpanded(boolean expanded) {
        final ImageView imageView = (ImageView)itemBackupKernel.findViewById(R.id.icon_list_item);

        if (expanded == true) {
            imageView.setImageResource(R.drawable.ic_navigation_collapse);
            MainActivity.this.itemBackupPrimaryKernel.setVisibility(View.VISIBLE);
            if (isSecondRomInstalled()) {
                MainActivity.this.itemBackupSecondaryKernel.setVisibility(View.VISIBLE);
            }
            SharedState.backupKernelExpanded = true;
        }
        else {
            imageView.setImageResource(R.drawable.ic_navigation_expand);
            MainActivity.this.itemBackupPrimaryKernel.setVisibility(View.GONE);
            MainActivity.this.itemBackupSecondaryKernel.setVisibility(View.GONE);
            SharedState.backupKernelExpanded = false;
        }
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
                if (SharedState.busyWritingKernel) {
                    DialogUtils.showDialog("How did you manage to get here?");
                }
                final WriteKernel task = new WriteKernel();
                task.execute(SharedState.KERNEL_PRIMARY);
            }
        });

        itemChooseSecondaryRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedState.busyWritingKernel) {
                    DialogUtils.showDialog("How did you manage to get here?");
                }
                final WriteKernel task = new WriteKernel();
                task.execute(SharedState.KERNEL_SECONDARY);
            }
        });
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
                if (SharedState.busyBackingUpKernel) {
                    DialogUtils.showDialog("How did you manage to get here?");
                }
                final BackupKernel task = new BackupKernel();
                task.execute(SharedState.KERNEL_PRIMARY);
            }
        });

        itemBackupSecondaryKernel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedState.busyBackingUpKernel) {
                    DialogUtils.showDialog("How did you manage to get here?");
                }
                final BackupKernel task = new BackupKernel();
                task.execute(SharedState.KERNEL_SECONDARY);
            }
        });
    }

    private void addRebootItem(LinearLayout layout) {
        View rowView = inflater.inflate(R.layout.list_item, null);
        layout.addView(rowView);

        final TextView textView = (TextView)rowView.findViewById(R.id.label_list_item);
        textView.setText("Reboot");

        ImageView imageView = (ImageView)rowView.findViewById(R.id.icon_list_item);
        imageView.setImageResource(R.drawable.ic_navigation_refresh);;

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

    private boolean isSecondRomInstalled() {
        File temp = new File("/raw-system/dual");
        if (!temp.exists() || !temp.isDirectory()) {
            temp = new File("/system/dual");
            if (!temp.exists() || !temp.isDirectory()) {
                return false;
            }
        }
        return true;
    }
}
