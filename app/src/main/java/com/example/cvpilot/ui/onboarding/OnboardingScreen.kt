package com.example.cvpilot.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.cvpilot.R
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape


data class OnboardingItem(
    val resId: Int,
    val title: String,
    val desc: String
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingItem(R.raw.analyze, "Analyze Your Resume", "Upload your resume and let AI analyze ATS compatibility instantly."),
        OnboardingItem(R.raw.ai, "AI Improvements", "Get smart suggestions and missing keywords for your dream job."),
        OnboardingItem(R.raw.interview, "Land More Interviews", "Create optimized resumes that pass recruiter screening systems.")
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // Ensures content doesn't hit the notch
                .navigationBarsPadding()
        ) {

            // 1. Pager takes the majority of the top space
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f) // Increased weight to push content down slightly
            ) { index ->
                val page = pages[index]
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(page.resId))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top, // Align content towards the bottom of the pager area
                    modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp)
                ) {
                    Spacer(modifier = Modifier.height(100.dp))

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(280.dp) // Slightly larger for impact
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineMedium, // Responsive font scaling
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = page.desc,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp // Better legibility
                    )
                }
            }

            // 2. Bottom Navigation Area
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                // Page Indicator (Dots)
                Row(
                    Modifier.height(40.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                                .size(8.dp) // Uniform size for dots like iOS
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                // Primary Action Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // Standard modern button height
                        .padding(horizontal = 24.dp),
                    shape = MaterialTheme.shapes.large // Rounded/Modern look
                ) {
                    Text(
                        if (pagerState.currentPage == pages.size - 1) "Get Started" else "Continue",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}