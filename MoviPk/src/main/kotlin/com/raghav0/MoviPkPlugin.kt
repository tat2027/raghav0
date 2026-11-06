
package com.raghav0

import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

class MoviPkPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(MoviPkProvider())
    }
}
