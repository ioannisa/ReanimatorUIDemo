package eu.anifantakis.cmpmasterdetail.simple_product_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SimpleProductScreen(viewModel: SimpleProductViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Header with action buttons
        Text("Products", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.processIntent(SimpleProductIntent.LoadProducts) }) {
                Text("Load Products")
            }

            Button(onClick = { viewModel.processIntent(SimpleProductIntent.UnloadProducts) }) {
                Text("Unload Products")
            }
        }

        Button(onClick = { viewModel.processIntent(SimpleProductIntent.RemoveSelectedProduct) }) {
            Text("Remove Product")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading indicator (transient - resets after process death)
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Error message (transient - resets after process death)
        state.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Product list (persistent - survives process death)
        if (state.products.isEmpty()) {
            Text(
                text = "No products available",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(state.products.size) { index ->
                    val product = state.products[index]
                    val isSelected = product == state.selectedProduct

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.processIntent(SimpleProductIntent.SelectProduct(product))
                            }
                            .padding(vertical = 8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = product,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}