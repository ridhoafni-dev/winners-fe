package com.dailyapps.home.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dailyapps.common.Neutral400
import com.dailyapps.common.fontMedium
import com.dailyapps.common.fontRegular
import com.dailyapps.common.utils.uppercaseFirstChar
import com.dailyapps.entity.Student
import com.dailyapps.entity.User
import com.dailyapps.home.R

@Composable
fun HomeProfile(
    modifier: Modifier = Modifier,
    user: User? = null,
    onClickProfile:() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val matrix = ColorMatrix()
        matrix.setToSaturation(0F)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://bilpunkten.se/wp-content/uploads/2021/03/dummy-user-image-e1616512544203-1.png")
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.image_profile),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .border(
                    BorderStroke(2.dp, Neutral400),
                    CircleShape
                )
                .clip(CircleShape)
                .clickable { onClickProfile() },
            colorFilter = ColorFilter.colorMatrix(matrix)
        )

        Spacer(modifier = Modifier.width(11.dp))
        Column {
            Text(
                text = stringResource(R.string.welcome),
                fontFamily = fontRegular,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 8.dp),
                text = user?.name?.uppercaseFirstChar() ?: "",
                fontFamily = fontMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Dark")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Light")
@Composable
fun HomeProfilePreview() {
    HomeProfile() {}
}