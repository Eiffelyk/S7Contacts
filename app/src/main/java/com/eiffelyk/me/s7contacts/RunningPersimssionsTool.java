package com.eiffelyk.me.s7contacts;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eiffelyk on 2016/3/21.
 * 封装的运行时权限调用的工具类（Activity）
 */
public class RunningPersimssionsTool {
    /**
     * 回调常量
     */
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    /**
     * 拒绝后第二次弹出后展示的权限使用说明的自定义对话框
     * @param context  依赖
     * @param message  需要展示的文字
     * @param okListener  确认键的回调ClickListener
     */
    private static void showMessageOKCancel(Context context,String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("允许", okListener)
                .setNegativeButton("拒绝", null)
                .create()
                .show();
    }

    /**
     * 申请权限的方法
     * @param act  需要调用权限方法的页面
     * @param runningPersimssionArrayList  需要请求的 权限数组
     * @return  true已经全部授权，false没有授权或者部分授权
     */
    public static boolean insertDummyContactWrapper(final Activity act, ArrayList<RunningPersimssion> runningPersimssionArrayList) {
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();
        for (RunningPersimssion run : runningPersimssionArrayList) {
            if (!addPermission(act,permissionsList, run.getPermission())) {
                permissionsNeeded.add(run.getName());
            }
        }
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "您需要允许本应用 " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(act,message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(act, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return false;
            }
            ActivityCompat.requestPermissions(act, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    /**
     * 将单个权限加入到权限申请的数组中
     * @param activity  需要请求权限的页面
     * @param permissionsList  权限数组
     * @param permission  单个权限
     * @return  此权限是否已经授权
     */
    private static boolean addPermission(Activity activity,List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return false;
        }
        return true;
    }
}
