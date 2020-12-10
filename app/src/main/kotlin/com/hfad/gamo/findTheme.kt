package com.hfad.gamo

/*
fun getDarkColor1() = R.color.myDark1

fun getDarkColor2() = R.color.myDark2

fun getDarkLightColor() = R.color.myDarkLight

fun getWidgetLayoutColor() = if (Component.darkTheme) {
    R.drawable.myround_dark
} else {
    when (Component.theme) {
        "red" -> {
            R.drawable.myround_red
        }
        "green" -> {
            R.drawable.myround_green
        }
        else -> {
            R.drawable.myround_default
        }
    }
}


fun getThemeColor(mTheme: String?) = when (mTheme) {
    "red" -> {
        R.color.red
    }
    "green" -> {
        R.color.green
    }
    else -> {
        R.color.main2Blue
    }
}*/

fun getThemeColor() = when (Component.theme) {
    "red" -> {
        R.color.red
    }
    "green" -> {
        R.color.green
    }
    else -> {
        R.color.main2Blue
    }
}

/*fun getThemeButtonResource(mTheme: String?) = when (mTheme) {
    "red" -> {
        R.drawable.dialog_button_red
    }
    "green" -> {
        R.drawable.dialog_button_green
    }
    else -> {
        R.drawable.dialog_button_default
    }
}*/

fun getThemeButtonResource() = if (Component.darkTheme) {
    R.drawable.dialog_button_dark
} else {
    when (Component.theme) {
        "red" -> {
            R.drawable.dialog_button_red
        }
        "green" -> {
            R.drawable.dialog_button_green
        }
        else -> {
            R.drawable.dialog_button_default
        }
    }
}

/*
fun getThemeDeepColor() = when (Component.theme) {
    "red" -> {
        R.color.deepRed
    }
    "green" -> {
        R.color.deepGreen
    }
    else -> {
        R.color.main2DeepBlue
    }
}

fun getThemeLightColor() = when (Component.theme) {
    "red" -> R.color.lightRed
    "green" -> R.color.lightGreen
    else -> R.color.main2LightBlue
}*/
