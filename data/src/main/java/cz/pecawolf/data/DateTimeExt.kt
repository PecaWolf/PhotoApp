package cz.pecawolf.data

import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Instant

fun String.toInstant(): Instant? = runCatching {
    Instant.parse(input = this)
}.recoverCatching {
    LocalDateTime.parse(this.replace(' ', 'T'))
        .toInstant(TimeZone.currentSystemDefault())
}.fold(
    onSuccess = { it },
    onFailure = {
        Napier.w(it) { "toInstant(): Failed to parse date string $this with error: " }
        null
    },
)
