package com.hamzafrd.storia.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.hamzafrd.storia.R
import com.hamzafrd.storia.utils.isEmailValid

class EditTextField : AppCompatEditText, View.OnTouchListener {
    private lateinit var clearButtonImage: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    override fun setError(error: CharSequence?) {
        super.setError(error)
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        super.setError(error, icon)
    }

    private fun init() {

    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (id == R.id.edt_desc_story) {
            if (compoundDrawables[2] != null) {
                val clearButtonStart: Float
                val clearButtonEnd: Float
                var isClearButtonClicked = false
                if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                    when {
                        event.x < clearButtonEnd -> isClearButtonClicked = true
                    }
                } else {
                    clearButtonStart =
                        (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                    when {
                        event.x > clearButtonStart -> isClearButtonClicked = true
                    }
                }
                if (isClearButtonClicked) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            clearButtonImage =
                                ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                            showClearButton()
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            clearButtonImage =
                                ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                            when {
                                text != null -> text?.clear()
                            }
                            hideClearButton()
                            return true
                        }

                        else -> return false
                    }
                }
            }
        }
        return false
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}