package com.example.mvp_autorskiy_start.ui.test

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Quiz
import kotlin.math.PI
import kotlin.math.sin

class PathMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Компактные значения в dp
    private val startYDp = 60f
    private val verticalSpacingDp = 110f
    private val horizontalAmplitudeDp = 60f
    private val pointRadiusDp = 22f
    private val touchRadiusDp = 40f
    private val labelOffsetDp = 10f
    private val labelCornerDp = 8f
    private val decorationSizeDp = 40f
    private val lampSizeDp = 45f

    private var startY = 0f
    private var verticalSpacing = 0f
    private var horizontalAmplitude = 0f
    private var pointRadius = 0f
    private var touchRadius = 0f
    private var labelOffset = 0f
    private var labelCorner = 0f
    private var decorationSize = 0f
    private var lampSize = 0f
    private var density = 0f

    private var quizzes: List<Quiz> = emptyList()
    private val pointPositions = mutableListOf<PointF>()
    private val fullPath = Path()
    private var clipPath = Path()
    private var pathProgress = 0f
    private var pathAnimator: ValueAnimator? = null
    private val pointAlpha = mutableMapOf<Int, Float>()
    private val pointScale = mutableMapOf<Int, Float>()

    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.argb(200, 160, 100, 60)
        strokeCap = Paint.Cap.ROUND
    }
    private val pathShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 7f
        color = Color.argb(80, 0, 0, 0)
        setShadowLayer(5f, 0f, 2f, Color.argb(100, 0, 0, 0))
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 13f
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 13f
        color = Color.argb(255, 80, 50, 20)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val labelBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(220, 255, 248, 225)
        setShadowLayer(3f, 1f, 1f, Color.argb(80, 0, 0, 0))
    }
    private val lockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GRAY }
    private val pointShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(80, 0, 0, 0)
        setShadowLayer(5f, 0f, 2f, Color.argb(120, 0, 0, 0))
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 255, 215, 0)
        style = Paint.Style.STROKE
        strokeWidth = 4f
        setShadowLayer(8f, 0f, 0f, Color.argb(200, 255, 215, 0))
    }
    private var glowAnimator: ValueAnimator? = null
    private var glowAlpha = 0f
    private var glowingPointIndex = -1

    private var onQuizClickListener: ((Quiz) -> Unit)? = null

    private val backgroundGradient = LinearGradient(0f, 0f, 0f, 1f, intArrayOf(
        Color.parseColor("#FFF5E6"),
        Color.parseColor("#F5E6D3")
    ), null, Shader.TileMode.CLAMP)

    private val gradientsCache = mutableMapOf<Boolean, RadialGradient>()
    private var hasShownHint = false

    // Декоративные элементы
    private var bookPointDrawable: Drawable? = null
    private var quillTreeDrawable: Drawable? = null
    private var openBookHillDrawable: Drawable? = null
    private var lampDrawable: Drawable? = null

    init {
        density = resources.displayMetrics.density
        setLayerType(LAYER_TYPE_HARDWARE, null)
        updateDimensions()
        loadDecorations()
    }

    private fun loadDecorations() {
        bookPointDrawable = ContextCompat.getDrawable(context, R.drawable.ic_book_point)
        quillTreeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_quill_tree)
        openBookHillDrawable = ContextCompat.getDrawable(context, R.drawable.ic_open_book_hill)
        lampDrawable = ContextCompat.getDrawable(context, R.drawable.ic_lamp_knowledge)
    }

    private fun updateDimensions() {
        startY = dpToPx(startYDp)
        verticalSpacing = dpToPx(verticalSpacingDp)
        horizontalAmplitude = dpToPx(horizontalAmplitudeDp)
        pointRadius = dpToPx(pointRadiusDp)
        touchRadius = dpToPx(touchRadiusDp)
        labelOffset = dpToPx(labelOffsetDp)
        labelCorner = dpToPx(labelCornerDp)
        decorationSize = dpToPx(decorationSizeDp)
        lampSize = dpToPx(lampSizeDp)

        textPaint.textSize = dpToPx(13f)
        labelPaint.textSize = dpToPx(13f)
        pathPaint.strokeWidth = dpToPx(5f)
        pathShadowPaint.strokeWidth = dpToPx(7f)
        glowPaint.strokeWidth = dpToPx(4f)
        pointShadowPaint.setShadowLayer(dpToPx(5f), 0f, dpToPx(2f), Color.argb(120, 0, 0, 0))
        labelBgPaint.setShadowLayer(dpToPx(3f), dpToPx(1f), dpToPx(1f), Color.argb(80, 0, 0, 0))
        glowPaint.setShadowLayer(dpToPx(8f), 0f, 0f, Color.argb(200, 255, 215, 0))
    }

    private fun dpToPx(dp: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
    )

    private fun getPointGradient(isCompleted: Boolean): RadialGradient {
        return gradientsCache.getOrPut(isCompleted) {
            RadialGradient(0f, 0f, pointRadius,
                intArrayOf(
                    if (isCompleted) Color.parseColor("#66BB6A") else Color.parseColor("#FFD700"),
                    if (isCompleted) Color.parseColor("#388E3C") else Color.parseColor("#FFA500")
                ),
                null, Shader.TileMode.CLAMP
            )
        }
    }

    fun setQuizzes(quizzes: List<Quiz>) {
        this.quizzes = quizzes
        if (quizzes.isEmpty()) return
        updateDimensions()
        computePositions()
        startPathAnimation()
        findAndGlowNextPoint()
        animatePointsAppearance()
        requestLayout()
        invalidate()
        showFirstTimeHint()
    }

    private fun showFirstTimeHint() {
        if (!hasShownHint && quizzes.isNotEmpty()) {
            hasShownHint = true
            postDelayed({
                Toast.makeText(context, "Нажмите на точку, чтобы пройти тест", Toast.LENGTH_LONG).show()
            }, 500)
        }
    }

    fun setOnQuizClickListener(listener: (Quiz) -> Unit) {
        onQuizClickListener = listener
    }

    fun getPointY(index: Int): Float = pointPositions.getOrNull(index)?.y ?: 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        backgroundGradient.setLocalMatrix(Matrix().apply { setScale(1f, h.toFloat()) })
        computePositions()
    }

    private fun computePositions() {
        pointPositions.clear()
        if (quizzes.isEmpty()) return
        val centerX = width / 2f
        for (i in quizzes.indices) {
            val offsetX = (sin(i * PI / 2) * horizontalAmplitude).toFloat()
            val x = centerX + offsetX
            val y = startY + i * verticalSpacing
            pointPositions.add(PointF(x, y))
        }
        buildFullPath()
    }

    private fun buildFullPath() {
        fullPath.reset()
        if (pointPositions.size < 2) return
        fullPath.moveTo(pointPositions[0].x, pointPositions[0].y)
        for (i in 1 until pointPositions.size - 1) {
            val p0 = pointPositions[i - 1]
            val p1 = pointPositions[i]
            val p2 = pointPositions[i + 1]
            val cp1x = p0.x + (p1.x - p0.x) * 0.5f
            val cp1y = p0.y
            val cp2x = p1.x - (p2.x - p1.x) * 0.5f
            val cp2y = p1.y
            fullPath.cubicTo(cp1x, cp1y, cp2x, cp2y, p1.x, p1.y)
        }
        val last = pointPositions.last()
        fullPath.lineTo(last.x, last.y)
        updateClipPath()
    }

    private fun updateClipPath() {
        clipPath.reset()
        val measure = PathMeasure(fullPath, false)
        val length = measure.length
        val stop = pathProgress * length
        clipPath.moveTo(pointPositions[0].x, pointPositions[0].y)
        if (stop > 0) {
            val pos = FloatArray(2)
            measure.getPosTan(stop, pos, null)
            clipPath.lineTo(pos[0], pos[1])
        } else {
            clipPath.lineTo(pointPositions[0].x, pointPositions[0].y)
        }
    }

    private fun startPathAnimation() {
        pathAnimator?.cancel()
        pathAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                pathProgress = it.animatedValue as Float
                updateClipPath()
                invalidate()
            }
            start()
        }
    }

    private fun animatePointsAppearance() {
        for (i in quizzes.indices) {
            pointAlpha[i] = 0f
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 200
                startDelay = i * 30L
                addUpdateListener { animation ->
                    pointAlpha[i] = animation.animatedValue as Float
                    invalidate()
                }
                start()
            }
        }
    }

    private fun findAndGlowNextPoint() {
        val nextUnlocked = quizzes.indexOfFirst { it.isUnlocked && !it.isCompleted }
        if (nextUnlocked != -1 && nextUnlocked != glowingPointIndex) {
            startGlow(nextUnlocked)
        }
    }

    private fun startGlow(index: Int) {
        glowingPointIndex = index
        glowAnimator?.cancel()
        glowAnimator = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                glowAlpha = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = if (quizzes.isNotEmpty()) {
            (startY + (quizzes.size - 1) * verticalSpacing + dpToPx(100f)).toInt()
        } else {
            suggestedMinimumHeight
        }
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        val width = resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(width, height)
        computePositions()
    }

    override fun onDraw(canvas: Canvas) {
        if (quizzes.isEmpty() || pointPositions.size != quizzes.size) return

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply { shader = backgroundGradient })

        // Рисуем декорации (деревья, книжки на холмах)
        drawDecorations(canvas)

        canvas.drawPath(fullPath, pathShadowPaint)
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawPath(fullPath, pathPaint)
        canvas.restore()

        for ((index, pos) in pointPositions.withIndex()) {
            val scale = pointScale[index] ?: 1f
            canvas.save()
            canvas.translate(pos.x, pos.y)
            canvas.scale(scale, scale)
            drawPoint(canvas, 0f, 0f, quizzes[index], index)
            canvas.restore()
            drawLabel(canvas, pos, quizzes[index])
        }

        drawLamp(canvas)
    }

    private fun drawDecorations(canvas: Canvas) {
        for ((i, pos) in pointPositions.withIndex()) {
            val leftX = pos.x - decorationSize * 1.2f
            val rightX = pos.x + decorationSize * 1.2f
            val y = pos.y - decorationSize * 0.3f
            drawDrawable(quillTreeDrawable, leftX, y, canvas, decorationSize, decorationSize)
            drawDrawable(quillTreeDrawable, rightX, y, canvas, decorationSize, decorationSize)

            if (i < pointPositions.size - 1) {
                val midX = (pos.x + pointPositions[i+1].x) / 2
                val midY = (pos.y + pointPositions[i+1].y) / 2
                drawDrawable(openBookHillDrawable, midX, midY, canvas, decorationSize * 1.2f, decorationSize * 0.8f)
            }
        }
    }

    private fun drawDrawable(drawable: Drawable?, x: Float, y: Float, canvas: Canvas, width: Float, height: Float) {
        drawable?.let {
            val left = x - width/2
            val top = y - height/2
            it.setBounds(left.toInt(), top.toInt(), (left + width).toInt(), (top + height).toInt())
            it.draw(canvas)
        }
    }

    private fun drawLamp(canvas: Canvas) {
        val lampX = width * 0.85f
        val lampY = dpToPx(60f)
        lampDrawable?.let {
            val size = lampSize
            val left = (lampX - size/2).toInt()
            val top = (lampY - size/2).toInt()
            val right = (lampX + size/2).toInt()
            val bottom = (lampY + size/2).toInt()
            it.setBounds(left, top, right, bottom)
            it.draw(canvas)
        }
    }

    private fun drawPoint(canvas: Canvas, x: Float, y: Float, quiz: Quiz, index: Int) {
        val alphaValue = (pointAlpha[index] ?: 1f) * 255
        canvas.drawCircle(x, y + dpToPx(2f), pointRadius + dpToPx(1f), pointShadowPaint)

        val gradient = getPointGradient(quiz.isCompleted)
        val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = gradient
            alpha = alphaValue.toInt()
        }
        canvas.drawCircle(x, y, pointRadius, pointPaint)

        // Книжка поверх точки
        bookPointDrawable?.let {
            val size = pointRadius * 1.2f
            val left = x - size/2
            val top = y - size/2
            it.setBounds(left.toInt(), top.toInt(), (left + size).toInt(), (top + size).toInt())
            it.alpha = alphaValue.toInt()
            it.draw(canvas)
        }

        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(100, 255, 255, 255)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(x - pointRadius / 3, y - pointRadius / 3, pointRadius / 4, highlightPaint)

        if (quiz.isUnlocked) {
            val number = quiz.id.toString()
            textPaint.textSize = pointRadius * 0.6f
            if (glowingPointIndex == index && !quiz.isCompleted) {
                glowPaint.alpha = (glowAlpha * 200).toInt()
                canvas.drawCircle(x, y, pointRadius + dpToPx(5f), glowPaint)
                textPaint.setShadowLayer(dpToPx(5f), 0f, 0f, Color.rgb(255, 215, 0))
                textPaint.color = Color.rgb(255, 200, 50)
                canvas.drawText(number, x, y + textPaint.textSize / 3, textPaint)
                textPaint.setShadowLayer(0f, 0f, 0f, 0)
                textPaint.color = Color.BLACK
            } else {
                canvas.drawText(number, x, y + textPaint.textSize / 3, textPaint)
            }
        } else {
            val lockSize = pointRadius * 0.5f
            canvas.drawRect(x - lockSize/2, y - lockSize/2, x + lockSize/2, y + lockSize/2, lockPaint)
            canvas.drawCircle(x, y - lockSize/2, lockSize/3, lockPaint)
        }
    }

    private fun drawLabel(canvas: Canvas, pos: PointF, quiz: Quiz) {
        val isLeft = pos.x < width / 2
        val labelX = if (isLeft) pos.x + pointRadius + labelOffset else pos.x - pointRadius - labelOffset
        val labelY = pos.y + labelPaint.textSize / 3

        val text = quiz.title
        val bounds = Rect()
        labelPaint.getTextBounds(text, 0, text.length, bounds)

        val bgLeft = if (isLeft) labelX - dpToPx(5f) else labelX - bounds.width() - dpToPx(5f)
        val bgTop = labelY - bounds.height() - dpToPx(3f)
        val bgRight = if (isLeft) labelX + bounds.width() + dpToPx(5f) else labelX + dpToPx(5f)
        val bgBottom = labelY + dpToPx(3f)

        canvas.drawRoundRect(bgLeft, bgTop, bgRight, bgBottom, labelCorner, labelCorner, labelBgPaint)
        labelPaint.textAlign = if (isLeft) Paint.Align.LEFT else Paint.Align.RIGHT
        canvas.drawText(text, labelX, labelY, labelPaint)

        if (quiz.isCompleted) {
            val scoreText = "✓ ${quiz.bestScore}%"
            val scorePaint = Paint(labelPaint).apply { textSize = dpToPx(10f) }
            canvas.drawText(scoreText, labelX, labelY + dpToPx(14f), scorePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            for ((index, pos) in pointPositions.withIndex()) {
                val dx = event.x - pos.x
                val dy = event.y - pos.y
                if (dx * dx + dy * dy < touchRadius * touchRadius) {
                    val quiz = quizzes[index]
                    if (quiz.isUnlocked) {
                        animatePointClick(index)
                        onQuizClickListener?.invoke(quiz)
                    } else {
                        Toast.makeText(context, "Сначала пройдите предыдущий тест", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun animatePointClick(index: Int) {
        ValueAnimator.ofFloat(1f, 1.2f, 1f).apply {
            duration = 200
            addUpdateListener { animation ->
                pointScale[index] = animation.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    pointScale.remove(index)
                    invalidate()
                }
            })
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pathAnimator?.cancel()
        glowAnimator?.cancel()
        gradientsCache.clear()
    }
}