package pl.pszklarska.qrcodereader

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

import androidx.core.content.ContextCompat
import com.matheusvillela.flutter.plugins.qrcodereader.R

class OverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

  private val backgroundColor = ContextCompat.getColor(getContext(), R.color.backgroundColor)
  private val lineColor = ContextCompat.getColor(getContext(), R.color.lineColor)

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

  private var leftCorner: Int = 0
  private var topCorner: Int = 0
  private var rightCorner: Int = 0
  private var bottomCorner: Int = 0
  private var scannerLineHeight: Int = 0

  private var animator: ValueAnimator? = null

  init {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
  }

  public override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (leftCorner != 0 && topCorner != 0) {
      paint.color = backgroundColor
      paint.style = Paint.Style.FILL
      canvas.drawPaint(paint)

      paint.xfermode = xfermode
      canvas.drawRect(leftCorner.toFloat(), topCorner.toFloat(), rightCorner.toFloat(), bottomCorner.toFloat(), paint)

      paint.xfermode = null
      paint.color = lineColor
      paint.strokeWidth = 5f
      paint.strokeCap = Paint.Cap.ROUND
      val lineLength = 50

      canvas.drawLine(leftCorner.toFloat(), topCorner.toFloat(), (leftCorner + lineLength).toFloat(), topCorner.toFloat(), paint)
      canvas.drawLine(leftCorner.toFloat(), topCorner.toFloat(), leftCorner.toFloat(), (topCorner + lineLength).toFloat(), paint)
      canvas.drawLine(rightCorner.toFloat(), topCorner.toFloat(), (rightCorner - lineLength).toFloat(), topCorner.toFloat(), paint)
      canvas.drawLine(rightCorner.toFloat(), topCorner.toFloat(), rightCorner.toFloat(), (topCorner + lineLength).toFloat(), paint)
      canvas.drawLine(leftCorner.toFloat(), bottomCorner.toFloat(), (leftCorner + lineLength).toFloat(), bottomCorner.toFloat(), paint)
      canvas.drawLine(leftCorner.toFloat(), bottomCorner.toFloat(), leftCorner.toFloat(), (bottomCorner - lineLength).toFloat(), paint)
      canvas.drawLine(rightCorner.toFloat(), bottomCorner.toFloat(), (rightCorner - lineLength).toFloat(), bottomCorner.toFloat(), paint)
      canvas.drawLine(rightCorner.toFloat(), bottomCorner.toFloat(), rightCorner.toFloat(), (bottomCorner - lineLength).toFloat(), paint)

      paint.strokeWidth = 7f
      canvas.drawLine((leftCorner + 20).toFloat(), scannerLineHeight.toFloat(), (rightCorner - 20).toFloat(), scannerLineHeight.toFloat(), paint)
    }
  }


  override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
    val horizontalMargin = if (width < height) {
      dpToPx(context, 40f)
    } else {
      dpToPx(context, 300f)
    }


    leftCorner = horizontalMargin
    rightCorner = width - horizontalMargin

    val rectWidth = rightCorner - leftCorner
    topCorner = height / 2 - rectWidth / 2
    bottomCorner = topCorner + rectWidth
    scannerLineHeight = height / 2

    postInvalidate()
    initAnimation()
  }

  private fun initAnimation() {
    animator = ValueAnimator.ofInt(topCorner + 20, bottomCorner - 20)
    animator?.apply {
      addUpdateListener { animation ->
        scannerLineHeight = animation.animatedValue as Int
        postInvalidate()
      }
      repeatMode = ValueAnimator.REVERSE
      repeatCount = ValueAnimator.INFINITE
      interpolator = AccelerateDecelerateInterpolator()
      duration = 3000
      start()
    }

  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    animator?.cancel()
  }

  private fun dpToPx(context: Context, dp: Float): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
  }
}