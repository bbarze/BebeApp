package com.example.bebeapp

import java.io.Serializable
import java.sql.Time
import java.time.LocalDateTime
import java.util.*

enum class TipologiaAttivita {
    ALLATTAMENTO, BAGNO, CAMBIO //TODO, CACCA
}

class Attivita(
    var tipo : TipologiaAttivita,
    var giorno : String,
    var inizio : String,
    var fine : String,
    var durata: Long
    //TODO var note : String
)