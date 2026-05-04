package cz.pecawolf.domain.usecase

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class EncodeUrlUseCase{
    operator fun invoke(url: String): String = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
}