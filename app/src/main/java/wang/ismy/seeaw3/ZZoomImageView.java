package wang.ismy.seeaw3;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class ZZoomImageView extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
    //suppress the unused warning because maybe it will be used sometime later
    @SuppressWarnings("unused")
    private static final String TAG = "ZZoomImageView";

    /**
     * 最大放大倍数
     */
//    public static final float SCALE_MAX = 4.0f;

    /**
     * 默认缩放
     */
//    private float initScale = 1.0f;

    /**
     * 手势检测
     */
    ScaleGestureDetector scaleGestureDetector = null;

    Matrix scaleMatrix = new Matrix();



    /** 记录是拖拉照片模式还是放大缩小照片模式 */
    private int mode = 0;// 初始状态
    /** 拖拉照片模式 */
    private static final int MODE_DRAG = 1;
    /** 放大缩小照片模式 */
    private static final int MODE_ZOOM = 2;

    /** 用于记录开始时候的坐标位置 */
    private PointF startPoint = new PointF();
    /** 用于记录拖拉图片移动的坐标位置 */
    private Matrix matrix = new Matrix();
    /** 用于记录图片要进行拖拉时候的坐标位置 */
    private Matrix currentMatrix = new Matrix();

    /** 两个手指的开始距离 */
    private float startDis;
    /** 两个手指的中间点 */
    private PointF midPoint;

    /**
     * 处理矩阵的9个值
     */
//    float[] martixValue = new float[9];

    public ZZoomImageView(Context context) {
        this(context, null);
    }

    public ZZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    /**
     * 获取当前缩放比例
     */
//    public float getScale() {
//        scaleMatrix.getValues(martixValue);
//        return martixValue[Matrix.MSCALE_X];
//    }

    //--------------------------implement OnTouchListener----------------------------//

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 手指压下屏幕
            case MotionEvent.ACTION_DOWN:
                mode = MODE_DRAG;
                // 记录ImageView当前的移动位置
                currentMatrix.set(getImageMatrix());
                startPoint.set(event.getX(), event.getY());
                String t=event.getX()+"||"+event.getY();
                Log.e("point",t);
                break;
            // 手指在屏幕上移动，改事件会被不断触发
            case MotionEvent.ACTION_MOVE:
                // 拖拉图片
                if (mode == MODE_DRAG) {
                    float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                    float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
                    // 在没有移动之前的位置上进行移动
                    matrix.set(currentMatrix);
                    matrix.postTranslate(dx, dy);
                }
                // 放大缩小图片
                else if (mode == MODE_ZOOM) {
                    float endDis = distance(event);// 结束距离
                    if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        float scale = endDis / startDis;// 得到缩放倍数
                        matrix.set(currentMatrix);
                        matrix.postScale(scale, scale,midPoint.x,midPoint.y);
                    }
                }
                break;
            // 手指离开屏幕
            case MotionEvent.ACTION_UP:
                // 当触点离开屏幕，但是屏幕上还有触点(手指)
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = MODE_ZOOM;
                /** 计算两个手指间的距离 */
                startDis = distance(event);
                /** 计算两个手指间的中间点 */
                if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                    midPoint = mid(event);
                    //记录当前ImageView的缩放倍数
                    currentMatrix.set(getImageMatrix());
                }
                break;
        }
        setImageMatrix(matrix);
        return true;

//        return scaleGestureDetector.onTouchEvent(event);
    }

    //----------------------implement OnScaleGestureListener------------------------//

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
//        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null)
            return true;
//        Log.e(TAG,"君甚咸，此鱼何能及君也？");
//        if (scaleFactor * scale < initScale)
//            scaleFactor = initScale / scale;
//        if (scaleFactor * scale > SCALE_MAX)
//            scaleFactor = SCALE_MAX / scale;
        //设置缩放比例
        scaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 2);
        setImageMatrix(scaleMatrix);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /** 计算两个手指间的距离 */
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** 使用勾股定理返回两点之间的距离 */
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /** 计算两个手指间的中间点 */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }
}


