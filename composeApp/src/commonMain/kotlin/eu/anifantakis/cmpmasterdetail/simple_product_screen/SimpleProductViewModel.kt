package eu.anifantakis.cmpmasterdetail.simple_product_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.anifantakis.lib.reanimator.getMutableStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// MVI state with both persistent and transient properties
@Serializable
data class SimpleProductState(
    // Persistent properties (survive process death)
    val products: List<String> = emptyList(),
    val selectedProduct: String? = null,

    // Transient properties (reset after process death)
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// MVI Intent sealed class
sealed interface SimpleProductIntent {
    data object LoadProducts : SimpleProductIntent
    data object UnloadProducts : SimpleProductIntent
    data object RemoveSelectedProduct : SimpleProductIntent
    data class SelectProduct(val name: String) : SimpleProductIntent
}

// MVI ViewModel with SavedStateHandleUtils
class SimpleProductViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // Define which properties should reset after process death
    private val transientProperties = listOf("isLoading", "errorMessage")

    // State with automatic key inference and selective persistence
    private val _state by savedStateHandle.getMutableStateFlow(
        defaultValue = SimpleProductState(),
        coroutineScope = viewModelScope,
        transientProperties = transientProperties
    )
    val state = _state.asStateFlow()

    // Process MVI intents
    fun processIntent(intent: SimpleProductIntent) {
        when (intent) {
            is SimpleProductIntent.LoadProducts -> loadProducts()
            is SimpleProductIntent.UnloadProducts -> unloadProducts()
            is SimpleProductIntent.RemoveSelectedProduct -> removeSelectedProduct()
            is SimpleProductIntent.SelectProduct -> selectProduct(intent.name)
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            // Start loading - this won't persist after process death
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Simulate network request
            delay(2000)
            val newProducts = listOf("Laptop", "Phone", "Headphones")

            // Update state with products (this will persist)
            _state.update { it.copy(
                products = newProducts,
                isLoading = false
            )}
        }
    }

    private fun unloadProducts() {
        // Clear products list and selected product
        _state.update { it.copy(
            products = emptyList(),
            selectedProduct = null,
            errorMessage = null
        )}
    }

    private fun removeSelectedProduct() {
        val currentState = _state.value
        val selectedProduct = currentState.selectedProduct

        if (selectedProduct == null) {
            // Show error message if no product is selected (transient - will reset after process death)
            _state.update { it.copy(
                errorMessage = "Please select a product first"
            )}
        } else {
            // Remove the selected product
            val updatedProducts = currentState.products.filter { it != selectedProduct }
            _state.update { it.copy(
                products = updatedProducts,
                selectedProduct = null,
                errorMessage = null
            )}
        }
    }

    private fun selectProduct(name: String) {
        _state.update { it.copy(
            selectedProduct = name,
            errorMessage = null
        )}
    }
}