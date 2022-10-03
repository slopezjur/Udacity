package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var textMessage = ""

    private val attributes = attrs
    private val auxRect = Rect()

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        if (old != new) {
            when (new) {
                ButtonState.Clicked -> {
                    buttonState = ButtonState.Loading
                    textMessage = context.getString(R.string.button_loading)
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
        context.withStyledAttributes(attributes, R.styleable.LoadingButton) {
            color = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
        }
    }

    private var paintText = Paint().apply {
        context.withStyledAttributes(attributes, R.styleable.LoadingButton) {
            color = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            textSize = getDimensionPixelSize(R.styleable.LoadingButton_buttonTextSize, 0) + 0f
            textMessage = getString(R.styleable.LoadingButton_buttonTextMessage).toString()
        }
        textAlign = Paint.Align.CENTER
        // Preview render fails with Typeface is defined
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawDownloadButton(canvas)
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