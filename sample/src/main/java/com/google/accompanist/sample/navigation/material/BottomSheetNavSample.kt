/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.accompanist.sample.navigation.material

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.sample.AccompanistSampleTheme
import java.util.UUID

class BottomSheetNavSample : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AccompanistSampleTheme {
                BottomSheetNavDemo()
            }
        }
    }
}

private object Destinations {
    const val Home = "HOME"
    const val Feed = "FEED"
    const val Sheet = "SHEET"
    const val Dialog = "Dialog"
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialNavigationApi::class
)
@Composable
fun rememberFullyExpandedBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec
): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec,
        true
    )
    return remember(sheetState) {
        BottomSheetNavigator(sheetState = sheetState)
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun BottomSheetNavDemo() {
    val bottomSheetNavigator = rememberFullyExpandedBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)

    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHost(navController, Destinations.Home) {
            composable(Destinations.Home) {
                HomeScreen(
                    showSheet = {
                        navController.navigate(Destinations.Sheet + "?arg=From Home Screen")
                    },
                    showFeed = { navController.navigate(Destinations.Feed) }
                )
            }
            composable(Destinations.Feed) { Text("Feed!") }
            bottomSheet(Destinations.Sheet + "?arg={arg}") { backstackEntry ->
                val arg = backstackEntry.arguments?.getString("arg") ?: "Missing argument :("
                BottomSheet(
                    showFeed = { navController.navigate("graph") },
                    showAnotherSheet = {
                        navController.navigate(Destinations.Sheet + "?arg=${UUID.randomUUID()}")
                    },
                    arg = arg
                )
            }

            navigation(Destinations.Dialog, "graph") {
                dialog(Destinations.Dialog) {
                    Surface {
                        Button(onClick = { navController.navigate("feed1") }) {
                            Text(text = "Open Feed")
                        }
                    }
                }
                composable("feed1") {
                    Text(text = "Another Feed")
                }
            }

        }
    }
}

@Composable
private fun HomeScreen(showSheet: () -> Unit, showFeed: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Body")
        Button(onClick = showSheet) {
            Text("Show sheet!")
        }
        Button(onClick = showFeed) {
            Text("Navigate to Feed")
        }
    }
}

@Composable
private fun BottomSheet(showFeed: () -> Unit, showAnotherSheet: () -> Unit, arg: String) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sheet with arg: $arg")
        Button(onClick = showFeed) {
            Text("Open Dialog")
        }
        Button(onClick = showAnotherSheet) {
            Text("Click me to show another sheet!")
        }
    }
}
