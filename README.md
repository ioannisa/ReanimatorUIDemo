# Reanimator Demo - Compose Multiplatform

[![Reanimator Library](https://img.shields.io/badge/Library-Reanimator-blue)](https://github.com/ioannisa/reanimator)
This repository contains a sample Compose Multiplatform application demonstrating the features of the [**Reanimator**](https://github.com/ioannisa/reanimator) library.

## Overview

The demo showcases how Reanimator simplifies state persistence in a ViewModel, particularly handling process death scenarios gracefully in an Android environment (while being built with KMP).

It features a simple "Product List" screen where you can:
* Load a list of products (simulated network delay).
* Select a product from the list.
* Remove the selected product.
* Unload all products.

The key demonstration is how Reanimator manages the `SimpleProductState` within the `SimpleProductViewModel`.

## Features Demonstrated

This demo specifically highlights Reanimator's core functionality:

1.  **Effortless State Persistence:** Uses `savedStateHandle.getMutableStateFlow` to manage the entire UI state (`SimpleProductState`) within the `SimpleProductViewModel`.
2.  **Selective Persistence (Transient Properties):**
    * The `products` list and `selectedProduct` are **persistent**. They survive process death.
    * The `isLoading` flag and `errorMessage` string are marked as **transient**. They are automatically reset to their default values (`false` and `null`) after process death and restoration.
3.  **Automatic Key Inference:** Reanimator uses the property name (`_state`) as the key in `SavedStateHandle` automatically.
4.  **KMP Compatibility:** Shows Reanimator being used within a standard ViewModel structure potentially shared in `commonMain` or used directly in `androidMain`.

## How it Works

* **`SimpleProductState`:** A `@Serializable` data class holding both persistent (`products`, `selectedProduct`) and transient (`isLoading`, `errorMessage`) UI state.
* **`SimpleProductViewModel`:**
    * Injects `SavedStateHandle`.
    * Defines `transientProperties = listOf("isLoading", "errorMessage")`.
    * Declares the `_state` using `savedStateHandle.getMutableStateFlow(...)`, passing the default state, `viewModelScope`, and the `transientProperties`.
    * Processes intents (`SimpleProductIntent`) to update the state via `_state.update { ... }`. Reanimator handles saving the persistent parts automatically.
* **`SimpleProductScreen`:**
    * A Composable function that observes the `viewModel.state`.
    * Displays the product list, loading indicator, error messages, and handles button clicks/product selection by sending intents to the ViewModel.

## Testing Process Death (Android)

To see Reanimator in action restoring persistent state while resetting transient state:

1.  Run the application on an Android device or emulator.
2.  Navigate to the "Products" screen.
3.  Click "Load Products". Wait for the list to appear.
4.  Select a product (e.g., "Laptop").
5.  Optional: Click "Remove Product" without selecting one first to see the transient error message appear.
6.  **Simulate Process Death:** While the app is in the foreground or background, run the following `adb` command in your terminal (replace `eu.anifantakis.cmpmasterdetail` if your demo app's package name is different):
    ```bash
    adb shell am kill eu.anifantakis.cmpmasterdetail
    ```
7.  Re-open the application from the recent apps list.

**Expected Result:**

* The product list (`products`) and the selected item (`selectedProduct`) should be restored exactly as they were before the process was killed.
* The loading indicator (`isLoading`) should **not** be visible (reset to `false`).
* Any error message (`errorMessage`) should be gone (reset to `null`).

This demonstrates that Reanimator successfully persisted the relevant state while correctly resetting the transient parts.

## Running the Demo

This is a standard Compose Multiplatform project.
* **Android:** Open the project in Android Studio and run the `androidApp` configuration.
* **Desktop:** Run the `./gradlew :desktopApp:run` command or use the corresponding run configuration in IntelliJ IDEA.
* **(Other Platforms):** Follow standard procedures for running KMP apps on iOS, Web, etc., if configured.

## Learn More

For detailed information about the Reanimator library itself (setup, API, etc.), please visit the main repository:

➡️ [**github.com/ioannisa/reanimator**](https://github.com/ioannisa/reanimator)
