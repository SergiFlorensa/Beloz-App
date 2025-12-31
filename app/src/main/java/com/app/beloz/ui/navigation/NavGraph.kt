package com.app.beloz.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.beloz.ui.screens.*
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.PaymentViewModel
import com.app.beloz.ui.viewModel.CartViewModel
import com.app.beloz.ui.viewModel.PedidosViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    paymentViewModel: PaymentViewModel
) {
    val cartViewModel: CartViewModel = viewModel()
    val pedidosViewModel: PedidosViewModel = viewModel()

    val startDestination = if (authViewModel.user != null) "home" else "splash"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            PortadaInicio(navController = navController)
        }
        composable("home") {
            PagPrincipal(navController = navController, cartViewModel = cartViewModel)
        }
        composable("cuenta") {
            Cuenta(navController = navController, authViewModel = authViewModel, paymentViewModel =paymentViewModel, pedidosViewModel = pedidosViewModel, cartViewModel = cartViewModel)
        }
        composable("usuario_registro") {
            UsuarioRegistro(navController = navController, authViewModel = authViewModel)
        }
        composable("InicioSesionUsuario") {
            InicioSesionUsuario(navController = navController, authViewModel = authViewModel)
        }
        composable("pedidos") {
            Pedidos(navController = navController, authViewModel = authViewModel, viewModel = pedidosViewModel)
        }
        composable(
            route = "detallePedido/{pedidoId}",
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: -1
            DetallesPedido(
                navController = navController,
                pedidoId = pedidoId,
                viewModel = pedidosViewModel,
                authViewModel = authViewModel
            )
        }
        composable(
            route = "lista_restaurantes?query={query}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            ListaRestaurantes(navController = navController, searchQuery = query)
        }

        composable("comida_pais_restaurante/{country}") { backStackEntry ->
            val country = backStackEntry.arguments?.getString("country") ?: ""
            ComidaPaisRestaurante(navController = navController, country = country)
        }
        composable("resultados_busquedad/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            ResultadosBusquedad(query = query, navController = navController)
        }
        composable(
            route = "tipo_comida_resultados/{selectedTypes}",
            arguments = listOf(navArgument("selectedTypes") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val selectedTypesJson = backStackEntry.arguments?.getString("selectedTypes")
            val selectedTypes: List<String> = Gson().fromJson(
                selectedTypesJson,
                object : TypeToken<List<String>>() {}.type
            )
            TipoComidaSeleccionadaRestaurantes(
                selectedTypes = selectedTypes,
                navController = navController
            )
        }
        composable(
            route = "precio_resultados/{selectedPrice}",
            arguments = listOf(navArgument("selectedPrice") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedPrice = backStackEntry.arguments?.getString("selectedPrice") ?: ""
            RestaurantesSegunPrecio(selectedPrice = selectedPrice, navController = navController)
        }
        composable(
            route = "platos_restaurante/{restauranteId}",
            arguments = listOf(navArgument("restauranteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val restauranteId = backStackEntry.arguments?.getInt("restauranteId") ?: 0
            PlatosRestaurante(
                restauranteId = restauranteId,
                navController = navController,
                cartViewModel = cartViewModel
            )
        }
        composable("modificar_email") {
            ModificacionEmail(navController = navController, authViewModel = authViewModel)
        }
        composable("modificar_password") {
            ModificacionPassword(navController = navController, authViewModel = authViewModel)
        }
        composable("modificacion_telefono") {
            ModificacionTelefono(navController, authViewModel)
        }

        composable("metodo_pago") {
            MetodoPago(navController = navController, paymentViewModel = paymentViewModel)
        }
        composable("map") {
            MapaScreen(navController=navController)
        }

        composable("carrito") {
            CarritoScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
                paymentViewModel = paymentViewModel
            )
        }
        composable("relevancia_restaurantes") {
            RelevanciaRestaurantes(navController = navController)
        }
        composable("valoracion_restaurantes") {
            ValoracionRestaurantes(navController = navController)
        }
        composable("privacidad") {
            PagPrivacidad(navController = navController)
        }
        composable("cookies") {
            CookiesPage(navController = navController)
        }
        composable("permisos") {
            PermisosPage(navController = navController)
        }
        composable("politicas") {
            PoliticasPage(navController = navController)
        }
        composable("eliminarcuenta") {
            EliminarCuentaPage(navController = navController, authViewModel = authViewModel,paymentViewModel = paymentViewModel)
        }

    }
}
