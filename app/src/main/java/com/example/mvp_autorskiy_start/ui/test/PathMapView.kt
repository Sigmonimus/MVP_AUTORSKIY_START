package com.example.mvp_autorskiy_start.ui.test

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Quiz
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PathMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var quizzes: List<Quiz> = emptyList()
    private val pointPositions = mutableListOf<PointF>()
    private val fullPath = Path()
    private var clipPath = Path()
    private var pathProgress = 0f
    private var pathAnimator: ValueAnimator? = null

    private var startY = 200f
    private var verticalSpacing = 280f
    private var horizontalAmplitude = 150f
    private var pointRadius = 48f
    private val touchRadius = 80f

    private val paintPath = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        color = Color.argb(200, 160, 100, 60)
        strokeCap = Paint.Cap.ROUND
    }
    private val paintPathShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 14f
        color = Color.argb(80, 0, 0, 0)
        setShadowLayer(8f, 0f, 3f, Color.argb(100, 0, 0, 0))
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 28f
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val paintLabel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 28f
        color = Color.argb(255, 80, 50, 20)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val paintLabelBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 245, 240, 220)
    }
    private val paintLock = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GRAY }
    private val paintShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(80, 0, 0, 0)
        setShadowLayer(12f, 0f, 4f, Color.argb(120, 0, 0, 0))
    }
    private val paintPointBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F5E6D3")
    }
    private val paintGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 255, 215, 0)
        style = Paint.Style.STROKE
        strokeWidth = 8f
        setShadowLayer(16f, 0f, 0f, Color.argb(200, 255, 215, 0))
    }
    private var glowAnimator: ValueAnimator? = null
    private var glowAlpha = 0f
    private var glowingPointIndex = -1

    private var bookPointDrawable: Drawable? = null
    private var quillTreeDrawable: Drawable? = null
    private var openBookHillDrawable: Drawable? = null
    private var lampDrawable: Drawable? = null

    private var onQuizClickListener: ((Quiz) -> Unit)? = null

    init {
        bookPointDrawable = ContextCompat.getDrawable(context, R.drawable.ic_book_point)
        quillTreeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_quill_tree)
        openBookHillDrawable = ContextCompat.getDrawable(context, R.drawable.ic_open_book_hill)
        lampDrawable = ContextCompat.getDrawable(context, R.drawable.ic_lamp_knowledge)
    }

    fun setQuizzes(quizzes: List<Quiz>) {
        this.quizzes = quizzes
        computePositions()
        startPathAnimation()
        findAndGlowNextPoint()
        invalidate()
    }

    fun setOnQuizClickListener(listener: (Quiz) -> Unit) {
        onQuizClickListener = listener
    }

    fun getPointY(index: Int): Float = pointPositions.getOrNull(index)?.y ?: 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computePositions()
    }

    private fun computePositions() {
        pointPositions.clear()
        if (quizzes.isEmpty()) return
        val width = width.toFloat()
        val centerX = width / 2
        for (i in quizzes.indices) {
            val phase = i * PI / 2
            val offsetX = (sin(phase) * horizontalAmplitude).toFloat()
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
            duration = 1500
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                pathProgress = it.animatedValue as Float
                updateClipPath()
                invalidate()
            }
            start()
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
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                glowAlpha = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = if (quizzes.isNotEmpty()) {
            (startY + (quizzes.size - 1) * verticalSpacing + 300).toInt()
        } else {
            suggestedMinimumHeight
        }
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        val width = resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(width, height)
        computePositions()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (quizzes.isEmpty() || pointPositions.size != quizzes.size) return

        canvas.drawColor(Color.parseColor("#F5E6D3"))
        drawDecorations(canvas)
        canvas.drawPath(fullPath, paintPathShadow)
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawPath(fullPath, paintPath)
        canvas.restore()
        for ((index, pos) in pointPositions.withIndex()) {
            drawPoint(canvas, pos, quizzes[index], index)
            drawLabel(canvas, pos, quizzes[index])
        }

        drawLamp(canvas)
    }

    private fun drawDecorations(canvas: Canvas) {
        for ((i, pos) in pointPositions.withIndex()) {
            val leftX = pos.x - 150f
            val rightX = pos.x + 150f
            val y = pos.y - 10f
            drawDrawable(quillTreeDrawable, leftX, y, canvas, 80f, 80f)
            drawDrawable(quillTreeDrawable, rightX, y, canvas, 80f, 80f)

            if (i < pointPositions.size - 1) {
                val midX = (pos.x + pointPositions[i+1].x) / 2
                val midY = (pos.y + pointPositions[i+1].y) / 2
                drawDrawable(openBookHillDrawable, midX, midY, canvas, 100f, 70f)
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
        val lampY = 100f
        lampDrawable?.let {
            val size = 90f
            val left = (lampX - size/2).toInt()
            val top = (lampY - size/2).toInt()
            val right = (lampX + size/2).toInt()
            val bottom = (lampY + size/2).toInt()
            it.setBounds(left, top, right, bottom)
            it.draw(canvas)
        }
    }

    private fun drawPoint(canvas: Canvas, pos: PointF, quiz: Quiz, index: Int) {
        canvas.drawCircle(pos.x, pos.y + 4f, pointRadius + 2f, paintShadow)
        canvas.drawCircle(pos.x, pos.y, pointRadius, paintPointBg)

        bookPointDrawable?.let {
            val size = pointRadius * 1.5f
            val left = pos.x - size/2
            val top = pos.y - size/2
            it.setBounds(left.toInt(), top.toInt(), (left + size).toInt(), (top + size).toInt())
            it.draw(canvas)
        }

        if (quiz.isUnlocked) {
            val number = quiz.id.toString()
            paintText.textSize = pointRadius * 0.6f

            if (glowingPointIndex == index && !quiz.isCompleted) {
                paintGlow.alpha = (glowAlpha * 200).toInt()
                canvas.drawCircle(pos.x, pos.y, pointRadius + 12f, paintGlow)
                paintText.setShadowLayer(12f, 0f, 0f, Color.rgb(255, 215, 0))
                paintText.color = Color.rgb(255, 200, 50)
                canvas.drawText(number, pos.x, pos.y + paintText.textSize / 3, paintText)
                paintText.setShadowLayer(0f, 0f, 0f, 0)
                paintText.color = Color.BLACK
            } else {
                canvas.drawText(number, pos.x, pos.y + paintText.textSize / 3, paintText)
            }
        } else {
            val lockSize = pointRadius * 0.5f
            canvas.drawRect(pos.x - lockSize/2, pos.y - lockSize/2, pos.x + lockSize/2, pos.y + lockSize/2, paintLock)
            canvas.drawCircle(pos.x, pos.y - lockSize/2, lockSize/3, paintLock)
        }
    }

    private fun drawLabel(canvas: Canvas, pos: PointF, quiz: Quiz) {
        val isLeft = pos.x < width / 2
        val labelX = if (isLeft) pos.x + pointRadius + 20 else pos.x - pointRadius - 20
        val labelY = pos.y + paintLabel.textSize / 3

        val text = quiz.title
        val bounds = Rect()
        paintLabel.getTextBounds(text, 0, text.length, bounds)

        val bgLeft = if (isLeft) labelX - 8f else labelX - bounds.width() - 8f
        val bgTop = labelY - bounds.height() - 8f
        val bgRight = if (isLeft) labelX + bounds.width() + 8f else labelX + 8f
        val bgBottom = labelY + 8f

        canvas.drawRoundRect(bgLeft, bgTop, bgRight, bgBottom, 12f, 12f, paintLabelBg)

        paintLabel.textAlign = if (isLeft) Paint.Align.LEFT else Paint.Align.RIGHT
        canvas.drawText(text, labelX, labelY, paintLabel)

        if (quiz.isCompleted) {
            val scoreText = "✓ ${quiz.bestScore}%"
            val scorePaint = Paint(paintLabel).apply { textSize = 22f }
            canvas.drawText(scoreText, labelX, labelY + 28, scorePaint)
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pathAnimator?.cancel()
        glowAnimator?.cancel()
    }
}