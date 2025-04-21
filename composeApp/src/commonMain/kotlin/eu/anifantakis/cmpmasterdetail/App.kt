package eu.anifantakis.cmpmasterdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.anifantakis.cmpmasterdetail.simple_product_screen.SimpleProductScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier.safeDrawingPadding()
        ) {
            SimpleProductScreen()
        }
    }
}




