package com.app.beloz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.app.beloz.apis.services.PaymentService
import com.app.beloz.data.repositories.PaymentRepository
import com.app.beloz.theme.BelozTheme
import com.app.beloz.ui.navigation.NavGraph
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.PaymentViewModel
import com.app.beloz.utils.SessionManager

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private lateinit var paymentViewModel: PaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(applicationContext)
        val paymentService = PaymentService()
        val paymentRepository = PaymentRepository(paymentService)
        paymentViewModel = PaymentViewModel(paymentRepository, sessionManager)

        setContent {
            BelozTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        paymentViewModel = paymentViewModel,
                    )
                }
            }
        }
    }
}
