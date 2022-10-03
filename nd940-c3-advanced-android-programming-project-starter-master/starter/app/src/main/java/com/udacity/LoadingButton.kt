package com.udacity

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val DOWNLOADING_BUTTON_PERCENTAGE = 100.0
        const val PERCENTAGE = "percentage"
    }

    private var widthSize = 0
    private var heightSize = 0
    private var textMessage = ""

    private var currentDownloadingWidth = 0
    private var defaultButtonBackgroundColor = 0

    private val auxRect = Rect()

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        if (old != new) {
            when (new) {
                ButtonState.Clicked -> {
                    buttonState = ButtonState.Loading
                    textMessage = context.getString(R.string.button_loading)
                    startDownloadButtonAnimation()
                    invalidate()
                }
                ButtonState.Completed -> {
                    buttonState = ButtonState.Completed
                    textMessage = context.getString(R.string.download)
                    invalidate()
                }
                else -> {
                    // Do nothing
                }
            }
        }
    }

    private var paintRect = Paint().apply {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultButtonBackgroundColor =
                getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
            color = defaultButtonBackgroundColor
        }
    }

    private var paintText = Paint().apply {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            color = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            textSize = getDimensionPixelSize(R.styleable.LoadingButton_buttonTextSize, 0) + 0f
            textMessage = getString(R.styleable.LoadingButton_buttonTextMessage).toString()
        }
        textAlign = Paint.Align.CENTER
        // TODO : Preview render fails when Typeface is defined. Comment to check the preview
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val paintDownloadButton = Paint().apply {
        color = resources.getColor(R.color.colorPrimaryDark)
    }

    private fun startDownloadButtonAnimation() {
        val valuesHolder = PropertyValuesHolder.ofFloat(PERCENTAGE, 0f, 100f)
        valueAnimator.apply {
            setValues(valuesHolder)
            duration = 1500
            addUpdateListener {
                val percentage = it.getAnimatedValue(PERCENTAGE) as Float
                currentDownloadingWidth = percentage.toInt()
                invalidate()
                if (buttonState == ButtonState.Completed) {
                    valueAnimator.end()
                }
            }
            repeatCount = Animation.INFINITE
        }
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawDownloadButton(canvas)
        if (buttonState == ButtonState.Loading) {
            drawDownloadingButton(canvas)
        }
        drawDownloadText(canvas)
    }

    private fun drawDownloadButton(canvas: Canvas?) {
        canvas?.drawRect(widthSize + 0f, heightSize + 0f, 0f, 0f, paintRect)
        paintText.getTextBounds(
            textMessage,
            0,
            textMessage.length,
            auxRect
        )
    }

    private fun drawDownloadText(canvas: Canvas?) {
        // TODO : Check how to write text
        canvas?.drawText(
            textMessage.uppercase(),
            (widthSize / 2) + 0f,
            (heightSize / 2) + 0f - auxRect.exactCenterY(),
            paintText
        )
    }

    private fun drawDownloadingButton(canvas: Canvas?) {
        val percentageToFill =
            (widthSize * (currentDownloadingWidth / DOWNLOADING_BUTTON_PERCENTAGE)).toFloat()
        canvas?.drawRect(percentageToFill + 0f, heightSize + 0f, 0f, 0f, paintDownloadButton)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun buttonClicked() {
        buttonState = ButtonState.Clicked
    }

    fun buttonCompleted() {
        buttonState = ButtonState.Completed
    }
}