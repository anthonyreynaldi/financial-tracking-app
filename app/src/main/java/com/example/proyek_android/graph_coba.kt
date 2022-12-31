package com.example.proyek_android

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*


class graph_coba : AppCompatActivity() {
    lateinit var pieChart: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_coba)

        val chart = findViewById<View>(R.id.chart) as BarChart
        var x: ArrayList<Any> = ArrayList()
        x.add("jan")
        x.add("feb")
        x.add("mar")
        x.add("apr")
        x.add("jun")

        var entry1: ArrayList<BarEntry> = ArrayList<BarEntry>()
        entry1.add(BarEntry(110.000f, 0f))
        entry1.add(BarEntry(140.000f, 1f))
        entry1.add(BarEntry(160.000f, 2f))
        entry1.add(BarEntry(100.000f, 3f))
        entry1.add(BarEntry(210.000f, 4f))

        var barDS1 = BarDataSet(entry1, "Brand 1")
        barDS1.setColor(Color.rgb(155, 155, 155))
        barDS1.barBorderWidth = 40f
        barDS1.valueTextSize = 15f
//        barDS1.formSize = 15f

        val data = BarData(barDS1)
        chart.data = data
        chart.animateXY(2000, 2000)

        chart.setPinchZoom(false)
        chart.setDrawBarShadow(false)
        chart.setDrawGridBackground(false)

        chart.description.isEnabled = false
        val xAxis = chart.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.spaceTop = 35f
        leftAxis.axisMinimum = 0f
        chart.axisRight.isEnabled = false
        chart.xAxis.axisMinimum = 1f
//        chart.xAxis.axisMaximum = MAX_X_VALUE.toFloat()

        val legend: Legend = chart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 11f
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)


        chart.invalidate()

//        pieChart = findViewById<View>(R.id.pieChart) as PieChart
//        pieChart.invalidate()

    }

}
