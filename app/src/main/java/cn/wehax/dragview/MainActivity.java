package cn.wehax.dragview;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import cn.wehax.common.util.WindowUtils;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class MainActivity extends RoboActivity {
    public final static String TAG = "MainActivity";
    @InjectView(R.id.image_view1)
    ImageView imageView1;

    @InjectView(R.id.image_view2)
    ImageView imageView2;

    int statusBarHeight;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusBarHeight = WindowUtils.getStatusBarHeight(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int screenWidth = dm.widthPixels;
        final int screenHeight = dm.heightPixels - statusBarHeight;

        /**
         * 移动思路1：A点按下，移动到B，在移动到C，抬起<br>
         * （1）从A移动到B，计算移动相对量。（我们需要知道启动起始点和结束点位置）<br>
         * （2）控件在B的位置=控件在A的位置+移动相对量<br>
         */
        imageView1.setOnTouchListener(new OnTouchListener() {
            int lastX; // 上一个动作位置
            int lastY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        Log.e(TAG, "控件位置=（" + v.getLeft() + "," + v.getTop() + ")");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 控件在x和y方向上移动的相对量（可以是负值）
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        // 重新计算控件位置
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;

                        // 检查控件位置是否正确，如果不正确，修改之
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }

                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();
                        }

                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }

                        if (bottom > screenHeight) {
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }

                        // 设置控件位置
                        v.layout(left, top, right, bottom);

                        // 保存本次动作位置，下一个动作需要使用
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return true;
            }
        });

        /**
         * 移动思路2：A点按下，移动到B，在移动到C，抬起<br>
         * （一）用户手指从A点移动到B点，控件跟着移动。在移动过程中，按下点在控件中的位置肯定保持不变。
         * （二）如果我们知道（1）B点屏幕坐标（2）按下点在控件中的相对位置，不难算出控件位置
         */
        imageView2.setOnTouchListener(new OnTouchListener() {
            private float downRelativeX; // 按下点在控件中的相对位置
            private float downRelativeY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downRelativeX = event.getX();
                        downRelativeY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 计算控件位置
                        int left = (int) (event.getRawX() - downRelativeX);
                        int top = (int) (event.getRawY() - statusBarHeight - downRelativeY);
                        int right = left + v.getWidth();
                        int bottom = top + v.getHeight();

                        // 检查控件位置是否正确，如果不正确，修改之
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }

                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();
                        }

                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }

                        if (bottom > screenHeight) {
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }

                        // 设置控件位置
                        v.layout(left, top, right, bottom);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return true;
            }
        });
    }
}
