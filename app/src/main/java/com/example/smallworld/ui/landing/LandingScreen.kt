package com.example.smallworld.ui.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smallworld.R
import com.example.smallworld.ui.theme.SmallWorldTheme

@Composable
fun LandingScreen(
    onSignInButtonClick: () -> Unit,
    onSignUpButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.sign_up_background),
                true,
                contentScale = ContentScale.Crop
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.earth),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .shadow(20.dp, RoundedCornerShape(75.dp), true)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Not such a small world after all...",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Button(
                onSignInButtonClick,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.landing_page_sign_in))
            }
            Button(
                onSignUpButtonClick,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.landing_page_sign_up))
            }
            Spacer(modifier = Modifier.weight(2f))
        }
    }

}

@Preview("Sign Up Screen", widthDp = 400, heightDp = 700, showBackground = true)
@Composable
fun LandingScreenPreview() {
    SmallWorldTheme {
        LandingScreen(onSignInButtonClick = {}, onSignUpButtonClick = {})
    }
}