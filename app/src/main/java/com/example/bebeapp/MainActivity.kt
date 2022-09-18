package com.example.bebeapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Chronometer
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bebeapp.MainActivity.myData.attivitaList
import com.example.myapp.Main.AttivitaAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    object myData {
        //elenco con tutte le attivita
        var attivitaList: MutableList<Attivita> = mutableListOf<Attivita>()
    }

    lateinit var adapter: AttivitaAdapter
    lateinit var listView: ListView
    lateinit var sessionManager: SessionManager
    lateinit var chronometer: Chronometer

    var isWorking: Boolean = false

    // si recupera il file dove verranno salvati tutti i dati
    fun GetFile(): File {
        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir(filesDir.name, Context.MODE_PRIVATE)
        var dFile = File(directory, "data.txt")
        return dFile
    }

    // si leggono tutti i dati dal file e vengono salavati nella lista attivitaList
    fun LoadData() {
        val gson = Gson()
        val listType = object : TypeToken<MutableList<Attivita>>() {}.type
        var dFile: File = GetFile()
        if (dFile.exists()) {
            try {
                var jsonAttivitaListRead = dFile.readText()
                println("AAA LoadData reading file" + jsonAttivitaListRead)
                myData.attivitaList = gson.fromJson(jsonAttivitaListRead, listType)
            } catch (e: Exception) {
                println("AAA LoadData error whilr reading file:" + e)
                dFile.delete()
            }
        } else {
            dFile.createNewFile()
        }
    }

    // si prendono tutti i dati presenti nella lista attivitaList e vengono salvati sul file
    fun SaveData() {
        //TODO ordinare la lista per orario decrescente
        val gson = Gson()
        val jsonAttivitaListWrite: String = gson.toJson(myData.attivitaList)
        println("AAA SaveData writing file" + jsonAttivitaListWrite)
        var dFile: File = GetFile()
        dFile.delete()
        dFile.writeText(jsonAttivitaListWrite)
    }

    // si controlla se si ha il permesso di scrittura
    //TODO se non lo si ha si richiede
    fun CheckPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //se non lo si ha lo si richiede
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                150
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        CheckPermission()

        LoadData()

        val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyy HH:mm:ss")

        // si recuperano la listview dove verranno visualizzate le attivita
        listView = findViewById(R.id.main_list_view)
        // si istanzia un adapter
        adapter = AttivitaAdapter(this)
        // si usa l'adapter per associare ogni elemento della lista alla sua listview
        listView.adapter = adapter
        // si istanzia un SessionManager
        sessionManager = SessionManager(applicationContext)
        // all'inizio si suppone che il cronometro non stia funzionando
        isWorking = false

        // access the chronometer from XML file
        chronometer = findViewById<Chronometer>(R.id.c_meter)

        // se si clicca a lungo su una riga si apre l'activity dalla quale è possibile:
        // modificare l'attivita
        // eliminare l'attivita
        listView.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener {
            override fun onItemLongClick(
                arg0: AdapterView<*>?,
                arg1: View?,
                pos: Int,
                id: Long
            ): Boolean {
                println("AAA Selected pos:" + pos)
                val intent: Intent = Intent(applicationContext, EditAttivitaActivity::class.java).apply {
                    // si passa all'activity di modifica la posizione dell'elemnto nella lista
                    putExtra("POSIZIONE", pos)
                }
                startActivity(intent)
                return true
            }
        })

        try {
            // si recupera il flag dal SessionManager
            var flag: Boolean = sessionManager.getFlag()

            //https://www.youtube.com/watch?edufilter=NULL&v=B_xWjKhfBMA
            // se è true significa che il cronometro stava conteggiando quando l'applicazione è stata chiusa
            if (flag) {
                // il cronometro sta conteggiando
                isWorking = true
                // si recupera il tempo salvato quando si è chiusa l'applicazione
                var SessionManagerTime: String = sessionManager.getCurrentTime()
                // il tempo era stato salvato come stringa, lo si converte
                var retrivedDate: Date = formatter.parse(SessionManagerTime);
                // si calcola la differenza tra adesso e il tempo salvato [millisecondi]
                var mills: Long = Date().time - retrivedDate.time;
                // si imposta il cronometro con il tempo di partenza
                chronometer.setBase(SystemClock.elapsedRealtime() - mills)
                // si avvia il cronometro
                chronometer.start()
            }
        } catch (e: Exception) {
            println("AAA Error while creating" + e)
        }

        fabAllattamento.setOnClickListener { view ->
            // se il cronometro non sta funzionando si avvia
            if (!isWorking) {
                // si azzera il cronometro
                chronometer.setBase(SystemClock.elapsedRealtime())
                // si avvia il cronometro
                chronometer.start()
                // si salva l'orario di partenza
                sessionManager.setCurrentTime(formatter.format(Date()))
                // si salva il fatto che il cronometro sta conteggiando
                sessionManager.setFlag(true)
                // si imposta il bit del cronometro a true
                isWorking = true
                // si visualizza il messagio di avvio cronometro
                Snackbar.make(
                    view,
                    getResources().getString(R.string.action_inizio_allattamento),
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }
            // se il cronometro sta funzionando si ferma
            else {
                // si salva il fatto che il cronometro va fermato
                sessionManager.setFlag(false)
                // si ferma il cronometro
                chronometer.stop()
                // si salva il fatto che il conteggio è fermo
                isWorking = false
                // si calcola quanti millisecondi è durato il periodo cronometrato
                val elapsedMillis: Long = SystemClock.elapsedRealtime() - chronometer.getBase()
                // dai millisecondi si calcolano i minuti
                val minutes = elapsedMillis / 1000 / 60
                // si visualizza il messagio di stop del cronometro
                Snackbar.make(
                    view,
                    getResources().getString(R.string.action_fine_allattamento) + minutes + getResources().getString(
                        R.string.minuti
                    ),
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
                // si recupera tempo in questo istante
                var now: LocalDateTime = LocalDateTime.now()
                // si calcola il momento in cui è stato avviato il cronometro
                var start: LocalDateTime = now.minusMinutes(minutes)
                // si aggiunge una nuova attivita
                myData.attivitaList.add(
                    0,
                    Attivita(
                        TipologiaAttivita.ALLATTAMENTO,
                        start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        start.format(DateTimeFormatter.ofPattern("HH:mm")),
                        now.format(DateTimeFormatter.ofPattern("HH:mm")),
                        minutes
                    )
                )
                // finally, notify the adapter for data set changed
                adapter.notifyDataSetChanged()
                // si azzera il cronometro
                chronometer.setBase(SystemClock.elapsedRealtime());
                // si salvano le attivita
                SaveData()
            }
        }

        fabBagno.setOnClickListener { view ->
            var now: LocalDateTime = LocalDateTime.now()
            myData.attivitaList.add(
                0,
                Attivita(
                    TipologiaAttivita.BAGNO,
                    now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    now.format(DateTimeFormatter.ofPattern("HH:mm")),
                    "",
                    0
                )
            )
            // finally, notify the adapter for data set changed
            adapter.notifyDataSetChanged()
            SaveData()
        }

        fabCambio.setOnClickListener { view ->
            var now: LocalDateTime = LocalDateTime.now()
            myData.attivitaList.add(
                0,
                Attivita(
                    TipologiaAttivita.CAMBIO,
                    now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    now.format(DateTimeFormatter.ofPattern("HH:mm")),
                    "",
                    0
                )
            )
            // finally, notify the adapter for data set changed
            adapter.notifyDataSetChanged()
            SaveData()
        }

    }

    fun resetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.reset)
            .setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    // si salva il fatto che il cronometro va fermato
                    sessionManager.setFlag(false)
                    // si ferma il cronometro
                    chronometer.stop()
                    // si salva il fatto che il conteggio è fermo
                    isWorking = false
                    // si azzera la lista
                    attivitaList = mutableListOf<Attivita>()
                    // si aggiorna la visualizzazione della lista
                    adapter.notifyDataSetChanged()
                    // si salvano i dati
                    SaveData()
                })
            .setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialog, id ->
                })
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    //se non viene fornito il permesso di scrittura allora viene chiesto nuovamente
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            150 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_reset -> {
                resetDialog()
            }
            //R.id.action_settings -> true
        }

        return super.onOptionsItemSelected(item)

    }
}
