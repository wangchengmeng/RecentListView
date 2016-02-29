package com.example.administrator.recentlistview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author wangchengmeng
 * @desc UI的帮助类
 */
public class UIUtil {
    private static final String       TAG                  = "UIUtil";
    private static final Object       mSync                = new Object();
    private static final int          DEFAULT_COOLING_TIME = 1000;
    private static final List<String> ACTION_LIST          = new ArrayList<>();

    public static boolean checkBits(int status, int checkBit) {
        return (status & checkBit) == checkBit;
    }

    /**
     * 设置listView高度，以适应内容
     *
     * @param listView       指定的listView
     * @param hasFixedHeight 是否每个item的高度一致,提高计算效率
     */
    public static void setListViewHeightMatchContent(ListView listView, boolean hasFixedHeight) {
        try {
            // 获取ListView对应的Adapter
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }

            int totalHeight = 0;
            int count = listAdapter.getCount();
            if (count > 0) {
                if (hasFixedHeight) {
                    View listItem = listAdapter.getView(0, null, listView);
                    listItem.measure(0, 0);
                    totalHeight = count * listItem.getMeasuredHeight();
                } else {
                    for (int i = 0; i < count; i++) {
                        View listItem = listAdapter.getView(i, null, listView);
                        //修复缺陷： item的父布局要为线性布局，或者参考网址http://www.chengxuyuans.com/Android/85072.html
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }
                }
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            // listView.getDividerHeight()获取子项间分隔符占用的高度
            // params.height最后得到整个ListView完整显示需要的高度
            listView.setLayoutParams(params);
        } catch (Exception e) {
        }
    }

    /**
     * 设置view的高度
     *
     * @param view   指定的view
     * @param height 指定的高度，以像素为单位
     */
    public static void setViewHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }

    /**
     * 设置view的宽度
     *
     * @param view  指定的view
     * @param width 指定的宽度，以像素为单位
     */
    public static void setViewWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        view.setLayoutParams(params);
    }

    /**
     * 限制执行频率的方法。如按钮需要在指定的3000ms时间后才能再次执行，使用方式如：<br>
     *
     * @param id             方法的标识，可以使用按钮控件的id或者其他唯一标识方法的字符串
     * @param actionListener 方法的回调函数
     */
    public static void limitReClick(final String id, ActionListener actionListener) {
        if (StringUtil.isNullOrEmpty(id) || actionListener == null) {
            throw new NullPointerException();
        }

        synchronized (mSync) {
            if (ACTION_LIST.contains(id)) {
                return;
            } else {
                ACTION_LIST.add(id);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        removeAction(id);
                    }
                }, DEFAULT_COOLING_TIME);
            }
        }
        actionListener.doAction();
    }

    /**
     * 限制执行频率的方法。如按钮需要在指定的时间后才能再次执行，使用方式如：<br>
     *
     * @param id             方法的标识，可以使用按钮控件的id或者其他唯一标识方法的字符串
     * @param delay          延迟时间，以毫秒为单位
     * @param actionListener 方法的回调函数
     */
    public static void limitReClick(final String id, int delay, ActionListener actionListener) {
        synchronized (mSync) {
            if (ACTION_LIST.contains(id)) {
                return;
            } else {
                ACTION_LIST.add(id);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        removeAction(id);
                    }
                }, delay);
            }
        }
        actionListener.doAction();
    }

    /**
     * 把View绘制到Bitmap上
     *
     * @param view   需要绘制的View
     * @param width  该View的宽度
     * @param height 该View的高度
     * @return 返回Bitmap对象
     */
    public static Bitmap getBitmapFromView(View view, int width, int height, Config bitmapConfig) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, bitmapConfig);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        view.draw(canvas);
        return bitmap;
    }

    public static void removeAction(String id) {
        synchronized (mSync) {
            ACTION_LIST.remove(id);
        }
    }

    public static Drawable getArrowDrawable(Context context, int id) {
        Drawable drawable = context.getResources().getDrawable(id);
        if (null == drawable) {
            return null;
        }
        // / 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

    /**
     * 移动光标到最后
     *
     * @param editText 输入框
     */
    public static void moveCursorToEnd(EditText editText) {
        if (editText == null) {
            return;
        }
        Editable text = editText.getText();
        if (text != null) {
            Selection.setSelection(text, text.length());
        }
    }

    /**
     * @author zou.sq
     */
    public interface ActionListener {
        /**
         * 限制点击冻结接触方法
         */
        void doAction();
    }
}
