package ch.ffhs.conscious_pancake.utils

import android.net.Uri
import java.io.File

val Uri.fileName: String
    get() = File(this.toString()).name