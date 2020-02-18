package com.example.app


val storeChoices = listOf(
    "-",
    "Carrefour",
    "Monoprix",
    "Franprix",
    "Leclerc",
    "Auchan"
)

val typeChoices = mutableListOf(
    "-",
    "Vodka",
    "Tequila",
    "Rhum",
    "Vin",
    "Bière"
)

fun getYearChoices(): MutableList<String>
{
    val yearChoicesInt = (1900..2020).toMutableList()
    val yearChoices = yearChoicesInt.map { it.toString() }.toMutableList()
    yearChoices.add("-")
    return yearChoices
}
