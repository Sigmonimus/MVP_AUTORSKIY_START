package com.example.mvp_autorskiy_start.ui.test

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Quiz
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PathMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var quizzes: List<Quiz> = emptyList()
    private var pointPositions = mutableListOf<PointF>()
    private val fullPath = Path()
    private var clipPath = Path()
    private var pathProgress = 0f
    private var pathAnimator: ValueAnimator? = null

    private var startY = 200f
    private var verticalSpacing = 280f
    private var horizontalAmplitude = 150f
    private var pointRadius = 48f
    private val touchRadius = 80f

    // Paints
    private val paintPath = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        color = Color.argb(200, 160, 100, 60)
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(20f, 15f), 0f)
    }
    private val paintPathShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 14f
        color = Color.argb(80, 0, 0, 0)
        strokeCap = Paint.Cap.ROUND
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

    // Glow paint (pulsing circle)
    private val paintGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(100, 255, 215, 0)
        style = Paint.Style.STROKE
        strokeWidth = 8f
        setShadowLayer(16f, 0f, 0f, Color.argb(200, 255, 215, 0))
    }
    private var glowAnimator: ValueAnimator? = null
    private var glowAlpha = 0f
    private var glowingPointIndex = -1

    // Background color animation (day/night cycle)
    private val bgColorStart = Color.parseColor("#F5E6D3")  // тёплый
    private val bgColorEnd = Color.parseColor("#E8DDC0")    // прохладный
    private var currentBgColor = bgColorStart
    private val bgAnimator = ValueAnimator.ofArgb(bgColorStart, bgColorEnd).apply {
        duration = 180000 // 3 минуты
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        interpolator = LinearInterpolator()
        addUpdateListener {
            currentBgColor = it.animatedValue as Int
            invalidate()
        }
    }

    // Background parchment paint (color will be set each frame)
    private val parchmentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val grainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(30, 100, 70, 30)
        style = Paint.Style.FILL
    }

    // Decorations drawables
    private var bookPointDrawable: Drawable? = null
    private var quillTreeDrawable: Drawable? = null
    private var openBookHillDrawable: Drawable? = null
    private var lampDrawable: Drawable? = null

    // Lamp flicker
    private var lampAlpha = 1f
    private val lampFlickerAnim = ValueAnimator.ofFloat(0.7f, 1f).apply {
        duration = 3000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener {
            lampAlpha = it.animatedValue as Float
            invalidate()
        }
    }

    // Floating feathers
    private data class Feather(var x: Float, var y: Float, var alpha: Float, var vx: Float, var vy: Float)
    private val feathers = mutableListOf<Feather>()
    private var featherAnimator: ValueAnimator? = null

    // Golden sparkles on tap
    private data class Sparkle(var x: Float, var y: Float, var vx: Float, var vy: Float, var life: Float)
    private val sparkles = mutableListOf<Sparkle>()
    private var sparkleAnimator: ValueAnimator? = null

    private var onQuizClickListener: ((Quiz) -> Unit)? = null

    init {
        bookPointDrawable = ContextCompat.getDrawable(context, R.drawable.ic_book_point)
        quillTreeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_quill_tree)
        openBookHillDrawable = ContextCompat.getDrawable(context, R.drawable.ic_open_book_hill)
        lampDrawable = ContextCompat.getDrawable(context, R.drawable.ic_lamp_knowledge)

        lampFlickerAnim.start()
        bgAnimator.start()
        startFeatherAnimation()
    }

    private fun startFeatherAnimation() {
        val width = width.toFloat()
        val height = height.toFloat()
        for (i in 0..4) {
            val x = Random.Default.nextFloat() * width
            val y = Random.Default.nextFloat() * height
            val alpha = 0.3f + Random.Default.nextFloat() * 0.5f
            val vx = (Random.Default.nextFloat() - 0.5f) * 0.3f
            val vy = (Random.Default.nextFloat() - 0.5f) * 0.3f
            feathers.add(Feather(x, y, alpha, vx, vy))
        }

        featherAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 30000 // 30 seconds cycle
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                updateFeathers()
                invalidate()
            }
            start()
        }
    }

    private fun updateFeathers() {
        val width = width.toFloat()
        val height = height.toFloat()
        for (f in feathers) {
            f.x += f.vx
            f.y += f.vy
            if (f.x < -50f) f.x = width + 50f
            if (f.x > width + 50f) f.x = -50f
            if (f.y < -50f) f.y = height + 50f
            if (f.y > height + 50f) f.y = -50f
            f.alpha += (Random.Default.nextFloat() - 0.5f) * 0.02f
            f.alpha = f.alpha.coerceIn(0.2f, 0.8f)
        }
    }

    // Sparkle methods
    private fun startSparkleAnimation() {
        if (sparkleAnimator?.isRunning == true) return
        sparkleAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 40 // update every 40 ms
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateSparkles()
                invalidate()
            }
            start()
        }
    }

    private fun stopSparkleAnimation() {
        sparkleAnimator?.cancel()
        sparkleAnimator = null
    }

    private fun updateSparkles() {
        sparkles.removeAll { it.life <= 0f }
        for (s in sparkles) {
            s.x += s.vx
            s.y += s.vy
            s.life -= 0.05f
        }
        if (sparkles.isEmpty()) {
            stopSparkleAnimation()
        }
    }

    private fun addSparkles(centerX: Float, centerY: Float) {
        val count = 8 + Random.Default.nextInt(5) // 8–12 sparkles
        repeat(count) {
            val angle = Random.Default.nextDouble() * 2 * PI
            val speed = 2f + Random.Default.nextFloat() * 3f
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed).toFloat()
            val life = 0.8f + Random.Default.nextFloat() * 0.6f
            sparkles.add(Sparkle(centerX, centerY, vx, vy, life))
        }
        startSparkleAnimation()
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
            val tan = FloatArray(2)
            measure.getPosTan(stop, pos, tan)
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
            interpolator = LinearInterpolator()
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

        // 1. Parchment background with animated color
        parchmentPaint.color = currentBgColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), parchmentPaint)

        // 2. Subtle grain texture (static)
        val random = java.util.Random(System.currentTimeMillis())
        for (i in 0 until 150) {
            val x = random.nextInt(width)
            val y = random.nextInt(height)
            grainPaint.color = Color.argb(20 + random.nextInt(30), 80, 60, 30)
            canvas.drawCircle(x.toFloat(), y.toFloat(), 1.5f, grainPaint)
        }

        // 3. Decorations (quills, open books) behind path
        drawDecorations(canvas)

        // 4. Path with shadow and animated drawing
        canvas.drawPath(fullPath, paintPathShadow)
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawPath(fullPath, paintPath)
        canvas.restore()

        // 5. Points (books) and labels
        for ((index, pos) in pointPositions.withIndex()) {
            drawPoint(canvas, pos, quizzes[index], index)
            drawLabel(canvas, pos, quizzes[index])
        }

        // 6. Floating feathers (very slow drift)
        for (feather in feathers) {
            val alpha = (feather.alpha * 150).toInt()
            val paintFeather = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(alpha, 180, 140, 100)
                style = Paint.Style.FILL
            }
            val path = Path()
            path.moveTo(feather.x, feather.y)
            path.cubicTo(feather.x + 10f, feather.y - 8f, feather.x + 5f, feather.y - 12f, feather.x, feather.y - 10f)
            path.lineTo(feather.x - 5f, feather.y - 5f)
            canvas.drawPath(path, paintFeather)
            canvas.drawCircle(feather.x, feather.y, 2f, paintFeather)
        }

        // 7. Golden sparkles on tap
        for (s in sparkles) {
            val alpha = (s.life * 200).toInt().coerceIn(0, 255)
            val paintSparkle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(alpha, 255, 215, 0)
                style = Paint.Style.FILL
            }
            val radius = 3f * s.life
            canvas.drawCircle(s.x, s.y, radius, paintSparkle)
        }

        // 8. Lamp of knowledge (with flicker)
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
            it.alpha = (lampAlpha * 255).toInt()
            it.draw(canvas)
        }
        val paintRay = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(80, 255, 215, 0)
            strokeWidth = 4f
        }
        for (i in 0..11) {
            val angle = i * PI * 2 / 12
            val dx = cos(angle).toFloat() * 70f
            val dy = sin(angle).toFloat() * 70f
            canvas.drawLine(lampX, lampY, lampX + dx, lampY + dy, paintRay)
        }
    }

    private fun drawPoint(canvas: Canvas, pos: PointF, quiz: Quiz, index: Int) {
        canvas.drawCircle(pos.x, pos.y + 4f, pointRadius + 2f, paintShadow)

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
                // Pulsing glow ring
                paintGlow.alpha = (glowAlpha * 200).toInt()
                canvas.drawCircle(pos.x, pos.y, pointRadius + 12f, paintGlow)

                // Yellow number with shadow
                paintText.setShadowLayer(12f, 0f, 0f, Color.rgb(255, 215, 0))
                paintText.color = Color.rgb(255, 200, 50)
                canvas.drawText(number, pos.x, pos.y + paintText.textSize / 3, paintText)
                paintText.setShadowLayer(0f, 0f, 0f, 0)
                paintText.color = Color.BLACK
            } else {
                paintText.setShadowLayer(0f, 0f, 0f, 0)
                paintText.color = Color.BLACK
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
                        addSparkles(pos.x, pos.y)   // золотые искры
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
        glowAnimator?.cancel()
        pathAnimator?.cancel()
        lampFlickerAnim.cancel()
        featherAnimator?.cancel()
        bgAnimator.cancel()
        stopSparkleAnimation()
    }
}