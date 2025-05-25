package com.dailyapps.feature.observation.page.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.dailyapps.common.Neutral100
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseTextField
import com.dailyapps.feature.observation.ObservationViewModel
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.feature.observation.state.ObservationState
import com.dailyapps.observation.R

@Composable
fun AddObservationScreen(
    navController: NavHostController, viewModel: ObservationViewModel, modifier: Modifier = Modifier
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

//    LaunchedEffect(currentState.token) {
//        if (currentState.token.isEmpty()) return@LaunchedEffect
//
//        viewModel.handleAction(
//            ObservationAction.OnGetObservation(observationId, currentState.token)
//        )
//    }

    val onValueChanged =
        { name: String, description: String, date: String, lecturerId: Long, imageUri: String? ->
            viewModel.handleAction(
                ObservationAction.OnAddObservation(
                    name = name,
                    description = description,
                    date = date,
                    lecturerId = lecturerId,
                    imageUri = imageUri
                )
            )
        }

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.title_observation_add),
                onClickBack = { navController.popBackStack() },
                menuIconResource = com.dailyapps.common.R.drawable.ic_history,
                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {},
            )
        }) { paddingValues ->
        AddContent(
            modifier = modifier.padding(paddingValues),
            onValueChanged = onValueChanged,
            state = currentState
        )
    }
}

@Composable
fun AddContent(
    state: ObservationState,
    modifier: Modifier,
    onValueChanged: (name: String, description: String, date: String, lecturer: Long, image: String) -> Unit = { _, _, _, _, _ ->
    },
) {
    val currentState = state.add

    // Form state
    val name = currentState.name
    val description = currentState.description
    val date= currentState.date
    val selectedLecture = currentState.lecturer
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Lecture options (replace with actual data)
    val lectureOptions = listOf("Lecture 1", "Lecture 2", "Lecture 3")

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        // Name field
        BaseTextField(
            value = name,
            title = stringResource(R.string.name),
            keyboardType = KeyboardType.Text,
            onValueChange = { currentName ->
                onValueChanged(
                    currentName, description, date, selectedLecture, imageUri.toString()
                )
            },
            placeholder = stringResource(R.string.name),
            //enable = !isLoading
        )

        // Description field
        BaseTextField(
            modifier = Modifier.padding(top = 16.dp),
            value = description,
            title = stringResource(R.string.description),
            keyboardType = KeyboardType.Text,
            onValueChange = { currentDescription ->
                onValueChanged(
                    name, currentDescription, date, selectedLecture, imageUri.toString()
                )
            },
            placeholder = stringResource(R.string.description),
            //enable = !isLoading
        )

        // Date picker
        BaseTextField(
            modifier = Modifier.padding(top = 16.dp),
            value = date,
            title = stringResource(R.string.date),
            keyboardType = KeyboardType.Text,
            onValueChange = { currentDate ->
                onValueChanged(
                    name, description, currentDate, selectedLecture, imageUri.toString()
                )
            },
            placeholder = stringResource(R.string.date),
            isDate = true,
            onIconClick = {
                showDatePicker = true
            }
            //enable = !isLoading
        )

        // Lectures dropdown

        // Image picker
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = if (imageUri != null) "Change Image" else "Select Image")
        }

        // Show selected image preview
        imageUri?.let { uri ->
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Neutral100, RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
//                            onSubmit(
//                                name.value,
//                                description.value,
//                                date.value,
//                                selectedLecture.value,
//                                imageUri.value
//                            )
                }, modifier = Modifier.weight(1f)
            ) {
                Text("Submit")
            }
        }

        // Add some bottom padding to avoid the sheet's bottom edge
        Spacer(modifier = Modifier.height(16.dp))
    }
}
