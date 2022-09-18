package com.example.bebeapp

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.transition.Visibility
import kotlinx.android.synthetic.main.activity_edit_attivita.*
import java.time.LocalDate
import java.time.LocalDateTime


class EditAttivitaActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_attivita)

        // Get the Intent that started this activity and extract the string
        var pos : Int = intent.getIntExtra("POSIZIONE", 0);
        println("AAA Retrived pos:" + pos)

        // si recupera l'attivita selezionata
        var selectedA : Attivita = MainActivity.myData.attivitaList[pos];

        // si valorizza il giorno
        var day : Int = selectedA.giorno.substring(0, 2).toInt();
        var month : Int = selectedA.giorno.substring(3, 5).toInt();
        var year : Int = selectedA.giorno.substring(6, 10).toInt();
        dp.init(year,month,day, null);

        // si valorizza l'orario di inizio
        var hour : Int = selectedA.inizio.substring(0, 2).toInt();
        tp.hour = hour;
        var min : Int = selectedA.inizio.substring(3, 5).toInt();
        tp.minute = min;
        tp.setIs24HourView(true);

        //TODO solo per allattamento
        // si recupera la textbox dove viene visualizzata la durata
        var eDurata: EditText = findViewById(R.id.etDurata);
        // si valorizza la durata
        //TODO eDurata.setText(e)

        if(selectedA.tipo == TipologiaAttivita.ALLATTAMENTO){
            btnEat.isChecked = true;
            llDurata.isVisible = true;
            //TODO eDurata.setText("15");
        }else if(selectedA.tipo == TipologiaAttivita.BAGNO) {
            btnBath.isChecked = true;
            llDurata.isVisible = false;
        }else{
            btnChange.isChecked = true;
            llDurata.isVisible = false;
        }

        btnSave.setOnClickListener { view ->
            //TODO, implementare anche altri pulsanti
        }
    }

    fun radio_button_bathclick(view: View) {
        llDurata.isVisible = true;
    }

    fun radio_button_click(view: View) {
        llDurata.isVisible = false;
    }

    //TODO  Controllare se i dati presenti nel layout sono validi
    //fun DataOk() : Boolean{
    //}
}
