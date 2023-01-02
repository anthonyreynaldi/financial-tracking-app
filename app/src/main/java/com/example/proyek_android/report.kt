package com.example.proyek_android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyek_android.Classes.Pemasukkan
import com.example.proyek_android.Classes.Pengeluaran
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*


class report : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var arPengeluaran = IntArray(12)
        var arPemasukkan = IntArray(12)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        var sdf = SimpleDateFormat("M")
        val curMonth = sdf.format(Date()).toString().toInt()
        sdf = SimpleDateFormat("yyyy")
        val curYear = sdf.format(Date()).toString().toInt() - 1

        var tv_judul = findViewById<TextView>(R.id.tv_report_judul)
        tv_judul.setText("REPORT TAHUN "+curYear)

        val btn_back = findViewById<ImageView>(R.id.btn_back9)
        btn_back.setOnClickListener{
            startActivity(Intent(this, homepage::class.java))
        }

        for (item in homepage.user.listPengeluaran) {
            val data = Pengeluaran(
                item.nama,
                item.nominal,
                item.kategori,
                item.sumberDana,
                item.tanggal,
                "Pengeluaran"
            )
            var tanggal = data.tanggal.toString().split(' ')
            if(tanggal[3].toInt() == curYear){
                arPengeluaran[getMonth(tanggal[2]) - 1] = arPengeluaran[getMonth(tanggal[2]) - 1] + item.nominal
            }
        }
        println("peng"+Arrays.toString(arPengeluaran))

        for (item in homepage.user.listPemasukkan) {
            val dataM = Pemasukkan(
                item.nama,
                item.nominal,
                item.kategori,
                item.sumberDana,
                item.tanggal,
                "Pemasukkan"
            )
            var tanggal = dataM.tanggal.toString().split(' ')
            if(tanggal[3].toInt() == curYear){
                arPemasukkan[getMonth(tanggal[2]) - 1] = arPemasukkan[getMonth(tanggal[2]) - 1] + item.nominal
            }
        }
        println("pem"+Arrays.toString(arPemasukkan))


        val pemasukkan2 = ArrayList<Entry>()
        pemasukkan2.add(Entry(0F, arPemasukkan[0].toFloat()/1000000))
        pemasukkan2.add(Entry(1F, arPemasukkan[1].toFloat()/1000000))
        pemasukkan2.add(Entry(2F, arPemasukkan[2].toFloat()/1000000))
        pemasukkan2.add(Entry(3F, arPemasukkan[3].toFloat()/1000000))
        pemasukkan2.add(Entry(4F, arPemasukkan[4].toFloat()/1000000))
        pemasukkan2.add(Entry(5F, arPemasukkan[5].toFloat()/1000000))
        pemasukkan2.add(Entry(6F, arPemasukkan[6].toFloat()/1000000))
        pemasukkan2.add(Entry(7F, arPemasukkan[7].toFloat()/1000000))
        pemasukkan2.add(Entry(8F, arPemasukkan[8].toFloat()/1000000))
        pemasukkan2.add(Entry(9F, arPemasukkan[9].toFloat()/1000000))
        pemasukkan2.add(Entry(10F, arPemasukkan[10].toFloat()/1000000))
        pemasukkan2.add(Entry(11F, arPemasukkan[11].toFloat()/1000000))


        val pengeluaran2 = ArrayList<Entry>()
        pengeluaran2.add(Entry(0F, arPengeluaran[0].toFloat()/1000000))
        pengeluaran2.add(Entry(1F, arPengeluaran[1].toFloat()/1000000))
        pengeluaran2.add(Entry(2F, arPengeluaran[2].toFloat()/1000000))
        pengeluaran2.add(Entry(3F, arPengeluaran[3].toFloat()/1000000))
        pengeluaran2.add(Entry(4F, arPengeluaran[4].toFloat()/1000000))
        pengeluaran2.add(Entry(5F, arPengeluaran[5].toFloat()/1000000))
        pengeluaran2.add(Entry(6F, arPengeluaran[6].toFloat()/1000000))
        pengeluaran2.add(Entry(7F, arPengeluaran[7].toFloat()/1000000))
        pengeluaran2.add(Entry(8F, arPengeluaran[8].toFloat()/1000000))
        pengeluaran2.add(Entry(9F, arPengeluaran[9].toFloat()/1000000))
        pengeluaran2.add(Entry(10F, arPengeluaran[10].toFloat()/1000000))
        pengeluaran2.add(Entry(11F, arPengeluaran[11].toFloat()/1000000))


        val pemasukkanLineDataSet = LineDataSet(pemasukkan2, "Pemasukkan")
        pemasukkanLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        pemasukkanLineDataSet.color = Color.parseColor("#58B2F6")
        pemasukkanLineDataSet.circleRadius = 3f
        pemasukkanLineDataSet.setCircleColor(Color.parseColor("#58B2F6"))

        val pengeluaranLineDataSet = LineDataSet(pengeluaran2, "Pengeluaran")
        pengeluaranLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        pengeluaranLineDataSet.color = Color.parseColor("#FD9118")
        pengeluaranLineDataSet.circleRadius = 3f
        pengeluaranLineDataSet.setCircleColor(Color.parseColor("#FD9118"))

        //Setup Legend
        val lineChart = findViewById<View>(R.id.lineChart) as LineChart
        val legend2 = lineChart.legend
        legend2.isEnabled = true
        legend2.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
        legend2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)
        legend2.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend2.setDrawInside(false)

        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.data = LineData(pemasukkanLineDataSet, pengeluaranLineDataSet)
        lineChart.animateXY(100, 500)
        val yAxisRight2: YAxis = lineChart.getAxisRight()
        yAxisRight2.isEnabled = false

        lineChart.getAxisLeft().setValueFormatter(IAxisValueFormatter { value, axis ->
            "" + value.toInt() + "jt" // yVal is a string array
        })

        val xLabel: ArrayList<String> = ArrayList()
        xLabel.add("Jan")
        xLabel.add("Feb")
        xLabel.add("Mar")
        xLabel.add("Apr")
        xLabel.add("May")
        xLabel.add("Jun")
        xLabel.add("Jul")
        xLabel.add("Aug")
        xLabel.add("Sep")
        xLabel.add("Oct")
        xLabel.add("Nov")
        xLabel.add("Dec")


        val xAxis: XAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter =
            IAxisValueFormatter { value, axis -> xLabel.get(value.toInt()) }
    }

    fun getMonth(month: String): Int{
        if(month == "January"){
            return 1
        }else if(month == "February"){
            return 2
        }else if(month == "March"){
            return 3
        }else if(month == "April"){
            return 4
        }else if(month == "May"){
            return 5
        }else if(month == "June"){
            return 6
        }else if(month == "July"){
            return 7
        }else if(month == "August"){
            return 8
        }else if(month == "September"){
            return 9
        }else if(month == "October"){
            return 10
        }else if(month == "November"){
            return 11
        }else if(month == "December"){
            return 12
        }
        return 0
    }
}