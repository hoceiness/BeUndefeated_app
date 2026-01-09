package com.example.beundefeatedapp.data

import com.example.beundefeatedapp.R
import kotlin.random.Random

data class Player(val nickname: String, val winRate: String, val totalMatches: Int, val isOnline: Boolean, val avatarResId: Int)

data class Match(
    val id: Int,
    val type: String, // "5v5", "7v7", "Panna", "Futsal"
    val status: String, // "Open", "Full", "Private"
    val city: String,
    val field: String,
    val time: String, // "09:00 - 10:00", etc.
    val timeCategory: String, // "Morning", "Afternoon", "Evening"
    val day: String, // "Today", "Tomorrow", "History"
    val price: Int,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val scoreA: Int? = null,
    val scoreB: Int? = null,
    val teamALogoResId: Int,
    val teamBLogoResId: Int
)

object MockMatchData {
    val playerAvatars = listOf(
        R.drawable.player1, R.drawable.player2, R.drawable.player3,
        R.drawable.player4, R.drawable.player5, R.drawable.player6,
        R.drawable.player7, R.drawable.player8, R.drawable.player9
    )

    val teamLogos = listOf(
        R.drawable.team1, R.drawable.team2, R.drawable.team3,
        R.drawable.team4, R.drawable.team5
    )

    val cities = listOf(
        "Casablanca", "Rabat", "Marrakech", "Fes", "Tangier", "Agadir", "Meknes", "Oujda", "Kenitra", "Tetouan",
        "Safi", "Mohammedia", "Beni Mellal", "El Jadida", "Taza", "Nador", "Settat", "Larache", "Khemisset", "Guelmim"
    )

    val fieldsByCity: Map<String, List<String>> = mapOf(
        "Casablanca" to listOf("Stade Mohammed V Annex", "Oasis Sports City", "City Foot 5", "Green Sports Park", "Urban Soccer Casa"),
        "Rabat" to listOf("Stade Moulay Abdellah Annex", "Mega Foot Rabat", "Rabat City Center Field", "Agdal Sports", "Hay Riad Kickoff"),
        "Marrakech" to listOf("Grand Stade Marrakech Annex", "Marrakech Soccer City", "Palmeriae Sports", "Gueliz Foot 5", "Atlas Lions Arena"),
        "Fes" to listOf("Fes Football Academy", "Saiss Sports Center", "Medina Kick", "Route d'Imouzzer Field", "Wesslan Park"),
        "Tangier" to listOf("Ibn Batouta Training Ground", "Tangier City Sports", "Malabata Foot 5", "Corniche Arena", "Boughaz Kick"),
        "Agadir" to listOf("Adrar Stadium Annex", "Agadir Bay Sports", "Talborjt Field", "Souss Foot Park", "Anza Arena"),
        "Meknes" to listOf("Stade d'Honneur Annex", "Meknes City Foot", "Hamriya Sports", "Bassatine Kick", "Zitouna Park"),
        "Oujda" to listOf("Mouloudia Training Center", "Oujda Sports Complex", "Lazaret Field", "Isly Arena", "Oriental Foot"),
        "Kenitra" to listOf("Kenitra Futsal Club", "Sebou River Field", "Maamora Sports", "Saknia Arena", "Mehdia Beach Kick"),
        "Tetouan" to listOf("Saniat Rmel Annex", "Tetouan City Foot", "Martil Sports Park", "Wilaya Kickoff", "Rincon Arena"),
        "Safi" to listOf("Olympic Safi Annex", "Safi Corniche Field", "Plateau Sports", "Ocean View Kick", "Abda Arena"),
        "Mohammedia" to listOf("El Bachir Annex", "Monica Beach Sports", "Parc des Villes Field", "Kasbah Foot", "Fedala Arena"),
        "Beni Mellal" to listOf("Stade Municipal Annex", "Ain Asserdoun Sports", "Atlas Foot Park", "Oulad Hamdan Field", "Tasmit Arena"),
        "El Jadida" to listOf("El Abdi Annex", "Mazagan Sports Center", "Sidi Bouzid Kick", "Doukkala Arena", "Port Field"),
        "Taza" to listOf("Taza Municipal Field", "Friouato Sports", "Taza Haut Kick", "Ghiata Arena", "Medina Park"),
        "Nador" to listOf("Nador City Stadium", "Mar Chica Sports", "Corniche Nador Field", "Arouit Kick", "Rif Arena"),
        "Settat" to listOf("Complexe Sportif Settat", "Chaouia Foot", "University Field", "Settat Park", "Golf Sports Annex"),
        "Larache" to listOf("Stade Municipal Larache", "Lixus Sports", "Balcon Atlantico Field", "Larache City Kick", "Loukkos Arena"),
        "Khemisset" to listOf("18 Novembre Annex", "Zemmour Sports", "Rommani Field", "Tiflet Kickoff", "Dayet Erroumi Park"),
        "Guelmim" to listOf("Bab Sahara Sports", "Guelmim City Field", "Oued Noun Arena", "Sahara Foot 5", "Tan-Tan Road Park")
    )

    val teams5v5 = listOf(
        "Atlas Lions 5", "Raja Boys", "Wydad Stars", "City Kickers", "Urban Legends", "Desert Eagles", "Atlantic Waves"
    )

    val teams7v7 = listOf(
        "Atlas Titans 7", "Casablanca United", "Rabat Warriors", "Marrakech Rangers", "Fes Falcons", "Tangier Toros", "Agadir Anchors"
    )

    val playersPool = listOf(
        Player("Hocine", "92%", 245, true, R.drawable.player1), Player("Achraf", "88%", 120, true, R.drawable.player2), 
        Player("Yassine", "85%", 90, false, R.drawable.player3), Player("Hakim", "90%", 150, true, R.drawable.player4), 
        Player("Sofyan", "82%", 110, true, R.drawable.player5), Player("Nayef", "78%", 80, false, R.drawable.player6),
        Player("Noussair", "84%", 95, true, R.drawable.player7), Player("Selim", "75%", 60, true, R.drawable.player8), 
        Player("Azzedine", "89%", 130, true, R.drawable.player9), Player("Munir", "81%", 100, false, R.drawable.player1), 
        Player("Walid", "95%", 300, true, R.drawable.player2), Player("Brahim", "87%", 140, true, R.drawable.player3),
        Player("Amine", "72%", 50, false, R.drawable.player4), Player("Faycal", "80%", 85, true, R.drawable.player5), 
        Player("Hamza", "79%", 75, true, R.drawable.player6), Player("Oussama", "83%", 92, false, R.drawable.player7), 
        Player("Youssef", "86%", 115, true, R.drawable.player8), Player("Zakaria", "91%", 180, true, R.drawable.player9),
        Player("Ayoub", "77%", 70, false, R.drawable.player1), Player("Ilias", "84%", 105, true, R.drawable.player2), 
        Player("Rayan", "74%", 55, true, R.drawable.player3)
    )

    val matches: MutableList<Match> = generateMockMatches(200).toMutableList()

    private fun generateMockMatches(count: Int): List<Match> {
        val types = listOf("5v5", "7v7", "Panna", "Futsal")
        val statuses = listOf("Open", "Full", "Private")
        val baseHours = listOf(
            "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00",
            "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00",
            "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00", "23:00 - 00:00"
        )
        val days = listOf("Today", "Tomorrow", "History")
        
        return List(count) { i ->
            val type = types.random()
            val status = statuses.random()
            val city = cities.random()
            val field = fieldsByCity[city]?.random() ?: "Random Field"
            val time = baseHours.random()
            val hour = time.split(":")[0].toInt()
            val timeCategory = when {
                hour < 12 -> "Morning"
                hour < 18 -> "Afternoon"
                else -> "Evening"
            }
            val day = days.random()
            val maxPlayers = when(type) {
                "5v5" -> 10
                "7v7" -> 14
                "Panna" -> 2
                "Futsal" -> 10
                else -> 10
            }
            val currentPlayers = if (status == "Full" || day == "History") maxPlayers else Random.nextInt(1, maxPlayers)
            
            val sA = if (day == "History") Random.nextInt(0, 10) else null
            val sB = if (day == "History") Random.nextInt(0, 10) else null
            
            Match(
                i, type, status, city, field, time, timeCategory, day, 
                Random.nextInt(30, 100), currentPlayers, maxPlayers, sA, sB,
                teamLogos.random(), teamLogos.random()
            )
        }
    }

    fun getAvailableHours(fieldName: String): List<String> {
        val baseHours = listOf(
            "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00",
            "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00",
            "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00", "23:00 - 00:00"
        )
        val skip = fieldName.length % 3
        return baseHours.filterIndexed { index, _ -> index % 3 != skip }
    }
}
