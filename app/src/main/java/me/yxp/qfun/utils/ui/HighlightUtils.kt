package me.yxp.qfun.utils.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

object HighlightUtils {

    enum class Style {

        BOLD_COLOR,

        COLOR_UNDERLINE,

        BACKGROUND_TINT,

        GEEK_ITALIC,

        REVERSE_BLOCK,

        MINIMAL_BOLD_DASHED
    }

    fun highlightText(
        text: String,
        query: String,
        highlightColor: Color,
        baseColor: Color,
        mode: Style = Style.COLOR_UNDERLINE
    ): AnnotatedString {

        if (query.isEmpty() || !text.contains(query, ignoreCase = true)) {
            return AnnotatedString(
                text = text,
                spanStyle = SpanStyle(color = baseColor)
            )
        }

        return buildAnnotatedString {
            val lowerText = text.lowercase()
            val lowerQuery = query.lowercase()
            var start = 0

            while (true) {
                val index = lowerText.indexOf(lowerQuery, start)
                
                if (index == -1) {
                    withStyle(style = SpanStyle(color = baseColor)) {
                        append(text.substring(start))
                    }
                    break
                }

                withStyle(style = SpanStyle(color = baseColor)) {
                    append(text.substring(start, index))
                }

                val matchStyle = when (mode) {
                    Style.BOLD_COLOR -> {
                        SpanStyle(
                            color = highlightColor,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Style.COLOR_UNDERLINE -> {
                        SpanStyle(
                            color = highlightColor,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    }

                    Style.BACKGROUND_TINT -> {
                        SpanStyle(
                            color = highlightColor,
                            background = highlightColor.copy(alpha = 0.15f),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Style.GEEK_ITALIC -> {
                        SpanStyle(
                            color = highlightColor,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            textDecoration = TextDecoration.Underline
                        )
                    }

                    Style.REVERSE_BLOCK -> {
                        SpanStyle(
                            color = Color.White,
                            background = highlightColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Style.MINIMAL_BOLD_DASHED -> {
                        SpanStyle(
                            color = baseColor,
                            fontWeight = FontWeight.Black,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                withStyle(style = matchStyle) {
                    append(text.substring(index, index + query.length))
                }

                start = index + query.length
            }
        }
    }
}