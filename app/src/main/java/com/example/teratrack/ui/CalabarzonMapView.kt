package com.example.teratrack.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import location.Location
import vehicle.Vehicle
import warehouse.Warehouse

/** Schematic CALABARZON plot using the project's stored latitude/longitude values. */
class CalabarzonMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var warehouses: List<Warehouse> = emptyList()
    private var vehicles: List<Vehicle> = emptyList()

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFF4F8F7.toInt() }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFDDE8E4.toInt(); strokeWidth = dp(1f)
    }
    private val landPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFE3EEE8.toInt(); style = Paint.Style.FILL
    }
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFB9CEC4.toInt(); style = Paint.Style.STROKE; strokeWidth = dp(1.5f)
    }
    private val warehousePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF138A7E.toInt() }
    private val vehiclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFE18B35.toInt() }
    private val provincePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF6F8980.toInt(); textSize = dp(9f)
        typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
    }
    private val captionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF526D63.toInt(); textSize = dp(9f)
        typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
    }
    private val bounds = RectF()

    fun setData(warehouses: List<Warehouse>, vehicles: List<Vehicle>) {
        this.warehouses = warehouses
        this.vehicles = vehicles
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val widthF = width.toFloat()
        val heightF = height.toFloat()
        canvas.drawRoundRect(0f, 0f, widthF, heightF, dp(20f), dp(20f), backgroundPaint)

        val pad = dp(22f)
        bounds.set(pad, pad, widthF - pad, heightF - dp(28f))
        for (i in 1..4) {
            val x = bounds.left + bounds.width() * i / 5f
            val y = bounds.top + bounds.height() * i / 5f
            canvas.drawLine(x, bounds.top, x, bounds.bottom, gridPaint)
            canvas.drawLine(bounds.left, y, bounds.right, y, gridPaint)
        }

        // A restrained schematic land shape; marker positions use geographic coordinates.
        val region = Path().apply {
            moveTo(px(.08f), py(.13f)); lineTo(px(.31f), py(.04f))
            lineTo(px(.52f), py(.12f)); lineTo(px(.78f), py(.18f))
            lineTo(px(.93f), py(.39f)); lineTo(px(.84f), py(.59f))
            lineTo(px(.68f), py(.79f)); lineTo(px(.50f), py(.93f))
            lineTo(px(.28f), py(.84f)); lineTo(px(.16f), py(.65f))
            lineTo(px(.05f), py(.39f)); close()
        }
        canvas.drawPath(region, landPaint)
        canvas.drawPath(region, outlinePaint)

        canvas.drawText("CAVITE", px(.10f), py(.32f), provincePaint)
        canvas.drawText("LAGUNA", px(.39f), py(.43f), provincePaint)
        canvas.drawText("RIZAL", px(.70f), py(.28f), provincePaint)
        canvas.drawText("BATANGAS", px(.20f), py(.76f), provincePaint)
        canvas.drawText("QUEZON", px(.69f), py(.75f), provincePaint)

        warehouses.forEach { drawMarker(canvas, it.location, warehousePaint) }
        vehicles.forEach { drawMarker(canvas, it.currentLocation, vehiclePaint) }
        canvas.drawText("CALABARZON  ·  COORDINATE VIEW", pad, heightF - dp(10f), captionPaint)
    }

    private fun drawMarker(canvas: Canvas, location: Location, paint: Paint) {
        val x = projectX(location.longitude)
        val y = projectY(location.latitude)
        canvas.drawCircle(x, y, dp(8f), Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFFFFF.toInt() })
        canvas.drawCircle(x, y, dp(5f), paint)
    }

    // Approximate regional frame: longitude 120.0–123.0, latitude 13.0–15.2.
    private fun projectX(longitude: Double): Float =
        bounds.left + (((longitude - 120.0) / 3.0).toFloat().coerceIn(0f, 1f) * bounds.width())

    private fun projectY(latitude: Double): Float =
        bounds.bottom - (((latitude - 13.0) / 2.2).toFloat().coerceIn(0f, 1f) * bounds.height())

    private fun px(fraction: Float): Float = bounds.left + bounds.width() * fraction
    private fun py(fraction: Float): Float = bounds.top + bounds.height() * fraction
    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}
