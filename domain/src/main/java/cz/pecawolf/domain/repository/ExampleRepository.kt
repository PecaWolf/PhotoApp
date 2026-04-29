package cz.pecawolf.domain.repository

interface ExampleRepository {
    fun getExample(): Result<Unit>
}
