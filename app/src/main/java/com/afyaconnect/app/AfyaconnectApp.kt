package com.afyaconnect.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AfyaGreen = Color(0xFF087F5B)
private val AfyaDark = Color(0xFF12372A)
private val AfyaSoft = Color(0xFFEAF7F1)
private val AfyaBlue = Color(0xFF176B87)
private val Danger = Color(0xFFB42318)

enum class Screen { WELCOME, PATIENT_HOME, DOCTOR_LIST, DOCTOR_DETAIL, PAYMENT, CONSULTATION, HISTORY, DOCTOR_DASHBOARD, CLINICAL_NOTE, ADMIN_DASHBOARD }
enum class Availability { NOW, SOON, OFFLINE }

data class Doctor(
    val id: Int,
    val name: String,
    val specialty: String,
    val qualification: String,
    val languages: String,
    val fee: Int,
    val availability: Availability,
    val waitMinutes: Int,
    val rating: Double,
    val verified: Boolean,
    val location: String
)

data class Message(val fromDoctor: Boolean, val text: String)

private val specialties = listOf(
    "General Practice", "Internal Medicine", "Dermatology", "Paediatrics",
    "Obstetrics & Gynaecology", "Mental Health", "Cardiology", "Orthopaedics"
)

private val demoDoctors = listOf(
    Doctor(1, "Dr. Amani Joseph", "Internal Medicine", "MD, MMed", "Swahili / English", 15000, Availability.NOW, 3, 4.9, true, "Dodoma"),
    Doctor(2, "Dr. Neema Mushi", "Dermatology", "MD, MMed Dermatology", "Swahili / English", 20000, Availability.NOW, 5, 4.8, true, "Dar es Salaam"),
    Doctor(3, "Dr. Baraka Mrema", "General Practice", "MD", "Swahili / English", 10000, Availability.NOW, 2, 4.7, true, "Tabora"),
    Doctor(4, "Dr. Rehema Hassan", "Paediatrics", "MD, MMed Paediatrics", "Swahili / English", 18000, Availability.SOON, 30, 4.9, true, "Mwanza"),
    Doctor(5, "Dr. Said Ally", "Cardiology", "MD, MMed Internal Medicine", "Swahili / English", 30000, Availability.OFFLINE, 0, 4.8, true, "Dar es Salaam")
)

@Composable
fun AfyaconnectApp() {
    var screen by remember { mutableStateOf(Screen.WELCOME) }
    var selectedSpecialty by remember { mutableStateOf("General Practice") }
    var selectedDoctor by remember { mutableStateOf(demoDoctors[2]) }
    var symptomText by remember { mutableStateOf("") }
    var emergency by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AfyaGreen,
            secondary = AfyaBlue,
            surface = Color.White,
            background = Color(0xFFF7FBFA),
            error = Danger
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (screen) {
                Screen.WELCOME -> WelcomeScreen(
                    onPatient = { screen = Screen.PATIENT_HOME },
                    onDoctor = { screen = Screen.DOCTOR_DASHBOARD },
                    onAdmin = { screen = Screen.ADMIN_DASHBOARD }
                )
                Screen.PATIENT_HOME -> PatientHome(
                    symptomText = symptomText,
                    onSymptomChange = {
                        symptomText = it
                        emergency = isEmergencyText(it)
                    },
                    emergency = emergency,
                    onSpecialty = {
                        selectedSpecialty = it
                        screen = Screen.DOCTOR_LIST
                    },
                    onHistory = { screen = Screen.HISTORY },
                    onBack = { screen = Screen.WELCOME }
                )
                Screen.DOCTOR_LIST -> DoctorListScreen(
                    specialty = selectedSpecialty,
                    doctors = rankDoctors(selectedSpecialty),
                    onDoctor = {
                        selectedDoctor = it
                        screen = Screen.DOCTOR_DETAIL
                    },
                    onBack = { screen = Screen.PATIENT_HOME }
                )
                Screen.DOCTOR_DETAIL -> DoctorDetailScreen(selectedDoctor, { screen = Screen.PAYMENT }, { screen = Screen.DOCTOR_LIST })
                Screen.PAYMENT -> PaymentScreen(selectedDoctor, { screen = Screen.CONSULTATION }, { screen = Screen.DOCTOR_DETAIL })
                Screen.CONSULTATION -> ConsultationScreen(selectedDoctor) { screen = Screen.PATIENT_HOME }
                Screen.HISTORY -> HistoryScreen { screen = Screen.PATIENT_HOME }
                Screen.DOCTOR_DASHBOARD -> DoctorDashboard({ screen = Screen.CLINICAL_NOTE }, { screen = Screen.WELCOME })
                Screen.CLINICAL_NOTE -> ClinicalNoteScreen { screen = Screen.DOCTOR_DASHBOARD }
                Screen.ADMIN_DASHBOARD -> AdminDashboard { screen = Screen.WELCOME }
            }
        }
    }
}

@Composable
private fun PageHeader(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) TextButton(onClick = onBack) { Text("← Back") }
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AfyaDark)
            subtitle?.let { Text(it, fontSize = 13.sp, color = Color.Gray) }
        }
        Surface(shape = RoundedCornerShape(12.dp), color = AfyaSoft) {
            Text("Afya+", color = AfyaGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
private fun WelcomeScreen(onPatient: () -> Unit, onDoctor: () -> Unit, onAdmin: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(32.dp))
        Surface(shape = RoundedCornerShape(28.dp), color = AfyaGreen) {
            Text("A+", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(22.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Afyaconnect", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = AfyaDark)
        Text("Verified healthcare, when you need it", color = Color.Gray)
        Spacer(Modifier.height(24.dp))
        EntryCard("For patients", "Find verified doctors, pay and consult securely.", "Continue as Patient", onPatient)
        Spacer(Modifier.height(12.dp))
        EntryCard("For doctors", "Manage availability, consultations, notes and earnings.", "Doctor Dashboard", onDoctor)
        Spacer(Modifier.height(12.dp))
        EntryCard("Administration", "Verify practitioners and oversee platform activity.", "Admin Dashboard", onAdmin)
        Spacer(Modifier.height(18.dp))
        Text("Prototype • Demo data only • Not for emergency care", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun EntryCard(title: String, body: String, button: String, action: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(18.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Spacer(Modifier.height(6.dp))
            Text(body, color = Color.DarkGray)
            Spacer(Modifier.height(12.dp))
            Button(onClick = action, modifier = Modifier.fillMaxWidth()) { Text(button) }
        }
    }
}

@Composable
private fun PatientHome(
    symptomText: String,
    onSymptomChange: (String) -> Unit,
    emergency: Boolean,
    onSpecialty: (String) -> Unit,
    onHistory: () -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        item { PageHeader("Find a doctor", "Tell us what you need help with", onBack) }
        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                if (emergency) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9E7)), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("⚠ Possible emergency", fontWeight = FontWeight.Bold, color = Danger)
                            Text("Your description may need urgent in-person care. Seek the nearest emergency facility now and do not rely on this app alone.")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                OutlinedTextField(
                    value = symptomText,
                    onValueChange = onSymptomChange,
                    label = { Text("Describe your problem") },
                    placeholder = { Text("Example: stomach pain for three days") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { onSpecialty(suggestSpecialty(symptomText)) },
                    enabled = symptomText.isNotBlank() && !emergency,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Suggest doctor type") }
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Browse specialties", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    TextButton(onClick = onHistory) { Text("History") }
                }
            }
        }
        items(specialties) { specialty ->
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).clickable { onSpecialty(specialty) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(specialtyIcon(specialty), fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(specialty, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun DoctorListScreen(specialty: String, doctors: List<Doctor>, onDoctor: (Doctor) -> Unit, onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        item { PageHeader(specialty, "Available verified clinicians", onBack) }
        if (doctors.isEmpty()) item {
            Text("No matching doctor is available in this demo.", modifier = Modifier.padding(20.dp))
        }
        items(doctors) { doctor ->
            Card(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 7.dp).clickable { onDoctor(doctor) }) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (doctor.verified) Text("✓ Verified", color = AfyaGreen, fontWeight = FontWeight.Bold)
                    }
                    Text(doctor.specialty)
                    Text(doctor.qualification, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text(availabilityText(doctor), color = availabilityColor(doctor.availability))
                    Text("${doctor.languages} • ${doctor.location}")
                    Text("TZS ${formatMoney(doctor.fee)} • ★ ${doctor.rating}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DoctorDetailScreen(doctor: Doctor, onConsult: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        PageHeader("Doctor profile", doctor.specialty, onBack)
        Column(Modifier.padding(20.dp)) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text(doctor.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(if (doctor.verified) "✓ Verified practitioner" else "Verification pending", color = AfyaGreen)
                    Spacer(Modifier.height(12.dp))
                    DetailRow("Qualification", doctor.qualification)
                    DetailRow("Languages", doctor.languages)
                    DetailRow("Location", doctor.location)
                    DetailRow("Availability", availabilityText(doctor))
                    DetailRow("Rating", "★ ${doctor.rating}")
                    DetailRow("Consultation fee", "TZS ${formatMoney(doctor.fee)}")
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onConsult, enabled = doctor.availability != Availability.OFFLINE, modifier = Modifier.fillMaxWidth()) {
                Text(if (doctor.availability == Availability.OFFLINE) "Doctor offline" else "Consult now")
            }
        }
    }
}

@Composable
private fun PaymentScreen(doctor: Doctor, onPaid: () -> Unit, onBack: () -> Unit) {
    var method by remember { mutableStateOf("M-Pesa") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        PageHeader("Payment", "Prepay consultation", onBack)
        Column(Modifier.padding(20.dp)) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text(doctor.name, fontWeight = FontWeight.Bold)
                    Text("Consultation fee: TZS ${formatMoney(doctor.fee)}")
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("Payment method", fontWeight = FontWeight.Bold)
            listOf("M-Pesa", "Airtel Money", "Mixx by Yas", "Card").forEach { option ->
                Row(Modifier.fillMaxWidth().clickable { method = option }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = method == option, onClick = { method = option })
                    Text(option)
                }
            }
            Button(onClick = onPaid, modifier = Modifier.fillMaxWidth()) { Text("Demo pay TZS ${formatMoney(doctor.fee)}") }
            Text("Production payments must be verified server-side through a licensed payment provider.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun ConsultationScreen(doctor: Doctor, onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf(Message(true, "Hello. I am ${doctor.name}. Please tell me more about your symptoms."))) }
    Column(Modifier.fillMaxSize()) {
        PageHeader("Consultation", doctor.name, onBack)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text("💬 Chat") })
            AssistChip(onClick = {}, label = { Text("📞 Voice") })
            AssistChip(onClick = {}, label = { Text("📹 Video") })
            AssistChip(onClick = {}, label = { Text("📎 File") })
        }
        LazyColumn(Modifier.weight(1f).padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages) { msg ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (msg.fromDoctor) Arrangement.Start else Arrangement.End) {
                    Surface(color = if (msg.fromDoctor) AfyaSoft else MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(14.dp)) {
                        Text(msg.text, modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(input, { input = it }, placeholder = { Text("Message doctor") }, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (input.isNotBlank()) {
                    messages = messages + Message(false, input.trim())
                    input = ""
                }
            }) { Text("Send") }
        }
    }
}

@Composable
private fun HistoryScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        PageHeader("Consultation history", "Your previous care", onBack)
        Card(Modifier.fillMaxWidth().padding(20.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text("Dr. Amani Joseph", fontWeight = FontWeight.Bold)
                Text("Internal Medicine • Completed")
                Text("Demo record: consultation summary and follow-up would appear here.", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun DoctorDashboard(onOpenConsultation: () -> Unit, onBack: () -> Unit) {
    var availability by remember { mutableStateOf(Availability.NOW) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        PageHeader("Doctor dashboard", "Dr. Amani Joseph", onBack)
        Column(Modifier.padding(20.dp)) {
            Text("Availability", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Availability.values().forEach { value ->
                    FilterChip(selected = availability == value, onClick = { availability = value }, label = { Text(availabilityLabel(value)) })
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("Today", "6", Modifier.weight(1f))
                MetricCard("Earnings", "TZS 92k", Modifier.weight(1f))
                MetricCard("Rating", "4.9", Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            Text("Incoming consultation", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Card(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Patient: Asha M.", fontWeight = FontWeight.Bold)
                    Text("Reason: stomach pain for three days")
                    Text("Payment: confirmed", color = AfyaGreen)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = onOpenConsultation, modifier = Modifier.fillMaxWidth()) { Text("Open clinical workspace") }
                }
            }
        }
    }
}

@Composable
private fun ClinicalNoteScreen(onBack: () -> Unit) {
    var assessment by remember { mutableStateOf("") }
    var advice by remember { mutableStateOf("") }
    var medication by remember { mutableStateOf("") }
    var referral by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        PageHeader("Clinical workspace", "Consultation documentation", onBack)
        Column(Modifier.padding(20.dp)) {
            ClinicalField("Provisional assessment", assessment) { assessment = it }
            ClinicalField("Advice / plan", advice) { advice = it }
            ClinicalField("Medication / prescription", medication) { medication = it }
            ClinicalField("Referral / laboratory recommendation", referral) { referral = it }
            Button(onClick = { saved = true }, modifier = Modifier.fillMaxWidth()) { Text("Save consultation summary") }
            if (saved) Text("✓ Saved locally in this prototype", color = AfyaGreen, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun ClinicalField(label: String, value: String, onValue: (String) -> Unit) {
    OutlinedTextField(value, onValue, label = { Text(label) }, minLines = 2, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun AdminDashboard(onBack: () -> Unit) {
    var verified by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        PageHeader("Admin dashboard", "Verification, payments & platform safety", onBack)
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("Patients", "1,284", Modifier.weight(1f))
                MetricCard("Doctors", "86", Modifier.weight(1f))
                MetricCard("Today", "43", Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            Text("Doctor verification queue", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Card(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Dr. Grace Peter", fontWeight = FontWeight.Bold)
                    Text("Applicant: Dermatology")
                    DetailRow("Identity", "✓ Submitted")
                    DetailRow("Registration no.", "✓ Submitted")
                    DetailRow("Practicing licence", "✓ Submitted")
                    DetailRow("Qualification", "✓ Submitted")
                    DetailRow("Specialty", "✓ Submitted")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { verified = true }, enabled = !verified, modifier = Modifier.fillMaxWidth()) {
                        Text(if (verified) "Verified" else "Approve after external verification")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

private fun rankDoctors(specialty: String): List<Doctor> {
    val exact = demoDoctors.filter { it.specialty == specialty }
    val fallback = if (exact.isEmpty() && specialty !in listOf("Cardiology", "Dermatology", "Paediatrics")) demoDoctors.filter { it.specialty == "General Practice" } else emptyList()
    return (exact + fallback).sortedWith(
        compareBy<Doctor> { when (it.availability) { Availability.NOW -> 0; Availability.SOON -> 1; Availability.OFFLINE -> 2 } }
            .thenByDescending { it.verified }
            .thenBy { it.fee }
            .thenByDescending { it.rating }
    )
}

private fun isEmergencyText(text: String): Boolean {
    val input = text.lowercase()
    val redFlags = listOf("severe chest pain", "difficulty breathing", "can't breathe", "cannot breathe", "unconscious", "seizure", "heavy bleeding", "stroke", "suicidal", "overdose", "pregnant bleeding", "severe allergic")
    return redFlags.any { input.contains(it) }
}

private fun suggestSpecialty(text: String): String {
    val input = text.lowercase()
    return when {
        listOf("skin", "rash", "acne", "itch").any { input.contains(it) } -> "Dermatology"
        listOf("child", "baby", "infant").any { input.contains(it) } -> "Paediatrics"
        listOf("pregnant", "pregnancy", "period", "vaginal").any { input.contains(it) } -> "Obstetrics & Gynaecology"
        listOf("heart", "palpitation").any { input.contains(it) } -> "Cardiology"
        listOf("bone", "joint", "fracture", "back pain").any { input.contains(it) } -> "Orthopaedics"
        listOf("anxiety", "depression", "stress", "sleep").any { input.contains(it) } -> "Mental Health"
        listOf("stomach", "abdominal", "diabetes", "pressure", "hypertension").any { input.contains(it) } -> "Internal Medicine"
        else -> "General Practice"
    }
}

private fun availabilityText(doctor: Doctor): String = when (doctor.availability) {
    Availability.NOW -> "● Available now • ~${doctor.waitMinutes} min"
    Availability.SOON -> "● Available in ~${doctor.waitMinutes} min"
    Availability.OFFLINE -> "● Offline"
}

private fun availabilityLabel(value: Availability): String = when (value) {
    Availability.NOW -> "Available now"
    Availability.SOON -> "Soon"
    Availability.OFFLINE -> "Offline"
}

private fun availabilityColor(value: Availability): Color = when (value) {
    Availability.NOW -> AfyaGreen
    Availability.SOON -> Color(0xFFB26A00)
    Availability.OFFLINE -> Color.Gray
}

private fun specialtyIcon(specialty: String): String = when (specialty) {
    "Dermatology" -> "🩺"
    "Paediatrics" -> "👶"
    "Cardiology" -> "♥"
    "Orthopaedics" -> "🦴"
    "Mental Health" -> "🧠"
    "Obstetrics & Gynaecology" -> "♀"
    else -> "+"
}

private fun formatMoney(value: Int): String = "%,d".format(value)
