package cz.pecawolf.photoapp

import android.app.Application
import cz.pecawolf.data.dataModule
import cz.pecawolf.domain.domainModule
import cz.pecawolf.presentation.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(
                presentationModule,
                domainModule,
                dataModule,
            )
        }
    }
}