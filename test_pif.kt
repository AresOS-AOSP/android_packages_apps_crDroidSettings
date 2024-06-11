import java.net.URL
import java.nio.charset.StandardCharsets
import org.json.JSONObject
import java.util.regex.Pattern

fun main() {
    val GOOGLE_URL = "https://developer.android.com"
    val FLASH_URL = "https://flash.android.com"
    val FLASH_API = "https://content-flashstation-pa.googleapis.com/v1/builds"

    try {
        val versionsHtml = URL("$GOOGLE_URL/about/versions").readText(StandardCharsets.UTF_8)
        val knownVersions = Regex("""https://developer\.android\.com/about/versions/(\d+)""")
            .findAll(versionsHtml).map { it.groupValues[1].toInt() }.toSet().sortedDescending()
        
        val maxVersion = knownVersions.firstOrNull() ?: return
        val versions = listOf(maxVersion + 1) + knownVersions

        var apiKey: String? = null
        val devices = mutableListOf<String>()

        for (version in versions) {
            try {
                val latestHtml = URL("$GOOGLE_URL/about/versions/$version").readText(StandardCharsets.UTF_8)
                val qprPath = Regex("""href="(/about/versions/$version/qpr(\d+)/download-ota)"""")
                    .findAll(latestHtml)
                    .map { it.groupValues[2].toInt() to it.groupValues[1] }
                    .maxByOrNull { it.first }?.second ?: continue

                val fiHtml = URL("$GOOGLE_URL$qprPath").readText(StandardCharsets.UTF_8)
                val rowPattern = Regex("""<tr id="([^"]+)">\s*<td[^>]*>([^<]+)</td>""", RegexOption.DOT_MATCHES_ALL)
                
                rowPattern.findAll(fiHtml).forEach { devices.add(it.groupValues[1]) }
                
                if (devices.isNotEmpty()) {
                    val flashHtml = URL(FLASH_URL).readText(StandardCharsets.UTF_8)
                    apiKey = Regex("""AIza[0-9A-Za-z_-]{35}""").find(flashHtml)?.value
                    break
                }
            } catch (e: Exception) {}
        }
        
        println("API Key: $apiKey")
        println("Devices: $devices")
        
        for (device in devices.distinct()) {
            val product = "${device}_beta"
            val buildsUrl = "$FLASH_API?product=$product&key=$apiKey"
            val conn = URL(buildsUrl).openConnection()
            conn.setRequestProperty("Referer", FLASH_URL)
            conn.setRequestProperty("X-Goog-Api-Key", apiKey)
            try {
                val buildsJson = String(conn.getInputStream().readBytes(), StandardCharsets.UTF_8)
                val root = JSONObject(buildsJson)
                val buildsArray = root.optJSONArray("flashstationBuild") ?: continue
                var foundCanary = false
                for (i in buildsArray.length() - 1 downTo 0) {
                    val b = buildsArray.optJSONObject(i) ?: continue
                    val meta = b.optJSONObject("previewMetadata") ?: continue
                    if (meta.optBoolean("canary")) { foundCanary = true; break }
                }
                println("Device $device: Canary found = $foundCanary")
            } catch(e: Exception) {
                println("Device $device: Exception ${e.message}")
            }
        }
    } catch(e: Exception) { e.printStackTrace() }
}
