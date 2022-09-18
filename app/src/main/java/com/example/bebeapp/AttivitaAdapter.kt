package com.example.myapp.Main

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.example.bebeapp.Attivita
import com.example.bebeapp.MainActivity
import com.example.bebeapp.R
import com.example.bebeapp.TipologiaAttivita

class AttivitaAdapter(private val context: Context) : BaseAdapter(){

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // Get view for row item
        val rowView = inflater.inflate(R.layout.listview_item_attivita, parent, false)

        // Get elements
        val tGiorno = rowView.findViewById(R.id.lgiorno) as TextView
        val tInizio = rowView.findViewById(R.id.linizio) as TextView
        val tFine = rowView.findViewById(R.id.lfine) as TextView
        val tDurata = rowView.findViewById(R.id.ldurata) as TextView
        val tTipo = rowView.findViewById(R.id.ltipo) as TextView
        val img = rowView.findViewById(R.id.image) as AppCompatImageView

        // 1
        val c = getItem(position) as Attivita

        // 2

        tGiorno.text = c.giorno
        tInizio.text = c.inizio
        tFine.text = c.fine
        tTipo.text = c.tipo.toString()

        if(c.tipo == TipologiaAttivita.ALLATTAMENTO){
            img.setImageResource(R.drawable.baseline_local_dining_24)
            tDurata.text = "[ " + c.durata + "minuti ]"
        }else if(c.tipo == TipologiaAttivita.CAMBIO){
            img.setImageResource(R.drawable.baseline_baby_changing_station_24)
        }else if(c.tipo == TipologiaAttivita.BAGNO){
            img.setImageResource(R.drawable.baseline_bathtub_24)
        }

        //https://www.raywenderlich.com/155-android-listview-tutorial-with-kotlin

        return  rowView
    }

    override fun getItem(position: Int): Any {
        return MainActivity.myData.attivitaList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return MainActivity.myData.attivitaList.size
    }

}