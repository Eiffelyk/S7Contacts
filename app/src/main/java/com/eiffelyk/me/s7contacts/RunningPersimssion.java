package com.eiffelyk.me.s7contacts;

/**
 * Created by Eiffelyk on 2016/3/21.
 * 权限封装，包括权限（系统请求使用）和说明（自定义的拒绝后的弹出对话框中的文字），
 */
public class RunningPersimssion {
    /**
     * 权限
     */
    String permission;
    /**
     * 说明
     */
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public RunningPersimssion(String permission, String name) {
        this.permission = permission;
        this.name = name;
    }

    public RunningPersimssion() {
    }

    @Override
    public String toString() {
        return "RunningPersimssion{" +
                "permission='" + permission + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}