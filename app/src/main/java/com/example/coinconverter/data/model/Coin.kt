package com.example.coinconverter.data.model

import java.util.*

enum class Coin(val locale: Locale) {
    USD(Locale.US),
    CAD(Locale.CANADA),
    BRL(Locale("pt","BR")),
    EUR(Locale.GERMANY),
    ARS(Locale("es","AR"));

    companion object{
        fun getByName(name:String) = values().find { it.name == name } ?: BRL
    }
}